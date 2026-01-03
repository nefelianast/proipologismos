package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for BudgetDataService class.
 */
class BudgetDataServiceTest {

    @Test
    void testGetInstance() {
        // Test singleton pattern
        BudgetDataService instance1 = BudgetDataService.getInstance();
        BudgetDataService instance2 = BudgetDataService.getInstance();
        
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2); // Should be the same instance
    }

    @Test
    void testGetBudgetData() {
        BudgetDataService service = BudgetDataService.getInstance();
        
        // Test getting data for existing year (from sample data)
        BudgetDataService.BudgetYearData data2023 = service.getBudgetData(2023);
        assertNotNull(data2023);
        assertEquals(2023, data2023.getYear());
        
        // Test getting data for non-existing year
        BudgetDataService.BudgetYearData data2020 = service.getBudgetData(2020);
        assertNull(data2020);
    }

    // BudgetYearData inner class tests
    @Test
    void testBudgetYearData() {
        BudgetDataService.BudgetYearData data = new BudgetDataService.BudgetYearData(2024);
        
        assertEquals(2024, data.getYear());
        
        // Test setters and getters
        data.setTotalRevenues(100.0);
        data.setTotalExpenses(95.0);
        
        assertEquals(100.0, data.getTotalRevenues(), 0.01);
        assertEquals(95.0, data.getTotalExpenses(), 0.01);
        
        // Test adding categories
        data.addCategory("Test Category", 50.0, 52.6);
        List<BudgetDataService.CategoryInfo> categories = data.getCategories();
        
        assertNotNull(categories);
        assertEquals(1, categories.size());
        assertEquals("Test Category", categories.get(0).getName());
        assertEquals(50.0, categories.get(0).getAmount(), 0.01);
        assertEquals(52.6, categories.get(0).getPercentage(), 0.01);
    }

    @Test
    void testBudgetYearDataMultipleCategories() {
        BudgetDataService.BudgetYearData data = new BudgetDataService.BudgetYearData(2025);
        
        data.addCategory("Category 1", 10.0, 10.0);
        data.addCategory("Category 2", 20.0, 20.0);
        data.addCategory("Category 3", 30.0, 30.0);
        
        List<BudgetDataService.CategoryInfo> categories = data.getCategories();
        assertEquals(3, categories.size());
        
        assertEquals("Category 1", categories.get(0).getName());
        assertEquals("Category 2", categories.get(1).getName());
        assertEquals("Category 3", categories.get(2).getName());
    }

    // CategoryInfo inner class tests
    @Test
    void testCategoryInfo() {
        BudgetDataService.CategoryInfo category = new BudgetDataService.CategoryInfo(
            "Test Category", 1000.0, 25.5
        );
        
        assertEquals("Test Category", category.getName());
        assertEquals(1000.0, category.getAmount(), 0.01);
        assertEquals(25.5, category.getPercentage(), 0.01);
    }

    @Test
    void testCategoryInfoWithZeroValues() {
        BudgetDataService.CategoryInfo category = new BudgetDataService.CategoryInfo(
            "Zero Category", 0.0, 0.0
        );
        
        assertEquals("Zero Category", category.getName());
        assertEquals(0.0, category.getAmount(), 0.01);
        assertEquals(0.0, category.getPercentage(), 0.01);
    }

    @Test
    void testCategoryInfoWithNegativePercentage() {
        // Percentage can be negative in some cases
        BudgetDataService.CategoryInfo category = new BudgetDataService.CategoryInfo(
            "Negative Change", 100.0, -5.5
        );
        
        assertEquals("Negative Change", category.getName());
        assertEquals(100.0, category.getAmount(), 0.01);
        assertEquals(-5.5, category.getPercentage(), 0.01);
    }

    @Test
    void testCategoryInfoWithLargeValues() {
        BudgetDataService.CategoryInfo category = new BudgetDataService.CategoryInfo(
            "Large Category", 1000000000.0, 99.99
        );
        
        assertEquals("Large Category", category.getName());
        assertEquals(1000000000.0, category.getAmount(), 0.01);
        assertEquals(99.99, category.getPercentage(), 0.01);
    }
}

