package ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// βοηθητική κλάση για τη διαχείριση συνδέσεων με τη βάση δεδομένων
public class DatabaseConnection {
    
    // URL σύνδεσης με τη βάση δεδομένων 
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/database/BudgetData.db";
    
    private DatabaseConnection() {
        // utility class 
    }
    
    // επιστρέφει το URL της βάσης 
    public static String getDatabaseUrl() {
        return DB_URL;
    }
    
    // δημιουργεί και επιστρέφει μια νέα σύνδεση με τη βάση 
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            throw e;
        }
    }
    
    // κλείνει μια σύνδεση με τη βάση με ασφαλή τρόπο
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}

