package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AIAssistantServiceTest {

    @Test
    void testConstructor() {
        AIAssistantService service = new AIAssistantService();
        assertNotNull(service);
        assertFalse(service.isConfigured());
    }

    @Test
    void testIsConfiguredFalseWhenNoApiKey() {
        AIAssistantService service = new AIAssistantService();
        assertFalse(service.isConfigured());
    }

    @Test
    void testIsConfiguredFalseWhenApiKeyIsNull() {
        AIAssistantService service = new AIAssistantService();
        service.setApiKey(null);
        assertFalse(service.isConfigured());
    }

    @Test
    void testIsConfiguredFalseWhenApiKeyIsEmpty() {
        AIAssistantService service = new AIAssistantService();
        service.setApiKey("");
        assertFalse(service.isConfigured());
    }

    @Test
    void testIsConfiguredFalseWhenApiKeyIsWhitespace() {
        AIAssistantService service = new AIAssistantService();
        service.setApiKey("   ");
        assertFalse(service.isConfigured());
    }

    @Test
    void testIsConfiguredTrueWhenApiKeyIsSet() {
        AIAssistantService service = new AIAssistantService();
        service.setApiKey("test-api-key");
        assertTrue(service.isConfigured());
    }

    @Test
    void testSetApiKey() {
        AIAssistantService service = new AIAssistantService();
        assertFalse(service.isConfigured());
        service.setApiKey("my-api-key-123");
        assertTrue(service.isConfigured());
    }

    @Test
    void testSetApiUrl() {
        AIAssistantService service = new AIAssistantService();
        String customUrl = "https://custom-api.example.com/v1/chat";
        service.setApiUrl(customUrl);
        assertNotNull(service);
    }

    @Test
    void testSetModel() {
        AIAssistantService service = new AIAssistantService();
        String customModel = "gpt-4";
        service.setModel(customModel);
        assertNotNull(service);
    }

    @Test
    void testSendMessageThrowsExceptionWhenApiKeyNotSet() {
        AIAssistantService service = new AIAssistantService();
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.sendMessage("test message")
        );
        assertTrue(exception.getMessage().contains("API key is not set"));
    }

    @Test
    void testSendMessageThrowsExceptionWhenApiKeyIsEmpty() {
        AIAssistantService service = new AIAssistantService();
        service.setApiKey("");
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.sendMessage("test message")
        );
        assertTrue(exception.getMessage().contains("API key is not set"));
    }

    @Test
    void testSendMessageThrowsExceptionWhenApiKeyIsWhitespace() {
        AIAssistantService service = new AIAssistantService();
        service.setApiKey("   ");
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> service.sendMessage("test message")
        );
        assertTrue(exception.getMessage().contains("API key is not set"));
    }

    @Test
    void testSendMessageWithNullConversationHistory() {
        AIAssistantService service = new AIAssistantService();
        service.setApiKey("test-key");
        assertThrows(
            java.io.IOException.class,
            () -> service.sendMessage("test", null)
        );
    }

    @Test
    void testSendMessageWithConversationHistory() {
        AIAssistantService service = new AIAssistantService();
        service.setApiKey("test-key");
        org.json.JSONArray history = new org.json.JSONArray();
        org.json.JSONObject msg = new org.json.JSONObject();
        msg.put("role", "user");
        msg.put("content", "previous message");
        history.put(msg);
        
        assertThrows(
            java.io.IOException.class,
            () -> service.sendMessage("test", history)
        );
    }
}
