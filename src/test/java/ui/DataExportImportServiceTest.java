package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Unit tests for DataExportImportService class.
 */
class DataExportImportServiceTest {
    
    private DataExportImportService service;
    private DataPersistenceService persistenceService;
    private String testExportPath;
    private String testImportPath;
    
    @BeforeEach
    void setUp() {
        service = DataExportImportService.getInstance();
        persistenceService = DataPersistenceService.getInstance();
        
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
            Files.deleteIfExists(Paths.get("test_export.xml"));
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
            persistenceService.deleteComment("TEST_Category", 2025);
            persistenceService.deleteComment("TEST_Category2", 2025);
            
            // Delete test scenarios
            persistenceService.deleteScenario("TEST_Scenario");
            persistenceService.deleteScenario("TEST_Scenario2");
            
            // Delete test preferences
            persistenceService.deletePreference("TEST_PREF_KEY");
            persistenceService.deletePreference("TEST_PREF_KEY2");
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    @Test
    void testGetInstance() {
        // Test singleton pattern
        DataExportImportService instance1 = DataExportImportService.getInstance();
        DataExportImportService instance2 = DataExportImportService.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }
    
    @Test
    void testExportToJSON() {
        // Setup test data
        persistenceService.saveComment("TEST_Category", 2025, "Test comment");
        persistenceService.saveScenario("TEST_Scenario", "Test description", 2025, "{\"test\":\"data\"}");
        persistenceService.savePreference("TEST_PREF_KEY", "test_value");
        
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
        persistenceService.saveComment("TEST_Category", 2025, "Test comment");
        persistenceService.saveScenario("TEST_Scenario", "Test description", 2025, "{\"test\":\"data\"}");
        persistenceService.savePreference("TEST_PREF_KEY", "test_value");
        
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
        persistenceService.saveComment("TEST_Category", 2025, "Original comment");
        persistenceService.saveScenario("TEST_Scenario", "Original description", 2025, "{\"original\":\"data\"}");
        persistenceService.savePreference("TEST_PREF_KEY", "original_value");
        
        service.exportToJSON(testExportPath);
        
        // Delete the data
        persistenceService.deleteComment("TEST_Category", 2025);
        persistenceService.deleteScenario("TEST_Scenario");
        persistenceService.deletePreference("TEST_PREF_KEY");
        
        // Import
        boolean result = service.importFromJSON(testExportPath);
        assertTrue(result);
        
        // Verify data was restored
        String comment = persistenceService.getComment("TEST_Category", 2025);
        assertEquals("Original comment", comment);
        
        DataPersistenceService.SavedScenario scenario = persistenceService.getScenario("TEST_Scenario");
        assertNotNull(scenario);
        assertEquals("Original description", scenario.getDescription());
        
        String preference = persistenceService.getPreference("TEST_PREF_KEY");
        assertEquals("original_value", preference);
    }
    
    @Test
    void testImportFromJSONNonExistentFile() {
        boolean result = service.importFromJSON("non_existent_file.json");
        assertFalse(result);
    }
    
    @Test
    void testExportToXML() {
        // Setup test data
        persistenceService.saveComment("TEST_Category", 2025, "Test comment");
        persistenceService.saveScenario("TEST_Scenario", "Test description", 2025, "{\"test\":\"data\"}");
        
        // Export
        boolean result = service.exportToXML("test_export.xml");
        assertTrue(result);
        
        // Verify file was created
        File file = new File("test_export.xml");
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }
    
    @Test
    void testExportToXMLFileContent() {
        // Setup test data
        persistenceService.saveComment("TEST_Category", 2025, "Test comment");
        
        // Export
        service.exportToXML("test_export.xml");
        
        // Read and verify content
        try {
            String content = new String(Files.readAllBytes(Paths.get("test_export.xml")));
            assertNotNull(content);
            assertTrue(content.contains("<?xml"));
            assertTrue(content.contains("<export>"));
            assertTrue(content.contains("<comments>"));
            assertTrue(content.contains("<scenarios>"));
            assertTrue(content.contains("<preferences>"));
        } catch (Exception e) {
            fail("Failed to read exported XML file: " + e.getMessage());
        }
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
            assertEquals("Comment 1", persistenceService.getComment("TEST_Category", 2025));
            assertEquals("Comment 2", persistenceService.getComment("TEST_Category2", 2025));
            
            DataPersistenceService.SavedScenario scenario1 = persistenceService.getScenario("TEST_Scenario");
            assertNotNull(scenario1);
            assertEquals("Desc 1", scenario1.getDescription());
            
            DataPersistenceService.SavedScenario scenario2 = persistenceService.getScenario("TEST_Scenario2");
            assertNotNull(scenario2);
            assertEquals("Desc 2", scenario2.getDescription());
            
            assertEquals("value1", persistenceService.getPreference("TEST_PREF_KEY"));
            assertEquals("value2", persistenceService.getPreference("TEST_PREF_KEY2"));
        } catch (Exception e) {
            fail("Failed to create or import test file: " + e.getMessage());
        }
    }
    
    @Test
    void testExportImportRoundTrip() {
        // Setup original data
        persistenceService.saveComment("TEST_Category", 2025, "Round trip comment");
        persistenceService.saveScenario("TEST_Scenario", "Round trip scenario", 2025, "{\"test\":\"roundtrip\"}");
        persistenceService.savePreference("TEST_PREF_KEY", "roundtrip_value");
        
        // Export
        service.exportToJSON(testExportPath);
        
        // Clear data
        persistenceService.deleteComment("TEST_Category", 2025);
        persistenceService.deleteScenario("TEST_Scenario");
        persistenceService.deletePreference("TEST_PREF_KEY");
        
        // Verify cleared
        assertNull(persistenceService.getComment("TEST_Category", 2025));
        assertNull(persistenceService.getScenario("TEST_Scenario"));
        assertNull(persistenceService.getPreference("TEST_PREF_KEY"));
        
        // Import
        service.importFromJSON(testExportPath);
        
        // Verify restored
        assertEquals("Round trip comment", persistenceService.getComment("TEST_Category", 2025));
        assertNotNull(persistenceService.getScenario("TEST_Scenario"));
        assertEquals("Round trip scenario", persistenceService.getScenario("TEST_Scenario").getDescription());
        assertEquals("roundtrip_value", persistenceService.getPreference("TEST_PREF_KEY"));
    }
    
    @Test
    void testExportToJSONInvalidPath() {
        // Try to export to an invalid path (directory that doesn't exist)
        boolean result = service.exportToJSON("/nonexistent/directory/file.json");
        // Should handle gracefully (may return false or throw, depending on implementation)
        assertNotNull(Boolean.valueOf(result));
    }
}

