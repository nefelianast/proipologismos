import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import ui.DatabaseConnection;

/**
 * Unit tests for SQLmaker class.
 */
class SQLmakerTest {

    @Test
    void testMake() {
        SQLmaker maker = new SQLmaker();
        // Test that make() can be called without errors
        assertDoesNotThrow(() -> maker.make());
    }

    @Test
    void testMakeCreatesTables() throws SQLException {
        SQLmaker maker = new SQLmaker();
        maker.make();
        
        // Verify that tables were created
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            
            // Check if revenue_2025 table exists
            ResultSet tables = metaData.getTables(null, null, "revenue_2025", null);
            assertTrue(tables.next(), "revenue_2025 table should exist");
            
            // Check if expenses_2025 table exists
            tables = metaData.getTables(null, null, "expenses_2025", null);
            assertTrue(tables.next(), "expenses_2025 table should exist");
            
            // Check if ministries_2025 table exists
            tables = metaData.getTables(null, null, "ministries_2025", null);
            assertTrue(tables.next(), "ministries_2025 table should exist");
        }
    }
}

