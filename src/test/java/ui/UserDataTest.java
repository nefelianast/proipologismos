package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for UserData class.
 */
class UserDataTest {
    
    private UserData service;
    
    @BeforeEach
    void setUp() {
        service = UserData.getInstance();
        // Clean up test data before each test
        cleanupTestData();
    }
    
    /**
     * Clean up test data from database
     */
    private void cleanupTestData() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement()) {
            
            // Delete test comments
            stmt.execute("DELETE FROM user_comments WHERE category_name LIKE 'TEST_%'");
            
            // Delete test scenarios
            stmt.execute("DELETE FROM saved_scenarios WHERE scenario_name LIKE 'TEST_%'");
            
            // Delete test preferences
            stmt.execute("DELETE FROM user_preferences WHERE preference_key LIKE 'TEST_%'");
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    @Test
    void testGetInstance() {
        // Test singleton pattern
        UserData instance1 = UserData.getInstance();
        UserData instance2 = UserData.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }
    
    // Comment tests
    @Test
    void testSaveComment() {
        boolean result = service.saveComment("TEST_Category", 2025, "Test comment");
        assertTrue(result);
    }
    
    @Test
    void testGetComment() {
        // Save a comment first
        service.saveComment("TEST_Category", 2025, "Test comment text");
        
        // Retrieve it
        String comment = service.getComment("TEST_Category", 2025);
        assertNotNull(comment);
        assertEquals("Test comment text", comment);
    }
    
    @Test
    void testGetCommentNonExistent() {
        String comment = service.getComment("NON_EXISTENT", 2025);
        assertNull(comment);
    }
    
    @Test
    void testUpdateComment() {
        // Save initial comment
        service.saveComment("TEST_Category", 2025, "Initial comment");
        
        // Update it
        boolean result = service.saveComment("TEST_Category", 2025, "Updated comment");
        assertTrue(result);
        
        // Verify update
        String comment = service.getComment("TEST_Category", 2025);
        assertEquals("Updated comment", comment);
    }
    
    @Test
    void testDeleteComment() {
        // Save a comment
        service.saveComment("TEST_Category", 2025, "Comment to delete");
        
        // Delete it
        boolean result = service.deleteComment("TEST_Category", 2025);
        assertTrue(result);
        
        // Verify deletion
        String comment = service.getComment("TEST_Category", 2025);
        assertNull(comment);
    }
    
    @Test
    void testDeleteCommentNonExistent() {
        boolean result = service.deleteComment("NON_EXISTENT", 2025);
        assertFalse(result);
    }
    
    @Test
    void testGetAllCommentsForYear() {
        // Save multiple comments for the same year
        service.saveComment("TEST_Category1", 2025, "Comment 1");
        service.saveComment("TEST_Category2", 2025, "Comment 2");
        service.saveComment("TEST_Category3", 2024, "Comment 3"); // Different year
        
        Map<String, String> comments = service.getAllCommentsForYear(2025);
        assertNotNull(comments);
        assertEquals(2, comments.size());
        assertTrue(comments.containsKey("TEST_Category1"));
        assertTrue(comments.containsKey("TEST_Category2"));
        assertFalse(comments.containsKey("TEST_Category3"));
    }
    
    @Test
    void testGetAllCommentsForYearEmpty() {
        Map<String, String> comments = service.getAllCommentsForYear(2099);
        assertNotNull(comments);
        assertTrue(comments.isEmpty());
    }
    
    // Scenario tests
    @Test
    void testSaveScenario() {
        String scenarioData = "{\"year\":2025,\"totalRevenues\":100.0}";
        boolean result = service.saveScenario("TEST_Scenario", "Test description", 2025, scenarioData);
        assertTrue(result);
    }
    
    @Test
    void testGetScenario() {
        String scenarioData = "{\"year\":2025,\"totalRevenues\":100.0}";
        service.saveScenario("TEST_Scenario", "Test description", 2025, scenarioData);
        
        UserData.SavedScenario scenario = service.getScenario("TEST_Scenario");
        assertNotNull(scenario);
        assertEquals("TEST_Scenario", scenario.getScenarioName());
        assertEquals("Test description", scenario.getDescription());
        assertEquals(2025, scenario.getYear());
        assertEquals(scenarioData, scenario.getScenarioData());
    }
    
    @Test
    void testGetScenarioNonExistent() {
        UserData.SavedScenario scenario = service.getScenario("NON_EXISTENT");
        assertNull(scenario);
    }
    
    @Test
    void testUpdateScenario() {
        String initialData = "{\"year\":2025,\"totalRevenues\":100.0}";
        service.saveScenario("TEST_Scenario", "Initial description", 2025, initialData);
        
        String updatedData = "{\"year\":2025,\"totalRevenues\":150.0}";
        boolean result = service.updateScenario("TEST_Scenario", "Updated description", updatedData);
        assertTrue(result);
        
        UserData.SavedScenario scenario = service.getScenario("TEST_Scenario");
        assertEquals("Updated description", scenario.getDescription());
        assertEquals(updatedData, scenario.getScenarioData());
    }
    
    @Test
    void testUpdateScenarioNonExistent() {
        boolean result = service.updateScenario("NON_EXISTENT", "Description", "{}");
        assertFalse(result);
    }
    
    @Test
    void testGetAllScenarios() {
        service.saveScenario("TEST_Scenario1", "Description 1", 2025, "{}");
        service.saveScenario("TEST_Scenario2", "Description 2", 2024, "{}");
        
        List<UserData.SavedScenario> scenarios = service.getAllScenarios();
        assertNotNull(scenarios);
        assertTrue(scenarios.size() >= 2);
        
        // Check that our test scenarios are in the list
        boolean found1 = scenarios.stream().anyMatch(s -> s.getScenarioName().equals("TEST_Scenario1"));
        boolean found2 = scenarios.stream().anyMatch(s -> s.getScenarioName().equals("TEST_Scenario2"));
        assertTrue(found1);
        assertTrue(found2);
    }
    
    @Test
    void testGetScenariosForYear() {
        service.saveScenario("TEST_Scenario1", "Description 1", 2025, "{}");
        service.saveScenario("TEST_Scenario2", "Description 2", 2025, "{}");
        service.saveScenario("TEST_Scenario3", "Description 3", 2024, "{}");
        
        List<UserData.SavedScenario> scenarios = service.getScenariosForYear(2025);
        assertNotNull(scenarios);
        assertTrue(scenarios.size() >= 2);
        
        // All scenarios should be for year 2025
        for (UserData.SavedScenario scenario : scenarios) {
            if (scenario.getScenarioName().startsWith("TEST_")) {
                assertEquals(2025, scenario.getYear());
            }
        }
    }
    
    @Test
    void testDeleteScenario() {
        service.saveScenario("TEST_Scenario", "Description", 2025, "{}");
        
        boolean result = service.deleteScenario("TEST_Scenario");
        assertTrue(result);
        
        UserData.SavedScenario scenario = service.getScenario("TEST_Scenario");
        assertNull(scenario);
    }
    
    @Test
    void testDeleteScenarioNonExistent() {
        boolean result = service.deleteScenario("NON_EXISTENT");
        assertFalse(result);
    }
    
    @Test
    void testSavedScenarioGetters() {
        String scenarioData = "{\"test\":\"data\"}";
        service.saveScenario("TEST_Scenario", "Test description", 2025, scenarioData);
        
        UserData.SavedScenario scenario = service.getScenario("TEST_Scenario");
        assertNotNull(scenario);
        assertNotNull(scenario.getId());
        assertEquals("TEST_Scenario", scenario.getScenarioName());
        assertEquals("Test description", scenario.getDescription());
        assertEquals(2025, scenario.getYear());
        assertEquals(scenarioData, scenario.getScenarioData());
        assertNotNull(scenario.getCreatedAt());
        assertNotNull(scenario.getUpdatedAt());
    }
    
    // Preference tests
    @Test
    void testSavePreference() {
        boolean result = service.savePreference("TEST_PREF_KEY", "test_value");
        assertTrue(result);
    }
    
    @Test
    void testGetPreference() {
        service.savePreference("TEST_PREF_KEY", "test_value");
        
        String value = service.getPreference("TEST_PREF_KEY");
        assertNotNull(value);
        assertEquals("test_value", value);
    }
    
    @Test
    void testGetPreferenceNonExistent() {
        String value = service.getPreference("NON_EXISTENT");
        assertNull(value);
    }
    
    @Test
    void testUpdatePreference() {
        // Save initial preference
        service.savePreference("TEST_PREF_KEY", "initial_value");
        
        // Update it
        boolean result = service.savePreference("TEST_PREF_KEY", "updated_value");
        assertTrue(result);
        
        // Verify update
        String value = service.getPreference("TEST_PREF_KEY");
        assertEquals("updated_value", value);
    }
    
    @Test
    void testGetAllPreferences() {
        service.savePreference("TEST_PREF_KEY1", "value1");
        service.savePreference("TEST_PREF_KEY2", "value2");
        
        Map<String, String> preferences = service.getAllPreferences();
        assertNotNull(preferences);
        assertTrue(preferences.size() >= 2);
        assertTrue(preferences.containsKey("TEST_PREF_KEY1"));
        assertTrue(preferences.containsKey("TEST_PREF_KEY2"));
        assertEquals("value1", preferences.get("TEST_PREF_KEY1"));
        assertEquals("value2", preferences.get("TEST_PREF_KEY2"));
    }
    
    @Test
    void testDeletePreference() {
        service.savePreference("TEST_PREF_KEY", "value");
        
        boolean result = service.deletePreference("TEST_PREF_KEY");
        assertTrue(result);
        
        String value = service.getPreference("TEST_PREF_KEY");
        assertNull(value);
    }
    
    @Test
    void testDeletePreferenceNonExistent() {
        boolean result = service.deletePreference("NON_EXISTENT");
        assertFalse(result);
    }
    
    @Test
    void testCommentWithSpecialCharacters() {
        String comment = "Test comment with special chars: <>&\"'";
        service.saveComment("TEST_Category", 2025, comment);
        
        String retrieved = service.getComment("TEST_Category", 2025);
        assertEquals(comment, retrieved);
    }
    
    @Test
    void testCommentWithLongText() {
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longText.append("This is a very long comment. ");
        }
        
        service.saveComment("TEST_Category", 2025, longText.toString());
        String retrieved = service.getComment("TEST_Category", 2025);
        assertEquals(longText.toString(), retrieved);
    }
    
    @Test
    void testScenarioWithComplexJSON() {
        String complexJSON = "{\"year\":2025,\"revenues\":[{\"type\":\"taxes\",\"amount\":1000}],\"expenses\":[{\"type\":\"salaries\",\"amount\":500}]}";
        service.saveScenario("TEST_Complex", "Complex scenario", 2025, complexJSON);
        
        UserData.SavedScenario scenario = service.getScenario("TEST_Complex");
        assertNotNull(scenario);
        assertEquals(complexJSON, scenario.getScenarioData());
    }
}
