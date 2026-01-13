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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Controller for the AI Assistant chat interface.
 * Manages the chat UI, message sending, and API communication.
 */
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
        // Initialize preferences
        preferences = Preferences.userNodeForPackage(AIAssistantController.class);
        
        // Initialize AI service
        aiService = new AIAssistantService();
        
        // Try to load API key from config file first (for easy setup, not in git)
        String apiKey = loadApiKeyFromFile();
        
        // If not in file, try environment variable
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("OPENAI_API_KEY");
        }
        
        // If still not set, try saved preferences (user-entered key)
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = preferences.get(API_KEY_PREF, "");
        }
        
        // If we have a key, use it
        if (apiKey != null && !apiKey.isEmpty()) {
            aiService.setApiKey(apiKey);
        }
        
        // Always hide the configuration alert by default
        // It will only show when user tries to send a message without API key
        showConfigurationAlert(false);
        
        // Initialize conversation history
        conversationHistory = new JSONArray();
        
        // Add welcome message if configured
        if (aiService.isConfigured()) {
            addWelcomeMessage();
        }
    }
    
    /**
     * Shows or hides the configuration alert.
     * The UI remains enabled - users can type and see the interface.
     * Alert only appears when they try to send a message without API key.
     */
    private void showConfigurationAlert(boolean show) {
        configAlertContainer.setVisible(show);
        configAlertContainer.setManaged(show);
        
        // Keep UI enabled - only disable when showing the alert overlay
        // This allows users to see and interact with the interface
        sendButton.setDisable(show);
        messageInputField.setDisable(show);
    }
    
    /**
     * Adds a welcome message to the chat.
     */
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
    
    /**
     * Handles the send message button click.
     */
    @FXML
    private void onSendMessage() {
        String message = messageInputField.getText().trim();
        if (message.isEmpty() || !aiService.isConfigured()) {
            return;
        }
        
        // Add user message to UI
        addMessage("user", message);
        
        // Clear input field
        messageInputField.clear();
        sendButton.setDisable(true);
        messageInputField.setDisable(true);
        
        // Add user message to conversation history
        JSONObject userMessageObj = new JSONObject();
        userMessageObj.put("role", "user");
        userMessageObj.put("content", message);
        conversationHistory.put(userMessageObj);
        
        // Show loading indicator
        addLoadingIndicator();
        
        // Send message asynchronously
        new Thread(() -> {
            try {
                // Get response from AI
                String response = aiService.sendMessage(message, conversationHistory);
                
                // Add assistant message to conversation history
                JSONObject assistantMessageObj = new JSONObject();
                assistantMessageObj.put("role", "assistant");
                assistantMessageObj.put("content", response);
                conversationHistory.put(assistantMessageObj);
                
                // Update UI on JavaFX thread
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
                    // Show alert only when user tries to use AI without key
                    showConfigurationAlert(true);
                    addErrorMessage("Το API Key δεν είναι ρυθμισμένο. Παρακαλώ ρυθμίστε το API Key σας.");
                });
            }
        }).start();
    }
    
    /**
     * Adds a message bubble to the chat.
     */
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
            
            // Scroll to bottom
            scrollToBottom();
        });
    }
    
    /**
     * Adds a loading indicator while waiting for AI response.
     */
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
    
    /**
     * Removes the loading indicator.
     */
    private void removeLoadingIndicator() {
        Platform.runLater(() -> {
            chatMessagesContainer.getChildren().removeIf(node -> 
                node.getId() != null && node.getId().equals("loading-indicator")
            );
        });
    }
    
    /**
     * Adds an error message to the chat.
     */
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
    
    /**
     * Scrolls the chat to the bottom.
     */
    private void scrollToBottom() {
        Platform.runLater(() -> {
            chatScrollPane.setVvalue(1.0);
        });
    }
    
    /**
     * Handles the save API key button click.
     */
    @FXML
    private void onSaveApiKey() {
        String apiKey = apiKeyField.getText().trim();
        if (apiKey.isEmpty()) {
            showAlert("Σφάλμα", "Το API Key δεν μπορεί να είναι κενό.", Alert.AlertType.ERROR);
            return;
        }
        
        // Save API key
        preferences.put(API_KEY_PREF, apiKey);
        aiService.setApiKey(apiKey);
        
        // Hide configuration alert
        showConfigurationAlert(false);
        
        // Clear chat and show welcome message
        chatMessagesContainer.getChildren().clear();
        conversationHistory = new JSONArray();
        addWelcomeMessage();
        
        showAlert("Επιτυχία", "Το API Key αποθηκεύτηκε επιτυχώς!", Alert.AlertType.INFORMATION);
    }
    
    /**
     * Handles the settings button click.
     */
    @FXML
    private void onSettingsClicked() {
        showConfigurationAlert(true);
        String currentApiKey = preferences.get(API_KEY_PREF, "");
        apiKeyField.setText(currentApiKey);
    }
    
    /**
     * Handles the info button click.
     */
    @FXML
    private void onInfoClicked() {
        String infoMessage = "Το AI Βοηθός χρησιμοποιεί το OpenAI API (διαφορετικό από το ChatGPT Premium).\n\n" +
                "⚠️ ΣΗΜΑΝΤΙΚΟ: ChatGPT Premium/Plus (για chat.openai.com) ≠ OpenAI API\n" +
                "Αυτά είναι δύο διαφορετικές υπηρεσίες!\n\n" +
                "Για να αποκτήσετε API Key:\n" +
                "1. Επισκεφτείτε το https://platform.openai.com/\n" +
                "2. Συνδεθείτε με τον ίδιο λογαριασμό OpenAI (αν έχετε)\n" +
                "3. Μεταβείτε στις API Keys → Create new secret key\n" +
                "4. Αντιγράψτε το κλειδί (ξεκινάει με sk-...)\n" +
                "5. Εισάγετε το κλειδί στο πεδίο παραπάνω\n\n" +
                "💰 Κόστος:\n" +
                "• Αν έχετε νέο λογαριασμό: δωρεάν πίστωση $5-18\n" +
                "• Μετά: ~$0.002 ανά 1000 tokens (πολύ φθηνό για demos)\n" +
                "• Μπορείτε να ορίσετε spending limit $0 για να αποφύγετε χρεώσεις\n" +
                "• Η λειτουργία είναι προαιρετική!\n\n" +
                "ℹ️ Pricing: https://openai.com/api/pricing/";
        
        showAlert("Πληροφορίες - AI Βοηθός", infoMessage, Alert.AlertType.INFORMATION);
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
     * Loads API key from config file (api-key.txt in project root).
     * This file is gitignored so the key won't be visible in the repository.
     * Tries multiple locations to handle different execution scenarios (Maven, IDE, etc.).
     */
    private String loadApiKeyFromFile() {
        String userDir = System.getProperty("user.dir");
        
        // Try multiple possible locations
        String[] pathsToTry = {
            // 1. Current working directory
            userDir + File.separator + "api-key.txt",
            // 2. Project root (look for pom.xml)
            findProjectRootWithPom(),
            // 3. Parent directory (in case running from a subdirectory)
            userDir + File.separator + ".." + File.separator + "api-key.txt"
        };
        
        for (String path : pathsToTry) {
            if (path == null) continue;
            try {
                File apiKeyFile = new File(path).getCanonicalFile();
                if (apiKeyFile.exists() && apiKeyFile.isFile()) {
                    long fileSize = apiKeyFile.length();
                    
                    // Check if file is empty (likely OneDrive Files On-Demand not downloaded)
                    if (fileSize == 0) {
                        continue; // Try next path or skip
                    }
                    
                    // Try multiple reading methods for compatibility
                    String key = null;
                    
                    // Method 1: Try BufferedReader with FileInputStream (like other code in project)
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(new FileInputStream(apiKeyFile), StandardCharsets.UTF_8))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (!line.trim().isEmpty()) { // Skip empty lines
                                sb.append(line.trim());
                            }
                        }
                        key = sb.toString().trim();
                    } catch (Exception e1) {
                        // Fallback to readAllBytes
                        try {
                            byte[] fileBytes = Files.readAllBytes(apiKeyFile.toPath());
                            key = new String(fileBytes, StandardCharsets.UTF_8).trim();
                        } catch (Exception e2) {
                            // Continue to next path
                        }
                    }
                    
                    if (key != null && !key.isEmpty() && key.startsWith("sk-")) {
                        return key;
                    }
                }
            } catch (Exception e) {
                // Try next path
                continue;
            }
        }
        
        return null;
    }
    
    /**
     * Attempts to find project root by looking for pom.xml and returns path to api-key.txt.
     */
    private String findProjectRootWithPom() {
        try {
            File currentDir = new File(System.getProperty("user.dir"));
            File dir = currentDir.getCanonicalFile();
            
            // Search up the directory tree for pom.xml
            for (int i = 0; i < 5; i++) {
                File pomFile = new File(dir, "pom.xml");
                if (pomFile.exists()) {
                    return dir.getAbsolutePath() + File.separator + "api-key.txt";
                }
                File parent = dir.getParentFile();
                if (parent == null) break;
                dir = parent;
            }
        } catch (Exception e) {
            // Could not determine
        }
        return null;
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
