package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// κλάση για authentication και διαχείριση χρηστών
// διαχειρίζεται database operations για login, εγγραφή και επικύρωση
public class Authentication {
    
    // ελέγχει αν το συνδυασμό username/password είναι έγκυρος
    public boolean checkLogin(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // απλή σύγκριση password (σε production, χρησιμοποιήστε hashing!)
                return storedPassword != null && storedPassword.equals(password);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking login: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // ελέγχει αν ένα username υπάρχει ήδη στη βάση
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            // αν rs.next() επιστρέφει true, το username υπάρχει
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // αποθηκεύει έναν νέο χρήστη στη βάση
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
