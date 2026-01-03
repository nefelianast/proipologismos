package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private VBox citizenCard;
    @FXML
    private VBox governmentCard;
    @FXML
    private ImageView logoImageView;

    @FXML
    private void initialize() {
        // Apply style to logo
        if (logoImageView != null) {
            logoImageView.getStyleClass().add("login-logo");
        }
        
        // Set up click handlers
        citizenCard.setOnMouseClicked(e -> onCitizenSelected());
        citizenCard.setCursor(javafx.scene.Cursor.HAND);
        
        governmentCard.setOnMouseClicked(e -> onGovernmentSelected());
        governmentCard.setCursor(javafx.scene.Cursor.HAND);
    }

    @FXML
    private void onCitizenSelected() {
        // Set user type to citizen
        HomeController.setUserType(HomeController.UserType.CITIZEN);
        
        // Go directly to main application
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Home.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1400, 900);
            scene.getStylesheets().add(getClass().getResource("/ui/styles.css").toExternalForm());
            
            Stage stage = (Stage) citizenCard.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Κρατικός Προϋπολογισμός - Σύστημα Ανάλυσης");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Σφάλμα", "Δεν ήταν δυνατή η πρόσβαση στην εφαρμογή.\n\nΛεπτομέρειες: " + e.getMessage() + "\n\nΠαρακαλώ ελέγξτε τα logs για περισσότερες πληροφορίες.");
        }
    }

    @FXML
    private void onGovernmentSelected() {
        // Show government login dialog
        showGovernmentLogin();
    }

    private void showGovernmentLogin() {
        try {
            // Create login dialog
            Stage loginStage = new Stage();
            loginStage.setTitle("Σύνδεση - Κυβέρνηση");
            loginStage.setResizable(false);

            VBox loginPane = new VBox(20);
            loginPane.setPadding(new Insets(40));
            loginPane.setStyle("-fx-background-color: white;");

            Label titleLabel = new Label("Σύνδεση Κυβέρνησης");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e40af;");

            // Username field with visible placeholder
            TextField usernameField = new TextField();
            usernameField.setPrefWidth(300);
            usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            
            Label usernamePrompt = new Label("Όνομα χρήστη");
            usernamePrompt.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af;");
            usernamePrompt.setMouseTransparent(true);
            usernamePrompt.setPadding(new Insets(0, 0, 0, 10));
            
            StackPane usernamePane = new StackPane();
            usernamePane.setPrefWidth(300);
            usernamePane.getChildren().addAll(usernameField, usernamePrompt);
            StackPane.setAlignment(usernamePrompt, Pos.CENTER_LEFT);
            
            usernameField.textProperty().addListener((obs, oldText, newText) -> {
                usernamePrompt.setVisible(newText == null || newText.isEmpty());
            });
            
            // Username error label
            Label usernameErrorLabel = new Label();
            usernameErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
            usernameErrorLabel.setVisible(false);
            usernameErrorLabel.setWrapText(true);
            usernameErrorLabel.setPrefWidth(300);
            usernameErrorLabel.setMaxWidth(300);
            
            // Real-time validation for username
            usernameField.textProperty().addListener((obs, oldText, newText) -> {
                DataValidator.ValidationResult result = DataValidator.validateUsername(newText);
                DataValidator.applyValidationStyle(usernameField, result.isValid(), usernameErrorLabel, result.getErrorMessage());
            });

            // Password field with visible placeholder
            PasswordField passwordField = new PasswordField();
            passwordField.setPrefWidth(300);
            passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
            
            Label passwordPrompt = new Label("Κωδικός πρόσβασης");
            passwordPrompt.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af;");
            passwordPrompt.setMouseTransparent(true);
            passwordPrompt.setPadding(new Insets(0, 0, 0, 10));
            
            StackPane passwordPane = new StackPane();
            passwordPane.setPrefWidth(300);
            passwordPane.getChildren().addAll(passwordField, passwordPrompt);
            StackPane.setAlignment(passwordPrompt, Pos.CENTER_LEFT);
            
            passwordField.textProperty().addListener((obs, oldText, newText) -> {
                passwordPrompt.setVisible(newText == null || newText.isEmpty());
            });
            
            // Password error label
            Label passwordErrorLabel = new Label();
            passwordErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
            passwordErrorLabel.setVisible(false);
            passwordErrorLabel.setWrapText(true);
            passwordErrorLabel.setPrefWidth(300);
            passwordErrorLabel.setMaxWidth(300);
            
            // Real-time validation for password
            passwordField.textProperty().addListener((obs, oldText, newText) -> {
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
                
                // Validate username and password
                DataValidator.ValidationResult usernameResult = DataValidator.validateUsername(username);
                DataValidator.ValidationResult passwordResult = DataValidator.validatePassword(password);
                
                // Apply validation styles
                DataValidator.applyValidationStyle(usernameField, usernameResult.isValid(), usernameErrorLabel, usernameResult.getErrorMessage());
                DataValidator.applyValidationStyle(passwordField, passwordResult.isValid(), passwordErrorLabel, passwordResult.getErrorMessage());
                
                // Check if all validations pass
                if (usernameResult.isValid() && passwordResult.isValid()) {
                    // Set user type to government
                    HomeController.setUserType(HomeController.UserType.GOVERNMENT);
                    
                    // Close login dialog
                    loginStage.close();
                    // Navigate to main application
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Home.fxml"));
                        Parent root = loader.load();
                        Scene scene = new Scene(root, 1400, 900);
                        scene.getStylesheets().add(getClass().getResource("/ui/styles.css").toExternalForm());
                        
                        Stage mainStage = (Stage) governmentCard.getScene().getWindow();
                        mainStage.setScene(scene);
                        mainStage.setTitle("Κρατικός Προϋπολογισμός - Σύστημα Ανάλυσης");
                        mainStage.show();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showError("Σφάλμα", "Δεν ήταν δυνατή η πρόσβαση στην εφαρμογή: " + ex.getMessage());
                    }
                }
            });

            loginPane.getChildren().addAll(titleLabel, usernamePane, usernameErrorLabel, passwordPane, passwordErrorLabel, loginButton);

            Scene loginScene = new Scene(loginPane, 400, 400);
            loginStage.setScene(loginScene);
            loginStage.initOwner(governmentCard.getScene().getWindow());
            loginStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Σφάλμα", "Δεν ήταν δυνατή η προβολή της σελίδας σύνδεσης.");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

