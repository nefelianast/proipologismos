package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.application.Platform;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for creating and managing charts.
 * Provides utility methods for filling pie charts and loading line charts with budget data.
 */
public class ChartService {

    /**
     * Fills a pie chart with data from a map.
     * 
     * @param chart The pie chart to fill
     * @param data The data map (key = category name, value = amount)
     * @param title The title for the chart
     */
    public static void fillPieChart(PieChart chart, Map<String, Double> data, String title) {
        if (chart == null) return;
        
        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
        data.forEach((k, v) -> {
            if (v > 0) list.add(new PieChart.Data(k, v));
        });
        chart.setData(list);
        chart.setTitle(title);
    }

    /**
     * Loads a line chart with historical revenue and expense data.
     * 
     * @param lineChart The line chart to populate
     * @param dataService The budget data service to retrieve data from
     */
    public static void loadLineChart(LineChart<String, Number> lineChart, BudgetDataService dataService) {
        if (lineChart == null || dataService == null) return;
        
        lineChart.getData().clear();
        
        XYChart.Series<String, Number> revSeries = new XYChart.Series<>();
        revSeries.setName("Έσοδα");
        
        XYChart.Series<String, Number> expSeries = new XYChart.Series<>();
        expSeries.setName("Έξοδα");

        int[] years = {2023, 2024, 2025, 2026};
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        
        for (int y : years) {
            double rev = dataService.getTotalAmount(y, "total_revenue");
            double exp = dataService.getTotalAmount(y, "total_expenses");
            
            minValue = Math.min(minValue, Math.min(rev, exp));
            maxValue = Math.max(maxValue, Math.max(rev, exp));
            
            revSeries.getData().add(new XYChart.Data<>(String.valueOf(y), rev));
            expSeries.getData().add(new XYChart.Data<>(String.valueOf(y), exp));
        }
        
        @SuppressWarnings("unchecked")
        ObservableList<XYChart.Series<String, Number>> chartData = FXCollections.observableArrayList(revSeries, expSeries);
        lineChart.getData().addAll(chartData);
        
        // Apply colors after chart is rendered
        Platform.runLater(() -> {
            lineChart.applyCss();
            lineChart.layout();
            
            // Set colors for series lines - green for revenues, red for expenses
            if (revSeries.getNode() != null) {
                revSeries.getNode().setStyle("-fx-stroke: #22c55e; -fx-stroke-width: 2px;");
            }
            if (expSeries.getNode() != null) {
                expSeries.getNode().setStyle("-fx-stroke: #ef4444; -fx-stroke-width: 2px;");
            }
            
            // Style data points (nodes) - green for revenues, red for expenses
            for (XYChart.Data<String, Number> data : revSeries.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-background-color: #22c55e, white; -fx-background-radius: 4px; -fx-padding: 4px;");
                }
            }
            for (XYChart.Data<String, Number> data : expSeries.getData()) {
                if (data.getNode() != null) {
                    data.getNode().setStyle("-fx-background-color: #ef4444, white; -fx-background-radius: 4px; -fx-padding: 4px;");
                }
            }
        });
        
        // Set y-axis with rounded bounds and clean tick units (e.g., 700, 800, 900 billions)
        if (lineChart.getYAxis() instanceof NumberAxis) {
            NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
            
            // Round to nearest 100 billion (100.000.000.000)
            double tickUnit = 100_000_000_000.0; // 100 billions
            double lowerBound = Math.floor(minValue / tickUnit) * tickUnit;
            double upperBound = Math.ceil(maxValue / tickUnit) * tickUnit;
            
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(lowerBound);
            yAxis.setUpperBound(upperBound);
            yAxis.setTickUnit(tickUnit);
        }
    }

    /**
     * Loads a bar chart with top categories (revenues or expenses).
     * 
     * @param barChart The bar chart to populate
     * @param data The data map (key = category name, value = amount)
     * @param title The title for the chart
     * @param topN Number of top categories to show (default 10)
     */
    public static void loadBarChartForTopCategories(BarChart<String, Number> barChart, Map<String, Double> data, String title, int topN) {
        if (barChart == null || data == null) return;
        
        barChart.getData().clear();
        barChart.setTitle(title);
        
        // Sort by value descending and take top N
        List<Map.Entry<String, Double>> sorted = data.entrySet().stream()
            .filter(e -> e.getValue() > 0)
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(topN)
            .collect(Collectors.toList());
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ποσό");
        
        for (Map.Entry<String, Double> entry : sorted) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        barChart.getData().add(series);
    }

    /**
     * Loads an area chart with historical data for revenues or expenses.
     * 
     * @param areaChart The area chart to populate
     * @param dataService The budget data service
     * @param dataType "total_revenue" or "total_expenses"
     * @param title The title for the chart
     */
    public static void loadAreaChart(AreaChart<String, Number> areaChart, BudgetDataService dataService, String dataType, String title) {
        if (areaChart == null || dataService == null) return;
        
        areaChart.getData().clear();
        areaChart.setTitle(title);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(dataType.equals("total_revenue") ? "Έσοδα" : "Δαπάνες");
        
        int[] years = {2023, 2024, 2025, 2026};
        for (int y : years) {
            double value = dataService.getTotalAmount(y, dataType);
            series.getData().add(new XYChart.Data<>(String.valueOf(y), value));
        }
        
        areaChart.getData().add(series);
    }

    /**
     * Loads a stacked bar chart showing composition of revenues or expenses across multiple years.
     * 
     * @param stackedBarChart The stacked bar chart to populate
     * @param dataService The budget data service
     * @param dataType "revenue" or "expense"
     * @param title The title for the chart
     */
    public static void loadStackedBarChart(StackedBarChart<String, Number> stackedBarChart, BudgetDataService dataService, String dataType, String title) {
        if (stackedBarChart == null || dataService == null) return;
        
        stackedBarChart.getData().clear();
        stackedBarChart.setTitle(title);
        
        int[] years = {2023, 2024, 2025, 2026};
        Map<String, Double> allCategories = new HashMap<>();
        
        // Get all categories across all years
        for (int y : years) {
            Map<String, Double> yearData = dataType.equals("revenue") 
                ? dataService.getRevenueBreakdownForGraphs(y)
                : dataService.getExpenseBreakdownForGraphs(y);
            allCategories.putAll(yearData);
        }
        
        // Create a series for each category
        for (String category : allCategories.keySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(category);
            
            for (int y : years) {
                Map<String, Double> yearData = dataType.equals("revenue")
                    ? dataService.getRevenueBreakdownForGraphs(y)
                    : dataService.getExpenseBreakdownForGraphs(y);
                double value = yearData.getOrDefault(category, 0.0);
                series.getData().add(new XYChart.Data<>(String.valueOf(y), value));
            }
            
            stackedBarChart.getData().add(series);
        }
    }

    /**
     * Loads a bar chart with top ministries.
     * 
     * @param barChart The bar chart to populate
     * @param data The ministries data map
     * @param title The title for the chart
     * @param topN Number of top ministries to show (default 10)
     */
    public static void loadBarChartForMinistries(BarChart<String, Number> barChart, Map<String, Double> data, String title, int topN) {
        loadBarChartForTopCategories(barChart, data, title, topN);
    }

    /**
     * Loads a bar chart showing balance (surplus/deficit) for multiple years.
     * 
     * @param barChart The bar chart to populate
     * @param dataService The budget data service
     * @param title The title for the chart
     */
    public static void loadBalanceBarChart(BarChart<String, Number> barChart, BudgetDataService dataService, String title) {
        if (barChart == null || dataService == null) return;
        
        barChart.getData().clear();
        barChart.setTitle(title);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ισοζύγιο");
        
        int[] years = {2023, 2024, 2025, 2026};
        for (int y : years) {
            double balance = dataService.getBalance(y);
            series.getData().add(new XYChart.Data<>(String.valueOf(y), balance));
        }
        
        barChart.getData().add(series);
    }

    /**
     * Loads a bar chart comparing revenues vs expenses for multiple years.
     * 
     * @param barChart The bar chart to populate
     * @param dataService The budget data service
     * @param title The title for the chart
     */
    public static void loadCombinationChart(BarChart<String, Number> barChart, BudgetDataService dataService, String title) {
        if (barChart == null || dataService == null) return;
        
        barChart.getData().clear();
        barChart.setTitle(title);
        
        XYChart.Series<String, Number> revSeries = new XYChart.Series<>();
        revSeries.setName("Έσοδα");
        
        XYChart.Series<String, Number> expSeries = new XYChart.Series<>();
        expSeries.setName("Δαπάνες");
        
        int[] years = {2023, 2024, 2025, 2026};
        for (int y : years) {
            double rev = dataService.getTotalAmount(y, "total_revenue");
            double exp = dataService.getTotalAmount(y, "total_expenses");
            
            revSeries.getData().add(new XYChart.Data<>(String.valueOf(y), rev));
            expSeries.getData().add(new XYChart.Data<>(String.valueOf(y), exp));
        }
        
        barChart.getData().addAll(revSeries, expSeries);
    }
}
