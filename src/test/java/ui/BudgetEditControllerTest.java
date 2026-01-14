package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BudgetEditControllerTest {

    @Test
    void testBudgetItemConstructor() {
        BudgetEditController.BudgetItem item = new BudgetEditController.BudgetItem(
            "Φορολογία", "taxes", 1000.0
        );
        assertNotNull(item);
    }

    @Test
    void testBudgetItemGetCategoryName() {
        BudgetEditController.BudgetItem item = new BudgetEditController.BudgetItem(
            "Φορολογία", "taxes", 1000.0
        );
        assertEquals("Φορολογία", item.getCategoryName());
    }

    @Test
    void testBudgetItemGetColumnName() {
        BudgetEditController.BudgetItem item = new BudgetEditController.BudgetItem(
            "Φορολογία", "taxes", 1000.0
        );
        assertEquals("taxes", item.getColumnName());
    }

    @Test
    void testBudgetItemGetAmount() {
        BudgetEditController.BudgetItem item = new BudgetEditController.BudgetItem(
            "Φορολογία", "taxes", 1000.0
        );
        assertEquals(1000.0, item.getAmount(), 0.01);
    }

    @Test
    void testBudgetItemSetAmount() {
        BudgetEditController.BudgetItem item = new BudgetEditController.BudgetItem(
            "Φορολογία", "taxes", 1000.0
        );
        item.setAmount(2000.0);
        assertEquals(2000.0, item.getAmount(), 0.01);
    }

    @Test
    void testBudgetItemSetAmountToZero() {
        BudgetEditController.BudgetItem item = new BudgetEditController.BudgetItem(
            "Φορολογία", "taxes", 1000.0
        );
        item.setAmount(0.0);
        assertEquals(0.0, item.getAmount(), 0.01);
    }

    @Test
    void testBudgetItemSetAmountToNegative() {
        BudgetEditController.BudgetItem item = new BudgetEditController.BudgetItem(
            "Φορολογία", "taxes", 1000.0
        );
        item.setAmount(-500.0);
        assertEquals(-500.0, item.getAmount(), 0.01);
    }

    @Test
    void testBudgetItemWithDifferentCategory() {
        BudgetEditController.BudgetItem item = new BudgetEditController.BudgetItem(
            "Κοινωνικές Εισφορές", "social_contributions", 500.0
        );
        assertEquals("Κοινωνικές Εισφορές", item.getCategoryName());
        assertEquals("social_contributions", item.getColumnName());
        assertEquals(500.0, item.getAmount(), 0.01);
    }

    @Test
    void testBudgetItemWithLargeAmount() {
        BudgetEditController.BudgetItem item = new BudgetEditController.BudgetItem(
            "Μεταβιβάσεις", "transfers", 1000000.0
        );
        assertEquals(1000000.0, item.getAmount(), 0.01);
    }

    @Test
    void testBudgetItemWithDecimalAmount() {
        BudgetEditController.BudgetItem item = new BudgetEditController.BudgetItem(
            "Πωλήσεις", "sales", 1234.56
        );
        assertEquals(1234.56, item.getAmount(), 0.01);
    }

    @Test
    void testBudgetItemMultipleSetAmount() {
        BudgetEditController.BudgetItem item = new BudgetEditController.BudgetItem(
            "Φορολογία", "taxes", 1000.0
        );
        item.setAmount(2000.0);
        assertEquals(2000.0, item.getAmount(), 0.01);
        
        item.setAmount(3000.0);
        assertEquals(3000.0, item.getAmount(), 0.01);
        
        item.setAmount(1500.0);
        assertEquals(1500.0, item.getAmount(), 0.01);
    }
}
