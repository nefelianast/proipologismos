package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;
import javafx.scene.chart.CategoryAxis; // ΠΡΟΣΘΕΣΑ ΤΑ 5 ΑΥΤΑ
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

public class HomeController {

    // Inner class for table data
    public static class CategoryData {
        private final StringProperty category;
        private final DoubleProperty amount;
        private final DoubleProperty percentage;
        private final StringProperty change;

        public CategoryData(String category, double amount, double percentage, String change) {
            this.category = new SimpleStringProperty(category);
            this.amount = new SimpleDoubleProperty(amount);
            this.percentage = new SimpleDoubleProperty(percentage);
            this.change = new SimpleStringProperty(change);
        }

        public String getCategory() {
            return category.get();
        }

        public StringProperty categoryProperty() {
            return category;
        }

        public double getAmount() {
            return amount.get();
        }

        public DoubleProperty amountProperty() {
            return amount;
        }

        public double getPercentage() {
            return percentage.get();
        }

        public DoubleProperty percentageProperty() {
            return percentage;
        }

        public String getChange() {
            return change.get();
        }

        public StringProperty changeProperty() {
            return change;
        }
    }

    @FXML
    private Label statusLabel;
    @FXML
    private ComboBox<String> yearCombo;
    
    // Summary labels
    @FXML
    private Label totalRevenuesLabel;
    @FXML
    private Label revenuesDeltaLabel;
    @FXML
    private Label totalExpensesLabel;
    @FXML
    private Label expensesDeltaLabel;
    @FXML
    private Label balanceLabel;
    @FXML
    private Label balanceStatusLabel;
    
    
    // Table
    @FXML
    private TableView<CategoryData> categoryTable;
    @FXML
    private TableColumn<CategoryData, String> categoryColumn;
    @FXML
    private TableColumn<CategoryData, Double> amountColumn;
    @FXML
    private TableColumn<CategoryData, Double> percentageColumn;
    @FXML
    private TableColumn<CategoryData, String> changeColumn;
    @FXML // ΠΡΟΣΘΕΣΑ ΑΥΤΑ ΤΑ 5
    private PieChart pieRevenue;
    @FXML 
    private PieChart pieExpenses;
    @FXML 
    private PieChart pieMinistries;
    @FXML 
    private PieChart pieTotals;
    @FXML 
    private LineChart<String, Number> lineHistory;
    
    // Navigation buttons
    @FXML
    private Button navHome;
    @FXML
    private Button navRevenues;
    @FXML
    private Button navExpenses;
    @FXML
    private Button navCategories;
    @FXML
    private Button navComparisons;
    @FXML
    private Button navSimulations;
    @FXML
    private Button navAbout;

    private final DecimalFormat df = new DecimalFormat("#,##0.0");
    private BudgetDataService dataService;

    @FXML
    private void initialize() {
        // Initialize data service
        dataService = BudgetDataService.getInstance();
        // Initialize year selector
        yearCombo.setItems(FXCollections.observableArrayList("2023", "2024", "2025"));
        yearCombo.getSelectionModel().selectLast(); // 2025 by default
        yearCombo.setOnAction(e -> updateDataForYear());

        // Initialize table columns
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        percentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        changeColumn.setCellValueFactory(new PropertyValueFactory<>("change"));
        
        // Format amount column
        amountColumn.setCellFactory(column -> new TableCell<CategoryData, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText("€" + df.format(amount) + " δισ.");
                }
            }
        });
        
        // Format percentage column
        percentageColumn.setCellFactory(column -> new TableCell<CategoryData, Double>() {
            @Override
            protected void updateItem(Double percentage, boolean empty) {
                super.updateItem(percentage, empty);
                if (empty || percentage == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f%%", percentage));
                }
            }
        });

        // Load initial data
        updateDataForYear();
        
        // Set active navigation button
        setActiveNavButton(navHome);
    }

    private void updateDataForYear() {
        String selectedYear = yearCombo.getValue();
        if (selectedYear == null) return;

        // Update summary cards with sample data
        updateSummaryCards(selectedYear);
        
        // Update table
        updateCategoryTable();
        updateCharts(Integer.parseInt(selectedYear)); // ΠΡΟΣΘΕΣΑ ΑΥΤΟ
    }

    private void updateSummaryCards(String year) {
        int yearInt = Integer.parseInt(year);
        
        // Load real data from service
        double totalRevenues = dataService.getTotalRevenues(yearInt);
        double totalExpenses = dataService.getTotalExpenses(yearInt);
        double balance = dataService.getBalance(yearInt);
        double revenuesChange = dataService.getRevenuesChange(yearInt);
        double expensesChange = dataService.getExpensesChange(yearInt);
        
        // Update revenues
        totalRevenuesLabel.setText("€" + df.format(totalRevenues) + " δισ.");
        String revenuesChangeText = String.format("%+.1f%% από πέρυσι", revenuesChange);
        revenuesDeltaLabel.setText(revenuesChangeText);
        revenuesDeltaLabel.getStyleClass().removeAll("negative", "positive");
        revenuesDeltaLabel.getStyleClass().add(revenuesChange >= 0 ? "positive" : "negative");
        
        // Update expenses
        totalExpensesLabel.setText("€" + df.format(totalExpenses) + " δισ.");
        String expensesChangeText = String.format("%+.1f%% από πέρυσι", expensesChange);
        expensesDeltaLabel.setText(expensesChangeText);
        expensesDeltaLabel.getStyleClass().removeAll("negative", "positive");
        expensesDeltaLabel.getStyleClass().add(expensesChange >= 0 ? "positive" : "negative");
        
        // Update balance
        if (balance >= 0) {
            balanceLabel.setText("+€" + df.format(balance) + " δισ.");
            balanceStatusLabel.setText("Πλεόνασμα");
            balanceStatusLabel.getStyleClass().removeAll("negative");
            balanceStatusLabel.getStyleClass().add("positive");
        } else {
            balanceLabel.setText("€" + df.format(balance) + " δισ.");
            balanceStatusLabel.setText("Ελλείμμα");
            balanceStatusLabel.getStyleClass().removeAll("positive");
            balanceStatusLabel.getStyleClass().add("negative");
        }
    }


    private void updateCategoryTable() {
        String selectedYear = yearCombo.getValue();
        if (selectedYear == null) return;
        
        int yearInt = Integer.parseInt(selectedYear);
        List<BudgetDataService.CategoryInfo> categories = dataService.getCategories(yearInt);
        
        // Get previous year data for comparison
        List<BudgetDataService.CategoryInfo> prevCategories = dataService.getCategories(yearInt - 1);
        Map<String, Double> prevYearMap = new HashMap<>();
        for (BudgetDataService.CategoryInfo cat : prevCategories) {
            prevYearMap.put(cat.getName(), cat.getAmount());
        }
        
        // Convert to table data
        ObservableList<CategoryData> tableData = FXCollections.observableArrayList();
        for (BudgetDataService.CategoryInfo cat : categories) {
            double prevAmount = prevYearMap.getOrDefault(cat.getName(), cat.getAmount());
            double change = prevAmount > 0 ? ((cat.getAmount() - prevAmount) / prevAmount) * 100 : 0;
            String changeText = String.format("%+.1f%%", change);
            tableData.add(new CategoryData(cat.getName(), cat.getAmount(), cat.getPercentage(), changeText));
        }
        
        categoryTable.setItems(tableData);
    }

    // Navigation handlers
    @FXML
    private void onNavigateHome() {
        setActiveNavButton(navHome);
        System.out.println("Navigation: Αρχική");
        // TODO: Show home dashboard
    }

    @FXML
    private void onNavigateRevenues() {
        setActiveNavButton(navRevenues);
        System.out.println("Navigation: Έσοδα");
        // TODO: Show revenues screen
    }

    @FXML
    private void onNavigateExpenses() {
        setActiveNavButton(navExpenses);
        System.out.println("Navigation: Δαπάνες");
        // TODO: Show expenses screen
    }

    @FXML
    private void onNavigateCategories() {
        setActiveNavButton(navCategories);
        System.out.println("Navigation: Κατηγορίες");
        // TODO: Show categories screen
    }

    @FXML
    private void onNavigateComparisons() {
        setActiveNavButton(navComparisons);
        System.out.println("Navigation: Συγκρίσεις");
        // TODO: Show comparisons screen
    }

    @FXML
    private void onNavigateSimulations() {
        setActiveNavButton(navSimulations);
        System.out.println("Navigation: Προσομοιώσεις");
        // TODO: Show simulations screen
    }

    @FXML
    private void onNavigateAbout() {
        setActiveNavButton(navAbout);
        System.out.println("Navigation: Σχετικά");
        // TODO: Show about dialog
    }

    private void setActiveNavButton(Button activeButton) {
        // Remove active class from all nav buttons
        navHome.getStyleClass().remove("nav-button-active");
        navRevenues.getStyleClass().remove("nav-button-active");
        navExpenses.getStyleClass().remove("nav-button-active");
        navCategories.getStyleClass().remove("nav-button-active");
        navComparisons.getStyleClass().remove("nav-button-active");
        navSimulations.getStyleClass().remove("nav-button-active");
        navAbout.getStyleClass().remove("nav-button-active");
        
        // Add active class to selected button
        if (activeButton != null) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    @FXML
    private void onShowRevenues() {
        onNavigateRevenues();
    }

    @FXML
    private void onShowExpenses() {
        onNavigateExpenses();
    }

    @FXML
    private void onShowScenarios() {
        onNavigateSimulations();
    }
    
    //  ΝΕΕΣ ΜΕΘΟΔΟΙ ΓΙΑ ΤΑ ΓΡΑΦΗΜΑΤΑ (CHARTS LOGIC)

    private void updateCharts(int year) {
        // Αν το FXML δεν έχει φορτώσει ακόμα τα γραφήματα, σταματάμε
        if (pieRevenue == null) return;

        // 1. Πίτα Εσόδων
        fillPie(pieRevenue, dataService.getRevenueBreakdown(year), "Έσοδα " + year);

        // 2. Πίτα Εξόδων
        fillPie(pieExpenses, dataService.getExpenseBreakdown(year), "Έξοδα " + year);

        // 3. Πίτα Υπουργείων
        fillPie(pieMinistries, dataService.getMinistriesBreakdown(year), "Υπουργεία " + year);

        // 4. Πίτα Συνόλων (Έσοδα vs Έξοδα)
        double totalRev = dataService.getTotalAmount(year, "total_revenue");
        double totalExp = dataService.getTotalAmount(year, "total_expenses");
        
        ObservableList<PieChart.Data> totals = FXCollections.observableArrayList(
            new PieChart.Data("Έσοδα", totalRev),
            new PieChart.Data("Έξοδα", totalExp)
        );
        pieTotals.setData(totals);
        pieTotals.setTitle("Ισοζύγιο " + year);

        // 5. Γραμμικό Διάγραμμα (2023-2025)
        loadLineChart();
    }

    // Βοηθητική μέθοδος για γέμισμα πίτας
    private void fillPie(PieChart chart, Map<String, Double> data, String title) {
        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
        data.forEach((k, v) -> {
            if (v > 0) list.add(new PieChart.Data(k, v));
        });
        chart.setData(list);
        chart.setTitle(title);
    }

    // Βοηθητική μέθοδος για γραμμικό διάγραμμα
    private void loadLineChart() {
        if (lineHistory == null) return;
        lineHistory.getData().clear();
        
        XYChart.Series<String, Number> revSeries = new XYChart.Series<>();
        revSeries.setName("Έσοδα");
        
        XYChart.Series<String, Number> expSeries = new XYChart.Series<>();
        expSeries.setName("Έξοδα");

        int[] years = {2023, 2024, 2025};
        for (int y : years) {
            double rev = dataService.getTotalAmount(y, "total_revenue");
            double exp = dataService.getTotalAmount(y, "total_expenses");
            
            revSeries.getData().add(new XYChart.Data<>(String.valueOf(y), rev));
            expSeries.getData().add(new XYChart.Data<>(String.valueOf(y), exp));
        }
        lineHistory.getData().addAll(revSeries, expSeries);
    }
}
