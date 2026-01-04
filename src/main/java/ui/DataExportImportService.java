package ui;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Service class for exporting and importing data in JSON and XML formats.
 * Supports exporting comments, scenarios, preferences, and budget data.
 */
public class DataExportImportService {
    
    private static DataExportImportService instance;
    private DataPersistenceService persistenceService;
    
    private DataExportImportService() {
        this.persistenceService = DataPersistenceService.getInstance();
    }
    
    /**
     * Gets the singleton instance of DataExportImportService
     * @return The DataExportImportService instance
     */
    public static DataExportImportService getInstance() {
        if (instance == null) {
            instance = new DataExportImportService();
        }
        return instance;
    }
    
    /**
     * Exports all user data to a JSON file
     * @param filePath The path where to save the JSON file
     * @return true if successful, false otherwise
     */
    public boolean exportToJSON(String filePath) {
        try {
            JSONObject exportData = new JSONObject();
            
            // Export comments
            JSONArray commentsArray = new JSONArray();
            for (int year = 2023; year <= 2026; year++) {
                Map<String, String> comments = persistenceService.getAllCommentsForYear(year);
                for (Map.Entry<String, String> entry : comments.entrySet()) {
                    JSONObject commentObj = new JSONObject();
                    commentObj.put("category", entry.getKey());
                    commentObj.put("year", year);
                    commentObj.put("comments", entry.getValue());
                    commentsArray.put(commentObj);
                }
            }
            exportData.put("comments", commentsArray);
            
            // Export scenarios
            JSONArray scenariosArray = new JSONArray();
            List<DataPersistenceService.SavedScenario> scenarios = persistenceService.getAllScenarios();
            for (DataPersistenceService.SavedScenario scenario : scenarios) {
                JSONObject scenarioObj = new JSONObject();
                scenarioObj.put("name", scenario.getScenarioName());
                scenarioObj.put("description", scenario.getDescription());
                scenarioObj.put("year", scenario.getYear());
                scenarioObj.put("data", scenario.getScenarioData());
                scenarioObj.put("createdAt", scenario.getCreatedAt().toString());
                scenariosArray.put(scenarioObj);
            }
            exportData.put("scenarios", scenariosArray);
            
            // Export preferences
            Map<String, String> preferences = persistenceService.getAllPreferences();
            JSONObject preferencesObj = new JSONObject();
            for (Map.Entry<String, String> entry : preferences.entrySet()) {
                preferencesObj.put(entry.getKey(), entry.getValue());
            }
            exportData.put("preferences", preferencesObj);
            
            // Write to file
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(exportData.toString(4)); // Pretty print with 4 spaces indentation
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Imports user data from a JSON file
     * @param filePath The path to the JSON file to import
     * @return true if successful, false otherwise
     */
    public boolean importFromJSON(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject importData = new JSONObject(content);
            
            // Import comments
            if (importData.has("comments")) {
                JSONArray commentsArray = importData.getJSONArray("comments");
                for (int i = 0; i < commentsArray.length(); i++) {
                    JSONObject commentObj = commentsArray.getJSONObject(i);
                    String category = commentObj.getString("category");
                    int year = commentObj.getInt("year");
                    String comments = commentObj.getString("comments");
                    persistenceService.saveComment(category, year, comments);
                }
            }
            
            // Import scenarios
            if (importData.has("scenarios")) {
                JSONArray scenariosArray = importData.getJSONArray("scenarios");
                for (int i = 0; i < scenariosArray.length(); i++) {
                    JSONObject scenarioObj = scenariosArray.getJSONObject(i);
                    String name = scenarioObj.getString("name");
                    String description = scenarioObj.optString("description", "");
                    int year = scenarioObj.getInt("year");
                    String data = scenarioObj.getString("data");
                    
                    // Check if scenario exists, update if it does, otherwise create new
                    DataPersistenceService.SavedScenario existing = persistenceService.getScenario(name);
                    if (existing != null) {
                        persistenceService.updateScenario(name, description, data);
                    } else {
                        persistenceService.saveScenario(name, description, year, data);
                    }
                }
            }
            
            // Import preferences
            if (importData.has("preferences")) {
                JSONObject preferencesObj = importData.getJSONObject("preferences");
                for (String key : preferencesObj.keySet()) {
                    String value = preferencesObj.getString(key);
                    persistenceService.savePreference(key, value);
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Exports all user data to an XML file
     * @param filePath The path where to save the XML file
     * @return true if successful, false otherwise
     */
    public boolean exportToXML(String filePath) {
        try {
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xml.append("<export>\n");
            
            // Export comments
            xml.append("  <comments>\n");
            for (int year = 2023; year <= 2026; year++) {
                Map<String, String> comments = persistenceService.getAllCommentsForYear(year);
                for (Map.Entry<String, String> entry : comments.entrySet()) {
                    xml.append("    <comment>\n");
                    xml.append("      <category>").append(escapeXML(entry.getKey())).append("</category>\n");
                    xml.append("      <year>").append(year).append("</year>\n");
                    xml.append("      <text>").append(escapeXML(entry.getValue())).append("</text>\n");
                    xml.append("    </comment>\n");
                }
            }
            xml.append("  </comments>\n");
            
            // Export scenarios
            xml.append("  <scenarios>\n");
            List<DataPersistenceService.SavedScenario> scenarios = persistenceService.getAllScenarios();
            for (DataPersistenceService.SavedScenario scenario : scenarios) {
                xml.append("    <scenario>\n");
                xml.append("      <name>").append(escapeXML(scenario.getScenarioName())).append("</name>\n");
                xml.append("      <description>").append(escapeXML(scenario.getDescription())).append("</description>\n");
                xml.append("      <year>").append(scenario.getYear()).append("</year>\n");
                xml.append("      <data>").append(escapeXML(scenario.getScenarioData())).append("</data>\n");
                xml.append("      <createdAt>").append(scenario.getCreatedAt()).append("</createdAt>\n");
                xml.append("    </scenario>\n");
            }
            xml.append("  </scenarios>\n");
            
            // Export preferences
            xml.append("  <preferences>\n");
            Map<String, String> preferences = persistenceService.getAllPreferences();
            for (Map.Entry<String, String> entry : preferences.entrySet()) {
                xml.append("    <preference>\n");
                xml.append("      <key>").append(escapeXML(entry.getKey())).append("</key>\n");
                xml.append("      <value>").append(escapeXML(entry.getValue())).append("</value>\n");
                xml.append("    </preference>\n");
            }
            xml.append("  </preferences>\n");
            
            xml.append("</export>\n");
            
            // Write to file
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(xml.toString());
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Escapes XML special characters
     * @param text The text to escape
     * @return The escaped text
     */
    private String escapeXML(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&apos;");
    }
    
    /**
     * Exports budget data for a specific year to JSON
     * @param filePath The path where to save the JSON file
     * @param year The year to export
     * @return true if successful, false otherwise
     */
    public boolean exportBudgetDataToJSON(String filePath, int year) {
        try {
            BudgetDataService dataService = BudgetDataService.getInstance();
            JSONObject budgetData = new JSONObject();
            
            budgetData.put("year", year);
            budgetData.put("totalRevenues", dataService.getTotalRevenues(year));
            budgetData.put("totalExpenses", dataService.getTotalExpenses(year));
            budgetData.put("balance", dataService.getBalance(year));
            
            // Export revenue breakdown
            JSONArray revenuesArray = new JSONArray();
            List<BudgetDataService.CategoryInfo> revenues = dataService.getRevenueBreakdown(year);
            for (BudgetDataService.CategoryInfo revenue : revenues) {
                JSONObject revenueObj = new JSONObject();
                revenueObj.put("name", revenue.getName());
                revenueObj.put("amount", revenue.getAmount());
                revenueObj.put("percentage", revenue.getPercentage());
                revenuesArray.put(revenueObj);
            }
            budgetData.put("revenues", revenuesArray);
            
            // Export expense breakdown
            JSONArray expensesArray = new JSONArray();
            List<BudgetDataService.CategoryInfo> expenses = dataService.getExpensesBreakdown(year);
            for (BudgetDataService.CategoryInfo expense : expenses) {
                JSONObject expenseObj = new JSONObject();
                expenseObj.put("name", expense.getName());
                expenseObj.put("amount", expense.getAmount());
                expenseObj.put("percentage", expense.getPercentage());
                expensesArray.put(expenseObj);
            }
            budgetData.put("expenses", expensesArray);
            
            // Write to file
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(budgetData.toString(4));
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

