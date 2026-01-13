package ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

// κλάση για το chat του AI βοηθού

public class AIAssistantController implements Initializable {
    
    @FXML
    private VBox chatMessagesContainer;
    
    @FXML
    private ScrollPane chatScrollPane;
    
    @FXML
    private TextField messageInputField;
    
    @FXML
    private Button sendButton;
    
    @FXML
    private VBox configAlertContainer;
    
    @FXML
    private TextField apiKeyField;
    
    private AIAssistantService aiService;
    private JSONArray conversationHistory;
    private Preferences preferences;
    
    private static final String API_KEY_PREF = "ai_assistant_api_key";
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preferences = Preferences.userNodeForPackage(AIAssistantController.class);
        aiService = new AIAssistantService();
        
        // το πεδίο εισαγωγής API key είναι αρχικά κρυμμένο 
        showConfigurationAlert(false);
        
        conversationHistory = new JSONArray();
        
        // ελέγχει αν ο χρήστης έχει αποθηκεύσει API key και το φορτώνει
        String savedApiKey = preferences.get(API_KEY_PREF, "");
        if (!savedApiKey.isEmpty()) {
            aiService.setApiKey(savedApiKey);
            addWelcomeMessage();
        }
    }
    
    // εμφανίζει ή κρύβει το πεδίο εισαγωγής API key
    private void showConfigurationAlert(boolean show) {
        configAlertContainer.setVisible(show);
        configAlertContainer.setManaged(show);
        sendButton.setDisable(show);
        messageInputField.setDisable(show);
    }
    
    // welcome message
    private void addWelcomeMessage() {
        String welcomeMessage = "Γεια σας! Είμαι ο AI Βοηθός για τον ελληνικό κρατικό προϋπολογισμό. " +
                "Μπορώ να σας βοηθήσω με ερωτήσεις σχετικά με:\n\n" +
                "• Έσοδα και δαπάνες\n" +
                "• Ανάλυση υπουργείων\n" +
                "• Τάσεις και μεταβολές\n" +
                "• Συγκρίσεις ετών\n" +
                "• Και πολλά άλλα!\n\n" +
                "Πώς μπορώ να σας βοηθήσω σήμερα;";
        
        addMessage("assistant", welcomeMessage);
    }
    
    //send message button 
    
    @FXML
    private void onSendMessage() {
        String message = messageInputField.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        
        if (!aiService.isConfigured()) {
            String apiKey = preferences.get(API_KEY_PREF, "");
            if (apiKey.isEmpty()) {
                showConfigurationAlert(true);
                return;
            } else {
                aiService.setApiKey(apiKey);
            }
        }
        
        // προσθέτει το μήνυμα του χρήστη στο UI
        addMessage("user", message);
        
        messageInputField.clear();
        sendButton.setDisable(true);
        messageInputField.setDisable(true);
        
        // προσθέτει το μήνυμα του χρήστη στην ιστορία συνομιλίας
        JSONObject userMessageObj = new JSONObject();
        userMessageObj.put("role", "user");
        userMessageObj.put("content", message);
        conversationHistory.put(userMessageObj);
        
        addLoadingIndicator();
        
        // στέλνει το μήνυμα ασύγχρονα
        new Thread(() -> {
            try {
                // λαμβάνει την απάντηση από το AI
                String response = aiService.sendMessage(message, conversationHistory);
                
                // προσθέτει το μήνυμα του AI στην ιστορία συνομιλίας
                JSONObject assistantMessageObj = new JSONObject();
                assistantMessageObj.put("role", "assistant");
                assistantMessageObj.put("content", response);
                conversationHistory.put(assistantMessageObj);
                
                Platform.runLater(() -> {
                    removeLoadingIndicator();
                    addMessage("assistant", response);
                    sendButton.setDisable(false);
                    messageInputField.setDisable(false);
                    messageInputField.requestFocus();
                });
                
            } catch (IOException e) {
                Platform.runLater(() -> {
                    removeLoadingIndicator();
                    addErrorMessage("Σφάλμα κατά την επικοινωνία με το AI: " + e.getMessage());
                    sendButton.setDisable(false);
                    messageInputField.setDisable(false);
                });
            } catch (IllegalArgumentException e) {
                Platform.runLater(() -> {
                    removeLoadingIndicator();
                    // εμφανίζει προειδοποίηση όταν το API key είναι άκυρο ή λείπει
                    showConfigurationAlert(true);
                    addErrorMessage("Το API Key δεν είναι ρυθμισμένο. Παρακαλώ ρυθμίστε το API Key σας.");
                });
            }
        }).start();
    }
    
    // προσθέτει ένα μήνυμα στο chat
    private void addMessage(String role, String content) {
        Platform.runLater(() -> {
            HBox messageContainer = new HBox(12);
            messageContainer.setAlignment(role.equals("user") ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            messageContainer.setMaxWidth(Double.MAX_VALUE);
            
            Label messageLabel = new Label(content);
            messageLabel.setWrapText(true);
            messageLabel.setMaxWidth(600);
            messageLabel.setPadding(new Insets(12, 16, 12, 16));
            messageLabel.setFont(Font.font(14));
            
            if (role.equals("user")) {
                messageLabel.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; -fx-background-radius: 18;");
                HBox.setHgrow(messageContainer, Priority.ALWAYS);
            } else {
                messageLabel.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #1e293b; -fx-background-radius: 18;");
                HBox.setHgrow(messageContainer, Priority.ALWAYS);
            }
            
            messageContainer.getChildren().add(messageLabel);
            chatMessagesContainer.getChildren().add(messageContainer);
            
            scrollToBottom();
        });
    }
    
    // δείχνει σήμα φόρτωσης όσο περιμένουμε απάντηση από το AI
    private void addLoadingIndicator() {
        Platform.runLater(() -> {
            HBox loadingContainer = new HBox(12);
            loadingContainer.setAlignment(Pos.CENTER_LEFT);
            
            Label loadingLabel = new Label("🤔 Αναμονή απάντησης...");
            loadingLabel.setPadding(new Insets(12, 16, 12, 16));
            loadingLabel.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-background-radius: 18;");
            loadingLabel.setFont(Font.font(14));
            
            loadingContainer.getChildren().add(loadingLabel);
            loadingContainer.setId("loading-indicator");
            chatMessagesContainer.getChildren().add(loadingContainer);
            
            scrollToBottom();
        });
    }
    
    //αφαιρεί το σήμα φόρτωσης
    private void removeLoadingIndicator() {
        Platform.runLater(() -> {
            chatMessagesContainer.getChildren().removeIf(node -> 
                node.getId() != null && node.getId().equals("loading-indicator")
            );
        });
    }
    
    // προσθέτει error message
    private void addErrorMessage(String errorMessage) {
        Platform.runLater(() -> {
            HBox errorContainer = new HBox(12);
            errorContainer.setAlignment(Pos.CENTER_LEFT);
            
            Label errorLabel = new Label("⚠️ " + errorMessage);
            errorLabel.setWrapText(true);
            errorLabel.setMaxWidth(600);
            errorLabel.setPadding(new Insets(12, 16, 12, 16));
            errorLabel.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-background-radius: 18; -fx-border-color: #fca5a5; -fx-border-width: 1; -fx-border-radius: 18;");
            errorLabel.setFont(Font.font(14));
            
            errorContainer.getChildren().add(errorLabel);
            chatMessagesContainer.getChildren().add(errorContainer);
            
            scrollToBottom();
        });
    }
    
    // σκρολάρει κάτω
    private void scrollToBottom() {
        Platform.runLater(() -> {
            chatScrollPane.setVvalue(1.0);
        });
    }
    
    // αποθηκεύει το API key
    @FXML
    private void onSaveApiKey() {
        String apiKey = apiKeyField.getText().trim();
        if (apiKey.isEmpty()) {
            showAlert("Σφάλμα", "Το API Key δεν μπορεί να είναι κενό.", Alert.AlertType.ERROR);
            return;
        }
        
        preferences.put(API_KEY_PREF, apiKey);
        aiService.setApiKey(apiKey);
        
        showConfigurationAlert(false);
        
        chatMessagesContainer.getChildren().clear();
        conversationHistory = new JSONArray();
        addWelcomeMessage();
        
        showAlert("Επιτυχία", "Το API Key αποθηκεύτηκε επιτυχώς!", Alert.AlertType.INFORMATION);
    }
    
    // settings button
    @FXML
    private void onSettingsClicked() {
        showConfigurationAlert(true);
        String currentApiKey = preferences.get(API_KEY_PREF, "");
        apiKeyField.setText(currentApiKey);
    }
    
    
    /**
     * Handles the close button click.
     */
    @FXML
    private void onCloseClicked() {
        Stage stage = (Stage) chatScrollPane.getScene().getWindow();
        stage.close();
    }
    
    
    /**
     * Shows an alert dialog.
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
