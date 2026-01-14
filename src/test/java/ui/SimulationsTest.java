package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimulationsTest {
    
    @Test
    void testSimulateRevenueIncrease() {
        String result = Simulations.simulateRevenueIncrease(1000.0, 800.0, 10.0);
        
        assertNotNull(result);
        assertTrue(result.contains("Σενάριο"));
        assertTrue(result.contains("Αύξηση εσόδων"));
        assertTrue(result.contains("10.00"));
        assertTrue(result.contains("1100"));
        assertTrue(result.contains("200"));
    }
    
    @Test
    void testSimulateRevenueIncreaseZeroPercent() {
        String result = Simulations.simulateRevenueIncrease(1000.0, 800.0, 0.0);
        
        assertNotNull(result);
        assertTrue(result.contains("0.00"));
        assertTrue(result.contains("1000"));
    }
    
    @Test
    void testSimulateRevenueIncreaseNegativePercent() {
        String result = Simulations.simulateRevenueIncrease(1000.0, 800.0, -5.0);
        
        assertNotNull(result);
        assertTrue(result.contains("-5.00"));
        assertTrue(result.contains("950"));
    }
    
    @Test
    void testSimulateExpenseDecrease() {
        String result = Simulations.simulateExpenseDecrease(1000.0, 800.0, 10.0);
        
        assertNotNull(result);
        assertTrue(result.contains("Σενάριο"));
        assertTrue(result.contains("Μείωση δαπανών"));
        assertTrue(result.contains("10.00"));
        assertTrue(result.contains("720"));
        assertTrue(result.contains("280"));
    }
    
    @Test
    void testSimulateExpenseDecreaseZeroPercent() {
        String result = Simulations.simulateExpenseDecrease(1000.0, 800.0, 0.0);
        
        assertNotNull(result);
        assertTrue(result.contains("0.00"));
        assertTrue(result.contains("800"));
    }
    
    @Test
    void testSimulateCombinedScenario() {
        String result = Simulations.simulateCombinedScenario(
            1000.0, 800.0, 10.0, -5.0
        );
        
        assertNotNull(result);
        assertTrue(result.contains("Σενάριο"));
        assertTrue(result.contains("Συνδυασμένη Αλλαγή"));
        assertTrue(result.contains("10.00"));
        assertTrue(result.contains("-5.00"));
        assertTrue(result.contains("1100"));
        assertTrue(result.contains("760"));
    }
    
    @Test
    void testSimulateCombinedScenarioBothPositive() {
        String result = Simulations.simulateCombinedScenario(
            1000.0, 800.0, 5.0, 3.0
        );
        
        assertNotNull(result);
        assertTrue(result.contains("1050"));
        assertTrue(result.contains("824"));
    }
    
    @Test
    void testSimulateCombinedScenarioBothNegative() {
        String result = Simulations.simulateCombinedScenario(
            1000.0, 800.0, -2.0, -3.0
        );
        
        assertNotNull(result);
        assertTrue(result.contains("980"));
        assertTrue(result.contains("776"));
    }
    
    @Test
    void testSimulateFutureYear() {
        String result = Simulations.simulateFutureYear(
            2023, 2025, 1000.0, 800.0, 50.0, 30.0
        );
        
        assertNotNull(result);
        assertTrue(result.contains("2025"));
        assertTrue(result.contains("2023"));
        assertTrue(result.contains("1100"));
        assertTrue(result.contains("860"));
    }
    
    @Test
    void testSimulateFutureYearInvalidYears() {
        String result = Simulations.simulateFutureYear(
            2025, 2023, 1000.0, 800.0, 50.0, 30.0
        );
        
        assertNotNull(result);
        assertTrue(result.contains("μεγαλύτερο"));
    }
    
    @Test
    void testSimulateFutureYearSameYear() {
        String result = Simulations.simulateFutureYear(
            2023, 2023, 1000.0, 800.0, 50.0, 30.0
        );
        
        assertNotNull(result);
        assertTrue(result.contains("μεγαλύτερο"));
    }
    
    @Test
    void testSimulateFutureYearNegativeTrends() {
        String result = Simulations.simulateFutureYear(
            2023, 2025, 1000.0, 800.0, -20.0, -10.0
        );
        
        assertNotNull(result);
        assertTrue(result.contains("960"));
        assertTrue(result.contains("780"));
    }
    
    @Test
    void testSimulateCategoryExpenseReduction() {
        String result = Simulations.simulateCategoryExpenseReduction(
            "Test Category", 100.0, 800.0, 1000.0, 20.0
        );
        
        assertNotNull(result);
        assertTrue(result.contains("Σενάριο"));
        assertTrue(result.contains("Μείωση Δαπανών Κατηγορίας"));
        assertTrue(result.contains("Test Category"));
        assertTrue(result.contains("20.00"));
        assertTrue(result.contains("80"));
        assertTrue(result.contains("700"));
    }
    
    @Test
    void testSimulateCategoryExpenseReductionZeroPercent() {
        String result = Simulations.simulateCategoryExpenseReduction(
            "Test Category", 100.0, 800.0, 1000.0, 0.0
        );
        
        assertNotNull(result);
        assertTrue(result.contains("100"));
        assertTrue(result.contains("800"));
    }
    
    @Test
    void testSimulateCategoryExpenseReductionFullReduction() {
        String result = Simulations.simulateCategoryExpenseReduction(
            "Test Category", 100.0, 800.0, 1000.0, 100.0
        );
        
        assertNotNull(result);
        assertTrue(result.contains("0"));
        assertTrue(result.contains("700"));
    }
    
    @Test
    void testAllSimulationsProduceValidOutput() {
        String result1 = Simulations.simulateRevenueIncrease(1000.0, 800.0, 10.0);
        assertFalse(result1.isEmpty());
        
        String result2 = Simulations.simulateExpenseDecrease(1000.0, 800.0, 10.0);
        assertFalse(result2.isEmpty());
        
        String result3 = Simulations.simulateCombinedScenario(1000.0, 800.0, 5.0, -3.0);
        assertFalse(result3.isEmpty());
        
        String result4 = Simulations.simulateFutureYear(2023, 2024, 1000.0, 800.0, 50.0, 30.0);
        assertFalse(result4.isEmpty());
        
        String result5 = Simulations.simulateCategoryExpenseReduction("Test", 100.0, 800.0, 1000.0, 10.0);
        assertFalse(result5.isEmpty());
    }
}
