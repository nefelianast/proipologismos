package ui;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javafx.application.Platform;
import javafx.scene.chart.*;
import java.lang.reflect.Method;
import java.util.*;

class ChartsTest {

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

    @Test
    void testFillPieChartWithNullChart() {
        Map<String, Double> data = new HashMap<>();
        data.put("A", 100.0);
        assertDoesNotThrow(() -> Charts.fillPieChart(null, data, "Test"));
    }

    @Test
    void testFillPieChartWithNullData() {
        PieChart chart = new PieChart();
        assertDoesNotThrow(() -> Charts.fillPieChart(chart, null, "Test"));
    }

    @Test
    void testFillPieChartWithEmptyData() {
        PieChart chart = new PieChart();
        Map<String, Double> data = new HashMap<>();
        assertDoesNotThrow(() -> Charts.fillPieChart(chart, data, "Test"));
        assertEquals(0, chart.getData().size());
    }

    @Test
    void testFillPieChartFiltersNegativeValues() {
        PieChart chart = new PieChart();
        Map<String, Double> data = new HashMap<>();
        data.put("Positive", 100.0);
        data.put("Negative", -50.0);
        data.put("Zero", 0.0);
        
        Charts.fillPieChart(chart, data, "Test");
        
        assertEquals(1, chart.getData().size());
        assertTrue(chart.getData().get(0).getName().contains("Positive"));
        assertTrue(chart.getData().get(0).getName().contains("100"));
        assertTrue(chart.getData().get(0).getName().contains("%"));
    }

    @Test
    void testFillPieChartFiltersNullValues() {
        PieChart chart = new PieChart();
        Map<String, Double> data = new HashMap<>();
        data.put("Valid", 100.0);
        data.put("Null", null);
        
        Charts.fillPieChart(chart, data, "Test");
        
        assertEquals(1, chart.getData().size());
    }

    @Test
    void testFillPieChartCalculatesPercentages() {
        PieChart chart = new PieChart();
        Map<String, Double> data = new HashMap<>();
        data.put("A", 25.0);
        data.put("B", 75.0);
        
        Charts.fillPieChart(chart, data, "Test");
        
        assertEquals(2, chart.getData().size());
        String name1 = chart.getData().get(0).getName();
        String name2 = chart.getData().get(1).getName();
        assertTrue((name1.contains("25") && name1.contains("%")) || 
                   (name1.contains("75") && name1.contains("%")));
        assertTrue((name2.contains("25") && name2.contains("%")) || 
                   (name2.contains("75") && name2.contains("%")));
    }

    @Test
    void testLoadTopPieChartWithNullChart() {
        Map<String, Double> data = new HashMap<>();
        data.put("A", 100.0);
        assertDoesNotThrow(() -> Charts.loadTopPieChart(null, data, "Test", 5));
    }

    @Test
    void testLoadTopPieChartWithNullData() {
        PieChart chart = new PieChart();
        assertDoesNotThrow(() -> Charts.loadTopPieChart(chart, null, "Test", 5));
    }

    @Test
    void testLoadTopPieChartFiltersAndSorts() {
        PieChart chart = new PieChart();
        Map<String, Double> data = new HashMap<>();
        data.put("Low", 10.0);
        data.put("High", 100.0);
        data.put("Medium", 50.0);
        data.put("Negative", -20.0);
        data.put("Zero", 0.0);
        
        Charts.loadTopPieChart(chart, data, "Test", 3);
        
        assertEquals(3, chart.getData().size());
    }

    @Test
    void testLoadTopPieChartLimitsToTopN() {
        PieChart chart = new PieChart();
        Map<String, Double> data = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            data.put("Item" + i, (double) i * 10);
        }
        
        Charts.loadTopPieChart(chart, data, "Test", 5);
        
        assertEquals(5, chart.getData().size());
    }

    @Test
    void testTranslateCategoryName() throws Exception {
        Method method = Charts.class.getDeclaredMethod("translateCategoryName", String.class);
        method.setAccessible(true);
        
        assertEquals("Φόροι", method.invoke(null, "Taxes"));
        assertEquals("Κοινωνικές Εισφορές", method.invoke(null, "Social contributions"));
        assertEquals("Μεταβιβάσεις", method.invoke(null, "Transfers"));
        assertEquals("Αμοιβές Προσωπικού", method.invoke(null, "Employee benefits"));
        assertEquals("Unknown", method.invoke(null, "Unknown"));
    }

    @Test
    void testCalculateAppropriateTickUnitForYearComparison() throws Exception {
        Method method = Charts.class.getDeclaredMethod("calculateAppropriateTickUnitForYearComparison", double.class);
        method.setAccessible(true);
        
        double result1 = (Double) method.invoke(null, 1_500_000_000_000.0);
        assertEquals(100_000_000_000.0, result1, 0.01);
        
        double result2 = (Double) method.invoke(null, 150_000_000_000.0);
        assertEquals(10_000_000_000.0, result2, 0.01);
        
        double result3 = (Double) method.invoke(null, 15_000_000_000.0);
        assertEquals(1_000_000_000.0, result3, 0.01);
        
        double result4 = (Double) method.invoke(null, 1_500_000_000.0);
        assertEquals(100_000_000.0, result4, 0.01);
        
        double result5 = (Double) method.invoke(null, 150_000_000.0);
        assertEquals(10_000_000.0, result5, 0.01);
        
        double result6 = (Double) method.invoke(null, 15_000_000.0);
        assertEquals(1_000_000.0, result6, 0.01);
        
        double result7 = (Double) method.invoke(null, 1_000_000.0);
        assertTrue(result7 >= 100_000.0);
    }

    @Test
    void testLoadYearComparisonRevenueExpensesChartWithNullChart() {
        assertDoesNotThrow(() -> Charts.loadYearComparisonRevenueExpensesChart(
            null, 1000L, 800L, 1200L, 900L, 2023, 2024));
    }

    @Test
    void testLoadYearComparisonBalanceChartWithNullChart() {
        assertDoesNotThrow(() -> Charts.loadYearComparisonBalanceChart(
            null, 100L, 200L, 2023, 2024));
    }

    @Test
    void testLoadBarChartForTopCategoriesWithNullChart() {
        Map<String, Double> data = new HashMap<>();
        data.put("A", 100.0);
        assertDoesNotThrow(() -> Charts.loadBarChartForTopCategories(null, data, "Test", 5));
    }

    @Test
    void testLoadBarChartForTopCategoriesWithNullData() {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        assertDoesNotThrow(() -> Charts.loadBarChartForTopCategories(chart, null, "Test", 5));
    }

    @Test
    void testLoadBarChartForTopCategoriesFiltersNegativeValues() {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        Map<String, Double> data = new HashMap<>();
        data.put("Positive", 100.0);
        data.put("Negative", -50.0);
        data.put("Zero", 0.0);
        
        Charts.loadBarChartForTopCategories(chart, data, "Test", 5);
        
        assertEquals(1, chart.getData().size());
        assertEquals(1, chart.getData().get(0).getData().size());
    }

    @Test
    void testLoadBarChartForTopCategoriesLimitsToTopN() {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        Map<String, Double> data = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            data.put("Item" + i, (double) i * 10);
        }
        
        Charts.loadBarChartForTopCategories(chart, data, "Test", 6);
        
        assertEquals(1, chart.getData().size());
        assertEquals(6, chart.getData().get(0).getData().size());
    }

    @Test
    void testLoadBarChartForTopCategoriesMaxLimit() {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        Map<String, Double> data = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            data.put("Item" + i, (double) i * 10);
        }
        
        Charts.loadBarChartForTopCategories(chart, data, "Test", 10);
        
        assertEquals(1, chart.getData().size());
        assertTrue(chart.getData().get(0).getData().size() <= 6);
    }

    @Test
    void testLoadAreaChartWithNullChart() {
        BudgetData budgetData = BudgetData.getInstance();
        assertDoesNotThrow(() -> Charts.loadAreaChart(null, budgetData, "total_revenue", "Test"));
    }

    @Test
    void testLoadAreaChartWithNullBudgetData() {
        AreaChart<String, Number> chart = new AreaChart<>(new CategoryAxis(), new NumberAxis());
        assertDoesNotThrow(() -> Charts.loadAreaChart(chart, null, "total_revenue", "Test"));
    }

    @Test
    void testLoadStackedBarChartWithNullChart() {
        BudgetData budgetData = BudgetData.getInstance();
        assertDoesNotThrow(() -> Charts.loadStackedBarChart(null, budgetData, "revenue", "Test"));
    }

    @Test
    void testLoadStackedBarChartWithNullBudgetData() {
        StackedBarChart<String, Number> chart = new StackedBarChart<>(new CategoryAxis(), new NumberAxis());
        assertDoesNotThrow(() -> Charts.loadStackedBarChart(chart, null, "revenue", "Test"));
    }

    @Test
    void testLoadBalanceBarChartWithNullChart() {
        BudgetData budgetData = BudgetData.getInstance();
        assertDoesNotThrow(() -> Charts.loadBalanceBarChart(null, budgetData, "Test"));
    }

    @Test
    void testLoadBalanceBarChartWithNullBudgetData() {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        assertDoesNotThrow(() -> Charts.loadBalanceBarChart(chart, null, "Test"));
    }

    @Test
    void testLoadCombinationChartWithNullChart() {
        BudgetData budgetData = BudgetData.getInstance();
        assertDoesNotThrow(() -> Charts.loadCombinationChart(null, budgetData, "Test"));
    }

    @Test
    void testLoadCombinationChartWithNullBudgetData() {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        assertDoesNotThrow(() -> Charts.loadCombinationChart(chart, null, "Test"));
    }

    @Test
    void testLoadLineChartWithNullChart() {
        BudgetData budgetData = BudgetData.getInstance();
        assertDoesNotThrow(() -> Charts.loadLineChart(null, budgetData));
    }

    @Test
    void testLoadLineChartWithNullBudgetData() {
        LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        assertDoesNotThrow(() -> Charts.loadLineChart(chart, null));
    }

    @Test
    void testLoadOverviewTrendsChartWithNullChart() {
        BudgetData budgetData = BudgetData.getInstance();
        List<String> years = Arrays.asList("2023", "2024");
        assertDoesNotThrow(() -> Charts.loadOverviewTrendsChart(null, budgetData, years));
    }

    @Test
    void testLoadOverviewTrendsChartWithNullBudgetData() {
        LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        List<String> years = Arrays.asList("2023", "2024");
        assertDoesNotThrow(() -> Charts.loadOverviewTrendsChart(chart, null, years));
    }

    @Test
    void testLoadOverviewTrendsChartWithNullYears() {
        LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        BudgetData budgetData = BudgetData.getInstance();
        assertDoesNotThrow(() -> Charts.loadOverviewTrendsChart(chart, budgetData, null));
    }

    @Test
    void testLoadOverviewTrendsChartWithEmptyYears() {
        LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        BudgetData budgetData = BudgetData.getInstance();
        assertDoesNotThrow(() -> Charts.loadOverviewTrendsChart(chart, budgetData, new ArrayList<>()));
    }

    @Test
    void testLoadOverviewBalanceChartWithNullChart() {
        BudgetData budgetData = BudgetData.getInstance();
        List<String> years = Arrays.asList("2023", "2024");
        assertDoesNotThrow(() -> Charts.loadOverviewBalanceChart(null, budgetData, years));
    }

    @Test
    void testLoadOverviewBalanceChartWithNullBudgetData() {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        List<String> years = Arrays.asList("2023", "2024");
        assertDoesNotThrow(() -> Charts.loadOverviewBalanceChart(chart, null, years));
    }

    @Test
    void testLoadOverviewBalanceChartWithNullYears() {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        BudgetData budgetData = BudgetData.getInstance();
        assertDoesNotThrow(() -> Charts.loadOverviewBalanceChart(chart, budgetData, null));
    }

    @Test
    void testLoadOverviewBalanceChartWithEmptyYears() {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        BudgetData budgetData = BudgetData.getInstance();
        assertDoesNotThrow(() -> Charts.loadOverviewBalanceChart(chart, budgetData, new ArrayList<>()));
    }

    @Test
    void testLoadOverviewRevenuePieChartWithNullChart() {
        BudgetData budgetData = BudgetData.getInstance();
        assertDoesNotThrow(() -> Charts.loadOverviewRevenuePieChart(null, budgetData, 2025));
    }

    @Test
    void testLoadOverviewRevenuePieChartWithNullBudgetData() {
        PieChart chart = new PieChart();
        assertDoesNotThrow(() -> Charts.loadOverviewRevenuePieChart(chart, null, 2025));
    }

    @Test
    void testLoadOverviewExpensesPieChartWithNullChart() {
        BudgetData budgetData = BudgetData.getInstance();
        assertDoesNotThrow(() -> Charts.loadOverviewExpensesPieChart(null, budgetData, 2025));
    }

    @Test
    void testLoadOverviewExpensesPieChartWithNullBudgetData() {
        PieChart chart = new PieChart();
        assertDoesNotThrow(() -> Charts.loadOverviewExpensesPieChart(chart, null, 2025));
    }

    @Test
    void testLoadYearComparisonRevenuePieChartWithNullChart() {
        Map<String, Comparisons.ComparisonData> revenues = new HashMap<>();
        Map<String, Comparisons.ComparisonData> expenses = new HashMap<>();
        Map<String, Comparisons.ComparisonData> administrations = new HashMap<>();
        Map<String, Comparisons.ComparisonData> ministries = new HashMap<>();
        Comparisons.ComparisonResults results = new Comparisons.ComparisonResults(
            revenues, expenses, administrations, ministries,
            0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L
        );
        assertDoesNotThrow(() -> Charts.loadYearComparisonRevenuePieChart(null, results, 2023, true));
    }

    @Test
    void testLoadYearComparisonRevenuePieChartWithNullResults() {
        PieChart chart = new PieChart();
        assertDoesNotThrow(() -> Charts.loadYearComparisonRevenuePieChart(chart, null, 2023, true));
    }

    @Test
    void testLoadYearComparisonExpensesPieChartWithNullChart() {
        Map<String, Comparisons.ComparisonData> revenues = new HashMap<>();
        Map<String, Comparisons.ComparisonData> expenses = new HashMap<>();
        Map<String, Comparisons.ComparisonData> administrations = new HashMap<>();
        Map<String, Comparisons.ComparisonData> ministries = new HashMap<>();
        Comparisons.ComparisonResults results = new Comparisons.ComparisonResults(
            revenues, expenses, administrations, ministries,
            0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L
        );
        assertDoesNotThrow(() -> Charts.loadYearComparisonExpensesPieChart(null, results, 2023, true));
    }

    @Test
    void testLoadYearComparisonExpensesPieChartWithNullResults() {
        PieChart chart = new PieChart();
        assertDoesNotThrow(() -> Charts.loadYearComparisonExpensesPieChart(chart, null, 2023, true));
    }
}
