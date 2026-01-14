package ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.Statement;

class AuthenticationTest {
    
    private Authentication auth;
    
    @BeforeEach
    void setUp() {
        auth = new Authentication();
        cleanupTestUsers();
    }
    
    private void cleanupTestUsers() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM users WHERE username LIKE 'TEST_%'");
        } catch (Exception e) {
        }
    }
    
    @Test
    void testCheckLoginValidCredentials() {
        auth.saveUser("TEST_user1", "password123");
        
        boolean result = auth.checkLogin("TEST_user1", "password123");
        assertTrue(result);
    }
    
    @Test
    void testCheckLoginInvalidPassword() {
        auth.saveUser("TEST_user2", "password123");
        
        boolean result = auth.checkLogin("TEST_user2", "wrongpassword");
        assertFalse(result);
    }
    
    @Test
    void testCheckLoginNonExistentUser() {
        boolean result = auth.checkLogin("TEST_nonexistent", "password123");
        assertFalse(result);
    }
    
    @Test
    void testCheckLoginNullUsername() {
        boolean result = auth.checkLogin(null, "password123");
        assertFalse(result);
    }
    
    @Test
    void testCheckLoginNullPassword() {
        auth.saveUser("TEST_user3", "password123");
        
        boolean result = auth.checkLogin("TEST_user3", null);
        assertFalse(result);
    }
    
    @Test
    void testUsernameExistsTrue() {
        auth.saveUser("TEST_user4", "password123");
        
        boolean result = auth.usernameExists("TEST_user4");
        assertTrue(result);
    }
    
    @Test
    void testUsernameExistsFalse() {
        boolean result = auth.usernameExists("TEST_nonexistent2");
        assertFalse(result);
    }
    
    @Test
    void testUsernameExistsNull() {
        boolean result = auth.usernameExists(null);
        assertFalse(result);
    }
    
    @Test
    void testSaveUserSuccess() {
        boolean result = auth.saveUser("TEST_user5", "password123");
        assertTrue(result);
        
        boolean exists = auth.usernameExists("TEST_user5");
        assertTrue(exists);
    }
    
    @Test
    void testSaveUserDuplicateUsername() {
        auth.saveUser("TEST_user6", "password123");
        
        boolean result = auth.saveUser("TEST_user6", "password456");
        assertFalse(result);
    }
    
    @Test
    void testSaveUserNullUsername() {
        boolean result = auth.saveUser(null, "password123");
        assertFalse(result);
    }
    
    @Test
    void testSaveUserNullPassword() {
        boolean result = auth.saveUser("TEST_user7", null);
        assertFalse(result);
    }
    
    @Test
    void testSaveUserEmptyUsername() {
        boolean result = auth.saveUser("", "password123");
        assertFalse(result);
    }
    
    @Test
    void testSaveUserEmptyPassword() {
        boolean result = auth.saveUser("TEST_user8", "");
        assertFalse(result);
    }
    
    @Test
    void testLoginFlow() {
        boolean saved = auth.saveUser("TEST_user9", "mypassword");
        assertTrue(saved);
        
        boolean exists = auth.usernameExists("TEST_user9");
        assertTrue(exists);
        
        boolean login = auth.checkLogin("TEST_user9", "mypassword");
        assertTrue(login);
        
        boolean wrongPassword = auth.checkLogin("TEST_user9", "wrong");
        assertFalse(wrongPassword);
    }
}
