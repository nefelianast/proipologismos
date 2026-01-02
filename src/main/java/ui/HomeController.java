package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;

public class HomeController {
    
    public enum UserType {
        CITIZEN,
        GOVERNMENT
    }
    
    private static UserType currentUserType = UserType.CITIZEN; // Default to citizen
    
    public static void setUserType(UserType userType) {
        currentUserType = userType;
    }
    
    public static UserType getUserType() {
        return currentUserType;
    }

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
    private Label headerTitleLabel;
    @FXML
    private javafx.scene.control.Button authButton;
    @FXML
    private ComboBox<String> yearComboBox;
    
    private String selectedYear = "2025";
    
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
    private HBox dataManagementButtonContainer;
    @FXML
    private Button dataManagementButton;
    @FXML
    private Button projectionsButton;
    
    // Data Management UI
    @FXML
    private TableView<CategoryData> dataManagementTable;
    @FXML
    private TableColumn<CategoryData, String> dmCategoryColumn;
    @FXML
    private TableColumn<CategoryData, Double> dmAmountColumn;
    @FXML
    private TableColumn<CategoryData, String> dmYearColumn;
    @FXML
    private TableColumn<CategoryData, String> dmTypeColumn;
    @FXML
    private Button editDataButton;
    @FXML
    private Button deleteDataButton;
    @FXML
    private TextArea internalCommentsArea;
    
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
    

    private final DecimalFormat df = new DecimalFormat("#,##0.0");
    private BudgetDataService dataService;

    @FXML
    private void initialize() {
        // Initialize data service
        dataService = BudgetDataService.getInstance();
        
        // Set initial header title
        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Κρατικός Προϋπολογισμός του 2025");
        }
        
        // Initialize auth button based on user type
        updateAuthButton();
        
        // Show/hide government features based on user type
        updateGovernmentFeatures();
        
        // Initialize year ComboBox
        if (yearComboBox != null) {
            yearComboBox.getItems().addAll("2023", "2024", "2025");
            yearComboBox.setValue("2025");
        }
        
        // Initialize data management table
        if (dataManagementTable != null) {
            dmCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            dmAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            dmYearColumn.setCellValueFactory(param -> {
                // Extract year from change field (format: "2025 | Έσοδο" or "2025 | Δαπάνη")
                String change = param.getValue().getChange();
                if (change != null && change.contains("|")) {
                    return new javafx.beans.property.SimpleStringProperty(change.split("\\|")[0].trim());
                }
                return new javafx.beans.property.SimpleStringProperty("");
            });
            dmTypeColumn.setCellValueFactory(param -> {
                // Extract type from change field
                String change = param.getValue().getChange();
                if (change != null && change.contains("|")) {
                    return new javafx.beans.property.SimpleStringProperty(change.split("\\|")[1].trim());
                }
                return new javafx.beans.property.SimpleStringProperty("");
            });
            
            dmAmountColumn.setCellFactory(column -> new TableCell<CategoryData, Double>() {
                @Override
                protected void updateItem(Double amount, boolean empty) {
                    super.updateItem(amount, empty);
                    if (empty || amount == null) {
                        setText(null);
                    } else {
                        setText("€" + df.format(amount));
                    }
                }
            });
            
            // Enable/disable edit/delete buttons based on selection
            dataManagementTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                boolean hasSelection = newSelection != null;
                if (editDataButton != null) editDataButton.setDisable(!hasSelection);
                if (deleteDataButton != null) deleteDataButton.setDisable(!hasSelection);
            });
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
                    setText("€" + df.format(amount));
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
                    setText("€" + df.format(amount));
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
                    setText(String.format("%.1f%%", percentage));
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
                    setText("€" + df.format(amount));
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
                    setText(String.format("%.1f%%", percentage));
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
                    setText("€" + df.format(amount));
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
                    setText(String.format("%.1f%%", percentage));
                }
            }
        });

        // Load initial data
        updateDataForYear();
        
        // Set up quick navigation cards
        setupQuickNavigation();
        
        // Show home view (this will also update the header button)
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
    
    private void updateGovernmentFeatures() {
        boolean isGovernment = currentUserType == UserType.GOVERNMENT;
        
        // Show/hide data management view
        if (dataManagementView != null) {
            dataManagementView.setVisible(isGovernment);
            dataManagementView.setManaged(isGovernment);
        }
        
        // Show/hide data management button in header
        if (dataManagementButtonContainer != null) {
            dataManagementButtonContainer.setVisible(isGovernment);
            dataManagementButtonContainer.setManaged(isGovernment);
        }
    }
    
    @FXML
    private void onAuthButtonClicked() {
        if (currentUserType == UserType.CITIZEN) {
            // Show government login dialog
            showGovernmentLogin();
        } else {
            // Logout - go back to login screen as citizen
            currentUserType = UserType.CITIZEN;
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

            // Username field
            TextField usernameField = new TextField();
            usernameField.setPrefWidth(300);
            usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            
            Label usernamePrompt = new Label("Όνομα χρήστη");
            usernamePrompt.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af;");
            usernamePrompt.setMouseTransparent(true);
            usernamePrompt.setPadding(new javafx.geometry.Insets(0, 0, 0, 10));
            
            javafx.scene.layout.StackPane usernamePane = new javafx.scene.layout.StackPane();
            usernamePane.setPrefWidth(300);
            usernamePane.getChildren().addAll(usernameField, usernamePrompt);
            javafx.scene.layout.StackPane.setAlignment(usernamePrompt, javafx.geometry.Pos.CENTER_LEFT);
            
            usernameField.textProperty().addListener((obs, oldText, newText) -> {
                usernamePrompt.setVisible(newText == null || newText.isEmpty());
            });

            // Password field
            PasswordField passwordField = new PasswordField();
            passwordField.setPrefWidth(300);
            passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            
            Label passwordPrompt = new Label("Κωδικός πρόσβασης");
            passwordPrompt.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af;");
            passwordPrompt.setMouseTransparent(true);
            passwordPrompt.setPadding(new javafx.geometry.Insets(0, 0, 0, 10));
            
            javafx.scene.layout.StackPane passwordPane = new javafx.scene.layout.StackPane();
            passwordPane.setPrefWidth(300);
            passwordPane.getChildren().addAll(passwordField, passwordPrompt);
            javafx.scene.layout.StackPane.setAlignment(passwordPrompt, javafx.geometry.Pos.CENTER_LEFT);
            
            passwordField.textProperty().addListener((obs, oldText, newText) -> {
                passwordPrompt.setVisible(newText == null || newText.isEmpty());
            });

            javafx.scene.control.Button loginButton = new javafx.scene.control.Button("Σύνδεση");
            loginButton.setPrefWidth(300);
            loginButton.setPrefHeight(40);
            loginButton.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            loginButton.setOnAction(e -> {
                String username = usernameField.getText();
                String password = passwordField.getText();
                
                // Accept any username and password for now
                if (username != null && !username.trim().isEmpty() && 
                    password != null && !password.trim().isEmpty()) {
                    // Set user type to government
                    currentUserType = UserType.GOVERNMENT;
                    updateAuthButton();
                    
                    // Close login dialog
                    loginStage.close();
                }
            });

            loginPane.getChildren().addAll(titleLabel, usernamePane, passwordPane, loginButton);

            Scene loginScene = new Scene(loginPane, 400, 300);
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
            
            // Update data
            updateDataForYear();
        }
    }
    
    private void updateDataForYear() {
        if (selectedYear == null) return;

        // Update header title with year
        if (headerTitleLabel != null) {
            headerTitleLabel.setText("Κρατικός Προϋπολογισμός του " + selectedYear);
        }
        
        // Update welcome title with year
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
        totalRevenuesLabel.setText("€" + df.format(totalRevenues));
        String revenuesChangeText = String.format("%+.1f%% από πέρυσι", revenuesChange);
        revenuesDeltaLabel.setText(revenuesChangeText);
        revenuesDeltaLabel.getStyleClass().removeAll("negative", "positive");
        revenuesDeltaLabel.getStyleClass().add(revenuesChange >= 0 ? "positive" : "negative");
        revenuesDeltaLabel.setStyle("-fx-text-fill: white;");
        
        // Update expenses
        totalExpensesLabel.setText("€" + df.format(totalExpenses));
        String expensesChangeText = String.format("%+.1f%% από πέρυσι", expensesChange);
        expensesDeltaLabel.setText(expensesChangeText);
        expensesDeltaLabel.getStyleClass().removeAll("negative", "positive");
        expensesDeltaLabel.getStyleClass().add(expensesChange >= 0 ? "positive" : "negative");
        expensesDeltaLabel.setStyle("-fx-text-fill: white;");
        
        // Update balance
        if (balance >= 0) {
            balanceLabel.setText("+€" + df.format(balance));
            balanceStatusLabel.setText("Πλεόνασμα");
            balanceStatusLabel.getStyleClass().removeAll("negative");
            balanceStatusLabel.getStyleClass().add("positive");
            balanceStatusLabel.setStyle("-fx-text-fill: white;");
        } else {
            balanceLabel.setText("€" + df.format(balance));
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
                // No previous year data available - show dash
                changeText = "-";
            } else {
                double prevAmount = prevYearMap.get(cat.getName());
                if (prevAmount > 0) {
                    double change = ((cat.getAmount() - prevAmount) / prevAmount) * 100;
                    changeText = String.format("%+.1f%%", change);
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

    // Navigation handlers (called from quick nav cards)
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
    private void onNavigateDataManagement() {
        if (currentUserType == UserType.GOVERNMENT) {
            showView(dataManagementView);
            loadDataManagementTable();
        }
    }
    
    @FXML
    private void onNavigateProjections() {
        // Coming soon - no action for now
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
        
        // Show selected view
        if (view != null) {
            view.setVisible(true);
            view.setManaged(true);
        }
        
        // Update header button based on current view
        updateHeaderButton(view);
    }
    
    private void updateHeaderButton(VBox currentView) {
        if (dataManagementButton != null) {
            if (currentView == dataManagementView) {
                // We're in data management view, show back button
                dataManagementButton.setText("← Αρχική");
                dataManagementButton.setOnAction(e -> onNavigateHome());
            } else {
                // We're in home or other view, show data management button
                dataManagementButton.setText("Διαχείριση Δεδομένων");
                dataManagementButton.setOnAction(e -> onNavigateDataManagement());
            }
        }
        
    }
    
    // Data Management Methods
    private void loadDataManagementTable() {
        // Load data for management table
        if (dataManagementTable != null && dataService != null) {
            ObservableList<CategoryData> tableData = FXCollections.observableArrayList();
            
            // Load data for all available years (2023, 2024, 2025)
            int[] years = {2023, 2024, 2025};
            
            for (int year : years) {
                // Load ministries (expenses by ministry)
                List<BudgetDataService.CategoryInfo> ministries = dataService.getCategories(year);
                for (BudgetDataService.CategoryInfo cat : ministries) {
                    tableData.add(new CategoryData(
                        cat.getName(), 
                        cat.getAmount(), 
                        cat.getPercentage(), 
                        String.valueOf(year) + " | Υπουργείο"
                    ));
                }
                
                // Load expense breakdown (by expense type)
                List<BudgetDataService.CategoryInfo> expenses = dataService.getExpensesBreakdown(year);
                for (BudgetDataService.CategoryInfo cat : expenses) {
                    tableData.add(new CategoryData(
                        cat.getName(), 
                        cat.getAmount(), 
                        cat.getPercentage(), 
                        String.valueOf(year) + " | Δαπάνη"
                    ));
                }
                
                // Load revenues (by revenue type)
                List<BudgetDataService.CategoryInfo> revenues = dataService.getRevenueBreakdown(year);
                for (BudgetDataService.CategoryInfo cat : revenues) {
                    tableData.add(new CategoryData(
                        cat.getName(), 
                        cat.getAmount(), 
                        cat.getPercentage(), 
                        String.valueOf(year) + " | Έσοδο"
                    ));
                }
                
                // Load decentralized administrations
                List<BudgetDataService.CategoryInfo> administrations = dataService.getDecentralizedAdministrations(year);
                for (BudgetDataService.CategoryInfo cat : administrations) {
                    tableData.add(new CategoryData(
                        cat.getName(), 
                        cat.getAmount(), 
                        cat.getPercentage(), 
                        String.valueOf(year) + " | Αποκεντρωμένη Διοίκηση"
                    ));
                }
            }
            
            dataManagementTable.setItems(tableData);
        }
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
        TextField amountField = new TextField();
        amountField.setPromptText("Ποσό");
        TextField yearField = new TextField();
        yearField.setPromptText("Έτος");
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Έσοδο", "Δαπάνη");
        typeCombo.setPromptText("Τύπος");
        
        grid.add(new Label("Κατηγορία:"), 0, 0);
        grid.add(categoryField, 1, 0);
        grid.add(new Label("Ποσό:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Έτος:"), 0, 2);
        grid.add(yearField, 1, 2);
        grid.add(new Label("Τύπος:"), 0, 3);
        grid.add(typeCombo, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType addButton = new ButtonType("Προσθήκη", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    String year = yearField.getText();
                    return new CategoryData(categoryField.getText(), amount, 0.0, year);
                } catch (NumberFormatException e) {
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
        CategoryData selected = dataManagementTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Dialog<CategoryData> dialog = new Dialog<>();
        dialog.setTitle("Επεξεργασία Δεδομένου");
        dialog.setHeaderText("Επεξεργαστείτε τα στοιχεία");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField categoryField = new TextField(selected.getCategory());
        TextField amountField = new TextField(String.valueOf(selected.getAmount()));
        
        grid.add(new Label("Κατηγορία:"), 0, 0);
        grid.add(categoryField, 1, 0);
        grid.add(new Label("Ποσό:"), 0, 1);
        grid.add(amountField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType saveButton = new ButtonType("Αποθήκευση", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    selected.categoryProperty().set(categoryField.getText());
                    selected.amountProperty().set(amount);
                    dataManagementTable.refresh();
                    return selected;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    @FXML
    private void onDeleteData() {
        CategoryData selected = dataManagementTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Επιβεβαίωση Διαγραφής");
        confirmDialog.setHeaderText("Είστε σίγουροι ότι θέλετε να διαγράψετε αυτό το δεδομένο;");
        confirmDialog.setContentText("Κατηγορία: " + selected.getCategory());
        
        java.util.Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dataManagementTable.getItems().remove(selected);
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
                
                // Parse CSV line (assuming format: Category,Amount,Year,Type)
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
            // Here you would save comments to database or file
            // For now, just show confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Αποθήκευση Σχολίων");
            alert.setHeaderText("Επιτυχής Αποθήκευση");
            alert.setContentText("Τα εσωτερικά σχόλια (" + comments.length() + " χαρακτήρες) αποθηκεύτηκαν επιτυχώς.");
            alert.showAndWait();
        }
    }

}
