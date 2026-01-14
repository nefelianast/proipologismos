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

public class FetchInternationalDataFromAPIs {

    private static final double USD_TO_EUR_RATE = 0.92;

    private static final String[] COUNTRY_CODES = {
        "GRC", "DEU", "FRA", "ITA", "ESP", "GBR", "USA", "JPN", "CAN", "AUS",
        "NLD", "BEL", "AUT", "SWE", "DNK", "FIN", "NOR", "POL", "CZE", "HUN",
        "PRT", "IRL", "CHE", "LUX", "ISL", "EST", "LVA", "LTU", "SVK", "SVN"
    };

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

    private static final Map<String, String> WB_INDICATORS = new HashMap<>();
    static {
        WB_INDICATORS.put("gdp_per_capita", "NY.GDP.PCAP.CD");
        WB_INDICATORS.put("gdp_total", "NY.GDP.MKTP.CD");
        WB_INDICATORS.put("tax_pct_gdp", "GC.TAX.TOTL.GD.ZS");
        WB_INDICATORS.put("debt_to_gdp", "GC.DOD.TOTL.GD.ZS");
        WB_INDICATORS.put("budget_balance_pct_gdp", "GC.BAL.CASH.GD.ZS");
        WB_INDICATORS.put("revenue_pct_gdp", "GC.REV.XGR.GD.ZS");
        WB_INDICATORS.put("expenditure_pct_gdp", "GC.XPN.TOTL.GD.ZS");
    }

    public static void main(String[] args) {
        System.out.println("=== Fetching International Indicators from APIs ===\n");

        try {
            createInternationalBudgetsTableIfNotExists();
            int totalInserted = 0;

            for (int year = 2023; year <= 2026; year++) {
                System.out.println("Fetching data for year " + year + "...");
                int yearInserted = fetchYearData(year);
                totalInserted += yearInserted;
                System.out.println("Inserted " + yearInserted + " records for " + year + "\n");
            }

            System.out.println("=== Summary ===");
            System.out.println("Total records inserted: " + totalInserted);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

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

            Map<String, Double> indicatorValues = new HashMap<>();

            for (Map.Entry<String, String> entry : WB_INDICATORS.entrySet()) {
                String indicatorName = entry.getKey();
                String wbIndicatorCode = entry.getValue();

                try {
                    Double value = fetchFromWorldBank(countryCode, wbIndicatorCode, year);

                    // ✅ Αν null ή <=0, βάζουμε 0
                    if (value == null || value <= 0) {
                        value = 0.0;
                    }

                    indicatorValues.put(indicatorName, value);
                    insertData(countryCode, countryName, year, indicatorName, value);
                    totalInserted++;

                    Thread.sleep(100);

                } catch (Exception e) {
                    System.err.println("Error fetching " + indicatorName + " for " + countryCode + " " + year + ": " + e.getMessage());
                }
            }

            try {
                calculateAndStoreBudget(countryCode, countryName, year, indicatorValues);
            } catch (Exception e) {
                System.err.println("Error calculating budget for " + countryCode + " " + year + ": " + e.getMessage());
            }
        }

        return totalInserted;
    }

    private static void calculateAndStoreBudget(String countryCode, String countryName, int year, 
                                                Map<String, Double> indicatorValues) {
        Double totalGDP = indicatorValues.get("gdp_total");
        Double revenuePct = indicatorValues.get("revenue_pct_gdp");
        Double expenditurePct = indicatorValues.get("expenditure_pct_gdp");
        Double taxPct = indicatorValues.get("tax_pct_gdp");
        Double balancePct = indicatorValues.get("budget_balance_pct_gdp");

        // Αν totalGDP είναι 0, μπορούμε να συνεχίσουμε με 0
        if (totalGDP == null) totalGDP = 0.0;

        Double totalRevenue = null;
        Double totalExpenses = null;
        Double budgetBalance = null;

        if (expenditurePct != null && expenditurePct > 0) {
            totalExpenses = (expenditurePct / 100.0) * totalGDP;
        } else if (revenuePct != null && balancePct != null) {
            totalExpenses = (revenuePct / 100.0) * totalGDP - (balancePct / 100.0) * totalGDP;
        } else {
            totalExpenses = 0.0;
        }

        if (revenuePct != null && revenuePct > 0) {
            totalRevenue = (revenuePct / 100.0) * totalGDP;
        } else if (taxPct != null && taxPct > 0) {
            totalRevenue = (taxPct / 100.0) * totalGDP * 1.2;
        } else {
            totalRevenue = totalExpenses; // fallback
        }

        if (totalRevenue != null && totalExpenses != null) {
            budgetBalance = totalRevenue - totalExpenses;
        } else {
            budgetBalance = 0.0;
        }

        insertBudgetData(countryCode, countryName, year, totalGDP, totalRevenue, totalExpenses, budgetBalance);
    }

    private static Double convertUsdToEur(Double usdValue) {
        if (usdValue == null) return 0.0;
        return usdValue * USD_TO_EUR_RATE;
    }

    private static void insertBudgetData(String countryCode, String countryName, int year,
                                        Double totalGDP, Double totalRevenue, Double totalExpenses, Double budgetBalance) {
        String sql = "INSERT OR REPLACE INTO international_budgets " +
                    "(country_code, country_name, year, total_gdp, total_revenue, total_expenses, budget_balance) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, countryCode);
            pstmt.setString(2, countryName);
            pstmt.setInt(3, year);
            pstmt.setObject(4, convertUsdToEur(totalGDP));
            pstmt.setObject(5, convertUsdToEur(totalRevenue));
            pstmt.setObject(6, convertUsdToEur(totalExpenses));
            pstmt.setObject(7, convertUsdToEur(budgetBalance));

            pstmt.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error inserting budget data: " + e.getMessage());
        }
    }

    private static Double fetchFromWorldBank(String countryCode, String indicatorCode, int year) {
        try {
            String url = "https://api.worldbank.org/v2/country/" + countryCode + 
                        "/indicator/" + indicatorCode + 
                        "?format=json&date=" + year + ":" + year + "&per_page=1";

            String response = fetchFromAPI(url);
            if (response == null || response.trim().isEmpty()) return null;

            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() < 2) return null;

            JSONArray data = jsonArray.getJSONArray(1);
            if (data.length() == 0) return null;

            JSONObject item = data.getJSONObject(0);
            if (item.has("value") && !item.isNull("value")) {
                return item.getDouble("value");
            }

        } catch (Exception e) { }

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
        } catch (Exception e) { }
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
