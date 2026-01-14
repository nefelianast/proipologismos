package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

// Controller για τη σελίδα επεξεργασίας προϋπολογισμού
public class BudgetEditController implements Initializable {
    
    // Data model για μια γραμμή στον πίνακα
    public static class BudgetItem {
        private final String categoryName;  // Ελληνικό όνομα
        private final String columnName;    // Όνομα στήλης στη βάση
        private double amount;
        
        public BudgetItem(String categoryName, String columnName, double amount) {
            this.categoryName = categoryName;
            this.columnName = columnName;
            this.amount = amount;
        }
        
        public String getCategoryName() { return categoryName; }
        public String getColumnName() { return columnName; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
    }
    
    // Revenue categories for 2026
    private static final String[][] REVENUE_CATEGORIES = {
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
    
    // Expense categories for 2026
    private static final String[][] EXPENSE_CATEGORIES = {
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
        {"loans_liabilities", "Δάνεια (Υποχρεώσεις)"},
        {"financial_derivatives", "Χρηματοοικονομικά Παράγωγα"}
    };
    
    // Ministry categories
    private static final String[][] MINISTRY_CATEGORIES = {
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
    
    // Administration categories
    private static final String[][] ADMINISTRATION_CATEGORIES = {
        {"decentralized_administration_of_attica", "Αποκεντρωμένη Διοίκηση Αττικής"},
        {"decentralized_administration_of_thessaly_central_greece", "Αποκεντρωμένη Διοίκηση Θεσσαλίας & Στερεάς Ελλάδας"},
        {"decentralized_administration_of_epirus_western_macedonia", "Αποκεντρωμένη Διοίκηση Ηπείρου & Δυτικής Μακεδονίας"},
        {"decentralized_administration_of_peloponnese_western_greece_and_ionian", "Αποκεντρωμένη Διοίκηση Πελοποννήσου, Δυτικής Ελλάδας & Ιονίου"},
        {"decentralized_administration_of_aegean", "Αποκεντρωμένη Διοίκηση Αιγαίου"},
        {"decentralized_administration_of_crete", "Αποκεντρωμένη Διοίκηση Κρήτης"},
        {"decentralized_administration_of_macedonia_thrace", "Αποκεντρωμένη Διοίκηση Μακεδονίας & Θράκης"}
    };
    
    // FXML components - single set of tables that update based on selected year
    @FXML private ComboBox<String> yearComboBox;
    @FXML private TabPane categoryTabPane;
    @FXML private Button publishButton;
    @FXML private TextArea commentsTextArea;
    
    @FXML private TableView<BudgetItem> revenueTable;
    @FXML private TableColumn<BudgetItem, String> revenueCategoryColumn;
    @FXML private TableColumn<BudgetItem, Double> revenueAmountColumn;
    
    @FXML private TableView<BudgetItem> expensesTable;
    @FXML private TableColumn<BudgetItem, String> expenseCategoryColumn;
    @FXML private TableColumn<BudgetItem, Double> expenseAmountColumn;
    
    @FXML private TableView<BudgetItem> ministriesTable;
    @FXML private TableColumn<BudgetItem, String> ministryCategoryColumn;
    @FXML private TableColumn<BudgetItem, Double> ministryAmountColumn;
    
    @FXML private TableView<BudgetItem> administrationsTable;
    @FXML private TableColumn<BudgetItem, String> administrationCategoryColumn;
    @FXML private TableColumn<BudgetItem, Double> administrationAmountColumn;
    
    private int currentYear = 2026;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ensureCommentsTableExists();
        setupYearComboBox();
        setupTables();
        loadDataForCurrentYear();
        loadCommentsForCurrentYear();
    }
    
    private void setupYearComboBox() {
        yearComboBox.getItems().addAll("2026", "2027");
        yearComboBox.setValue("2026");
        currentYear = 2026;
        
        yearComboBox.setOnAction(e -> {
            String selectedYear = yearComboBox.getValue();
            if (selectedYear != null) {
                currentYear = Integer.parseInt(selectedYear);
                loadDataForCurrentYear();
                loadCommentsForCurrentYear();
                updatePublishButtonVisibility();
            }
        });
        
        // Initial check for publish button visibility
        updatePublishButtonVisibility();
    }
    
    private void updatePublishButtonVisibility() {
        if (publishButton != null) {
            boolean isPublished = isYearPublished(currentYear);
            publishButton.setVisible(!isPublished);
        }
    }
    
    private boolean isYearPublished(int year) {
        try (java.sql.Connection connection = ui.DatabaseConnection.getConnection();
             java.sql.Statement stmt = connection.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT year FROM published_years WHERE year = " + year)) {
            
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void setupTables() {
        setupRevenueTable(revenueTable, revenueCategoryColumn, revenueAmountColumn);
        setupExpensesTable(expensesTable, expenseCategoryColumn, expenseAmountColumn);
        setupMinistriesTable(ministriesTable, ministryCategoryColumn, ministryAmountColumn);
        setupAdministrationsTable(administrationsTable, administrationCategoryColumn, administrationAmountColumn);
    }
    
    private void loadDataForCurrentYear() {
        loadDataForYear(currentYear);
    }
    
    private void setupRevenueTable(TableView<BudgetItem> table, TableColumn<BudgetItem, String> categoryColumn, TableColumn<BudgetItem, Double> amountColumn) {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        
        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter() {
            @Override
            public String toString(Double value) {
                if (value == null) return "";
                return AmountFormatter.formatCurrency(value).replace("€", "").replace(".", "").replace(",", ".");
            }
            
            @Override
            public Double fromString(String string) {
                try {
                    return Double.parseDouble(string.replace(".", "").replace(",", "."));
                } catch (NumberFormatException e) {
                    return 0.0;
                }
            }
        }));
        
        amountColumn.setOnEditCommit(event -> {
            BudgetItem item = event.getRowValue();
            item.setAmount(event.getNewValue());
        });
        
        table.setEditable(true);
    }
    
    private void setupExpensesTable(TableView<BudgetItem> table, TableColumn<BudgetItem, String> categoryColumn, TableColumn<BudgetItem, Double> amountColumn) {
        setupRevenueTable(table, categoryColumn, amountColumn);
    }
    
    private void setupMinistriesTable(TableView<BudgetItem> table, TableColumn<BudgetItem, String> categoryColumn, TableColumn<BudgetItem, Double> amountColumn) {
        setupRevenueTable(table, categoryColumn, amountColumn);
    }
    
    private void setupAdministrationsTable(TableView<BudgetItem> table, TableColumn<BudgetItem, String> categoryColumn, TableColumn<BudgetItem, Double> amountColumn) {
        setupRevenueTable(table, categoryColumn, amountColumn);
    }
    
    private void loadDataForYear(int year) {
        loadRevenueData(year);
        loadExpensesData(year);
        loadMinistriesData(year);
        loadAdministrationsData(year);
    }
    
    private void loadRevenueData(int year) {
        ObservableList<BudgetItem> items = FXCollections.observableArrayList();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM revenue_" + year)) {
            
            if (rs.next()) {
                for (String[] category : REVENUE_CATEGORIES) {
                    String columnName = category[0];
                    String greekName = category[1];
                    double amount = rs.getBigDecimal(columnName) != null ? rs.getBigDecimal(columnName).doubleValue() : 0.0;
                    items.add(new BudgetItem(greekName, columnName, amount));
                }
            } else {
                // Αν δεν υπάρχουν δεδομένα, δημιούργησε empty rows
                for (String[] category : REVENUE_CATEGORIES) {
                    String columnName = category[0];
                    String greekName = category[1];
                    items.add(new BudgetItem(greekName, columnName, 0.0));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading revenue data for " + year + ": " + e.getMessage());
            e.printStackTrace();
            // Αν υπάρχει σφάλμα, δημιούργησε empty rows
            for (String[] category : REVENUE_CATEGORIES) {
                String columnName = category[0];
                String greekName = category[1];
                items.add(new BudgetItem(greekName, columnName, 0.0));
            }
        }
        
        revenueTable.setItems(items);
    }
    
    private void loadExpensesData(int year) {
        ObservableList<BudgetItem> items = FXCollections.observableArrayList();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM expenses_" + year)) {
            
            if (rs.next()) {
                for (String[] category : EXPENSE_CATEGORIES) {
                    String columnName = category[0];
                    String greekName = category[1];
                    double amount = rs.getBigDecimal(columnName) != null ? rs.getBigDecimal(columnName).doubleValue() : 0.0;
                    items.add(new BudgetItem(greekName, columnName, amount));
                }
            } else {
                // Αν δεν υπάρχουν δεδομένα, δημιούργησε empty rows
                for (String[] category : EXPENSE_CATEGORIES) {
                    String columnName = category[0];
                    String greekName = category[1];
                    items.add(new BudgetItem(greekName, columnName, 0.0));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading expenses data for " + year + ": " + e.getMessage());
            e.printStackTrace();
            // Αν υπάρχει σφάλμα, δημιούργησε empty rows
            for (String[] category : EXPENSE_CATEGORIES) {
                String columnName = category[0];
                String greekName = category[1];
                items.add(new BudgetItem(greekName, columnName, 0.0));
            }
        }
        
        expensesTable.setItems(items);
    }
    
    private void loadMinistriesData(int year) {
        ObservableList<BudgetItem> items = FXCollections.observableArrayList();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM ministries_" + year)) {
            
            if (rs.next()) {
                for (String[] category : MINISTRY_CATEGORIES) {
                    String columnName = category[0];
                    String greekName = category[1];
                    double amount = rs.getBigDecimal(columnName) != null ? rs.getBigDecimal(columnName).doubleValue() : 0.0;
                    items.add(new BudgetItem(greekName, columnName, amount));
                }
            } else {
                // Αν δεν υπάρχουν δεδομένα, δημιούργησε empty rows
                for (String[] category : MINISTRY_CATEGORIES) {
                    String columnName = category[0];
                    String greekName = category[1];
                    items.add(new BudgetItem(greekName, columnName, 0.0));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading ministries data for " + year + ": " + e.getMessage());
            e.printStackTrace();
            // Αν υπάρχει σφάλμα, δημιούργησε empty rows
            for (String[] category : MINISTRY_CATEGORIES) {
                String columnName = category[0];
                String greekName = category[1];
                items.add(new BudgetItem(greekName, columnName, 0.0));
            }
        }
        
        ministriesTable.setItems(items);
    }
    
    private void loadAdministrationsData(int year) {
        ObservableList<BudgetItem> items = FXCollections.observableArrayList();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM decentralized_administrations_" + year)) {
            
            if (rs.next()) {
                for (String[] category : ADMINISTRATION_CATEGORIES) {
                    String columnName = category[0];
                    String greekName = category[1];
                    double amount = rs.getBigDecimal(columnName) != null ? rs.getBigDecimal(columnName).doubleValue() : 0.0;
                    items.add(new BudgetItem(greekName, columnName, amount));
                }
            } else {
                // Αν δεν υπάρχουν δεδομένα, δημιούργησε empty rows
                for (String[] category : ADMINISTRATION_CATEGORIES) {
                    String columnName = category[0];
                    String greekName = category[1];
                    items.add(new BudgetItem(greekName, columnName, 0.0));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading administrations data for " + year + ": " + e.getMessage());
            e.printStackTrace();
            // Αν υπάρχει σφάλμα, δημιούργησε empty rows
            for (String[] category : ADMINISTRATION_CATEGORIES) {
                String columnName = category[0];
                String greekName = category[1];
                items.add(new BudgetItem(greekName, columnName, 0.0));
            }
        }
        
        administrationsTable.setItems(items);
    }
    
    // Save methods - use current selected year
    @FXML
    private void onSaveRevenue() {
        saveRevenueData(currentYear, revenueTable);
    }
    
    @FXML
    private void onSaveExpenses() {
        saveExpensesData(currentYear, expensesTable);
    }
    
    @FXML
    private void onSaveMinistries() {
        saveMinistriesData(currentYear, ministriesTable);
    }
    
    @FXML
    private void onSaveAdministrations() {
        saveAdministrationsData(currentYear, administrationsTable);
    }
    
    private void saveRevenueData(int year, TableView<BudgetItem> table) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // First, get the current total_revenue value
            String selectSql = "SELECT total_revenue FROM revenue_" + year;
            BigDecimal currentTotalRevenue = null;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
                if (rs.next()) {
                    currentTotalRevenue = rs.getBigDecimal("total_revenue");
                }
            }
            
            // If no row exists, we need to insert one
            if (currentTotalRevenue == null) {
                // Calculate total
                double total = table.getItems().stream().mapToDouble(BudgetItem::getAmount).sum();
                currentTotalRevenue = BigDecimal.valueOf(total);
                
                // Insert new row
                String insertSql = "INSERT INTO revenue_" + year + " (total_revenue";
                for (String[] category : REVENUE_CATEGORIES) {
                    insertSql += ", " + category[0];
                }
                insertSql += ") VALUES (?";
                for (int i = 0; i < REVENUE_CATEGORIES.length; i++) {
                    insertSql += ", ?";
                }
                insertSql += ")";
                
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setBigDecimal(1, currentTotalRevenue);
                    int paramIndex = 2;
                    for (BudgetItem item : table.getItems()) {
                        pstmt.setBigDecimal(paramIndex++, BigDecimal.valueOf(item.getAmount()));
                    }
                    pstmt.executeUpdate();
                }
            } else {
                // Update existing row
                StringBuilder updateSql = new StringBuilder("UPDATE revenue_" + year + " SET ");
                for (int i = 0; i < REVENUE_CATEGORIES.length; i++) {
                    if (i > 0) updateSql.append(", ");
                    updateSql.append(REVENUE_CATEGORIES[i][0]).append(" = ?");
                }
                updateSql.append(" WHERE total_revenue = ?");
                
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql.toString())) {
                    int paramIndex = 1;
                    for (BudgetItem item : table.getItems()) {
                        pstmt.setBigDecimal(paramIndex++, BigDecimal.valueOf(item.getAmount()));
                    }
                    pstmt.setBigDecimal(paramIndex, currentTotalRevenue);
                    pstmt.executeUpdate();
                }
                
                // Update total_revenue
                double total = table.getItems().stream().mapToDouble(BudgetItem::getAmount).sum();
                String updateTotalSql = "UPDATE revenue_" + year + " SET total_revenue = ? WHERE total_revenue = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateTotalSql)) {
                    pstmt.setBigDecimal(1, BigDecimal.valueOf(total));
                    pstmt.setBigDecimal(2, currentTotalRevenue);
                    pstmt.executeUpdate();
                }
            }
            
            // Update budget_summary table
            updateBudgetSummary(year);
            
            showSuccessAlert("Τα δεδομένα εσόδων για το " + year + " αποθηκεύτηκαν επιτυχώς!");
        } catch (Exception e) {
            System.err.println("Error saving revenue data for " + year + ": " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Σφάλμα αποθήκευσης: " + e.getMessage());
        }
    }
    
    private void saveExpensesData(int year, TableView<BudgetItem> table) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String selectSql = "SELECT total_expenses FROM expenses_" + year;
            BigDecimal currentTotalExpenses = null;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
                if (rs.next()) {
                    currentTotalExpenses = rs.getBigDecimal("total_expenses");
                }
            }
            
            if (currentTotalExpenses == null) {
                double total = table.getItems().stream().mapToDouble(BudgetItem::getAmount).sum();
                currentTotalExpenses = BigDecimal.valueOf(total);
                
                String insertSql = "INSERT INTO expenses_" + year + " (total_expenses";
                for (String[] category : EXPENSE_CATEGORIES) {
                    insertSql += ", " + category[0];
                }
                insertSql += ") VALUES (?";
                for (int i = 0; i < EXPENSE_CATEGORIES.length; i++) {
                    insertSql += ", ?";
                }
                insertSql += ")";
                
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setBigDecimal(1, currentTotalExpenses);
                    int paramIndex = 2;
                    for (BudgetItem item : table.getItems()) {
                        pstmt.setBigDecimal(paramIndex++, BigDecimal.valueOf(item.getAmount()));
                    }
                    pstmt.executeUpdate();
                }
            } else {
                StringBuilder updateSql = new StringBuilder("UPDATE expenses_" + year + " SET ");
                for (int i = 0; i < EXPENSE_CATEGORIES.length; i++) {
                    if (i > 0) updateSql.append(", ");
                    updateSql.append(EXPENSE_CATEGORIES[i][0]).append(" = ?");
                }
                updateSql.append(" WHERE total_expenses = ?");
                
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql.toString())) {
                    int paramIndex = 1;
                    for (BudgetItem item : table.getItems()) {
                        pstmt.setBigDecimal(paramIndex++, BigDecimal.valueOf(item.getAmount()));
                    }
                    pstmt.setBigDecimal(paramIndex, currentTotalExpenses);
                    pstmt.executeUpdate();
                }
                
                double total = table.getItems().stream().mapToDouble(BudgetItem::getAmount).sum();
                String updateTotalSql = "UPDATE expenses_" + year + " SET total_expenses = ? WHERE total_expenses = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateTotalSql)) {
                    pstmt.setBigDecimal(1, BigDecimal.valueOf(total));
                    pstmt.setBigDecimal(2, currentTotalExpenses);
                    pstmt.executeUpdate();
                }
            }
            
            // Update budget_summary table
            updateBudgetSummary(year);
            
            showSuccessAlert("Τα δεδομένα δαπανών για το " + year + " αποθηκεύτηκαν επιτυχώς!");
        } catch (Exception e) {
            System.err.println("Error saving expenses data for " + year + ": " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Σφάλμα αποθήκευσης: " + e.getMessage());
        }
    }
    
    private void saveMinistriesData(int year, TableView<BudgetItem> table) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String selectSql = "SELECT total_ministries FROM ministries_" + year;
            BigDecimal currentTotalMinistries = null;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
                if (rs.next()) {
                    currentTotalMinistries = rs.getBigDecimal("total_ministries");
                }
            }
            
            if (currentTotalMinistries == null) {
                double total = table.getItems().stream().mapToDouble(BudgetItem::getAmount).sum();
                currentTotalMinistries = BigDecimal.valueOf(total);
                
                String insertSql = "INSERT INTO ministries_" + year + " (total_ministries";
                for (String[] category : MINISTRY_CATEGORIES) {
                    insertSql += ", " + category[0];
                }
                insertSql += ") VALUES (?";
                for (int i = 0; i < MINISTRY_CATEGORIES.length; i++) {
                    insertSql += ", ?";
                }
                insertSql += ")";
                
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setBigDecimal(1, currentTotalMinistries);
                    int paramIndex = 2;
                    for (BudgetItem item : table.getItems()) {
                        pstmt.setBigDecimal(paramIndex++, BigDecimal.valueOf(item.getAmount()));
                    }
                    pstmt.executeUpdate();
                }
            } else {
                StringBuilder updateSql = new StringBuilder("UPDATE ministries_" + year + " SET ");
                for (int i = 0; i < MINISTRY_CATEGORIES.length; i++) {
                    if (i > 0) updateSql.append(", ");
                    updateSql.append(MINISTRY_CATEGORIES[i][0]).append(" = ?");
                }
                updateSql.append(" WHERE total_ministries = ?");
                
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql.toString())) {
                    int paramIndex = 1;
                    for (BudgetItem item : table.getItems()) {
                        pstmt.setBigDecimal(paramIndex++, BigDecimal.valueOf(item.getAmount()));
                    }
                    pstmt.setBigDecimal(paramIndex, currentTotalMinistries);
                    pstmt.executeUpdate();
                }
                
                double total = table.getItems().stream().mapToDouble(BudgetItem::getAmount).sum();
                String updateTotalSql = "UPDATE ministries_" + year + " SET total_ministries = ? WHERE total_ministries = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateTotalSql)) {
                    pstmt.setBigDecimal(1, BigDecimal.valueOf(total));
                    pstmt.setBigDecimal(2, currentTotalMinistries);
                    pstmt.executeUpdate();
                }
            }
            
            // Update budget_summary table
            updateBudgetSummary(year);
            
            showSuccessAlert("Τα δεδομένα υπουργείων για το " + year + " αποθηκεύτηκαν επιτυχώς!");
        } catch (Exception e) {
            System.err.println("Error saving ministries data for " + year + ": " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Σφάλμα αποθήκευσης: " + e.getMessage());
        }
    }
    
    private void saveAdministrationsData(int year, TableView<BudgetItem> table) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String selectSql = "SELECT total_da FROM decentralized_administrations_" + year;
            BigDecimal currentTotalDA = null;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
                if (rs.next()) {
                    currentTotalDA = rs.getBigDecimal("total_da");
                }
            }
            
            if (currentTotalDA == null) {
                double total = table.getItems().stream().mapToDouble(BudgetItem::getAmount).sum();
                currentTotalDA = BigDecimal.valueOf(total);
                
                String insertSql = "INSERT INTO decentralized_administrations_" + year + " (total_da";
                for (String[] category : ADMINISTRATION_CATEGORIES) {
                    insertSql += ", " + category[0];
                }
                insertSql += ") VALUES (?";
                for (int i = 0; i < ADMINISTRATION_CATEGORIES.length; i++) {
                    insertSql += ", ?";
                }
                insertSql += ")";
                
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setBigDecimal(1, currentTotalDA);
                    int paramIndex = 2;
                    for (BudgetItem item : table.getItems()) {
                        pstmt.setBigDecimal(paramIndex++, BigDecimal.valueOf(item.getAmount()));
                    }
                    pstmt.executeUpdate();
                }
            } else {
                StringBuilder updateSql = new StringBuilder("UPDATE decentralized_administrations_" + year + " SET ");
                for (int i = 0; i < ADMINISTRATION_CATEGORIES.length; i++) {
                    if (i > 0) updateSql.append(", ");
                    updateSql.append(ADMINISTRATION_CATEGORIES[i][0]).append(" = ?");
                }
                updateSql.append(" WHERE total_da = ?");
                
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql.toString())) {
                    int paramIndex = 1;
                    for (BudgetItem item : table.getItems()) {
                        pstmt.setBigDecimal(paramIndex++, BigDecimal.valueOf(item.getAmount()));
                    }
                    pstmt.setBigDecimal(paramIndex, currentTotalDA);
                    pstmt.executeUpdate();
                }
                
                double total = table.getItems().stream().mapToDouble(BudgetItem::getAmount).sum();
                String updateTotalSql = "UPDATE decentralized_administrations_" + year + " SET total_da = ? WHERE total_da = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateTotalSql)) {
                    pstmt.setBigDecimal(1, BigDecimal.valueOf(total));
                    pstmt.setBigDecimal(2, currentTotalDA);
                    pstmt.executeUpdate();
                }
            }
            
            // Update budget_summary table
            updateBudgetSummary(year);
            
            showSuccessAlert("Τα δεδομένα αποκεντρωμένων διοικήσεων για το " + year + " αποθηκεύτηκαν επιτυχώς!");
        } catch (Exception e) {
            System.err.println("Error saving administrations data for " + year + ": " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Σφάλμα αποθήκευσης: " + e.getMessage());
        }
    }
    
    // Updates the budget_summary table with current totals
    private void updateBudgetSummary(int year) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Ensure budget_summary table exists
            try (Statement stmt = conn.createStatement()) {
                String createTableSql = "CREATE TABLE IF NOT EXISTS budget_summary_" + year + " (" +
                                      "budget_result MONEY PRIMARY KEY," +
                                      "total_revenue MONEY," +
                                      "total_expenses MONEY," +
                                      "total_ministries MONEY," +
                                      "total_da MONEY," +
                                      "FOREIGN KEY (total_revenue) REFERENCES revenue_" + year + "(total_revenue)," +
                                      "FOREIGN KEY (total_expenses) REFERENCES expenses_" + year + "(total_expenses)," +
                                      "FOREIGN KEY (total_ministries) REFERENCES ministries_" + year + "(total_ministries)," +
                                      "FOREIGN KEY (total_da) REFERENCES decentralized_administrations_" + year + "(total_da)" +
                                      ")";
                stmt.execute(createTableSql);
            } catch (Exception e) {
                // If foreign keys fail, try without them
                try (Statement stmt = conn.createStatement()) {
                    String createTableSql = "CREATE TABLE IF NOT EXISTS budget_summary_" + year + " (" +
                                          "budget_result MONEY PRIMARY KEY," +
                                          "total_revenue MONEY," +
                                          "total_expenses MONEY," +
                                          "total_ministries MONEY," +
                                          "total_da MONEY" +
                                          ")";
                    stmt.execute(createTableSql);
                }
            }
            
            // Get current totals from individual tables
            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal totalExpenses = BigDecimal.ZERO;
            BigDecimal totalMinistries = BigDecimal.ZERO;
            BigDecimal totalDA = BigDecimal.ZERO;
            
            // Get total_revenue
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT total_revenue FROM revenue_" + year)) {
                if (rs.next()) {
                    totalRevenue = rs.getBigDecimal("total_revenue");
                    if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
                }
            } catch (Exception e) {
                // Table might not exist yet, use zero
            }
            
            // Get total_expenses
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT total_expenses FROM expenses_" + year)) {
                if (rs.next()) {
                    totalExpenses = rs.getBigDecimal("total_expenses");
                    if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;
                }
            } catch (Exception e) {
                // Table might not exist yet, use zero
            }
            
            // Get total_ministries
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT total_ministries FROM ministries_" + year)) {
                if (rs.next()) {
                    totalMinistries = rs.getBigDecimal("total_ministries");
                    if (totalMinistries == null) totalMinistries = BigDecimal.ZERO;
                }
            } catch (Exception e) {
                // Table might not exist yet, use zero
            }
            
            // Get total_da
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT total_da FROM decentralized_administrations_" + year)) {
                if (rs.next()) {
                    totalDA = rs.getBigDecimal("total_da");
                    if (totalDA == null) totalDA = BigDecimal.ZERO;
                }
            } catch (Exception e) {
                // Table might not exist yet, use zero
            }
            
            // Calculate budget_result (revenue - expenses)
            BigDecimal budgetResult = totalRevenue.subtract(totalExpenses);
            
            // Check if budget_summary row exists
            boolean exists = false;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM budget_summary_" + year)) {
                if (rs.next()) {
                    exists = rs.getInt("count") > 0;
                }
            } catch (Exception e) {
                // Table might not exist, we'll create it
            }
            
            if (exists) {
                // Update existing row
                String updateSql = "UPDATE budget_summary_" + year + 
                                 " SET total_revenue = ?, total_expenses = ?, total_ministries = ?, total_da = ?, budget_result = ?" +
                                 " WHERE budget_result IS NOT NULL OR 1=1";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setBigDecimal(1, totalRevenue);
                    pstmt.setBigDecimal(2, totalExpenses);
                    pstmt.setBigDecimal(3, totalMinistries);
                    pstmt.setBigDecimal(4, totalDA);
                    pstmt.setBigDecimal(5, budgetResult);
                    pstmt.executeUpdate();
                }
            } else {
                // Insert new row
                String insertSql = "INSERT INTO budget_summary_" + year + 
                                 " (budget_result, total_revenue, total_expenses, total_ministries, total_da)" +
                                 " VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setBigDecimal(1, budgetResult);
                    pstmt.setBigDecimal(2, totalRevenue);
                    pstmt.setBigDecimal(3, totalExpenses);
                    pstmt.setBigDecimal(4, totalMinistries);
                    pstmt.setBigDecimal(5, totalDA);
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating budget_summary for " + year + ": " + e.getMessage());
            e.printStackTrace();
            // Don't throw - this is a secondary update
        }
    }
    
    // Add/Delete methods - use current tables
    @FXML
    private void onAddRevenue() {
        // For now, just show a message - adding new categories would require schema changes
        showInfoAlert("Η προσθήκη νέων κατηγοριών απαιτεί αλλαγές στο σχήμα της βάσης.");
    }
    
    @FXML
    private void onDeleteRevenue() {
        BudgetItem selected = revenueTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setAmount(0.0);
            revenueTable.refresh();
        }
    }
    
    @FXML
    private void onAddExpense() {
        showInfoAlert("Η προσθήκη νέων κατηγοριών απαιτεί αλλαγές στο σχήμα της βάσης.");
    }
    
    @FXML
    private void onDeleteExpense() {
        BudgetItem selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setAmount(0.0);
            expensesTable.refresh();
        }
    }
    
    @FXML
    private void onAddMinistry() {
        showInfoAlert("Η προσθήκη νέων κατηγοριών απαιτεί αλλαγές στο σχήμα της βάσης.");
    }
    
    @FXML
    private void onDeleteMinistry() {
        BudgetItem selected = ministriesTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setAmount(0.0);
            ministriesTable.refresh();
        }
    }
    
    @FXML
    private void onAddAdministration() {
        showInfoAlert("Η προσθήκη νέων κατηγοριών απαιτεί αλλαγές στο σχήμα της βάσης.");
    }
    
    @FXML
    private void onDeleteAdministration() {
        BudgetItem selected = administrationsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setAmount(0.0);
            administrationsTable.refresh();
        }
    }
    
    @FXML
    private void onPublishBudget() {
        if (currentYear <= 0) {
            showErrorAlert("Παρακαλώ επιλέξτε έγκυρο έτος.");
            return;
        }
        
        // Confirm publication
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Επιβεβαίωση Δημοσίευσης");
        confirmAlert.setHeaderText("Δημοσίευση Προϋπολογισμού");
        confirmAlert.setContentText("Είστε σίγουροι ότι θέλετε να δημοσιεύσετε τον προϋπολογισμό για το έτος " + currentYear + ";\n\nΜετά τη δημοσίευση, ο προϋπολογισμός θα είναι διαθέσιμος στη σύνδεση πολίτη.");
        
        java.util.Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                publishYear(currentYear);
                showSuccessAlert("Ο προϋπολογισμός για το έτος " + currentYear + " δημοσιεύτηκε επιτυχώς!\n\nΤώρα είναι διαθέσιμος στη σύνδεση πολίτη.");
                updatePublishButtonVisibility();
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Σφάλμα κατά τη δημοσίευση: " + e.getMessage());
            }
        }
    }
    
    private void publishYear(int year) {
        try (java.sql.Connection connection = ui.DatabaseConnection.getConnection();
             java.sql.Statement stmt = connection.createStatement()) {
            
            // First, ensure the published_years table exists
            String createTableSql = "CREATE TABLE IF NOT EXISTS published_years (" +
                                  "year INTEGER PRIMARY KEY)";
            stmt.execute(createTableSql);
            
            // Insert the year if it doesn't exist
            String insertSql = "INSERT OR IGNORE INTO published_years (year) VALUES (" + year + ")";
            stmt.execute(insertSql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to publish year: " + e.getMessage(), e);
        }
    }
    
    @FXML
    private void onSaveComments() {
        if (commentsTextArea == null) return;
        
        String comments = commentsTextArea.getText();
        String categoryName = "budget_edit_general"; // Special category name for general budget edit comments
        
        try {
            UserData userData = UserData.getInstance();
            boolean success = userData.saveComment(categoryName, currentYear, comments);
            
            if (success) {
                showSuccessAlert("Τα σχόλια αποθηκεύτηκαν επιτυχώς!");
            } else {
                showErrorAlert("Σφάλμα κατά την αποθήκευση των σχολίων.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Σφάλμα κατά την αποθήκευση των σχολίων: " + e.getMessage());
        }
    }
    
    private void loadCommentsForCurrentYear() {
        if (commentsTextArea == null) return;
        
        String categoryName = "budget_edit_general";
        try {
            UserData userData = UserData.getInstance();
            String comments = userData.getComment(categoryName, currentYear);
            
            if (comments != null && !comments.isEmpty()) {
                commentsTextArea.setText(comments);
            } else {
                commentsTextArea.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            commentsTextArea.clear();
        }
    }
    
    private void ensureCommentsTableExists() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String createTableSql = "CREATE TABLE IF NOT EXISTS user_comments (" +
                                  "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                  "category_name TEXT NOT NULL," +
                                  "year INTEGER NOT NULL," +
                                  "comments TEXT," +
                                  "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                                  "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                                  "UNIQUE(category_name, year)" +
                                  ")";
            stmt.execute(createTableSql);
        } catch (Exception e) {
            System.err.println("Error creating user_comments table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onClose() {
        Stage stage = (Stage) (categoryTabPane != null ? categoryTabPane.getScene().getWindow() : null);
        if (stage != null) {
            stage.close();
        }
    }
    
    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Επιτυχία");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Σφάλμα");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Πληροφόρηση");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
