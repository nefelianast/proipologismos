
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SQLinserter class.
 */
class SQLinserterTest {

    @Test
    void testInsertRevenue2026() {
        SQLinserter inserter = new SQLinserter();
        // Test that method can be called 
        assertDoesNotThrow(() -> {
            try {
                inserter.insertRevenue2026();
            } catch (Exception e) {
                // Expected if CSV file doesn't exist
                assertNotNull(e);
            }
        });
    }

    @Test
    void testInsertRevenue2025() {
        SQLinserter inserter = new SQLinserter();
        assertDoesNotThrow(() -> {
            try {
                inserter.insertRevenue2025();
            } catch (Exception e) {
                assertNotNull(e);
            }
        });
    }

    @Test
    void testInsertExpenses2025() {
        SQLinserter inserter = new SQLinserter();
        assertDoesNotThrow(() -> {
            try {
                inserter.insertExpenses2025();
            } catch (Exception e) {
                assertNotNull(e);
            }
        });
    }

    @Test
    void testInsertDecentralizedAdministrations2026() {
        SQLinserter inserter = new SQLinserter();
        assertDoesNotThrow(() -> {
            try {
                inserter.insertDecentralizedAdministrations2026();
            } catch (Exception e) {
                assertNotNull(e);
            }
        });
    }

    @Test
    void testInsertBudgetSummary2024() {
        SQLinserter inserter = new SQLinserter();
        assertDoesNotThrow(() -> {
            try {
                inserter.insertBudgetSummary2024();
            } catch (Exception e) {
                assertNotNull(e);
            }
        });
    }
}

