package ui;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ComparisonService {
    
    // κρατά δεδομένα για σύγκριση μεταξύ 2 ετών σε μια συγκεκριμένη κατηγορία (πχ έσοδα)
    public static class ComparisonData {
        private final String categoryName;
        private final long year1Value;
        private final long year2Value;
        private final long difference;
        private final double percentageChange;
        
    // δέχεται κατηγορία και τιμές για κάθε έτος και υπολογίζει τα υπόλοιπα
    public ComparisonData(String categoryName, long year1Value, long year2Value) {
            this.categoryName = categoryName;
            this.year1Value = year1Value;
            this.year2Value = year2Value;
            // υπολογισμός διαφοράς 
            this.difference = year2Value - year1Value;            
            // περίπτωση διαίρεσης με 0 (δεν μπο να υπολογίσουμε ποσοστό)
            if (year1Value == 0) {
                // Αν το year2 > 0, τότε πήγαμε από 0 σε κάτι - δεν μπορούμε να υπολογίσουμε ποσοστό
                // Χρησιμοποιούμε Double.NaN (Not a Number) για να δείξουμε ότι είναι undefined
                // Αν και τα δύο είναι 0, τότε δεν υπάρχει αλλαγή (0%)
                this.percentageChange = year2Value > 0 ? Double.NaN : 0.0;
            } else {
                // Κανονική περίπτωση: υπολογισμός ποσοστιαίας αλλαγής
                this.percentageChange = (double) difference / year1Value * 100.0;
            }
        }
        
        // Simple getters to access the data
        public String getCategoryName() { return categoryName; }
        public long getYear1Value() { return year1Value; }
        public long getYear2Value() { return year2Value; }
        public long getDifference() { return difference; }
        public double getPercentageChange() { return percentageChange; }
        
        // Helper method to get percentage as a formatted string - handles NaN case
        public String getPercentageChangeAsString() {
            if (Double.isNaN(percentageChange)) {
                return "N/A";
            }
            return String.format("%.2f%%", percentageChange);
        }
    }
    
    // Container for all comparison results - organized by category type
    // Each map holds comparisons for different categories (like "Φόροι", "Συνολικά Έσοδα", etc.)
    public static class ComparisonResults {
        private final Map<String, ComparisonData> revenues = new HashMap<>();
        private final Map<String, ComparisonData> expenses = new HashMap<>();
        private final Map<String, ComparisonData> administrations = new HashMap<>();
        private final Map<String, ComparisonData> ministries = new HashMap<>();
        private final Map<String, ComparisonData> budgetSummary = new HashMap<>();
        
        // Getters to access each category of comparisons
        public Map<String, ComparisonData> getRevenues() { return revenues; }
        public Map<String, ComparisonData> getExpenses() { return expenses; }
        public Map<String, ComparisonData> getAdministrations() { return administrations; }
        public Map<String, ComparisonData> getMinistries() { return ministries; }
        public Map<String, ComparisonData> getBudgetSummary() { return budgetSummary; }
    }
    
    // Main method - compares two years and returns all the data
    // This is the entry point - call this with two years and get back all comparison data
    public ComparisonResults compareYears(int year1, int year2) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();      
        Statement stmt = connection.createStatement();
        ComparisonResults results = new ComparisonResults();
        
        try {
            // Check if the tables exist first - can't compare if data doesn't exist
            if (!tableExists(connection, "revenue_" + year1)) {
                throw new SQLException("Ο πίνακας revenue_" + year1 + " δεν υπάρχει στη βάση δεδομένων. " +
                    "Παρακαλώ βεβαιωθείτε ότι τα δεδομένα για το έτος " + year1 + " έχουν εισαχθεί.");
            }
            if (!tableExists(connection, "revenue_" + year2)) {
                throw new SQLException("Ο πίνακας revenue_" + year2 + " δεν υπάρχει στη βάση δεδομένων. " +
                    "Παρακαλώ βεβαιωθείτε ότι τα δεδομένα για το έτος " + year2 + " έχουν εισαχθεί.");
            }
            
            // Compare everything - revenues, expenses, administrations, ministries, and summary
            compareRevenues(stmt, year1, year2, results);
            compareExpenses(stmt, year1, year2, results);
            compareAdministrations(stmt, year1, year2, results);
            compareMinistries(stmt, year1, year2, results);
            compareBudgetSummary(stmt, year1, year2, results);

        } finally {
            // Always close resources, even if something goes wrong
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return results;
    }
    
    // Quick check if a table exists in the database
    // Uses database metadata to see if the table is there
    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet tables = meta.getTables(null, null, tableName, null);
            boolean exists = tables.next(); // If there's a row, table exists
            tables.close();
            return exists;
        } catch (SQLException e) {
            // If something goes wrong, assume table doesn't exist
            return false;
        }
    }
    
    // Compare revenue data between two years
    // Gets all revenue categories from both years and creates comparison data
    private void compareRevenues(Statement stmt, int year1, int year2, ComparisonResults results) throws SQLException {
        // Query both years' revenue tables
        String sql1 = "SELECT * FROM revenue_" + year1;
        String sql2 = "SELECT * FROM revenue_" + year2;
        
        ResultSet rs1 = stmt.executeQuery(sql1);
        ResultSet rs2 = stmt.executeQuery(sql2);
        
        // Initialize all the variables - we'll read from the database into these
        long total_revenue1 = 0, total_revenue2 = 0;
        long taxes1 = 0, taxes2 = 0;
        long social_contributions1 = 0, social_contributions2 = 0;       
        long transfers1 = 0, transfers2 = 0;
        long sales_of_goods_and_services1 = 0, sales_of_goods_and_services2 = 0;
        long other_current_revenue1 = 0, other_current_revenue2 = 0;     
        long fixed_assets1 = 0, fixed_assets2 = 0;
        long debt_securities1 = 0, debt_securities2 = 0;
        long loans1 = 0, loans2 = 0;
        long equity_securities_and_fund_shares1 = 0, equity_securities_and_fund_shares2 = 0;
        long currency_and_deposit_liabilities1 = 0, currency_and_deposit_liabilities2 = 0;
        long debt_securities_liabilities1 = 0, debt_securities_liabilities2 = 0;
        long loans_liabilities1 = 0, loans_liabilities2 = 0;
        long financial_derivatives1 = 0, financial_derivatives2 = 0;     
        
        // Read data from year1
        if (rs1.next()) {
            total_revenue1 = rs1.getLong("total_revenue");
            taxes1 = rs1.getLong("taxes");
            social_contributions1 = rs1.getLong("social_contributions"); 
            transfers1 = rs1.getLong("transfers");
            sales_of_goods_and_services1 = rs1.getLong("sales_of_goods_and_services");
            other_current_revenue1 = rs1.getLong("other_current_revenue");
            fixed_assets1 = rs1.getLong("fixed_assets");
            debt_securities1 = rs1.getLong("debt_securities");
            // Loans column only exists for 2025 and 2026 - check before reading
            if (year1 == 2025 || year1 == 2026) {
                loans1 = rs1.getLong("loans");
            }
            equity_securities_and_fund_shares1 = rs1.getLong("equity_securities_and_fund_shares");
            currency_and_deposit_liabilities1 = rs1.getLong("currency_and_deposit_liabilities");
            debt_securities_liabilities1 = rs1.getLong("debt_securities_liabilities");
            loans_liabilities1 = rs1.getLong("loans_liabilities");       
            financial_derivatives1 = rs1.getLong("financial_derivatives");
        }
        
        // Read data from year2 (same structure)
        if (rs2.next()) {
            total_revenue2 = rs2.getLong("total_revenue");
            taxes2 = rs2.getLong("taxes");
            social_contributions2 = rs2.getLong("social_contributions"); 
            transfers2 = rs2.getLong("transfers");
            sales_of_goods_and_services2 = rs2.getLong("sales_of_goods_and_services");
            other_current_revenue2 = rs2.getLong("other_current_revenue");
            fixed_assets2 = rs2.getLong("fixed_assets");
            debt_securities2 = rs2.getLong("debt_securities");
            if (year2 == 2025 || year2 == 2026) {
                loans2 = rs2.getLong("loans");
            }
            equity_securities_and_fund_shares2 = rs2.getLong("equity_securities_and_fund_shares");
            currency_and_deposit_liabilities2 = rs2.getLong("currency_and_deposit_liabilities");
            debt_securities_liabilities2 = rs2.getLong("debt_securities_liabilities");
            loans_liabilities2 = rs2.getLong("loans_liabilities");       
            financial_derivatives2 = rs2.getLong("financial_derivatives");
        }
        
        rs1.close();
        rs2.close();
        
        // Store all the comparison results
        results.getRevenues().put("Συνολικά Έσοδα", new ComparisonData("Συνολικά Έσοδα", total_revenue1, total_revenue2));
        results.getRevenues().put("Φόροι", new ComparisonData("Φόροι", taxes1, taxes2));
        results.getRevenues().put("Κοινωνικές Εισφορές", new ComparisonData("Κοινωνικές Εισφορές", social_contributions1, social_contributions2)); 
        results.getRevenues().put("Μεταβιβάσεις", new ComparisonData("Μεταβιβάσεις", transfers1, transfers2));
        results.getRevenues().put("Πωλήσεις Αγαθών & Υπηρεσιών", new ComparisonData("Πωλήσεις Αγαθών & Υπηρεσιών", sales_of_goods_and_services1, sales_of_goods_and_services2));
        results.getRevenues().put("Άλλα Τρέχοντα Έσοδα", new ComparisonData("Άλλα Τρέχοντα Έσοδα", other_current_revenue1, other_current_revenue2));
        results.getRevenues().put("Πάγια", new ComparisonData("Πάγια", fixed_assets1, fixed_assets2));
        results.getRevenues().put("Ομόλογα", new ComparisonData("Ομόλογα", debt_securities1, debt_securities2));
        // Only add loans if at least one year has it
        if (year1 == 2025 || year1 == 2026 || year2 == 2025 || year2 == 2026) {
            results.getRevenues().put("Δάνεια", new ComparisonData("Δάνεια", loans1, loans2));
        }
        results.getRevenues().put("Μετοχές & Μερίδια", new ComparisonData("Μετοχές & Μερίδια", equity_securities_and_fund_shares1, equity_securities_and_fund_shares2));
        results.getRevenues().put("Νομισματικά & Καταθέσεις", new ComparisonData("Νομισματικά & Καταθέσεις", currency_and_deposit_liabilities1, currency_and_deposit_liabilities2));
        results.getRevenues().put("Ομόλογα Υποχρεώσεις", new ComparisonData("Ομόλογα Υποχρεώσεις", debt_securities_liabilities1, debt_securities_liabilities2));
        results.getRevenues().put("Δάνεια Υποχρεώσεις", new ComparisonData("Δάνεια Υποχρεώσεις", loans_liabilities1, loans_liabilities2));
        results.getRevenues().put("Χρηματοοικονομικά Παράγωγα", new ComparisonData("Χρηματοοικονομικά Παράγωγα", financial_derivatives1, financial_derivatives2));
    }
    
    // Compare expense data between two years
    // Similar to compareRevenues but for expenses instead
    private void compareExpenses(Statement stmt, int year1, int year2, ComparisonResults results) throws SQLException {
        String sql1 = "SELECT * FROM expenses_" + year1;
        String sql2 = "SELECT * FROM expenses_" + year2;
        
        ResultSet rs1 = stmt.executeQuery(sql1);
        ResultSet rs2 = stmt.executeQuery(sql2);
        
        // Initialize all expense variables
        long total_expenses1 = 0, total_expenses2 = 0;
        long employee_benefits1 = 0, employee_benefits2 = 0;
        long social_benefits1 = 0, social_benefits2 = 0;
        long transfers1 = 0, transfers2 = 0;
        long purchases_of_goods_and_services1 = 0, purchases_of_goods_and_services2 = 0;
        long subsidies1 = 0, subsidies2 = 0;
        long interest1 = 0, interest2 = 0;
        long other_expenditures1 = 0, other_expenditures2 = 0;
        long loans1 = 0, loans2 = 0;
        long appropriations1 = 0, appropriations2 = 0;
        long fixed_assets1 = 0, fixed_assets2 = 0;
        long valuables1 = 0, valuables2 = 0;
        long equity_securities_and_fund_shares1 = 0, equity_securities_and_fund_shares2 = 0;
        long debt_securities_liabilities1 = 0, debt_securities_liabilities2 = 0;
        long loans_liabilities1 = 0, loans_liabilities2 = 0;
        
        // Read expense data from year1
        if (rs1.next()) {
            total_expenses1 = rs1.getLong("total_expenses");
            employee_benefits1 = rs1.getLong("employee_benefits");       
            social_benefits1 = rs1.getLong("social_benefits");
            transfers1 = rs1.getLong("transfers");
            purchases_of_goods_and_services1 = rs1.getLong("purchases_of_goods_and_services");
            subsidies1 = rs1.getLong("subsidies");
            interest1 = rs1.getLong("interest");
            other_expenditures1 = rs1.getLong("other_expenditures");     
            loans1 = rs1.getLong("loans");
            appropriations1 = rs1.getLong("appropriations");
            fixed_assets1 = rs1.getLong("fixed_assets");
            valuables1 = rs1.getLong("valuables");
            equity_securities_and_fund_shares1 = rs1.getLong("equity_securities_and_fund_shares");
            debt_securities_liabilities1 = rs1.getLong("debt_securities_liabilities");
            loans_liabilities1 = rs1.getLong("loans_liabilities");       
        }
        
        // Read expense data from year2
        if (rs2.next()) {
            total_expenses2 = rs2.getLong("total_expenses");
            employee_benefits2 = rs2.getLong("employee_benefits");       
            social_benefits2 = rs2.getLong("social_benefits");
            transfers2 = rs2.getLong("transfers");
            purchases_of_goods_and_services2 = rs2.getLong("purchases_of_goods_and_services");
            subsidies2 = rs2.getLong("subsidies");
            interest2 = rs2.getLong("interest");
            other_expenditures2 = rs2.getLong("other_expenditures");     
            loans2 = rs2.getLong("loans");
            appropriations2 = rs2.getLong("appropriations");
            fixed_assets2 = rs2.getLong("fixed_assets");
            valuables2 = rs2.getLong("valuables");
            equity_securities_and_fund_shares2 = rs2.getLong("equity_securities_and_fund_shares");
            debt_securities_liabilities2 = rs2.getLong("debt_securities_liabilities");
            loans_liabilities2 = rs2.getLong("loans_liabilities");       
        }
        
        rs1.close();
        rs2.close();
        
        results.getExpenses().put("Συνολικές Δαπάνες", new ComparisonData("Συνολικές Δαπάνες", total_expenses1, total_expenses2));
        results.getExpenses().put("Αποδοχές Εργαζομένων", new ComparisonData("Αποδοχές Εργαζομένων", employee_benefits1, employee_benefits2));     
        results.getExpenses().put("Κοινωνικές Παροχές", new ComparisonData("Κοινωνικές Παροχές", social_benefits1, social_benefits2));
        results.getExpenses().put("Μεταβιβάσεις", new ComparisonData("Μεταβιβάσεις", transfers1, transfers2));
        results.getExpenses().put("Αγορές Αγαθών & Υπηρεσιών", new ComparisonData("Αγορές Αγαθών & Υπηρεσιών", purchases_of_goods_and_services1, purchases_of_goods_and_services2));
        results.getExpenses().put("Επιδοτήσεις", new ComparisonData("Επιδοτήσεις", subsidies1, subsidies2));
        results.getExpenses().put("Τόκοι", new ComparisonData("Τόκοι", interest1, interest2));
        results.getExpenses().put("Άλλες Δαπάνες", new ComparisonData("Άλλες Δαπάνες", other_expenditures1, other_expenditures2));
        results.getExpenses().put("Δάνεια", new ComparisonData("Δάνεια", loans1, loans2));
        results.getExpenses().put("Πιστώσεις", new ComparisonData("Πιστώσεις", appropriations1, appropriations2));
        results.getExpenses().put("Πάγια", new ComparisonData("Πάγια", fixed_assets1, fixed_assets2));
        results.getExpenses().put("Αξιόγραφα", new ComparisonData("Αξιόγραφα", valuables1, valuables2));
        results.getExpenses().put("Μετοχές & Μερίδια", new ComparisonData("Μετοχές & Μερίδια", equity_securities_and_fund_shares1, equity_securities_and_fund_shares2));
        results.getExpenses().put("Ομόλογα Υποχρεώσεις", new ComparisonData("Ομόλογα Υποχρεώσεις", debt_securities_liabilities1, debt_securities_liabilities2));
        results.getExpenses().put("Δάνεια Υποχρεώσεις", new ComparisonData("Δάνεια Υποχρεώσεις", loans_liabilities1, loans_liabilities2));
    }
    
    // Compare decentralized administrations data
    // Compares the different regional administrations (like Attica, Crete, etc.)
    private void compareAdministrations(Statement stmt, int year1, int year2, ComparisonResults results) throws SQLException {
        String sql1 = "SELECT * FROM decentralized_administrations_" + year1;
        String sql2 = "SELECT * FROM decentralized_administrations_" + year2;
        
        ResultSet rs1 = stmt.executeQuery(sql1);
        ResultSet rs2 = stmt.executeQuery(sql2);
        
        // Variables for each region
        long total_da1 = 0, total_da2 = 0;
        long attica1 = 0, attica2 = 0;
        long thessaly1 = 0, thessaly2 = 0;
        long epirus1 = 0, epirus2 = 0;
        long peloponnese1 = 0, peloponnese2 = 0;
        long aegean1 = 0, aegean2 = 0;
        long crete1 = 0, crete2 = 0;
        long macedonia_thrace1 = 0, macedonia_thrace2 = 0;
        
        // Read administration data from year1
        if (rs1.next()) {
            total_da1 = rs1.getLong("total_da");
            attica1 = rs1.getLong("decentralized_administration_of_attica");
            thessaly1 = rs1.getLong("decentralized_administration_of_thessaly_central_greece");
            epirus1 = rs1.getLong("decentralized_administration_of_epirus_western_macedonia");
            peloponnese1 = rs1.getLong("decentralized_administration_of_peloponnese_western_greece_and_ionian");
            aegean1 = rs1.getLong("decentralized_administration_of_aegean");
            crete1 = rs1.getLong("decentralized_administration_of_crete");
            macedonia_thrace1 = rs1.getLong("decentralized_administration_of_macedonia_thrace");
        }
        
        // Read administration data from year2
        if (rs2.next()) {
            total_da2 = rs2.getLong("total_da");
            attica2 = rs2.getLong("decentralized_administration_of_attica");
            thessaly2 = rs2.getLong("decentralized_administration_of_thessaly_central_greece");
            epirus2 = rs2.getLong("decentralized_administration_of_epirus_western_macedonia");
            peloponnese2 = rs2.getLong("decentralized_administration_of_peloponnese_western_greece_and_ionian");
            aegean2 = rs2.getLong("decentralized_administration_of_aegean");
            crete2 = rs2.getLong("decentralized_administration_of_crete");
            macedonia_thrace2 = rs2.getLong("decentralized_administration_of_macedonia_thrace");
        }
        
        rs1.close();
        rs2.close();
        
        results.getAdministrations().put("Συνολικά", new ComparisonData("Συνολικά", total_da1, total_da2));
        results.getAdministrations().put("Αττική", new ComparisonData("Αττική", attica1, attica2));
        results.getAdministrations().put("Θεσσαλία & Στερεά Ελλάδα", new ComparisonData("Θεσσαλία & Στερεά Ελλάδα", thessaly1, thessaly2));        
        results.getAdministrations().put("Ήπειρος & Δυτική Μακεδονία", new ComparisonData("Ήπειρος & Δυτική Μακεδονία", epirus1, epirus2));        
        results.getAdministrations().put("Πελοπόννησος, Δυτική Ελλάδα & Ιόνιο", new ComparisonData("Πελοπόννησος, Δυτική Ελλάδα & Ιόνιο", peloponnese1, peloponnese2));
        results.getAdministrations().put("Αιγαίο", new ComparisonData("Αιγαίο", aegean1, aegean2));
        results.getAdministrations().put("Κρήτη", new ComparisonData("Κρήτη", crete1, crete2));
        results.getAdministrations().put("Μακεδονία & Θράκη", new ComparisonData("Μακεδονία & Θράκη", macedonia_thrace1, macedonia_thrace2));      
    }
    
    // Compare ministries data - this one's a bit more complex because columns can differ
    private void compareMinistries(Statement stmt, int year1, int year2, ComparisonResults results) throws SQLException {
        String sql1 = "SELECT * FROM ministries_" + year1;
        String sql2 = "SELECT * FROM ministries_" + year2;
        
        ResultSet rs1 = stmt.executeQuery(sql1);
        ResultSet rs2 = stmt.executeQuery(sql2);
        
        // Get column names to check what's available
        java.sql.ResultSetMetaData meta1 = rs1.getMetaData();
        java.sql.ResultSetMetaData meta2 = rs2.getMetaData();
        
        // Build sets of column names to check what's available in each table
        Set<String> columns1 = new HashSet<>();
        Set<String> columns2 = new HashSet<>();
        for (int i = 1; i <= meta1.getColumnCount(); i++) {
            columns1.add(meta1.getColumnName(i).toLowerCase());
        }
        for (int i = 1; i <= meta2.getColumnCount(); i++) {
            columns2.add(meta2.getColumnName(i).toLowerCase());
        }
        
        // Maps to store ministry data with Greek names as keys
        Map<String, Long> ministries1 = new HashMap<>();
        Map<String, Long> ministries2 = new HashMap<>();
        
        // Read all ministries from year1 and store with Greek names
        if (rs1.next()) {
            ministries1.put("Συνολικά", rs1.getLong("total_ministries"));
            ministries1.put("Προεδρία της Δημοκρατίας", rs1.getLong("presidency_of_the_republic"));
            ministries1.put("Βουλή των Ελλήνων", rs1.getLong("hellenic_parliament"));
            ministries1.put("Προεδρία της Κυβέρνησης", rs1.getLong("presidency_of_the_government"));
            ministries1.put("Εσωτερικών", rs1.getLong("ministry_of_interior"));
            ministries1.put("Εξωτερικών", rs1.getLong("ministry_of_foreign_affairs"));
            ministries1.put("Εθνικής Άμυνας", rs1.getLong("ministry_of_national_defence"));
            ministries1.put("Υγείας", rs1.getLong("ministry_of_health"));
            ministries1.put("Δικαιοσύνης", rs1.getLong("ministry_of_justice"));
            ministries1.put("Εκπαίδευσης, Θρησκευμάτων & Αθλητισμού", rs1.getLong("ministry_of_education_religious_affairs_and_sports"));
            ministries1.put("Πολιτισμού", rs1.getLong("ministry_of_culture"));
            ministries1.put("Εθνικής Οικονομίας & Οικονομικών", rs1.getLong("ministry_of_national_economy_and_finance"));
            ministries1.put("Αγροτικής Ανάπτυξης & Τροφίμων", rs1.getLong("ministry_of_agricultural_development_and_food"));
            ministries1.put("Περιβάλλοντος & Ενέργειας", rs1.getLong("ministry_of_environment_and_energy"));
            ministries1.put("Εργασίας & Κοινωνικής Ασφάλισης", rs1.getLong("ministry_of_labor_and_social_security"));
            // This ministry might not exist in older years - check first
            if (columns1.contains("ministry_of_social_cohesion_and_family")) {
                ministries1.put("Κοινωνικής Συνοχής & Οικογένειας", rs1.getLong("ministry_of_social_cohesion_and_family"));
            } else {
                ministries1.put("Κοινωνικής Συνοχής & Οικογένειας", 0L); 
            }
            ministries1.put("Ανάπτυξης", rs1.getLong("ministry_of_development"));
            ministries1.put("Υποδομών & Μεταφορών", rs1.getLong("ministry_of_infrastructure_and_transport"));
            ministries1.put("Ναυτιλίας & Νησιωτικής Πολιτικής", rs1.getLong("ministry_of_maritime_affairs_and_insular_policy"));
            ministries1.put("Τουρισμού", rs1.getLong("ministry_of_tourism"));
            ministries1.put("Ψηφιακής Διακυβέρνησης", rs1.getLong("ministry_of_digital_governance"));
            ministries1.put("Μεταναστευτικής Πολιτικής & Ασύλου", rs1.getLong("ministry_of_migration_and_asylum"));
            ministries1.put("Προστασίας του Πολίτη", rs1.getLong("ministry_of_citizen_protection"));
            ministries1.put("Κλιματικής Κρίσης & Πολιτικής Προστασίας", rs1.getLong("ministry_of_climate_crisis_and_civil_protection"));
        }
        
        // Read all ministries from year2 (same structure)
        if (rs2.next()) {
            ministries2.put("Συνολικά", rs2.getLong("total_ministries"));
            ministries2.put("Προεδρία της Δημοκρατίας", rs2.getLong("presidency_of_the_republic"));
            ministries2.put("Βουλή των Ελλήνων", rs2.getLong("hellenic_parliament"));
            ministries2.put("Προεδρία της Κυβέρνησης", rs2.getLong("presidency_of_the_government"));
            ministries2.put("Εσωτερικών", rs2.getLong("ministry_of_interior"));
            ministries2.put("Εξωτερικών", rs2.getLong("ministry_of_foreign_affairs"));
            ministries2.put("Εθνικής Άμυνας", rs2.getLong("ministry_of_national_defence"));
            ministries2.put("Υγείας", rs2.getLong("ministry_of_health"));
            ministries2.put("Δικαιοσύνης", rs2.getLong("ministry_of_justice"));
            ministries2.put("Εκπαίδευσης, Θρησκευμάτων & Αθλητισμού", rs2.getLong("ministry_of_education_religious_affairs_and_sports"));
            ministries2.put("Πολιτισμού", rs2.getLong("ministry_of_culture"));
            ministries2.put("Εθνικής Οικονομίας & Οικονομικών", rs2.getLong("ministry_of_national_economy_and_finance"));
            ministries2.put("Αγροτικής Ανάπτυξης & Τροφίμων", rs2.getLong("ministry_of_agricultural_development_and_food"));
            ministries2.put("Περιβάλλοντος & Ενέργειας", rs2.getLong("ministry_of_environment_and_energy"));
            ministries2.put("Εργασίας & Κοινωνικής Ασφάλισης", rs2.getLong("ministry_of_labor_and_social_security"));
            // Check if this ministry exists in year2's table
            if (columns2.contains("ministry_of_social_cohesion_and_family")) {
                ministries2.put("Κοινωνικής Συνοχής & Οικογένειας", rs2.getLong("ministry_of_social_cohesion_and_family"));
            } else {
                ministries2.put("Κοινωνικής Συνοχής & Οικογένειας", 0L); 
            }
            ministries2.put("Ανάπτυξης", rs2.getLong("ministry_of_development"));
            ministries2.put("Υποδομών & Μεταφορών", rs2.getLong("ministry_of_infrastructure_and_transport"));
            ministries2.put("Ναυτιλίας & Νησιωτικής Πολιτικής", rs2.getLong("ministry_of_maritime_affairs_and_insular_policy"));
            ministries2.put("Τουρισμού", rs2.getLong("ministry_of_tourism"));
            ministries2.put("Ψηφιακής Διακυβέρνησης", rs2.getLong("ministry_of_digital_governance"));
            ministries2.put("Μεταναστευτικής Πολιτικής & Ασύλου", rs2.getLong("ministry_of_migration_and_asylum"));
            ministries2.put("Προστασίας του Πολίτη", rs2.getLong("ministry_of_citizen_protection"));
            ministries2.put("Κλιματικής Κρίσης & Πολιτικής Προστασίας", rs2.getLong("ministry_of_climate_crisis_and_civil_protection"));
        }
        
        rs1.close();
        rs2.close();
        
        // Match up all the ministries and create comparison data
        // Loop through all ministries from year1 and compare with year2
        for (String key : ministries1.keySet()) {
            long val1 = ministries1.get(key);
            long val2 = ministries2.getOrDefault(key, 0L); // Use 0 if ministry doesn't exist in year2
            results.getMinistries().put(key, new ComparisonData(key, val1, val2));
        }
    }
    
    // Compare the overall budget summary
    // Gets the high-level totals (revenues, expenses, result, etc.) for both years
    private void compareBudgetSummary(Statement stmt, int year1, int year2, ComparisonResults results) throws SQLException {
        String sql1 = "SELECT * FROM budget_summary_" + year1;
        String sql2 = "SELECT * FROM budget_summary_" + year2;
        
        ResultSet rs1 = stmt.executeQuery(sql1);
        ResultSet rs2 = stmt.executeQuery(sql2);
        
        // Variables for the summary totals
        long budget_result1 = 0, budget_result2 = 0;
        long total_revenue1 = 0, total_revenue2 = 0;
        long total_expenses1 = 0, total_expenses2 = 0;
        long total_ministries1 = 0, total_ministries2 = 0;
        long total_da1 = 0, total_da2 = 0;
        
        // Read summary data from year1
        if (rs1.next()) {
            budget_result1 = rs1.getLong("budget_result");
            total_revenue1 = rs1.getLong("total_revenue");
            total_expenses1 = rs1.getLong("total_expenses");
            total_ministries1 = rs1.getLong("total_ministries");
            total_da1 = rs1.getLong("total_da");
        }
        
        // Read summary data from year2
        if (rs2.next()) {
            budget_result2 = rs2.getLong("budget_result");
            total_revenue2 = rs2.getLong("total_revenue");
            total_expenses2 = rs2.getLong("total_expenses");
            total_ministries2 = rs2.getLong("total_ministries");
            total_da2 = rs2.getLong("total_da");
        }
        
        rs1.close();
        rs2.close();
        
        // Store all the summary comparisons
        results.getBudgetSummary().put("Αποτέλεσμα Προϋπολογισμού", new ComparisonData("Αποτέλεσμα Προϋπολογισμού", budget_result1, budget_result2));
        results.getBudgetSummary().put("Συνολικά Έσοδα", new ComparisonData("Συνολικά Έσοδα", total_revenue1, total_revenue2));
        results.getBudgetSummary().put("Συνολικές Δαπάνες", new ComparisonData("Συνολικές Δαπάνες", total_expenses1, total_expenses2));
        results.getBudgetSummary().put("Συνολικά Υπουργεία", new ComparisonData("Συνολικά Υπουργεία", total_ministries1, total_ministries2));      
        results.getBudgetSummary().put("Συνολικές Αποκεντρωμένες Διοικήσεις", new ComparisonData("Συνολικές Αποκεντρωμένες Διοικήσεις", total_da1, total_da2));
    }
}
