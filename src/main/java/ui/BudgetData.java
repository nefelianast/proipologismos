package ui;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.math.BigDecimal;

// βοηθητική κλάση για τη φόρτωση, διαχείριση & πρόσβαση στα budget δεδομένα 
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
}
