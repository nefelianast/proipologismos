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

public class Login {

    private Authentication authentication = new Authentication();

    @FXML
    private VBox citizenCard;
    @FXML
    private VBox governmentCard;
    @FXML
    private ImageView logoImageView;

    @FXML
    private void initialize() {
        // style στο logo
        if (logoImageView != null) {
            logoImageView.getStyleClass().add("login-logo");
        }
        
        // click handlers
        citizenCard.setOnMouseClicked(e -> onCitizenSelected());
        citizenCard.setCursor(javafx.scene.Cursor.HAND);
        
        governmentCard.setOnMouseClicked(e -> onGovernmentSelected());
        governmentCard.setCursor(javafx.scene.Cursor.HAND);
    }

    @FXML
    private void onCitizenSelected() {
        // ορισμός τύπου χρήστη σε πολίτης
        HomeController.setUserType(HomeController.UserType.CITIZEN);
        
        // μετάβαση στην κύρια οθόνη
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
        // εμφάνιση login dialog για κυβέρνηση
        showGovernmentLogin();
    }

    private void showGovernmentLogin() {
        try {
            // δημιουργία login dialog
            Stage loginStage = new Stage();
            loginStage.setTitle("Σύνδεση - Κυβέρνηση");
            loginStage.setResizable(false);

            VBox loginPane = new VBox(20);
            loginPane.setPadding(new Insets(40));
            loginPane.setStyle("-fx-background-color: white;");

            Label titleLabel = new Label("Σύνδεση Κυβέρνησης");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e40af;");

            // πεδίο username
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
            
            // label για σφάλματα username
            Label usernameErrorLabel = new Label();
            usernameErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
            usernameErrorLabel.setVisible(false);
            usernameErrorLabel.setWrapText(true);
            usernameErrorLabel.setPrefWidth(300);
            usernameErrorLabel.setMaxWidth(300);
            
            // username validation
            usernameField.textProperty().addListener((obs, oldText, newText) -> {
                Constraints.ValidationResult result = Constraints.validateUsername(newText);
                Constraints.applyValidationStyle(usernameField, result.isValid(), usernameErrorLabel, result.getErrorMessage());
            });

            // πεδίο password 
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
            
            // label για σφάλματα password
            Label passwordErrorLabel = new Label();
            passwordErrorLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
            passwordErrorLabel.setVisible(false);
            passwordErrorLabel.setWrapText(true);
            passwordErrorLabel.setPrefWidth(300);
            passwordErrorLabel.setMaxWidth(300);
            
            // password validation
            passwordField.textProperty().addListener((obs, oldText, newText) -> {
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
                
                // username & password validation
                Constraints.ValidationResult usernameResult = Constraints.validateUsername(username);
                Constraints.ValidationResult passwordResult = Constraints.validatePassword(password);
                
                // validation styles
                Constraints.applyValidationStyle(usernameField, usernameResult.isValid(), usernameErrorLabel, usernameResult.getErrorMessage());
                Constraints.applyValidationStyle(passwordField, passwordResult.isValid(), passwordErrorLabel, passwordResult.getErrorMessage());
                
                // validation check
                if (usernameResult.isValid() && passwordResult.isValid()) {
                    // login check
                    if (authentication.checkLogin(username, password)) {
                        // user type set
                        HomeController.setUserType(HomeController.UserType.GOVERNMENT);
                        
                        // κλείσιμο login dialog
                        loginStage.close();
                        // μετάβαση στην κύρια οθόνη
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
                    } else {
                        // μη έγκυρα διαπιστευτήρια
                        showError("Σφάλμα Σύνδεσης", "Λάθος όνομα χρήστη ή κωδικός πρόσβασης.\nΠαρακαλώ προσπαθήστε ξανά.");
                    }
                }
            });

            // κουμπί εγγραφής
            javafx.scene.control.Button signUpButton = new javafx.scene.control.Button("Εγγραφή");
            signUpButton.setPrefWidth(300);
            signUpButton.setPrefHeight(40);
            signUpButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            signUpButton.setOnAction(e -> {
                showSignUpDialog(loginStage);
            });

            loginPane.getChildren().addAll(titleLabel, usernamePane, usernameErrorLabel, passwordPane, passwordErrorLabel, loginButton, signUpButton);

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

    private void showSignUpDialog(Stage parentStage) {
        try {
            Stage signUpStage = new Stage();
            signUpStage.setTitle("Εγγραφή - Κυβέρνηση");
            signUpStage.setResizable(false);

            VBox signUpPane = new VBox(20);
            signUpPane.setPadding(new Insets(40));
            signUpPane.setStyle("-fx-background-color: white;");

            Label titleLabel = new Label("Εγγραφή Νέου Χρήστη");
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #10b981;");

            // πεδίο username
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

            // πεδίο password
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

            // κουμπί δημιουργίας λογαριασμού
            javafx.scene.control.Button createButton = new javafx.scene.control.Button("Δημιουργία Λογαριασμού");
            createButton.setPrefWidth(300);
            createButton.setPrefHeight(40);
            createButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-cursor: hand;");
            createButton.setOnAction(e -> {
                String username = usernameField.getText();
                String password = passwordField.getText();

                // επικύρωση
                Constraints.ValidationResult usernameResult = Constraints.validateUsername(username);
                Constraints.ValidationResult passwordResult = Constraints.validatePassword(password);

                Constraints.applyValidationStyle(usernameField, usernameResult.isValid(), usernameErrorLabel, usernameResult.getErrorMessage());
                Constraints.applyValidationStyle(passwordField, passwordResult.isValid(), passwordErrorLabel, passwordResult.getErrorMessage());

                if (usernameResult.isValid() && passwordResult.isValid()) {
                    // έλεγχος αν το username υπάρχει ήδη
                    if (authentication.usernameExists(username)) {
                        usernameErrorLabel.setText("Το όνομα χρήστη υπάρχει ήδη");
                        usernameErrorLabel.setVisible(true);
                        usernameField.setStyle("-fx-border-color: #dc2626; -fx-font-size: 14px; -fx-padding: 10;");
                    } else {
                        // δημιουργία χρήστη
                        if (authentication.saveUser(username, password)) {
                            Alert success = new Alert(Alert.AlertType.INFORMATION);
                            success.setTitle("Επιτυχία");
                            success.setHeaderText(null);
                            success.setContentText("Ο λογαριασμός δημιουργήθηκε επιτυχώς!\nΚάντε login για να συνεχίσετε.");
                            success.showAndWait();
                            signUpStage.close();
                        } else {
                            showError("Σφάλμα", "Δεν ήταν δυνατή η δημιουργία του λογαριασμού.\nΠαρακαλώ προσπαθήστε ξανά.");
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
            showError("Σφάλμα", "Δεν ήταν δυνατή η προβολή της σελίδας εγγραφής.");
        }
    }

    // εμφάνιση μηνύματος σφάλματος
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
