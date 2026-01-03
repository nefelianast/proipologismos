package ui;

import org.json.JSONObject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.HashMap; // ΠΡΟΣΘΕΣΑ ΑΥΤΑ ΤΑ 3
import java.util.Map;
import java.math.BigDecimal;

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
    String DB = "jdbc:sqlite:src/main/resources/database/BudgetData.db";
    long totalRevenue = 0;

    String sql = "SELECT total_revenue FROM budget_summary_" + year;

    try (Connection connection = DriverManager.getConnection(DB);
         Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        if (rs.next()) {
            totalRevenue = rs.getLong("total_revenue");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return totalRevenue;
}

    
    /**
     * Get total expenses for a year
     */
    public double getTotalExpenses(int year) {
        BudgetYearData data = budgetData.get(year);
        return data != null ? data.getTotalExpenses() : 0.0;
    }
    
    /**
     * Get balance (surplus/deficit) for a year
     */
    public double getBalance(int year) {
        BudgetYearData data = budgetData.get(year);
        if (data == null) return 0.0;
        return data.getTotalRevenues() - data.getTotalExpenses();
    }
    
    /**
     * Get percentage change from previous year
     */
    public double getRevenuesChange(int year) {
        BudgetYearData current = budgetData.get(year);
        BudgetYearData previous = budgetData.get(year - 1);
        if (current == null || previous == null) return 0.0;
        return ((current.getTotalRevenues() - previous.getTotalRevenues()) / previous.getTotalRevenues()) * 100;
    }
    
    public double getExpensesChange(int year) {
        BudgetYearData current = budgetData.get(year);
        BudgetYearData previous = budgetData.get(year - 1);
        if (current == null || previous == null) return 0.0;
        return ((current.getTotalExpenses() - previous.getTotalExpenses()) / previous.getTotalExpenses()) * 100;
    }
    
    /**
     * Get category data for a year
     */
    public List<CategoryInfo> getCategories(int year) {
        BudgetYearData data = budgetData.get(year);
        if (data == null) return new ArrayList<>();
        return data.getCategories();
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
    
    // ΟΙ ΝΕΕΣ ΜΕΘΟΔΟΙ ΓΙΑ ΤΑ ΔΙΑΓΡΑΜΜΑΤΑ 

    // 1. Παίρνουμε τα Έσοδα αναλυτικά (Map: Κατηγορία -> Ποσό)
    public Map<String, Double> getRevenueBreakdown(int year) {
        Map<String, Double> data = new HashMap<>();
        String DB_URL = "jdbc:sqlite:src/main/resources/database/BudgetData.db";
        String sql = "SELECT * FROM revenue_" + year; 

        try (Connection conn = DriverManager.getConnection(DB_URL);
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

    // 2. Παίρνουμε τα Έξοδα αναλυτικά
    public Map<String, Double> getExpenseBreakdown(int year) {
        Map<String, Double> data = new HashMap<>();
        String DB_URL = "jdbc:sqlite:src/main/resources/database/BudgetData.db";
        String sql = "SELECT * FROM expenses_" + year;

        try (Connection conn = DriverManager.getConnection(DB_URL);
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

    // 3. Παίρνουμε τα Υπουργεία αναλυτικά
    public Map<String, Double> getMinistriesBreakdown(int year) {
        Map<String, Double> data = new HashMap<>();
        String DB_URL = "jdbc:sqlite:src/main/resources/database/BudgetData.db";
        String sql = "SELECT * FROM ministries_" + year;

        try (Connection conn = DriverManager.getConnection(DB_URL);
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

    // 4. Βοηθητική μέθοδος για να παίρνουμε τα Σύνολα (για την Πίτα 4 και το Γραμμικό)
    public double getTotalAmount(int year, String type) {
        String DB_URL = "jdbc:sqlite:src/main/resources/database/BudgetData.db";
        String sql = "SELECT " + type + " FROM budget_summary_" + year;
        double amount = 0;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                amount = safeGet(rs, type);
            }
        } catch (Exception e) {
             // Μην τυπώνεις λάθος, απλά γύρνα 0 αν δεν βρεθεί το έτος
        }
        return amount;
    }

    // Βοηθητική μέθοδος για ασφαλή μετατροπή από βάση σε double
    private double safeGet(ResultSet rs, String column) {
        try {
            BigDecimal bd = rs.getBigDecimal(column);
            return bd != null ? bd.doubleValue() : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
}

