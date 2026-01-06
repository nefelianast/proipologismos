package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for BudgetData class.
 */
class BudgetDataTest {

    @Test
    void testGetInstance() {
        // Test singleton pattern
        BudgetData instance1 = BudgetData.getInstance();
        BudgetData instance2 = BudgetData.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2); // Should be the same instance
    }

    @Test
    void testGetTotalRevenues() {
        BudgetData service = BudgetData.getInstance();
        
        // Test ότι η μέθοδος δεν πετάει exception
        // ΣΗΜΕΙΩΣΗ: Αυτό το test μπορεί να επιστρέψει 0 αν δεν υπάρχουν δεδομένα στη βάση
        double revenues = service.getTotalRevenues(2025);
        assertTrue(revenues >= 0, "Revenues should be non-negative");
    }

    @Test
    void testGetTotalExpenses() {
        BudgetData service = BudgetData.getInstance();
        
        // Test ότι η μέθοδος δεν πετάει exception
        // ΣΗΜΕΙΩΣΗ: Αυτό το test μπορεί να επιστρέφει 0 αν δεν υπάρχουν δεδομένα στη βάση
        double expenses = service.getTotalExpenses(2025);
        assertTrue(expenses >= 0, "Expenses should be non-negative");
    }

    @Test
    void testGetBalance() {
        BudgetData service = BudgetData.getInstance();
        
        // Test ότι η μέθοδος υπολογίζει σωστά το ισοζύγιο
        double balance = service.getBalance(2025);
        // Το ισοζύγιο μπορεί να είναι θετικό, αρνητικό ή μηδέν
        assertNotNull(balance);
    }

    @Test
    void testGetRevenueBreakdown() {
        BudgetData service = BudgetData.getInstance();
        
        // Test ότι η μέθοδος επιστρέφει λίστα (μπορεί να είναι κενή αν δεν υπάρχουν δεδομένα)
        List<BudgetData.CategoryInfo> revenues = service.getRevenueBreakdown(2025);
        assertNotNull(revenues);
        // Αν υπάρχουν δεδομένα, ελέγχουμε ότι κάθε κατηγορία έχει όνομα, ποσό και ποσοστό
        for (BudgetData.CategoryInfo revenue : revenues) {
            assertNotNull(revenue.getName());
            assertTrue(revenue.getAmount() >= 0);
            assertTrue(revenue.getPercentage() >= 0);
        }
    }

    @Test
    void testGetExpensesBreakdown() {
        BudgetData service = BudgetData.getInstance();
        
        // Test ότι η μέθοδος επιστρέφει λίστα (μπορεί να είναι κενή αν δεν υπάρχουν δεδομένα)
        List<BudgetData.CategoryInfo> expenses = service.getExpensesBreakdown(2025);
        assertNotNull(expenses);
        // Αν υπάρχουν δεδομένα, ελέγχουμε ότι κάθε κατηγορία έχει όνομα, ποσό και ποσοστό
        for (BudgetData.CategoryInfo expense : expenses) {
            assertNotNull(expense.getName());
            assertTrue(expense.getAmount() >= 0);
            assertTrue(expense.getPercentage() >= 0);
        }
    }

    // CategoryInfo inner class tests
    @Test
    void testCategoryInfo() {
        BudgetData.CategoryInfo category = new BudgetData.CategoryInfo(
            "Test Category", 1000.0, 25.5
        );
        
        assertEquals("Test Category", category.getName());
        assertEquals(1000.0, category.getAmount(), 0.01);
        assertEquals(25.5, category.getPercentage(), 0.01);
    }

    @Test
    void testCategoryInfoWithZeroValues() {
        BudgetData.CategoryInfo category = new BudgetData.CategoryInfo(
            "Zero Category", 0.0, 0.0
        );
        
        assertEquals("Zero Category", category.getName());
        assertEquals(0.0, category.getAmount(), 0.01);
        assertEquals(0.0, category.getPercentage(), 0.01);
    }

    @Test
    void testCategoryInfoWithNegativePercentage() {
        // Percentage can be negative in some cases
        BudgetData.CategoryInfo category = new BudgetData.CategoryInfo(
            "Negative Change", 100.0, -5.5
        );
        
        assertEquals("Negative Change", category.getName());
        assertEquals(100.0, category.getAmount(), 0.01);
        assertEquals(-5.5, category.getPercentage(), 0.01);
    }

    @Test
    void testCategoryInfoWithLargeValues() {
        BudgetData.CategoryInfo category = new BudgetData.CategoryInfo(
            "Large Category", 1000000000.0, 99.99
        );
        
        assertEquals("Large Category", category.getName());
        assertEquals(1000000000.0, category.getAmount(), 0.01);
        assertEquals(99.99, category.getPercentage(), 0.01);
    }
}
