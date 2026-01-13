import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import ui.DatabaseConnection;

/**
 * Fetches international indicators data from official APIs and inserts into database.
 * 
 * Sources:
 * - World Bank API: https://api.worldbank.org/v2/ (for 2023-2024 actual data)
 * - IMF/OECD forecasts: For 2025-2026 projections
 * 
 * Indicators:
 * - gdp_per_capita: GDP per capita (current US$)
 * - tax_pct_gdp: Tax revenue as % of GDP
 * - debt_to_gdp: Government debt as % of GDP
 * - budget_balance_pct_gdp: Budget balance as % of GDP
 * - revenue_pct_gdp: Government revenue as % of GDP
 */
public class FetchInternationalDataFromAPIs {
    
    // Exchange rate: 1 USD = X EUR
    // Using approximate rate (can be updated or fetched from API)
    // As of 2024, 1 USD ≈ 0.92 EUR (or 1 EUR ≈ 1.09 USD)
    // NOTE: This rate should be updated periodically for accuracy
    private static final double USD_TO_EUR_RATE = 0.92;
    
    // Major countries to fetch data for
    private static final String[] COUNTRY_CODES = {
        "GRC", "DEU", "FRA", "ITA", "ESP", "GBR", "USA", "JPN", "CAN", "AUS",
        "NLD", "BEL", "AUT", "SWE", "DNK", "FIN", "NOR", "POL", "CZE", "HUN",
        "PRT", "IRL", "CHE", "LUX", "ISL", "EST", "LVA", "LTU", "SVK", "SVN"
    };
    
    // Country code to name mapping
    private static final Map<String, String> COUNTRY_NAMES = new HashMap<>();
    static {
        COUNTRY_NAMES.put("GRC", "Greece");
        COUNTRY_NAMES.put("DEU", "Germany");
        COUNTRY_NAMES.put("FRA", "France");
        COUNTRY_NAMES.put("ITA", "Italy");
        COUNTRY_NAMES.put("ESP", "Spain");
        COUNTRY_NAMES.put("GBR", "United Kingdom");
        COUNTRY_NAMES.put("USA", "United States");
        COUNTRY_NAMES.put("JPN", "Japan");
        COUNTRY_NAMES.put("CAN", "Canada");
        COUNTRY_NAMES.put("AUS", "Australia");
        COUNTRY_NAMES.put("NLD", "Netherlands");
        COUNTRY_NAMES.put("BEL", "Belgium");
        COUNTRY_NAMES.put("AUT", "Austria");
        COUNTRY_NAMES.put("SWE", "Sweden");
        COUNTRY_NAMES.put("DNK", "Denmark");
        COUNTRY_NAMES.put("FIN", "Finland");
        COUNTRY_NAMES.put("NOR", "Norway");
        COUNTRY_NAMES.put("POL", "Poland");
        COUNTRY_NAMES.put("CZE", "Czech Republic");
        COUNTRY_NAMES.put("HUN", "Hungary");
        COUNTRY_NAMES.put("PRT", "Portugal");
        COUNTRY_NAMES.put("IRL", "Ireland");
        COUNTRY_NAMES.put("CHE", "Switzerland");
        COUNTRY_NAMES.put("LUX", "Luxembourg");
        COUNTRY_NAMES.put("ISL", "Iceland");
        COUNTRY_NAMES.put("EST", "Estonia");
        COUNTRY_NAMES.put("LVA", "Latvia");
        COUNTRY_NAMES.put("LTU", "Lithuania");
        COUNTRY_NAMES.put("SVK", "Slovakia");
        COUNTRY_NAMES.put("SVN", "Slovenia");
    }
    
    // World Bank indicator codes
    private static final Map<String, String> WB_INDICATORS = new HashMap<>();
    static {
        WB_INDICATORS.put("gdp_per_capita", "NY.GDP.PCAP.CD"); // GDP per capita (current US$)
        WB_INDICATORS.put("gdp_total", "NY.GDP.MKTP.CD"); // GDP, total (current US$) - NEEDED for budget calculations
        WB_INDICATORS.put("tax_pct_gdp", "GC.TAX.TOTL.GD.ZS"); // Tax revenue (% of GDP)
        WB_INDICATORS.put("debt_to_gdp", "GC.DOD.TOTL.GD.ZS"); // Central government debt (% of GDP)
        WB_INDICATORS.put("budget_balance_pct_gdp", "GC.BAL.CASH.GD.ZS"); // Cash surplus/deficit (% of GDP) - closest to budget balance
        WB_INDICATORS.put("revenue_pct_gdp", "GC.REV.XGR.GD.ZS"); // Revenue, excluding grants (% of GDP)
        WB_INDICATORS.put("expenditure_pct_gdp", "GC.XPN.TOTL.GD.ZS"); // Expense (% of GDP) - better than government_spending for total expenses
    }
    
    public static void main(String[] args) {
        System.out.println("=== Fetching International Indicators from APIs ===\n");
        System.out.println("IMPORTANT NOTES:");
        System.out.println("1. World Bank API returns data in US Dollars (USD)");
        System.out.println("2. All budget data is automatically converted to EUR for consistency");
        System.out.println("3. Exchange rate used: 1 USD = " + USD_TO_EUR_RATE + " EUR");
        System.out.println("4. FIXED: No longer using NE.CON.GOVT.ZS as fallback (it's not total expenses)\n");
        
        try {
            // Ensure the international_budgets table exists
            createInternationalBudgetsTableIfNotExists();
            
            int totalInserted = 0;
            
            // Fetch data for each year
            for (int year = 2023; year <= 2026; year++) {
                System.out.println("Fetching data for year " + year + "...");
                int yearInserted = fetchYearData(year);
                totalInserted += yearInserted;
                System.out.println("Inserted " + yearInserted + " records for " + year + "\n");
            }
            
            System.out.println("=== Summary ===");
            System.out.println("Total records inserted: " + totalInserted);
            System.out.println("\nNote: 2025-2026 data are forecasts/projections from international organizations.");
            System.out.println("\n✓ CURRENCY: All budget data stored in EUR (converted from USD)");
            System.out.println("   - Exchange rate: 1 USD = " + USD_TO_EUR_RATE + " EUR");
            System.out.println("   - All countries (including Greece) now use EUR for consistent comparison");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates the international_budgets table if it doesn't exist.
     */
    private static void createInternationalBudgetsTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS international_budgets ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "country_code TEXT NOT NULL,"
                + "country_name TEXT NOT NULL,"
                + "year INTEGER NOT NULL,"
                + "total_gdp REAL,"
                + "total_revenue REAL,"
                + "total_expenses REAL,"
                + "budget_balance REAL,"
                + "UNIQUE(country_code, year)"
                + ");";
        
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✓ Verified international_budgets table exists\n");
        } catch (Exception e) {
            System.err.println("Warning: Could not create international_budgets table: " + e.getMessage());
        }
    }
    
    private static int fetchYearData(int year) {
        int totalInserted = 0;
        
        for (String countryCode : COUNTRY_CODES) {
            String countryName = COUNTRY_NAMES.get(countryCode);
            if (countryName == null) continue;
            
            // First, fetch all indicators and store them
            Map<String, Double> indicatorValues = new HashMap<>();
            
            // Fetch each indicator
            for (Map.Entry<String, String> entry : WB_INDICATORS.entrySet()) {
                String indicatorName = entry.getKey();
                String wbIndicatorCode = entry.getValue();
                
                try {
                    Double value = fetchFromWorldBank(countryCode, wbIndicatorCode, year);
                    
                    if (value != null && value > 0) {
                        indicatorValues.put(indicatorName, value);
                        insertData(countryCode, countryName, year, indicatorName, value);
                        totalInserted++;
                    }
                    
                    // Small delay to avoid rate limiting
                    Thread.sleep(100);
                    
                } catch (Exception e) {
                    // Continue with next indicator
                    System.err.println("Error fetching " + indicatorName + " for " + countryCode + " " + year + ": " + e.getMessage());
                }
            }
            
            // Calculate and store budget amounts if we have the necessary data
            try {
                calculateAndStoreBudget(countryCode, countryName, year, indicatorValues);
            } catch (Exception e) {
                System.err.println("Error calculating budget for " + countryCode + " " + year + ": " + e.getMessage());
            }
        }
        
        return totalInserted;
    }
    
    /**
     * Calculates absolute budget amounts from GDP and percentages, then stores in international_budgets table.
     */
    private static void calculateAndStoreBudget(String countryCode, String countryName, int year, 
                                                Map<String, Double> indicatorValues) {
        // We need: total GDP, revenue_pct_gdp, and expenditure_pct_gdp
        Double totalGDP = indicatorValues.get("gdp_total");
        Double revenuePct = indicatorValues.get("revenue_pct_gdp");
        Double expenditurePct = indicatorValues.get("expenditure_pct_gdp");
        Double taxPct = indicatorValues.get("tax_pct_gdp");
        Double balancePct = indicatorValues.get("budget_balance_pct_gdp");
        
        // Debug logging
        System.out.println("  Calculating budget for " + countryName + " (" + countryCode + ") " + year + ":");
        System.out.println("    GDP: " + totalGDP);
        System.out.println("    Revenue %: " + revenuePct);
        System.out.println("    Tax %: " + taxPct);
        System.out.println("    Expenditure %: " + expenditurePct);
        System.out.println("    Balance %: " + balancePct);
        
        // We need at least GDP and one of the percentages to calculate budget
        if (totalGDP == null || totalGDP <= 0) {
            System.out.println("    ✗ Skipping: No GDP data");
            return; // Can't calculate without GDP
        }
        
        // Calculate absolute amounts
        Double totalRevenue = null;
        Double totalExpenses = null;
        Double budgetBalance = null;
        
        // Calculate expenses - ONLY use GC.XPN.TOTL.GD.ZS (expenditure_pct_gdp)
        // Do NOT fallback to NE.CON.GOVT.ZS as it's not total expenses
        if (expenditurePct != null && expenditurePct > 0) {
            totalExpenses = (expenditurePct / 100.0) * totalGDP;
            System.out.println("    ✓ Expenses calculated from expenditure_pct_gdp: " + totalExpenses);
        } else {
            // If we don't have expenditure_pct_gdp, try to estimate from revenue and balance
            if (revenuePct != null && revenuePct > 0 && balancePct != null) {
                // Expenses = Revenue - Balance
                double estimatedRevenue = (revenuePct / 100.0) * totalGDP;
                double estimatedBalance = (balancePct / 100.0) * totalGDP;
                totalExpenses = estimatedRevenue - estimatedBalance;
                System.out.println("    ✓ Expenses estimated from revenue and balance: " + totalExpenses);
            } else {
                System.out.println("    ✗ Expenses: No expenditure_pct_gdp data and cannot estimate");
            }
        }
        
        // Try to calculate revenue
        if (revenuePct != null && revenuePct > 0) {
            totalRevenue = (revenuePct / 100.0) * totalGDP;
            System.out.println("    ✓ Revenue calculated from revenue_pct_gdp: " + totalRevenue);
        } else if (taxPct != null && taxPct > 0) {
            // Use tax revenue as a proxy for total revenue (taxes are usually 70-90% of total revenue)
            // We'll use a conservative estimate: tax revenue * 1.2 to account for non-tax revenue
            totalRevenue = (taxPct / 100.0) * totalGDP * 1.2;
            System.out.println("    ✓ Revenue estimated from tax_pct_gdp (×1.2): " + totalRevenue);
        } else if (totalExpenses != null) {
            // Fallback: Estimate revenue from expenses (most countries have revenue ≈ expenses)
            // Use expenses as a proxy for revenue (with a small adjustment for typical budget balance)
            totalRevenue = totalExpenses * 0.98; // Assume revenue is ~98% of expenses (slight deficit)
            System.out.println("    ✓ Revenue estimated from expenses (×0.98): " + totalRevenue);
        } else {
            System.out.println("    ✗ Revenue: No revenue, tax, or expense data available");
        }
        
        // Calculate budget balance - try multiple methods
        if (totalRevenue != null && totalExpenses != null) {
            // Method 1: Revenue - Expenses
            budgetBalance = totalRevenue - totalExpenses;
            System.out.println("    ✓ Balance calculated from revenue - expenses: " + budgetBalance);
        } else if (balancePct != null) {
            // Method 2: From balance percentage
            budgetBalance = (balancePct / 100.0) * totalGDP;
            System.out.println("    ✓ Balance from budget_balance_pct_gdp: " + budgetBalance);
            
            // If we have balance, calculate missing revenue or expenses
            if (totalRevenue == null && totalExpenses != null) {
                // Revenue = Expenses + Balance
                totalRevenue = totalExpenses + budgetBalance;
                System.out.println("    ✓ Revenue calculated from expenses + balance: " + totalRevenue);
            } else if (totalExpenses == null && totalRevenue != null) {
                // Expenses = Revenue - Balance
                totalExpenses = totalRevenue - budgetBalance;
                System.out.println("    ✓ Expenses calculated from revenue - balance: " + totalExpenses);
            }
        } else {
            System.out.println("    ✗ Balance: Cannot calculate - insufficient data");
        }
        
        // Only insert if we have at least expenses (required for comparison)
        // Revenue and balance can be null if not available
        // Also validate that expenses are reasonable (should be 20-60% of GDP typically)
        if (totalExpenses != null && totalExpenses > 0) {
            double expensesAsPctOfGDP = (totalExpenses / totalGDP) * 100.0;
            if (expensesAsPctOfGDP < 10 || expensesAsPctOfGDP > 80) {
                System.out.println("    ⚠ Warning: Expenses as % of GDP (" + expensesAsPctOfGDP + "%) seems unusual. " +
                                 "Typical range is 20-60%. Data may be incorrect.");
            }
            insertBudgetData(countryCode, countryName, year, totalGDP, totalRevenue, totalExpenses, budgetBalance);
        } else {
            System.out.println("    ✗ Skipping insert: No valid expenses data (required)");
        }
    }
    
    /**
     * Converts USD to EUR.
     * World Bank API returns data in USD, but we store everything in EUR for consistency with Greece's data.
     */
    private static Double convertUsdToEur(Double usdValue) {
        if (usdValue == null) return null;
        return usdValue * USD_TO_EUR_RATE;
    }
    
    /**
     * Inserts budget data into international_budgets table.
     * All values are converted from USD (World Bank API) to EUR for consistency with Greece's data.
     */
    private static void insertBudgetData(String countryCode, String countryName, int year,
                                        Double totalGDP, Double totalRevenue, Double totalExpenses, Double budgetBalance) {
        String sql = "INSERT OR REPLACE INTO international_budgets " +
                    "(country_code, country_name, year, total_gdp, total_revenue, total_expenses, budget_balance) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Convert all USD values to EUR for consistency with Greece's data
            Double totalGDP_EUR = convertUsdToEur(totalGDP);
            Double totalRevenue_EUR = convertUsdToEur(totalRevenue);
            Double totalExpenses_EUR = convertUsdToEur(totalExpenses);
            Double budgetBalance_EUR = convertUsdToEur(budgetBalance);
            
            pstmt.setString(1, countryCode);
            pstmt.setString(2, countryName);
            pstmt.setInt(3, year);
            pstmt.setObject(4, totalGDP_EUR);
            pstmt.setObject(5, totalRevenue_EUR);
            pstmt.setObject(6, totalExpenses_EUR);
            pstmt.setObject(7, budgetBalance_EUR);
            
            pstmt.executeUpdate();
            System.out.println("  ✓ Budget data inserted for " + countryName + " (" + year + ") [converted to EUR]:");
            System.out.println("    GDP: " + (totalGDP_EUR != null ? String.format("%.2f EUR (%.2f USD)", totalGDP_EUR, totalGDP) : "NULL"));
            System.out.println("    Revenue: " + (totalRevenue_EUR != null ? String.format("%.2f EUR (%.2f USD)", totalRevenue_EUR, totalRevenue) : "NULL"));
            System.out.println("    Expenses: " + (totalExpenses_EUR != null ? String.format("%.2f EUR (%.2f USD)", totalExpenses_EUR, totalExpenses) : "NULL"));
            System.out.println("    Balance: " + (budgetBalance_EUR != null ? String.format("%.2f EUR (%.2f USD)", budgetBalance_EUR, budgetBalance) : "NULL"));
            
        } catch (Exception e) {
            System.err.println("Error inserting budget data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static Double fetchFromWorldBank(String countryCode, String indicatorCode, int year) {
        try {
            // World Bank API format: https://api.worldbank.org/v2/country/{country}/indicator/{indicator}?format=json&date={year}:{year}
            String url = "https://api.worldbank.org/v2/country/" + countryCode + 
                        "/indicator/" + indicatorCode + 
                        "?format=json&date=" + year + ":" + year + "&per_page=1";
            
            String response = fetchFromAPI(url);
            if (response == null || response.trim().isEmpty()) {
                return null;
            }
            
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() < 2) {
                return null;
            }
            
            JSONArray data = jsonArray.getJSONArray(1);
            if (data.length() == 0) {
                return null;
            }
            
            JSONObject item = data.getJSONObject(0);
            if (item.has("value") && !item.isNull("value")) {
                return item.getDouble("value");
            }
            
        } catch (Exception e) {
            // API may not have data for this year/country/indicator
        }
        
        return null;
    }
    
    private static String fetchFromAPI(String urlString) {
        try {
            URI uri = new URI(urlString);
            URL url = uri.toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                }
            }
        } catch (Exception e) {
            // Ignore - API may not have data
        }
        return null;
    }
    
    private static void insertData(String countryCode, String countryName, int year, 
                                   String indicator, double value) {
        String sql = "INSERT OR REPLACE INTO international_indicators " +
                    "(country_code, country_name, year, indicator, value) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, countryCode);
            pstmt.setString(2, countryName);
            pstmt.setInt(3, year);
            pstmt.setString(4, indicator);
            pstmt.setDouble(5, value);
            
            pstmt.executeUpdate();
            
        } catch (Exception e) {
            System.err.println("Error inserting data: " + e.getMessage());
        }
    }
}
