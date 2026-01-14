package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.NodeOrientation;
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
import java.sql.PreparedStatement;
import java.math.BigDecimal;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.AreaChart;
import javafx.util.StringConverter;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.geometry.Bounds;

public class HomeController {
    
    // user type 
    public enum UserType {
        CITIZEN,
        GOVERNMENT
    }
    
    private static UserType currentUserType = UserType.CITIZEN;
    
    private Authentication authentication = new Authentication();
    
    // sets user type
    public static void setUserType(UserType userType) {
        currentUserType = userType;
    }
    
    // gets user type
    public static UserType getUserType() {
        return currentUserType;
    }
    
    // checks if user is government user
    public static boolean isGovernmentUser() {
        return currentUserType == UserType.GOVERNMENT;
    }


    // κλάση για τα δεδομένα κατηγορίας
    public static class CategoryData {
        private final StringProperty category;
        private final DoubleProperty amount;
        private final DoubleProperty percentage;
        private final StringProperty change;
        private final StringProperty type;
        private final StringProperty status;
        private final DoubleProperty previousYearAmount;
        private final StringProperty comments;
        private final BooleanProperty selected;

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
            this.selected = new SimpleBooleanProperty(false);
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
        
        public boolean isSelected() {
            return selected.get();
        }
        
        public BooleanProperty selectedProperty() {
            return selected;
        }
        
        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }
    }

    @FXML
    private Label statusLabel;
    @FXML
    private Label headerTitleLabel;
    @FXML
    private Button authButton;
    @FXML
    private Button homeButton;
    @FXML
    private Button projectionsButton;
    @FXML
    private MenuButton comparisonsMenuButton;
    @FXML
    private MenuItem comparisonsMenuItem;
    @FXML
    private MenuItem internationalComparisonMenuItem;
    @FXML
    private Button internationalComparisonButton; 
    @FXML
    private Button statisticsButton;
    @FXML
    private Button aiAssistantButton;
    @FXML
    private ComboBox<String> yearComboBox;
    @FXML
    private Button editButton;
    private String selectedYear = "2025";
    
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
    private VBox quickNavMinistries;
    @FXML
    private VBox quickNavRevenues;
    @FXML
    private VBox quickNavExpenses;
    @FXML
    private VBox quickNavAdministrations;
    
    @FXML
    private VBox dataExplorationView;
    @FXML
    private VBox explorationSidebar;
    @FXML
    private VBox exploreComparisonControls;
    @FXML
    private VBox statisticsView;
    @FXML
    private VBox projectionsView;
    @FXML
    private VBox internationalComparisonView;
    @FXML
    private VBox aiAssistantView;
    @FXML
    private ScrollPane aiChatScrollPane;
    @FXML
    private VBox aiChatMessagesContainer;
    @FXML
    private TextField aiMessageInputField;
    @FXML
    private Button aiSendButton;
    @FXML
    private VBox aiConfigAlertContainer;
    @FXML
    private TextField aiApiKeyField;
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
    private ComboBox<String> ministriesYearComboBox;
    @FXML
    private ComboBox<String> revenuesYearComboBox;
    @FXML
    private ComboBox<String> expensesYearComboBox;
    @FXML
    private ComboBox<String> administrationsYearComboBox;
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
    
    @FXML
    private VBox yearComparisonChartContainer;
    @FXML
    private BarChart<String, Number> yearComparisonRevenueExpensesChart;
    
    @FXML
    private VBox overviewChartsContainer;
    @FXML
    private LineChart<String, Number> overviewTrendsChart;
    
    @FXML
    private ComboBox<String> internationalYearComboBox;
    @FXML
    private ComboBox<String> internationalCountry1ComboBox;
    @FXML
    private ComboBox<String> internationalCountry2ComboBox;
    @FXML
    private Button loadInternationalComparisonButton;
    @FXML
    private VBox internationalResultsContainer;
    @FXML
    private Label internationalComparisonTitle;
    @FXML
    private TableView<CountryComparisonData> internationalComparisonTable;
    @FXML
    private TableColumn<CountryComparisonData, String> intIndicatorColumn;
    @FXML
    private TableColumn<CountryComparisonData, String> intCountry1Column;
    @FXML
    private TableColumn<CountryComparisonData, String> intCountry2Column;
    @FXML
    private TableColumn<CountryComparisonData, String> intDifferenceColumn;
    @FXML
    private VBox allCountriesTableContainer;
    @FXML
    private TableView<CountryBudgetData> allCountriesTable;
    @FXML
    private TableColumn<CountryBudgetData, String> allCountriesCountryColumn;
    @FXML
    private TableColumn<CountryBudgetData, Double> allCountriesRevenueColumn;
    @FXML
    private TableColumn<CountryBudgetData, Double> allCountriesExpenseColumn;
    @FXML
    private TableColumn<CountryBudgetData, Double> allCountriesBalanceColumn;
    
    
    private String currentExplorationView = "";
    private boolean isLoadingExplorationView = false;
    private boolean isEditMode = false;
    
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
    
    @FXML
    private AreaChart<String, Number> areaRevenueTrend;
    
    @FXML
    private AreaChart<String, Number> areaExpenseTrend;
    
    @FXML
    private AreaChart<String, Number> areaMinistriesTrend;
    
    @FXML
    private BarChart<String, Number> barAdministrationsTop;
    @FXML
    private AreaChart<String, Number> areaAdministrationsTrend;
    
    @FXML
    private ComboBox<String> simulationBaseYearComboBox;
    @FXML
    private TabPane simulationTypeTabPane;
    @FXML
    private HBox expensesLevelContainer;
    @FXML
    private ComboBox<String> simulationLevelComboBox;
    @FXML
    private TableView<SimulationSelectionItem> simulationSelectionsTable;
    @FXML
    private TableColumn<SimulationSelectionItem, Boolean> simulationCheckColumn;
    @FXML
    private TableColumn<SimulationSelectionItem, String> simulationCategoryColumn;
    @FXML
    private TableColumn<SimulationSelectionItem, Double> simulationAmountColumn;
    @FXML
    private TableColumn<SimulationSelectionItem, String> simulationChangeColumn;
    @FXML
    private TextField simulationDefaultChangeField;
    @FXML
    private TextArea simulationResultsArea;
    
    private int simulationBaseYear = Calendar.getInstance().get(Calendar.YEAR);
    private double simulationBaseRevenue = 0;
    private double simulationBaseExpense = 0;
    private ObservableList<SimulationSelectionItem> simulationSelections = FXCollections.observableArrayList();
    
    
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
    
    @FXML
    private TableView<CategoryData> revenuesTable;
    @FXML
    private TableColumn<CategoryData, String> revenueCategoryColumn;
    @FXML
    private TableColumn<CategoryData, Double> revenueAmountColumn;
    @FXML
    private TableColumn<CategoryData, Double> revenuePercentageColumn;
    
    @FXML
    private TableView<CategoryData> expensesTable;
    @FXML
    private TableColumn<CategoryData, String> expenseCategoryColumn;
    @FXML
    private TableColumn<CategoryData, Double> expenseAmountColumn;
    @FXML
    private TableColumn<CategoryData, Double> expensePercentageColumn;
    
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
    
    // ελέγχει αν μπορεί να γίνει edit σε ένα έτος
    private boolean isYearEditable(int year) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return year >= currentYear;
    }
    
    // παίρνει το έτος από τα δεδομένα
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
    
    // setup για table selection
    private void setupTableSelection(TableView<CategoryData> table) {
        if (table == null) return;
        
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            boolean isEditable = false;
            
            if (hasSelection) {
                int year = extractYearFromData(newSelection);
                isEditable = isYearEditable(year);
                
            }
        });
        
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    
    @FXML
    private void initialize() {
        budgetData = BudgetData.getInstance();
        userData = UserData.getInstance();
        exportImportService = ExportsImports.getInstance();
        
        initializePublishedYearsTable();
        
        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Κρατικός Προϋπολογισμός του 2025");
        }
        
        updateAuthButton();
        
        updateUserTypeLabel();
        
        updateGovernmentFeatures();
        
        if (dataExplorationView != null) {
            dataExplorationView.setVisible(false);
            dataExplorationView.setManaged(false);
        }
        
        if (yearComboBox != null) {
            updateYearComboBox();
        }

        // Initialize table columns
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        percentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        changeColumn.setCellValueFactory(new PropertyValueFactory<>("change"));
        
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

        updateDataForYear();
        
        setupQuickNavigation();
        
        showView(homeView);
    }
    
    private void updateAuthButton() {
        if (authButton != null) {
            if (currentUserType == UserType.CITIZEN) {
                authButton.setText("Σύνδεση ως Κυβέρνηση");
                authButton.getStyleClass().removeAll("logout-button");
                if (!authButton.getStyleClass().contains("header-nav-button")) {
                    authButton.getStyleClass().add("header-nav-button");
                }
            } else {
                authButton.setText("Αποσύνδεση");
                authButton.getStyleClass().removeAll("header-nav-button");
                if (!authButton.getStyleClass().contains("logout-button")) {
                    authButton.getStyleClass().add("logout-button");
                }
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
            
            if (isGovernmentUser()) {
                yearComboBox.getItems().addAll("2023", "2024", "2025", "2026");
                yearComboBox.getItems().add("2027");
            } else {
                for (int year = 2023; year <= 2027; year++) {
                    if (publishedYears.contains(year)) {
                        yearComboBox.getItems().add(String.valueOf(year));
                    }
                }
            }
            
            String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            if (yearComboBox.getItems().contains(currentYear)) {
                yearComboBox.setValue(currentYear);
                selectedYear = currentYear;
            } else if (!yearComboBox.getItems().isEmpty()) {
                yearComboBox.setValue(yearComboBox.getItems().get(0));
                selectedYear = yearComboBox.getItems().get(0);
            }
        }
    }
    
    // αρχικοποιεί το year combo box
    private void initializeYearComboBox(ComboBox<String> comboBox) {
        if (comboBox == null) return;
        
        comboBox.getItems().clear();
        
        Set<Integer> publishedYears = getPublishedYears();
        
        // For citizens: only show published years
        if (isGovernmentUser()) {
            // Government sees all years
            comboBox.getItems().addAll("2023", "2024", "2025", "2026");
            comboBox.getItems().add("2027");
        } else {
            // Citizens only see published years
            for (int year = 2023; year <= 2027; year++) {
                if (publishedYears.contains(year)) {
                    comboBox.getItems().add(String.valueOf(year));
                }
            }
        }
        
        String yearToSet = selectedYear != null ? selectedYear : String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        if (comboBox.getItems().contains(yearToSet)) {
            comboBox.setValue(yearToSet);
        } else if (!comboBox.getItems().isEmpty()) {
            comboBox.setValue(comboBox.getItems().get(comboBox.getItems().size() - 1));
        }
        
        // Center the text in the ComboBox
        comboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
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
        comboBox.setButtonCell(new ListCell<String>() {
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
    
    
    private void initializePublishedYearsTable() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement()) {
            
            String sql = "CREATE TABLE IF NOT EXISTS published_years (" +
                        "year INTEGER PRIMARY KEY)";
            stmt.execute(sql);
            
            String customCategoriesSql = "CREATE TABLE IF NOT EXISTS custom_categories (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                        "category_name TEXT NOT NULL," +
                                        "year INTEGER NOT NULL," +
                                        "type TEXT NOT NULL," +
                                        "amount REAL NOT NULL DEFAULT 0," +
                                        "comments TEXT," +
                                        "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                                        "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                                        "UNIQUE(category_name, year, type)" +
                                        ")";
            stmt.execute(customCategoriesSql);
            
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
            initializePublishedYearsTable();
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
    
    // παίρνει τα διαθέσιμα έτη από τη βάση
    private List<String> getAvailableYearsFromDatabase() {
        List<String> availableYears = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            
            for (int year = 2020; year <= 2030; year++) {
                String tableName = "revenue_" + year;
                try (ResultSet tables = meta.getTables(null, null, tableName, null)) {
                    if (tables.next()) {
                        try (Statement stmt = connection.createStatement();
                             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM " + tableName)) {
                            if (rs.next() && rs.getInt("count") > 0) {
                                availableYears.add(String.valueOf(year));
                            }
                        } catch (SQLException e) {
                            continue;
                        }
                    }
                } catch (SQLException e) {
                    continue;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            availableYears.add(String.valueOf(currentYear - 1));
            availableYears.add(String.valueOf(currentYear));
        }
        
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
        updateYearComboBox();
        if (editButton != null) {
            editButton.setVisible(isGovernmentUser());
            editButton.setManaged(isGovernmentUser());
        }
    }
    
    @FXML
    private void onAuthButtonClicked() {
        if (currentUserType == UserType.CITIZEN) {
            showGovernmentLogin();
        } else {
            currentUserType = UserType.CITIZEN;
            updateUserTypeLabel();
            updateGovernmentFeatures();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/LoginView.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 1200, 700);
                scene.getStylesheets().add(getClass().getResource("/ui/styles.css").toExternalForm());
                
                Stage stage = null;
                if (authButton != null && authButton.getScene() != null) {
                    stage = (Stage) authButton.getScene().getWindow();
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
            Stage loginStage = new Stage();
            loginStage.setTitle("Σύνδεση - Κυβέρνηση");
            loginStage.setResizable(false);

            javafx.scene.layout.VBox loginPane = new javafx.scene.layout.VBox(20);
            loginPane.setPadding(new javafx.geometry.Insets(40));
            loginPane.setStyle("-fx-background-color: white;");

            Label titleLabel = new Label("Σύνδεση Κυβέρνησης");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e40af;");

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
                Constraints.ValidationResult result = Constraints.validateUsername(newText);
                Constraints.applyValidationStyle(usernameField, result.isValid(), usernameErrorLabel, result.getErrorMessage());
            });

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
                
                Constraints.ValidationResult usernameResult = Constraints.validateUsername(username);
                Constraints.ValidationResult passwordResult = Constraints.validatePassword(password);
                
                // Apply validation styles
                Constraints.applyValidationStyle(usernameField, usernameResult.isValid(), usernameErrorLabel, usernameResult.getErrorMessage());
                Constraints.applyValidationStyle(passwordField, passwordResult.isValid(), passwordErrorLabel, passwordResult.getErrorMessage());
                
                // Check if all validations pass
                if (usernameResult.isValid() && passwordResult.isValid()) {
                    if (authentication.checkLogin(username, password)) {
                            currentUserType = UserType.GOVERNMENT;
                            updateAuthButton();
                            updateUserTypeLabel();
                            updateGovernmentFeatures();
                    
                    loginStage.close();
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Σφάλμα Σύνδεσης");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Λάθος όνομα χρήστη ή κωδικός πρόσβασης.\nΠαρακαλώ προσπαθήστε ξανά.");
                        errorAlert.showAndWait();
                    }
                } else {
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
            if (authButton != null && authButton.getScene() != null) {
                ownerWindow = authButton.getScene().getWindow();
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
    private void onEditButtonClicked() {
        if (!isGovernmentUser()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Προειδοποίηση");
            alert.setHeaderText(null);
            alert.setContentText("Η επεξεργασία είναι διαθέσιμη μόνο για χρήστες κυβέρνησης.");
            alert.showAndWait();
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/BudgetEditView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1400, 800);
            scene.getStylesheets().add(getClass().getResource("/ui/styles.css").toExternalForm());
            
            Stage editStage = new Stage();
            editStage.setTitle("Επεξεργασία Προϋπολογισμού");
            editStage.setScene(scene);
            editStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText(null);
            alert.setContentText("Δεν ήταν δυνατή η φόρτωση της σελίδας επεξεργασίας: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    private void onYearSelected(javafx.event.ActionEvent event) {
        if (yearComboBox != null && yearComboBox.getValue() != null) {
            selectedYear = yearComboBox.getValue();
            updateDataForYear();
        }
    }
    
    @FXML
    private void onOpenAIAssistant() {
        if (aiAssistantView == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Δεν ήταν δυνατή η φόρτωση του AI Βοηθού");
            alert.setContentText("Παρακαλώ δοκιμάστε ξανά.");
            alert.showAndWait();
            return;
        }
        
        showView(aiAssistantView);
        initializeAIAssistant();
    }
    
    private void initializeAIAssistant() {
        if (aiChatMessagesContainer == null) return;
        
        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(ui.AIAssistantController.class);
        String savedApiKey = preferences.get("ai_assistant_api_key", "");
        
        if (!savedApiKey.isEmpty()) {
            aiChatMessagesContainer.getChildren().clear();
            addWelcomeMessage();
        } else {
            if (aiConfigAlertContainer != null) {
                aiConfigAlertContainer.setVisible(true);
                aiConfigAlertContainer.setManaged(true);
            }
        }
    }
    
    private void addWelcomeMessage() {
        String welcomeMessage = "Γεια σας! Είμαι ο AI Βοηθός για τον ελληνικό κρατικό προϋπολογισμό. " +
                "Μπορώ να σας βοηθήσω με ερωτήσεις σχετικά με:\n\n" +
                "• Έσοδα και δαπάνες\n" +
                "• Ανάλυση υπουργείων\n" +
                "• Τάσεις και μεταβολές\n" +
                "• Συγκρίσεις ετών\n" +
                "• Και πολλά άλλα!\n\n" +
                "Πώς μπορώ να σας βοηθήσω σήμερα;";
        
        addAiMessage("assistant", welcomeMessage);
    }
    
    private void addAiMessage(String role, String content) {
        if (aiChatMessagesContainer == null) return;
        
        javafx.application.Platform.runLater(() -> {
            HBox messageContainer = new HBox(12);
            messageContainer.setAlignment(role.equals("user") ? javafx.geometry.Pos.CENTER_RIGHT : javafx.geometry.Pos.CENTER_LEFT);
            messageContainer.setMaxWidth(Double.MAX_VALUE);
            
            Label messageLabel = new Label(content);
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(600);
            messageLabel.setPadding(new javafx.geometry.Insets(12, 16, 12, 16));
            messageLabel.setFont(javafx.scene.text.Font.font(14));
            
            if (role.equals("user")) {
                messageLabel.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; -fx-background-radius: 18;");
                HBox.setHgrow(messageContainer, javafx.scene.layout.Priority.ALWAYS);
            } else {
                messageLabel.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-background-radius: 18;");
                HBox.setHgrow(messageContainer, javafx.scene.layout.Priority.ALWAYS);
            }
            
            messageContainer.getChildren().add(messageLabel);
            aiChatMessagesContainer.getChildren().add(messageContainer);
            
            scrollAiToBottom();
        });
    }
    
    private void scrollAiToBottom() {
        if (aiChatScrollPane != null) {
            javafx.application.Platform.runLater(() -> {
                aiChatScrollPane.setVvalue(1.0);
            });
        }
    }
    
    @FXML
    private void onSendAiMessage() {
        if (aiMessageInputField == null) return;
        
        String message = aiMessageInputField.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        
        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(ui.AIAssistantController.class);
        String apiKey = preferences.get("ai_assistant_api_key", "");
        
        if (apiKey.isEmpty()) {
            if (aiConfigAlertContainer != null) {
                aiConfigAlertContainer.setVisible(true);
                aiConfigAlertContainer.setManaged(true);
            }
            return;
        }
        
        addAiMessage("user", message);
        aiMessageInputField.clear();
        
        if (aiSendButton != null) {
            aiSendButton.setDisable(true);
        }
        if (aiMessageInputField != null) {
            aiMessageInputField.setDisable(true);
        }
        
        new Thread(() -> {
            try {
                ui.AIAssistantService aiService = new ui.AIAssistantService();
                aiService.setApiKey(apiKey);
                
                org.json.JSONArray conversationHistory = new org.json.JSONArray();
                org.json.JSONObject userMessageObj = new org.json.JSONObject();
                userMessageObj.put("role", "user");
                userMessageObj.put("content", message);
                conversationHistory.put(userMessageObj);
                
                String response = aiService.sendMessage(message, conversationHistory);
                
                javafx.application.Platform.runLater(() -> {
                    addAiMessage("assistant", response);
                    if (aiSendButton != null) {
                        aiSendButton.setDisable(false);
                    }
                    if (aiMessageInputField != null) {
                        aiMessageInputField.setDisable(false);
                        aiMessageInputField.requestFocus();
                    }
                });
        } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    addAiMessage("assistant", "Σφάλμα: " + e.getMessage());
                    if (aiSendButton != null) {
                        aiSendButton.setDisable(false);
                    }
                    if (aiMessageInputField != null) {
                        aiMessageInputField.setDisable(false);
                    }
                });
            }
        }).start();
    }
    
    @FXML
    private void onSaveAiApiKey() {
        if (aiApiKeyField == null) return;
        
        String apiKey = aiApiKeyField.getText().trim();
        if (apiKey.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setContentText("Το API Key δεν μπορεί να είναι κενό.");
            alert.showAndWait();
            return;
        }
        
        java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(ui.AIAssistantController.class);
        preferences.put("ai_assistant_api_key", apiKey);
        
        if (aiConfigAlertContainer != null) {
            aiConfigAlertContainer.setVisible(false);
            aiConfigAlertContainer.setManaged(false);
        }
        
        if (aiChatMessagesContainer != null) {
            aiChatMessagesContainer.getChildren().clear();
        }
        addWelcomeMessage();
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Επιτυχία");
        alert.setContentText("Το API Key αποθηκεύτηκε επιτυχώς!");
        alert.showAndWait();
    }
    
    private void updateDataForYear() {
        if (selectedYear == null) return;

        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Κρατικός Προϋπολογισμός του " + selectedYear);
        }
        
        updateSummaryCards(selectedYear);
        
        updateRevenuesTable();
        updateExpensesTable();
        updateAdministrationsTable();
        updateCharts(Integer.parseInt(selectedYear));
        
        int year = Integer.parseInt(selectedYear);
        if (revenuesView != null && revenuesView.isVisible()) {
            if (pieRevenue != null) {
                Charts.fillPieChart(pieRevenue, budgetData.getRevenueBreakdownForGraphs(year), "Κύριες Πηγές Εσόδων");
            }
            if (areaRevenueTrend != null) {
                Charts.loadAreaChart(areaRevenueTrend, budgetData, "total_revenue", "Διαχρονική Εξέλιξη Εσόδων");
            }
        }
        if (expensesView != null && expensesView.isVisible()) {
            if (pieExpenses != null) {
                Charts.fillPieChart(pieExpenses, budgetData.getExpenseBreakdownForGraphs(year), "Έξοδα " + selectedYear);
            }
            if (areaExpenseTrend != null) {
                Charts.loadAreaChart(areaExpenseTrend, budgetData, "total_expenses", "Διαχρονική Εξέλιξη Δαπανών");
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
        String yearToUse = selectedYear;
        if (ministriesView != null && ministriesView.isVisible() && ministriesYearComboBox != null && ministriesYearComboBox.getValue() != null) {
            yearToUse = ministriesYearComboBox.getValue();
        }
        if (yearToUse == null) return;
        
        int yearInt = Integer.parseInt(yearToUse);
        List<BudgetData.CategoryInfo> categories = budgetData.getCategories(yearInt);
        
        // Get previous year data for comparison
        List<BudgetData.CategoryInfo> prevCategories = budgetData.getCategories(yearInt - 1);
        boolean hasPreviousYearData = !prevCategories.isEmpty();
        Map<String, Double> prevYearMap = new HashMap<>();
        for (BudgetData.CategoryInfo cat : prevCategories) {
            prevYearMap.put(cat.getName(), cat.getAmount());
        }
        
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
        String yearToUse = selectedYear;
        if (revenuesView != null && revenuesView.isVisible() && revenuesYearComboBox != null && revenuesYearComboBox.getValue() != null) {
            yearToUse = revenuesYearComboBox.getValue();
        }
        if (yearToUse == null) return;
        
        int yearInt = Integer.parseInt(yearToUse);
        List<BudgetData.CategoryInfo> revenues = budgetData.getRevenueBreakdown(yearInt);
        
        ObservableList<CategoryData> tableData = FXCollections.observableArrayList();
        for (BudgetData.CategoryInfo revenue : revenues) {
            tableData.add(new CategoryData(revenue.getName(), revenue.getAmount(), revenue.getPercentage(), ""));
        }
        
        revenuesTable.setItems(tableData);
    }
    
    private void updateExpensesTable() {
        String yearToUse = selectedYear;
        if (expensesView != null && expensesView.isVisible() && expensesYearComboBox != null && expensesYearComboBox.getValue() != null) {
            yearToUse = expensesYearComboBox.getValue();
        }
        if (yearToUse == null) return;
        
        int yearInt = Integer.parseInt(yearToUse);
        List<BudgetData.CategoryInfo> expenses = budgetData.getExpensesBreakdown(yearInt);
        
        ObservableList<CategoryData> tableData = FXCollections.observableArrayList();
        for (BudgetData.CategoryInfo expense : expenses) {
            tableData.add(new CategoryData(expense.getName(), expense.getAmount(), expense.getPercentage(), ""));
        }
        
        expensesTable.setItems(tableData);
    }
    
    private void updateAdministrationsTable() {
        String yearToUse = selectedYear;
        if (administrationsView != null && administrationsView.isVisible() && administrationsYearComboBox != null && administrationsYearComboBox.getValue() != null) {
            yearToUse = administrationsYearComboBox.getValue();
        }
        if (yearToUse == null) return;
        
        int yearInt = Integer.parseInt(yearToUse);
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
        
        // Initialize year combo box if needed
        if (ministriesYearComboBox != null && ministriesYearComboBox.getItems().isEmpty()) {
            initializeYearComboBox(ministriesYearComboBox);
        }
        
        // Get selected year from combo box or use default
        String yearStr = ministriesYearComboBox != null && ministriesYearComboBox.getValue() != null 
            ? ministriesYearComboBox.getValue() 
            : selectedYear;
        
        if (yearStr != null) {
            int year = Integer.parseInt(yearStr);
            updateCategoryTable(); // Update the category table for ministries view
            // Update ministries pie chart
            if (pieMinistries != null) {
                Charts.loadTopPieChart(pieMinistries, budgetData.getMinistriesBreakdown(year), "Κορυφαία Υπουργεία " + yearStr, 3);
            }
            // Area chart shows historical trend 
            if (areaMinistriesTrend != null && areaMinistriesTrend.getData().isEmpty()) {
                Charts.loadAreaChart(areaMinistriesTrend, budgetData, "total_expenses", "Διαχρονική Εξέλιξη Δαπανών Υπουργείων");
            }
        }
    }
    
    @FXML
    private void onMinistriesYearSelected() {
        if (ministriesYearComboBox != null && ministriesYearComboBox.getValue() != null) {
            String yearStr = ministriesYearComboBox.getValue();
            int year = Integer.parseInt(yearStr);
            // Update the category table for ministries view
            updateCategoryTable();
            // Update ministries pie chart
            if (pieMinistries != null) {
                Charts.loadTopPieChart(pieMinistries, budgetData.getMinistriesBreakdown(year), "Κορυφαία Υπουργεία " + yearStr, 3);
            }
            // Area chart shows historical trend - no need to update when year changes
        }
    }

    @FXML
    private void onNavigateRevenues() {
        showView(revenuesView);
        
        // Initialize year combo box if needed
        if (revenuesYearComboBox != null && revenuesYearComboBox.getItems().isEmpty()) {
            initializeYearComboBox(revenuesYearComboBox);
        }
        
        // Get selected year from combo box or use default
        String yearStr = revenuesYearComboBox != null && revenuesYearComboBox.getValue() != null 
            ? revenuesYearComboBox.getValue() 
            : selectedYear;
        
        if (yearStr != null) {
            int year = Integer.parseInt(yearStr);
            // Update revenues pie chart
            if (pieRevenue != null) {
                Charts.fillPieChart(pieRevenue, budgetData.getRevenueBreakdownForGraphs(year), "Κύριες Πηγές Εσόδων");
            }
            // Update area chart - trend
            if (areaRevenueTrend != null) {
                Charts.loadAreaChart(areaRevenueTrend, budgetData, "total_revenue", "Διαχρονική Εξέλιξη Εσόδων");
            }
        }
    }
    
    @FXML
    private void onRevenuesYearSelected() {
        if (revenuesYearComboBox != null && revenuesYearComboBox.getValue() != null) {
            String yearStr = revenuesYearComboBox.getValue();
            int year = Integer.parseInt(yearStr);
            // Update revenues table
            updateRevenuesTable();
            // Update revenues pie chart
            if (pieRevenue != null) {
                Charts.fillPieChart(pieRevenue, budgetData.getRevenueBreakdownForGraphs(year), "Κύριες Πηγές Εσόδων");
            }
        }
    }

    @FXML
    private void onNavigateExpenses() {
        showView(expensesView);
        
        // Initialize year combo box if needed
        if (expensesYearComboBox != null && expensesYearComboBox.getItems().isEmpty()) {
            initializeYearComboBox(expensesYearComboBox);
        }
        
        // Get selected year from combo box or use default
        String yearStr = expensesYearComboBox != null && expensesYearComboBox.getValue() != null 
            ? expensesYearComboBox.getValue() 
            : selectedYear;
        
        if (yearStr != null) {
            int year = Integer.parseInt(yearStr);
            // Update expenses pie chart
            if (pieExpenses != null) {
                Charts.fillPieChart(pieExpenses, budgetData.getExpenseBreakdownForGraphs(year), "Έξοδα " + yearStr);
            }
            // Update area chart - trend
            if (areaExpenseTrend != null) {
                Charts.loadAreaChart(areaExpenseTrend, budgetData, "total_expenses", "Διαχρονική Εξέλιξη Δαπανών");
            }
        }
    }
    
    @FXML
    private void onExpensesYearSelected() {
        if (expensesYearComboBox != null && expensesYearComboBox.getValue() != null) {
            String yearStr = expensesYearComboBox.getValue();
            int year = Integer.parseInt(yearStr);
            // Update expenses table
            updateExpensesTable();
            // Update expenses pie chart
            if (pieExpenses != null) {
                Charts.fillPieChart(pieExpenses, budgetData.getExpenseBreakdownForGraphs(year), "Έξοδα " + yearStr);
            }
            // Update area chart - trend
            if (areaExpenseTrend != null) {
                Charts.loadAreaChart(areaExpenseTrend, budgetData, "total_expenses", "Διαχρονική Εξέλιξη Δαπανών");
            }
        }
    }

    @FXML
    private void onNavigateAdministrations() {
        showView(administrationsView);
        
        // Initialize year combo box if needed
        if (administrationsYearComboBox != null && administrationsYearComboBox.getItems().isEmpty()) {
            initializeYearComboBox(administrationsYearComboBox);
        }
        
        // Get selected year from combo box or use default
        String yearStr = administrationsYearComboBox != null && administrationsYearComboBox.getValue() != null 
            ? administrationsYearComboBox.getValue() 
            : selectedYear;
        
        if (yearStr != null) {
            int year = Integer.parseInt(yearStr);
            // Update bar chart - top administrations
            if (barAdministrationsTop != null) {
                Charts.loadBarChartForTopCategories(barAdministrationsTop, budgetData.getDecentralizedAdministrationsBreakdown(year), "Κορυφαίες Διοικήσεις", 7);
            }
            // Update area chart - trend
            if (areaAdministrationsTrend != null) {
                Charts.loadAreaChart(areaAdministrationsTrend, budgetData, "total_expenses", "Διαχρονική Εξέλιξη Δαπανών Αποκεντρωμένων Διοικήσεων");
            }
        }
    }
    
    @FXML
    private void onAdministrationsYearSelected() {
        if (administrationsYearComboBox != null && administrationsYearComboBox.getValue() != null) {
            String yearStr = administrationsYearComboBox.getValue();
            int year = Integer.parseInt(yearStr);
            // Update administrations table
            updateAdministrationsTable();
            // Update bar chart - top administrations
            if (barAdministrationsTop != null) {
                Charts.loadBarChartForTopCategories(barAdministrationsTop, budgetData.getDecentralizedAdministrationsBreakdown(year), "Κορυφαίες Διοικήσεις", 7);
            }
            // Update area chart - trend
            if (areaAdministrationsTrend != null) {
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
            // Initialize year combo box
            if (exploreYearComboBox != null) {
                initializeYearComboBox(exploreYearComboBox);
                // Set default to current year + 1
                int defaultYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
                String defaultYearStr = String.valueOf(defaultYear);
                if (exploreYearComboBox.getItems().contains(defaultYearStr)) {
                    exploreYearComboBox.setValue(defaultYearStr);
                } else if (!exploreYearComboBox.getItems().isEmpty()) {
                    exploreYearComboBox.setValue(exploreYearComboBox.getItems().get(exploreYearComboBox.getItems().size() - 1));
                }
            }
            
            // Set default view to total
            resetExplorationButtons();
            if (exploreTotalButton != null) {
                exploreTotalButton.getStyleClass().add("exploration-category-button-active");
            }
            currentExplorationView = "total";
            loadTotalView();
            
        } catch (Exception e) {
            e.printStackTrace();
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
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Σφάλμα κατά την φόρτωση της προβολής");
            alert.setContentText("Παρακαλώ δοκιμάστε ξανά. " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    private void onExploreYearSelected() {
        if (!isLoadingExplorationView) {
        refreshExplorationView();
        }
    }
    
    @FXML
    private void onExploreFilterChanged() {
        if (!isLoadingExplorationView) {
        refreshExplorationView();
        }
    }
    
    private void refreshExplorationView() {
        switch (currentExplorationView) {
            case "total": loadTotalView(); break;
            case "ministry": loadMinistryView(); break;
            case "revenue_category": loadRevenueCategoryView(); break;
            case "expense_category": loadExpenseCategoryView(); break;
            case "comparison":
            break;

            case "trends": loadTrendsView(); break;
        }
    }
    
    private int getSelectedExplorationYear() {
        if (exploreYearComboBox != null && exploreYearComboBox.getValue() != null) {
            try {
                return Integer.parseInt(exploreYearComboBox.getValue());
            } catch (NumberFormatException e) {
                // Fall back to default
            }
        }
        return Calendar.getInstance().get(Calendar.YEAR) + 1;
    }
    
    private void loadTotalView() {
        if (exploreViewTitleLabel != null) {
            exploreViewTitleLabel.setText("Συνολικά Έσοδα και Δαπάνες ανά Έτος");
        }
        if (exploreViewDescriptionLabel != null) {
            exploreViewDescriptionLabel.setText("Προβολή συνολικών εσόδων και δαπανών για κάθε έτος");
        }
        
        // Setup column headers
        if (exploreColumn1 != null) exploreColumn1.setText("Έτος");
        if (exploreColumn2 != null) exploreColumn2.setText("Συνολικά Έσοδα (€)");
        if (exploreColumn3 != null) exploreColumn3.setText("Συνολικές Δαπάνες (€)");
        if (exploreColumn4 != null) exploreColumn4.setText("Υπόλοιπο (€)");
        if (exploreColumn5 != null) exploreColumn5.setText("Αλλαγή από Προηγ. Έτος");
        
        // Setup column value factories and cell factories for Total View
        if (exploreColumn1 != null) {
            exploreColumn1.setCellValueFactory(new PropertyValueFactory<>("category"));
        }
        if (exploreColumn2 != null) {
            exploreColumn2.setCellValueFactory(new PropertyValueFactory<>("amount"));
            exploreColumn2.setCellFactory(column -> new TableCell<CategoryData, Double>() {
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
        }
        if (exploreColumn3 != null) {
            exploreColumn3.setCellValueFactory(cellData -> {
                double expenses = cellData.getValue().getPercentage(); // percentage stores expenses
                return new SimpleStringProperty(expenses == 0 ? "" : AmountFormatter.formatCurrency(expenses));
            });
            // Ensure it displays as currency, not percentage
            exploreColumn3.setCellFactory(column -> new TableCell<CategoryData, String>() {
                @Override
                protected void updateItem(String value, boolean empty) {
                    super.updateItem(value, empty);
                    if (empty || value == null) {
                        setText(null);
                    } else {
                        setText(value);
                    }
                }
            });
        }
        if (exploreColumn4 != null) {
            exploreColumn4.setCellValueFactory(new PropertyValueFactory<>("previousYearAmount"));
            exploreColumn4.setCellFactory(column -> new TableCell<CategoryData, Double>() {
                @Override
                protected void updateItem(Double balance, boolean empty) {
                    super.updateItem(balance, empty);
                    if (empty || balance == null) {
                        setText(null);
                    } else {
                        setText(AmountFormatter.formatCurrency(balance));
                    }
                }
            });
        }
        if (exploreColumn5 != null) {
            exploreColumn5.setCellValueFactory(new PropertyValueFactory<>("change"));
        }
        
        ObservableList<CategoryData> data = FXCollections.observableArrayList();
        int startYear = 2023;
        
        // Use selected year from combo box
        int endYear = getSelectedExplorationYear();
        
        for (int year = startYear; year <= endYear; year++) {
            double revenues = budgetData.getTotalRevenues(year);
            double expenses = budgetData.getTotalExpenses(year);
            double balance = StatisticalAnalysis.calculateBalance(revenues, expenses);
            double prevExpenses = year > startYear ? budgetData.getTotalExpenses(year - 1) : 0;
            double expensesChange = StatisticalAnalysis.calculatePercentageChange(expenses, prevExpenses);
            
            String changeText = prevExpenses > 0 ? AmountFormatter.formatPercentageChange(expensesChange) : "Νέο";
            data.add(new CategoryData(
                String.valueOf(year),
                revenues,        
                expenses,        
                changeText,      
                "Σύνολο",        
                balance          
            ));
        }
        
        if (exploreResultsTable != null) {
            exploreResultsTable.getItems().clear();
            exploreResultsTable.setItems(data);
        }
    }
    
    private void loadMinistryView() {
        try {
            isLoadingExplorationView = true;
            
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
            
            // Always refresh the list based on selected year
            String previousSelection = exploreMinistryComboBox.getValue();
            exploreMinistryComboBox.getItems().clear();
                int selectedYear = getSelectedExplorationYear();
            List<BudgetData.CategoryInfo> ministries = budgetData.getCategories(selectedYear);
            for (BudgetData.CategoryInfo m : ministries) {
                exploreMinistryComboBox.getItems().add(m.getName());
            }
            if (!exploreMinistryComboBox.getItems().isEmpty()) {
                // Try to preserve previous selection if it still exists
                if (previousSelection != null && exploreMinistryComboBox.getItems().contains(previousSelection)) {
                    exploreMinistryComboBox.setValue(previousSelection);
                } else {
                    exploreMinistryComboBox.setValue(exploreMinistryComboBox.getItems().get(0));
                }
            }
        }
        
        loadMinistryData();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in loadMinistryView: " + e.getMessage());
        } finally {
            isLoadingExplorationView = false;
        }
    }
    
    private void loadMinistryData() {
        try {
            if (exploreMinistryComboBox == null || exploreMinistryComboBox.getValue() == null) return;
            
            String ministryName = exploreMinistryComboBox.getValue();
            
            if (exploreColumn1 != null) {
                exploreColumn1.setText("Έτος");
                exploreColumn1.setCellValueFactory(new PropertyValueFactory<>("category"));
                exploreColumn1.setCellFactory(null);
            }
            if (exploreColumn2 != null) {
                exploreColumn2.setText("Υπουργείο");
                exploreColumn2.setCellValueFactory(cellData -> {
                    return new SimpleDoubleProperty(0).asObject();
                });
                exploreColumn2.setCellFactory(column -> new TableCell<CategoryData, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setText(null);
                        } else {
                            CategoryData data = getTableRow().getItem();
                            setText(data.getType());
                        }
                    }
                });
            }
            if (exploreColumn3 != null) {
                exploreColumn3.setText("Ποσό (€)");
                exploreColumn3.setCellValueFactory(cellData -> {
                    Double amount = cellData.getValue().getAmount();
                    return new SimpleStringProperty(amount == null ? "" : AmountFormatter.formatCurrency(amount));
                });
                exploreColumn3.setCellFactory(null);
            }
            if (exploreColumn4 != null) {
                exploreColumn4.setText("Συμμετοχή (%)");
                exploreColumn4.setCellValueFactory(new PropertyValueFactory<>("percentage"));
                exploreColumn4.setCellFactory(column -> new TableCell<CategoryData, Double>() {
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
            }
            if (exploreColumn5 != null) {
                exploreColumn5.setText("Αλλαγή");
                exploreColumn5.setCellValueFactory(new PropertyValueFactory<>("change"));
                exploreColumn5.setCellFactory(null);
            }
            
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
                exploreResultsTable.getItems().clear();
                exploreResultsTable.setItems(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in loadMinistryData: " + e.getMessage());
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
    
    // charts methods
    
    // ενημερώνει τα charts
    private void updateCharts(int year) {
        // Update Line Chart
        Charts.loadLineChart(lineHistory, budgetData);
    }

    
    private void loadRevenueCategoryView() {
        try {
            isLoadingExplorationView = true;
            
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
            
            // Always refresh the list based on selected year
            String previousSelection = exploreRevenueCategoryComboBox.getValue();
            exploreRevenueCategoryComboBox.getItems().clear();
                int selectedYear = getSelectedExplorationYear();
            List<BudgetData.CategoryInfo> revenues = budgetData.getRevenueBreakdown(selectedYear);
            for (BudgetData.CategoryInfo r : revenues) {
                exploreRevenueCategoryComboBox.getItems().add(r.getName());
            }
            if (!exploreRevenueCategoryComboBox.getItems().isEmpty()) {
                // Try to preserve previous selection if it still exists
                if (previousSelection != null && exploreRevenueCategoryComboBox.getItems().contains(previousSelection)) {
                    exploreRevenueCategoryComboBox.setValue(previousSelection);
                } else {
                    exploreRevenueCategoryComboBox.setValue(exploreRevenueCategoryComboBox.getItems().get(0));
                }
            }
        }
        
        loadRevenueCategoryData();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in loadRevenueCategoryView: " + e.getMessage());
        } finally {
            isLoadingExplorationView = false;
        }
    }
    
    private void loadRevenueCategoryData() {
        try {
            if (exploreRevenueCategoryComboBox == null || exploreRevenueCategoryComboBox.getValue() == null) return;
            
            String categoryName = exploreRevenueCategoryComboBox.getValue();
            
            if (exploreColumn1 != null) {
                exploreColumn1.setText("Έτος");
                exploreColumn1.setCellValueFactory(new PropertyValueFactory<>("category"));
                exploreColumn1.setCellFactory(null);
            }
            if (exploreColumn2 != null) {
                exploreColumn2.setText("Κατηγορία");
                exploreColumn2.setCellValueFactory(cellData -> {
                    return new SimpleDoubleProperty(0).asObject();
                });
                exploreColumn2.setCellFactory(column -> new TableCell<CategoryData, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setText(null);
                        } else {
                            CategoryData data = getTableRow().getItem();
                            setText(data.getType());
                        }
                    }
                });
            }
            if (exploreColumn3 != null) {
                exploreColumn3.setText("Ποσό (€)");
                exploreColumn3.setCellValueFactory(cellData -> {
                    Double amount = cellData.getValue().getAmount();
                    return new SimpleStringProperty(amount == null ? "" : AmountFormatter.formatCurrency(amount));
                });
                exploreColumn3.setCellFactory(null);
            }
            if (exploreColumn4 != null) {
                exploreColumn4.setText("Συμμετοχή (%)");
                exploreColumn4.setCellValueFactory(new PropertyValueFactory<>("percentage"));
                exploreColumn4.setCellFactory(column -> new TableCell<CategoryData, Double>() {
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
            }
            if (exploreColumn5 != null) {
                exploreColumn5.setText("Αλλαγή");
                exploreColumn5.setCellValueFactory(new PropertyValueFactory<>("change"));
                exploreColumn5.setCellFactory(null);
            }
            
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
                exploreResultsTable.getItems().clear();
                exploreResultsTable.setItems(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in loadRevenueCategoryData: " + e.getMessage());
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
        try {
            isLoadingExplorationView = true;
            
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
            
            // Always refresh the list based on selected year
            String previousSelection = exploreExpenseCategoryComboBox.getValue();
            exploreExpenseCategoryComboBox.getItems().clear();
                int selectedYear = getSelectedExplorationYear();
            List<BudgetData.CategoryInfo> expenses = budgetData.getExpensesBreakdown(selectedYear);
            for (BudgetData.CategoryInfo e : expenses) {
                exploreExpenseCategoryComboBox.getItems().add(e.getName());
            }
            if (!exploreExpenseCategoryComboBox.getItems().isEmpty()) {
                // Try to preserve previous selection if it still exists
                if (previousSelection != null && exploreExpenseCategoryComboBox.getItems().contains(previousSelection)) {
                    exploreExpenseCategoryComboBox.setValue(previousSelection);
                } else {
                    exploreExpenseCategoryComboBox.setValue(exploreExpenseCategoryComboBox.getItems().get(0));
                }
            }
        }
        
        loadExpenseCategoryData();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in loadExpenseCategoryView: " + e.getMessage());
        } finally {
            isLoadingExplorationView = false;
        }
    }
    
    private void loadExpenseCategoryData() {
        try {
            if (exploreExpenseCategoryComboBox == null || exploreExpenseCategoryComboBox.getValue() == null) return;
            
            String categoryName = exploreExpenseCategoryComboBox.getValue();
            
            if (exploreColumn1 != null) {
                exploreColumn1.setText("Έτος");
                exploreColumn1.setCellValueFactory(new PropertyValueFactory<>("category"));
                exploreColumn1.setCellFactory(null);
            }
            if (exploreColumn2 != null) {
                exploreColumn2.setText("Κατηγορία");
                exploreColumn2.setCellValueFactory(cellData -> {
                    return new SimpleDoubleProperty(0).asObject();
                });
                exploreColumn2.setCellFactory(column -> new TableCell<CategoryData, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setText(null);
                        } else {
                            CategoryData data = getTableRow().getItem();
                            setText(data.getType());
                        }
                    }
                });
            }
            if (exploreColumn3 != null) {
                exploreColumn3.setText("Ποσό (€)");
                exploreColumn3.setCellValueFactory(cellData -> {
                    Double amount = cellData.getValue().getAmount();
                    return new SimpleStringProperty(amount == null ? "" : AmountFormatter.formatCurrency(amount));
                });
                exploreColumn3.setCellFactory(null);
            }
            if (exploreColumn4 != null) {
                exploreColumn4.setText("Συμμετοχή (%)");
                exploreColumn4.setCellValueFactory(new PropertyValueFactory<>("percentage"));
                exploreColumn4.setCellFactory(column -> new TableCell<CategoryData, Double>() {
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
            }
            if (exploreColumn5 != null) {
                exploreColumn5.setText("Αλλαγή");
                exploreColumn5.setCellValueFactory(new PropertyValueFactory<>("change"));
                exploreColumn5.setCellFactory(null);
            }
            
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
                exploreResultsTable.getItems().clear();
                exploreResultsTable.setItems(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in loadExpenseCategoryData: " + e.getMessage());
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
        loadOverviewCharts();
        
        if (exploreComparisonControls != null) {
            exploreComparisonControls.setVisible(true);
            exploreComparisonControls.setManaged(true);
        }
        
        if (yearComparisonChartContainer != null) {
            yearComparisonChartContainer.setVisible(false);
            yearComparisonChartContainer.setManaged(false);
        }
        
        if (exploreResultsTable != null) {
            exploreResultsTable.setVisible(false);
            exploreResultsTable.setManaged(false);
        }
        if (exploreYear1Label != null) {
            exploreYear1Label.setVisible(true);
            exploreYear1Label.setManaged(true);
        }
        if (exploreYear1ComboBox != null) {
            exploreYear1ComboBox.setVisible(true);
            exploreYear1ComboBox.setManaged(true);
            List<String> availableYears = getAvailableYearsFromDatabase();
            exploreYear1ComboBox.getItems().clear();
            exploreYear1ComboBox.getItems().addAll(availableYears);
            exploreYear1ComboBox.setValue(null);
        }
        if (exploreYear2ComboBox != null) {
            exploreYear2ComboBox.setVisible(true);
            exploreYear2ComboBox.setManaged(true);
            List<String> availableYears = getAvailableYearsFromDatabase();
            exploreYear2ComboBox.getItems().clear();
            exploreYear2ComboBox.getItems().addAll(availableYears);
            exploreYear2ComboBox.setValue(null);
        }
        if (exploreLoadComparisonButton != null) {
            exploreLoadComparisonButton.setVisible(true);
            exploreLoadComparisonButton.setManaged(true);
        }
        
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
        
        if (exploreColumn5 != null) {
            exploreColumn5.setVisible(false);
        }
        
        if (exploreResultsTable != null) {
            exploreResultsTable.setVisible(false);
            exploreResultsTable.setManaged(false);
            exploreResultsTable.getStyleClass().add("comparison-table");
        }
        
        if (exploreResultsTable != null && exploreColumn2 != null && exploreColumn3 != null) {
            if (exploreColumn1 != null) {
                exploreColumn1.setCellValueFactory(new PropertyValueFactory<>("category"));
            }

            exploreColumn2.setCellValueFactory(new PropertyValueFactory<>("amount"));
            exploreColumn2.setCellFactory(column -> new TableCell<CategoryData, Double>() {
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
            
            exploreColumn3.setCellValueFactory(cellData -> {
                double value = cellData.getValue().getPercentage(); // percentage stores year2 value
                return new SimpleStringProperty(String.format("%,d €", (long)value));
            });
            
            if (exploreColumn4 != null) {
                exploreColumn4.setCellValueFactory(cellData -> {
                    double year1 = cellData.getValue().getAmount();
                    double year2 = cellData.getValue().getPercentage();
                    return new SimpleDoubleProperty(year2 - year1).asObject();
                });
                exploreColumn4.setCellFactory(column -> new TableCell<CategoryData, Double>() {
                    @Override
                    protected void updateItem(Double difference, boolean empty) {
                        super.updateItem(difference, empty);
                        if (empty || difference == null) {
                            setText(null);
                        } else {
                            setText(AmountFormatter.formatCurrency(difference));
                        }
                    }
                });
            }
        }
        
        // Clear table initially
        if (exploreResultsTable != null) {
            exploreResultsTable.setItems(FXCollections.observableArrayList());
        }

    }
    

    @FXML
    private void onLoadComparison() {
        System.out.println("onLoadComparison called");
        
        if (exploreYear1ComboBox == null || exploreYear2ComboBox == null) {
            System.err.println("ERROR: ComboBoxes are null!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Σφάλμα αρχικοποίησης");
            alert.setContentText("Τα πεδία επιλογής ετών δεν είναι διαθέσιμα.");
            alert.showAndWait();
            return;
        }
        
        String year1Str = exploreYear1ComboBox.getValue();
        String year2Str = exploreYear2ComboBox.getValue();
        
        System.out.println("Year1: " + year1Str + ", Year2: " + year2Str);
        
        if (year1Str == null || year1Str.isEmpty() || year2Str == null || year2Str.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Παρακαλώ επιλέξτε και τα δύο έτη");
            alert.setContentText("Πρέπει να επιλέξετε ένα έτος από κάθε λίστα.");
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
                alert.setContentText("Τα δύο έτη πρέπει να είναι διαφορετικά για να γίνει σύγκριση.");
                alert.showAndWait();
                return;
            }
            
            loadComparisonData(year1, year2);
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Μη έγκυρα έτη");
            alert.setContentText("Παρακαλώ επιλέξτε έγκυρα έτη.");
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
    @FXML
    private void onComparisonYearsClicked() {
        loadComparisonView();
    }
    
    @FXML
    private void onNavigateComparison() {
        if (dataExplorationView == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Δεν ήταν δυνατή η φόρτωση της προβολής σύγκρισης");
            alert.setContentText("Παρακαλώ δοκιμάστε ξανά.");
            alert.showAndWait();
            return;
        }
        
        // Show the data exploration view
        showView(dataExplorationView);
        
        // Hide the sidebar with all the buttons
        if (explorationSidebar != null) {
            explorationSidebar.setVisible(false);
            explorationSidebar.setManaged(false);
        }
        
        // Hide the year combo box 
        if (exploreYearComboBox != null) {
            exploreYearComboBox.setVisible(false);
            exploreYearComboBox.setManaged(false);
        }
        
        // Load comparison view directly
        loadComparisonView();
    }
    
    @FXML
    private void onNavigateInternationalComparison() {
        if (internationalComparisonView == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Δεν ήταν δυνατή η φόρτωση της προβολής σύγκρισης");
            alert.setContentText("Παρακαλώ δοκιμάστε ξανά.");
            alert.showAndWait();
            return;
        }
        
        showView(internationalComparisonView);
        initializeInternationalComparison();
    }
    
    private void initializeInternationalComparison() {
        try {
            BudgetData budgetData = BudgetData.getInstance();
            
            if (internationalYearComboBox != null) {
                internationalYearComboBox.getItems().clear();
                String sql = "SELECT DISTINCT year FROM international_indicators ORDER BY year DESC";
                try (Connection conn = DatabaseConnection.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        int year = rs.getInt("year");
                        internationalYearComboBox.getItems().add(String.valueOf(year));
                        System.out.println("DEBUG: Added year " + year + " to combo box");
                    }
                    System.out.println("DEBUG: Found " + internationalYearComboBox.getItems().size() + " years in international_indicators");
                    System.out.println("DEBUG: Years list: " + internationalYearComboBox.getItems());
                } catch (Exception e) {
                    System.err.println("Error loading years: " + e.getMessage());
                    e.printStackTrace();
                    // Fallback to standard years
                    initializeYearComboBox(internationalYearComboBox);
                }
                if (internationalYearComboBox.getItems().size() > 0) {
                    if (internationalYearComboBox.getItems().contains("2023")) {
                        internationalYearComboBox.setValue("2023");
                    } else {
                        internationalYearComboBox.setValue(internationalYearComboBox.getItems().get(0));
                    }
                }
            }
            
            // Πάντα προσθέτουμε την Ελλάδα πρώτη
            String greeceDisplay = "Greece (GRC)";
            if (internationalCountry1ComboBox != null) {
                internationalCountry1ComboBox.getItems().add(greeceDisplay);
            }
            if (internationalCountry2ComboBox != null) {
                internationalCountry2ComboBox.getItems().add(greeceDisplay);
            }
            
            // Προσθέτουμε τις υπόλοιπες χώρες
            List<String> countries = budgetData.getAvailableCountries();
            for (String countryCode : countries) {
                // Παραλείπουμε την Ελλάδα αν είναι ήδη στη λίστα
                if ("GRC".equals(countryCode)) {
                    continue;
                }
                String countryName = budgetData.getCountryName(countryCode);
                String countryDisplay = countryName != null ? countryName + " (" + countryCode + ")" : countryCode;
                
                if (internationalCountry1ComboBox != null) {
                    internationalCountry1ComboBox.getItems().add(countryDisplay);
                }
                if (internationalCountry2ComboBox != null) {
                    internationalCountry2ComboBox.getItems().add(countryDisplay);
                }
            }
            
            // Initialize table columns for country budget comparison
            if (intIndicatorColumn != null) {
                intIndicatorColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            }
            
            if (intCountry1Column != null) {
                intCountry1Column.setCellValueFactory(new PropertyValueFactory<>("country1Value"));
            }
            
            if (intCountry2Column != null) {
                intCountry2Column.setCellValueFactory(new PropertyValueFactory<>("country2Value"));
            }
            
            if (intDifferenceColumn != null) {
                intDifferenceColumn.setCellValueFactory(new PropertyValueFactory<>("difference"));
            }
            
            // Hide results initially
            if (internationalResultsContainer != null) {
                internationalResultsContainer.setVisible(false);
                internationalResultsContainer.setManaged(false);
            }
            
            if (allCountriesCountryColumn != null) {
                allCountriesCountryColumn.setCellValueFactory(new PropertyValueFactory<>("countryName"));
            }
            
            if (allCountriesRevenueColumn != null) {
                allCountriesRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
                allCountriesRevenueColumn.setCellFactory(column -> new TableCell<CountryBudgetData, Double>() {
                    @Override
                    protected void updateItem(Double revenue, boolean empty) {
                        super.updateItem(revenue, empty);
                        if (empty || revenue == null) {
                            setText(null);
                        } else {
                            setText(AmountFormatter.formatCurrency(revenue));
                        }
                    }
                });
            }
            
            if (allCountriesExpenseColumn != null) {
                allCountriesExpenseColumn.setCellValueFactory(new PropertyValueFactory<>("expense"));
                allCountriesExpenseColumn.setCellFactory(column -> new TableCell<CountryBudgetData, Double>() {
                    @Override
                    protected void updateItem(Double expense, boolean empty) {
                        super.updateItem(expense, empty);
                        if (empty || expense == null) {
                            setText(null);
                        } else {
                            setText(AmountFormatter.formatCurrency(expense));
                        }
                    }
                });
            }
            
            if (allCountriesBalanceColumn != null) {
                allCountriesBalanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
                allCountriesBalanceColumn.setCellFactory(column -> new TableCell<CountryBudgetData, Double>() {
                    @Override
                    protected void updateItem(Double balance, boolean empty) {
                        super.updateItem(balance, empty);
                        if (empty || balance == null) {
                            setText(null);
                        } else {
                            setText(AmountFormatter.formatCurrency(balance));
                        }
                    }
                });
            }
            
            loadAllCountriesTable();
            
            // Setup listeners
            if (internationalYearComboBox != null) {
                internationalYearComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                    updateCountryComboBoxAvailability();
                    validateCountrySelections();
                    loadAllCountriesTable();
                });
            }
            
            if (internationalCountry1ComboBox != null && internationalCountry2ComboBox != null) {
                internationalCountry1ComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null && newVal.equals(internationalCountry2ComboBox.getValue())) {
                        internationalCountry2ComboBox.setValue(null);
                    }
                });
                internationalCountry2ComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null && newVal.equals(internationalCountry1ComboBox.getValue())) {
                        internationalCountry1ComboBox.setValue(null);
                    }
                });
            }
            
            // Initial update
            updateCountryComboBoxAvailability();
            
        } catch (Exception e) {
            System.err.println("Error initializing international comparison: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // μετατρέπει string έτος σε int
    private int parseYearSafely(String yearStr) {
        if (yearStr == null) return 0;
        try {
            return Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    // Inner class for all countries budget data
    public static class CountryBudgetData {
        private final StringProperty countryName;
        private final DoubleProperty revenue;
        private final DoubleProperty expense;
        private final DoubleProperty balance;
        
        public CountryBudgetData(String countryName, double revenue, double expense, double balance) {
            this.countryName = new SimpleStringProperty(countryName);
            this.revenue = new SimpleDoubleProperty(revenue);
            this.expense = new SimpleDoubleProperty(expense);
            this.balance = new SimpleDoubleProperty(balance);
        }
        
        public StringProperty countryNameProperty() { return countryName; }
        public DoubleProperty revenueProperty() { return revenue; }
        public DoubleProperty expenseProperty() { return expense; }
        public DoubleProperty balanceProperty() { return balance; }
        
        public String getCountryName() { return countryName.get(); }
        public double getRevenue() { return revenue.get(); }
        public double getExpense() { return expense.get(); }
        public double getBalance() { return balance.get(); }
    }
    
    // Inner class for country budget comparison data
    public static class CountryComparisonData {
        private final StringProperty category;
        private final StringProperty country1Value;
        private final StringProperty country2Value;
        private final StringProperty difference;
        
        public CountryComparisonData(String category, String country1Value, String country2Value, 
                                     String difference) {
            this.category = new SimpleStringProperty(category);
            this.country1Value = new SimpleStringProperty(country1Value);
            this.country2Value = new SimpleStringProperty(country2Value);
            this.difference = new SimpleStringProperty(difference);
        }
        
        public StringProperty categoryProperty() { return category; }
        public StringProperty country1ValueProperty() { return country1Value; }
        public StringProperty country2ValueProperty() { return country2Value; }
        public StringProperty differenceProperty() { return difference; }
        
        public String getCategory() { return category.get(); }
        public String getCountry1Value() { return country1Value.get(); }
        public String getCountry2Value() { return country2Value.get(); }
        public String getDifference() { return difference.get(); }
    }
    
    @FXML
    private void onLoadInternationalComparison() {
        try {
            if (internationalYearComboBox == null || internationalCountry1ComboBox == null || 
                internationalCountry2ComboBox == null) {
                return;
            }
            
            String selectedYearStr = internationalYearComboBox.getValue();
            String selectedCountry1Str = internationalCountry1ComboBox.getValue();
            String selectedCountry2Str = internationalCountry2ComboBox.getValue();
            
            if (selectedYearStr == null || selectedCountry1Str == null || selectedCountry2Str == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Προειδοποίηση");
                alert.setHeaderText("Ατελής επιλογή");
                alert.setContentText("Παρακαλώ επιλέξτε έτος και και τα δύο κράτη.");
                alert.showAndWait();
                return;
            }
            
            int year = Integer.parseInt(selectedYearStr);
            
            // Extract country codes
            String country1Code = extractCountryCode(selectedCountry1Str);
            String country2Code = extractCountryCode(selectedCountry2Str);
            
            if (country1Code == null || country2Code == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Σφάλμα");
                alert.setHeaderText("Μη έγκυρος κωδικός χώρας");
                alert.setContentText("Δεν ήταν δυνατή η ανάγνωση των κωδικών χωρών.");
                alert.showAndWait();
                return;
            }
            
            if (country1Code.equals(country2Code)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Σφάλμα");
                alert.setHeaderText("Παρακαλώ επιλέξτε διαφορετικά κράτη");
                alert.setContentText("Τα δύο κράτη πρέπει να είναι διαφορετικά για να γίνει σύγκριση.");
                alert.showAndWait();
                return;
            }
            
            BudgetData budgetData = BudgetData.getInstance();
            
            // Get budget data for both countries
            // Ελλάδα χρησιμοποιεί τα δεδομένα από budget_summary, άλλες χώρες από international_budgets
            BudgetData.InternationalBudget budget1;
            BudgetData.InternationalBudget budget2;
            
            if ("GRC".equals(country1Code)) {
                budget1 = budgetData.getGreekBudget(year);
            } else {
                budget1 = budgetData.getInternationalBudget(country1Code, year);
            }
            
            if ("GRC".equals(country2Code)) {
                budget2 = budgetData.getGreekBudget(year);
            } else {
                budget2 = budgetData.getInternationalBudget(country2Code, year);
            }
            
            if (budget1 == null || budget2 == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Προειδοποίηση");
                alert.setHeaderText("Δεδομένα προϋπολογισμού δεν βρέθηκαν");
                alert.setContentText("Δεν βρέθηκαν δεδομένα προϋπολογισμού για μία ή και τις δύο χώρες το έτος " + year + ".\n\n" +
                    "Παρακαλώ τρέξτε το FetchInternationalDataFromAPIs για να κατεβάσετε τα δεδομένα.");
                alert.showAndWait();
                return;
            }
            
            String country1Name = budget1.getCountryName();
            String country2Name = budget2.getCountryName();
            
            // Update table column headers with selected country names
            if (intCountry1Column != null) {
                intCountry1Column.setText(country1Name != null ? country1Name : country1Code);
            }
            if (intCountry2Column != null) {
                intCountry2Column.setText(country2Name != null ? country2Name : country2Code);
            }
            
            // Debug: Print budget values with more details
            System.out.println("=== DEBUG: Budget Comparison ===");
            System.out.println("Budget1 (" + country1Code + " - " + country1Name + "):");
            System.out.println("  GDP: " + budget1.getTotalGDP());
            System.out.println("  Revenue: " + budget1.getTotalRevenue() + " EUR");
            System.out.println("  Expenses: " + budget1.getTotalExpenses() + " EUR");
            System.out.println("  Balance: " + budget1.getBudgetBalance() + " EUR");
            System.out.println("  Revenue/GDP: " + (budget1.getTotalGDP() > 0 ? (budget1.getTotalRevenue() / budget1.getTotalGDP() * 100) : 0) + "%");
            System.out.println("  Expenses/GDP: " + (budget1.getTotalGDP() > 0 ? (budget1.getTotalExpenses() / budget1.getTotalGDP() * 100) : 0) + "%");
            System.out.println("Budget2 (" + country2Code + " - " + country2Name + "):");
            System.out.println("  GDP: " + budget2.getTotalGDP());
            System.out.println("  Revenue: " + budget2.getTotalRevenue() + " EUR");
            System.out.println("  Expenses: " + budget2.getTotalExpenses() + " EUR");
            System.out.println("  Balance: " + budget2.getBudgetBalance() + " EUR");
            System.out.println("  Revenue/GDP: " + (budget2.getTotalGDP() > 0 ? (budget2.getTotalRevenue() / budget2.getTotalGDP() * 100) : 0) + "%");
            System.out.println("  Expenses/GDP: " + (budget2.getTotalGDP() > 0 ? (budget2.getTotalExpenses() / budget2.getTotalGDP() * 100) : 0) + "%");
            System.out.println("Ratio (Country2/Country1):");
            System.out.println("  GDP: " + (budget1.getTotalGDP() > 0 ? (budget2.getTotalGDP() / budget1.getTotalGDP()) : 0));
            System.out.println("  Expenses: " + (budget1.getTotalExpenses() > 0 ? (budget2.getTotalExpenses() / budget1.getTotalExpenses()) : 0));
            System.out.println("================================");
            
            // Build comparison data for budget categories
            List<CountryComparisonData> comparisonData = new ArrayList<>();
            
            // Συνολικά Έσοδα - πάντα εμφανίζεται
            double diffRevenue = budget2.getTotalRevenue() - budget1.getTotalRevenue();
            comparisonData.add(new CountryComparisonData(
                "Συνολικά Έσοδα",
                AmountFormatter.formatCurrency(budget1.getTotalRevenue()),
                AmountFormatter.formatCurrency(budget2.getTotalRevenue()),
                AmountFormatter.formatCurrency(diffRevenue)
            ));
            
            // Συνολικές Δαπάνες - πάντα εμφανίζεται
            double diffExpenses = budget2.getTotalExpenses() - budget1.getTotalExpenses();
            comparisonData.add(new CountryComparisonData(
                "Συνολικές Δαπάνες",
                AmountFormatter.formatCurrency(budget1.getTotalExpenses()),
                AmountFormatter.formatCurrency(budget2.getTotalExpenses()),
                AmountFormatter.formatCurrency(diffExpenses)
            ));
            
            // Ισοζύγιο - πάντα εμφανίζεται
            double diffBalance = budget2.getBudgetBalance() - budget1.getBudgetBalance();
            comparisonData.add(new CountryComparisonData(
                "Ισοζύγιο",
                AmountFormatter.formatCurrency(budget1.getBudgetBalance()),
                AmountFormatter.formatCurrency(budget2.getBudgetBalance()),
                AmountFormatter.formatCurrency(diffBalance)
            ));
            
            // Update table
            if (internationalComparisonTable != null) {
                javafx.collections.ObservableList<CountryComparisonData> data = 
                    javafx.collections.FXCollections.observableArrayList(comparisonData);
                internationalComparisonTable.setItems(data);
            }
            
            // Update title
            if (internationalComparisonTitle != null) {
                String title = String.format("Σύγκριση: %s vs %s (%d)", 
                    country1Name != null ? country1Name : country1Code,
                    country2Name != null ? country2Name : country2Code,
                    year);
                
                if (year >= 2025) {
                    title += " [Εκτίμηση/Πρόβλεψη]";
                }
                internationalComparisonTitle.setText(title);
            }
            
            // Show results
            if (comparisonData.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Πληροφορία");
                alert.setHeaderText("Δεν βρέθηκαν αποτελέσματα");
                alert.setContentText("Δεν βρέθηκαν κοινά δείκτες για τις επιλεγμένες χώρες το έτος " + year + ".");
                alert.showAndWait();
            } else {
                if (internationalResultsContainer != null) {
                    internationalResultsContainer.setVisible(true);
                    internationalResultsContainer.setManaged(true);
                }
                Platform.runLater(() -> {
                    if (internationalComparisonTable != null && internationalComparisonTable.getScene() != null) {
                        Node scrollNode = internationalComparisonTable.getScene().lookup(".content-scroll");
                        if (scrollNode != null && scrollNode instanceof ScrollPane) {
                            ScrollPane scrollPane = (ScrollPane) scrollNode;
                            Node parent = internationalComparisonTable.getParent();
                            while (parent != null && !(parent instanceof ScrollPane)) {
                                parent = parent.getParent();
                            }
                            if (parent == scrollPane && scrollPane.getContent() != null) {
                                Bounds tableSceneBounds = internationalComparisonTable.localToScene(internationalComparisonTable.getBoundsInLocal());
                                Bounds contentSceneBounds = scrollPane.getContent().localToScene(scrollPane.getContent().getBoundsInLocal());
                                if (contentSceneBounds.getHeight() > 0) {
                                    double scrollValue = (tableSceneBounds.getMinY() - contentSceneBounds.getMinY()) / contentSceneBounds.getHeight();
                                    scrollPane.setVvalue(Math.min(1.0, Math.max(0.0, scrollValue)));
                                }
                            }
                        }
                    }
                });
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Error parsing year: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Μη έγκυρο έτος");
            alert.setContentText("Παρακαλώ επιλέξτε έγκυρο έτος.");
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error loading international comparison: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Σφάλμα φόρτωσης δεδομένων");
            alert.setContentText("Δεν ήταν δυνατή η φόρτωση των διεθνών δεικτών: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private String extractCountryCode(String countrySelection) {
        if (countrySelection == null) return null;
        if (countrySelection.contains("(") && countrySelection.contains(")")) {
            return countrySelection.substring(
                countrySelection.indexOf("(") + 1,
                countrySelection.indexOf(")")
            ).trim();
        }
        return countrySelection.trim();
    }
    
    private void loadAllCountriesTable() {
        if (allCountriesTable == null || internationalYearComboBox == null) {
            return;
        }
        
        String selectedYearStr = internationalYearComboBox.getValue();
        if (selectedYearStr == null || selectedYearStr.isEmpty()) {
            return;
        }
        
        try {
            int year = Integer.parseInt(selectedYearStr);
            BudgetData budgetData = BudgetData.getInstance();
            
            List<CountryBudgetData> countriesData = new ArrayList<>();
            
            List<BudgetData.InternationalBudget> budgets = budgetData.getAllInternationalBudgets(year);
            for (BudgetData.InternationalBudget budget : budgets) {
                String countryName = budget.getCountryName();
                if (countryName == null || countryName.isEmpty()) {
                    countryName = budget.getCountryCode();
                }
                countriesData.add(new CountryBudgetData(
                    countryName,
                    budget.getTotalRevenue(),
                    budget.getTotalExpenses(),
                    budget.getBudgetBalance()
                ));
            }
            
            BudgetData.InternationalBudget greekBudget = budgetData.getGreekBudget(year);
            if (greekBudget != null) {
                boolean greeceExists = false;
                for (CountryBudgetData data : countriesData) {
                    if ("Greece".equals(data.getCountryName()) || "GRC".equals(data.getCountryName())) {
                        greeceExists = true;
                        break;
                    }
                }
                if (!greeceExists) {
                    countriesData.add(new CountryBudgetData(
                        "Greece",
                        greekBudget.getTotalRevenue(),
                        greekBudget.getTotalExpenses(),
                        greekBudget.getBudgetBalance()
                    ));
                }
            }
            
            countriesData.sort((a, b) -> a.getCountryName().compareToIgnoreCase(b.getCountryName()));
            
            ObservableList<CountryBudgetData> data = FXCollections.observableArrayList(countriesData);
            allCountriesTable.setItems(data);
        } catch (Exception e) {
            System.err.println("Error loading all countries table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ενημερώνει τα country combo boxes
    private void updateCountryComboBoxAvailability() {
        if (internationalYearComboBox == null) return;
        
        BudgetData budgetData = BudgetData.getInstance();
        String selectedYearStr = internationalYearComboBox.getValue();
        int year = parseYearSafely(selectedYearStr);
        
        // Get list of available countries for this year
        List<String> availableCountries = year > 0 ? budgetData.getAvailableCountriesForYear(year) : new ArrayList<>();
        
        // Update both country combo boxes
        updateCountryComboBoxAvailability(internationalCountry1ComboBox, availableCountries, year);
        updateCountryComboBoxAvailability(internationalCountry2ComboBox, availableCountries, year);
    }
    
    // ενημερώνει ένα συγκεκριμένο country combo box
    private void updateCountryComboBoxAvailability(ComboBox<String> comboBox, List<String> availableCountries, int year) {
        if (comboBox == null) return;
        
        BudgetData budgetData = BudgetData.getInstance();
        
        // Set cell factory to disable unavailable items
        comboBox.setCellFactory(listView -> new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                } else {
                    setText(item);
                    String countryCode = extractCountryCode(item);
                    
                    // Ελλάδα είναι πάντα διαθέσιμη αν υπάρχουν ελληνικά δεδομένα
                    boolean isAvailable;
                    if ("GRC".equals(countryCode)) {
                        isAvailable = year > 0 && budgetData.hasBudgetDataForYear("GRC", year);
                    } else {
                        // Check if this country has budget data for the selected year
                        isAvailable = year > 0 && availableCountries.contains(countryCode);
                    }
                    
                    setDisable(!isAvailable);
                    
                    // Style disabled items
                    if (!isAvailable && year > 0) {
                        setStyle("-fx-text-fill: #9ca3af; -fx-opacity: 0.6;");
                    } else {
                        setStyle(null);
                    }
                }
            }
        });
        
        // Also update button cell
        comboBox.setButtonCell(new javafx.scene.control.ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                }
            }
        });
    }
    
    // ελέγχει τις επιλογές χωρών
    private void validateCountrySelections() {
        if (internationalYearComboBox == null) return;
        
        String selectedYearStr = internationalYearComboBox.getValue();
        int year = parseYearSafely(selectedYearStr);
        
        if (year <= 0) {
            // No year selected - clear country selections
            if (internationalCountry1ComboBox != null) {
                internationalCountry1ComboBox.setValue(null);
            }
            if (internationalCountry2ComboBox != null) {
                internationalCountry2ComboBox.setValue(null);
            }
            return;
        }
        
        BudgetData budgetData = BudgetData.getInstance();
        List<String> availableCountries = budgetData.getAvailableCountriesForYear(year);
        
        // Validate country 1
        if (internationalCountry1ComboBox != null && internationalCountry1ComboBox.getValue() != null) {
            String countryCode = extractCountryCode(internationalCountry1ComboBox.getValue());
            if (!availableCountries.contains(countryCode)) {
                internationalCountry1ComboBox.setValue(null);
            }
        }
        
        // Validate country 2
        if (internationalCountry2ComboBox != null && internationalCountry2ComboBox.getValue() != null) {
            String countryCode = extractCountryCode(internationalCountry2ComboBox.getValue());
            if (!availableCountries.contains(countryCode)) {
                internationalCountry2ComboBox.setValue(null);
            }
        }
    }

    private void loadComparisonData(int year1, int year2) {
        try {
            System.out.println("Loading comparison data for years: " + year1 + " and " + year2);
            
            Comparisons comparisons = new Comparisons();

            ObservableList<CategoryData> data =
                    comparisons.getComparisonTableData(year1, year2);

            System.out.println("Data loaded, items count: " + (data != null ? data.size() : 0));
            
            if (exploreResultsTable == null) {
                System.err.println("ERROR: exploreResultsTable is null!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Σφάλμα");
                alert.setHeaderText("Σφάλμα αρχικοποίησης");
                alert.setContentText("Ο πίνακας αποτελεσμάτων δεν είναι διαθέσιμος.");
                alert.showAndWait();
                return;
            }
            
            exploreResultsTable.setItems(data);
            System.out.println("Table items set");
            
            exploreResultsTable.setVisible(true);
            exploreResultsTable.setManaged(true);
            System.out.println("Table set to visible and managed");
            
            exploreResultsTable.requestLayout();
            
            if (exploreColumn2 != null) {
                exploreColumn2.setText(String.format("%d (€)", year1));
            }
            if (exploreColumn3 != null) {
                exploreColumn3.setText(String.format("%d (€)", year2));
            }
            
            Comparisons.ComparisonResults results = comparisons.compareYears(year1, year2);
            loadYearComparisonCharts(results, year1, year2);
            
            javafx.application.Platform.runLater(() -> {
                if (exploreResultsTable.getScene() != null) {
                    javafx.scene.Node scrollNode = exploreResultsTable.getScene().lookup(".content-scroll");
                    if (scrollNode != null && scrollNode instanceof javafx.scene.control.ScrollPane) {
                        javafx.scene.control.ScrollPane scrollPane = (javafx.scene.control.ScrollPane) scrollNode;
                        if (scrollPane.getContent() != null) {
                            javafx.geometry.Bounds tableSceneBounds = exploreResultsTable.localToScene(exploreResultsTable.getBoundsInLocal());
                            javafx.geometry.Bounds contentSceneBounds = scrollPane.getContent().localToScene(scrollPane.getContent().getBoundsInLocal());
                            if (contentSceneBounds.getHeight() > 0) {
                                double scrollValue = (tableSceneBounds.getMinY() - contentSceneBounds.getMinY()) / contentSceneBounds.getHeight();
                                scrollPane.setVvalue(Math.min(1.0, Math.max(0.0, scrollValue)));
                            }
                        }
                    }
                }
            });
            
            System.out.println("Comparison data loaded successfully");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Σφάλμα φόρτωσης δεδομένων");
            alert.setContentText("Δεν ήταν δυνατή η φόρτωση των δεδομένων σύγκρισης.\n\nΣφάλμα: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    // φορτώνει τα overview charts
    private void loadOverviewCharts() {
        try {
            BudgetData budgetData = BudgetData.getInstance();
            List<String> availableYears = getAvailableYearsFromDatabase();
            
            if (availableYears.isEmpty()) {
                // Hide charts if no data available
                if (overviewChartsContainer != null) {
                    overviewChartsContainer.setVisible(false);
                    overviewChartsContainer.setManaged(false);
                }
                return;
            }
            
            // Show charts container
            if (overviewChartsContainer != null) {
                overviewChartsContainer.setVisible(true);
                overviewChartsContainer.setManaged(true);
            }
            
            // Load trends line chart
            if (overviewTrendsChart != null) {
                Charts.loadOverviewTrendsChart(overviewTrendsChart, budgetData, availableYears);
            }
        } catch (Exception e) {
            System.err.println("Error loading overview charts: " + e.getMessage());
            e.printStackTrace();
            // Hide charts on error
            if (overviewChartsContainer != null) {
                overviewChartsContainer.setVisible(false);
                overviewChartsContainer.setManaged(false);
            }
        }
    }
    
    private void loadYearComparisonCharts(Comparisons.ComparisonResults results, int year1, int year2) {
        if (results == null) return;
        
        // Load revenue vs expenses chart
        if (yearComparisonRevenueExpensesChart != null) {
            Charts.loadYearComparisonRevenueExpensesChart(
                yearComparisonRevenueExpensesChart,
                results.getTotalRevenueSummary1(),
                results.getTotalExpensesSummary1(),
                results.getTotalRevenueSummary2(),
                results.getTotalExpensesSummary2(),
                year1,
                year2
            );
        }
        
        // Show chart container
        if (yearComparisonChartContainer != null) {
            yearComparisonChartContainer.setVisible(true);
            yearComparisonChartContainer.setManaged(true);
        }
    }


    
    @FXML
    private void onNavigateProjections() {
        if (projectionsView == null) {
            return;
        }
        showView(projectionsView);
        
        // Initialize year combo box if empty
        if (simulationBaseYearComboBox != null && simulationBaseYearComboBox.getItems().isEmpty()) {
            List<String> availableYears = getAvailableYearsFromDatabase();
            simulationBaseYearComboBox.getItems().addAll(availableYears);
            
            if (!availableYears.isEmpty()) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                String currentYearStr = String.valueOf(currentYear);
                if (availableYears.contains(currentYearStr)) {
                    simulationBaseYearComboBox.setValue(currentYearStr);
                } else {
                    simulationBaseYearComboBox.setValue(availableYears.get(availableYears.size() - 1));
                }
                // Αυτόματη φόρτωση δεδομένων όταν ορίζεται το έτος
                onLoadSimulationData();
            }
        }
        
        // Initialize simulation type tab pane - first tab is selected by default
        if (simulationTypeTabPane != null && simulationTypeTabPane.getTabs().size() >= 2) {
            simulationTypeTabPane.getSelectionModel().select(0); // Select first tab (Εσόδων)
            if (expensesLevelContainer != null) {
                expensesLevelContainer.setVisible(false);
                expensesLevelContainer.setManaged(false);
            }
            if (simulationCategoryColumn != null) {
                simulationCategoryColumn.setText("Κατηγορία");
            }
            simulationTypeTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                if (newTab != null) {
                    onSimulationTypeTabChanged();
                }
            });
        }
        
        // Initialize simulation level combo box (only for Expenses tab)
        if (simulationLevelComboBox != null && simulationLevelComboBox.getItems().isEmpty()) {
            simulationLevelComboBox.getItems().addAll("Υπουργεία", "Είδη Δαπανών");
            simulationLevelComboBox.setValue("Είδη Δαπανών");
        }
        
        // Initialize simulation table
        if (simulationSelectionsTable != null && simulationCheckColumn != null) {
            initializeSimulationTable();
        }
      
        if (simulationBaseYearComboBox != null && simulationBaseYearComboBox.getValue() != null) {
            refreshSimulationSelections();
        }
        
        // Clear results
        if (simulationResultsArea != null) {
            simulationResultsArea.clear();
        }
    }
    
    @FXML
    private void onSimulationBaseYearSelected() {
        // Αυτόματη φόρτωση δεδομένων όταν επιλέγεται έτος
        if (simulationBaseYearComboBox != null && simulationBaseYearComboBox.getValue() != null) {
            onLoadSimulationData();
        }
    }
    
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
            
            // Refresh simulation selections when data is loaded
            refreshSimulationSelections();
            
            if (simulationResultsArea != null) {
                simulationResultsArea.setText("✅ Δεδομένα φορτώθηκαν επιτυχώς!\n\n" +
                    "Έτος: " + year + "\n" +
                    "Συνολικά Έσοδα: " + AmountFormatter.formatCurrency(simulationBaseRevenue) + "\n" +
                    "Συνολικές Δαπάνες: " + AmountFormatter.formatCurrency(simulationBaseExpense) + "\n" +
                    "Ισοζύγιο: " + AmountFormatter.formatCurrency(simulationBaseRevenue - simulationBaseExpense) + "\n\n" +
                    "Επιλέξτε κατηγορίες/υπουργεία και ποσοστά μεταβολής, στη συνέχεια πατήστε 'Αποτελέσματα'.");
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
    
    private void initializeSimulationTable() {
        if (simulationSelectionsTable == null) return;
        
        // Setup CheckBox column
        simulationCheckColumn.setCellValueFactory(param -> param.getValue().selectedProperty());
        simulationCheckColumn.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Integer index) {
                return simulationSelectionsTable.getItems().get(index).selectedProperty();
            }
        }));
        simulationCheckColumn.setEditable(true);
        
        // Setup Category column
        simulationCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        
        // Setup Amount column
        simulationAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        simulationAmountColumn.setCellFactory(column -> new TableCell<SimulationSelectionItem, Double>() {
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
        
        // Setup Change Percent column
        simulationChangeColumn.setCellValueFactory(new PropertyValueFactory<>("changePercent"));
        simulationChangeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        simulationChangeColumn.setEditable(true);
        
        // Set table data
        simulationSelectionsTable.setItems(simulationSelections);
        simulationSelectionsTable.setEditable(true);
    }
    
    private String getSelectedSimulationType() {
        if (simulationTypeTabPane == null) {
            return null;
        }
        Tab selectedTab = simulationTypeTabPane.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            return null;
        }
        String tabText = selectedTab.getText();
        if (tabText.contains("Εσόδων")) {
            return "Μεταβολή Εσόδων";
        } else if (tabText.contains("Εξόδων")) {
            return "Μεταβολή Εξόδων";
        }
        return null;
    }
    
    @FXML
    private void onSimulationTypeTabChanged() {
        // Show/hide expenses level container based on selected tab
        String type = getSelectedSimulationType();
        if (expensesLevelContainer != null) {
            expensesLevelContainer.setVisible("Μεταβολή Εξόδων".equals(type));
            expensesLevelContainer.setManaged("Μεταβολή Εξόδων".equals(type));
        }
        
        // Update category column header based on selected tab
        if (simulationCategoryColumn != null) {
            if ("Μεταβολή Εσόδων".equals(type)) {
                simulationCategoryColumn.setText("Κατηγορία");
            } else {
                simulationCategoryColumn.setText("Κατηγορία/Υπουργείο");
            }
        }
        
        refreshSimulationSelections();
    }
    
    @FXML
    private void onSimulationLevelChanged() {
        refreshSimulationSelections();
    }
    
    private void refreshSimulationSelections() {
        if (simulationBaseYearComboBox == null || simulationBaseYearComboBox.getValue() == null) {
            return;
        }
        
        String type = getSelectedSimulationType();
        if (type == null) {
            return;
        }
        
        try {
            int year = Integer.parseInt(simulationBaseYearComboBox.getValue());
            BudgetData service = BudgetData.getInstance();
            
            simulationSelections.clear();
            
            if ("Μεταβολή Εσόδων".equals(type)) {
                // For revenues, always show categories (no ComboBox needed)
                List<BudgetData.CategoryInfo> revenues = service.getRevenueBreakdown(year);
                for (BudgetData.CategoryInfo cat : revenues) {
                    simulationSelections.add(new SimulationSelectionItem(cat.getName(), cat.getAmount()));
                }
            } else if ("Μεταβολή Εξόδων".equals(type)) {
                // For expenses, check ComboBox selection
                if (simulationLevelComboBox == null || simulationLevelComboBox.getValue() == null) {
                    return;
                }
                String level = simulationLevelComboBox.getValue();
                
                if ("Είδη Δαπανών".equals(level)) {
                    List<BudgetData.CategoryInfo> expenses = service.getExpensesBreakdown(year);
                    for (BudgetData.CategoryInfo cat : expenses) {
                        simulationSelections.add(new SimulationSelectionItem(cat.getName(), cat.getAmount()));
                    }
                } else if ("Υπουργεία".equals(level)) {
                    Map<String, Double> ministries = service.getMinistriesBreakdown(year);
                    for (Map.Entry<String, Double> entry : ministries.entrySet()) {
                        simulationSelections.add(new SimulationSelectionItem(entry.getKey(), entry.getValue()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSimulationError("Σφάλμα φόρτωσης δεδομένων: " + e.getMessage());
        }
    }
    
    @FXML
    private void onApplyDefaultChange() {
        if (simulationDefaultChangeField == null || simulationDefaultChangeField.getText().isEmpty()) {
            showSimulationError("Παρακαλώ εισάγετε ποσοστό μεταβολής.");
            return;
        }
        
        try {
            String changePercent = simulationDefaultChangeField.getText().trim();
            // Remove % if present
            if (changePercent.endsWith("%")) {
                changePercent = changePercent.substring(0, changePercent.length() - 1).trim();
            }
            // Validate it's a valid number
            Double.parseDouble(changePercent);
            
            for (SimulationSelectionItem item : simulationSelections) {
                item.setChangePercent(changePercent);
                item.setSelected(true);
            }
        } catch (NumberFormatException e) {
            showSimulationError("Παρακαλώ εισάγετε έγκυρο ποσοστό (π.χ. 5% ή -5% ή 5.0 ή -5.0).");
        }
    }
    
    @FXML
    private void onCalculateSimulationResults() {
        if (simulationBaseRevenue == 0 || simulationBaseExpense == 0) {
            showSimulationError("Παρακαλώ φορτώστε πρώτα τα βασικά δεδομένα.");
            return;
        }
        
        String type = getSelectedSimulationType();
        if (type == null) {
            showSimulationError("Παρακαλώ επιλέξτε τύπο μεταβολής.");
            return;
        }
        
        // Get selected items with changes
        List<SimulationSelectionItem> selectedItems = new ArrayList<>();
        for (SimulationSelectionItem item : simulationSelections) {
            if (item.isSelected() && item.getChangePercent() != null && !item.getChangePercent().isEmpty()) {
                try {
                    String percentStr = item.getChangePercent().trim();
                    // Remove % if present
                    if (percentStr.endsWith("%")) {
                        percentStr = percentStr.substring(0, percentStr.length() - 1).trim();
                    }
                    Double.parseDouble(percentStr);
                    selectedItems.add(item);
                } catch (NumberFormatException e) {
                    // Skip items with invalid percentages
                }
            }
        }
        
        if (selectedItems.isEmpty()) {
            showSimulationError("Παρακαλώ επιλέξτε τουλάχιστον μία κατηγορία/υπουργείο και εισάγετε ποσοστό μεταβολής.");
            return;
        }
        
        // Calculate results
        try {
            double totalChange = 0;
            StringBuilder details = new StringBuilder();
            details.append("📊 Αποτελέσματα Προσομοίωσης\n");
            details.append("================================\n\n");
            details.append("Έτος: ").append(simulationBaseYear).append("\n\n");
            
            for (SimulationSelectionItem item : selectedItems) {
                String percentStr = item.getChangePercent().trim();
                // Remove % if present
                if (percentStr.endsWith("%")) {
                    percentStr = percentStr.substring(0, percentStr.length() - 1).trim();
                }
                double changePercent = Double.parseDouble(percentStr);
                double itemChange = item.getAmount() * (changePercent / 100.0);
                totalChange += itemChange;
                
                details.append("• ").append(item.getCategoryName()).append(": ");
                details.append(AmountFormatter.formatCurrency(item.getAmount())).append(" → ");
                details.append(String.format("%+.1f%%", changePercent)).append(" = ");
                details.append(AmountFormatter.formatCurrency(itemChange)).append("\n");
            }
            
            details.append("\nΣυνολική Μεταβολή: ").append(AmountFormatter.formatCurrency(totalChange)).append("\n");
            
            double newTotal;
            if ("Μεταβολή Εσόδων".equals(type)) {
                newTotal = simulationBaseRevenue + totalChange;
                details.append("Νέα Συνολικά Έσοδα: ").append(AmountFormatter.formatCurrency(newTotal)).append("\n");
                details.append("Προηγούμενα Συνολικά Έσοδα: ").append(AmountFormatter.formatCurrency(simulationBaseRevenue)).append("\n");
            } else {
                newTotal = simulationBaseExpense + totalChange;
                details.append("Νέες Συνολικές Δαπάνες: ").append(AmountFormatter.formatCurrency(newTotal)).append("\n");
                details.append("Προηγούμενες Συνολικές Δαπάνες: ").append(AmountFormatter.formatCurrency(simulationBaseExpense)).append("\n");
            }
            
            double newBalance;
            if ("Μεταβολή Εσόδων".equals(type)) {
                newBalance = newTotal - simulationBaseExpense;
            } else {
                newBalance = simulationBaseRevenue - newTotal;
            }
            
            details.append("\nΝέο Ισοζύγιο: ").append(AmountFormatter.formatCurrency(newBalance)).append("\n");
            details.append("Προηγούμενο Ισοζύγιο: ").append(AmountFormatter.formatCurrency(simulationBaseRevenue - simulationBaseExpense)).append("\n");
            
            double balanceChange = newBalance - (simulationBaseRevenue - simulationBaseExpense);
            details.append("Μεταβολή Ισοζυγίου: ").append(AmountFormatter.formatCurrency(balanceChange)).append("\n");
            
            if (simulationResultsArea != null) {
                simulationResultsArea.setText(details.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showSimulationError("Σφάλμα υπολογισμού: " + e.getMessage());
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
                statsRevenueCoeffVarLabel.setText(String.format("%.2f%%", revenueCoeffVar));
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
                    statsExpenseCoeffVarLabel.setText(String.format("%.2f%%", expenseCoeffVar));
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
            
            ObservableList<Map<String, Object>> outliersData = FXCollections.observableArrayList();
            for (Double outlierValue : revenueOutliers) {
                for (Map.Entry<Integer, Double> entry : revenueYearMap.entrySet()) {
                    double diff = Math.abs(entry.getValue() - outlierValue);
                    double tolerance = Math.max(Math.abs(outlierValue) * 0.0001, 1000.0);
                    if (diff < tolerance || Math.abs(diff / Math.max(Math.abs(outlierValue), 1.0)) < 0.0001) {
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
                    for (Map.Entry<Integer, Double> entry : expenseYearMap.entrySet()) {
                        double diff = Math.abs(entry.getValue() - outlierValue);
                        double tolerance = Math.max(Math.abs(outlierValue) * 0.0001, 1000.0);
                        if (diff < tolerance || Math.abs(diff / Math.max(Math.abs(outlierValue), 1.0)) < 0.0001) {
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
        if (internationalComparisonView != null) {
            internationalComparisonView.setVisible(false);
            internationalComparisonView.setManaged(false);
        }
        if (aiAssistantView != null) {
            aiAssistantView.setVisible(false);
            aiAssistantView.setManaged(false);
        }
        
        // Update menu item selection states
        updateMenuSelection(view);
        
        // Show selected view
        if (view != null) {
            view.setVisible(true);
            view.setManaged(true);
        }
    }
    
    private void updateMenuSelection(VBox view) {
        // Remove selected style from ALL navigation buttons
        Button[] allNavButtons = {
            homeButton,
            projectionsButton,
            statisticsButton,
            aiAssistantButton
        };
        
        // Remove selected class from all buttons first
        for (Button button : allNavButtons) {
            if (button != null) {
                ObservableList<String> styles = button.getStyleClass();
                styles.removeAll("header-nav-button-active");
                if (!styles.contains("header-nav-button")) {
                    styles.add("header-nav-button");
                }
            }
        }
        
        // Handle comparisons menu button separately
        if (comparisonsMenuButton != null) {
            ObservableList<String> menuStyles = comparisonsMenuButton.getStyleClass();
            menuStyles.removeAll("header-nav-button-active");
            if (!menuStyles.contains("header-nav-button")) {
                menuStyles.add("header-nav-button");
            }
        }
        
        // Add selected style to the appropriate button
        Button buttonToSelect = null;
        if (view == homeView) {
            buttonToSelect = homeButton;
        } else if (view == projectionsView) {
            buttonToSelect = projectionsButton;
        } else if (view == aiAssistantView) {
            buttonToSelect = aiAssistantButton;
        } else if (view == dataExplorationView || view == internationalComparisonView) {
            // Both comparison views highlight the comparisons menu button
            buttonToSelect = null; // Will handle menu button separately
            if (comparisonsMenuButton != null) {
                ObservableList<String> menuStyles = comparisonsMenuButton.getStyleClass();
                if (!menuStyles.contains("header-nav-button-active")) {
                    menuStyles.add("header-nav-button-active");
                }
            }
        } else if (view == statisticsView) {
            buttonToSelect = statisticsButton;
        }
        
        if (buttonToSelect != null) {
            ObservableList<String> styles = buttonToSelect.getStyleClass();
            if (!styles.contains("header-nav-button-active")) {
                styles.add("header-nav-button-active");
            }
        }
    }
    
    // Data model for simulation selections
    public static class SimulationSelectionItem {
        private final BooleanProperty selected = new SimpleBooleanProperty();
        private final StringProperty categoryName = new SimpleStringProperty();
        private final DoubleProperty amount = new SimpleDoubleProperty();
        private final StringProperty changePercent = new SimpleStringProperty("0.0");
        
        public SimulationSelectionItem(String categoryName, double amount) {
            this.categoryName.set(categoryName);
            this.amount.set(amount);
        }
        
        public BooleanProperty selectedProperty() { return selected; }
        public boolean isSelected() { return selected.get(); }
        public void setSelected(boolean selected) { this.selected.set(selected); }
        
        public StringProperty categoryNameProperty() { return categoryName; }
        public String getCategoryName() { return categoryName.get(); }
        public void setCategoryName(String name) { this.categoryName.set(name); }
        
        public DoubleProperty amountProperty() { return amount; }
        public double getAmount() { return amount.get(); }
        public void setAmount(double amount) { this.amount.set(amount); }
        
        public StringProperty changePercentProperty() { return changePercent; }
        public String getChangePercent() { return changePercent.get(); }
        public void setChangePercent(String percent) { this.changePercent.set(percent); }
    }
}