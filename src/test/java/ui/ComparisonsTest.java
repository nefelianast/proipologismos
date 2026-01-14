package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

class ComparisonsTest {
    
    @Test
    void testComparisonDataConstructor() {
        Comparisons.ComparisonData data = new Comparisons.ComparisonData(
            "Test Category", 1000L, 1200L
        );
        
        assertEquals("Test Category", data.getCategoryName());
        assertEquals(1000L, data.getYear1Value());
        assertEquals(1200L, data.getYear2Value());
    }
    
    @Test
    void testComparisonDataGetDifference() {
        Comparisons.ComparisonData data = new Comparisons.ComparisonData(
            "Test", 1000L, 1200L
        );
        
        assertEquals(200L, data.getDifference());
    }
    
    @Test
    void testComparisonDataGetDifferenceNegative() {
        Comparisons.ComparisonData data = new Comparisons.ComparisonData(
            "Test", 1200L, 1000L
        );
        
        assertEquals(-200L, data.getDifference());
    }
    
    @Test
    void testComparisonDataGetPercentageChangeAsString() {
        Comparisons.ComparisonData data1 = new Comparisons.ComparisonData(
            "Test", 1000L, 1200L
        );
        String result1 = data1.getPercentageChangeAsString();
        assertTrue(result1.contains("20.00"));
        
        Comparisons.ComparisonData data2 = new Comparisons.ComparisonData(
            "Test", 1000L, 800L
        );
        String result2 = data2.getPercentageChangeAsString();
        assertTrue(result2.contains("-20.00"));
    }
    
    @Test
    void testComparisonDataGetPercentageChangeAsStringZeroYear1() {
        Comparisons.ComparisonData data1 = new Comparisons.ComparisonData(
            "Test", 0L, 1000L
        );
        assertEquals("Νέο", data1.getPercentageChangeAsString());
        
        Comparisons.ComparisonData data2 = new Comparisons.ComparisonData(
            "Test", 0L, 0L
        );
        assertEquals("0.00%", data2.getPercentageChangeAsString());
    }
    
    @Test
    void testComparisonResultsConstructor() {
        Map<String, Comparisons.ComparisonData> revenues = new HashMap<>();
        Map<String, Comparisons.ComparisonData> expenses = new HashMap<>();
        Map<String, Comparisons.ComparisonData> administrations = new HashMap<>();
        Map<String, Comparisons.ComparisonData> ministries = new HashMap<>();
        
        Comparisons.ComparisonResults results = new Comparisons.ComparisonResults(
            revenues, expenses, administrations, ministries,
            100L, 200L,
            1000L, 2000L,
            500L, 1000L,
            300L, 600L,
            200L, 400L
        );
        
        assertNotNull(results.getRevenues());
        assertNotNull(results.getExpenses());
        assertNotNull(results.getAdministrations());
        assertNotNull(results.getMinistries());
        assertEquals(100L, results.getBudgetResult1());
        assertEquals(200L, results.getBudgetResult2());
        assertEquals(1000L, results.getTotalRevenueSummary1());
        assertEquals(2000L, results.getTotalRevenueSummary2());
        assertEquals(500L, results.getTotalExpensesSummary1());
        assertEquals(1000L, results.getTotalExpensesSummary2());
        assertEquals(300L, results.getTotalMinistriesSummary1());
        assertEquals(600L, results.getTotalMinistriesSummary2());
        assertEquals(200L, results.getTotalDASummary1());
        assertEquals(400L, results.getTotalDASummary2());
    }
    
    @Test
    void testComparisonResultsGetters() {
        Map<String, Comparisons.ComparisonData> revenues = new HashMap<>();
        revenues.put("Taxes", new Comparisons.ComparisonData("Taxes", 100L, 120L));
        
        Map<String, Comparisons.ComparisonData> expenses = new HashMap<>();
        expenses.put("Employee benefits", new Comparisons.ComparisonData("Employee benefits", 50L, 60L));
        
        Map<String, Comparisons.ComparisonData> administrations = new HashMap<>();
        Map<String, Comparisons.ComparisonData> ministries = new HashMap<>();
        
        Comparisons.ComparisonResults results = new Comparisons.ComparisonResults(
            revenues, expenses, administrations, ministries,
            100L, 200L, 1000L, 2000L,
            500L, 1000L, 300L, 600L, 200L, 400L
        );
        
        assertEquals(1, results.getRevenues().size());
        assertEquals(1, results.getExpenses().size());
        assertTrue(results.getRevenues().containsKey("Taxes"));
        assertTrue(results.getExpenses().containsKey("Employee benefits"));
    }
}
