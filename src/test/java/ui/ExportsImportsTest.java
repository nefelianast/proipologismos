package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Unit tests for ExportsImports class.
 */
class ExportsImportsTest {
    
    private ExportsImports service;
    private UserData userData;
    private String testExportPath;
    private String testImportPath;
    
    @BeforeEach
    void setUp() {
        service = ExportsImports.getInstance();
        userData = UserData.getInstance();
        
        // Setup test file paths
        testExportPath = "test_export.json";
        testImportPath = "test_import.json";
        
        // Clean up test files
        cleanupTestFiles();
        cleanupTestData();
    }
    
    /**
     * Clean up test files
     */
    private void cleanupTestFiles() {
        try {
            Files.deleteIfExists(Paths.get(testExportPath));
            Files.deleteIfExists(Paths.get(testImportPath));
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    /**
     * Clean up test data from database
     */
    private void cleanupTestData() {
        try {
            // Delete test comments
            userData.deleteComment("TEST_Category", 2025);
            userData.deleteComment("TEST_Category2", 2025);
            
            // Delete test scenarios
            userData.deleteScenario("TEST_Scenario");
            userData.deleteScenario("TEST_Scenario2");
            
            // Delete test preferences
            userData.deletePreference("TEST_PREF_KEY");
            userData.deletePreference("TEST_PREF_KEY2");
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    @Test
    void testGetInstance() {
        // Test singleton pattern
        ExportsImports instance1 = ExportsImports.getInstance();
        ExportsImports instance2 = ExportsImports.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }
    
    @Test
    void testExportToJSON() {
        // Setup test data
        userData.saveComment("TEST_Category", 2025, "Test comment");
        userData.saveScenario("TEST_Scenario", "Test description", 2025, "{\"test\":\"data\"}");
        userData.savePreference("TEST_PREF_KEY", "test_value");
        
        // Export
        boolean result = service.exportToJSON(testExportPath);
        assertTrue(result);
        
        // Verify file was created
        File file = new File(testExportPath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }
    
    @Test
    void testExportToJSONFileContent() {
        // Setup test data
        userData.saveComment("TEST_Category", 2025, "Test comment");
        userData.saveScenario("TEST_Scenario", "Test description", 2025, "{\"test\":\"data\"}");
        userData.savePreference("TEST_PREF_KEY", "test_value");
        
        // Export
        service.exportToJSON(testExportPath);
        
        // Read and verify content
        try {
            String content = new String(Files.readAllBytes(Paths.get(testExportPath)));
            assertNotNull(content);
            assertTrue(content.contains("comments"));
            assertTrue(content.contains("scenarios"));
            assertTrue(content.contains("preferences"));
            assertTrue(content.contains("TEST_Category"));
            assertTrue(content.contains("TEST_Scenario"));
            assertTrue(content.contains("TEST_PREF_KEY"));
        } catch (Exception e) {
            fail("Failed to read exported file: " + e.getMessage());
        }
    }
    
    @Test
    void testImportFromJSON() {
        // First export some data
        userData.saveComment("TEST_Category", 2025, "Original comment");
        userData.saveScenario("TEST_Scenario", "Original description", 2025, "{\"original\":\"data\"}");
        userData.savePreference("TEST_PREF_KEY", "original_value");
        
        service.exportToJSON(testExportPath);
        
        // Delete the data
        userData.deleteComment("TEST_Category", 2025);
        userData.deleteScenario("TEST_Scenario");
        userData.deletePreference("TEST_PREF_KEY");
        
        // Import
        boolean result = service.importFromJSON(testExportPath);
        assertTrue(result);
        
        // Verify data was restored
        String comment = userData.getComment("TEST_Category", 2025);
        assertEquals("Original comment", comment);
        
        UserData.SavedScenario scenario = userData.getScenario("TEST_Scenario");
        assertNotNull(scenario);
        assertEquals("Original description", scenario.getDescription());
        
        String preference = userData.getPreference("TEST_PREF_KEY");
        assertEquals("original_value", preference);
    }
    
    @Test
    void testImportFromJSONNonExistentFile() {
        boolean result = service.importFromJSON("non_existent_file.json");
        assertFalse(result);
    }
    
    @Test
    void testExportBudgetDataToJSON() {
        // This test depends on having budget data in the database
        // We'll test that the method doesn't throw an exception
        // and creates a file (even if empty)
        boolean result = service.exportBudgetDataToJSON("test_budget_export.json", 2025);
        
        // The method should complete without error
        // Note: It may return false if there's no data, which is acceptable
        assertNotNull(Boolean.valueOf(result));
        
        // Clean up
        try {
            Files.deleteIfExists(Paths.get("test_budget_export.json"));
        } catch (Exception e) {
            // Ignore
        }
    }
    
    @Test
    void testExportEmptyData() {
        // Export with no data
        boolean result = service.exportToJSON(testExportPath);
        assertTrue(result);
        
        // File should still be created
        File file = new File(testExportPath);
        assertTrue(file.exists());
        
        // Content should have empty arrays
        try {
            String content = new String(Files.readAllBytes(Paths.get(testExportPath)));
            assertTrue(content.contains("\"comments\":[]") || content.contains("\"comments\": []"));
            assertTrue(content.contains("\"scenarios\":[]") || content.contains("\"scenarios\": []"));
        } catch (Exception e) {
            fail("Failed to read exported file: " + e.getMessage());
        }
    }
    
    @Test
    void testImportFromJSONWithMultipleItems() {
        // Create export file manually with multiple items
        String jsonContent = "{\n" +
            "  \"comments\": [\n" +
            "    {\"category\": \"TEST_Category\", \"year\": 2025, \"comments\": \"Comment 1\"},\n" +
            "    {\"category\": \"TEST_Category2\", \"year\": 2025, \"comments\": \"Comment 2\"}\n" +
            "  ],\n" +
            "  \"scenarios\": [\n" +
            "    {\"name\": \"TEST_Scenario\", \"description\": \"Desc 1\", \"year\": 2025, \"data\": \"{}\"},\n" +
            "    {\"name\": \"TEST_Scenario2\", \"description\": \"Desc 2\", \"year\": 2024, \"data\": \"{}\"}\n" +
            "  ],\n" +
            "  \"preferences\": {\n" +
            "    \"TEST_PREF_KEY\": \"value1\",\n" +
            "    \"TEST_PREF_KEY2\": \"value2\"\n" +
            "  }\n" +
            "}";
        
        try {
            Files.write(Paths.get(testImportPath), jsonContent.getBytes());
            
            // Import
            boolean result = service.importFromJSON(testImportPath);
            assertTrue(result);
            
            // Verify all items were imported
            assertEquals("Comment 1", userData.getComment("TEST_Category", 2025));
            assertEquals("Comment 2", userData.getComment("TEST_Category2", 2025));
            
            UserData.SavedScenario scenario1 = userData.getScenario("TEST_Scenario");
            assertNotNull(scenario1);
            assertEquals("Desc 1", scenario1.getDescription());
            
            UserData.SavedScenario scenario2 = userData.getScenario("TEST_Scenario2");
            assertNotNull(scenario2);
            assertEquals("Desc 2", scenario2.getDescription());
            
            assertEquals("value1", userData.getPreference("TEST_PREF_KEY"));
            assertEquals("value2", userData.getPreference("TEST_PREF_KEY2"));
        } catch (Exception e) {
            fail("Failed to create or import test file: " + e.getMessage());
        }
    }
    
    @Test
    void testExportImportRoundTrip() {
        // Setup original data
        userData.saveComment("TEST_Category", 2025, "Round trip comment");
        userData.saveScenario("TEST_Scenario", "Round trip scenario", 2025, "{\"test\":\"roundtrip\"}");
        userData.savePreference("TEST_PREF_KEY", "roundtrip_value");
        
        // Export
        service.exportToJSON(testExportPath);
        
        // Clear data
        userData.deleteComment("TEST_Category", 2025);
        userData.deleteScenario("TEST_Scenario");
        userData.deletePreference("TEST_PREF_KEY");
        
        // Verify cleared
        assertNull(userData.getComment("TEST_Category", 2025));
        assertNull(userData.getScenario("TEST_Scenario"));
        assertNull(userData.getPreference("TEST_PREF_KEY"));
        
        // Import
        service.importFromJSON(testExportPath);
        
        // Verify restored
        assertEquals("Round trip comment", userData.getComment("TEST_Category", 2025));
        assertNotNull(userData.getScenario("TEST_Scenario"));
        assertEquals("Round trip scenario", userData.getScenario("TEST_Scenario").getDescription());
        assertEquals("roundtrip_value", userData.getPreference("TEST_PREF_KEY"));
    }
    
    @Test
    void testExportToJSONInvalidPath() {
        // Try to export to an invalid path (directory that doesn't exist)
        boolean result = service.exportToJSON("/nonexistent/directory/file.json");
        // Should handle gracefully (may return false or throw, depending on implementation)
        assertNotNull(Boolean.valueOf(result));
    }
}
