package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository class for user authentication and management.
 * Handles database operations for user login, registration, and validation.
 */
public class UserRepository {
    
    /**
     * Checks if a username and password combination is valid.
     * 
     * @param username The username to check
     * @param password The password to check
     * @return true if the credentials are valid, false otherwise
     */
    public boolean checkLogin(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // Simple password comparison (in production, use hashing!)
                return storedPassword != null && storedPassword.equals(password);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking login: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Checks if a username already exists in the database.
     * 
     * @param username The username to check
     * @return true if the username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            // If rs.next() returns true, the username exists
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Saves a new user to the database.
     * 
     * @param username The username for the new user
     * @param password The password for the new user
     * @return true if the user was saved successfully, false otherwise
     */
    public boolean saveUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

