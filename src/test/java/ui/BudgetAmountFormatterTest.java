package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BudgetAmountFormatter utility class.
 */
class BudgetAmountFormatterTest {

    @Test
    void testFormatCurrency() {
        // Test positive amounts
        assertEquals("€1,000.0", BudgetAmountFormatter.formatCurrency(1000.0));
        assertEquals("€1,234.5", BudgetAmountFormatter.formatCurrency(1234.5));
        assertEquals("€10,000.0", BudgetAmountFormatter.formatCurrency(10000.0));
        
        // Test zero
        assertEquals("€0.0", BudgetAmountFormatter.formatCurrency(0.0));
        
        // Test negative amounts
        assertEquals("€-1,000.0", BudgetAmountFormatter.formatCurrency(-1000.0));
        
        // Test large amounts
        assertEquals("€1,000,000.0", BudgetAmountFormatter.formatCurrency(1000000.0));
    }

    @Test
    void testFormatCurrencyNoDecimals() {
        // Test positive amounts
        assertEquals("€1,000", BudgetAmountFormatter.formatCurrencyNoDecimals(1000.0));
        assertEquals("€1,234", BudgetAmountFormatter.formatCurrencyNoDecimals(1234.5));
        assertEquals("€10,000", BudgetAmountFormatter.formatCurrencyNoDecimals(10000.0));
        
        // Test zero
        assertEquals("€0", BudgetAmountFormatter.formatCurrencyNoDecimals(0.0));
        
        // Test negative amounts
        assertEquals("€-1,000", BudgetAmountFormatter.formatCurrencyNoDecimals(-1000.0));
    }

    @Test
    void testFormatPercentage() {
        // Test positive percentages
        assertEquals("15.50%", BudgetAmountFormatter.formatPercentage(15.5));
        assertEquals("100.00%", BudgetAmountFormatter.formatPercentage(100.0));
        assertEquals("0.50%", BudgetAmountFormatter.formatPercentage(0.5));
        
        // Test zero
        assertEquals("0.00%", BudgetAmountFormatter.formatPercentage(0.0));
        
        // Test negative percentages
        assertEquals("-5.25%", BudgetAmountFormatter.formatPercentage(-5.25));
    }

    @Test
    void testFormatPercentageOneDecimal() {
        // Test positive percentages
        assertEquals("15.5%", BudgetAmountFormatter.formatPercentageOneDecimal(15.5));
        assertEquals("100.0%", BudgetAmountFormatter.formatPercentageOneDecimal(100.0));
        assertEquals("0.5%", BudgetAmountFormatter.formatPercentageOneDecimal(0.5));
        
        // Test zero
        assertEquals("0.0%", BudgetAmountFormatter.formatPercentageOneDecimal(0.0));
        
        // Test negative percentages
        assertEquals("-5.3%", BudgetAmountFormatter.formatPercentageOneDecimal(-5.25));
    }

    @Test
    void testFormatPercentageChange() {
        // Test positive changes
        assertEquals("+5.25%", BudgetAmountFormatter.formatPercentageChange(5.25));
        assertEquals("+100.00%", BudgetAmountFormatter.formatPercentageChange(100.0));
        
        // Test zero
        assertEquals("+0.00%", BudgetAmountFormatter.formatPercentageChange(0.0));
        
        // Test negative changes
        assertEquals("-5.25%", BudgetAmountFormatter.formatPercentageChange(-5.25));
        assertEquals("-10.50%", BudgetAmountFormatter.formatPercentageChange(-10.5));
    }

    @Test
    void testFormatPercentageChangeOneDecimal() {
        // Test positive changes
        assertEquals("+5.3%", BudgetAmountFormatter.formatPercentageChangeOneDecimal(5.25));
        assertEquals("+100.0%", BudgetAmountFormatter.formatPercentageChangeOneDecimal(100.0));
        
        // Test zero
        assertEquals("+0.0%", BudgetAmountFormatter.formatPercentageChangeOneDecimal(0.0));
        
        // Test negative changes
        assertEquals("-5.3%", BudgetAmountFormatter.formatPercentageChangeOneDecimal(-5.25));
        assertEquals("-10.5%", BudgetAmountFormatter.formatPercentageChangeOneDecimal(-10.5));
    }

    @Test
    void testFormatChange() {
        // Test positive change
        String result = BudgetAmountFormatter.formatChange(5.25, 1234.5);
        assertTrue(result.contains("5.25%"));
        assertTrue(result.contains("1234.50"));
        
        // Test negative change
        result = BudgetAmountFormatter.formatChange(-10.5, -5000.0);
        assertTrue(result.contains("-10.50%"));
        assertTrue(result.contains("-5000.00"));
    }

    @Test
    void testFormatAmount() {
        // Test positive amounts
        assertEquals("1,000.0", BudgetAmountFormatter.formatAmount(1000.0));
        assertEquals("1,234.5", BudgetAmountFormatter.formatAmount(1234.5));
        assertEquals("10,000.0", BudgetAmountFormatter.formatAmount(10000.0));
        
        // Test zero
        assertEquals("0.0", BudgetAmountFormatter.formatAmount(0.0));
        
        // Test negative amounts
        assertEquals("-1,000.0", BudgetAmountFormatter.formatAmount(-1000.0));
        
        // Test large amounts
        assertEquals("1,000,000.0", BudgetAmountFormatter.formatAmount(1000000.0));
    }
}

