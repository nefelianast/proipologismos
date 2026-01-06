package ui;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

// κλάση για εξαγωγή και εισαγωγή δεδομένων σε μορφήJSON 
public class ExportsImports {
    
    private static ExportsImports instance;
    private UserData userData;
    
    private ExportsImports() {
        this.userData = UserData.getInstance();
    }
    
    // επιστρέφει το singleton instance
    public static ExportsImports getInstance() {
        if (instance == null) {
            instance = new ExportsImports();
        }
        return instance;
    }
    
    // εξάγει όλα τα user data σε JSON αρχείο
    public boolean exportToJSON(String filePath) {
        try {
            JSONObject exportData = new JSONObject();
            
            // Export comments
            JSONArray commentsArray = new JSONArray();
            for (int year = 2023; year <= 2026; year++) {
                   Map<String, String> comments = userData.getAllCommentsForYear(year);
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
            List<UserData.SavedScenario> scenarios = userData.getAllScenarios();
            for (UserData.SavedScenario scenario : scenarios) {
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
                    Map<String, String> preferences = userData.getAllPreferences();
            JSONObject preferencesObj = new JSONObject();
            for (Map.Entry<String, String> entry : preferences.entrySet()) {
                preferencesObj.put(entry.getKey(), entry.getValue());
            }
            exportData.put("preferences", preferencesObj);
            
            // Write to file
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(exportData.toString(4)); 
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // εισάγει user data από JSON αρχείο
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
                    userData.saveComment(category, year, comments);
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
                    UserData.SavedScenario existing = userData.getScenario(name);
                    if (existing != null) {
                        userData.updateScenario(name, description, data);
                    } else {
                        userData.saveScenario(name, description, year, data);
                    }
                }
            }
            
            // Import preferences
            if (importData.has("preferences")) {
                JSONObject preferencesObj = importData.getJSONObject("preferences");
                for (String key : preferencesObj.keySet()) {
                        String value = preferencesObj.getString(key);
                        userData.savePreference(key, value);
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // εξάγει budget data για συγκεκριμένο έτος σε JSON
    public boolean exportBudgetDataToJSON(String filePath, int year) {
        try {
            BudgetData budgetDataService = BudgetData.getInstance();
            JSONObject budgetDataJson = new JSONObject();
            
            budgetDataJson.put("year", year);
            budgetDataJson.put("totalRevenues", budgetDataService.getTotalRevenues(year));
            budgetDataJson.put("totalExpenses", budgetDataService.getTotalExpenses(year));
            budgetDataJson.put("balance", budgetDataService.getBalance(year));
            
            // Export revenue breakdown
            JSONArray revenuesArray = new JSONArray();
            List<BudgetData.CategoryInfo> revenues = budgetDataService.getRevenueBreakdown(year);
            for (BudgetData.CategoryInfo revenue : revenues) {
                JSONObject revenueObj = new JSONObject();
                revenueObj.put("name", revenue.getName());
                revenueObj.put("amount", revenue.getAmount());
                revenueObj.put("percentage", revenue.getPercentage());
                revenuesArray.put(revenueObj);
            }
            budgetDataJson.put("revenues", revenuesArray);
            
            // Export expense breakdown
            JSONArray expensesArray = new JSONArray();
            List<BudgetData.CategoryInfo> expenses = budgetDataService.getExpensesBreakdown(year);
            for (BudgetData.CategoryInfo expense : expenses) {
                JSONObject expenseObj = new JSONObject();
                expenseObj.put("name", expense.getName());
                expenseObj.put("amount", expense.getAmount());
                expenseObj.put("percentage", expense.getPercentage());
                expensesArray.put(expenseObj);
            }
            budgetDataJson.put("expenses", expensesArray);
            
            // Write to file
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(budgetDataJson.toString(4));
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
