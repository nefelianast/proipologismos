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
import javafx.stage.FileChooser;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;

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
            double changePercent = BudgetStatisticsCalculator.calculatePercentageChange(currentAmount, previousAmount);
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
            double changePercent = BudgetStatisticsCalculator.calculatePercentageChange(amount.get(), previousYearAmount.get());
            return BudgetAmountFormatter.formatChange(changePercent, changeValue);
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
    private javafx.scene.control.Button authButton;
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
    private Button projectionsButton;
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
    private Button exploreTotalButton;
    @FXML
    private Button exploreMinistryButton;
    @FXML
    private Button exploreRevenueCategoryButton;
    @FXML
    private Button exploreExpenseCategoryButton;
    @FXML
    private Button exploreYearComparisonButton;
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
    

    private BudgetDataService dataService;
    
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
                    setText(BudgetAmountFormatter.formatCurrency(amount));
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
                    setText(BudgetAmountFormatter.formatPercentage(percentage));
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
        dataService = BudgetDataService.getInstance();
        
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
                        setText(BudgetAmountFormatter.formatCurrency(amount));
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
                        setText(BudgetAmountFormatter.formatPercentage(percentage));
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
                    setText(BudgetAmountFormatter.formatCurrency(amount));
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
                    setText(BudgetAmountFormatter.formatPercentageOneDecimal(percentage));
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
                    setText(BudgetAmountFormatter.formatCurrency(amount));
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
                    setText(BudgetAmountFormatter.formatPercentageOneDecimal(percentage));
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
                    setText(BudgetAmountFormatter.formatCurrency(amount));
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
                    setText(BudgetAmountFormatter.formatPercentageOneDecimal(percentage));
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
                    setText(BudgetAmountFormatter.formatCurrency(amount));
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
                    setText(BudgetAmountFormatter.formatPercentageOneDecimal(percentage));
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
        if (authButton != null) {
            if (currentUserType == UserType.CITIZEN) {
                authButton.setText("Σύνδεση ως Κυβέρνηση");
            } else {
                authButton.setText("Αποσύνδεση");
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
                
                Stage stage = (Stage) (authButton != null ? authButton.getScene().getWindow() : (yearComboBox != null ? yearComboBox.getScene().getWindow() : null));
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
                DataValidator.ValidationResult result = DataValidator.validateUsername(newText);
                DataValidator.applyValidationStyle(usernameField, result.isValid(), usernameErrorLabel, result.getErrorMessage());
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
                DataValidator.ValidationResult result = DataValidator.validatePassword(newText);
                DataValidator.applyValidationStyle(passwordField, result.isValid(), passwordErrorLabel, result.getErrorMessage());
            });

            javafx.scene.control.Button loginButton = new javafx.scene.control.Button("Σύνδεση");
            loginButton.setPrefWidth(300);
            loginButton.setPrefHeight(40);
            loginButton.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            loginButton.setOnAction(e -> {
                String username = usernameField.getText();
                String password = passwordField.getText();
                
                // Validate inputs
                DataValidator.ValidationResult usernameResult = DataValidator.validateUsername(username);
                DataValidator.ValidationResult passwordResult = DataValidator.validatePassword(password);
                
                // Apply validation styles
                DataValidator.applyValidationStyle(usernameField, usernameResult.isValid(), usernameErrorLabel, usernameResult.getErrorMessage());
                DataValidator.applyValidationStyle(passwordField, passwordResult.isValid(), passwordErrorLabel, passwordResult.getErrorMessage());
                
                // Check if all validations pass
                if (usernameResult.isValid() && passwordResult.isValid()) {
                    // Set user type to government
                    currentUserType = UserType.GOVERNMENT;
                    updateAuthButton();
                    updateUserTypeLabel();
                    updateGovernmentFeatures();
                    
                    // Close login dialog
                    loginStage.close();
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

            loginPane.getChildren().addAll(titleLabel, usernamePane, usernameErrorLabel, passwordPane, passwordErrorLabel, loginButton);

            Scene loginScene = new Scene(loginPane, 400, 400);
            loginStage.setScene(loginScene);
            loginStage.initOwner(authButton != null ? authButton.getScene().getWindow() : null);
            loginStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
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
        updateCategoryTable();
        updateRevenuesTable();
        updateExpensesTable();
        updateAdministrationsTable();
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
        totalRevenuesLabel.setText(BudgetAmountFormatter.formatCurrency(totalRevenues));
        String revenuesChangeText = BudgetAmountFormatter.formatPercentageChangeOneDecimal(revenuesChange) + " από πέρυσι";
        revenuesDeltaLabel.setText(revenuesChangeText);
        revenuesDeltaLabel.getStyleClass().removeAll("negative", "positive");
        revenuesDeltaLabel.getStyleClass().add(revenuesChange >= 0 ? "positive" : "negative");
        revenuesDeltaLabel.setStyle("-fx-text-fill: white;");
        
        // Update expenses
        totalExpensesLabel.setText(BudgetAmountFormatter.formatCurrency(totalExpenses));
        String expensesChangeText = BudgetAmountFormatter.formatPercentageChangeOneDecimal(expensesChange) + " από πέρυσι";
        expensesDeltaLabel.setText(expensesChangeText);
        expensesDeltaLabel.getStyleClass().removeAll("negative", "positive");
        expensesDeltaLabel.getStyleClass().add(expensesChange >= 0 ? "positive" : "negative");
        expensesDeltaLabel.setStyle("-fx-text-fill: white;");
        
        // Update balance
        if (balance >= 0) {
            balanceLabel.setText("+" + BudgetAmountFormatter.formatCurrency(balance));
            balanceStatusLabel.setText("Πλεόνασμα");
            balanceStatusLabel.getStyleClass().removeAll("negative");
            balanceStatusLabel.getStyleClass().add("positive");
            balanceStatusLabel.setStyle("-fx-text-fill: white;");
        } else {
            balanceLabel.setText(BudgetAmountFormatter.formatCurrency(balance));
            balanceStatusLabel.setText("Έλλειμμα");
            balanceStatusLabel.getStyleClass().removeAll("positive");
            balanceStatusLabel.getStyleClass().add("negative");
            balanceStatusLabel.setStyle("-fx-text-fill: white;");
        }
    }


    private void updateCategoryTable() {
        if (selectedYear == null) return;
        
        int yearInt = Integer.parseInt(selectedYear);
        List<BudgetDataService.CategoryInfo> categories = dataService.getCategories(yearInt);
        
        // Get previous year data for comparison
        List<BudgetDataService.CategoryInfo> prevCategories = dataService.getCategories(yearInt - 1);
        boolean hasPreviousYearData = !prevCategories.isEmpty();
        Map<String, Double> prevYearMap = new HashMap<>();
        for (BudgetDataService.CategoryInfo cat : prevCategories) {
            prevYearMap.put(cat.getName(), cat.getAmount());
        }
        
        // Convert to table data
        ObservableList<CategoryData> tableData = FXCollections.observableArrayList();
        for (BudgetDataService.CategoryInfo cat : categories) {
            String changeText;
            if (!hasPreviousYearData || !prevYearMap.containsKey(cat.getName())) {
                changeText = "-";
            } else {
                double prevAmount = prevYearMap.get(cat.getName());
                if (prevAmount > 0) {
                    double change = BudgetStatisticsCalculator.calculatePercentageChange(cat.getAmount(), prevAmount);
                    changeText = BudgetAmountFormatter.formatPercentageChangeOneDecimal(change);
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
        List<BudgetDataService.CategoryInfo> revenues = dataService.getRevenueBreakdown(yearInt);
        
        ObservableList<CategoryData> tableData = FXCollections.observableArrayList();
        for (BudgetDataService.CategoryInfo revenue : revenues) {
            tableData.add(new CategoryData(revenue.getName(), revenue.getAmount(), revenue.getPercentage(), ""));
        }
        
        revenuesTable.setItems(tableData);
    }
    
    private void updateExpensesTable() {
        if (selectedYear == null) return;
        
        int yearInt = Integer.parseInt(selectedYear);
        List<BudgetDataService.CategoryInfo> expenses = dataService.getExpensesBreakdown(yearInt);
        
        ObservableList<CategoryData> tableData = FXCollections.observableArrayList();
        for (BudgetDataService.CategoryInfo expense : expenses) {
            tableData.add(new CategoryData(expense.getName(), expense.getAmount(), expense.getPercentage(), ""));
        }
        
        expensesTable.setItems(tableData);
    }
    
    private void updateAdministrationsTable() {
        if (selectedYear == null) return;
        
        int yearInt = Integer.parseInt(selectedYear);
        List<BudgetDataService.CategoryInfo> administrations = dataService.getDecentralizedAdministrations(yearInt);
        
        ObservableList<CategoryData> tableData = FXCollections.observableArrayList();
        for (BudgetDataService.CategoryInfo admin : administrations) {
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
    }

    @FXML
    private void onNavigateRevenues() {
        showView(revenuesView);
    }

    @FXML
    private void onNavigateExpenses() {
        showView(expensesView);
    }

    @FXML
    private void onNavigateAdministrations() {
        showView(administrationsView);
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
        if (exploreYearComparisonButton != null) exploreYearComparisonButton.getStyleClass().remove("exploration-category-button-active");
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
            currentExplorationView = "year_comparison";
            loadYearComparisonView();
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
            case "year_comparison": loadYearComparisonView(); break;
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
        
        if (exploreColumn1 != null) exploreColumn1.setText("Έτος");
        if (exploreColumn2 != null) exploreColumn2.setText("Συνολικά Έσοδα (€)");
        if (exploreColumn3 != null) exploreColumn3.setText("Συνολικές Δαπάνες (€)");
        if (exploreColumn4 != null) exploreColumn4.setText("Υπόλοιπο (€)");
        if (exploreColumn5 != null) exploreColumn5.setText("Αλλαγή από Προηγ. Έτος");
        
        ObservableList<CategoryData> data = FXCollections.observableArrayList();
        int startYear = 2023;
        int endYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
        
        for (int year = startYear; year <= endYear; year++) {
            double revenues = dataService.getTotalRevenues(year);
            double expenses = dataService.getTotalExpenses(year);
            double balance = BudgetStatisticsCalculator.calculateBalance(revenues, expenses);
            double prevRevenues = year > startYear ? dataService.getTotalRevenues(year - 1) : 0;
            double change = BudgetStatisticsCalculator.calculatePercentageChange(revenues, prevRevenues);
            
            String changeText = prevRevenues > 0 ? BudgetAmountFormatter.formatPercentageChange(change) : "Νέο";
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
                List<BudgetDataService.CategoryInfo> ministries = dataService.getCategories(currentYear);
                for (BudgetDataService.CategoryInfo m : ministries) {
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
            List<BudgetDataService.CategoryInfo> ministries = dataService.getCategories(year);
            for (BudgetDataService.CategoryInfo m : ministries) {
                if (m.getName().equals(ministryName)) {
                    double prevAmount = year > startYear ? getMinistryAmountForYear(ministryName, year - 1) : 0;
                    String change = prevAmount > 0 ? 
                        BudgetAmountFormatter.formatPercentageChange(BudgetStatisticsCalculator.calculatePercentageChange(m.getAmount(), prevAmount)) : "Νέο";
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
        List<BudgetDataService.CategoryInfo> ministries = dataService.getCategories(year);
        for (BudgetDataService.CategoryInfo m : ministries) {
            if (m.getName().equals(ministryName)) {
                return m.getAmount();
            }
        }
        return 0;
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
                List<BudgetDataService.CategoryInfo> revenues = dataService.getRevenueBreakdown(currentYear);
                for (BudgetDataService.CategoryInfo r : revenues) {
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
            List<BudgetDataService.CategoryInfo> revenues = dataService.getRevenueBreakdown(year);
            for (BudgetDataService.CategoryInfo r : revenues) {
                if (r.getName().equals(categoryName)) {
                    double prevAmount = year > startYear ? getRevenueCategoryAmountForYear(categoryName, year - 1) : 0;
                    String change = prevAmount > 0 ? 
                        BudgetAmountFormatter.formatPercentageChange(BudgetStatisticsCalculator.calculatePercentageChange(r.getAmount(), prevAmount)) : "Νέο";
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
        List<BudgetDataService.CategoryInfo> revenues = dataService.getRevenueBreakdown(year);
        for (BudgetDataService.CategoryInfo r : revenues) {
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
                List<BudgetDataService.CategoryInfo> expenses = dataService.getExpensesBreakdown(currentYear);
                for (BudgetDataService.CategoryInfo e : expenses) {
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
            List<BudgetDataService.CategoryInfo> expenses = dataService.getExpensesBreakdown(year);
            for (BudgetDataService.CategoryInfo e : expenses) {
                if (e.getName().equals(categoryName)) {
                    double prevAmount = year > startYear ? getExpenseCategoryAmountForYear(categoryName, year - 1) : 0;
                    String change = prevAmount > 0 ? 
                        BudgetAmountFormatter.formatPercentageChange(BudgetStatisticsCalculator.calculatePercentageChange(e.getAmount(), prevAmount)) : "Νέο";
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
        List<BudgetDataService.CategoryInfo> expenses = dataService.getExpensesBreakdown(year);
        for (BudgetDataService.CategoryInfo e : expenses) {
            if (e.getName().equals(categoryName)) {
                return e.getAmount();
            }
        }
        return 0;
    }
    
    private void loadYearComparisonView() {
        if (exploreViewTitleLabel != null) {
            exploreViewTitleLabel.setText("Σύγκριση Ετών");
        }
        if (exploreViewDescriptionLabel != null) {
            exploreViewDescriptionLabel.setText("Σύγκριση συνολικών εσόδων και δαπανών μεταξύ ετών");
        }
        
        if (exploreColumn1 != null) exploreColumn1.setText("Έτος");
        if (exploreColumn2 != null) exploreColumn2.setText("Συνολικά Έσοδα (€)");
        if (exploreColumn3 != null) exploreColumn3.setText("Συνολικές Δαπάνες (€)");
        if (exploreColumn4 != null) exploreColumn4.setText("Υπόλοιπο (€)");
        if (exploreColumn5 != null) exploreColumn5.setText("Αλλαγή Έσόδων (%)");
        
        ObservableList<CategoryData> data = FXCollections.observableArrayList();
        int startYear = 2023;
        int endYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
        
        for (int year = startYear; year <= endYear; year++) {
            double revenues = dataService.getTotalRevenues(year);
            double expenses = dataService.getTotalExpenses(year);
            double balance = BudgetStatisticsCalculator.calculateBalance(revenues, expenses);
            double prevRevenues = year > startYear ? dataService.getTotalRevenues(year - 1) : 0;
            double change = BudgetStatisticsCalculator.calculatePercentageChange(revenues, prevRevenues);
            
            String changeText = prevRevenues > 0 ? BudgetAmountFormatter.formatPercentageChange(change) : "Νέο";
            data.add(new CategoryData(
                String.valueOf(year),
                revenues,
                expenses,
                changeText,
                String.format("%.2f", balance),
                0
            ));
        }
        
        if (exploreResultsTable != null) {
            exploreResultsTable.setItems(data);
        }
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
        
        List<BudgetDataService.CategoryInfo> currentMinistries = dataService.getCategories(currentYear);
        List<BudgetDataService.CategoryInfo> prevMinistries = dataService.getCategories(previousYear);
        Map<String, Double> prevMinistryMap = new HashMap<>();
        for (BudgetDataService.CategoryInfo m : prevMinistries) {
            prevMinistryMap.put(m.getName(), m.getAmount());
        }
        
        for (BudgetDataService.CategoryInfo m : currentMinistries) {
            double prevAmount = prevMinistryMap.getOrDefault(m.getName(), 0.0);
            double change = BudgetStatisticsCalculator.calculatePercentageChange(m.getAmount(), prevAmount);
            String trend = change > 5 ? "↑ Αύξηση" : change < -5 ? "↓ Μείωση" : "— Σταθερό";
            data.add(new CategoryData(
                m.getName(),
                m.getAmount(),
                prevAmount,
                BudgetAmountFormatter.formatPercentageChange(change),
                trend,
                0
            ));
        }
        
        if (exploreResultsTable != null) {
            exploreResultsTable.setItems(data);
        }
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
        // Coming soon 
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
        if (dataService != null) {
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
            List<BudgetDataService.CategoryInfo> prevMinistries = dataService.getCategories(previousYear);
            for (BudgetDataService.CategoryInfo cat : prevMinistries) {
                previousYearMap.put(cat.getName() + "|Υπουργείο", cat.getAmount());
            }
            List<BudgetDataService.CategoryInfo> prevExpenses = dataService.getExpensesBreakdown(previousYear);
            for (BudgetDataService.CategoryInfo cat : prevExpenses) {
                previousYearMap.put(cat.getName() + "|Δαπάνη", cat.getAmount());
            }
            List<BudgetDataService.CategoryInfo> prevRevenues = dataService.getRevenueBreakdown(previousYear);
            for (BudgetDataService.CategoryInfo cat : prevRevenues) {
                previousYearMap.put(cat.getName() + "|Έσοδο", cat.getAmount());
            }
            List<BudgetDataService.CategoryInfo> prevAdmins = dataService.getDecentralizedAdministrations(previousYear);
            for (BudgetDataService.CategoryInfo cat : prevAdmins) {
                previousYearMap.put(cat.getName() + "|Αποκεντρωμένη Διοίκηση", cat.getAmount());
            }
            
            // Check if data exists for selected year
            double totalRevenues = dataService.getTotalRevenues(selectedYear);
            double totalExpenses = dataService.getTotalExpenses(selectedYear);
            
            if (totalRevenues == 0 && totalExpenses == 0) {
                updateDataManagementStatistics(selectedYear, previousYear);
                applyFilters();
                return;
            }
            
            // Load revenues (by revenue type) - ONLY revenues
            List<BudgetDataService.CategoryInfo> revenues = dataService.getRevenueBreakdown(selectedYear);
            for (BudgetDataService.CategoryInfo cat : revenues) {
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
            List<BudgetDataService.CategoryInfo> expenses = dataService.getExpensesBreakdown(selectedYear);
            for (BudgetDataService.CategoryInfo cat : expenses) {
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
            List<BudgetDataService.CategoryInfo> ministries = dataService.getCategories(selectedYear);
            for (BudgetDataService.CategoryInfo cat : ministries) {
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
            List<BudgetDataService.CategoryInfo> administrations = dataService.getDecentralizedAdministrations(selectedYear);
            for (BudgetDataService.CategoryInfo cat : administrations) {
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
        double totalRevenues = dataService.getTotalRevenues(currentYear);
        double totalExpenses = dataService.getTotalExpenses(currentYear);
        double balance = dataService.getBalance(currentYear);
        
        if (dmTotalRevenuesLabel != null) {
            dmTotalRevenuesLabel.setText(BudgetAmountFormatter.formatAmount(totalRevenues) + " €");
            // Ensure large and bold styling
            dmTotalRevenuesLabel.getStyleClass().clear();
            dmTotalRevenuesLabel.getStyleClass().addAll("stat-value-large", "revenue-value");
        }
        if (dmTotalExpensesLabel != null) {
            dmTotalExpensesLabel.setText(BudgetAmountFormatter.formatAmount(totalExpenses) + " €");
            // Ensure large and bold styling
            dmTotalExpensesLabel.getStyleClass().clear();
            dmTotalExpensesLabel.getStyleClass().addAll("stat-value-large", "expense-value");
        }
        if (dmBalanceLabel != null) {
            dmBalanceLabel.setText(BudgetAmountFormatter.formatAmount(balance) + " €");
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
            DataValidator.ValidationResult result = DataValidator.validateCategory(newText);
            DataValidator.applyValidationStyle(categoryField, result.isValid(), categoryErrorLabel, result.getErrorMessage());
        });
        
        // Real-time validation for amount
        amountField.textProperty().addListener((obs, oldText, newText) -> {
            DataValidator.ValidationResult result = DataValidator.validateAmount(newText);
            DataValidator.applyValidationStyle(amountField, result.isValid(), amountErrorLabel, result.getErrorMessage());
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
                
                DataValidator.ValidationResult categoryResult = DataValidator.validateCategory(category);
                DataValidator.ValidationResult amountResult = DataValidator.validateAmount(amountText);
                DataValidator.ValidationResult typeResult = DataValidator.validateComboBoxSelection(type, "τύπος");
                
                // Apply validation styles
                DataValidator.applyValidationStyle(categoryField, categoryResult.isValid(), categoryErrorLabel, categoryResult.getErrorMessage());
                DataValidator.applyValidationStyle(amountField, amountResult.isValid(), amountErrorLabel, amountResult.getErrorMessage());
                
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
            DataValidator.ValidationResult result = DataValidator.validateCategory(newText);
            DataValidator.applyValidationStyle(categoryField, result.isValid(), categoryErrorLabel, result.getErrorMessage());
        });
        
        // Real-time validation for amount
        amountField.textProperty().addListener((obs, oldText, newText) -> {
            DataValidator.ValidationResult result = DataValidator.validateAmount(newText);
            DataValidator.applyValidationStyle(amountField, result.isValid(), amountErrorLabel, result.getErrorMessage());
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
                
                DataValidator.ValidationResult categoryResult = DataValidator.validateCategory(category);
                DataValidator.ValidationResult amountResult = DataValidator.validateAmount(amountText);
                
                // Apply validation styles
                DataValidator.applyValidationStyle(categoryField, categoryResult.isValid(), categoryErrorLabel, categoryResult.getErrorMessage());
                DataValidator.applyValidationStyle(amountField, amountResult.isValid(), amountErrorLabel, amountResult.getErrorMessage());
                
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
                    BusinessConstraintsValidator.ValidationResult amountChangeResult = 
                        BusinessConstraintsValidator.validateAmountChange(amount, previousAmount);
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
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        Stage stage = (Stage) (dataManagementTable != null ? dataManagementTable.getScene().getWindow() : null);
        java.io.File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            try {
                String fileName = file.getName().toLowerCase();
                int importedCount = 0;
                
                if (fileName.endsWith(".csv")) {
                    importedCount = importFromCSV(file);
                } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                    // For Excel files, we'll show a message that it's not fully implemented yet
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Εισαγωγή Αρχείου");
                    alert.setHeaderText("Υποστήριξη Excel");
                    alert.setContentText("Η εισαγωγή από Excel αρχεία (.xlsx, .xls) θα προστεθεί σύντομα. Παρακαλώ χρησιμοποιήστε CSV αρχεία.");
                    alert.showAndWait();
                    return;
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Σφάλμα");
                    alert.setHeaderText("Μη υποστηριζόμενος τύπος αρχείου");
                    alert.setContentText("Παρακαλώ επιλέξτε CSV αρχείο (.csv)");
                    alert.showAndWait();
                    return;
                }
                
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
        if (internalCommentsArea != null) {
            String comments = internalCommentsArea.getText();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Αποθήκευση Σχολίων");
            alert.setHeaderText("Επιτυχής Αποθήκευση");
            alert.setContentText("Τα εσωτερικά σχόλια (" + comments.length() + " χαρακτήρες) αποθηκεύτηκαν επιτυχώς.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void onExportData() {
        if (dataManagementTable == null || dataManagementTable.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Εξαγωγή Δεδομένων");
            alert.setHeaderText("Καμία εγγραφή");
            alert.setContentText("Δεν υπάρχουν δεδομένα προς εξαγωγή.");
            alert.showAndWait();
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Εξαγωγή Δεδομένων");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        Stage stage = (Stage) (dataManagementTable != null ? dataManagementTable.getScene().getWindow() : null);
        java.io.File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            try {
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
                DataValidator.ValidationResult result;
                
                if (action.contains("%")) {
                    // Percentage validation
                    result = DataValidator.validatePercentage(newText);
                } else {
                    // Amount validation
                    result = DataValidator.validateAmount(newText);
                }
                
                DataValidator.applyValidationStyle(valueField, result.isValid(), valueErrorLabel, result.getErrorMessage());
            }
        });
        
        // Also validate when action changes
        actionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !valueField.getText().isEmpty()) {
                DataValidator.ValidationResult result;
                if (newVal.contains("%")) {
                    result = DataValidator.validatePercentage(valueField.getText());
                } else {
                    result = DataValidator.validateAmount(valueField.getText());
                }
                DataValidator.applyValidationStyle(valueField, result.isValid(), valueErrorLabel, result.getErrorMessage());
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
                DataValidator.ValidationResult actionResult = DataValidator.validateComboBoxSelection(action, "ενέργεια");
                
                if (!actionResult.isValid()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Σφάλμα Επικύρωσης");
                    alert.setHeaderText("Μη έγκυρα δεδομένα");
                    alert.setContentText(actionResult.getErrorMessage());
                    alert.showAndWait();
                    return null;
                }
                
                // Validate value based on action type
                DataValidator.ValidationResult valueResult;
                if (action.contains("%")) {
                    valueResult = DataValidator.validatePercentage(value);
                } else {
                    valueResult = DataValidator.validateAmount(value);
                }
                
                DataValidator.applyValidationStyle(valueField, valueResult.isValid(), valueErrorLabel, valueResult.getErrorMessage());
                
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
