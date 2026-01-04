package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BudgetStatisticsCalculator utility class.
 */
class BudgetStatisticsCalculatorTest {

    @Test
    void testCalculatePercentageChange() {
        // Test positive change
        assertEquals(25.0, BudgetStatisticsCalculator.calculatePercentageChange(125.0, 100.0), 0.01);
        
        // Test negative change
        assertEquals(-20.0, BudgetStatisticsCalculator.calculatePercentageChange(80.0, 100.0), 0.01);
        
        // Test no change
        assertEquals(0.0, BudgetStatisticsCalculator.calculatePercentageChange(100.0, 100.0), 0.01);
        
        // Test division by zero (should return 0.0)
        assertEquals(0.0, BudgetStatisticsCalculator.calculatePercentageChange(100.0, 0.0), 0.01);
        
        // Test from zero to positive
        assertEquals(0.0, BudgetStatisticsCalculator.calculatePercentageChange(100.0, 0.0), 0.01);
    }

    @Test
    void testCalculateBalance() {
        // Test surplus
        assertEquals(500.0, BudgetStatisticsCalculator.calculateBalance(1000.0, 500.0), 0.01);
        
        // Test deficit
        assertEquals(-500.0, BudgetStatisticsCalculator.calculateBalance(500.0, 1000.0), 0.01);
        
        // Test balanced
        assertEquals(0.0, BudgetStatisticsCalculator.calculateBalance(1000.0, 1000.0), 0.01);
        
        // Test with zero revenues
        assertEquals(-500.0, BudgetStatisticsCalculator.calculateBalance(0.0, 500.0), 0.01);
        
        // Test with zero expenses
        assertEquals(1000.0, BudgetStatisticsCalculator.calculateBalance(1000.0, 0.0), 0.01);
    }

    @Test
    void testCalculatePercentage() {
        // Test normal percentage
        assertEquals(25.0, BudgetStatisticsCalculator.calculatePercentage(25.0, 100.0), 0.01);
        assertEquals(50.0, BudgetStatisticsCalculator.calculatePercentage(50.0, 100.0), 0.01);
        assertEquals(100.0, BudgetStatisticsCalculator.calculatePercentage(100.0, 100.0), 0.01);
        
        // Test zero amount
        assertEquals(0.0, BudgetStatisticsCalculator.calculatePercentage(0.0, 100.0), 0.01);
        
        // Test division by zero (should return 0.0)
        assertEquals(0.0, BudgetStatisticsCalculator.calculatePercentage(25.0, 0.0), 0.01);
        
        // Test percentage greater than 100%
        assertEquals(200.0, BudgetStatisticsCalculator.calculatePercentage(200.0, 100.0), 0.01);
    }

    @Test
    void testCalculateTotal() {
        // Test with positive values
        double[] values1 = {10.0, 20.0, 30.0, 40.0};
        assertEquals(100.0, BudgetStatisticsCalculator.calculateTotal(values1), 0.01);
        
        // Test with negative values
        double[] values2 = {-10.0, -20.0, -30.0};
        assertEquals(-60.0, BudgetStatisticsCalculator.calculateTotal(values2), 0.01);
        
        // Test with mixed values
        double[] values3 = {10.0, -5.0, 20.0, -15.0};
        assertEquals(10.0, BudgetStatisticsCalculator.calculateTotal(values3), 0.01);
        
        // Test with single value
        double[] values4 = {42.0};
        assertEquals(42.0, BudgetStatisticsCalculator.calculateTotal(values4), 0.01);
        
        // Test with empty array
        double[] values5 = {};
        assertEquals(0.0, BudgetStatisticsCalculator.calculateTotal(values5), 0.01);
    }

    @Test
    void testCalculateAverage() {
        // Test with positive values
        double[] values1 = {10.0, 20.0, 30.0, 40.0};
        assertEquals(25.0, BudgetStatisticsCalculator.calculateAverage(values1), 0.01);
        
        // Test with negative values
        double[] values2 = {-10.0, -20.0, -30.0};
        assertEquals(-20.0, BudgetStatisticsCalculator.calculateAverage(values2), 0.01);
        
        // Test with mixed values
        double[] values3 = {10.0, -5.0, 20.0, -15.0};
        assertEquals(2.5, BudgetStatisticsCalculator.calculateAverage(values3), 0.01);
        
        // Test with single value
        double[] values4 = {42.0};
        assertEquals(42.0, BudgetStatisticsCalculator.calculateAverage(values4), 0.01);
        
        // Test with empty array (should return 0.0)
        double[] values5 = {};
        assertEquals(0.0, BudgetStatisticsCalculator.calculateAverage(values5), 0.01);
    }

    @Test
    void testCalculateGrowthRate() {
        // Test positive growth
        assertEquals(25.0, BudgetStatisticsCalculator.calculateGrowthRate(125.0, 100.0), 0.01);
        
        // Test negative growth
        assertEquals(-20.0, BudgetStatisticsCalculator.calculateGrowthRate(80.0, 100.0), 0.01);
        
        // Test no growth
        assertEquals(0.0, BudgetStatisticsCalculator.calculateGrowthRate(100.0, 100.0), 0.01);
        
        // Test division by zero
        assertEquals(0.0, BudgetStatisticsCalculator.calculateGrowthRate(100.0, 0.0), 0.01);
    }

    @Test
    void testIsSignificantChangeWithThreshold() {
        // Test significant positive change
        assertTrue(BudgetStatisticsCalculator.isSignificantChange(10.0, 5.0));
        
        // Test significant negative change
        assertTrue(BudgetStatisticsCalculator.isSignificantChange(-10.0, 5.0));
        
        // Test non-significant change
        assertFalse(BudgetStatisticsCalculator.isSignificantChange(3.0, 5.0));
        assertFalse(BudgetStatisticsCalculator.isSignificantChange(-3.0, 5.0));
        
        // Test exactly at threshold
        assertFalse(BudgetStatisticsCalculator.isSignificantChange(5.0, 5.0));
        assertTrue(BudgetStatisticsCalculator.isSignificantChange(5.1, 5.0));
        
        // Test with custom threshold
        assertTrue(BudgetStatisticsCalculator.isSignificantChange(15.0, 10.0));
        assertFalse(BudgetStatisticsCalculator.isSignificantChange(5.0, 10.0));
    }

    @Test
    void testIsSignificantChangeDefaultThreshold() {
        // Test significant positive change (default threshold is 5.0)
        assertTrue(BudgetStatisticsCalculator.isSignificantChange(10.0));
        
        // Test significant negative change
        assertTrue(BudgetStatisticsCalculator.isSignificantChange(-10.0));
        
        // Test non-significant change
        assertFalse(BudgetStatisticsCalculator.isSignificantChange(3.0));
        assertFalse(BudgetStatisticsCalculator.isSignificantChange(-3.0));
        
        // Test exactly at default threshold
        assertFalse(BudgetStatisticsCalculator.isSignificantChange(5.0));
        assertTrue(BudgetStatisticsCalculator.isSignificantChange(5.1));
    }
}

