package ui;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Service class for interacting with AI assistant APIs (OpenAI, Anthropic, etc.).
 * Handles API communication and response processing.
 */
public class AIAssistantService {
    
    private static final String DEFAULT_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
    private static final int DEFAULT_TIMEOUT = 60; // seconds
    
    private final OkHttpClient client;
    private String apiKey;
    private String apiUrl;
    private String model;
    
    /**
     * Creates a new AIAssistantService with default settings.
     * API key must be set via setApiKey() before use.
     */
    public AIAssistantService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        this.apiUrl = DEFAULT_API_URL;
        this.model = DEFAULT_MODEL;
    }
    
    /**
     * Sets the API key for authentication.
     * 
     * @param apiKey The API key to use
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    /**
     * Sets a custom API URL (for different providers or local servers).
     * 
     * @param apiUrl The API URL to use
     */
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    
    /**
     * Sets the model to use for completions.
     * 
     * @param model The model name (e.g., "gpt-3.5-turbo", "gpt-4")
     */
    public void setModel(String model) {
        this.model = model;
    }
    
    /**
     * Sends a message to the AI assistant and returns the response.
     * 
     * @param userMessage The user's message
     * @param conversationHistory Previous messages in the conversation (optional)
     * @return The AI assistant's response
     * @throws IOException If there's an error communicating with the API
     * @throws IllegalArgumentException If the API key is not set
     */
    public String sendMessage(String userMessage, JSONArray conversationHistory) throws IOException {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key is not set. Please configure your AI API key.");
        }
        
        // Build system prompt for budget analysis context
        String systemPrompt = "Είσαι ένας έμπειρος βοηθός για ανάλυση του ελληνικού κρατικού προϋπολογισμού. " +
                "Βοηθάς χρήστες να κατανοήσουν τα δεδομένα του προϋπολογισμού, " +
                "να αναλύσουν τάσεις και να κάνουν ερωτήσεις σχετικά με έσοδα, δαπάνες, " +
                "υπουργεία και άλλα θέματα προϋπολογισμού. " +
                "Απαντάς στα Ελληνικά και παρέχεις σαφείς, ακριβείς απαντήσεις.";
        
        // Build messages array
        JSONArray messages = new JSONArray();
        
        // Add system message
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.put(systemMessage);
        
        // Add conversation history if provided
        if (conversationHistory != null) {
            for (int i = 0; i < conversationHistory.length(); i++) {
                messages.put(conversationHistory.getJSONObject(i));
            }
        }
        
        // Add user message
        JSONObject userMessageObj = new JSONObject();
        userMessageObj.put("role", "user");
        userMessageObj.put("content", userMessage);
        messages.put(userMessageObj);
        
        // Build request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1000);
        
        // Create HTTP request
        RequestBody body = RequestBody.create(
                requestBody.toString(),
                MediaType.parse("application/json")
        );
        
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();
        
        // Execute request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("API request failed: " + response.code() + " - " + errorBody);
            }
            
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            
            // Extract assistant's message from response
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() == 0) {
                throw new IOException("No response from AI assistant");
            }
            
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            return message.getString("content");
        }
    }
    
    /**
     * Sends a message without conversation history (for single-turn conversations).
     * 
     * @param userMessage The user's message
     * @return The AI assistant's response
     * @throws IOException If there's an error communicating with the API
     */
    public String sendMessage(String userMessage) throws IOException {
        return sendMessage(userMessage, null);
    }
    
    /**
     * Checks if the API key is configured.
     * 
     * @return true if API key is set, false otherwise
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}
