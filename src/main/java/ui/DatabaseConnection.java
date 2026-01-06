package ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// βοηθητική κλάση για τη διαχείριση συνδέσεων με τη βάση δεδομένων
// παρέχει κεντρική διαχείριση συνδέσεων για την εφαρμογή προϋπολογισμού
public class DatabaseConnection {
    
    // URL σύνδεσης με τη βάση δεδομένων SQLite
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/database/BudgetData.db";
    
    // private constructor για να αποτρέψει τη δημιουργία instance
    private DatabaseConnection() {
        // utility class - δεν χρειάζεται instantiation
    }
    
    // επιστρέφει το URL της βάσης δεδομένων
    public static String getDatabaseUrl() {
        return DB_URL;
    }
    
    // δημιουργεί και επιστρέφει μια νέα σύνδεση με τη βάση δεδομένων
    // @return ένα Connection object προς τη βάση δεδομένων
    // @throws SQLException αν συμβεί σφάλμα πρόσβασης στη βάση
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            throw e;
        }
    }
    
    // κλείνει μια σύνδεση με τη βάση δεδομένων με ασφαλή τρόπο
    // @param connection η σύνδεση που θα κλείσει
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

