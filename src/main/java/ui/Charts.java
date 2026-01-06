package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.application.Platform;
import java.util.*;
import java.util.stream.Collectors;

// κλάση για τη δημιουργία και διαχείριση γραφημάτων
public class Charts {

    // γεμίζει ένα pie chart με δεδομένα από ένα map
    public static void fillPieChart(PieChart chart, Map<String, Double> data, String title) {
        if (chart == null) return;
        
        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
        // προσθήκη δεδομένων στο chart (μόνο θετικά ποσά)
        data.forEach((k, v) -> {
            if (v > 0) list.add(new PieChart.Data(k, v));
        });
        chart.setData(list);
        chart.setTitle(title);
    }

    // φορτώνει ένα line chart με ιστορικά δεδομένα εσόδων και δαπανών
    public static void loadLineChart(LineChart<String, Number> lineChart, BudgetData budgetData) {
        if (lineChart == null || budgetData == null) return;
        
        lineChart.getData().clear();
        
        // δημιουργία σειράς για έσοδα
        XYChart.Series<String, Number> revSeries = new XYChart.Series<>();
        revSeries.setName("Έσοδα");
        
        // δημιουργία σειράς για έξοδα
        XYChart.Series<String, Number> expSeries = new XYChart.Series<>();
        expSeries.setName("Έξοδα");

        int[] years = {2023, 2024, 2025, 2026};
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        
        // συλλογή δεδομένων για κάθε έτος
        for (int y : years) {
            double rev = budgetData.getTotalAmount(y, "total_revenue");
            double exp = budgetData.getTotalAmount(y, "total_expenses");
            
            // εύρεση ελάχιστης και μέγιστης τιμής για τον άξονα y
            minValue = Math.min(minValue, Math.min(rev, exp));
            maxValue = Math.max(maxValue, Math.max(rev, exp));
            
            revSeries.getData().add(new XYChart.Data<>(String.valueOf(y), rev));
            expSeries.getData().add(new XYChart.Data<>(String.valueOf(y), exp));
        }
        
        @SuppressWarnings("unchecked")
        ObservableList<XYChart.Series<String, Number>> chartData = FXCollections.observableArrayList(revSeries, expSeries);
        lineChart.getData().addAll(chartData);
        
        // εφαρμογή χρωμάτων
        Platform.runLater(() -> {
            lineChart.applyCss();
            lineChart.layout();
            
            // ορισμός στιλ και χρωμάτων

            if (revSeries.getNode() != null) {
                revSeries.getNode().setStyle("-fx-stroke: #22c55e; -fx-stroke-width: 2px;");
            }
            if (expSeries.getNode() != null) {
                expSeries.getNode().setStyle("-fx-stroke: #ef4444; -fx-stroke-width: 2px;");
            }
            
            
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
        
        // ορισμός άξονα y με στρογγυλοποίηση τιμών
        if (lineChart.getYAxis() instanceof NumberAxis) {
            NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
            
            // στρογγυλοποίηση
            double tickUnit = 100_000_000_000.0; 
            double lowerBound = Math.floor(minValue / tickUnit) * tickUnit;
            double upperBound = Math.ceil(maxValue / tickUnit) * tickUnit;
            
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(lowerBound);
            yAxis.setUpperBound(upperBound);
            yAxis.setTickUnit(tickUnit);
        }
    }

    // φορτώνει ένα bar chart με τις κορυφαίες κατηγορίες (έσοδα ή δαπάνες)
    public static void loadBarChartForTopCategories(BarChart<String, Number> barChart, Map<String, Double> data, String title, int topN) {
        if (barChart == null || data == null) return;
        
        barChart.getData().clear();
        barChart.setTitle(title);
        
        // ταξινόμηση κατά τιμή φθίνουσα και επιλογή των top N
        List<Map.Entry<String, Double>> sorted = data.entrySet().stream()
            .filter(e -> e.getValue() > 0)
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(topN)
            .collect(Collectors.toList());
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ποσό");
        
        // προσθήκη δεδομένων στο chart
        for (Map.Entry<String, Double> entry : sorted) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        barChart.getData().add(series);
    }

    // φορτώνει ένα area chart με ιστορικά δεδομένα για έσοδα ή δαπάνες
    public static void loadAreaChart(AreaChart<String, Number> areaChart, BudgetData budgetData, String dataType, String title) {
        if (areaChart == null || budgetData == null) return;
        
        areaChart.getData().clear();
        areaChart.setTitle(title);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(dataType.equals("total_revenue") ? "Έσοδα" : "Δαπάνες");
        
        int[] years = {2023, 2024, 2025, 2026};
        // συλλογή δεδομένων για κάθε έτος
        for (int y : years) {
                double value = budgetData.getTotalAmount(y, dataType);
            series.getData().add(new XYChart.Data<>(String.valueOf(y), value));
        }
        
        areaChart.getData().add(series);
    }

    // φορτώνει ένα stacked bar chart που δείχνει τη σύνθεση εσόδων ή δαπανών σε πολλαπλά έτη
    public static void loadStackedBarChart(StackedBarChart<String, Number> stackedBarChart, BudgetData budgetData, String dataType, String title) {
        if (stackedBarChart == null || budgetData == null) return;
        
        stackedBarChart.getData().clear();
        stackedBarChart.setTitle(title);
        
        int[] years = {2023, 2024, 2025, 2026};
        Map<String, Double> allCategories = new HashMap<>();
        
        // συλλογή όλων των κατηγοριών σε όλα τα έτη
        for (int y : years) {
            Map<String, Double> yearData = dataType.equals("revenue") 
                ? budgetData.getRevenueBreakdownForGraphs(y)
                : budgetData.getExpenseBreakdownForGraphs(y);
            allCategories.putAll(yearData);
        }
        
        // δημιουργία μιας σειράς για κάθε κατηγορία
        for (String category : allCategories.keySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(category);
            
            // προσθήκη δεδομένων για κάθε έτος
            for (int y : years) {
                Map<String, Double> yearData = dataType.equals("revenue")
                    ? budgetData.getRevenueBreakdownForGraphs(y)
                    : budgetData.getExpenseBreakdownForGraphs(y);
                double value = yearData.getOrDefault(category, 0.0);
                series.getData().add(new XYChart.Data<>(String.valueOf(y), value));
            }
            
            stackedBarChart.getData().add(series);
        }
    }

    // φορτώνει ένα bar chart με τα κορυφαία υπουργεία
    public static void loadBarChartForMinistries(BarChart<String, Number> barChart, Map<String, Double> data, String title, int topN) {
        loadBarChartForTopCategories(barChart, data, title, topN);
    }

    // φορτώνει ένα bar chart που δείχνει το ισοζύγιο για πολλαπλά έτη
    public static void loadBalanceBarChart(BarChart<String, Number> barChart, BudgetData budgetData, String title) {
        if (barChart == null || budgetData == null) return;
        
        barChart.getData().clear();
        barChart.setTitle(title);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ισοζύγιο");
        
        int[] years = {2023, 2024, 2025, 2026};
        // συλλογή δεδομένων ισοζυγίου για κάθε έτος
        for (int y : years) {
                double balance = budgetData.getBalance(y);
            series.getData().add(new XYChart.Data<>(String.valueOf(y), balance));
        }
        
        barChart.getData().add(series);
    }

    // φορτώνει ένα bar chart που συγκρίνει έσοδα με δαπάνες για πολλαπλά έτη
    @SuppressWarnings("unchecked")
    public static void loadCombinationChart(BarChart<String, Number> barChart, BudgetData budgetData, String title) {
        if (barChart == null || budgetData == null) return;
        
        barChart.getData().clear();
        barChart.setTitle(title);
        
        // δημιουργία σειράς για έσοδα
        XYChart.Series<String, Number> revSeries = new XYChart.Series<>();
        revSeries.setName("Έσοδα");
        
        // δημιουργία σειράς για δαπάνες
        XYChart.Series<String, Number> expSeries = new XYChart.Series<>();
        expSeries.setName("Δαπάνες");
        
        int[] years = {2023, 2024, 2025, 2026};
        // συλλογή δεδομένων για κάθε έτος
        for (int y : years) {
            double rev = budgetData.getTotalAmount(y, "total_revenue");
            double exp = budgetData.getTotalAmount(y, "total_expenses");
            
            revSeries.getData().add(new XYChart.Data<>(String.valueOf(y), rev));
            expSeries.getData().add(new XYChart.Data<>(String.valueOf(y), exp));
        }
        
        barChart.getData().addAll(revSeries, expSeries);
    }
}
