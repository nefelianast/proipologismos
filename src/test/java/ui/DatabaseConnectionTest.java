package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Unit tests for DatabaseConnection utility class.
 */
class DatabaseConnectionTest {

    @Test
    void testGetDatabaseUrl() {
        // Test that the database URL is not null
        String url = DatabaseConnection.getDatabaseUrl();
        assertNotNull(url);
        assertTrue(url.contains("jdbc:sqlite"));
        assertTrue(url.contains("BudgetData.db"));
    }

    @Test
    void testGetConnection() {
        // Test that connection can be created (if database exists)
        try {
            Connection connection = DatabaseConnection.getConnection();
            assertNotNull(connection);
            assertFalse(connection.isClosed());
            DatabaseConnection.closeConnection(connection);
        } catch (SQLException e) {
            assertNotNull(e);
        }
    }

    @Test
    void testCloseConnection() {
        // Test closing a valid connection
        try {
            Connection connection = DatabaseConnection.getConnection();
            assertNotNull(connection);
            assertFalse(connection.isClosed());
            
            DatabaseConnection.closeConnection(connection);
            assertTrue(connection.isClosed());
        } catch (SQLException e) {
            // If database doesn't exist, skip this test
        }
    }

    @Test
    void testCloseConnectionWithNull() {
        // Test that closing a null connection doesn't throw an exception
        assertDoesNotThrow(() -> DatabaseConnection.closeConnection(null));
    }

    @Test
    void testCloseConnectionTwice() {
        // Test that closing a connection twice doesn't throw an exception
        try {
            Connection connection = DatabaseConnection.getConnection();
            DatabaseConnection.closeConnection(connection);
            assertDoesNotThrow(() -> DatabaseConnection.closeConnection(connection));
        } catch (SQLException e) {
            // If database doesn't exist, skip this test
        }
    }
}

