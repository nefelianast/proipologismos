package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// κλάση για διαχείριση persistence operations για user data
// διαχειρίζεται αποθήκευση και ανάκτηση σχολίων, σεναρίων και προτιμήσεων χρήστη
public class UserData {
    
    private static UserData instance;
    
    private UserData() {
        // private constructor για singleton pattern
    }
    
    // επιστρέφει το singleton instance του UserData
    public static UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
        }
        return instance;
    }
    
    // αποθηκεύει ή ενημερώνει ένα σχόλιο για συγκεκριμένη κατηγορία και έτος
    public boolean saveComment(String categoryName, int year, String comments) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // έλεγχος αν το σχόλιο υπάρχει ήδη
            String checkSql = "SELECT id FROM user_comments WHERE category_name = ? AND year = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, categoryName);
                checkStmt.setInt(2, year);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    // ενημέρωση υπάρχοντος σχολίου
                    String updateSql = "UPDATE user_comments SET comments = ?, updated_at = CURRENT_TIMESTAMP WHERE category_name = ? AND year = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setString(1, comments);
                        updateStmt.setString(2, categoryName);
                        updateStmt.setInt(3, year);
                        updateStmt.executeUpdate();
                        return true;
                    }
                } else {
                    // εισαγωγή νέου σχολίου
                    String insertSql = "INSERT INTO user_comments (category_name, year, comments) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setString(1, categoryName);
                        insertStmt.setInt(2, year);
                        insertStmt.setString(3, comments);
                        insertStmt.executeUpdate();
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ανάκτηση σχολίου για συγκεκριμένη κατηγορία και έτος
    public String getComment(String categoryName, int year) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "SELECT comments FROM user_comments WHERE category_name = ? AND year = ?")) {
            
            stmt.setString(1, categoryName);
            stmt.setInt(2, year);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("comments");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // διαγραφή σχολίου για συγκεκριμένη κατηγορία και έτος
    public boolean deleteComment(String categoryName, int year) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "DELETE FROM user_comments WHERE category_name = ? AND year = ?")) {
            
            stmt.setString(1, categoryName);
            stmt.setInt(2, year);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // επιστρέφει όλα τα σχόλια για ένα συγκεκριμένο έτος
    public Map<String, String> getAllCommentsForYear(int year) {
        Map<String, String> commentsMap = new HashMap<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "SELECT category_name, comments FROM user_comments WHERE year = ?")) {
            
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                commentsMap.put(rs.getString("category_name"), rs.getString("comments"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commentsMap;
    }
    
    // εσωτερική κλάση για αντιπροσώπευση αποθηκευμένου σεναρίου
    public static class SavedScenario {
        private int id;
        private String scenarioName;
        private String description;
        private int year;
        private String scenarioData; // δεδομένα σε JSON format
        private Timestamp createdAt;
        private Timestamp updatedAt;
        
        public SavedScenario(int id, String scenarioName, String description, int year, 
                           String scenarioData, Timestamp createdAt, Timestamp updatedAt) {
            this.id = id;
            this.scenarioName = scenarioName;
            this.description = description;
            this.year = year;
            this.scenarioData = scenarioData;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        
        // getters
        public int getId() { return id; }
        public String getScenarioName() { return scenarioName; }
        public String getDescription() { return description; }
        public int getYear() { return year; }
        public String getScenarioData() { return scenarioData; }
        public Timestamp getCreatedAt() { return createdAt; }
        public Timestamp getUpdatedAt() { return updatedAt; }
    }
    
    // αποθηκεύει ένα νέο σενάριο (το όνομα πρέπει να είναι μοναδικό)
    public boolean saveScenario(String scenarioName, String description, int year, String scenarioData) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "INSERT INTO saved_scenarios (scenario_name, description, year, scenario_data) VALUES (?, ?, ?, ?)")) {
            
            stmt.setString(1, scenarioName);
            stmt.setString(2, description);
            stmt.setInt(3, year);
            stmt.setString(4, scenarioData);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ενημερώνει ένα υπάρχον σενάριο
    public boolean updateScenario(String scenarioName, String description, String scenarioData) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "UPDATE saved_scenarios SET description = ?, scenario_data = ?, updated_at = CURRENT_TIMESTAMP WHERE scenario_name = ?")) {
            
            stmt.setString(1, description);
            stmt.setString(2, scenarioData);
            stmt.setString(3, scenarioName);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ανάκτηση σεναρίου με βάση το όνομά του
    public SavedScenario getScenario(String scenarioName) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "SELECT * FROM saved_scenarios WHERE scenario_name = ?")) {
            
            stmt.setString(1, scenarioName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new SavedScenario(
                    rs.getInt("id"),
                    rs.getString("scenario_name"),
                    rs.getString("description"),
                    rs.getInt("year"),
                    rs.getString("scenario_data"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // επιστρέφει όλα τα αποθηκευμένα σενάρια
    public List<SavedScenario> getAllScenarios() {
        List<SavedScenario> scenarios = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM saved_scenarios ORDER BY created_at DESC")) {
            
            while (rs.next()) {
                scenarios.add(new SavedScenario(
                    rs.getInt("id"),
                    rs.getString("scenario_name"),
                    rs.getString("description"),
                    rs.getInt("year"),
                    rs.getString("scenario_data"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scenarios;
    }
    
    // επιστρέφει όλα τα σενάρια για ένα συγκεκριμένο έτος
    public List<SavedScenario> getScenariosForYear(int year) {
        List<SavedScenario> scenarios = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "SELECT * FROM saved_scenarios WHERE year = ? ORDER BY created_at DESC")) {
            
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                scenarios.add(new SavedScenario(
                    rs.getInt("id"),
                    rs.getString("scenario_name"),
                    rs.getString("description"),
                    rs.getInt("year"),
                    rs.getString("scenario_data"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scenarios;
    }
    
    // διαγραφή σεναρίου με βάση το όνομά του
    public boolean deleteScenario(String scenarioName) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "DELETE FROM saved_scenarios WHERE scenario_name = ?")) {
            
            stmt.setString(1, scenarioName);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // αποθηκεύει ή ενημερώνει μια προτίμηση χρήστη
    public boolean savePreference(String key, String value) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // έλεγχος αν η προτίμηση υπάρχει ήδη
            String checkSql = "SELECT id FROM user_preferences WHERE preference_key = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
                checkStmt.setString(1, key);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    // ενημέρωση υπάρχουσας προτίμησης
                    String updateSql = "UPDATE user_preferences SET preference_value = ?, updated_at = CURRENT_TIMESTAMP WHERE preference_key = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setString(1, value);
                        updateStmt.setString(2, key);
                        updateStmt.executeUpdate();
                        return true;
                    }
                } else {
                    // εισαγωγή νέας προτίμησης
                    String insertSql = "INSERT INTO user_preferences (preference_key, preference_value) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setString(1, key);
                        insertStmt.setString(2, value);
                        insertStmt.executeUpdate();
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ανάκτηση προτίμησης χρήστη με βάση το key
    public String getPreference(String key) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "SELECT preference_value FROM user_preferences WHERE preference_key = ?")) {
            
            stmt.setString(1, key);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("preference_value");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // επιστρέφει όλες τις προτιμήσεις χρήστη
    public Map<String, String> getAllPreferences() {
        Map<String, String> preferences = new HashMap<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT preference_key, preference_value FROM user_preferences")) {
            
            while (rs.next()) {
                preferences.put(rs.getString("preference_key"), rs.getString("preference_value"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return preferences;
    }
    
    // διαγραφή προτίμησης χρήστη
    public boolean deletePreference(String key) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "DELETE FROM user_preferences WHERE preference_key = ?")) {
            
            stmt.setString(1, key);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
