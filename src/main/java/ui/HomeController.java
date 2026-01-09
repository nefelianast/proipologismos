package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.StackedBarChart;

/**
 * Main controller for the home screen of the Budget Analysis System.
 * Manages data display, user interactions, and navigation between different views.
 * Supports both citizen and government user types with different access levels.
 */
public class HomeController {
    
    /**
     * Enum representing the type of user accessing the system.
     */
    public enum UserType {
        /** Regular citizen user with read-only access */
        CITIZEN,
        /** Government user with full access including editing capabilities */
        GOVERNMENT
    }
    
    /**
     * Current user type (defaults to CITIZEN)
     */
    private static UserType currentUserType = UserType.CITIZEN;
    
    /**
     * Authentication service
     */
    private Authentication authentication = new Authentication();
    
    /**
     * Sets the current user type.
     * 
     * @param userType The user type to set (CITIZEN or GOVERNMENT)
     */
    public static void setUserType(UserType userType) {
        currentUserType = userType;
    }
    
    /**
     * Gets the current user type.
     * 
     * @return The current UserType
     */
    public static UserType getUserType() {
        return currentUserType;
    }
    
    /**
     * Checks if the current user is a government user.
     * 
     * @return true if the user is a government user, false otherwise
     */
    public static boolean isGovernmentUser() {
        return currentUserType == UserType.GOVERNMENT;
    }


    /**
     * Inner class representing category data for table display.
     * Contains information about budget categories including amounts, percentages, and changes.
     */
    public static class CategoryData {
        private final StringProperty category;
        private final DoubleProperty amount;
        private final DoubleProperty percentage;
        private final StringProperty change;
        private final StringProperty type;
        private final StringProperty status;
        private final DoubleProperty previousYearAmount;
        private final StringProperty comments;

        public CategoryData(String category, double amount, double percentage, String change) {
            this(category, amount, percentage, change, extractTypeFromChange(change), 0.0);
        }
        
        public CategoryData(String category, double amount, double percentage, String change, String type, double previousYearAmount) {
            this.category = new SimpleStringProperty(category);
            this.amount = new SimpleDoubleProperty(amount);
            this.percentage = new SimpleDoubleProperty(percentage);
            this.change = new SimpleStringProperty(change);
            this.type = new SimpleStringProperty(type);
            this.previousYearAmount = new SimpleDoubleProperty(previousYearAmount);
            this.status = new SimpleStringProperty(calculateStatus(amount, previousYearAmount));
            this.comments = new SimpleStringProperty("");
        }
        
        private static String extractTypeFromChange(String change) {
            if (change == null) return "Άγνωστο";
            if (change.contains("Έσοδο")) return "Έσοδο";
            if (change.contains("Δαπάνη")) return "Δαπάνη";
            if (change.contains("Υπουργείο")) return "Υπουργείο";
            if (change.contains("Αποκεντρωμένη Διοίκηση")) return "Αποκεντρωμένη Διοίκηση";
            return "Άγνωστο";
        }
        
        private static String calculateStatus(double currentAmount, double previousAmount) {
            if (previousAmount == 0) return "Νέο";
            double changePercent = StatisticalAnalysis.calculatePercentageChange(currentAmount, previousAmount);
            if (changePercent > 5) return "Αύξηση";
            if (changePercent < -5) return "Μείωση";
            return "Σταθερό";
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
        
        public String getType() {
            return type.get();
        }
        
        public StringProperty typeProperty() {
            return type;
        }
        
        public String getStatus() {
            return status.get();
        }
        
        public StringProperty statusProperty() {
            return status;
        }
        
        public double getPreviousYearAmount() {
            return previousYearAmount.get();
        }
        
        public DoubleProperty previousYearAmountProperty() {
            return previousYearAmount;
        }
        
        public String getChangeFromPrevious() {
            if (previousYearAmount.get() == 0) return "Νέο";
            double changeValue = amount.get() - previousYearAmount.get();
            double changePercent = StatisticalAnalysis.calculatePercentageChange(amount.get(), previousYearAmount.get());
            return AmountFormatter.formatChange(changePercent, changeValue);
        }
        
        public String getComments() {
            return comments.get();
        }
        
        public StringProperty commentsProperty() {
            return comments;
        }
        
        public void setComments(String comments) {
            this.comments.set(comments);
        }
    }

    @FXML
    private Label statusLabel;
    @FXML
    private Label headerTitleLabel;
    @FXML
    private MenuItem authMenuItem;
    @FXML
    private ComboBox<String> yearComboBox;
    @FXML
    private ComboBox<String> dataManagementYearComboBox;
    
    private String selectedYear = "2025";
    private int dataManagementSelectedYear = Calendar.getInstance().get(Calendar.YEAR);
    
    // Views
    @FXML
    private VBox homeView;
    @FXML
    private VBox ministriesView;
    @FXML
    private VBox revenuesView;
    @FXML
    private VBox expensesView;
    @FXML
    private VBox administrationsView;
    @FXML
    private VBox dataManagementView;
    
    // Quick navigation cards
    @FXML
    private VBox quickNavMinistries;
    @FXML
    private VBox quickNavRevenues;
    @FXML
    private VBox quickNavExpenses;
    @FXML
    private VBox quickNavAdministrations;
    @FXML
    private MenuItem projectionsMenuItem;
    @FXML
    private MenuItem dataExplorationMenuItem;
    @FXML
    private MenuItem statisticsMenuItem;
    @FXML
    private Button homeEditButton;
    
    // Data Management UI - Revenues Table
    @FXML
    private TableView<CategoryData> dmRevenuesTable;
    @FXML
    private TableColumn<CategoryData, String> revCategoryColumn;
    @FXML
    private TableColumn<CategoryData, Double> revAmountColumn;
    @FXML
    private TableColumn<CategoryData, Double> revPercentageColumn;
    @FXML
    private TableColumn<CategoryData, String> revChangeColumn;
    @FXML
    private TableColumn<CategoryData, String> revStatusColumn;
    @FXML
    private TableColumn<CategoryData, String> revCommentsColumn;
    
    // Data Management UI - Expenses Table
    @FXML
    private TableView<CategoryData> dmExpensesTable;
    @FXML
    private TableColumn<CategoryData, String> expCategoryColumn;
    @FXML
    private TableColumn<CategoryData, Double> expAmountColumn;
    @FXML
    private TableColumn<CategoryData, Double> expPercentageColumn;
    @FXML
    private TableColumn<CategoryData, String> expChangeColumn;
    @FXML
    private TableColumn<CategoryData, String> expStatusColumn;
    @FXML
    private TableColumn<CategoryData, String> expCommentsColumn;
    
    // Keep old table reference for backward compatibility (will be removed later)
    @FXML
    private TableView<CategoryData> dataManagementTable;
    @FXML
    private TableColumn<CategoryData, String> dmCategoryColumn;
    @FXML
    private TableColumn<CategoryData, Double> dmAmountColumn;
    @FXML
    private TableColumn<CategoryData, Double> dmPercentageColumn;
    @FXML
    private TableColumn<CategoryData, String> dmTypeColumn;
    @FXML
    private TableColumn<CategoryData, String> dmChangeColumn;
    @FXML
    private TableColumn<CategoryData, String> dmStatusColumn;
    @FXML
    private Label dataManagementTitleLabel;
    @FXML
    private Label dataManagementYearTitleLabel;
    @FXML
    private Button editDataButton;
    @FXML
    private Button deleteDataButton;
    @FXML
    private Button publishYearButton;
    @FXML
    private Button bulkEditButton;
    @FXML
    private TextArea internalCommentsArea;
    @FXML
    private TextField dmSearchField;
    @FXML
    private ComboBox<String> dmTypeFilter;
    @FXML
    private Label dmRecordCountLabel;
    @FXML
    private Label dmTotalRevenuesLabel;
    @FXML
    private Label dmTotalExpensesLabel;
    @FXML
    private Label dmBalanceLabel;
    @FXML
    private Label dmYearChangeLabel;
    
    // Data Exploration View
    @FXML
    private VBox dataExplorationView;
    @FXML
    private VBox statisticsView;
    @FXML
    private VBox projectionsView;
    @FXML
    private Button exploreTotalButton;
    @FXML
    private Button exploreMinistryButton;
    @FXML
    private Button exploreRevenueCategoryButton;
    @FXML
    private Button exploreExpenseCategoryButton;
    @FXML
    private Button exploreComparisonButton;
    @FXML
    private Button exploreTrendsButton;
    @FXML
    private ComboBox<String> exploreYearComboBox;
    @FXML
    private VBox exploreDynamicFilters;
    @FXML
    private Label exploreMinistryLabel;
    @FXML
    private ComboBox<String> exploreMinistryComboBox;
    @FXML
    private Label exploreRevenueCategoryLabel;
    @FXML
    private ComboBox<String> exploreRevenueCategoryComboBox;
    @FXML
    private Label exploreExpenseCategoryLabel;
    @FXML
    private ComboBox<String> exploreExpenseCategoryComboBox;
    @FXML
    private Label exploreYear1Label;
    @FXML
    private ComboBox<String> exploreYear1ComboBox;
    @FXML
    private Label exploreYear2Label;
    @FXML
    private ComboBox<String> exploreYear2ComboBox;
    @FXML
    private Button exploreLoadComparisonButton;
    @FXML
    private Label exploreViewTitleLabel;
    @FXML
    private Label exploreViewDescriptionLabel;
    @FXML
    private TableView<CategoryData> exploreResultsTable;
    @FXML
    private TableColumn<CategoryData, String> exploreColumn1;
    @FXML
    private TableColumn<CategoryData, Double> exploreColumn2;
    @FXML
    private TableColumn<CategoryData, String> exploreColumn3;
    @FXML
    private TableColumn<CategoryData, Double> exploreColumn4;
    @FXML
    private TableColumn<CategoryData, String> exploreColumn5;
    
    private String currentExplorationView = "";
    
    // Statistics View
    @FXML
    private ComboBox<String> statisticsStartYearComboBox;
    @FXML
    private ComboBox<String> statisticsEndYearComboBox;
    @FXML
    private Button calculateStatisticsButton;
    @FXML
    private Label statsRevenueMeanLabel;
    @FXML
    private Label statsRevenueMedianLabel;
    @FXML
    private Label statsRevenueStdDevLabel;
    @FXML
    private Label statsRevenueVarianceLabel;
    @FXML
    private Label statsRevenueCoeffVarLabel;
    @FXML
    private Label statsExpenseMeanLabel;
    @FXML
    private Label statsExpenseMedianLabel;
    @FXML
    private Label statsExpenseStdDevLabel;
    @FXML
    private Label statsExpenseVarianceLabel;
    @FXML
    private Label statsExpenseCoeffVarLabel;
    @FXML
    private Label statsCorrelationLabel;
    @FXML
    private Label statsRevenueTrendLabel;
    @FXML
    private Label statsExpenseTrendLabel;
    @FXML
    private TableView<Map<String, Object>> statisticsOutliersTable;
    @FXML
    private TableColumn<Map<String, Object>, String> statsOutlierYearColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> statsOutlierTypeColumn;
    @FXML
    private TableColumn<Map<String, Object>, Double> statsOutlierValueColumn;
    @FXML
    private TableColumn<Map<String, Object>, Double> statsOutlierZScoreColumn;
    
    // Charts (from graphs branch)
    @FXML
    private PieChart pieRevenue;
    @FXML
    private PieChart pieExpenses;
    @FXML
    private PieChart pieMinistries;
    @FXML
    private PieChart pieTotals;
    @FXML
    private LineChart<String, Number> lineHistory;
    
    // New Charts for Revenues View
    @FXML
    private BarChart<String, Number> barRevenueTop;
    @FXML
    private AreaChart<String, Number> areaRevenueTrend;
    @FXML
    private StackedBarChart<String, Number> stackedRevenue;
    
    // New Charts for Expenses View
    @FXML
    private BarChart<String, Number> barExpenseTop;
    @FXML
    private AreaChart<String, Number> areaExpenseTrend;
    @FXML
    private StackedBarChart<String, Number> stackedExpense;
    
    // New Charts for Ministries View
    @FXML
    private AreaChart<String, Number> areaMinistriesTrend;
    
    // New Charts for Administrations View
    @FXML
    private PieChart pieAdministrations;
    @FXML
    private BarChart<String, Number> barAdministrationsTop;
    @FXML
    private AreaChart<String, Number> areaAdministrationsTrend;
    
    // Projections/Simulations View
    @FXML
    private ComboBox<String> simulationBaseYearComboBox;
    @FXML
    private Button loadSimulationDataButton;
    @FXML
    private TextField revenueIncreasePercentField;
    @FXML
    private TextField expenseDecreasePercentField;
    @FXML
    private TextField combinedRevenueChangeField;
    @FXML
    private TextField combinedExpenseChangeField;
    @FXML
    private ComboBox<String> simulationFutureYearComboBox;
    @FXML
    private TextField revenueTrendField;
    @FXML
    private TextField expenseTrendField;
    @FXML
    private TextArea simulationResultsArea;
    
    private int simulationBaseYear = Calendar.getInstance().get(Calendar.YEAR);
    private double simulationBaseRevenue = 0;
    private double simulationBaseExpense = 0;
    
    // Filtered data for search/filter
    private ObservableList<CategoryData> allDataManagementItems = FXCollections.observableArrayList();
    private ObservableList<CategoryData> revenuesItems = FXCollections.observableArrayList();
    private ObservableList<CategoryData> expensesItems = FXCollections.observableArrayList();
    
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
    @FXML
    private Label userTypeLabel;
    @FXML
    private Label userTypeIconLabel;
    
    // Ministries Table
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
    
    // Revenues Table
    @FXML
    private TableView<CategoryData> revenuesTable;
    @FXML
    private TableColumn<CategoryData, String> revenueCategoryColumn;
    @FXML
    private TableColumn<CategoryData, Double> revenueAmountColumn;
    @FXML
    private TableColumn<CategoryData, Double> revenuePercentageColumn;
    
    // Expenses Table
    @FXML
    private TableView<CategoryData> expensesTable;
    @FXML
    private TableColumn<CategoryData, String> expenseCategoryColumn;
    @FXML
    private TableColumn<CategoryData, Double> expenseAmountColumn;
    @FXML
    private TableColumn<CategoryData, Double> expensePercentageColumn;
    
    // Administrations Table
    @FXML
    private TableView<CategoryData> administrationsTable;
    @FXML
    private TableColumn<CategoryData, String> adminCategoryColumn;
    @FXML
    private TableColumn<CategoryData, Double> adminAmountColumn;
    @FXML
    private TableColumn<CategoryData, Double> adminPercentageColumn;
    

    private BudgetData budgetData;
    private UserData userData;
    private ExportsImports exportImportService;
    
    /**
     * Check if a year allows modifications (current year or future years only)
     * @param year The year to check
     * @return true if modifications are allowed, false otherwise
     */
    private boolean isYearEditable(int year) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return year >= currentYear;
    }
    
    /**
     * Extract year from CategoryData change field (format: "2025 | Έσοδο")
     * @param data The CategoryData object
     * @return The year as integer, or -1 if not found
     */
    private int extractYearFromData(CategoryData data) {
        if (data == null || data.getChange() == null) {
            return -1;
        }
        String change = data.getChange();
        if (change.contains("|")) {
            try {
                return Integer.parseInt(change.split("\\|")[0].trim());
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }
    
    /**
     * Setup table column formatting (amount, percentage, status)
     */
    private void setupTableColumnFormatting(TableColumn<CategoryData, Double> amountColumn, 
                                           TableColumn<CategoryData, Double> percentageColumn,
                                           TableColumn<CategoryData, String> statusColumn) {
        // Format amount column
        amountColumn.setCellFactory(column -> new TableCell<CategoryData, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(AmountFormatter.formatCurrency(amount));
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
                    setText(AmountFormatter.formatPercentage(percentage));
                }
            }
        });
        
        // Format status column with visual badges and icons
        statusColumn.setCellFactory(column -> new TableCell<CategoryData, String>() {
            private final HBox container = new HBox(6);
            private final Label iconLabel = new Label();
            private final Label textLabel = new Label();
            
            {
                container.setAlignment(Pos.CENTER_LEFT);
                textLabel.getStyleClass().add("status-badge");
                container.getChildren().addAll(iconLabel, textLabel);
            }
            
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    textLabel.setText(status);
                    iconLabel.setText("");
                    textLabel.getStyleClass().removeAll("status-badge-increase", "status-badge-decrease", "status-badge-new", "status-badge-stable");
                    
                    switch (status) {
                        case "Αύξηση":
                            iconLabel.setText("↑");
                            iconLabel.setStyle("-fx-text-fill: #065f46; -fx-font-weight: bold;");
                            textLabel.getStyleClass().add("status-badge-increase");
                            break;
                        case "Μείωση":
                            iconLabel.setText("↓");
                            iconLabel.setStyle("-fx-text-fill: #991b1b; -fx-font-weight: bold;");
                            textLabel.getStyleClass().add("status-badge-decrease");
                            break;
                        case "Νέο":
                            iconLabel.setText("✨");
                            iconLabel.setStyle("-fx-text-fill: #1e40af; -fx-font-weight: bold;");
                            textLabel.getStyleClass().add("status-badge-new");
                            break;
                        default:
                            iconLabel.setText("—");
                            iconLabel.setStyle("-fx-text-fill: #4b5563;");
                            textLabel.getStyleClass().add("status-badge-stable");
                    }
                    setGraphic(container);
                }
            }
        });
    }
    
    /**
     * Setup table selection listeners
     */
    private void setupTableSelection(TableView<CategoryData> table) {
        if (table == null) return;
        
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            boolean isEditable = false;
            
            if (hasSelection) {
                int year = extractYearFromData(newSelection);
                isEditable = isYearEditable(year);
                
                // Load comments for selected category
                if (internalCommentsArea != null && userData != null) {
                    String categoryName = newSelection.getCategory();
                    String comments = userData.getComment(categoryName, year);
                    if (comments != null) {
                        internalCommentsArea.setText(comments);
                        newSelection.setComments(comments);
                    } else {
                        // Load from CategoryData if exists
                        String existingComments = newSelection.getComments();
                        if (existingComments != null && !existingComments.isEmpty()) {
                            internalCommentsArea.setText(existingComments);
                        } else {
                            internalCommentsArea.clear();
                        }
                    }
                }
            } else {
                // Clear comments area when no selection
                if (internalCommentsArea != null) {
                    internalCommentsArea.clear();
                }
            }
            
            if (editDataButton != null) editDataButton.setDisable(!hasSelection || !isEditable);
            if (deleteDataButton != null) deleteDataButton.setDisable(!hasSelection || !isEditable);
            if (bulkEditButton != null) {
                int selectedCount = getTotalSelectedCount();
                bulkEditButton.setDisable(selectedCount == 0 || !isEditable);
            }
        });
        
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    /**
     * Get total selected count from both tables
     */
    private int getTotalSelectedCount() {
        int count = 0;
        if (dmRevenuesTable != null) {
            count += dmRevenuesTable.getSelectionModel().getSelectedItems().size();
        }
        if (dmExpensesTable != null) {
            count += dmExpensesTable.getSelectionModel().getSelectedItems().size();
        }
        if (dataManagementTable != null) {
            count += dataManagementTable.getSelectionModel().getSelectedItems().size();
        }
        return count;
    }
    
    /**
     * Get selected item from either revenues or expenses table
     */
    private CategoryData getSelectedItemFromTables() {
        if (dmRevenuesTable != null && dmRevenuesTable.getSelectionModel().getSelectedItem() != null) {
            return dmRevenuesTable.getSelectionModel().getSelectedItem();
        }
        if (dmExpensesTable != null && dmExpensesTable.getSelectionModel().getSelectedItem() != null) {
            return dmExpensesTable.getSelectionModel().getSelectedItem();
        }
        if (dataManagementTable != null) {
            return dataManagementTable.getSelectionModel().getSelectedItem();
        }
        return null;
    }
    
    /**
     * Get all selected items from both tables
     */
    private ObservableList<CategoryData> getSelectedItemsFromTables() {
        ObservableList<CategoryData> selected = FXCollections.observableArrayList();
        if (dmRevenuesTable != null) {
            selected.addAll(dmRevenuesTable.getSelectionModel().getSelectedItems());
        }
        if (dmExpensesTable != null) {
            selected.addAll(dmExpensesTable.getSelectionModel().getSelectedItems());
        }
        if (dataManagementTable != null) {
            selected.addAll(dataManagementTable.getSelectionModel().getSelectedItems());
        }
        return selected;
    }

    @FXML
    private void initialize() {
        // Initialize data service
        budgetData = BudgetData.getInstance();
        userData = UserData.getInstance();
        exportImportService = ExportsImports.getInstance();
        
        // Initialize published years table
        initializePublishedYearsTable();
        
        // Set initial header title
        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Κρατικός Προϋπολογισμός του 2025");
        }
        
        // Initialize auth button based on user type
        updateAuthButton();
        
        // Update user type label
        updateUserTypeLabel();
        
        // Show/hide government features based on user type
        updateGovernmentFeatures();
        
        // Ensure data exploration view is hidden initially
        if (dataExplorationView != null) {
            dataExplorationView.setVisible(false);
            dataExplorationView.setManaged(false);
        }
        
        // Initialize year ComboBox
        if (yearComboBox != null) {
            updateYearComboBox();
            
            // Center the text in the ComboBox
            yearComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                @Override
                public ListCell<String> call(ListView<String> param) {
                    return new ListCell<String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                            } else {
                                setText(item);
                                setAlignment(javafx.geometry.Pos.CENTER);
                            }
                        }
                    };
                }
            });
            
            // Center the selected item display
            yearComboBox.setButtonCell(new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setAlignment(javafx.geometry.Pos.CENTER);
                    }
                }
            });
        }
        
        // Initialize home edit button visibility
        updateHomeEditButtonVisibility();
        
        // Initialize data management revenues table
        if (dmRevenuesTable != null && revCategoryColumn != null) {
            revCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            revAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            revPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
            revChangeColumn.setCellValueFactory(param -> {
                CategoryData data = param.getValue();
                return new javafx.beans.property.SimpleStringProperty(data.getChangeFromPrevious());
            });
            revStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            if (revCommentsColumn != null) {
                revCommentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));
                revCommentsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                revCommentsColumn.setOnEditCommit(event -> {
                    CategoryData data = event.getRowValue();
                    data.setComments(event.getNewValue());
                });
                dmRevenuesTable.setEditable(true);
            }
            
            setupTableColumnFormatting(revAmountColumn, revPercentageColumn, revStatusColumn);
            setupTableSelection(dmRevenuesTable);
        }
        
        // Initialize data management expenses table
        if (dmExpensesTable != null && expCategoryColumn != null) {
            expCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            expAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            expPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
            expChangeColumn.setCellValueFactory(param -> {
                CategoryData data = param.getValue();
                return new javafx.beans.property.SimpleStringProperty(data.getChangeFromPrevious());
            });
            expStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            if (expCommentsColumn != null) {
                expCommentsColumn.setCellValueFactory(new PropertyValueFactory<>("comments"));
                expCommentsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                expCommentsColumn.setOnEditCommit(event -> {
                    CategoryData data = event.getRowValue();
                    data.setComments(event.getNewValue());
                });
                dmExpensesTable.setEditable(true);
            }
            
            setupTableColumnFormatting(expAmountColumn, expPercentageColumn, expStatusColumn);
            setupTableSelection(dmExpensesTable);
        }
        
        // Initialize old data management table (for backward compatibility)
        if (dataManagementTable != null) {
            dmCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            dmAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            dmPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
            dmTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            dmChangeColumn.setCellValueFactory(param -> {
                CategoryData data = param.getValue();
                return new javafx.beans.property.SimpleStringProperty(data.getChangeFromPrevious());
            });
            dmStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            
            // Format amount column
            dmAmountColumn.setCellFactory(column -> new TableCell<CategoryData, Double>() {
                @Override
                protected void updateItem(Double amount, boolean empty) {
                    super.updateItem(amount, empty);
                    if (empty || amount == null) {
                        setText(null);
                    } else {
                        setText(AmountFormatter.formatCurrency(amount));
                    }
                }
            });
            
            // Format percentage column
            dmPercentageColumn.setCellFactory(column -> new TableCell<CategoryData, Double>() {
                @Override
                protected void updateItem(Double percentage, boolean empty) {
                    super.updateItem(percentage, empty);
                    if (empty || percentage == null) {
                        setText(null);
                    } else {
                        setText(AmountFormatter.formatPercentage(percentage));
                    }
                }
            });
            
            // Format status column with colors
            dmStatusColumn.setCellFactory(column -> new TableCell<CategoryData, String>() {
                @Override
                protected void updateItem(String status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty || status == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(status);
                        switch (status) {
                            case "Αύξηση":
                                setStyle("-fx-text-fill: #059669; -fx-font-weight: bold;");
                                break;
                            case "Μείωση":
                                setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                                break;
                            case "Νέο":
                                setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");
                                break;
                            default:
                                setStyle("-fx-text-fill: #6b7280;");
                        }
                    }
                }
            });
            
            // Enable/disable edit/delete buttons based on selection and year
            dataManagementTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean hasSelection = newSelection != null;
                boolean isEditable = false;
                
                if (hasSelection) {
                    int year = extractYearFromData(newSelection);
                    isEditable = isYearEditable(year);
                }
                
                if (editDataButton != null) editDataButton.setDisable(!hasSelection || !isEditable);
                if (deleteDataButton != null) deleteDataButton.setDisable(!hasSelection || !isEditable);
                if (bulkEditButton != null) {
                    int selectedCount = dataManagementTable.getSelectionModel().getSelectedItems().size();
                    bulkEditButton.setDisable(selectedCount == 0);
                }
            });
            
            // Enable multi-selection
            dataManagementTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
        
        // Initialize search and filter
        if (dmSearchField != null) {
            dmSearchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }
        
        if (dmTypeFilter != null) {
            dmTypeFilter.getItems().addAll("Όλα", "Έσοδο", "Δαπάνη", "Υπουργείο", "Αποκεντρωμένη Διοίκηση");
            dmTypeFilter.setValue("Όλα");
            dmTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }
        
        // Initialize data management year ComboBox
        if (dataManagementYearComboBox != null) {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            dataManagementYearComboBox.getItems().addAll(
                String.valueOf(currentYear), 
                String.valueOf(currentYear + 1)
            );
            dataManagementYearComboBox.setValue(String.valueOf(currentYear));
        }

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
                    setText(AmountFormatter.formatCurrency(amount));
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
                    setText(AmountFormatter.formatPercentageOneDecimal(percentage));
                }
            }
        });
        
        // Initialize revenues table
        revenueCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        revenueAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        revenuePercentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        
        revenueAmountColumn.setCellFactory(column -> new TableCell<CategoryData, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(AmountFormatter.formatCurrency(amount));
                }
            }
        });
        
        revenuePercentageColumn.setCellFactory(column -> new TableCell<CategoryData, Double>() {
            @Override
            protected void updateItem(Double percentage, boolean empty) {
                super.updateItem(percentage, empty);
                if (empty || percentage == null) {
                    setText(null);
                } else {
                    setText(AmountFormatter.formatPercentageOneDecimal(percentage));
                }
            }
        });
        
        // Initialize expenses table
        expenseCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        expenseAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        expensePercentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        
        expenseAmountColumn.setCellFactory(column -> new TableCell<CategoryData, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(AmountFormatter.formatCurrency(amount));
                }
            }
        });
        
        expensePercentageColumn.setCellFactory(column -> new TableCell<CategoryData, Double>() {
            @Override
            protected void updateItem(Double percentage, boolean empty) {
                super.updateItem(percentage, empty);
                if (empty || percentage == null) {
                    setText(null);
                } else {
                    setText(AmountFormatter.formatPercentageOneDecimal(percentage));
                }
            }
        });
        
        // Initialize administrations table
        adminCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        adminAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        adminPercentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        
        adminAmountColumn.setCellFactory(column -> new TableCell<CategoryData, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(AmountFormatter.formatCurrency(amount));
                }
            }
        });
        
        adminPercentageColumn.setCellFactory(column -> new TableCell<CategoryData, Double>() {
            @Override
            protected void updateItem(Double percentage, boolean empty) {
                super.updateItem(percentage, empty);
                if (empty || percentage == null) {
                    setText(null);
                } else {
                    setText(AmountFormatter.formatPercentageOneDecimal(percentage));
                }
            }
        });

        // Load initial data
        updateDataForYear();
        
        // Set up quick navigation cards
        setupQuickNavigation();
        
        // Show home view 
        showView(homeView);
    }
    
    private void updateAuthButton() {
        if (authMenuItem != null) {
            if (currentUserType == UserType.CITIZEN) {
                authMenuItem.setText("Σύνδεση ως Κυβέρνηση");
            } else {
                authMenuItem.setText("Αποσύνδεση");
            }
        }
    }
    
    private void updateUserTypeLabel() {
        if (userTypeLabel != null) {
            if (currentUserType == UserType.CITIZEN) {
                userTypeLabel.setText("Προβολή Πολίτη");
                if (userTypeIconLabel != null) {
                    userTypeIconLabel.setText("👤");
                    userTypeIconLabel.setStyle("-fx-font-size: 18px;");
                }
            } else {
                userTypeLabel.setText("Προβολή Κυβέρνησης");
                if (userTypeIconLabel != null) {
                    userTypeIconLabel.setText("🏢");
                    userTypeIconLabel.setStyle("-fx-font-size: 18px;");
                }
            }
        }
    }
    
    private void updateYearComboBox() {
        if (yearComboBox != null) {
            yearComboBox.getItems().clear();
            
            Set<Integer> publishedYears = getPublishedYears();
            
            // For citizens: only show published years
            // For government: show all years (2023-2027) but mark unpublished ones
            if (isGovernmentUser()) {
                // Government sees all years
                yearComboBox.getItems().addAll("2023", "2024", "2025", "2026");
                yearComboBox.getItems().add("2027");
            } else {
                // Citizens only see published years
                for (int year = 2023; year <= 2027; year++) {
                    if (publishedYears.contains(year)) {
                        yearComboBox.getItems().add(String.valueOf(year));
                    }
                }
            }
            
            // Set default value to current year (2026) if available
            String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            if (yearComboBox.getItems().contains(currentYear)) {
                yearComboBox.setValue(currentYear);
                selectedYear = currentYear;
            } else if (!yearComboBox.getItems().isEmpty()) {
                // Fallback to first available year if current year not in list
                yearComboBox.setValue(yearComboBox.getItems().get(0));
                selectedYear = yearComboBox.getItems().get(0);
            }
        }
    }
    
    // Published Years Management
    
    private void initializePublishedYearsTable() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement()) {
            
            String sql = "CREATE TABLE IF NOT EXISTS published_years (" +
                        "year INTEGER PRIMARY KEY)";
            stmt.execute(sql);
            
            // Initialize with default published years (2023-2026)
            Set<Integer> existingYears = getPublishedYears();
            for (int year = 2023; year <= 2026; year++) {
                if (!existingYears.contains(year)) {
                    String insertSql = "INSERT OR IGNORE INTO published_years (year) VALUES (" + year + ")";
                    stmt.execute(insertSql);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Set<Integer> getPublishedYears() {
        Set<Integer> publishedYears = new HashSet<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT year FROM published_years")) {
            
            while (rs.next()) {
                publishedYears.add(rs.getInt("year"));
            }
        } catch (Exception e) {
            // Table might not exist yet, initialize it
            initializePublishedYearsTable();
            // Retry after initialization
            try (Connection connection = DatabaseConnection.getConnection();
                 Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT year FROM published_years")) {
                
                while (rs.next()) {
                    publishedYears.add(rs.getInt("year"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return publishedYears;
    }
    
    private boolean isYearPublished(int year) {
        return getPublishedYears().contains(year);
    }
    
    /**
     * Gets the list of years that have data in the database by checking which revenue tables exist.
     * @return List of year strings (e.g., ["2023", "2024", "2025"])
     */
    private List<String> getAvailableYearsFromDatabase() {
        List<String> availableYears = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            
            // Check for revenue tables (revenue_YYYY) from 2020 to 2030
            for (int year = 2020; year <= 2030; year++) {
                String tableName = "revenue_" + year;
                try (ResultSet tables = meta.getTables(null, null, tableName, null)) {
                    if (tables.next()) {
                        // Table exists, verify it has data
                        try (Statement stmt = connection.createStatement();
                             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM " + tableName)) {
                            if (rs.next() && rs.getInt("count") > 0) {
                                availableYears.add(String.valueOf(year));
                            }
                        } catch (SQLException e) {
                            // Table exists but might be empty or have issues, skip it
                            continue;
                        }
                    }
                } catch (SQLException e) {
                    // Table doesn't exist, continue to next year
                    continue;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // If there's an error, return at least the current year and previous year as fallback
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            availableYears.add(String.valueOf(currentYear - 1));
            availableYears.add(String.valueOf(currentYear));
        }
        
        // Sort years in descending order (newest first)
        availableYears.sort((a, b) -> Integer.compare(Integer.parseInt(b), Integer.parseInt(a)));
        
        return availableYears;
    }
    
    private void publishYear(int year) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement()) {
            
            String sql = "INSERT OR IGNORE INTO published_years (year) VALUES (" + year + ")";
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateGovernmentFeatures() {
        // Update year ComboBox to include next year for government users
        updateYearComboBox();
        // Update home edit button visibility
        updateHomeEditButtonVisibility();
    }
    
    @FXML
    private void onAuthButtonClicked() {
        if (currentUserType == UserType.CITIZEN) {
            // Show government login dialog
            showGovernmentLogin();
        } else {
            // Logout - go back to login screen as citizen
            currentUserType = UserType.CITIZEN;
            updateUserTypeLabel();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/LoginView.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 1200, 700);
                scene.getStylesheets().add(getClass().getResource("/ui/styles.css").toExternalForm());
                
                Stage stage = null;
                if (authMenuItem != null && authMenuItem.getParentPopup() != null) {
                    stage = (Stage) authMenuItem.getParentPopup().getOwnerWindow();
                } else if (yearComboBox != null && yearComboBox.getScene() != null) {
                    stage = (Stage) yearComboBox.getScene().getWindow();
                } else if (homeView != null && homeView.getScene() != null) {
                    stage = (Stage) homeView.getScene().getWindow();
                }
                stage.setScene(scene);
                stage.setTitle("Κρατικός Προϋπολογισμός - Σύστημα Ανάλυσης");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void showGovernmentLogin() {
        try {
            // Create login dialog
            Stage loginStage = new Stage();
            loginStage.setTitle("Σύνδεση - Κυβέρνηση");
            loginStage.setResizable(false);

            javafx.scene.layout.VBox loginPane = new javafx.scene.layout.VBox(20);
            loginPane.setPadding(new javafx.geometry.Insets(40));
            loginPane.setStyle("-fx-background-color: white;");

            Label titleLabel = new Label("Σύνδεση Κυβέρνησης");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e40af;");

            // Username field with validation
            TextField usernameField = new TextField();
            usernameField.setPrefWidth(300);
            usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            
            Label usernamePrompt = new Label("Όνομα χρήστη");
            usernamePrompt.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af;");
            usernamePrompt.setMouseTransparent(true);
            usernamePrompt.setPadding(new javafx.geometry.Insets(0, 0, 0, 10));
            
            Label usernameErrorLabel = new Label();
            usernameErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
            usernameErrorLabel.setVisible(false);
            usernameErrorLabel.setWrapText(true);
            usernameErrorLabel.setPrefWidth(300);
            
            javafx.scene.layout.StackPane usernamePane = new javafx.scene.layout.StackPane();
            usernamePane.setPrefWidth(300);
            usernamePane.getChildren().addAll(usernameField, usernamePrompt);
            javafx.scene.layout.StackPane.setAlignment(usernamePrompt, javafx.geometry.Pos.CENTER_LEFT);
            
            usernameField.textProperty().addListener((obs, oldText, newText) -> {
                usernamePrompt.setVisible(newText == null || newText.isEmpty());
                // Real-time validation
                Constraints.ValidationResult result = Constraints.validateUsername(newText);
                Constraints.applyValidationStyle(usernameField, result.isValid(), usernameErrorLabel, result.getErrorMessage());
            });

            // Password field with validation
            PasswordField passwordField = new PasswordField();
            passwordField.setPrefWidth(300);
            passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            
            Label passwordPrompt = new Label("Κωδικός πρόσβασης");
            passwordPrompt.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af;");
            passwordPrompt.setMouseTransparent(true);
            passwordPrompt.setPadding(new javafx.geometry.Insets(0, 0, 0, 10));
            
            Label passwordErrorLabel = new Label();
            passwordErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
            passwordErrorLabel.setVisible(false);
            passwordErrorLabel.setWrapText(true);
            passwordErrorLabel.setPrefWidth(300);
            
            javafx.scene.layout.StackPane passwordPane = new javafx.scene.layout.StackPane();
            passwordPane.setPrefWidth(300);
            passwordPane.getChildren().addAll(passwordField, passwordPrompt);
            javafx.scene.layout.StackPane.setAlignment(passwordPrompt, javafx.geometry.Pos.CENTER_LEFT);
            
            passwordField.textProperty().addListener((obs, oldText, newText) -> {
                passwordPrompt.setVisible(newText == null || newText.isEmpty());
                // Real-time validation
                Constraints.ValidationResult result = Constraints.validatePassword(newText);
                Constraints.applyValidationStyle(passwordField, result.isValid(), passwordErrorLabel, result.getErrorMessage());
            });

            javafx.scene.control.Button loginButton = new javafx.scene.control.Button("Σύνδεση");
            loginButton.setPrefWidth(300);
            loginButton.setPrefHeight(40);
            loginButton.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            loginButton.setOnAction(e -> {
                String username = usernameField.getText();
                String password = passwordField.getText();
                
                // Validate inputs
                Constraints.ValidationResult usernameResult = Constraints.validateUsername(username);
                Constraints.ValidationResult passwordResult = Constraints.validatePassword(password);
                
                // Apply validation styles
                Constraints.applyValidationStyle(usernameField, usernameResult.isValid(), usernameErrorLabel, usernameResult.getErrorMessage());
                Constraints.applyValidationStyle(passwordField, passwordResult.isValid(), passwordErrorLabel, passwordResult.getErrorMessage());
                
                // Check if all validations pass
                if (usernameResult.isValid() && passwordResult.isValid()) {
                    // Check credentials against database
                    if (authentication.checkLogin(username, password)) {
                    // Set user type to government
                            currentUserType = UserType.GOVERNMENT;
                            updateAuthButton();
                            updateUserTypeLabel();
                            updateGovernmentFeatures();
                    
                    // Close login dialog
                    loginStage.close();
                    } else {
                        // Invalid credentials
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Σφάλμα Σύνδεσης");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Λάθος όνομα χρήστη ή κωδικός πρόσβασης.\nΠαρακαλώ προσπαθήστε ξανά.");
                        errorAlert.showAndWait();
                    }
                } else {
                    // Show error alert if validation fails
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Σφάλμα Επικύρωσης");
                    alert.setHeaderText("Παρακαλώ διορθώστε τα σφάλματα");
                    StringBuilder errorMsg = new StringBuilder();
                    if (!usernameResult.isValid()) {
                        errorMsg.append("• ").append(usernameResult.getErrorMessage()).append("\n");
                    }
                    if (!passwordResult.isValid()) {
                        errorMsg.append("• ").append(passwordResult.getErrorMessage()).append("\n");
                    }
                    alert.setContentText(errorMsg.toString());
                    alert.showAndWait();
                }
            });

            // Sign up button
            javafx.scene.control.Button signUpButton = new javafx.scene.control.Button("Εγγραφή");
            signUpButton.setPrefWidth(300);
            signUpButton.setPrefHeight(40);
            signUpButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            signUpButton.setOnAction(e -> {
                showSignUpDialog(loginStage);
            });

            loginPane.getChildren().addAll(titleLabel, usernamePane, usernameErrorLabel, passwordPane, passwordErrorLabel, loginButton, signUpButton);

            Scene loginScene = new Scene(loginPane, 400, 450);
            loginStage.setScene(loginScene);
            Window ownerWindow = null;
            if (authMenuItem != null && authMenuItem.getParentPopup() != null) {
                ownerWindow = authMenuItem.getParentPopup().getOwnerWindow();
            } else if (yearComboBox != null && yearComboBox.getScene() != null) {
                ownerWindow = yearComboBox.getScene().getWindow();
            } else if (homeView != null && homeView.getScene() != null) {
                ownerWindow = homeView.getScene().getWindow();
            }
            loginStage.initOwner(ownerWindow);
            loginStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showSignUpDialog(Stage parentStage) {
        try {
            Stage signUpStage = new Stage();
            signUpStage.setTitle("Εγγραφή - Κυβέρνηση");
            signUpStage.setResizable(false);

            javafx.scene.layout.VBox signUpPane = new javafx.scene.layout.VBox(20);
            signUpPane.setPadding(new javafx.geometry.Insets(40));
            signUpPane.setStyle("-fx-background-color: white;");

            Label titleLabel = new Label("Εγγραφή Νέου Χρήστη");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #10b981;");

            // Username field
            TextField usernameField = new TextField();
            usernameField.setPrefWidth(300);
            usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            usernameField.setPromptText("Όνομα χρήστη");

            Label usernameErrorLabel = new Label();
            usernameErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
            usernameErrorLabel.setVisible(false);
            usernameErrorLabel.setWrapText(true);
            usernameErrorLabel.setPrefWidth(300);

            usernameField.textProperty().addListener((obs, oldText, newText) -> {
                Constraints.ValidationResult result = Constraints.validateUsername(newText);
                Constraints.applyValidationStyle(usernameField, result.isValid(), usernameErrorLabel, result.getErrorMessage());
            });

            // Password field
            PasswordField passwordField = new PasswordField();
            passwordField.setPrefWidth(300);
            passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            passwordField.setPromptText("Κωδικός πρόσβασης");

            Label passwordErrorLabel = new Label();
            passwordErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
            passwordErrorLabel.setVisible(false);
            passwordErrorLabel.setWrapText(true);
            passwordErrorLabel.setPrefWidth(300);

            passwordField.textProperty().addListener((obs, oldText, newText) -> {
                Constraints.ValidationResult result = Constraints.validatePassword(newText);
                Constraints.applyValidationStyle(passwordField, result.isValid(), passwordErrorLabel, result.getErrorMessage());
            });

            // Sign up button
            javafx.scene.control.Button createButton = new javafx.scene.control.Button("Δημιουργία Λογαριασμού");
            createButton.setPrefWidth(300);
            createButton.setPrefHeight(40);
            createButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            createButton.setOnAction(e -> {
                String username = usernameField.getText();
                String password = passwordField.getText();

                // Validate
                Constraints.ValidationResult usernameResult = Constraints.validateUsername(username);
                Constraints.ValidationResult passwordResult = Constraints.validatePassword(password);

                Constraints.applyValidationStyle(usernameField, usernameResult.isValid(), usernameErrorLabel, usernameResult.getErrorMessage());
                Constraints.applyValidationStyle(passwordField, passwordResult.isValid(), passwordErrorLabel, passwordResult.getErrorMessage());

                if (usernameResult.isValid() && passwordResult.isValid()) {
                    // Check if username exists
                    if (authentication.usernameExists(username)) {
                        usernameErrorLabel.setText("Το όνομα χρήστη υπάρχει ήδη");
                        usernameErrorLabel.setVisible(true);
                        usernameField.setStyle("-fx-border-color: #dc2626; -fx-font-size: 14px; -fx-padding: 10;");
                    } else {
                        // Create user
                        if (authentication.saveUser(username, password)) {
                            Alert success = new Alert(Alert.AlertType.INFORMATION);
                            success.setTitle("Επιτυχία");
                            success.setHeaderText(null);
                            success.setContentText("Ο λογαριασμός δημιουργήθηκε επιτυχώς!\nΚάντε login για να συνεχίσετε.");
                            success.showAndWait();
                            signUpStage.close();
                        } else {
                            Alert error = new Alert(Alert.AlertType.ERROR);
                            error.setTitle("Σφάλμα");
                            error.setHeaderText(null);
                            error.setContentText("Δεν ήταν δυνατή η δημιουργία του λογαριασμού.\nΠαρακαλώ προσπαθήστε ξανά.");
                            error.showAndWait();
                        }
                    }
                }
            });

            signUpPane.getChildren().addAll(titleLabel, usernameField, usernameErrorLabel, passwordField, passwordErrorLabel, createButton);

            Scene signUpScene = new Scene(signUpPane, 400, 400);
            signUpStage.setScene(signUpScene);
            signUpStage.initOwner(parentStage);
            signUpStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            signUpStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Σφάλμα");
            error.setHeaderText(null);
            error.setContentText("Δεν ήταν δυνατή η προβολή της σελίδας εγγραφής.");
            error.showAndWait();
        }
    }
    
    private void setupQuickNavigation() {
        if (quickNavMinistries != null) {
            quickNavMinistries.setOnMouseClicked(e -> onNavigateMinistries());
            quickNavMinistries.setCursor(javafx.scene.Cursor.HAND);
        }
        if (quickNavRevenues != null) {
            quickNavRevenues.setOnMouseClicked(e -> onNavigateRevenues());
            quickNavRevenues.setCursor(javafx.scene.Cursor.HAND);
        }
        if (quickNavExpenses != null) {
            quickNavExpenses.setOnMouseClicked(e -> onNavigateExpenses());
            quickNavExpenses.setCursor(javafx.scene.Cursor.HAND);
        }
        if (quickNavAdministrations != null) {
            quickNavAdministrations.setOnMouseClicked(e -> onNavigateAdministrations());
            quickNavAdministrations.setCursor(javafx.scene.Cursor.HAND);
        }
    }

    @FXML
    private void onYearSelected(javafx.event.ActionEvent event) {
        if (yearComboBox != null && yearComboBox.getValue() != null) {
            selectedYear = yearComboBox.getValue();
            updateHomeEditButtonVisibility();
            updateDataForYear();
        }
    }
    
    private void updateHomeEditButtonVisibility() {
        if (homeEditButton != null) {
            boolean isGovernment = isGovernmentUser();
            if (isGovernment && selectedYear != null) {
                try {
                    int year = Integer.parseInt(selectedYear);
                    homeEditButton.setVisible(true);
                    homeEditButton.setManaged(true);
                    boolean isEditable = year >= 2026;
                    homeEditButton.setDisable(!isEditable);
                } catch (NumberFormatException e) {
                    homeEditButton.setVisible(false);
                    homeEditButton.setManaged(false);
                }
            } else {
                homeEditButton.setVisible(false);
                homeEditButton.setManaged(false);
            }
        }
    }
    
    private void updateDataForYear() {
        if (selectedYear == null) return;

        // Update header title with year
        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Κρατικός Προϋπολογισμός του " + selectedYear);
        }
        
        // Update summary cards
        updateSummaryCards(selectedYear);
        
        // Update all tables
        updateRevenuesTable();
        updateExpensesTable();
        updateAdministrationsTable();
        updateCharts(Integer.parseInt(selectedYear));
        
        // Update Charts in detailed views if they are visible
        int year = Integer.parseInt(selectedYear);
        if (revenuesView != null && revenuesView.isVisible()) {
            if (pieRevenue != null) {
                Charts.fillPieChart(pieRevenue, budgetData.getRevenueBreakdownForGraphs(year), "Έσοδα " + selectedYear);
            }
            if (barRevenueTop != null) {
                Charts.loadBarChartForTopCategories(barRevenueTop, budgetData.getRevenueBreakdownForGraphs(year), "Κορυφαίες Κατηγορίες", 10);
            }
            if (areaRevenueTrend != null) {
                Charts.loadAreaChart(areaRevenueTrend, budgetData, "total_revenue", "Διαχρονική Εξέλιξη Εσόδων");
            }
            if (stackedRevenue != null) {
                Charts.loadStackedBarChart(stackedRevenue, budgetData, "revenue", "Σύνθεση Εσόδων ανά Έτος");
            }
        }
        if (expensesView != null && expensesView.isVisible()) {
            if (pieExpenses != null) {
                Charts.fillPieChart(pieExpenses, budgetData.getExpenseBreakdownForGraphs(year), "Έξοδα " + selectedYear);
            }
            if (barExpenseTop != null) {
                Charts.loadBarChartForTopCategories(barExpenseTop, budgetData.getExpenseBreakdownForGraphs(year), "Κορυφαίες Κατηγορίες", 10);
            }
            if (areaExpenseTrend != null) {
                Charts.loadAreaChart(areaExpenseTrend, budgetData, "total_expenses", "Διαχρονική Εξέλιξη Δαπανών");
            }
            if (stackedExpense != null) {
                Charts.loadStackedBarChart(stackedExpense, budgetData, "expense", "Σύνθεση Δαπανών ανά Έτος");
            }
        }
        if (ministriesView != null && ministriesView.isVisible()) {
            if (pieMinistries != null) {
                Charts.loadTopPieChart(pieMinistries, budgetData.getMinistriesBreakdown(year), "Κορυφαία Υπουργεία " + selectedYear, 3);
            }
            if (areaMinistriesTrend != null) {
                Charts.loadAreaChart(areaMinistriesTrend, budgetData, "total_expenses", "Διαχρονική Εξέλιξη Δαπανών Υπουργείων");
            }
        }
        if (administrationsView != null && administrationsView.isVisible()) {
            if (pieAdministrations != null) {
                Charts.fillPieChart(pieAdministrations, budgetData.getDecentralizedAdministrationsBreakdown(year), "Αποκεντρωμένες Διοικήσεις " + selectedYear);
            }
            if (barAdministrationsTop != null) {
                Charts.loadBarChartForTopCategories(barAdministrationsTop, budgetData.getDecentralizedAdministrationsBreakdown(year), "Κορυφαίες Διοικήσεις", 7);
            }
            if (areaAdministrationsTrend != null) {
                Charts.loadAreaChart(areaAdministrationsTrend, budgetData, "total_expenses", "Διαχρονική Εξέλιξη Δαπανών Αποκεντρωμένων Διοικήσεων");
            }
        }
    }

    private void updateSummaryCards(String year) {
        int yearInt = Integer.parseInt(year);
        
        // Load real data from service
        double totalRevenues = budgetData.getTotalRevenues(yearInt);
        double totalExpenses = budgetData.getTotalExpenses(yearInt);
        double balance = budgetData.getBalance(yearInt);
        double revenuesChange = budgetData.getRevenuesChange(yearInt);
        double expensesChange = budgetData.getExpensesChange(yearInt);
        
        // Update revenues
        totalRevenuesLabel.setText(AmountFormatter.formatCurrency(totalRevenues));
        String revenuesChangeText = AmountFormatter.formatPercentageChangeOneDecimal(revenuesChange) + " από πέρυσι";
        revenuesDeltaLabel.setText(revenuesChangeText);
        revenuesDeltaLabel.getStyleClass().removeAll("negative", "positive");
        revenuesDeltaLabel.getStyleClass().add(revenuesChange >= 0 ? "positive" : "negative");
        revenuesDeltaLabel.setStyle("-fx-text-fill: white;");
        
        // Update expenses
        totalExpensesLabel.setText(AmountFormatter.formatCurrency(totalExpenses));
        String expensesChangeText = AmountFormatter.formatPercentageChangeOneDecimal(expensesChange) + " από πέρυσι";
        expensesDeltaLabel.setText(expensesChangeText);
        expensesDeltaLabel.getStyleClass().removeAll("negative", "positive");
        expensesDeltaLabel.getStyleClass().add(expensesChange >= 0 ? "positive" : "negative");
        expensesDeltaLabel.setStyle("-fx-text-fill: white;");
        
        // Update balance
        if (balance >= 0) {
            balanceLabel.setText("+" + AmountFormatter.formatCurrency(balance));
            balanceStatusLabel.setText("Πλεόνασμα");
            balanceStatusLabel.getStyleClass().removeAll("negative");
            balanceStatusLabel.getStyleClass().add("positive");
            balanceStatusLabel.setStyle("-fx-text-fill: white;");
        } else {
            balanceLabel.setText(AmountFormatter.formatCurrency(balance));
            balanceStatusLabel.setText("Έλλειμμα");
            balanceStatusLabel.getStyleClass().removeAll("positive");
            balanceStatusLabel.getStyleClass().add("negative");
            balanceStatusLabel.setStyle("-fx-text-fill: white;");
        }
    }


    private void updateCategoryTable() {
        if (selectedYear == null) return;
        
        int yearInt = Integer.parseInt(selectedYear);
        List<BudgetData.CategoryInfo> categories = budgetData.getCategories(yearInt);
        
        // Get previous year data for comparison
        List<BudgetData.CategoryInfo> prevCategories = budgetData.getCategories(yearInt - 1);
        boolean hasPreviousYearData = !prevCategories.isEmpty();
        Map<String, Double> prevYearMap = new HashMap<>();
        for (BudgetData.CategoryInfo cat : prevCategories) {
            prevYearMap.put(cat.getName(), cat.getAmount());
        }
        
        // Convert to table data
        ObservableList<CategoryData> tableData = FXCollections.observableArrayList();
        for (BudgetData.CategoryInfo cat : categories) {
            String changeText;
            if (!hasPreviousYearData || !prevYearMap.containsKey(cat.getName())) {
                changeText = "-";
            } else {
                double prevAmount = prevYearMap.get(cat.getName());
                if (prevAmount > 0) {
                    double change = StatisticalAnalysis.calculatePercentageChange(cat.getAmount(), prevAmount);
                    changeText = AmountFormatter.formatPercentageChangeOneDecimal(change);
                } else {
                    changeText = "-";
                }
            }
            tableData.add(new CategoryData(cat.getName(), cat.getAmount(), cat.getPercentage(), changeText));
        }
        
        categoryTable.setItems(tableData);
    }
    
    private void updateRevenuesTable() {
        if (selectedYear == null) return;
        
        int yearInt = Integer.parseInt(selectedYear);
        List<BudgetData.CategoryInfo> revenues = budgetData.getRevenueBreakdown(yearInt);
        
        ObservableList<CategoryData> tableData = FXCollections.observableArrayList();
        for (BudgetData.CategoryInfo revenue : revenues) {
            tableData.add(new CategoryData(revenue.getName(), revenue.getAmount(), revenue.getPercentage(), ""));
        }
        
        revenuesTable.setItems(tableData);
    }
    
    private void updateExpensesTable() {
        if (selectedYear == null) return;
        
        int yearInt = Integer.parseInt(selectedYear);
        List<BudgetData.CategoryInfo> expenses = budgetData.getExpensesBreakdown(yearInt);
        
        ObservableList<CategoryData> tableData = FXCollections.observableArrayList();
        for (BudgetData.CategoryInfo expense : expenses) {
            tableData.add(new CategoryData(expense.getName(), expense.getAmount(), expense.getPercentage(), ""));
        }
        
        expensesTable.setItems(tableData);
    }
    
    private void updateAdministrationsTable() {
        if (selectedYear == null) return;
        
        int yearInt = Integer.parseInt(selectedYear);
        List<BudgetData.CategoryInfo> administrations = budgetData.getDecentralizedAdministrations(yearInt);
        
        ObservableList<CategoryData> tableData = FXCollections.observableArrayList();
        for (BudgetData.CategoryInfo admin : administrations) {
            tableData.add(new CategoryData(admin.getName(), admin.getAmount(), admin.getPercentage(), ""));
        }
        
        administrationsTable.setItems(tableData);
    }

    @FXML
    private void onNavigateHome() {
        showView(homeView);
    }

    @FXML
    private void onNavigateMinistries() {
        showView(ministriesView);
        updateCategoryTable(); // Update the category table for ministries view
        if (selectedYear != null) {
            int year = Integer.parseInt(selectedYear);
            // Update ministries pie chart
            if (pieMinistries != null) {
                Charts.loadTopPieChart(pieMinistries, budgetData.getMinistriesBreakdown(year), "Κορυφαία Υπουργεία " + selectedYear, 3);
            }
            // Update area chart - trend
            if (areaMinistriesTrend != null) {
                Charts.loadAreaChart(areaMinistriesTrend, budgetData, "total_expenses", "Διαχρονική Εξέλιξη Δαπανών Υπουργείων");
            }
        }
    }

    @FXML
    private void onNavigateRevenues() {
        showView(revenuesView);
        if (selectedYear != null) {
            int year = Integer.parseInt(selectedYear);
            // Update revenues pie chart
            if (pieRevenue != null) {
                Charts.fillPieChart(pieRevenue, budgetData.getRevenueBreakdownForGraphs(year), "Έσοδα " + selectedYear);
            }
            // Update bar chart - top categories
            if (barRevenueTop != null) {
                Charts.loadBarChartForTopCategories(barRevenueTop, budgetData.getRevenueBreakdownForGraphs(year), "Κορυφαίες Κατηγορίες", 10);
            }
            // Update area chart - trend
            if (areaRevenueTrend != null) {
                Charts.loadAreaChart(areaRevenueTrend, budgetData, "total_revenue", "Διαχρονική Εξέλιξη Εσόδων");
            }
            // Update stacked bar chart
            if (stackedRevenue != null) {
                Charts.loadStackedBarChart(stackedRevenue, budgetData, "revenue", "Σύνθεση Εσόδων ανά Έτος");
            }
        }
    }

    @FXML
    private void onNavigateExpenses() {
        showView(expensesView);
        if (selectedYear != null) {
            int year = Integer.parseInt(selectedYear);
            // Update expenses pie chart
            if (pieExpenses != null) {
                Charts.fillPieChart(pieExpenses, budgetData.getExpenseBreakdownForGraphs(year), "Έξοδα " + selectedYear);
            }
            // Update bar chart - top categories
            if (barExpenseTop != null) {
                Charts.loadBarChartForTopCategories(barExpenseTop, budgetData.getExpenseBreakdownForGraphs(year), "Κορυφαίες Κατηγορίες", 10);
            }
            // Update area chart - trend
            if (areaExpenseTrend != null) {
                Charts.loadAreaChart(areaExpenseTrend, budgetData, "total_expenses", "Διαχρονική Εξέλιξη Δαπανών");
            }
            // Update stacked bar chart
            if (stackedExpense != null) {
                Charts.loadStackedBarChart(stackedExpense, budgetData, "expense", "Σύνθεση Δαπανών ανά Έτος");
            }
        }
    }

    @FXML
    private void onNavigateAdministrations() {
        showView(administrationsView);
        if (selectedYear != null) {
            int year = Integer.parseInt(selectedYear);
            // Update administrations pie chart
            if (pieAdministrations != null) {
                Charts.fillPieChart(pieAdministrations, budgetData.getDecentralizedAdministrationsBreakdown(year), "Αποκεντρωμένες Διοικήσεις " + selectedYear);
            }
            // Update bar chart - top administrations
            if (barAdministrationsTop != null) {
                Charts.loadBarChartForTopCategories(barAdministrationsTop, budgetData.getDecentralizedAdministrationsBreakdown(year), "Κορυφαίες Διοικήσεις", 7);
            }
            // Update area chart - trend (using total_da if available, otherwise total_expenses)
            if (areaAdministrationsTrend != null) {
                // For administrations, we'll use total_expenses as proxy since they are part of expenses
                Charts.loadAreaChart(areaAdministrationsTrend, budgetData, "total_expenses", "Διαχρονική Εξέλιξη Δαπανών Αποκεντρωμένων Διοικήσεων");
            }
        }
    }
    
    @FXML
    private void onNavigateDataExploration() {
        if (dataExplorationView == null) {
            // If exploration view is not loaded, show error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Δεν ήταν δυνατή η φόρτωση της προβολής εξερεύνησης");
            alert.setContentText("Παρακαλώ δοκιμάστε ξανά.");
            alert.showAndWait();
            return;
        }
        showView(dataExplorationView);
        initializeDataExploration();
    }
    
    private void initializeDataExploration() {
        try {
            if (exploreYearComboBox != null && exploreYearComboBox.getItems().isEmpty()) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                for (int year = 2023; year <= currentYear + 1; year++) {
                    exploreYearComboBox.getItems().add(String.valueOf(year));
                }
                exploreYearComboBox.setValue(String.valueOf(currentYear));
            }
            
            // Set default view to "Total" (Συνολικά Έσοδα/Δαπάνες)
            resetExplorationButtons();
            if (exploreTotalButton != null) {
                exploreTotalButton.getStyleClass().add("exploration-category-button-active");
            }
            currentExplorationView = "total";
            loadTotalView();
            
            if (exploreResultsTable != null && exploreColumn1 != null) {
                // Initialize columns - will be updated dynamically based on view type
                exploreColumn1.setCellValueFactory(new PropertyValueFactory<>("category"));
                if (exploreColumn2 != null) {
                    exploreColumn2.setCellValueFactory(new PropertyValueFactory<>("amount"));
                }
                if (exploreColumn3 != null) {
                    exploreColumn3.setCellValueFactory(new PropertyValueFactory<>("change"));
                }
                if (exploreColumn4 != null) {
                    exploreColumn4.setCellValueFactory(new PropertyValueFactory<>("percentage"));
                }
                if (exploreColumn5 != null) {
                    exploreColumn5.setCellValueFactory(new PropertyValueFactory<>("status"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Don't fail initialization if exploration view has issues
        }
    }
    
    private void resetExplorationButtons() {
        if (exploreTotalButton != null) exploreTotalButton.getStyleClass().remove("exploration-category-button-active");
        if (exploreMinistryButton != null) exploreMinistryButton.getStyleClass().remove("exploration-category-button-active");
        if (exploreRevenueCategoryButton != null) exploreRevenueCategoryButton.getStyleClass().remove("exploration-category-button-active");
        if (exploreExpenseCategoryButton != null) exploreExpenseCategoryButton.getStyleClass().remove("exploration-category-button-active");
        if (exploreComparisonButton != null) exploreComparisonButton.getStyleClass().remove("exploration-category-button-active");
        if (exploreTrendsButton != null) exploreTrendsButton.getStyleClass().remove("exploration-category-button-active");
    }
    
    @FXML
    private void onSelectExplorationView(javafx.event.ActionEvent event) {
        Button clicked = (Button) event.getSource();
        String viewType = clicked.getText();
        
        resetExplorationButtons();
        clicked.getStyleClass().add("exploration-category-button-active");
        
        // Hide all dynamic filters
        if (exploreDynamicFilters != null) {
            exploreDynamicFilters.setVisible(false);
            exploreDynamicFilters.setManaged(false);
        }
        if (exploreMinistryLabel != null) exploreMinistryLabel.setVisible(false);
        if (exploreMinistryComboBox != null) exploreMinistryComboBox.setVisible(false);
        if (exploreRevenueCategoryLabel != null) exploreRevenueCategoryLabel.setVisible(false);
        if (exploreRevenueCategoryComboBox != null) exploreRevenueCategoryComboBox.setVisible(false);
        if (exploreExpenseCategoryLabel != null) exploreExpenseCategoryLabel.setVisible(false);
        if (exploreExpenseCategoryComboBox != null) exploreExpenseCategoryComboBox.setVisible(false);
        if (exploreYear1Label != null) exploreYear1Label.setVisible(false);
        if (exploreYear1ComboBox != null) exploreYear1ComboBox.setVisible(false);
        if (exploreYear2Label != null) exploreYear2Label.setVisible(false);
        if (exploreYear2ComboBox != null) exploreYear2ComboBox.setVisible(false);
        if (exploreLoadComparisonButton != null) exploreLoadComparisonButton.setVisible(false);
        if (exploreLoadComparisonButton != null) exploreLoadComparisonButton.setManaged(false);
        
        // Restore year ComboBox visibility for non-comparison views
        if (exploreYearComboBox != null && !viewType.contains("Σύγκριση")) {
            exploreYearComboBox.setVisible(true);
            exploreYearComboBox.setManaged(true);
        }
        
        if (viewType.contains("Συνολικά")) {
            currentExplorationView = "total";
            loadTotalView();
        } else if (viewType.contains("Υπουργείο")) {
            currentExplorationView = "ministry";
            loadMinistryView();
        } else if (viewType.contains("Κατηγορία Έσόδων")) {
            currentExplorationView = "revenue_category";
            loadRevenueCategoryView();
        } else if (viewType.contains("Κατηγορία Δαπανών")) {
            currentExplorationView = "expense_category";
            loadExpenseCategoryView();
        } else if (viewType.contains("Σύγκριση Ετών")) {
            currentExplorationView = "comparison";
            loadComparisonView();
        } else if (viewType.contains("Τάσεις")) {
            currentExplorationView = "trends";
            loadTrendsView();
        }
    }
    
    @FXML
    private void onExploreYearSelected() {
        refreshExplorationView();
    }
    
    @FXML
    private void onExploreFilterChanged() {
        refreshExplorationView();
    }
    
    private void refreshExplorationView() {
        switch (currentExplorationView) {
            case "total": loadTotalView(); break;
            case "ministry": loadMinistryView(); break;
            case "revenue_category": loadRevenueCategoryView(); break;
            case "expense_category": loadExpenseCategoryView(); break;
            case "comparison": loadComparisonView(); break;
            case "trends": loadTrendsView(); break;
        }
    }
    
    private void loadTotalView() {
        if (exploreViewTitleLabel != null) {
            exploreViewTitleLabel.setText("Συνολικά Έσοδα και Δαπάνες ανά Έτος");
        }
        if (exploreViewDescriptionLabel != null) {
            exploreViewDescriptionLabel.setText("Προβολή συνολικών εσόδων και δαπανών για κάθε έτος");
        }
        
        // Restore original column mappings
        if (exploreColumn2 != null) {
            exploreColumn2.setCellValueFactory(new PropertyValueFactory<>("amount"));
        }
        if (exploreColumn3 != null) {
            exploreColumn3.setCellValueFactory(new PropertyValueFactory<>("change"));
        }
        if (exploreColumn4 != null) {
            exploreColumn4.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        }
        if (exploreColumn5 != null) {
            exploreColumn5.setCellValueFactory(new PropertyValueFactory<>("status"));
        }
        
        if (exploreColumn1 != null) exploreColumn1.setText("Έτος");
        if (exploreColumn2 != null) exploreColumn2.setText("Συνολικά Έσοδα (€)");
        if (exploreColumn3 != null) exploreColumn3.setText("Συνολικές Δαπάνες (€)");
        if (exploreColumn4 != null) exploreColumn4.setText("Υπόλοιπο (€)");
        if (exploreColumn5 != null) exploreColumn5.setText("Αλλαγή από Προηγ. Έτος");
        
        ObservableList<CategoryData> data = FXCollections.observableArrayList();
        int startYear = 2023;
        int endYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
        
        for (int year = startYear; year <= endYear; year++) {
            double revenues = budgetData.getTotalRevenues(year);
            double expenses = budgetData.getTotalExpenses(year);
            double balance = StatisticalAnalysis.calculateBalance(revenues, expenses);
            double prevRevenues = year > startYear ? budgetData.getTotalRevenues(year - 1) : 0;
            double change = StatisticalAnalysis.calculatePercentageChange(revenues, prevRevenues);
            
            String changeText = prevRevenues > 0 ? AmountFormatter.formatPercentageChange(change) : "Νέο";
            data.add(new CategoryData(
                String.valueOf(year),
                revenues,
                0,
                String.format("%.2f", expenses),
                changeText,
                balance
            ));
        }
        
        if (exploreResultsTable != null) {
            exploreResultsTable.setItems(data);
        }
    }
    
    private void loadMinistryView() {
        if (exploreViewTitleLabel != null) {
            exploreViewTitleLabel.setText("Δαπάνες ανά Υπουργείο");
        }
        if (exploreViewDescriptionLabel != null) {
            exploreViewDescriptionLabel.setText("Επιλέξτε υπουργείο για να δείτε τις δαπάνες του ανά έτος");
        }
        
        if (exploreDynamicFilters != null) {
            exploreDynamicFilters.setVisible(true);
            exploreDynamicFilters.setManaged(true);
        }
        if (exploreMinistryLabel != null) {
            exploreMinistryLabel.setVisible(true);
            exploreMinistryLabel.setManaged(true);
        }
        if (exploreMinistryComboBox != null) {
            exploreMinistryComboBox.setVisible(true);
            exploreMinistryComboBox.setManaged(true);
            
            if (exploreMinistryComboBox.getItems().isEmpty()) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                List<BudgetData.CategoryInfo> ministries = budgetData.getCategories(currentYear);
                for (BudgetData.CategoryInfo m : ministries) {
                    exploreMinistryComboBox.getItems().add(m.getName());
                }
                if (!exploreMinistryComboBox.getItems().isEmpty()) {
                    exploreMinistryComboBox.setValue(exploreMinistryComboBox.getItems().get(0));
                }
            }
        }
        
        loadMinistryData();
    }
    
    private void loadMinistryData() {
        if (exploreMinistryComboBox == null || exploreMinistryComboBox.getValue() == null) return;
        
        String ministryName = exploreMinistryComboBox.getValue();
        
        if (exploreColumn1 != null) exploreColumn1.setText("Έτος");
        if (exploreColumn2 != null) exploreColumn2.setText("Υπουργείο");
        if (exploreColumn3 != null) exploreColumn3.setText("Ποσό (€)");
        if (exploreColumn4 != null) exploreColumn4.setText("Συμμετοχή (%)");
        if (exploreColumn5 != null) exploreColumn5.setText("Αλλαγή");
        
        ObservableList<CategoryData> data = FXCollections.observableArrayList();
        int startYear = 2023;
        int endYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
        
        for (int year = startYear; year <= endYear; year++) {
            List<BudgetData.CategoryInfo> ministries = budgetData.getCategories(year);
            for (BudgetData.CategoryInfo m : ministries) {
                if (m.getName().equals(ministryName)) {
                    double prevAmount = year > startYear ? getMinistryAmountForYear(ministryName, year - 1) : 0;
                    String change = prevAmount > 0 ? 
                        AmountFormatter.formatPercentageChange(StatisticalAnalysis.calculatePercentageChange(m.getAmount(), prevAmount)) : "Νέο";
                    data.add(new CategoryData(
                        String.valueOf(year),
                        m.getAmount(),
                        m.getPercentage(),
                        change,
                        ministryName,
                        prevAmount
                    ));
                    break;
                }
            }
        }
        
        if (exploreResultsTable != null) {
            exploreResultsTable.setItems(data);
        }
    }
    
    private double getMinistryAmountForYear(String ministryName, int year) {
        List<BudgetData.CategoryInfo> ministries = budgetData.getCategories(year);
        for (BudgetData.CategoryInfo m : ministries) {
            if (m.getName().equals(ministryName)) {
                return m.getAmount();
            }
        }
        return 0;
    }
    
    // ========== CHART METHODS (from graphs branch) ==========
    
    /**
     * Update all charts for the selected year (home view - only overview charts).
     */
    private void updateCharts(int year) {
        // Update Line Chart
        Charts.loadLineChart(lineHistory, budgetData);
    }

    
    private void loadRevenueCategoryView() {
        if (exploreViewTitleLabel != null) {
            exploreViewTitleLabel.setText("Έσοδα ανά Κατηγορία");
        }
        if (exploreViewDescriptionLabel != null) {
            exploreViewDescriptionLabel.setText("Επιλέξτε κατηγορία εσόδων για να δείτε την εξέλιξή της ανά έτος");
        }
        
        if (exploreDynamicFilters != null) {
            exploreDynamicFilters.setVisible(true);
            exploreDynamicFilters.setManaged(true);
        }
        if (exploreRevenueCategoryLabel != null) {
            exploreRevenueCategoryLabel.setVisible(true);
            exploreRevenueCategoryLabel.setManaged(true);
        }
        if (exploreRevenueCategoryComboBox != null) {
            exploreRevenueCategoryComboBox.setVisible(true);
            exploreRevenueCategoryComboBox.setManaged(true);
            
            if (exploreRevenueCategoryComboBox.getItems().isEmpty()) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                List<BudgetData.CategoryInfo> revenues = budgetData.getRevenueBreakdown(currentYear);
                for (BudgetData.CategoryInfo r : revenues) {
                    exploreRevenueCategoryComboBox.getItems().add(r.getName());
                }
                if (!exploreRevenueCategoryComboBox.getItems().isEmpty()) {
                    exploreRevenueCategoryComboBox.setValue(exploreRevenueCategoryComboBox.getItems().get(0));
                }
            }
        }
        
        loadRevenueCategoryData();
    }
    
    private void loadRevenueCategoryData() {
        if (exploreRevenueCategoryComboBox == null || exploreRevenueCategoryComboBox.getValue() == null) return;
        
        String categoryName = exploreRevenueCategoryComboBox.getValue();
        
        if (exploreColumn1 != null) exploreColumn1.setText("Έτος");
        if (exploreColumn2 != null) exploreColumn2.setText("Κατηγορία");
        if (exploreColumn3 != null) exploreColumn3.setText("Ποσό (€)");
        if (exploreColumn4 != null) exploreColumn4.setText("Συμμετοχή (%)");
        if (exploreColumn5 != null) exploreColumn5.setText("Αλλαγή");
        
        ObservableList<CategoryData> data = FXCollections.observableArrayList();
        int startYear = 2023;
        int endYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
        
        for (int year = startYear; year <= endYear; year++) {
            List<BudgetData.CategoryInfo> revenues = budgetData.getRevenueBreakdown(year);
            for (BudgetData.CategoryInfo r : revenues) {
                if (r.getName().equals(categoryName)) {
                    double prevAmount = year > startYear ? getRevenueCategoryAmountForYear(categoryName, year - 1) : 0;
                    String change = prevAmount > 0 ? 
                        AmountFormatter.formatPercentageChange(StatisticalAnalysis.calculatePercentageChange(r.getAmount(), prevAmount)) : "Νέο";
                    data.add(new CategoryData(
                        String.valueOf(year),
                        r.getAmount(),
                        r.getPercentage(),
                        change,
                        categoryName,
                        prevAmount
                    ));
                    break;
                }
            }
        }
        
        if (exploreResultsTable != null) {
            exploreResultsTable.setItems(data);
        }
    }
    
    private double getRevenueCategoryAmountForYear(String categoryName, int year) {
        List<BudgetData.CategoryInfo> revenues = budgetData.getRevenueBreakdown(year);
        for (BudgetData.CategoryInfo r : revenues) {
            if (r.getName().equals(categoryName)) {
                return r.getAmount();
            }
        }
        return 0;
    }
    
    private void loadExpenseCategoryView() {
        if (exploreViewTitleLabel != null) {
            exploreViewTitleLabel.setText("Δαπάνες ανά Κατηγορία");
        }
        if (exploreViewDescriptionLabel != null) {
            exploreViewDescriptionLabel.setText("Επιλέξτε κατηγορία δαπανών για να δείτε την εξέλιξή της ανά έτος");
        }
        
        if (exploreDynamicFilters != null) {
            exploreDynamicFilters.setVisible(true);
            exploreDynamicFilters.setManaged(true);
        }
        if (exploreExpenseCategoryLabel != null) {
            exploreExpenseCategoryLabel.setVisible(true);
            exploreExpenseCategoryLabel.setManaged(true);
        }
        if (exploreExpenseCategoryComboBox != null) {
            exploreExpenseCategoryComboBox.setVisible(true);
            exploreExpenseCategoryComboBox.setManaged(true);
            
            if (exploreExpenseCategoryComboBox.getItems().isEmpty()) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                List<BudgetData.CategoryInfo> expenses = budgetData.getExpensesBreakdown(currentYear);
                for (BudgetData.CategoryInfo e : expenses) {
                    exploreExpenseCategoryComboBox.getItems().add(e.getName());
                }
                if (!exploreExpenseCategoryComboBox.getItems().isEmpty()) {
                    exploreExpenseCategoryComboBox.setValue(exploreExpenseCategoryComboBox.getItems().get(0));
                }
            }
        }
        
        loadExpenseCategoryData();
    }
    
    private void loadExpenseCategoryData() {
        if (exploreExpenseCategoryComboBox == null || exploreExpenseCategoryComboBox.getValue() == null) return;
        
        String categoryName = exploreExpenseCategoryComboBox.getValue();
        
        if (exploreColumn1 != null) exploreColumn1.setText("Έτος");
        if (exploreColumn2 != null) exploreColumn2.setText("Κατηγορία");
        if (exploreColumn3 != null) exploreColumn3.setText("Ποσό (€)");
        if (exploreColumn4 != null) exploreColumn4.setText("Συμμετοχή (%)");
        if (exploreColumn5 != null) exploreColumn5.setText("Αλλαγή");
        
        ObservableList<CategoryData> data = FXCollections.observableArrayList();
        int startYear = 2023;
        int endYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
        
        for (int year = startYear; year <= endYear; year++) {
            List<BudgetData.CategoryInfo> expenses = budgetData.getExpensesBreakdown(year);
            for (BudgetData.CategoryInfo e : expenses) {
                if (e.getName().equals(categoryName)) {
                    double prevAmount = year > startYear ? getExpenseCategoryAmountForYear(categoryName, year - 1) : 0;
                    String change = prevAmount > 0 ? 
                        AmountFormatter.formatPercentageChange(StatisticalAnalysis.calculatePercentageChange(e.getAmount(), prevAmount)) : "Νέο";
                    data.add(new CategoryData(
                        String.valueOf(year),
                        e.getAmount(),
                        e.getPercentage(),
                        change,
                        categoryName,
                        prevAmount
                    ));
                    break;
                }
            }
        }
        
        if (exploreResultsTable != null) {
            exploreResultsTable.setItems(data);
        }
    }
    
    private double getExpenseCategoryAmountForYear(String categoryName, int year) {
        List<BudgetData.CategoryInfo> expenses = budgetData.getExpensesBreakdown(year);
        for (BudgetData.CategoryInfo e : expenses) {
            if (e.getName().equals(categoryName)) {
                return e.getAmount();
            }
        }
        return 0;
    }
    
    private void loadTrendsView() {
        if (exploreViewTitleLabel != null) {
            exploreViewTitleLabel.setText("Τάσεις & Αλλαγές");
        }
        if (exploreViewDescriptionLabel != null) {
            exploreViewDescriptionLabel.setText("Προβολή τάσεων αύξησης/μείωσης για υπουργεία και κατηγορίες");
        }
        
        if (exploreColumn1 != null) exploreColumn1.setText("Κατηγορία");
        if (exploreColumn2 != null) exploreColumn2.setText("Τρέχον Έτος (€)");
        if (exploreColumn3 != null) exploreColumn3.setText("Προηγ. Έτος (€)");
        if (exploreColumn4 != null) exploreColumn4.setText("Αλλαγή (%)");
        if (exploreColumn5 != null) exploreColumn5.setText("Τάση");
        
        ObservableList<CategoryData> data = FXCollections.observableArrayList();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int previousYear = currentYear - 1;
        
        List<BudgetData.CategoryInfo> currentMinistries = budgetData.getCategories(currentYear);
        List<BudgetData.CategoryInfo> prevMinistries = budgetData.getCategories(previousYear);
        Map<String, Double> prevMinistryMap = new HashMap<>();
        for (BudgetData.CategoryInfo m : prevMinistries) {
            prevMinistryMap.put(m.getName(), m.getAmount());
        }
        
        for (BudgetData.CategoryInfo m : currentMinistries) {
            double prevAmount = prevMinistryMap.getOrDefault(m.getName(), 0.0);
            double change = StatisticalAnalysis.calculatePercentageChange(m.getAmount(), prevAmount);
            String trend = change > 5 ? "↑ Αύξηση" : change < -5 ? "↓ Μείωση" : "— Σταθερό";
            data.add(new CategoryData(
                m.getName(),
                m.getAmount(),
                prevAmount,
                AmountFormatter.formatPercentageChange(change),
                trend,
                0
            ));
        }
        
        if (exploreResultsTable != null) {
            exploreResultsTable.setItems(data);
        }
    }
    
    private void loadComparisonView() {
        if (exploreViewTitleLabel != null) {
            exploreViewTitleLabel.setText("Σύγκριση Προϋπολογισμού μεταξύ δύο ετών");
        }
        if (exploreViewDescriptionLabel != null) {
            exploreViewDescriptionLabel.setText("Επιλέξτε δύο έτη για σύγκριση εσόδων, δαπανών, υπουργείων και αποκεντρωμένων διοικήσεων");
        }
        
        // Hide the single year ComboBox
        if (exploreYearComboBox != null) {
            exploreYearComboBox.setVisible(false);
            exploreYearComboBox.setManaged(false);
        }
        
        // Show comparison controls
        if (exploreDynamicFilters != null) {
            exploreDynamicFilters.setVisible(true);
            exploreDynamicFilters.setManaged(true);
        }
        if (exploreYear1Label != null) {
            exploreYear1Label.setVisible(true);
            exploreYear1Label.setManaged(true);
        }
        if (exploreYear1ComboBox != null) {
            exploreYear1ComboBox.setVisible(true);
            exploreYear1ComboBox.setManaged(true);
            // Always refresh the list to ensure it reflects current database state
            List<String> availableYears = getAvailableYearsFromDatabase();
            exploreYear1ComboBox.getItems().clear();
            exploreYear1ComboBox.getItems().addAll(availableYears);
            if (exploreYear1ComboBox.getItems().size() > 1) {
                exploreYear1ComboBox.setValue(exploreYear1ComboBox.getItems().get(exploreYear1ComboBox.getItems().size() - 2));
            } else if (!exploreYear1ComboBox.getItems().isEmpty()) {
                exploreYear1ComboBox.setValue(exploreYear1ComboBox.getItems().get(0));
            }
        }
        if (exploreYear2Label != null) {
            exploreYear2Label.setVisible(true);
            exploreYear2Label.setManaged(true);
        }
        if (exploreYear2ComboBox != null) {
            exploreYear2ComboBox.setVisible(true);
            exploreYear2ComboBox.setManaged(true);
            // Always refresh the list to ensure it reflects current database state
            List<String> availableYears = getAvailableYearsFromDatabase();
            exploreYear2ComboBox.getItems().clear();
            exploreYear2ComboBox.getItems().addAll(availableYears);
            if (!exploreYear2ComboBox.getItems().isEmpty()) {
                exploreYear2ComboBox.setValue(exploreYear2ComboBox.getItems().get(exploreYear2ComboBox.getItems().size() - 1));
            }
        }
        if (exploreLoadComparisonButton != null) {
            exploreLoadComparisonButton.setVisible(true);
            exploreLoadComparisonButton.setManaged(true);
        }
        
        // Hide other filters
        if (exploreMinistryLabel != null) exploreMinistryLabel.setVisible(false);
        if (exploreMinistryComboBox != null) exploreMinistryComboBox.setVisible(false);
        if (exploreRevenueCategoryLabel != null) exploreRevenueCategoryLabel.setVisible(false);
        if (exploreRevenueCategoryComboBox != null) exploreRevenueCategoryComboBox.setVisible(false);
        if (exploreExpenseCategoryLabel != null) exploreExpenseCategoryLabel.setVisible(false);
        if (exploreExpenseCategoryComboBox != null) exploreExpenseCategoryComboBox.setVisible(false);
        
        // Set up table columns for comparison
        if (exploreColumn1 != null) exploreColumn1.setText("Κατηγορία");
        if (exploreColumn2 != null) exploreColumn2.setText("Έτος 1 (€)");
        if (exploreColumn3 != null) exploreColumn3.setText("Έτος 2 (€)");
        if (exploreColumn4 != null) exploreColumn4.setText("Διαφορά (€)");
        if (exploreColumn5 != null) exploreColumn5.setText("% Αλλαγή");
        
        // Set up custom cell value factories for comparison view
        if (exploreResultsTable != null && exploreColumn2 != null && exploreColumn3 != null) {
            // Column 2 (Double): Year 1 value (using amount property directly)
            exploreColumn2.setCellValueFactory(new PropertyValueFactory<>("amount"));
            
            // Column 3 (String): Year 2 value formatted (using change property to store formatted year2)
            exploreColumn3.setCellValueFactory(cellData -> {
                double value = cellData.getValue().getPercentage(); // percentage stores year2 value
                return new SimpleStringProperty(String.format("%,d €", (long)value));
            });
            
            // Column 4 (Double): Difference (calculate from amount and percentage)
            if (exploreColumn4 != null) {
                exploreColumn4.setCellValueFactory(cellData -> {
                    double year1 = cellData.getValue().getAmount();
                    double year2 = cellData.getValue().getPercentage();
                    return new SimpleDoubleProperty(year2 - year1).asObject();
                });
            }
            
            // Column 5 (String): % Change (already in status property)
            if (exploreColumn5 != null) {
                exploreColumn5.setCellValueFactory(new PropertyValueFactory<>("status"));
            }
        }
        
        // Clear table initially
        if (exploreResultsTable != null) {
            exploreResultsTable.setItems(FXCollections.observableArrayList());
        }
    }
    
    @FXML
    private void onLoadComparison() {
        if (exploreYear1ComboBox == null || exploreYear2ComboBox == null) return;
        
        String year1Str = exploreYear1ComboBox.getValue();
        String year2Str = exploreYear2ComboBox.getValue();
        
        if (year1Str == null || year2Str == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Παρακαλώ επιλέξτε και τα δύο έτη");
            alert.showAndWait();
            return;
        }
        
        try {
            int year1 = Integer.parseInt(year1Str);
            int year2 = Integer.parseInt(year2Str);
            
            if (year1 == year2) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Σφάλμα");
                alert.setHeaderText("Παρακαλώ επιλέξτε διαφορετικά έτη");
                alert.showAndWait();
                return;
            }
            
            loadComparisonData(year1, year2);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Μη έγκυρα έτη");
            alert.setContentText("Παρακαλώ επιλέξτε έγκυρα έτη");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = e.getClass().getSimpleName();
            }
            
            // Extract year values for error message
            String year1Display = year1Str != null ? year1Str : "?";
            String year2Display = year2Str != null ? year2Str : "?";
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Σφάλμα φόρτωσης δεδομένων σύγκρισης");
            alert.setContentText("Δεν ήταν δυνατή η φόρτωση των δεδομένων σύγκρισης.\n\n" +
                "Σφάλμα: " + errorMessage + "\n\n" +
                "Πιθανές αιτίες:\n" +
                "• Οι πίνακες για τα έτη " + year1Display + " ή " + year2Display + " δεν υπάρχουν\n" +
                "• Τα δεδομένα δεν έχουν εισαχθεί στη βάση\n" +
                "• Προβλήματα σύνδεσης με τη βάση δεδομένων\n\n" +
                "Παρακαλώ ελέγξτε ότι τα δεδομένα έχουν εισαχθεί για τα επιλεγμένα έτη.");
            alert.setResizable(true);
            alert.getDialogPane().setPrefWidth(550);
            alert.showAndWait();
        }
    }
    
    private void loadComparisonData(int year1, int year2) throws Exception {
        Comparisons comparisons = new Comparisons();
        Comparisons.ComparisonResults results = comparisons.compareYears(year1, year2);
        
        ObservableList<CategoryData> data = FXCollections.observableArrayList();
        
        // Add Budget Summary
        data.add(new CategoryData("━━━ ΣΥΝΟΨΗ ΠΡΟΥΠΟΛΟΓΙΣΜΟΥ ━━━", 0, 0, "", "", 0));
        
        // Calculate totals for budget summary
        long totalRevenues1 = 0, totalRevenues2 = 0;
        long totalExpenses1 = 0, totalExpenses2 = 0;
        
        for (Comparisons.ComparisonData compData : results.getRevenues().values()) {
            totalRevenues1 += compData.getYear1Value();
            totalRevenues2 += compData.getYear2Value();
        }
        
        for (Comparisons.ComparisonData compData : results.getExpenses().values()) {
            totalExpenses1 += compData.getYear1Value();
            totalExpenses2 += compData.getYear2Value();
        }
        
        long balance1 = totalRevenues1 - totalExpenses1;
        long balance2 = totalRevenues2 - totalExpenses2;
        
        // Use: category, amount (year1), percentage (year2 as double), change (difference), status (% change)
        data.add(new CategoryData("Συνολικά Έσοδα", totalRevenues1, totalRevenues2, 
            String.format("%,d €", totalRevenues2 - totalRevenues1),
            formatPercentageChange(totalRevenues1, totalRevenues2), 0));
        data.add(new CategoryData("Συνολικές Δαπάνες", totalExpenses1, totalExpenses2,
            String.format("%,d €", totalExpenses2 - totalExpenses1),
            formatPercentageChange(totalExpenses1, totalExpenses2), 0));
        data.add(new CategoryData("Υπόλοιπο", balance1, balance2,
            String.format("%,d €", balance2 - balance1),
            formatPercentageChange(balance1, balance2), 0));
        
        // Add Revenues
        data.add(new CategoryData("━━━ ΕΣΟΔΑ ━━━", 0, 0, "", "", 0));
        for (Comparisons.ComparisonData compData : results.getRevenues().values()) {
            data.add(new CategoryData(compData.getCategoryName(), 
                compData.getYear1Value(), compData.getYear2Value(),
                String.format("%,d €", compData.getDifference()),
                compData.getPercentageChangeAsString(), 0));
        }
        
        // Add Expenses
        data.add(new CategoryData("━━━ ΔΑΠΑΝΕΣ ━━━", 0, 0, "", "", 0));
        for (Comparisons.ComparisonData compData : results.getExpenses().values()) {
            data.add(new CategoryData(compData.getCategoryName(),
                compData.getYear1Value(), compData.getYear2Value(),
                String.format("%,d €", compData.getDifference()),
                compData.getPercentageChangeAsString(), 0));
        }
        
        // Add Administrations
        data.add(new CategoryData("━━━ ΑΠΟΚΕΝΤΡΩΜΕΝΕΣ ΔΙΟΙΚΗΣΕΙΣ ━━━", 0, 0, "", "", 0));
        for (Comparisons.ComparisonData compData : results.getAdministrations().values()) {
            data.add(new CategoryData(compData.getCategoryName(),
                compData.getYear1Value(), compData.getYear2Value(),
                String.format("%,d €", compData.getDifference()),
                compData.getPercentageChangeAsString(), 0));
        }
        
        // Add Ministries
        data.add(new CategoryData("━━━ ΥΠΟΥΡΓΕΙΑ ━━━", 0, 0, "", "", 0));
        for (Comparisons.ComparisonData compData : results.getMinistries().values()) {
            data.add(new CategoryData(compData.getCategoryName(),
                compData.getYear1Value(), compData.getYear2Value(),
                String.format("%,d €", compData.getDifference()),
                compData.getPercentageChangeAsString(), 0));
        }
        
        if (exploreResultsTable != null) {
            exploreResultsTable.setItems(data);
        }
    }
    
    private String formatPercentageChange(long value1, long value2) {
        if (value1 == 0) {
            return value2 > 0 ? "Νέο" : "0.00%";
        }
        double change = ((double)(value2 - value1) / value1) * 100.0;
        return String.format("%.2f%%", change);
    }
    
    @FXML
    private void onNavigateDataManagement() {
        if (isGovernmentUser()) {
            if (selectedYear != null) {
                try {
                    int year = Integer.parseInt(selectedYear);
                    if (isYearEditable(year)) {
                        dataManagementSelectedYear = year;
                    } else {
                        dataManagementSelectedYear = Calendar.getInstance().get(Calendar.YEAR);
                    }
                } catch (NumberFormatException e) {
                    dataManagementSelectedYear = Calendar.getInstance().get(Calendar.YEAR);
                }
            }
            
            if (dataManagementYearComboBox != null && dataManagementYearComboBox.getItems().isEmpty()) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                dataManagementYearComboBox.getItems().addAll(
                    String.valueOf(currentYear), 
                    String.valueOf(currentYear + 1)
                );
                
                // Center the text in the ComboBox
                dataManagementYearComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
                    @Override
                    public ListCell<String> call(ListView<String> param) {
                        return new ListCell<String>() {
                            @Override
                            protected void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                } else {
                                    setText(item);
                                    setAlignment(javafx.geometry.Pos.CENTER);
                                }
                            }
                        };
                    }
                });
                
                // Center the selected item display
                dataManagementYearComboBox.setButtonCell(new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item);
                            setAlignment(javafx.geometry.Pos.CENTER);
                        }
                    }
                });
            }
            
            // Set the value in data management ComboBox
            if (dataManagementYearComboBox != null) {
                dataManagementYearComboBox.setValue(String.valueOf(dataManagementSelectedYear));
            }
            
            showView(dataManagementView);
            loadDataManagementTable();
            updatePublishButtonVisibility();
        }
    }
    
    @FXML
    private void onNavigateProjections() {
        if (projectionsView == null) {
            return;
        }
        showView(projectionsView);
        
        // Initialize year combo boxes if empty
        if (simulationBaseYearComboBox != null && simulationBaseYearComboBox.getItems().isEmpty()) {
            List<String> availableYears = getAvailableYearsFromDatabase();
            simulationBaseYearComboBox.getItems().addAll(availableYears);
            simulationFutureYearComboBox.getItems().addAll(availableYears);
            
            if (!availableYears.isEmpty()) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                simulationBaseYearComboBox.setValue(String.valueOf(currentYear));
                if (availableYears.contains(String.valueOf(currentYear + 1))) {
                    simulationFutureYearComboBox.setValue(String.valueOf(currentYear + 1));
                } else if (!availableYears.isEmpty()) {
                    simulationFutureYearComboBox.setValue(availableYears.get(0));
                }
            }
        }
        
        // Clear results
        if (simulationResultsArea != null) {
            simulationResultsArea.clear();
        }
    }
    
    @FXML
    private void onLoadSimulationData() {
        if (simulationBaseYearComboBox == null || simulationBaseYearComboBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Παρακαλώ επιλέξτε έτος");
            alert.setContentText("Πρέπει να επιλέξετε ένα έτος για να φορτώσουν τα βασικά δεδομένα.");
            alert.showAndWait();
            return;
        }
        
        try {
            int year = Integer.parseInt(simulationBaseYearComboBox.getValue());
            BudgetData service = BudgetData.getInstance();
            
            simulationBaseYear = year;
            simulationBaseRevenue = service.getTotalRevenues(year);
            simulationBaseExpense = service.getTotalExpenses(year);
            
            if (simulationResultsArea != null) {
                simulationResultsArea.setText("✅ Δεδομένα φορτώθηκαν επιτυχώς!\n\n" +
                    "Έτος: " + year + "\n" +
                    "Συνολικά Έσοδα: " + AmountFormatter.formatCurrency(simulationBaseRevenue) + "\n" +
                    "Συνολικές Δαπάνες: " + AmountFormatter.formatCurrency(simulationBaseExpense) + "\n" +
                    "Ισοζύγιο: " + AmountFormatter.formatCurrency(simulationBaseRevenue - simulationBaseExpense) + "\n\n" +
                    "Επιλέξτε ένα σενάριο για να δείτε τα αποτελέσματα.");
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Μη έγκυρο έτος");
            alert.setContentText("Παρακαλώ επιλέξτε έγκυρο έτος.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Σφάλμα φόρτωσης δεδομένων");
            alert.setContentText("Δεν ήταν δυνατή η φόρτωση των δεδομένων: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    private void onSimulateRevenueIncrease() {
        if (simulationBaseRevenue == 0 || simulationBaseExpense == 0) {
            showSimulationError("Παρακαλώ φορτώστε πρώτα τα βασικά δεδομένα.");
            return;
        }
        
        try {
            double percent = Double.parseDouble(revenueIncreasePercentField.getText());
            String result = Simulations.simulateRevenueIncrease(
                simulationBaseRevenue, simulationBaseExpense, percent
            );
            if (simulationResultsArea != null) {
                simulationResultsArea.setText(result);
            }
        } catch (NumberFormatException e) {
            showSimulationError("Παρακαλώ εισάγετε έγκυρο ποσοστό (π.χ. 5.0 για 5%).");
        }
    }
    
    @FXML
    private void onSimulateExpenseDecrease() {
        if (simulationBaseRevenue == 0 || simulationBaseExpense == 0) {
            showSimulationError("Παρακαλώ φορτώστε πρώτα τα βασικά δεδομένα.");
            return;
        }
        
        try {
            double percent = Double.parseDouble(expenseDecreasePercentField.getText());
            String result = Simulations.simulateExpenseDecrease(
                simulationBaseRevenue, simulationBaseExpense, percent
            );
            if (simulationResultsArea != null) {
                simulationResultsArea.setText(result);
            }
        } catch (NumberFormatException e) {
            showSimulationError("Παρακαλώ εισάγετε έγκυρο ποσοστό (π.χ. 10.0 για 10%).");
        }
    }
    
    @FXML
    private void onSimulateCombined() {
        if (simulationBaseRevenue == 0 || simulationBaseExpense == 0) {
            showSimulationError("Παρακαλώ φορτώστε πρώτα τα βασικά δεδομένα.");
            return;
        }
        
        try {
            double revenueChange = Double.parseDouble(combinedRevenueChangeField.getText());
            double expenseChange = Double.parseDouble(combinedExpenseChangeField.getText());
            String result = Simulations.simulateCombinedScenario(
                simulationBaseRevenue, simulationBaseExpense, revenueChange, expenseChange
            );
            if (simulationResultsArea != null) {
                simulationResultsArea.setText(result);
            }
        } catch (NumberFormatException e) {
            showSimulationError("Παρακαλώ εισάγετε έγκυρα ποσοστά (π.χ. 3.0 ή -2.0).");
        }
    }
    
    @FXML
    private void onSimulateFutureYear() {
        if (simulationBaseRevenue == 0 || simulationBaseExpense == 0) {
            showSimulationError("Παρακαλώ φορτώστε πρώτα τα βασικά δεδομένα.");
            return;
        }
        
        if (simulationFutureYearComboBox == null || simulationFutureYearComboBox.getValue() == null) {
            showSimulationError("Παρακαλώ επιλέξτε μελλοντικό έτος.");
            return;
        }
        
        try {
            int futureYear = Integer.parseInt(simulationFutureYearComboBox.getValue());
            double revenueTrend = Double.parseDouble(revenueTrendField.getText());
            double expenseTrend = Double.parseDouble(expenseTrendField.getText());
            
            String result = Simulations.simulateFutureYear(
                simulationBaseYear, futureYear,
                simulationBaseRevenue, simulationBaseExpense,
                revenueTrend, expenseTrend
            );
            if (simulationResultsArea != null) {
                simulationResultsArea.setText(result);
            }
        } catch (NumberFormatException e) {
            showSimulationError("Παρακαλώ εισάγετε έγκυρες τιμές για τις τάσεις (σε €).");
        }
    }
    
    private void showSimulationError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Σφάλμα");
        alert.setHeaderText("Σφάλμα Προσομοίωσης");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void onNavigateStatistics() {
        if (statisticsView == null) {
            return;
        }
        showView(statisticsView);
        
        // Initialize year combo boxes if empty
        if (statisticsStartYearComboBox != null && statisticsStartYearComboBox.getItems().isEmpty()) {
            List<String> availableYears = getAvailableYearsFromDatabase();
            statisticsStartYearComboBox.getItems().addAll(availableYears);
            statisticsEndYearComboBox.getItems().addAll(availableYears);
            
            if (!availableYears.isEmpty()) {
                // Set default: start from first available year, end at last
                statisticsStartYearComboBox.setValue(availableYears.get(availableYears.size() - 1));
                statisticsEndYearComboBox.setValue(availableYears.get(0));
            }
        }
        
        // Setup outliers table columns programmatically
        if (statisticsOutliersTable != null && statsOutlierYearColumn != null) {
            statsOutlierYearColumn.setCellValueFactory(cellData -> {
                Map<String, Object> item = cellData.getValue();
                return new SimpleStringProperty(item.get("year") != null ? item.get("year").toString() : "");
            });
            statsOutlierTypeColumn.setCellValueFactory(cellData -> {
                Map<String, Object> item = cellData.getValue();
                return new SimpleStringProperty(item.get("type") != null ? item.get("type").toString() : "");
            });
            statsOutlierValueColumn.setCellValueFactory(cellData -> {
                Map<String, Object> item = cellData.getValue();
                Object value = item.get("value");
                if (value instanceof Number) {
                    return new SimpleDoubleProperty(((Number) value).doubleValue()).asObject();
                }
                return new SimpleDoubleProperty(0.0).asObject();
            });
            statsOutlierValueColumn.setCellFactory(tc -> new TableCell<Map<String, Object>, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(AmountFormatter.formatCurrency(item));
                    }
                }
            });
            statsOutlierZScoreColumn.setCellValueFactory(cellData -> {
                Map<String, Object> item = cellData.getValue();
                Object zScore = item.get("zScore");
                if (zScore instanceof Number) {
                    return new SimpleDoubleProperty(((Number) zScore).doubleValue()).asObject();
                }
                return new SimpleDoubleProperty(0.0).asObject();
            });
            statsOutlierZScoreColumn.setCellFactory(tc -> new TableCell<Map<String, Object>, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f", item));
                    }
                }
            });
        }
    }
    
    @FXML
    private void onCalculateStatistics() {
        if (statisticsStartYearComboBox == null || statisticsEndYearComboBox == null) {
            return;
        }
        
        String startYearStr = statisticsStartYearComboBox.getValue();
        String endYearStr = statisticsEndYearComboBox.getValue();
        
        if (startYearStr == null || endYearStr == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Παρακαλώ επιλέξτε περίοδο");
            alert.setContentText("Πρέπει να επιλέξετε έτος έναρξης και έτος λήξης.");
            alert.showAndWait();
            return;
        }
        
        try {
            int startYear = Integer.parseInt(startYearStr);
            int endYear = Integer.parseInt(endYearStr);
            
            if (startYear > endYear) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Σφάλμα");
                alert.setHeaderText("Μη έγκυρη περίοδος");
                alert.setContentText("Το έτος έναρξης πρέπει να είναι μικρότερο ή ίσο με το έτος λήξης.");
                alert.showAndWait();
                return;
            }
            
            calculateAndDisplayStatistics(startYear, endYear);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Μη έγκυρα έτη");
            alert.setContentText("Παρακαλώ επιλέξτε έγκυρα έτη.");
            alert.showAndWait();
        }
    }
    
    private void calculateAndDisplayStatistics(int startYear, int endYear) {
        try {
            BudgetData service = BudgetData.getInstance();
            
            // Get revenue and expense arrays
            double[] revenues = service.getRevenuesAcrossYears(startYear, endYear);
            double[] expenses = service.getExpensesAcrossYears(startYear, endYear);
            
            if (revenues == null || revenues.length < 2) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Σφάλμα");
                alert.setHeaderText("Ανεπαρκή δεδομένα");
                alert.setContentText("Δεν υπάρχουν αρκετά δεδομένα για την επιλεγμένη περίοδο.");
                alert.showAndWait();
                return;
            }
            
            // Calculate revenue statistics
            double revenueMean = StatisticalAnalysis.calculateMean(revenues);
            double revenueMedian = StatisticalAnalysis.calculateMedian(revenues);
            double revenueStdDev = StatisticalAnalysis.calculateStandardDeviation(revenues);
            double revenueVariance = StatisticalAnalysis.calculateVariance(revenues);
            double revenueCoeffVar = StatisticalAnalysis.calculateCoefficientOfVariation(revenues);
            
            // Display revenue statistics
            if (statsRevenueMeanLabel != null) {
                statsRevenueMeanLabel.setText(AmountFormatter.formatCurrency(revenueMean));
            }
            if (statsRevenueMedianLabel != null) {
                statsRevenueMedianLabel.setText(AmountFormatter.formatCurrency(revenueMedian));
            }
            if (statsRevenueStdDevLabel != null) {
                statsRevenueStdDevLabel.setText(AmountFormatter.formatCurrency(revenueStdDev));
            }
            if (statsRevenueVarianceLabel != null) {
                statsRevenueVarianceLabel.setText(AmountFormatter.formatCurrency(revenueVariance));
            }
            if (statsRevenueCoeffVarLabel != null) {
                statsRevenueCoeffVarLabel.setText(String.format("%.2f%%", revenueCoeffVar * 100));
            }
            
            // Calculate expense statistics
            if (expenses != null && expenses.length >= 2) {
                double expenseMean = StatisticalAnalysis.calculateMean(expenses);
                double expenseMedian = StatisticalAnalysis.calculateMedian(expenses);
                double expenseStdDev = StatisticalAnalysis.calculateStandardDeviation(expenses);
                double expenseVariance = StatisticalAnalysis.calculateVariance(expenses);
                double expenseCoeffVar = StatisticalAnalysis.calculateCoefficientOfVariation(expenses);
                
                // Display expense statistics
                if (statsExpenseMeanLabel != null) {
                    statsExpenseMeanLabel.setText(AmountFormatter.formatCurrency(expenseMean));
                }
                if (statsExpenseMedianLabel != null) {
                    statsExpenseMedianLabel.setText(AmountFormatter.formatCurrency(expenseMedian));
                }
                if (statsExpenseStdDevLabel != null) {
                    statsExpenseStdDevLabel.setText(AmountFormatter.formatCurrency(expenseStdDev));
                }
                if (statsExpenseVarianceLabel != null) {
                    statsExpenseVarianceLabel.setText(AmountFormatter.formatCurrency(expenseVariance));
                }
                if (statsExpenseCoeffVarLabel != null) {
                    statsExpenseCoeffVarLabel.setText(String.format("%.2f%%", expenseCoeffVar * 100));
                }
            }
            
            // Calculate and display correlation
            double correlation = service.calculateRevenueExpenseCorrelation(startYear, endYear);
            if (statsCorrelationLabel != null) {
                if (Double.isNaN(correlation)) {
                    statsCorrelationLabel.setText("-");
                } else {
                    statsCorrelationLabel.setText(String.format("%.4f", correlation));
                }
            }
            
            // Calculate and display trends
            double[] revenueTrend = service.calculateRevenueTrend(startYear, endYear);
            double[] expenseTrend = service.calculateExpenseTrend(startYear, endYear);
            
            if (statsRevenueTrendLabel != null && revenueTrend != null && revenueTrend.length >= 1) {
                double slope = revenueTrend[0];
                String trend = slope > 0 ? "↑ Αύξηση" : slope < 0 ? "↓ Μείωση" : "— Σταθερό";
                statsRevenueTrendLabel.setText(String.format("%s (%.2f €/έτος)", trend, Math.abs(slope)));
            }
            if (statsExpenseTrendLabel != null && expenseTrend != null && expenseTrend.length >= 1) {
                double slope = expenseTrend[0];
                String trend = slope > 0 ? "↑ Αύξηση" : slope < 0 ? "↓ Μείωση" : "— Σταθερό";
                statsExpenseTrendLabel.setText(String.format("%s (%.2f €/έτος)", trend, Math.abs(slope)));
            }
            
            // Display outliers
            List<Double> revenueOutliers = service.identifyRevenueOutliers(startYear, endYear);
            List<Double> expenseOutliers = service.identifyExpenseOutliers(startYear, endYear);
            
            // Create map of year to value for revenues and expenses
            Map<Integer, Double> revenueYearMap = new HashMap<>();
            Map<Integer, Double> expenseYearMap = new HashMap<>();
            for (int year = startYear; year <= endYear; year++) {
                double rev = service.getTotalRevenues(year);
                if (rev > 0) revenueYearMap.put(year, rev);
                double exp = service.getTotalExpenses(year);
                if (exp > 0) expenseYearMap.put(year, exp);
            }
            
            // Calculate z-scores for outliers
            ObservableList<Map<String, Object>> outliersData = FXCollections.observableArrayList();
            for (Double outlierValue : revenueOutliers) {
                // Find the year for this outlier value
                for (Map.Entry<Integer, Double> entry : revenueYearMap.entrySet()) {
                    if (Math.abs(entry.getValue() - outlierValue) < 0.01) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("year", entry.getKey().toString());
                        item.put("type", "Έσοδα");
                        item.put("value", outlierValue);
                        double zScore = StatisticalAnalysis.calculateZScore(outlierValue, revenues);
                        item.put("zScore", zScore);
                        outliersData.add(item);
                        break;
                    }
                }
            }
            
            if (expenses != null && expenses.length >= 2) {
                for (Double outlierValue : expenseOutliers) {
                    // Find the year for this outlier value
                    for (Map.Entry<Integer, Double> entry : expenseYearMap.entrySet()) {
                        if (Math.abs(entry.getValue() - outlierValue) < 0.01) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("year", entry.getKey().toString());
                            item.put("type", "Δαπάνες");
                            item.put("value", outlierValue);
                            double zScore = StatisticalAnalysis.calculateZScore(outlierValue, expenses);
                            item.put("zScore", zScore);
                            outliersData.add(item);
                            break;
                        }
                    }
                }
            }
            
            if (statisticsOutliersTable != null) {
                statisticsOutliersTable.setItems(outliersData);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Σφάλμα υπολογισμού στατιστικών");
            alert.setContentText("Δεν ήταν δυνατός ο υπολογισμός των στατιστικών: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void showView(VBox view) {
        // Hide all views
        homeView.setVisible(false);
        homeView.setManaged(false);
        ministriesView.setVisible(false);
        ministriesView.setManaged(false);
        revenuesView.setVisible(false);
        revenuesView.setManaged(false);
        expensesView.setVisible(false);
        expensesView.setManaged(false);
        administrationsView.setVisible(false);
        administrationsView.setManaged(false);
        if (dataManagementView != null) {
            dataManagementView.setVisible(false);
            dataManagementView.setManaged(false);
        }
        if (dataExplorationView != null) {
            dataExplorationView.setVisible(false);
            dataExplorationView.setManaged(false);
        }
        if (statisticsView != null) {
            statisticsView.setVisible(false);
            statisticsView.setManaged(false);
        }
        if (projectionsView != null) {
            projectionsView.setVisible(false);
            projectionsView.setManaged(false);
        }
        
        // Show selected view
        if (view != null) {
            view.setVisible(true);
            view.setManaged(true);
        }
    }
    
    @FXML
    private void onDataManagementYearSelected() {
        if (dataManagementYearComboBox != null && dataManagementYearComboBox.getValue() != null) {
            try {
                dataManagementSelectedYear = Integer.parseInt(dataManagementYearComboBox.getValue());
                loadDataManagementTable();
                updatePublishButtonVisibility();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updatePublishButtonVisibility() {
        if (publishYearButton != null) {
            // Show button only for government users and for years that are not yet published
            boolean shouldShow = isGovernmentUser() && !isYearPublished(dataManagementSelectedYear);
            publishYearButton.setVisible(shouldShow);
            publishYearButton.setManaged(shouldShow);
        }
    }
    
    @FXML
    private void onPublishYear() {
        if (!isGovernmentUser()) {
            return;
        }
        
        int year = dataManagementSelectedYear;
        
        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Δημοσίευση Προϋπολογισμού");
        confirmAlert.setHeaderText("Επιβεβαίωση Δημοσίευσης");
        confirmAlert.setContentText("Είστε σίγουροι ότι θέλετε να δημοσιεύσετε τον προϋπολογισμό του " + year + ";\n\n" +
                                    "Μετά τη δημοσίευση, ο προϋπολογισμός θα είναι ορατός σε όλους τους πολίτες.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                publishYear(year);
                updatePublishButtonVisibility();
                updateYearComboBox(); // Refresh year list for citizens
                
                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Επιτυχία");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Ο προϋπολογισμός του " + year + " δημοσιεύτηκε επιτυχώς!");
                successAlert.showAndWait();
            }
        });
    }
    
    // Data Management Methods
    private void loadDataManagementTable() {
        if (budgetData != null) {
            allDataManagementItems.clear();
            revenuesItems.clear();
            expensesItems.clear();
            
            int selectedYear = dataManagementSelectedYear;
            int previousYear = selectedYear - 1;
            
            // Update year title label
            if (dataManagementYearTitleLabel != null) {
                dataManagementYearTitleLabel.setText("Διαχείριση Δεδομένων Έτους " + selectedYear);
            }
            
            // Get previous year data for comparison
            Map<String, Double> previousYearMap = new HashMap<>();
            List<BudgetData.CategoryInfo> prevMinistries = budgetData.getCategories(previousYear);
            for (BudgetData.CategoryInfo cat : prevMinistries) {
                previousYearMap.put(cat.getName() + "|Υπουργείο", cat.getAmount());
            }
            List<BudgetData.CategoryInfo> prevExpenses = budgetData.getExpensesBreakdown(previousYear);
            for (BudgetData.CategoryInfo cat : prevExpenses) {
                previousYearMap.put(cat.getName() + "|Δαπάνη", cat.getAmount());
            }
            List<BudgetData.CategoryInfo> prevRevenues = budgetData.getRevenueBreakdown(previousYear);
            for (BudgetData.CategoryInfo cat : prevRevenues) {
                previousYearMap.put(cat.getName() + "|Έσοδο", cat.getAmount());
            }
            List<BudgetData.CategoryInfo> prevAdmins = budgetData.getDecentralizedAdministrations(previousYear);
            for (BudgetData.CategoryInfo cat : prevAdmins) {
                previousYearMap.put(cat.getName() + "|Αποκεντρωμένη Διοίκηση", cat.getAmount());
            }
            
            // Check if data exists for selected year
            double totalRevenues = budgetData.getTotalRevenues(selectedYear);
            double totalExpenses = budgetData.getTotalExpenses(selectedYear);
            
            if (totalRevenues == 0 && totalExpenses == 0) {
                updateDataManagementStatistics(selectedYear, previousYear);
                applyFilters();
                return;
            }
            
            // Load revenues (by revenue type) - ONLY revenues
            List<BudgetData.CategoryInfo> revenues = budgetData.getRevenueBreakdown(selectedYear);
            for (BudgetData.CategoryInfo cat : revenues) {
                double prevAmount = previousYearMap.getOrDefault(cat.getName() + "|Έσοδο", 0.0);
                CategoryData revenueData = new CategoryData(
                    cat.getName(), 
                    cat.getAmount(), 
                    cat.getPercentage(), 
                    String.valueOf(selectedYear) + " | Έσοδο",
                    "Έσοδο",
                    prevAmount
                );
                revenuesItems.add(revenueData);
                allDataManagementItems.add(revenueData);
            }
            
            // Load expense breakdown (by expense type) - ONLY expenses
            List<BudgetData.CategoryInfo> expenses = budgetData.getExpensesBreakdown(selectedYear);
            for (BudgetData.CategoryInfo cat : expenses) {
                double prevAmount = previousYearMap.getOrDefault(cat.getName() + "|Δαπάνη", 0.0);
                CategoryData expenseData = new CategoryData(
                    cat.getName(), 
                    cat.getAmount(), 
                    cat.getPercentage(), 
                    String.valueOf(selectedYear) + " | Δαπάνη",
                    "Δαπάνη",
                    prevAmount
                );
                expensesItems.add(expenseData);
                allDataManagementItems.add(expenseData);
            }
            
            // Load ministries (expenses by ministry) - add to expenses
            List<BudgetData.CategoryInfo> ministries = budgetData.getCategories(selectedYear);
            for (BudgetData.CategoryInfo cat : ministries) {
                double prevAmount = previousYearMap.getOrDefault(cat.getName() + "|Υπουργείο", 0.0);
                CategoryData ministryData = new CategoryData(
                    cat.getName(), 
                    cat.getAmount(), 
                    cat.getPercentage(), 
                    String.valueOf(selectedYear) + " | Υπουργείο",
                    "Υπουργείο",
                    prevAmount
                );
                expensesItems.add(ministryData);
                allDataManagementItems.add(ministryData);
            }
            
            // Load decentralized administrations - add to expenses
            List<BudgetData.CategoryInfo> administrations = budgetData.getDecentralizedAdministrations(selectedYear);
            for (BudgetData.CategoryInfo cat : administrations) {
                double prevAmount = previousYearMap.getOrDefault(cat.getName() + "|Αποκεντρωμένη Διοίκηση", 0.0);
                CategoryData adminData = new CategoryData(
                    cat.getName(), 
                    cat.getAmount(), 
                    cat.getPercentage(), 
                    String.valueOf(selectedYear) + " | Αποκεντρωμένη Διοίκηση",
                    "Αποκεντρωμένη Διοίκηση",
                    prevAmount
                );
                expensesItems.add(adminData);
                allDataManagementItems.add(adminData);
            }
            
            // Update statistics
            updateDataManagementStatistics(selectedYear, previousYear);
            
            // Apply filters
            applyFilters();
        }
    }
    
    private void updateDataManagementStatistics(int currentYear, int previousYear) {
        double totalRevenues = budgetData.getTotalRevenues(currentYear);
        double totalExpenses = budgetData.getTotalExpenses(currentYear);
        double balance = budgetData.getBalance(currentYear);
        
        if (dmTotalRevenuesLabel != null) {
            dmTotalRevenuesLabel.setText(AmountFormatter.formatAmount(totalRevenues) + " €");
            // Ensure large and bold styling
            dmTotalRevenuesLabel.getStyleClass().clear();
            dmTotalRevenuesLabel.getStyleClass().addAll("stat-value-large", "revenue-value");
        }
        if (dmTotalExpensesLabel != null) {
            dmTotalExpensesLabel.setText(AmountFormatter.formatAmount(totalExpenses) + " €");
            // Ensure large and bold styling
            dmTotalExpensesLabel.getStyleClass().clear();
            dmTotalExpensesLabel.getStyleClass().addAll("stat-value-large", "expense-value");
        }
        if (dmBalanceLabel != null) {
            dmBalanceLabel.setText(AmountFormatter.formatAmount(balance) + " €");
            // Ensure large and bold styling
            dmBalanceLabel.getStyleClass().clear();
            dmBalanceLabel.getStyleClass().add("stat-value-large");
            if (balance >= 0) {
                dmBalanceLabel.getStyleClass().add("balance-value");
                dmBalanceLabel.setStyle("-fx-text-fill: #059669;");
            } else {
                dmBalanceLabel.setStyle("-fx-text-fill: #dc2626;");
            }
        }
    }
    
    private void applyFilters() {
        String searchText = dmSearchField != null ? dmSearchField.getText().toLowerCase() : "";
        String typeFilter = dmTypeFilter != null && dmTypeFilter.getValue() != null ? dmTypeFilter.getValue() : "Όλα";
        
        // Filter revenues
        ObservableList<CategoryData> filteredRevenues = FXCollections.observableArrayList();
        for (CategoryData item : revenuesItems) {
            boolean matchesSearch = searchText.isEmpty() || 
                item.getCategory().toLowerCase().contains(searchText);
            
            if (matchesSearch) {
                filteredRevenues.add(item);
            }
        }
        filteredRevenues.sort((a, b) -> a.getCategory().compareTo(b.getCategory()));
        if (revenuesTable != null) {
            revenuesTable.setItems(filteredRevenues);
        }
        
        // Filter expenses
        ObservableList<CategoryData> filteredExpenses = FXCollections.observableArrayList();
        for (CategoryData item : expensesItems) {
            boolean matchesSearch = searchText.isEmpty() || 
                item.getCategory().toLowerCase().contains(searchText);
            
            boolean matchesType = typeFilter.equals("Όλα") || 
                typeFilter.equals(item.getType());
            
            if (matchesSearch && matchesType) {
                filteredExpenses.add(item);
            }
        }
        filteredExpenses.sort((a, b) -> a.getCategory().compareTo(b.getCategory()));
        if (expensesTable != null) {
            expensesTable.setItems(filteredExpenses);
        }
        
        // Update record count
        if (dmRecordCountLabel != null) {
            int totalCount = filteredRevenues.size() + filteredExpenses.size();
            dmRecordCountLabel.setText(totalCount + " εγγραφές");
        }
        
        // Update tables
        if (dmRevenuesTable != null) {
            dmRevenuesTable.setItems(filteredRevenues);
        }
        if (dmExpensesTable != null) {
            dmExpensesTable.setItems(filteredExpenses);
        }
        
        // Keep old table for backward compatibility
        if (dataManagementTable != null) {
            ObservableList<CategoryData> filtered = FXCollections.observableArrayList();
            filtered.addAll(filteredRevenues);
            filtered.addAll(filteredExpenses);
            dataManagementTable.setItems(filtered);
        }
    }
    
    @FXML
    private void onSearchData() {
        applyFilters();
    }
    
    @FXML
    private void onFilterData() {
        applyFilters();
    }
    
    @FXML
    private void onClearFilters() {
        if (dmSearchField != null) dmSearchField.clear();
        if (dmTypeFilter != null) dmTypeFilter.setValue("Όλα");
        applyFilters();
    }
    
    @FXML
    private void onAddData() {
        // Show dialog for adding new data
        Dialog<CategoryData> dialog = new Dialog<>();
        dialog.setTitle("Προσθήκη Νέου Δεδομένου");
        dialog.setHeaderText("Εισάγετε τα στοιχεία του νέου δεδομένου");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField categoryField = new TextField();
        categoryField.setPromptText("Κατηγορία");
        categoryField.setPrefWidth(300);
        Label categoryErrorLabel = new Label();
        categoryErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
        categoryErrorLabel.setVisible(false);
        categoryErrorLabel.setWrapText(true);
        categoryErrorLabel.setPrefWidth(300);
        categoryErrorLabel.setMaxWidth(300);
        
        TextField amountField = new TextField();
        amountField.setPromptText("Ποσό (π.χ. 1000.50)");
        amountField.setPrefWidth(300);
        Label amountErrorLabel = new Label();
        amountErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
        amountErrorLabel.setVisible(false);
        amountErrorLabel.setWrapText(true);
        amountErrorLabel.setPrefWidth(300);
        amountErrorLabel.setMaxWidth(300);
        
        int yearValue = dataManagementSelectedYear;
        if (dataManagementYearComboBox != null && dataManagementYearComboBox.getValue() != null) {
            try {
                yearValue = Integer.parseInt(dataManagementYearComboBox.getValue());
            } catch (NumberFormatException e) {
                // Keep default value
            }
        }
        final int selectedYear = yearValue;
        
        // Show selected year as read-only label
        Label yearLabel = new Label("Έτος: " + selectedYear);
        yearLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Έσοδο", "Δαπάνη");
        typeCombo.setPromptText("Τύπος");
        typeCombo.setPrefWidth(300);
        
        // Real-time validation for category
        categoryField.textProperty().addListener((obs, oldText, newText) -> {
            Constraints.ValidationResult result = Constraints.validateCategory(newText);
            Constraints.applyValidationStyle(categoryField, result.isValid(), categoryErrorLabel, result.getErrorMessage());
        });
        
        // Real-time validation for amount
        amountField.textProperty().addListener((obs, oldText, newText) -> {
            Constraints.ValidationResult result = Constraints.validateAmount(newText);
            Constraints.applyValidationStyle(amountField, result.isValid(), amountErrorLabel, result.getErrorMessage());
        });
        
        grid.add(new Label("Κατηγορία:"), 0, 0);
        grid.add(categoryField, 1, 0);
        grid.add(categoryErrorLabel, 1, 1);
        grid.add(new Label("Ποσό:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(amountErrorLabel, 1, 3);
        grid.add(yearLabel, 0, 4, 2, 1);
        grid.add(new Label("Τύπος:"), 0, 5);
        grid.add(typeCombo, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType addButton = new ButtonType("Προσθήκη", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                // Validate all fields
                String category = categoryField.getText();
                String amountText = amountField.getText();
                String type = typeCombo.getValue();
                
                Constraints.ValidationResult categoryResult = Constraints.validateCategory(category);
                Constraints.ValidationResult amountResult = Constraints.validateAmount(amountText);
                Constraints.ValidationResult typeResult = Constraints.validateComboBoxSelection(type, "τύπος");
                
                // Apply validation styles
                Constraints.applyValidationStyle(categoryField, categoryResult.isValid(), categoryErrorLabel, categoryResult.getErrorMessage());
                Constraints.applyValidationStyle(amountField, amountResult.isValid(), amountErrorLabel, amountResult.getErrorMessage());
                
                // Check if all validations pass
                if (!categoryResult.isValid() || !amountResult.isValid() || !typeResult.isValid()) {
                    StringBuilder errorMsg = new StringBuilder("Παρακαλώ διορθώστε τα ακόλουθα σφάλματα:\n\n");
                    if (!categoryResult.isValid()) {
                        errorMsg.append("• ").append(categoryResult.getErrorMessage()).append("\n");
                    }
                    if (!amountResult.isValid()) {
                        errorMsg.append("• ").append(amountResult.getErrorMessage()).append("\n");
                    }
                    if (!typeResult.isValid()) {
                        errorMsg.append("• ").append(typeResult.getErrorMessage()).append("\n");
                    }
                    
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Σφάλμα Επικύρωσης");
                    alert.setHeaderText("Μη έγκυρα δεδομένα");
                    alert.setContentText(errorMsg.toString());
                    alert.showAndWait();
                    return null;
                }
                
                // Check if the selected year is editable
                if (!isYearEditable(selectedYear)) {
                    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                    int maxYear = currentYear + 1;
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Μη Επιτρεπτή Αλλαγή");
                    alert.setHeaderText("Δεν επιτρέπονται αλλαγές για προηγούμενα έτη");
                    alert.setContentText("Μπορείτε να προσθέσετε δεδομένα μόνο για το τρέχον έτος (" + currentYear + ") και το επόμενο έτος (" + maxYear + ").");
                    alert.showAndWait();
                    return null;
                }
                
                // Business constraints validation
                try {
                    double amount = Double.parseDouble(amountText);
                    
                    return new CategoryData(category, amount, 0.0, selectedYear + " | " + type);
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Σφάλμα");
                    alert.setHeaderText("Μη έγκυρα δεδομένα");
                    alert.setContentText("Παρακαλώ εισάγετε έγκυρο ποσό.");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });
        
        java.util.Optional<CategoryData> result = dialog.showAndWait();
        result.ifPresent(data -> {
            if (dataManagementTable != null) {
                dataManagementTable.getItems().add(data);
            }
        });
    }
    
    @FXML
    private void onEditData() {
        CategoryData selected = getSelectedItemFromTables();
        if (selected == null) return;
        
        // Check if the year allows editing
        int year = extractYearFromData(selected);
        if (!isYearEditable(year)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Μη Επιτρεπτή Αλλαγή");
            alert.setHeaderText("Δεν επιτρέπονται αλλαγές για προηγούμενα έτη");
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            alert.setContentText("Το έτος " + year + " έχει περάσει. Μπορείτε να επεξεργαστείτε μόνο δεδομένα για το τρέχον έτος (" + currentYear + ") και μελλοντικά έτη.");
            alert.showAndWait();
            return;
        }
        
        Dialog<CategoryData> dialog = new Dialog<>();
        dialog.setTitle("Επεξεργασία Δεδομένου");
        dialog.setHeaderText("Επεξεργαστείτε τα στοιχεία");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField categoryField = new TextField(selected.getCategory());
        categoryField.setPrefWidth(300);
        Label categoryErrorLabel = new Label();
        categoryErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
        categoryErrorLabel.setVisible(false);
        categoryErrorLabel.setWrapText(true);
        categoryErrorLabel.setPrefWidth(300);
        categoryErrorLabel.setMaxWidth(300);
        
        TextField amountField = new TextField(String.valueOf(selected.getAmount()));
        amountField.setPrefWidth(300);
        Label amountErrorLabel = new Label();
        amountErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
        amountErrorLabel.setVisible(false);
        amountErrorLabel.setWrapText(true);
        amountErrorLabel.setPrefWidth(300);
        amountErrorLabel.setMaxWidth(300);
        
        // Real-time validation for category
        categoryField.textProperty().addListener((obs, oldText, newText) -> {
            Constraints.ValidationResult result = Constraints.validateCategory(newText);
            Constraints.applyValidationStyle(categoryField, result.isValid(), categoryErrorLabel, result.getErrorMessage());
        });
        
        // Real-time validation for amount
        amountField.textProperty().addListener((obs, oldText, newText) -> {
            Constraints.ValidationResult result = Constraints.validateAmount(newText);
            Constraints.applyValidationStyle(amountField, result.isValid(), amountErrorLabel, result.getErrorMessage());
        });
        
        grid.add(new Label("Κατηγορία:"), 0, 0);
        grid.add(categoryField, 1, 0);
        grid.add(categoryErrorLabel, 1, 1);
        grid.add(new Label("Ποσό:"), 0, 2);
        grid.add(amountField, 1, 2);
        grid.add(amountErrorLabel, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType saveButton = new ButtonType("Αποθήκευση", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                // Validate all fields
                String category = categoryField.getText();
                String amountText = amountField.getText();
                
                Constraints.ValidationResult categoryResult = Constraints.validateCategory(category);
                Constraints.ValidationResult amountResult = Constraints.validateAmount(amountText);
                
                // Apply validation styles
                Constraints.applyValidationStyle(categoryField, categoryResult.isValid(), categoryErrorLabel, categoryResult.getErrorMessage());
                Constraints.applyValidationStyle(amountField, amountResult.isValid(), amountErrorLabel, amountResult.getErrorMessage());
                
                // Check if all validations pass
                if (!categoryResult.isValid() || !amountResult.isValid()) {
                    StringBuilder errorMsg = new StringBuilder("Παρακαλώ διορθώστε τα ακόλουθα σφάλματα:\n\n");
                    if (!categoryResult.isValid()) {
                        errorMsg.append("• ").append(categoryResult.getErrorMessage()).append("\n");
                    }
                    if (!amountResult.isValid()) {
                        errorMsg.append("• ").append(amountResult.getErrorMessage()).append("\n");
                    }
                    
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Σφάλμα Επικύρωσης");
                    alert.setHeaderText("Μη έγκυρα δεδομένα");
                    alert.setContentText(errorMsg.toString());
                    alert.showAndWait();
                    return null;
                }
                
                try {
                    double amount = Double.parseDouble(amountText);
                    
                    // Business constraints validation
                    // Check if amount change is reasonable
                    double previousAmount = selected.getPreviousYearAmount();
                    Constraints.ValidationResult amountChangeResult = 
                        Constraints.validateAmountChange(amount, previousAmount);
                    if (!amountChangeResult.isValid()) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Περιορισμός Επιχειρηματικής Λογικής");
                        alert.setHeaderText("Μεγάλη Αλλαγή Ποσού");
                        alert.setContentText(amountChangeResult.getErrorMessage());
                        alert.showAndWait();
                        return null;
                    }
                    
                    selected.categoryProperty().set(category);
                    selected.amountProperty().set(amount);
                    if (dmRevenuesTable != null) dmRevenuesTable.refresh();
                    if (dmExpensesTable != null) dmExpensesTable.refresh();
                    if (dataManagementTable != null) dataManagementTable.refresh();
                    return selected;
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Σφάλμα");
                    alert.setHeaderText("Μη έγκυρο ποσό");
                    alert.setContentText("Παρακαλώ εισάγετε έγκυρο ποσό.");
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    @FXML
    private void onDeleteData() {
        CategoryData selected = getSelectedItemFromTables();
        if (selected == null) return;
        
        // Check if the year allows deletion
        int year = extractYearFromData(selected);
        if (!isYearEditable(year)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Μη Επιτρεπτή Αλλαγή");
            alert.setHeaderText("Δεν επιτρέπονται αλλαγές για προηγούμενα έτη");
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            alert.setContentText("Το έτος " + year + " έχει περάσει. Μπορείτε να διαγράψετε μόνο δεδομένα για το τρέχον έτος (" + currentYear + ") και μελλοντικά έτη.");
            alert.showAndWait();
            return;
        }
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Επιβεβαίωση Διαγραφής");
        confirmDialog.setHeaderText("Είστε σίγουροι ότι θέλετε να διαγράψετε αυτό το δεδομένο;");
        confirmDialog.setContentText("Κατηγορία: " + selected.getCategory());
        
        java.util.Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Remove from appropriate list and table
            String type = selected.getType();
            if (type.equals("Έσοδο")) {
                revenuesItems.remove(selected);
                if (dmRevenuesTable != null) {
                    dmRevenuesTable.getItems().remove(selected);
                }
            } else {
                expensesItems.remove(selected);
                if (dmExpensesTable != null) {
                    dmExpensesTable.getItems().remove(selected);
                }
            }
            allDataManagementItems.remove(selected);
            if (dataManagementTable != null) {
                dataManagementTable.getItems().remove(selected);
            }
        }
    }
    
    @FXML
    private void onImportData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Επιλέξτε Αρχείο για Εισαγωγή");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new FileChooser.ExtensionFilter("JSON Files", "*.json"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        Stage stage = (Stage) (dataManagementTable != null ? dataManagementTable.getScene().getWindow() : null);
        java.io.File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            try {
                String fileName = file.getName().toLowerCase();
                int importedCount = 0;
                boolean isUserDataImport = false;
                
                if (fileName.endsWith(".json")) {
                    // Import user data (comments, scenarios, preferences)
                    boolean success = exportImportService.importFromJSON(file.getAbsolutePath());
                    if (success) {
                        isUserDataImport = true;
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Εισαγωγή Δεδομένων");
                        alert.setHeaderText("Επιτυχής Εισαγωγή");
                        alert.setContentText("Εισήχθησαν επιτυχώς τα δεδομένα (σχόλια, scenarios, preferences) από το αρχείο " + file.getName() + ".\nΠαρακαλώ ανανεώστε την προβολή για να δείτε τις αλλαγές.");
                        alert.showAndWait();
                        
                        // Refresh data if needed
                        if (selectedYear != null) {
                            onYearSelected(null);
                        }
                    } else {
                        throw new Exception("Αποτυχία εισαγωγής JSON");
                    }
                } else if (fileName.endsWith(".csv")) {
                    importedCount = importFromCSV(file);
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Σφάλμα");
                    alert.setHeaderText("Μη υποστηριζόμενος τύπος αρχείου");
                    alert.setContentText("Παρακαλώ επιλέξτε CSV ή JSON αρχείο");
                    alert.showAndWait();
                    return;
                }
                
                if (!isUserDataImport) {
                    if (importedCount > 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Εισαγωγή Αρχείου");
                        alert.setHeaderText("Επιτυχής Εισαγωγή");
                        alert.setContentText("Εισήχθησαν " + importedCount + " εγγραφές από το αρχείο " + file.getName());
                        alert.showAndWait();
                        
                        loadDataManagementTable();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Εισαγωγή Αρχείου");
                        alert.setHeaderText("Καμία εγγραφή");
                        alert.setContentText("Δεν βρέθηκαν δεδομένα προς εισαγωγή στο αρχείο.");
                        alert.showAndWait();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Σφάλμα Εισαγωγής");
                alert.setHeaderText("Δεν ήταν δυνατή η εισαγωγή του αρχείου");
                alert.setContentText("Σφάλμα: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    private int importFromCSV(java.io.File file) throws Exception {
        int importedCount = 0;
        
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(new java.io.FileInputStream(file), "UTF-8"))) {
            
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    // Skip header line
                    isFirstLine = false;
                    continue;
                }
                
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    try {
                        String category = parts[0].trim();
                        double amount = Double.parseDouble(parts[1].trim());
                        String year = parts.length > 2 ? parts[2].trim() : selectedYear;
                        String type = parts.length > 3 ? parts[3].trim() : "Άγνωστο";
                        
                        // Add to table
                        if (dataManagementTable != null) {
                            CategoryData newData = new CategoryData(category, amount, 0.0, year + " | " + type);
                            dataManagementTable.getItems().add(newData);
                            importedCount++;
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                        System.out.println("Skipping invalid line: " + line);
                    }
                }
            }
        }
        
        return importedCount;
    }
    
    @FXML
    private void onSaveComments() {
        if (internalCommentsArea == null) {
            return;
        }
        
        CategoryData selected = getSelectedItemFromTables();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Αποθήκευση Σχολίων");
            alert.setHeaderText("Καμία επιλογή");
            alert.setContentText("Παρακαλώ επιλέξτε μια κατηγορία για να αποθηκεύσετε σχόλια.");
            alert.showAndWait();
            return;
        }
        
        String comments = internalCommentsArea.getText();
        String categoryName = selected.getCategory();
        int year = extractYearFromData(selected);
        
        if (year == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Δεν ήταν δυνατή η αποθήκευση");
            alert.setContentText("Δεν ήταν δυνατή η εξαγωγή του έτους από τα δεδομένα.");
            alert.showAndWait();
            return;
        }
        
        boolean success = userData.saveComment(categoryName, year, comments);
        
        if (success) {
            // Update the CategoryData object as well
            selected.setComments(comments);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Αποθήκευση Σχολίων");
            alert.setHeaderText("Επιτυχής Αποθήκευση");
            alert.setContentText("Τα εσωτερικά σχόλια (" + comments.length() + " χαρακτήρες) αποθηκεύτηκαν επιτυχώς στη βάση δεδομένων.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Δεν ήταν δυνατή η αποθήκευση");
            alert.setContentText("Προέκυψε σφάλμα κατά την αποθήκευση των σχολίων στη βάση δεδομένων.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void onExportData() {
        // Dialog για επιλογή τύπου εξαγωγής
        ChoiceDialog<String> typeDialog = new ChoiceDialog<>("User Data", "User Data", "Budget Data");
        typeDialog.setTitle("Εξαγωγή Δεδομένων");
        typeDialog.setHeaderText("Επιλέξτε τύπο εξαγωγής");
        typeDialog.setContentText("Τι θέλετε να εξάγετε;");
        
        Stage dialogStage = (Stage) (dataManagementTable != null ? dataManagementTable.getScene().getWindow() : null);
        typeDialog.initOwner(dialogStage);
        
        java.util.Optional<String> result = typeDialog.showAndWait();
        if (!result.isPresent()) {
            return; // Ο χρήστης ακύρωσε
        }
        
        String exportType = result.get();
        boolean isBudgetData = exportType.equals("Budget Data");
        
        // Αν είναι Budget Data, δεν χρειάζεται έλεγχος για άδειο πίνακα
        if (!isBudgetData && (dataManagementTable == null || dataManagementTable.getItems().isEmpty())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Εξαγωγή Δεδομένων");
            alert.setHeaderText("Καμία εγγραφή");
            alert.setContentText("Δεν υπάρχουν δεδομένα προς εξαγωγή.");
            alert.showAndWait();
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Εξαγωγή Δεδομένων");
        
        if (isBudgetData) {
            // Για Budget Data, μόνο JSON
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
        } else {
            // Για User Data, CSV και JSON
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("JSON Files", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
        }
        
        Stage stage = (Stage) (dataManagementTable != null ? dataManagementTable.getScene().getWindow() : null);
        java.io.File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            String fileName = file.getName().toLowerCase();
            try {
                if (isBudgetData) {
                    // Export Budget Data to JSON
                    int year = dataManagementSelectedYear;
                    boolean success = exportImportService.exportBudgetDataToJSON(file.getAbsolutePath(), year);
                    if (success) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Εξαγωγή Δεδομένων");
                        alert.setHeaderText("Επιτυχής Εξαγωγή");
                        alert.setContentText("Εξήχθησαν τα δεδομένα προϋπολογισμού για το έτος " + year + " στο αρχείο " + file.getName());
                        alert.showAndWait();
                    } else {
                        throw new Exception("Αποτυχία εξαγωγής Budget Data");
                    }
                } else if (fileName.endsWith(".json")) {
                    // Export User Data to JSON
                    boolean success = exportImportService.exportToJSON(file.getAbsolutePath());
                    if (success) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Εξαγωγή Δεδομένων");
                        alert.setHeaderText("Επιτυχής Εξαγωγή");
                        alert.setContentText("Εξήχθησαν όλα τα δεδομένα (σχόλια, scenarios, preferences) στο αρχείο " + file.getName());
                        alert.showAndWait();
                    } else {
                        throw new Exception("Αποτυχία εξαγωγής JSON");
                    }
                } else {
                    // Default CSV export (User Data from table)
                    java.io.FileWriter writer = new java.io.FileWriter(file);
                    
                    // Write header
                    writer.append("Κατηγορία,Ποσό (€),Συμμετοχή (%),Τύπος,Αλλαγή από Προηγ. Έτος,Κατάσταση\n");
                    
                    // Write data
                    for (CategoryData item : dataManagementTable.getItems()) {
                        writer.append(String.format("\"%s\",%.2f,%.2f,\"%s\",\"%s\",\"%s\"\n",
                            item.getCategory(),
                            item.getAmount(),
                            item.getPercentage(),
                            item.getType(),
                            item.getChangeFromPrevious(),
                            item.getStatus()
                        ));
                    }
                    
                    writer.flush();
                    writer.close();
                    
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Εξαγωγή Δεδομένων");
                    alert.setHeaderText("Επιτυχής Εξαγωγή");
                    alert.setContentText("Εξήχθησαν " + dataManagementTable.getItems().size() + " εγγραφές στο αρχείο " + file.getName());
                    alert.showAndWait();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Σφάλμα Εξαγωγής");
                alert.setHeaderText("Δεν ήταν δυνατή η εξαγωγή");
                alert.setContentText("Σφάλμα: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    /**
     * Handles saving a scenario
     */
    @FXML
    private void onSaveScenario() {
        if (selectedYear == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Αποθήκευση Scenario");
            alert.setHeaderText("Καμία επιλογή");
            alert.setContentText("Παρακαλώ επιλέξτε ένα έτος πρώτα.");
            alert.showAndWait();
            return;
        }
        
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Αποθήκευση Scenario");
        dialog.setHeaderText("Αποθηκεύστε το τρέχον scenario");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("Όνομα scenario (π.χ. 'Αύξηση Φόρων 10%')");
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Περιγραφή scenario");
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);
        
        grid.add(new Label("Όνομα:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Περιγραφή:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType saveButton = new ButtonType("Αποθήκευση", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Σφάλμα");
                    alert.setHeaderText("Κενό όνομα");
                    alert.setContentText("Το όνομα του scenario δεν μπορεί να είναι κενό.");
                    alert.showAndWait();
                    return null;
                }
                
                Map<String, String> result = new HashMap<>();
                result.put("name", name);
                result.put("description", descriptionField.getText().trim());
                return result;
            }
            return null;
        });
        
        java.util.Optional<Map<String, String>> result = dialog.showAndWait();
        result.ifPresent(data -> {
            String scenarioName = data.get("name");
            String description = data.get("description");
            int year = Integer.parseInt(selectedYear);
            
            // Create JSON data for the scenario
            // This would contain the current state of the budget data
            try {
                org.json.JSONObject scenarioData = new org.json.JSONObject();
                scenarioData.put("year", year);
                scenarioData.put("totalRevenues", budgetData.getTotalRevenues(year));
                scenarioData.put("totalExpenses", budgetData.getTotalExpenses(year));
                
                // Add current table data if available
                if (dataManagementTable != null && !dataManagementTable.getItems().isEmpty()) {
                    org.json.JSONArray items = new org.json.JSONArray();
                    for (CategoryData item : dataManagementTable.getItems()) {
                        org.json.JSONObject itemObj = new org.json.JSONObject();
                        itemObj.put("category", item.getCategory());
                        itemObj.put("amount", item.getAmount());
                        itemObj.put("percentage", item.getPercentage());
                        items.put(itemObj);
                    }
                    scenarioData.put("items", items);
                }
                
                boolean success = userData.saveScenario(scenarioName, description, year, scenarioData.toString());
                
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Αποθήκευση Scenario");
                    alert.setHeaderText("Επιτυχής Αποθήκευση");
                    alert.setContentText("Το scenario '" + scenarioName + "' αποθηκεύτηκε επιτυχώς.");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Σφάλμα");
                    alert.setHeaderText("Δεν ήταν δυνατή η αποθήκευση");
                    alert.setContentText("Πιθανώς υπάρχει ήδη scenario με αυτό το όνομα. Παρακαλώ επιλέξτε διαφορετικό όνομα.");
                    alert.showAndWait();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Σφάλμα");
                alert.setHeaderText("Δεν ήταν δυνατή η αποθήκευση");
                alert.setContentText("Σφάλμα: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }
    
    /**
     * Handles loading a saved scenario
     */
    @FXML
    private void onLoadScenario() {
        List<UserData.SavedScenario> scenarios = userData.getAllScenarios();
        
        if (scenarios.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Φόρτωση Scenario");
            alert.setHeaderText("Καμία αποθήκευση");
            alert.setContentText("Δεν υπάρχουν αποθηκευμένα scenarios.");
            alert.showAndWait();
            return;
        }
        
        Dialog<UserData.SavedScenario> dialog = new Dialog<>();
        dialog.setTitle("Φόρτωση Scenario");
        dialog.setHeaderText("Επιλέξτε scenario προς φόρτωση");
        
        ListView<UserData.SavedScenario> listView = new ListView<>();
        ObservableList<UserData.SavedScenario> items = FXCollections.observableArrayList(scenarios);
        listView.setItems(items);
        listView.setCellFactory(param -> new ListCell<UserData.SavedScenario>() {
            @Override
            protected void updateItem(UserData.SavedScenario item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getScenarioName() + " (" + item.getYear() + ") - " + 
                           (item.getDescription() != null && !item.getDescription().isEmpty() ? 
                            item.getDescription() : "Χωρίς περιγραφή"));
                }
            }
        });
        
        dialog.getDialogPane().setContent(listView);
        dialog.getDialogPane().setPrefSize(500, 400);
        
        ButtonType loadButton = new ButtonType("Φόρτωση", ButtonBar.ButtonData.OK_DONE);
        ButtonType deleteButton = new ButtonType("Διαγραφή", ButtonBar.ButtonData.LEFT);
        dialog.getDialogPane().getButtonTypes().addAll(loadButton, deleteButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            UserData.SavedScenario selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return null;
            }
            
            if (dialogButton == deleteButton) {
                // Delete scenario
                boolean success = userData.deleteScenario(selected.getScenarioName());
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Διαγραφή Scenario");
                    alert.setHeaderText("Επιτυχής Διαγραφή");
                    alert.setContentText("Το scenario '" + selected.getScenarioName() + "' διαγράφηκε.");
                    alert.showAndWait();
                    // Refresh list
                    items.remove(selected);
                }
                return null;
            } else if (dialogButton == loadButton) {
                return selected;
            }
            return null;
        });
        
        java.util.Optional<UserData.SavedScenario> result = dialog.showAndWait();
        result.ifPresent(scenario -> {
            try {
                // Parse scenario data and apply it
                org.json.JSONObject scenarioData = new org.json.JSONObject(scenario.getScenarioData());
                
                // Update selected year if different
                int scenarioYear = scenarioData.getInt("year");
                if (yearComboBox != null && !String.valueOf(scenarioYear).equals(selectedYear)) {
                    yearComboBox.setValue(String.valueOf(scenarioYear));
                    selectedYear = String.valueOf(scenarioYear);
                    onYearSelected(null);
                }
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Φόρτωση Scenario");
                alert.setHeaderText("Επιτυχής Φόρτωση");
                alert.setContentText("Το scenario '" + scenario.getScenarioName() + "' φορτώθηκε επιτυχώς.\n" +
                                   "Σημείωση: Οι αλλαγές στα δεδομένα θα πρέπει να γίνουν χειροκίνητα.");
                alert.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Σφάλμα");
                alert.setHeaderText("Δεν ήταν δυνατή η φόρτωση");
                alert.setContentText("Σφάλμα: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }
    
    @FXML
    private void onBulkEdit() {
        ObservableList<CategoryData> selected = getSelectedItemsFromTables();
        if (selected == null || selected.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Μαζική Επεξεργασία");
            alert.setHeaderText("Καμία επιλογή");
            alert.setContentText("Παρακαλώ επιλέξτε τουλάχιστον μία εγγραφή για μαζική επεξεργασία.");
            alert.showAndWait();
            return;
        }
        
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Μαζική Επεξεργασία");
        dialog.setHeaderText("Επεξεργασία " + selected.size() + " εγγραφών");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        Label infoLabel = new Label("Επιλέξτε την ενέργεια που θέλετε να εκτελέσετε:");
        ComboBox<String> actionCombo = new ComboBox<>();
        actionCombo.getItems().addAll("Αύξηση ποσού κατά %", "Μείωση ποσού κατά %", "Ορισμός νέου ποσού");
        actionCombo.setPrefWidth(300);
        
        TextField valueField = new TextField();
        valueField.setPromptText("Τιμή");
        valueField.setPrefWidth(300);
        Label valueErrorLabel = new Label();
        valueErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
        valueErrorLabel.setVisible(false);
        valueErrorLabel.setWrapText(true);
        valueErrorLabel.setPrefWidth(300);
        valueErrorLabel.setMaxWidth(300);
        
        // Real-time validation for value field
        valueField.textProperty().addListener((obs, oldText, newText) -> {
            if (actionCombo.getValue() != null) {
                String action = actionCombo.getValue();
                Constraints.ValidationResult result;
                
                if (action.contains("%")) {
                    // Percentage validation
                    result = Constraints.validatePercentage(newText);
                } else {
                    // Amount validation
                    result = Constraints.validateAmount(newText);
                }
                
                Constraints.applyValidationStyle(valueField, result.isValid(), valueErrorLabel, result.getErrorMessage());
            }
        });
        
        // Also validate when action changes
        actionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !valueField.getText().isEmpty()) {
                Constraints.ValidationResult result;
                if (newVal.contains("%")) {
                    result = Constraints.validatePercentage(valueField.getText());
                } else {
                    result = Constraints.validateAmount(valueField.getText());
                }
                Constraints.applyValidationStyle(valueField, result.isValid(), valueErrorLabel, result.getErrorMessage());
            }
        });
        
        grid.add(infoLabel, 0, 0, 2, 1);
        grid.add(new Label("Ενέργεια:"), 0, 1);
        grid.add(actionCombo, 1, 1);
        grid.add(new Label("Τιμή:"), 0, 2);
        grid.add(valueField, 1, 2);
        grid.add(valueErrorLabel, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType applyButton = new ButtonType("Εφαρμογή", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButton) {
                String action = actionCombo.getValue();
                String value = valueField.getText();
                
                // Validate action selection
                Constraints.ValidationResult actionResult = Constraints.validateComboBoxSelection(action, "ενέργεια");
                
                if (!actionResult.isValid()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Σφάλμα Επικύρωσης");
                    alert.setHeaderText("Μη έγκυρα δεδομένα");
                    alert.setContentText(actionResult.getErrorMessage());
                    alert.showAndWait();
                    return null;
                }
                
                // Validate value based on action type
                Constraints.ValidationResult valueResult;
                if (action.contains("%")) {
                    valueResult = Constraints.validatePercentage(value);
                } else {
                    valueResult = Constraints.validateAmount(value);
                }
                
                Constraints.applyValidationStyle(valueField, valueResult.isValid(), valueErrorLabel, valueResult.getErrorMessage());
                
                if (!valueResult.isValid()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Σφάλμα Επικύρωσης");
                    alert.setHeaderText("Μη έγκυρα δεδομένα");
                    alert.setContentText(valueResult.getErrorMessage());
                    alert.showAndWait();
                    return null;
                }
                
                if (action != null && value != null && !value.trim().isEmpty()) {
                    return action + "|" + value;
                }
            }
            return null;
        });
        
        java.util.Optional<String> result = dialog.showAndWait();
        result.ifPresent(actionValue -> {
            String[] parts = actionValue.split("\\|");
            String action = parts[0];
            double value = Double.parseDouble(parts[1]);
            
            for (CategoryData item : selected) {
                double newAmount = item.getAmount();
                if (action.contains("Αύξηση")) {
                    newAmount = item.getAmount() * (1 + value / 100);
                } else if (action.contains("Μείωση")) {
                    newAmount = item.getAmount() * (1 - value / 100);
                } else if (action.contains("Ορισμός")) {
                    newAmount = value;
                }
                item.amountProperty().set(newAmount);
            }
            
            if (dmRevenuesTable != null) dmRevenuesTable.refresh();
            if (dmExpensesTable != null) dmExpensesTable.refresh();
            if (dataManagementTable != null) dataManagementTable.refresh();
            updateDataManagementStatistics(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.YEAR) - 1);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Μαζική Επεξεργασία");
            alert.setHeaderText("Επιτυχής Επεξεργασία");
            alert.setContentText("Επεξεργάστηκαν " + selected.size() + " εγγραφές.");
            alert.showAndWait();
        });
    }
    
    @FXML
    private void onSelectAll() {
        if (dataManagementTable != null) {
            dataManagementTable.getSelectionModel().selectAll();
        }
    }
    
    @FXML
    private void onDeselectAll() {
        if (dataManagementTable != null) {
            if (dmRevenuesTable != null) dmRevenuesTable.getSelectionModel().clearSelection();
            if (dmExpensesTable != null) dmExpensesTable.getSelectionModel().clearSelection();
            if (dataManagementTable != null) dataManagementTable.getSelectionModel().clearSelection();
        }
    }
}

