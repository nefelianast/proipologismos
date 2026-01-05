package ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections.
 * Provides centralized database connection management for the budget application.
 */
public class DatabaseConnection {
    
    /**
     * Database connection string for SQLite database
     */
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/database/BudgetData.db";
    
    /**
     * Private constructor to prevent instantiation
     */
    private DatabaseConnection() {
        // Utility class - no instantiation needed
    }
    
    /**
     * Gets the database connection string
     * @return The database URL as a String
     */
    public static String getDatabaseUrl() {
        return DB_URL;
    }
    
    /**
     * Creates and returns a new database connection
     * @return A Connection object to the database
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Closes a database connection safely
     * @param connection The connection to close
     */
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

