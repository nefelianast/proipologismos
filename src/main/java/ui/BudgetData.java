package ui;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.math.BigDecimal;

// βοηθητική κλάση για τη φόρτωση, διαχείριση & πρόσβαση στα δεδομένα προϋπολογισμού
public class BudgetData {
    
    private static BudgetData instance;

    // αποθήκευση συνολικών ποσών 
     long budget_result = 0;        // συνολικό αποτέλεσμα προϋπολογισμού
     long total_revenue = 0;        // συνολικά έσοδα
     long total_expenses = 0;       // συνολικές δαπάνες
     long total_ministries = 0;     // συνολικά υπουργεία
     long total_da = 0;             // συνολικές αποκεντρωμένες διοικήσεις
    
    
    private BudgetData() {}
    
    public static BudgetData getInstance() {
        if (instance == null) {
            instance = new BudgetData();
        }
        return instance;
    }
    
    //επιστρέφει τα συνολικά έσοδα για ένα έτος 
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

    
    // επιστρέφει τις συνολικές δαπάνες για ένα έτος
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
    
    // επιστρέφει το ισοζύγιο για ένα έτος
    public double getBalance(int year) {
        double revenues = getTotalRevenues(year);
        double expenses = getTotalExpenses(year);
        return StatisticalAnalysis.calculateBalance(revenues, expenses);
    }
    
    // υπολογίζει την ποσοστιαία μεταβολή των εσόδων σε σχέση με το προηγούμενο έτος
    public double getRevenuesChange(int year) {
        double current = getTotalRevenues(year);
        double previous = getTotalRevenues(year - 1);
        return StatisticalAnalysis.calculatePercentageChange(current, previous);
    }
    
    // υπολογίζει την ποσοστιαία μεταβολή των δαπανών σε σχέση με το προηγούμενο έτος
    public double getExpensesChange(int year) {
        double current = getTotalExpenses(year);
        double previous = getTotalExpenses(year - 1);
        return StatisticalAnalysis.calculatePercentageChange(current, previous);
    }
    
    // επιστρέφει τα δεδομένα κατηγοριών για ένα έτος (από τον πίνακα ministries)
    public List<CategoryInfo> getCategories(int year) {
        List<CategoryInfo> categories = new ArrayList<>();
        
        // λήψη συνολικών δαπανών για τον υπολογισμό ποσοστών
        double totalExpenses = getTotalExpenses(year);
        if (totalExpenses == 0) return categories;
        
        // ορισμός στηλών υπουργείων και των ελληνικών ονομάτων τους
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
        
        // Ταξινόμηση κατά δαπάνες (φθίνουσα σειρά)
        categories.sort((a, b) -> Double.compare(b.getAmount(), a.getAmount()));
        
        return categories;
    }
    
    // επιστρέφει την ανάλυση εσόδων για ένα έτος
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
    
    // επιστρέφει την ανάλυση δαπανών για ένα έτος
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
    
    // επιστρέφει τις αποκεντρωμένες διοικήσεις για ένα έτος
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
    
    //εσωτερική κλάση για την αποθήκευση πληροφοριών κατηγορίας
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
    
    
    // επιστρέφει την ανάλυση εσόδων ως map για γραφήματα/διαγράμματα
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

    //επιστρέφει την ανάλυση δαπανών ως map για γραφήματα/διαγράμματα
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

    //επιστρέφει την ανάλυση υπουργείων ως map για γραφήματα/διαγράμματα
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

    //επιστρέφει την ανάλυση αποκεντρωμένων διοικήσεων ως map για γραφήματα/διαγράμματα
    public Map<String, Double> getDecentralizedAdministrationsBreakdown(int year) {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT * FROM decentralized_administrations_" + year;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                data.put("Αττικής", safeGet(rs, "decentralized_administration_of_attica"));
                data.put("Θεσσαλίας & Στερεάς Ελλάδας", safeGet(rs, "decentralized_administration_of_thessaly_central_greece"));
                data.put("Ηπείρου & Δυτικής Μακεδονίας", safeGet(rs, "decentralized_administration_of_epirus_western_macedonia"));
                data.put("Πελοποννήσου, Δυτικής Ελλάδας & Ιονίου", safeGet(rs, "decentralized_administration_of_peloponnese_western_greece_and_ionian"));
                data.put("Αιγαίου", safeGet(rs, "decentralized_administration_of_aegean"));
                data.put("Κρήτης", safeGet(rs, "decentralized_administration_of_crete"));
                data.put("Μακεδονίας & Θράκης", safeGet(rs, "decentralized_administration_of_macedonia_thrace"));
            }
        } catch (Exception e) {
            System.err.println("Error loading decentralized administrations for " + year + ": " + e.getMessage());
        }
        return data;
    }

    //επιστρέφει το συνολικό ποσό για ένα συγκεκριμένο τύπο από το budget summary
    public double getTotalAmount(int year, String type) {
        String sql = "SELECT " + type + " FROM budget_summary_" + year;
        double amount = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                amount = safeGet(rs, type);
            }
        } catch (Exception e) {}
        return amount;
    }

    //βοηθητική μέθοδος για ασφαλή μετατροπή από βάση δεδομένων σε double
    private double safeGet(ResultSet rs, String column) {
        try {
            BigDecimal bd = rs.getBigDecimal(column);
            return bd != null ? bd.doubleValue() : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
        
    //επιστρέφει τις τιμές εσόδων για πολλαπλά έτη για στατιστική ανάλυση
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
    
    //επιστρέφει τις τιμές δαπανών για πολλαπλά έτη για στατιστική ανάλυση
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
    
    //υπολογίζει τον συντελεστή συσχέτισης μεταξύ εσόδων και δαπανών για πολλαπλά έτη
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
    
    //επιστρέφει στατιστική περίληψη για τα έσοδα σε πολλαπλά έτη
    public String getRevenuesStatisticalSummary(int startYear, int endYear) {
        double[] revenues = getRevenuesAcrossYears(startYear, endYear); 
        if (revenues == null) {
            return null;
        }
        return StatisticalAnalysis.generateStatisticalSummary(revenues);
    }
    
    //επιστρέφει στατιστική περίληψη για τις δαπάνες σε πολλαπλά έτη
    public String getExpensesStatisticalSummary(int startYear, int endYear) {
        double[] expenses = getExpensesAcrossYears(startYear, endYear); 
        if (expenses == null) {
            return null;
        }
        return StatisticalAnalysis.generateStatisticalSummary(expenses);
    }
    
    //εντοπίζει ακραίες τιμές στις μεταβολές εσόδων σε πολλαπλά έτη
    public List<Double> identifyRevenueOutliers(int startYear, int endYear) {
        double[] revenues = getRevenuesAcrossYears(startYear, endYear); 
        if (revenues == null) {
            return new ArrayList<>();
        }
        return StatisticalAnalysis.identifyOutliers(revenues);
    }
    
    //εντοπίζει ακραίες τιμές στις μεταβολές δαπανών σε πολλαπλά έτη
    public List<Double> identifyExpenseOutliers(int startYear, int endYear) {
        double[] expenses = getExpensesAcrossYears(startYear, endYear); 
        if (expenses == null) {
            return new ArrayList<>();
        }
        return StatisticalAnalysis.identifyOutliers(expenses);
    }
    
    //υπολογίζει γραμμική παλινδρόμηση για τις τάσεις εσόδων σε πολλαπλά έτη
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
    
    //υπολογίζει γραμμική παλινδρόμηση για τις τάσεις δαπανών σε πολλαπλά έτη
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
    
    //επιστρέφει τον συντελεστή μεταβλητότητας για τα έσοδα σε πολλαπλά έτη
    public double getRevenuesCoefficientOfVariation(int startYear, int endYear) {
        double[] revenues = getRevenuesAcrossYears(startYear, endYear); 
        if (revenues == null) {
            return Double.NaN;
        }
        return StatisticalAnalysis.calculateCoefficientOfVariation(revenues);
    }
    
    //επιστρέφει τον συντελεστή μεταβλητότητας για τις δαπάνες σε πολλαπλά έτη
    public double getExpensesCoefficientOfVariation(int startYear, int endYear) {
        double[] expenses = getExpensesAcrossYears(startYear, endYear); 
        if (expenses == null) {
            return Double.NaN;
        }
        return StatisticalAnalysis.calculateCoefficientOfVariation(expenses);
    }
    
    // ========== ΔΙΕΘΝΩΝ ΠΡΟΥΠΟΛΟΓΙΣΜΩΝ ΜΕΘΟΔΟΙ ========== //
    
    /**
     * Επιστρέφει όλους τους διεθνείς δείκτες για μια συγκεκριμένη χώρα και έτος.
     * 
     * @param countryCode Ο κωδικός χώρας (π.χ. "DEU", "GRC")
     * @param year Το έτος
     * @return Map με όνομα δείκτη -> τιμή
     */
    public Map<String, Double> getInternationalIndicators(String countryCode, int year) {
        Map<String, Double> indicators = new HashMap<>();
        String sql = "SELECT indicator, value FROM international_indicators " +
                     "WHERE country_code = ? AND year = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, countryCode);
            pstmt.setInt(2, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String indicator = rs.getString("indicator");
                    double value = rs.getDouble("value");
                    indicators.put(indicator, value);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading international indicators: " + e.getMessage());
            e.printStackTrace();
        }
        
        return indicators;
    }
    
    /**
     * Επιστρέφει την τιμή ενός συγκεκριμένου δείκτη για μια χώρα και έτος.
     * 
     * @param countryCode Ο κωδικός χώρας
     * @param year Το έτος
     * @param indicator Ο δείκτης (π.χ. "tax_pct_gdp", "gdp_per_capita")
     * @return Η τιμή του δείκτη ή Double.NaN αν δεν βρεθεί
     */
    public double getInternationalIndicator(String countryCode, int year, String indicator) {
        String sql = "SELECT value FROM international_indicators " +
                     "WHERE country_code = ? AND year = ? AND indicator = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, countryCode);
            pstmt.setInt(2, year);
            pstmt.setString(3, indicator);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("value");
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading international indicator: " + e.getMessage());
            e.printStackTrace();
        }
        
        return Double.NaN;
    }
    
    /**
     * Επιστρέφει όλες τις διαθέσιμες χώρες στη βάση δεδομένων.
     * 
     * @return List με κωδικούς χωρών
     */
    public List<String> getAvailableCountries() {
        List<String> countries = new ArrayList<>();
        String sql = "SELECT DISTINCT country_code, country_name FROM international_indicators " +
                     "ORDER BY country_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                countries.add(rs.getString("country_code"));
            }
        } catch (Exception e) {
            System.err.println("Error loading available countries: " + e.getMessage());
            e.printStackTrace();
        }
        
        return countries;
    }
    
    /**
     * Επιστρέφει το όνομα μιας χώρας από τον κωδικό της.
     * 
     * @param countryCode Ο κωδικός χώρας
     * @return Το όνομα της χώρας ή null αν δεν βρεθεί
     */
    public String getCountryName(String countryCode) {
        String sql = "SELECT DISTINCT country_name FROM international_indicators " +
                     "WHERE country_code = ? LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, countryCode);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("country_name");
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading country name: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Επιστρέφει όλους τους διαθέσιμους δείκτες στη βάση δεδομένων.
     * 
     * @return List με ονόματα δεικτών
     */
    public List<String> getAvailableIndicators() {
        List<String> indicators = new ArrayList<>();
        String sql = "SELECT DISTINCT indicator FROM international_indicators ORDER BY indicator";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                indicators.add(rs.getString("indicator"));
            }
        } catch (Exception e) {
            System.err.println("Error loading available indicators: " + e.getMessage());
            e.printStackTrace();
        }
        
        return indicators;
    }
    
    /**
     * Επιστρέφει όλες τις τιμές ενός δείκτη για όλες τις χώρες σε ένα έτος.
     * Χρήσιμο για συγκρίσεις μεταξύ χωρών.
     * 
     * @param indicator Ο δείκτης
     * @param year Το έτος
     * @return Map με country_code -> value
     */
    public Map<String, Double> getIndicatorByCountry(String indicator, int year) {
        Map<String, Double> values = new HashMap<>();
        String sql = "SELECT country_code, value FROM international_indicators " +
                     "WHERE indicator = ? AND year = ? ORDER BY country_code";
        
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, indicator);
            pstmt.setInt(2, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String countryCode = rs.getString("country_code");
                    double value = rs.getDouble("value");
                    values.put(countryCode, value);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading indicator by country: " + e.getMessage());
            e.printStackTrace();
        }
        
        return values;
    }
    
    /**
     * Εσωτερική κλάση για αποθήκευση πληροφοριών διεθνούς δείκτη.
     */
    public static class InternationalIndicator {
        private String countryCode;
        private String countryName;
        private int year;
        private String indicator;
        private double value;
        
        public InternationalIndicator(String countryCode, String countryName, int year, 
                                      String indicator, double value) {
            this.countryCode = countryCode;
            this.countryName = countryName;
            this.year = year;
            this.indicator = indicator;
            this.value = value;
        }
        
        public String getCountryCode() { return countryCode; }
        public String getCountryName() { return countryName; }
        public int getYear() { return year; }
        public String getIndicator() { return indicator; }
        public double getValue() { return value; }
    }
    
    /**
     * Επιστρέφει όλους τους διεθνείς δείκτες ως λίστα αντικειμένων.
     * 
     * @param countryCode Ο κωδικός χώρας (null για όλες τις χώρες)
     * @param year Το έτος (0 για όλα τα έτη)
     * @return List με InternationalIndicator αντικείμενα
     */
    public List<InternationalIndicator> getAllInternationalIndicators(String countryCode, int year) {
        List<InternationalIndicator> indicators = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT country_code, country_name, year, indicator, value " +
            "FROM international_indicators WHERE 1=1"
        );
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (countryCode != null && !countryCode.isEmpty()) {
                sql.append(" AND country_code = ?");
            }
            if (year > 0) {
                sql.append(" AND year = ?");
            }
            sql.append(" ORDER BY country_name, year, indicator");
            
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            
            if (countryCode != null && !countryCode.isEmpty()) {
                pstmt.setString(paramIndex++, countryCode);
            }
            if (year > 0) {
                pstmt.setInt(paramIndex++, year);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    indicators.add(new InternationalIndicator(
                        rs.getString("country_code"),
                        rs.getString("country_name"),
                        rs.getInt("year"),
                        rs.getString("indicator"),
                        rs.getDouble("value")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading all international indicators: " + e.getMessage());
            e.printStackTrace();
        }
        
        return indicators;
    }
    
    /**
     * Ελέγχει αν ένας δείκτης είναι διαθέσιμος για συγκεκριμένο έτος και/ή χώρα.
     * 
     * @param indicator Ο δείκτης
     * @param countryCode Η χώρα (null για οποιαδήποτε)
     * @param year Το έτος (0 για οποιοδήποτε)
     * @return true αν ο δείκτης είναι διαθέσιμος
     */
    public boolean isIndicatorAvailable(String indicator, String countryCode, int year) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) as count FROM international_indicators WHERE indicator = ?"
        );
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (countryCode != null && !countryCode.isEmpty()) {
                sql.append(" AND country_code = ?");
            }
            if (year > 0) {
                sql.append(" AND year = ?");
            }
            
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, indicator);
            int paramIndex = 2;
            
            if (countryCode != null && !countryCode.isEmpty()) {
                pstmt.setString(paramIndex++, countryCode);
            }
            if (year > 0) {
                pstmt.setInt(paramIndex++, year);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking indicator availability: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Ελέγχει αν ένα έτος έχει δεδομένα για συγκεκριμένο δείκτη και/ή χώρα.
     * 
     * @param year Το έτος
     * @param indicator Ο δείκτης (null για οποιονδήποτε)
     * @param countryCode Η χώρα (null για οποιαδήποτε)
     * @return true αν το έτος έχει δεδομένα
     */
    public boolean isYearAvailable(int year, String indicator, String countryCode) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) as count FROM international_indicators WHERE year = ?"
        );
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (indicator != null && !indicator.isEmpty()) {
                sql.append(" AND indicator = ?");
            }
            if (countryCode != null && !countryCode.isEmpty()) {
                sql.append(" AND country_code = ?");
            }
            
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1, year);
            int paramIndex = 2;
            
            if (indicator != null && !indicator.isEmpty()) {
                pstmt.setString(paramIndex++, indicator);
            }
            if (countryCode != null && !countryCode.isEmpty()) {
                pstmt.setString(paramIndex++, countryCode);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking year availability: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Ελέγχει αν μια χώρα έχει δεδομένα για συγκεκριμένο δείκτη και/ή έτος.
     * 
     * @param countryCode Ο κωδικός χώρας
     * @param indicator Ο δείκτης (null για οποιονδήποτε)
     * @param year Το έτος (0 για οποιοδήποτε)
     * @return true αν η χώρα έχει δεδομένα
     */
    public boolean isCountryAvailable(String countryCode, String indicator, int year) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) as count FROM international_indicators WHERE country_code = ?"
        );
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (indicator != null && !indicator.isEmpty()) {
                sql.append(" AND indicator = ?");
            }
            if (year > 0) {
                sql.append(" AND year = ?");
            }
            
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, countryCode);
            int paramIndex = 2;
            
            if (indicator != null && !indicator.isEmpty()) {
                pstmt.setString(paramIndex++, indicator);
            }
            if (year > 0) {
                pstmt.setInt(paramIndex++, year);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking country availability: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Inner class για international budget data
     */
    public static class InternationalBudget {
        private String countryCode;
        private String countryName;
        private int year;
        private double totalGDP;
        private double totalRevenue;
        private double totalExpenses;
        private double budgetBalance;
        
        public InternationalBudget(String countryCode, String countryName, int year,
                                  double totalGDP, double totalRevenue, double totalExpenses, double budgetBalance) {
            this.countryCode = countryCode;
            this.countryName = countryName;
            this.year = year;
            this.totalGDP = totalGDP;
            this.totalRevenue = totalRevenue;
            this.totalExpenses = totalExpenses;
            this.budgetBalance = budgetBalance;
        }
        
        public String getCountryCode() { return countryCode; }
        public String getCountryName() { return countryName; }
        public int getYear() { return year; }
        public double getTotalGDP() { return totalGDP; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getTotalExpenses() { return totalExpenses; }
        public double getBudgetBalance() { return budgetBalance; }
    }
    
    /**
     * Επιστρέφει το προϋπολογισμό μιας χώρας για ένα έτος.
     */
    public InternationalBudget getInternationalBudget(String countryCode, int year) {
        String sql = "SELECT country_code, country_name, year, total_gdp, total_revenue, total_expenses, budget_balance " +
                    "FROM international_budgets " +
                    "WHERE country_code = ? AND year = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, countryCode);
            pstmt.setInt(2, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Handle NULL values properly - use getObject and check for null
                    Object gdpObj = rs.getObject("total_gdp");
                    Object revenueObj = rs.getObject("total_revenue");
                    Object expensesObj = rs.getObject("total_expenses");
                    Object balanceObj = rs.getObject("budget_balance");
                    
                    double totalGDP = (gdpObj != null) ? rs.getDouble("total_gdp") : 0.0;
                    double totalRevenue = (revenueObj != null) ? rs.getDouble("total_revenue") : 0.0;
                    double totalExpenses = (expensesObj != null) ? rs.getDouble("total_expenses") : 0.0;
                    double budgetBalance = (balanceObj != null) ? rs.getDouble("budget_balance") : 0.0;
                    
                    // Debug logging
                    System.out.println("DEBUG getInternationalBudget: " + countryCode + " (" + year + ")");
                    System.out.println("  GDP: " + totalGDP + (gdpObj == null ? " (NULL)" : ""));
                    System.out.println("  Revenue: " + totalRevenue + (revenueObj == null ? " (NULL)" : ""));
                    System.out.println("  Expenses: " + totalExpenses + (expensesObj == null ? " (NULL)" : ""));
                    System.out.println("  Balance: " + budgetBalance + (balanceObj == null ? " (NULL)" : ""));
                    
                    return new InternationalBudget(
                        rs.getString("country_code"),
                        rs.getString("country_name"),
                        rs.getInt("year"),
                        totalGDP,
                        totalRevenue,
                        totalExpenses,
                        budgetBalance
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading international budget: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Επιστρέφει όλους τους προϋπολογισμούς για ένα έτος (ή όλα τα έτη αν year = 0).
     */
    public List<InternationalBudget> getAllInternationalBudgets(int year) {
        List<InternationalBudget> budgets = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT country_code, country_name, year, total_gdp, total_revenue, total_expenses, budget_balance " +
            "FROM international_budgets WHERE 1=1"
        );
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (year > 0) {
                sql.append(" AND year = ?");
            }
            sql.append(" ORDER BY country_name, year");
            
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            if (year > 0) {
                pstmt.setInt(1, year);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    budgets.add(new InternationalBudget(
                        rs.getString("country_code"),
                        rs.getString("country_name"),
                        rs.getInt("year"),
                        rs.getDouble("total_gdp"),
                        rs.getDouble("total_revenue"),
                        rs.getDouble("total_expenses"),
                        rs.getDouble("budget_balance")
                    ));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading all international budgets: " + e.getMessage());
            e.printStackTrace();
        }
        
        return budgets;
    }
    
    /**
     * Επιστρέφει τις χώρες που έχουν budget data για ένα συγκεκριμένο έτος.
     * Η Ελλάδα (GRC) είναι πάντα διαθέσιμη αν υπάρχουν δεδομένα στον budget_summary.
     */
    public List<String> getAvailableCountriesForYear(int year) {
        List<String> countries = new ArrayList<>();
        
        // Ελλάδα είναι πάντα διαθέσιμη αν υπάρχουν δεδομένα στον budget_summary
        if (hasGreekBudgetData(year)) {
            countries.add("GRC");
        }
        
        // Προσθήκη άλλων χωρών από international_budgets
        String sql = "SELECT DISTINCT country_code FROM international_budgets WHERE year = ? ORDER BY country_code";
        
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String countryCode = rs.getString("country_code");
                    // Προσθέτουμε μόνο αν δεν είναι ήδη η Ελλάδα
                    if (!"GRC".equals(countryCode)) {
                        countries.add(countryCode);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading available countries for year: " + e.getMessage());
            e.printStackTrace();
        }
        
        return countries;
    }
    
    /**
     * Ελέγχει αν υπάρχουν ελληνικά δεδομένα προϋπολογισμού για ένα έτος.
     */
    private boolean hasGreekBudgetData(int year) {
        String sql = "SELECT COUNT(*) FROM budget_summary_" + year;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            // Table might not exist for this year
            return false;
        }
        
        return false;
    }
    
    /**
     * Ελέγχει αν μια χώρα έχει budget data για ένα συγκεκριμένο έτος.
     * Η Ελλάδα (GRC) ελέγχεται από τους πίνακες budget_summary.
     */
    public boolean hasBudgetDataForYear(String countryCode, int year) {
        // Ελλάδα - ελέγχουμε τους πίνακες budget_summary
        if ("GRC".equals(countryCode)) {
            return hasGreekBudgetData(year);
        }
        
        // Άλλες χώρες - ελέγχουμε τον πίνακα international_budgets
        String sql = "SELECT COUNT(*) FROM international_budgets WHERE country_code = ? AND year = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, countryCode);
            pstmt.setInt(2, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking budget data availability: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Επιστρέφει το ελληνικό προϋπολογισμό για ένα έτος ως InternationalBudget.
     * Χρησιμοποιείται όταν η Ελλάδα επιλέγεται στη σύγκριση.
     */
    public InternationalBudget getGreekBudget(int year) {
        try {
            double totalRevenue = getTotalRevenues(year);
            double totalExpenses = getTotalExpenses(year);
            double budgetBalance = getBalance(year);
            
            // Υπολογίζουμε το GDP από τα ποσοστά αν υπάρχουν
            double totalGDP = 0;
            double revenuePct = getInternationalIndicator("GRC", year, "revenue_pct_gdp");
            if (revenuePct > 0) {
                totalGDP = (totalRevenue / revenuePct) * 100.0;
            } else {
                // Fallback: χρησιμοποιούμε το gdp_total αν υπάρχει
                totalGDP = getInternationalIndicator("GRC", year, "gdp_total");
            }
            
            return new InternationalBudget("GRC", "Greece", year, totalGDP, totalRevenue, totalExpenses, budgetBalance);
        } catch (Exception e) {
            System.err.println("Error getting Greek budget: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
