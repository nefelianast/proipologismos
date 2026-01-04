package ui;

import org.json.JSONObject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.math.BigDecimal;

/**
 * Singleton service class for managing budget data.
 * Provides access to budget data for different years, including revenues, expenses,
 * categories, ministries, and decentralized administrations.
 * Loads data from JSON files or uses sample data as fallback.
 */
public class BudgetDataService {
    
    private static BudgetDataService instance;
    private Map<Integer, BudgetYearData> budgetData = new HashMap<>();

     long budget_result = 0;
     long total_revenue = 0;
     long total_expenses = 0;
     long total_ministries = 0;
     long total_da = 0;
    
    private BudgetDataService() {
        loadData();
    }
    
    public static BudgetDataService getInstance() {
        if (instance == null) {
            instance = new BudgetDataService();
        }
        return instance;
    }
    
    private void loadData() {
            try {
            File jsonFile = new File("proipologismos.json");
            if (jsonFile.exists()) {
                String content = new String(Files.readAllBytes(Paths.get(jsonFile.getPath())));
                @SuppressWarnings("unused")
                JSONObject json = new JSONObject(content);
            }
        } catch (Exception e) {
            System.out.println("Could not load JSON file, using sample data: " + e.getMessage());
        }
        
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Sample data for 2023
        BudgetYearData data2023 = new BudgetYearData(2023);
        data2023.setTotalRevenues(95.8);
        data2023.setTotalExpenses(97.4);
        data2023.addCategory("Οικονομικά", 20.5, 21.0);
        data2023.addCategory("Κοινωνική Προστασία", 17.2, 17.7);
        data2023.addCategory("Εκπαίδευση", 14.8, 15.2);
        data2023.addCategory("Υγεία", 11.5, 11.8);
        data2023.addCategory("Άλλα", 12.2, 12.5);
        data2023.addCategory("Υποδομές", 9.8, 10.1);
        data2023.addCategory("Άμυνα", 8.0, 8.2);
        budgetData.put(2023, data2023);
        
        // Sample data for 2024
        BudgetYearData data2024 = new BudgetYearData(2024);
        data2024.setTotalRevenues(98.0);
        data2024.setTotalExpenses(97.4);
        data2024.addCategory("Οικονομικά", 21.3, 21.8);
        data2024.addCategory("Κοινωνική Προστασία", 17.8, 18.3);
        data2024.addCategory("Εκπαίδευση", 15.0, 15.4);
        data2024.addCategory("Υγεία", 12.2, 12.5);
        data2024.addCategory("Άλλα", 12.5, 12.8);
        data2024.addCategory("Υποδομές", 10.1, 10.4);
        data2024.addCategory("Άμυνα", 8.2, 8.4);
        budgetData.put(2024, data2024);
        
        // Sample data for 2025
        BudgetYearData data2025 = new BudgetYearData(2025);
        data2025.setTotalRevenues(100.2);
        data2025.setTotalExpenses(98.5);
        data2025.addCategory("Οικονομικά", 22.1, 22.4);
        data2025.addCategory("Κοινωνική Προστασία", 18.3, 18.6);
        data2025.addCategory("Εκπαίδευση", 15.2, 15.4);
        data2025.addCategory("Υγεία", 12.8, 13.0);
        data2025.addCategory("Άλλα", 12.7, 12.9);
        data2025.addCategory("Υποδομές", 10.4, 10.6);
        data2025.addCategory("Άμυνα", 8.5, 8.6);
        budgetData.put(2025, data2025);
    }
    
    /**
     * Get budget data for a specific year
     */
    public BudgetYearData getBudgetData(int year) {
        return budgetData.get(year);
    }
    
    /**
     * Get total revenues for a year
     */
   public double getTotalRevenues(int year) {
    long totalRevenue = 0;

    String sql = "SELECT total_revenue FROM budget_summary_" + year;

    try (Connection connection = DatabaseConnection.getConnection();
         Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        if (rs.next()) {
            totalRevenue = rs.getLong("total_revenue");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return (double) totalRevenue;
}

    
    /**
     * Get total expenses for a year
     */
    public double getTotalExpenses(int year) {
        long totalExpenses = 0;

        String sql = "SELECT total_expenses FROM budget_summary_" + year;

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                totalExpenses = rs.getLong("total_expenses");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return (double) totalExpenses;
    }
    
    /**
     * Get balance (surplus/deficit) for a year
     */
    public double getBalance(int year) {
        double revenues = getTotalRevenues(year);
        double expenses = getTotalExpenses(year);
        return BudgetStatisticsCalculator.calculateBalance(revenues, expenses);
    }
    
    /**
     * Get percentage change from previous year
     */
    public double getRevenuesChange(int year) {
        double current = getTotalRevenues(year);
        double previous = getTotalRevenues(year - 1);
        return BudgetStatisticsCalculator.calculatePercentageChange(current, previous);
    }
    
    public double getExpensesChange(int year) {
        double current = getTotalExpenses(year);
        double previous = getTotalExpenses(year - 1);
        return BudgetStatisticsCalculator.calculatePercentageChange(current, previous);
    }
    
    /**
     * Get category data for a year (from ministries table)
     */
    public List<CategoryInfo> getCategories(int year) {
        List<CategoryInfo> categories = new ArrayList<>();
        
        // Get total expenses for percentage calculation (use total_expenses as the base)
        double totalExpenses = getTotalExpenses(year);
        if (totalExpenses == 0) return categories;
        
        // Define ministry columns and their Greek names (excluding total_ministries)
        String[][] ministries = {
            {"presidency_of_the_republic", "Προεδρία της Δημοκρατίας"},
            {"hellenic_parliament", "Ελληνικό Κοινοβούλιο"},
            {"presidency_of_the_government", "Προεδρία της Κυβέρνησης"},
            {"ministry_of_interior", "Εσωτερικών"},
            {"ministry_of_foreign_affairs", "Εξωτερικών"},
            {"ministry_of_national_defence", "Εθνικής Άμυνας"},
            {"ministry_of_health", "Υγείας"},
            {"ministry_of_justice", "Δικαιοσύνης"},
            {"ministry_of_education_religious_affairs_and_sports", "Παιδείας, Θρησκευμάτων και Αθλητισμού"},
            {"ministry_of_culture", "Πολιτισμού"},
            {"ministry_of_national_economy_and_finance", "Εθνικής Οικονομίας και Οικονομικών"},
            {"ministry_of_agricultural_development_and_food", "Αγροτικής Ανάπτυξης και Τροφίμων"},
            {"ministry_of_environment_and_energy", "Περιβάλλοντος και Ενέργειας"},
            {"ministry_of_labor_and_social_security", "Εργασίας και Κοινωνικής Ασφάλισης"},
            {"ministry_of_social_cohesion_and_family", "Κοινωνικής Συνοχής και Οικογένειας"},
            {"ministry_of_development", "Ανάπτυξης"},
            {"ministry_of_infrastructure_and_transport", "Υποδομών και Μεταφορών"},
            {"ministry_of_maritime_affairs_and_insular_policy", "Ναυτιλίας και Νησιωτικής Πολιτικής"},
            {"ministry_of_tourism", "Τουρισμού"},
            {"ministry_of_digital_governance", "Ψηφιακής Διακυβέρνησης"},
            {"ministry_of_migration_and_asylum", "Μετανάστευσης και Ασύλου"},
            {"ministry_of_citizen_protection", "Προστασίας του Πολίτη"},
            {"ministry_of_climate_crisis_and_civil_protection", "Κλιματικής Κρίσης και Πολιτικής Προστασίας"}
        };
        
        // Query all ministries
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ministries_" + year)) {
            
            if (rs.next()) {
                for (String[] ministry : ministries) {
                    String columnName = ministry[0];
                    String greekName = ministry[1];
                    long amount = rs.getLong(columnName);
                    
                    if (amount > 0) {
                        double amountDouble = (double) amount;
                        double percentage = (amountDouble / totalExpenses) * 100;
                        categories.add(new CategoryInfo(greekName, amountDouble, percentage));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return categories;
    }
    
    /**
     * Inner class to hold budget data for a year
     */
    public static class BudgetYearData {
        private int year;
        private double totalRevenues;
        private double totalExpenses;
        private List<CategoryInfo> categories = new ArrayList<>();
        
        public BudgetYearData(int year) {
            this.year = year;
        }
        
        public void setTotalRevenues(double totalRevenues) {
            this.totalRevenues = totalRevenues;
        }
        
        public void setTotalExpenses(double totalExpenses) {
            this.totalExpenses = totalExpenses;
        }
        
        public void addCategory(String name, double amount, double percentage) {
            categories.add(new CategoryInfo(name, amount, percentage));
        }
        
        public int getYear() { return year; }
        public double getTotalRevenues() { return totalRevenues; }
        public double getTotalExpenses() { return totalExpenses; }
        public List<CategoryInfo> getCategories() { return categories; }
    }
    
    /**
     * Get revenue breakdown for a year
     */
    public List<CategoryInfo> getRevenueBreakdown(int year) {
        List<CategoryInfo> revenues = new ArrayList<>();
        
        double totalRevenue = getTotalRevenues(year);
        if (totalRevenue == 0) return revenues;
        
        String[][] revenueCategories = {
            {"taxes", "Φορολογία"},
            {"social_contributions", "Κοινωνικές Εισφορές"},
            {"transfers", "Μεταβιβάσεις"},
            {"sales_of_goods_and_services", "Πωλήσεις Αγαθών και Υπηρεσιών"},
            {"other_current_revenue", "Άλλα Τρέχοντα Έσοδα"},
            {"fixed_assets", "Πάγια Περιουσιακά Στοιχεία"},
            {"debt_securities", "Ομόλογα"},
            {"loans", "Δάνεια"},
            {"equity_securities_and_fund_shares", "Μετοχές και Συμμετοχές"},
            {"currency_and_deposit_liabilities", "Νομισματικά Μέσα και Καταθέσεις"},
            {"debt_securities_liabilities", "Ομόλογα (Υποχρεώσεις)"},
            {"loans_liabilities", "Δάνεια (Υποχρεώσεις)"},
            {"financial_derivatives", "Χρηματοοικονομικά Παράγωγα"}
        };
        
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM revenue_" + year)) {
            
            if (rs.next()) {
                for (String[] category : revenueCategories) {
                    String columnName = category[0];
                    String greekName = category[1];
                    long amount = rs.getLong(columnName);
                    
                    if (amount > 0) {
                        double amountDouble = (double) amount;
                        double percentage = (amountDouble / totalRevenue) * 100;
                        revenues.add(new CategoryInfo(greekName, amountDouble, percentage));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return revenues;
    }
    
    /**
     * Get expenses breakdown for a year
     */
    public List<CategoryInfo> getExpensesBreakdown(int year) {
        List<CategoryInfo> expenses = new ArrayList<>();
        
        double totalExpenses = getTotalExpenses(year);
        if (totalExpenses == 0) return expenses;
        
        String[][] expenseCategories = {
            {"employee_benefits", "Προσωπικά"},
            {"social_benefits", "Κοινωνικά Επιδόματα"},
            {"transfers", "Μεταβιβάσεις"},
            {"purchases_of_goods_and_services", "Αγορές Αγαθών και Υπηρεσιών"},
            {"subsidies", "Επιδοτήσεις"},
            {"interest", "Τόκοι"},
            {"other_expenditures", "Άλλες Δαπάνες"},
            {"appropriations", "Πιστώσεις"},
            {"fixed_assets", "Πάγια Περιουσιακά Στοιχεία"},
            {"valuables", "Πολύτιμα"},
            {"loans", "Δάνεια"},
            {"equity_securities_and_fund_shares", "Μετοχές και Συμμετοχές"},
            {"debt_securities_liabilities", "Ομόλογα (Υποχρεώσεις)"},
            {"loans_liabilities", "Δάνεια (Υποχρεώσεις)"}
        };
        
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM expenses_" + year)) {
            
            if (rs.next()) {
                for (String[] category : expenseCategories) {
                    String columnName = category[0];
                    String greekName = category[1];
                    long amount = rs.getLong(columnName);
                    
                    if (amount > 0) {
                        double amountDouble = (double) amount;
                        double percentage = (amountDouble / totalExpenses) * 100;
                        expenses.add(new CategoryInfo(greekName, amountDouble, percentage));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return expenses;
    }
    
    /**
     * Get decentralized administrations for a year
     */
    public List<CategoryInfo> getDecentralizedAdministrations(int year) {
        List<CategoryInfo> administrations = new ArrayList<>();
        
        String[][] adminCategories = {
            {"decentralized_administration_of_attica", "Αποκεντρωμένη Διοίκηση Αττικής"},
            {"decentralized_administration_of_thessaly_central_greece", "Αποκεντρωμένη Διοίκηση Θεσσαλίας & Στερεάς Ελλάδας"},
            {"decentralized_administration_of_epirus_western_macedonia", "Αποκεντρωμένη Διοίκηση Ηπείρου & Δυτικής Μακεδονίας"},
            {"decentralized_administration_of_peloponnese_western_greece_and_ionian", "Αποκεντρωμένη Διοίκηση Πελοποννήσου, Δυτικής Ελλάδας & Ιονίου"},
            {"decentralized_administration_of_aegean", "Αποκεντρωμένη Διοίκηση Αιγαίου"},
            {"decentralized_administration_of_crete", "Αποκεντρωμένη Διοίκηση Κρήτης"},
            {"decentralized_administration_of_macedonia_thrace", "Αποκεντρωμένη Διοίκηση Μακεδονίας & Θράκης"}
        };
        
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM decentralized_administrations_" + year)) {
            
            if (rs.next()) {
                long totalDA = rs.getLong("total_da");
                if (totalDA == 0) return administrations;
                
                for (String[] category : adminCategories) {
                    String columnName = category[0];
                    String greekName = category[1];
                    long amount = rs.getLong(columnName);
                    
                    if (amount > 0) {
                        double amountDouble = (double) amount;
                        double percentage = (amountDouble / (double) totalDA) * 100;
                        administrations.add(new CategoryInfo(greekName, amountDouble, percentage));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return administrations;
    }
    
    /**
     * Inner class to hold category information
     */
    public static class CategoryInfo {
        private String name;
        private double amount;
        private double percentage;
        
        public CategoryInfo(String name, double amount, double percentage) {
            this.name = name;
            this.amount = amount;
            this.percentage = percentage;
        }
        
        public String getName() { return name; }
        public double getAmount() { return amount; }
        public double getPercentage() { return percentage; }
    }
    
    // ========== METHODS FOR GRAPHS (from graphs branch) ==========
    
    /**
     * Get revenue breakdown as Map for graphs/charts.
     * Returns Map: Category -> Amount
     */
    public Map<String, Double> getRevenueBreakdownForGraphs(int year) {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT * FROM revenue_" + year; 

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                data.put("Φόροι", safeGet(rs, "taxes"));
                data.put("Εισφορές", safeGet(rs, "social_contributions"));
                data.put("Μεταβιβάσεις", safeGet(rs, "transfers"));
                data.put("Πωλήσεις Αγαθών", safeGet(rs, "sales_of_goods_and_services"));
                data.put("Άλλα Έσοδα", safeGet(rs, "other_current_revenue"));
                data.put("Πάγια", safeGet(rs, "fixed_assets"));
                data.put("Δάνεια", safeGet(rs, "loans"));
            }
        } catch (Exception e) {
            System.err.println("Error loading revenues for " + year + ": " + e.getMessage());
        }
        return data;
    }

    /**
     * Get expense breakdown as Map for graphs/charts.
     * Returns Map: Category -> Amount
     */
    public Map<String, Double> getExpenseBreakdownForGraphs(int year) {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT * FROM expenses_" + year;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                data.put("Μισθοί", safeGet(rs, "employee_benefits"));
                data.put("Κοινωνικές Παροχές", safeGet(rs, "social_benefits"));
                data.put("Μεταβιβάσεις", safeGet(rs, "transfers"));
                data.put("Αγορές Αγαθών", safeGet(rs, "purchases_of_goods_and_services"));
                data.put("Επιδοτήσεις", safeGet(rs, "subsidies"));
                data.put("Τόκοι", safeGet(rs, "interest"));
                data.put("Πάγια", safeGet(rs, "fixed_assets"));
            }
        } catch (Exception e) {
            System.err.println("Error loading expenses for " + year + ": " + e.getMessage());
        }
        return data;
    }

    /**
     * Get ministries breakdown as Map for graphs/charts.
     * Returns Map: Ministry -> Amount
     */
    public Map<String, Double> getMinistriesBreakdown(int year) {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT * FROM ministries_" + year;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                data.put("Άμυνας", safeGet(rs, "ministry_of_national_defence"));
                data.put("Υγείας", safeGet(rs, "ministry_of_health"));
                data.put("Παιδείας", safeGet(rs, "ministry_of_education_religious_affairs_and_sports"));
                data.put("Εσωτερικών", safeGet(rs, "ministry_of_interior"));
                data.put("Υποδομών", safeGet(rs, "ministry_of_infrastructure_and_transport"));
                data.put("Εργασίας", safeGet(rs, "ministry_of_labor_and_social_security"));
                data.put("Οικονομικών", safeGet(rs, "ministry_of_national_economy_and_finance"));
                data.put("Προστασίας Πολίτη", safeGet(rs, "ministry_of_citizen_protection"));
            }
        } catch (Exception e) {
            System.err.println("Error loading ministries for " + year + ": " + e.getMessage());
        }
        return data;
    }

    /**
     * Get total amount for a specific type from budget summary.
     * Used for pie chart 4 and linear chart.
     */
    public double getTotalAmount(int year, String type) {
        String sql = "SELECT " + type + " FROM budget_summary_" + year;
        double amount = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                amount = safeGet(rs, type);
            }
        } catch (Exception e) {
             // Return 0 if year not found
        }
        return amount;
    }

    /**
     * Helper method for safe conversion from database to double.
     */
    private double safeGet(ResultSet rs, String column) {
        try {
            BigDecimal bd = rs.getBigDecimal(column);
            return bd != null ? bd.doubleValue() : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    // ========== STATISTICAL ANALYSIS METHODS (from main) ==========
    
    /**
     * Get revenue values across multiple years for statistical analysis.
     * 
     * @param startYear Starting year (inclusive)
     * @param endYear Ending year (inclusive)
     * @return Array of revenue values, or null if insufficient data    
     */
    public double[] getRevenuesAcrossYears(int startYear, int endYear) {
        List<Double> revenues = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            double revenue = getTotalRevenues(year);
            if (revenue > 0) {
                revenues.add(revenue);
            }
        }
        
        if (revenues.size() < 2) {
            return null;
        }
        
        return revenues.stream().mapToDouble(Double::doubleValue).toArray();
    }
    
    /**
     * Get expense values across multiple years for statistical analysis.
     * 
     * @param startYear Starting year (inclusive)
     * @param endYear Ending year (inclusive)
     * @return Array of expense values, or null if insufficient data    
     */
    public double[] getExpensesAcrossYears(int startYear, int endYear) {
        List<Double> expenses = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            double expense = getTotalExpenses(year);
            if (expense > 0) {
                expenses.add(expense);
            }
        }
        
        if (expenses.size() < 2) {
            return null;
        }
        
        return expenses.stream().mapToDouble(Double::doubleValue).toArray();
    }
    
    /**
     * Calculate correlation between revenues and expenses across years.
     * 
     * @param startYear Starting year (inclusive)
     * @param endYear Ending year (inclusive)
     * @return Correlation coefficient, or Double.NaN if insufficient data
     */
    public double calculateRevenueExpenseCorrelation(int startYear, int endYear) {
        double[] revenues = getRevenuesAcrossYears(startYear, endYear); 
        double[] expenses = getExpensesAcrossYears(startYear, endYear); 
        
        if (revenues == null || expenses == null || 
            revenues.length != expenses.length || 
            revenues.length < 2) {
            return Double.NaN;
        }
        
        return StatisticalAnalysis.calculateCorrelation(revenues, expenses);
    }
    
    /**
     * Get statistical summary for revenues across multiple years.      
     * 
     * @param startYear Starting year (inclusive)
     * @param endYear Ending year (inclusive)
     * @return Statistical summary string, or null if insufficient data
     */
    public String getRevenuesStatisticalSummary(int startYear, int endYear) {
        double[] revenues = getRevenuesAcrossYears(startYear, endYear); 
        if (revenues == null) {
            return null;
        }
        return StatisticalAnalysis.generateStatisticalSummary(revenues);
    }
    
    /**
     * Get statistical summary for expenses across multiple years.      
     * 
     * @param startYear Starting year (inclusive)
     * @param endYear Ending year (inclusive)
     * @return Statistical summary string, or null if insufficient data 
     */
    public String getExpensesStatisticalSummary(int startYear, int endYear) {
        double[] expenses = getExpensesAcrossYears(startYear, endYear); 
        if (expenses == null) {
            return null;
        }
        return StatisticalAnalysis.generateStatisticalSummary(expenses);
    }
    
    /**
     * Identify outliers in revenue changes across years.
     * 
     * @param startYear Starting year (inclusive)
     * @param endYear Ending year (inclusive)
     * @return List of outlier revenue values, or empty list if insufficient data
     */
    public List<Double> identifyRevenueOutliers(int startYear, int endYear) {
        double[] revenues = getRevenuesAcrossYears(startYear, endYear); 
        if (revenues == null) {
            return new ArrayList<>();
        }
        return StatisticalAnalysis.identifyOutliers(revenues);
    }
    
    /**
     * Identify outliers in expense changes across years.
     * 
     * @param startYear Starting year (inclusive)
     * @param endYear Ending year (inclusive)
     * @return List of outlier expense values, or empty list if insufficient data
     */
    public List<Double> identifyExpenseOutliers(int startYear, int endYear) {
        double[] expenses = getExpensesAcrossYears(startYear, endYear); 
        if (expenses == null) {
            return new ArrayList<>();
        }
        return StatisticalAnalysis.identifyOutliers(expenses);
    }
    
    /**
     * Calculate linear regression for revenue trends across years.     
     * Returns slope and intercept for predicting future revenues.      
     * 
     * @param startYear Starting year (inclusive)
     * @param endYear Ending year (inclusive)
     * @return Array with [slope, intercept], or null if insufficient data
     */
    public double[] calculateRevenueTrend(int startYear, int endYear) { 
        List<Double> revenues = new ArrayList<>();
        List<Double> years = new ArrayList<>();
        
        for (int year = startYear; year <= endYear; year++) {
            double revenue = getTotalRevenues(year);
            if (revenue > 0) {
                revenues.add(revenue);
                years.add((double) year);
            }
        }
        
        if (revenues.size() < 2) {
            return null;
        }
        
        double[] yearArray = years.stream().mapToDouble(Double::doubleValue).toArray();
        double[] revenueArray = revenues.stream().mapToDouble(Double::doubleValue).toArray();
        
        return StatisticalAnalysis.calculateLinearRegression(yearArray, revenueArray);
    }
    
    /**
     * Calculate linear regression for expense trends across years.     
     * Returns slope and intercept for predicting future expenses.      
     * 
     * @param startYear Starting year (inclusive)
     * @param endYear Ending year (inclusive)
     * @return Array with [slope, intercept], or null if insufficient data
     */
    public double[] calculateExpenseTrend(int startYear, int endYear) { 
        List<Double> expenses = new ArrayList<>();
        List<Double> years = new ArrayList<>();
        
        for (int year = startYear; year <= endYear; year++) {
            double expense = getTotalExpenses(year);
            if (expense > 0) {
                expenses.add(expense);
                years.add((double) year);
            }
        }
        
        if (expenses.size() < 2) {
            return null;
        }
        
        double[] yearArray = years.stream().mapToDouble(Double::doubleValue).toArray();
        double[] expenseArray = expenses.stream().mapToDouble(Double::doubleValue).toArray();
        
        return StatisticalAnalysis.calculateLinearRegression(yearArray, expenseArray);
    }
    
    /**
     * Get coefficient of variation for revenues across years.
     * Higher CV indicates more variability in revenue.
     * 
     * @param startYear Starting year (inclusive)
     * @param endYear Ending year (inclusive)
     * @return Coefficient of variation as percentage, or Double.NaN if insufficient data
     */
    public double getRevenuesCoefficientOfVariation(int startYear, int endYear) {
        double[] revenues = getRevenuesAcrossYears(startYear, endYear); 
        if (revenues == null) {
            return Double.NaN;
        }
        return StatisticalAnalysis.calculateCoefficientOfVariation(revenues);
    }
    
    /**
     * Get coefficient of variation for expenses across years.
     * Higher CV indicates more variability in expenses.
     * 
     * @param startYear Starting year (inclusive)
     * @param endYear Ending year (inclusive)
     * @return Coefficient of variation as percentage, or Double.NaN if insufficient data
     */
    public double getExpensesCoefficientOfVariation(int startYear, int endYear) {
        double[] expenses = getExpensesAcrossYears(startYear, endYear); 
        if (expenses == null) {
            return Double.NaN;
        }
        return StatisticalAnalysis.calculateCoefficientOfVariation(expenses);
    }
}