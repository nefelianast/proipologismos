package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AmountFormatter utility class.
 */
class AmountFormatterTest {

    @Test
    void testFormatCurrency() {
        assertEquals("€1.000,0", AmountFormatter.formatCurrency(1000.0));
        assertEquals("€1.234,5", AmountFormatter.formatCurrency(1234.5));
        assertEquals("€10.000,0", AmountFormatter.formatCurrency(10000.0));
        
        // Test zero
        assertEquals("€0,0", AmountFormatter.formatCurrency(0.0));
        
        // Test negative amounts
        assertEquals("€-1.000,0", AmountFormatter.formatCurrency(-1000.0));
        
        // Test large amounts
        assertEquals("€1.000.000,0", AmountFormatter.formatCurrency(1000000.0));
    }

    @Test
    void testFormatCurrencyNoDecimals() {
        // Test positive amounts (Greek locale: . for thousands)
        assertEquals("€1.000", AmountFormatter.formatCurrencyNoDecimals(1000.0));
        assertEquals("€1.234", AmountFormatter.formatCurrencyNoDecimals(1234.5));
        assertEquals("€10.000", AmountFormatter.formatCurrencyNoDecimals(10000.0));
        
        // Test zero
        assertEquals("€0", AmountFormatter.formatCurrencyNoDecimals(0.0));
        
        // Test negative amounts
        assertEquals("€-1.000", AmountFormatter.formatCurrencyNoDecimals(-1000.0));
    }

    @Test
    void testFormatPercentage() {
        // Test positive percentages (Greek locale: , for decimals)
        assertEquals("15,50%", AmountFormatter.formatPercentage(15.5));
        assertEquals("100,00%", AmountFormatter.formatPercentage(100.0));
        assertEquals("0,50%", AmountFormatter.formatPercentage(0.5));
        
        // Test zero
        assertEquals("0,00%", AmountFormatter.formatPercentage(0.0));
        
        // Test negative percentages
        assertEquals("-5,25%", AmountFormatter.formatPercentage(-5.25));
    }

    @Test
    void testFormatPercentageOneDecimal() {
        // Test positive percentages (Greek locale: , for decimals)
        assertEquals("15,5%", AmountFormatter.formatPercentageOneDecimal(15.5));
        assertEquals("100,0%", AmountFormatter.formatPercentageOneDecimal(100.0));
        assertEquals("0,5%", AmountFormatter.formatPercentageOneDecimal(0.5));
        
        // Test zero
        assertEquals("0,0%", AmountFormatter.formatPercentageOneDecimal(0.0));
        
        // Test negative percentages
        assertEquals("-5,3%", AmountFormatter.formatPercentageOneDecimal(-5.25));
    }

    @Test
    void testFormatPercentageChange() {
        // Test positive changes (Greek locale: , for decimals)
        assertEquals("+5,25%", AmountFormatter.formatPercentageChange(5.25));
        assertEquals("+100,00%", AmountFormatter.formatPercentageChange(100.0));
        
        // Test zero
        assertEquals("+0,00%", AmountFormatter.formatPercentageChange(0.0));
        
        // Test negative changes
        assertEquals("-5,25%", AmountFormatter.formatPercentageChange(-5.25));
        assertEquals("-10,50%", AmountFormatter.formatPercentageChange(-10.5));
    }

    @Test
    void testFormatPercentageChangeOneDecimal() {
        // Test positive changes (Greek locale: , for decimals)
        assertEquals("+5,3%", AmountFormatter.formatPercentageChangeOneDecimal(5.25));
        assertEquals("+100,0%", AmountFormatter.formatPercentageChangeOneDecimal(100.0));
        
        // Test zero
        assertEquals("+0,0%", AmountFormatter.formatPercentageChangeOneDecimal(0.0));
        
        // Test negative changes
        assertEquals("-5,3%", AmountFormatter.formatPercentageChangeOneDecimal(-5.25));
        assertEquals("-10,5%", AmountFormatter.formatPercentageChangeOneDecimal(-10.5));
    }

    @Test
    void testFormatChange() {
        // Test positive change (Greek locale: , for decimals)
        String result = AmountFormatter.formatChange(5.25, 1234.5);
        assertTrue(result.contains("5,25%"));
        assertTrue(result.contains("1234,50"));
        
        // Test negative change
        result = AmountFormatter.formatChange(-10.5, -5000.0);
        assertTrue(result.contains("-10,50%"));
        assertTrue(result.contains("-5000,00"));
    }

    @Test
    void testFormatAmount() {
        // Test positive amounts (Greek locale: . for thousands, , for decimals)
        assertEquals("1.000,0", AmountFormatter.formatAmount(1000.0));
        assertEquals("1.234,5", AmountFormatter.formatAmount(1234.5));
        assertEquals("10.000,0", AmountFormatter.formatAmount(10000.0));
        
        // Test zero
        assertEquals("0,0", AmountFormatter.formatAmount(0.0));
        
        // Test negative amounts
        assertEquals("-1.000,0", AmountFormatter.formatAmount(-1000.0));
        
        // Test large amounts
        assertEquals("1.000.000,0", AmountFormatter.formatAmount(1000000.0));
    }
}
