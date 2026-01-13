package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.*;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.*;
import java.util.stream.Collectors;

// κλάση για τη δημιουργία και διαχείριση γραφημάτων
public class Charts {

    // γεμίζει ένα pie chart με δεδομένα από ένα map
    public static void fillPieChart(PieChart chart, Map<String, Double> data, String title) {
        if (chart == null || data == null) return;
        
        // Υπολογισμός συνολικού αθροίσματος για ποσοστά
        double total = data.values().stream()
            .filter(v -> v != null && v > 0)
            .mapToDouble(Double::doubleValue)
            .sum();
        
        ObservableList<PieChart.Data> list = FXCollections.observableArrayList();
        // προσθήκη δεδομένων στο chart (μόνο θετικά ποσά) με ποσοστά στα labels
        data.forEach((k, v) -> {
            if (v != null && v > 0) {
                double percentage = total > 0 ? (v / total) * 100 : 0;
                String labelWithPercentage = k + " (" + String.format("%.1f", percentage) + "%)";
                list.add(new PieChart.Data(labelWithPercentage, v));
            }
        });
        chart.setData(list);
        chart.setTitle(title);
        
        // Ενεργοποίηση labels για όλα τα slices - πάνω στο διάγραμμα, όχι στο legend
        chart.setLabelsVisible(true);
        chart.setLabelLineLength(30);
        chart.setLegendVisible(false); // Απενεργοποίηση legend για να φαίνονται όλα τα labels πάνω
        
        // Εφαρμογή custom χρωμάτων για όλα τα slices
        // Χρησιμοποιούμε 8 χρώματα που επαναλαμβάνονται
        String[] colors = {
            "#1e40af",  // μπλε
            "#22c55e",  // πράσινο
            "#f59e0b",  // πορτοκαλί
            "#ef4444",  // κόκκινο
            "#6366f1",  // indigo
            "#a855f7",  // purple
            "#14b8a6",  // teal
            "#eab308"   // yellow
        };
        
        final String[] colorArray = colors;
        final ObservableList<PieChart.Data> chartData = chart.getData();
        
        // Εφαρμογή χρωμάτων μετά τη δημιουργία των nodes
        Platform.runLater(() -> {
            chart.applyCss();
            chart.layout();
            
            // Εφαρμογή χρωμάτων σε όλα τα slices
            for (int i = 0; i < chartData.size(); i++) {
                final int index = i;
                PieChart.Data slice = chartData.get(i);
                String color = colorArray[index % colorArray.length];
                
                // Εφαρμογή χρώματος στο slice
                if (slice.getNode() != null) {
                    slice.getNode().setStyle("-fx-pie-color: " + color + ";");
                }
                
                // Προσθήκη listener για όταν δημιουργηθεί το node
                slice.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        Platform.runLater(() -> {
                            String sliceColor = colorArray[index % colorArray.length];
                            newNode.setStyle("-fx-pie-color: " + sliceColor + ";");
                        });
                    }
                });
            }
            
            PauseTransition pause = new PauseTransition(Duration.millis(300));
            pause.setOnFinished(e -> {
                chart.applyCss();
                chart.layout();
                for (int i = 0; i < chartData.size(); i++) {
                    PieChart.Data slice = chartData.get(i);
                    if (slice.getNode() != null) {
                        String color = colorArray[i % colorArray.length];
                        slice.getNode().setStyle("-fx-pie-color: " + color + ";");
                    }
                }
                
                // Εξασφάλιση ότι όλα τα labels είναι ορατά - ακόμα και για μικρά slices
                // Χρήση lookupAll για όλα τα label nodes
                chart.lookupAll(".chart-pie-label").forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                    node.setStyle("-fx-font-size: 11px; -fx-opacity: 1.0;");
                });
                
                // Επίσης, εξασφάλιση ότι όλες οι label lines είναι ορατές
                chart.lookupAll(".chart-pie-label-line").forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                });
            });
            pause.play();
            
            // Επιπλέον delay για να βεβαιωθούμε ότι όλα τα labels έχουν δημιουργηθεί
            PauseTransition pause2 = new PauseTransition(Duration.millis(600));
            pause2.setOnFinished(e -> {
                chart.applyCss();
                chart.layout();
                
                // Εξαναγκασμός όλων των labels να είναι ορατά
                chart.lookupAll(".chart-pie-label").forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                    node.setStyle("-fx-font-size: 11px !important; -fx-opacity: 1.0 !important; -fx-visible: true !important;");
                });
                chart.lookupAll(".chart-pie-label-line").forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                    node.setStyle("-fx-opacity: 1.0 !important; -fx-visible: true !important;");
                });
                
                // Επίσης, προσπάθεια να βρούμε και να ενεργοποιήσουμε labels που μπορεί να είναι κρυμμένα
                chart.lookupAll("*").forEach(node -> {
                    if (node.getStyleClass().contains("chart-pie-label") || 
                        node.getStyleClass().contains("chart-pie-label-line")) {
                        node.setVisible(true);
                        node.setManaged(true);
                    }
                });
            });
            pause2.play();
            
            // Τρίτο delay για τελική εξασφάλιση
            PauseTransition pause3 = new PauseTransition(Duration.millis(1000));
            pause3.setOnFinished(e -> {
                chart.lookupAll(".chart-pie-label").forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                });
                chart.lookupAll(".chart-pie-label-line").forEach(node -> {
                    node.setVisible(true);
                    node.setManaged(true);
                });
            });
            pause3.play();
        });
        
        // CSS για να εξασφαλιστεί ότι όλα τα labels είναι ορατά
        // Χρήση !important για να override το default behavior
        chart.setStyle(
            "-fx-pie-label-visible: true !important; " +
            ".chart-pie-label { " +
            "    -fx-font-size: 11px !important; " +
            "    -fx-visible: true !important; " +
            "    -fx-managed: true !important; " +
            "    -fx-opacity: 1.0 !important; " +
            "} " +
            ".chart-pie-label-line { " +
            "    -fx-visible: true !important; " +
            "    -fx-managed: true !important; " +
            "    -fx-opacity: 1.0 !important; " +
            "} " +
            ".chart-pie { " +
            "    -fx-pie-label-visible: true !important; " +
            "}"
        );
    }

    /**
     * Γεμίζει ένα pie chart μόνο με τα top N στοιχεία (κατά φθίνουσα τιμή).
     */
    public static void loadTopPieChart(PieChart chart, Map<String, Double> data, String title, int topN) {
        if (chart == null || data == null) return;

        Map<String, Double> limited = data.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue() > 0)
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        fillPieChart(chart, limited, title);
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
            
            // ορισμός στιλ και χρωμάτων για τις γραμμές
            if (revSeries.getNode() != null) {
                revSeries.getNode().setStyle("-fx-stroke: #22c55e; -fx-stroke-width: 2px;");
            }
            if (expSeries.getNode() != null) {
                expSeries.getNode().setStyle("-fx-stroke: #ef4444; -fx-stroke-width: 2px;");
            }
            
            // ορισμός χρωμάτων για όλα τα data points (κουκίδες)
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
            
            // Επιπλέον: ορισμός χρωμάτων μέσω CSS classes για να εξασφαλιστεί ότι λειτουργεί για όλα τα έτη
            // Χρησιμοποιούμε lookupAll με delay για να βεβαιωθούμε ότι τα nodes έχουν δημιουργηθεί
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(e -> {
                lineChart.lookupAll(".default-color0.chart-line-symbol").forEach(node -> {
                    node.setStyle("-fx-background-color: #22c55e, white; -fx-background-radius: 4px; -fx-padding: 4px;");
                });
                lineChart.lookupAll(".default-color1.chart-line-symbol").forEach(node -> {
                    node.setStyle("-fx-background-color: #ef4444, white; -fx-background-radius: 4px; -fx-padding: 4px;");
                });
            });
            pause.play();
        });
        
        // Επίσης, προσθήκη listener για να ενημερώνονται τα χρώματα όταν αλλάζουν τα nodes
        revSeries.getData().forEach(data -> {
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Platform.runLater(() -> {
                        newNode.setStyle("-fx-background-color: #22c55e, white; -fx-background-radius: 4px; -fx-padding: 4px;");
                    });
                }
            });
        });
        
        expSeries.getData().forEach(data -> {
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Platform.runLater(() -> {
                        newNode.setStyle("-fx-background-color: #ef4444, white; -fx-background-radius: 4px; -fx-padding: 4px;");
                    });
                }
            });
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
        
        // Μείωση του αριθμού κατηγοριών για αποφυγή επικάλυψης labels (max 6)
        int actualTopN = Math.min(topN, 6);
        
        // ταξινόμηση κατά τιμή φθίνουσα και επιλογή των top N
        List<Map.Entry<String, Double>> sorted = data.entrySet().stream()
            .filter(e -> e.getValue() > 0)
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(actualTopN)
            .collect(Collectors.toList());
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ποσό");
        
        // προσθήκη δεδομένων στο chart - χωρίς περικοπή, πλήρη ονόματα
        for (Map.Entry<String, Double> entry : sorted) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        barChart.getData().add(series);
        
        // Ρύθμιση Y-axis για περισσότερες διαιρέσεις (μικρότερο tick unit)
        if (barChart.getYAxis() instanceof NumberAxis) {
            NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
            
            // Εύρεση μέγιστης τιμής από τα δεδομένα
            double maxValue = series.getData().stream()
                .mapToDouble(chartData -> chartData.getYValue().doubleValue())
                .max()
                .orElse(0);
            
            // Υπολογισμός κατάλληλου tick unit (περίπου 8-10 διαιρέσεις)
            double tickUnit;
            if (maxValue >= 50_000_000_000.0) {
                tickUnit = 10_000_000_000.0; // 10 δισεκατομμύρια
            } else if (maxValue >= 20_000_000_000.0) {
                tickUnit = 5_000_000_000.0; // 5 δισεκατομμύρια
            } else if (maxValue >= 10_000_000_000.0) {
                tickUnit = 2_000_000_000.0; // 2 δισεκατομμύρια
            } else if (maxValue >= 5_000_000_000.0) {
                tickUnit = 1_000_000_000.0; // 1 δισεκατομμύριο
            } else if (maxValue >= 1_000_000_000.0) {
                tickUnit = 200_000_000.0; // 200 εκατομμύρια
            } else if (maxValue >= 500_000_000.0) {
                tickUnit = 100_000_000.0; // 100 εκατομμύρια
            } else if (maxValue >= 100_000_000.0) {
                tickUnit = 50_000_000.0; // 50 εκατομμύρια
            } else if (maxValue >= 50_000_000.0) {
                tickUnit = 10_000_000.0; // 10 εκατομμύρια
            } else if (maxValue >= 10_000_000.0) {
                tickUnit = 5_000_000.0; // 5 εκατομμύρια
            } else {
                tickUnit = Math.max(maxValue / 8, 100_000.0); // Δυναμικός υπολογισμός για μικρές τιμές
            }
            
            // Υπολογισμός bounds
            double upperBound = Math.ceil(maxValue / tickUnit) * tickUnit;
            if (upperBound < maxValue * 1.1) {
                upperBound += tickUnit; // Προσθήκη επιπλέον διαίρεσης για καλύτερη οπτική
            }
            
            // Εξασφάλιση ότι το upperBound είναι τουλάχιστον 2 * maxValue για καλύτερη οπτική
            if (upperBound < maxValue * 2 && maxValue > 0) {
                upperBound = Math.ceil(maxValue * 1.2 / tickUnit) * tickUnit;
            }
            
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(upperBound);
            yAxis.setTickUnit(tickUnit);
            yAxis.setMinorTickCount(0); // Καμία μικρή διαίρεση για καθαρότητα
        }
        
        // Οριζόντια labels με μικρότερο font για λιγότερο χώρο
        if (barChart.getXAxis() instanceof CategoryAxis) {
            CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
            xAxis.setTickLabelRotation(0); // Οριζόντια labels (0 μοίρες)
            xAxis.setGapStartAndEnd(true); // Αυξάνει το spacing μεταξύ των categories
            
            // Εξασφάλιση ότι τα labels είναι ορατά και οριζόντια
            Platform.runLater(() -> {
                barChart.applyCss();
                barChart.layout();
                
                PauseTransition pause = new PauseTransition(Duration.millis(100));
                pause.setOnFinished(e -> {
                    // Βεβαιώνουμε ότι τα labels είναι οριζόντια
                    xAxis.setTickLabelRotation(0);
                    
                    // Εφαρμογή μικρότερου font size για λιγότερο χώρο
                    xAxis.setStyle(
                        "-fx-tick-label-font-size: 9px; " +
                        "-fx-tick-label-gap: 4;"
                    );
                    
                    // Επίσης εφαρμογή στο chart container για περισσότερο χώρο
                    barChart.setMinWidth(600);
                });
                pause.play();
                
                // Επιπλέον delay για εξασφάλιση ότι τα labels είναι ορατά
                PauseTransition pause2 = new PauseTransition(Duration.millis(300));
                pause2.setOnFinished(e2 -> {
                    xAxis.setTickLabelRotation(0); // Εξασφάλιση ότι είναι οριζόντια
                });
                pause2.play();
            });
            
            // Αρχικό style με μικρότερα labels
            xAxis.setStyle("-fx-tick-label-font-size: 9px;");
        }
    }

    // φορτώνει ένα area chart με ιστορικά δεδομένα για έσοδα ή δαπάνες
    public static void loadAreaChart(AreaChart<String, Number> areaChart, BudgetData budgetData, String dataType, String title) {
        if (areaChart == null || budgetData == null) return;
        
        areaChart.getData().clear();
        areaChart.setTitle(title);
        
        // Ορισμός των κατηγοριών στον CategoryAxis για να εξασφαλιστεί ότι τα έτη απλώνονται σωστά
        if (areaChart.getXAxis() instanceof CategoryAxis) {
            CategoryAxis xAxis = (CategoryAxis) areaChart.getXAxis();
            ObservableList<String> categories = FXCollections.observableArrayList("2023", "2024", "2025", "2026");
            xAxis.setCategories(categories);
        }
        
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
    
    // ========== YEAR COMPARISON CHARTS ========== //
    
    /**
     * Φορτώνει ένα bar chart που συγκρίνει έσοδα και δαπάνες μεταξύ δύο ετών.
     */
    public static void loadYearComparisonRevenueExpensesChart(BarChart<String, Number> barChart,
                                                               long revenue1, long expenses1,
                                                               long revenue2, long expenses2,
                                                               int year1, int year2) {
        if (barChart == null) return;
        
        barChart.getData().clear();
        barChart.setTitle("Έσοδα vs Δαπάνες");
        
        // Σειρά για έσοδα
        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Έσοδα");
        revenueSeries.getData().add(new XYChart.Data<>(String.valueOf(year1), revenue1));
        revenueSeries.getData().add(new XYChart.Data<>(String.valueOf(year2), revenue2));
        
        // Σειρά για δαπάνες
        XYChart.Series<String, Number> expensesSeries = new XYChart.Series<>();
        expensesSeries.setName("Δαπάνες");
        expensesSeries.getData().add(new XYChart.Data<>(String.valueOf(year1), expenses1));
        expensesSeries.getData().add(new XYChart.Data<>(String.valueOf(year2), expenses2));
        
        barChart.getData().addAll(revenueSeries, expensesSeries);
        
        // Ρύθμιση Y-axis
        if (barChart.getYAxis() instanceof NumberAxis) {
            NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
            double maxValue = Math.max(
                Math.max(revenue1, expenses1),
                Math.max(revenue2, expenses2)
            );
            
            double tickUnit = calculateAppropriateTickUnitForYearComparison(maxValue);
            double upperBound = Math.ceil(maxValue * 1.2 / tickUnit) * tickUnit;
            
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(upperBound);
            yAxis.setTickUnit(tickUnit);
            yAxis.setMinorTickCount(0);
        }
        
        // Εφαρμογή χρωμάτων
        Platform.runLater(() -> {
            barChart.applyCss();
            barChart.layout();
            
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(e -> {
                barChart.lookupAll(".default-color0.chart-bar").forEach(node -> {
                    node.setStyle("-fx-bar-fill: #22c55e;");
                });
                barChart.lookupAll(".default-color1.chart-bar").forEach(node -> {
                    node.setStyle("-fx-bar-fill: #ef4444;");
                });
            });
            pause.play();
        });
    }
    
    /**
     * Φορτώνει ένα bar chart που συγκρίνει το ισοζύγιο μεταξύ δύο ετών.
     */
    public static void loadYearComparisonBalanceChart(BarChart<String, Number> barChart,
                                                       long balance1, long balance2,
                                                       int year1, int year2) {
        if (barChart == null) return;
        
        barChart.getData().clear();
        barChart.setTitle("Ισοζύγιο Προϋπολογισμού");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ισοζύγιο");
        series.getData().add(new XYChart.Data<>(String.valueOf(year1), balance1));
        series.getData().add(new XYChart.Data<>(String.valueOf(year2), balance2));
        
        barChart.getData().add(series);
        
        // Ρύθμιση Y-axis - επιτρέπουμε αρνητικές τιμές για έλλειμμα
        if (barChart.getYAxis() instanceof NumberAxis) {
            NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
            double minValue = Math.min(balance1, balance2);
            double maxValue = Math.max(balance1, balance2);
            
            double tickUnit = calculateAppropriateTickUnitForYearComparison(Math.max(Math.abs(minValue), Math.abs(maxValue)));
            double lowerBound = Math.floor(minValue * 1.2 / tickUnit) * tickUnit;
            double upperBound = Math.ceil(maxValue * 1.2 / tickUnit) * tickUnit;
            
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(lowerBound);
            yAxis.setUpperBound(upperBound);
            yAxis.setTickUnit(tickUnit);
            yAxis.setMinorTickCount(0);
        }
        
        // Εφαρμογή χρωμάτων - πράσινο για θετικό, κόκκινο για αρνητικό
        Platform.runLater(() -> {
            barChart.applyCss();
            barChart.layout();
            
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(e -> {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null && data.getYValue() != null) {
                        double value = data.getYValue().doubleValue();
                        if (value >= 0) {
                            data.getNode().setStyle("-fx-bar-fill: #22c55e;");
                        } else {
                            data.getNode().setStyle("-fx-bar-fill: #ef4444;");
                        }
                    }
                }
            });
            pause.play();
        });
    }
    
    /**
     * Φορτώνει ένα pie chart με την κατανομή εσόδων για ένα συγκεκριμένο έτος.
     */
    public static void loadYearComparisonRevenuePieChart(PieChart pieChart,
                                                          ui.Comparisons.ComparisonResults results,
                                                          int year, boolean useYear1) {
        if (pieChart == null || results == null) return;
        
        Map<String, Double> data = new HashMap<>();
        for (ui.Comparisons.ComparisonData rev : results.getRevenues().values()) {
            if (rev.getCategoryName().equals("Total revenue")) continue; // Skip total
            long value = useYear1 ? rev.getYear1Value() : rev.getYear2Value();
            if (value > 0) {
                String greekName = translateCategoryName(rev.getCategoryName());
                data.put(greekName, (double) value);
            }
        }
        
        fillPieChart(pieChart, data, "Κατανομή Εσόδων - " + year);
    }
    
    /**
     * Φορτώνει ένα pie chart με την κατανομή δαπανών για ένα συγκεκριμένο έτος.
     */
    public static void loadYearComparisonExpensesPieChart(PieChart pieChart,
                                                           ui.Comparisons.ComparisonResults results,
                                                           int year, boolean useYear1) {
        if (pieChart == null || results == null) return;
        
        Map<String, Double> data = new HashMap<>();
        for (ui.Comparisons.ComparisonData exp : results.getExpenses().values()) {
            if (exp.getCategoryName().equals("Total expenses")) continue; // Skip total
            long value = useYear1 ? exp.getYear1Value() : exp.getYear2Value();
            if (value > 0) {
                String greekName = translateCategoryName(exp.getCategoryName());
                data.put(greekName, (double) value);
            }
        }
        
        fillPieChart(pieChart, data, "Κατανομή Δαπανών - " + year);
    }
    
    /**
     * Μεταφράζει ονόματα κατηγοριών από αγγλικά σε ελληνικά.
     */
    private static String translateCategoryName(String englishName) {
        Map<String, String> translations = new HashMap<>();
        translations.put("Taxes", "Φόροι");
        translations.put("Social contributions", "Κοινωνικές Εισφορές");
        translations.put("Transfers", "Μεταβιβάσεις");
        translations.put("Sales of goods & services", "Πωλήσεις Αγαθών & Υπηρεσιών");
        translations.put("Other current revenue", "Λοιπά Τρέχοντα Έσοδα");
        translations.put("Employee benefits", "Αμοιβές Προσωπικού");
        translations.put("Social benefits", "Κοινωνικές Παροχές");
        translations.put("Purchases of goods & services", "Αγορές Αγαθών & Υπηρεσιών");
        translations.put("Subsidies", "Επιδοτήσεις");
        translations.put("Interest", "Τόκοι");
        translations.put("Other expenditures", "Λοιπές Δαπάνες");
        
        return translations.getOrDefault(englishName, englishName);
    }
    
    /**
     * Βοηθητική μέθοδος για τον υπολογισμό κατάλληλου tick unit για συγκρίσεις ετών.
     */
    private static double calculateAppropriateTickUnitForYearComparison(double maxValue) {
        if (maxValue >= 1_000_000_000_000.0) {
            return 100_000_000_000.0; // 100 δισεκατομμύρια
        } else if (maxValue >= 100_000_000_000.0) {
            return 10_000_000_000.0; // 10 δισεκατομμύρια
        } else if (maxValue >= 10_000_000_000.0) {
            return 1_000_000_000.0; // 1 δισεκατομμύριο
        } else if (maxValue >= 1_000_000_000.0) {
            return 100_000_000.0; // 100 εκατομμύρια
        } else if (maxValue >= 100_000_000.0) {
            return 10_000_000.0; // 10 εκατομμύρια
        } else if (maxValue >= 10_000_000.0) {
            return 1_000_000.0; // 1 εκατομμύριο
        } else {
            return Math.max(maxValue / 8, 100_000.0);
        }
    }
    
    // ========== OVERVIEW CHARTS (ALL YEARS) ========== //
    
    /**
     * Φορτώνει ένα line chart που δείχνει την εξέλιξη εσόδων και δαπανών σε όλα τα διαθέσιμα έτη.
     */
    public static void loadOverviewTrendsChart(LineChart<String, Number> lineChart, BudgetData budgetData, List<String> availableYears) {
        if (lineChart == null || budgetData == null || availableYears == null || availableYears.isEmpty()) {
            return;
        }
        
        lineChart.getData().clear();
        lineChart.setTitle("Εξέλιξη Εσόδων και Δαπανών");
        
        // Sort years
        List<String> sortedYears = new ArrayList<>(availableYears);
        sortedYears.sort((a, b) -> Integer.compare(Integer.parseInt(a), Integer.parseInt(b)));
        
        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Έσοδα");
        
        XYChart.Series<String, Number> expensesSeries = new XYChart.Series<>();
        expensesSeries.setName("Δαπάνες");
        
        double maxValue = 0;
        for (String yearStr : sortedYears) {
            try {
                int year = Integer.parseInt(yearStr);
                double revenue = budgetData.getTotalRevenues(year);
                double expenses = budgetData.getTotalExpenses(year);
                
                revenueSeries.getData().add(new XYChart.Data<>(yearStr, revenue));
                expensesSeries.getData().add(new XYChart.Data<>(yearStr, expenses));
                
                maxValue = Math.max(maxValue, Math.max(revenue, expenses));
            } catch (NumberFormatException e) {
                // Skip invalid years
            }
        }
        
        lineChart.getData().addAll(revenueSeries, expensesSeries);
        
        // Configure Y-axis
        if (lineChart.getYAxis() instanceof NumberAxis) {
            NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
            double tickUnit = calculateAppropriateTickUnitForYearComparison(maxValue);
            double upperBound = Math.ceil(maxValue * 1.1 / tickUnit) * tickUnit;
            
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(upperBound);
            yAxis.setTickUnit(tickUnit);
            yAxis.setMinorTickCount(0);
        }
        
        // Configure X-axis to prevent label overlapping
        if (lineChart.getXAxis() instanceof CategoryAxis) {
            CategoryAxis xAxis = (CategoryAxis) lineChart.getXAxis();
            xAxis.setAnimated(false);
            xAxis.setTickLabelRotation(0);
            // Set gap between categories to ensure labels don't overlap
            xAxis.setGapStartAndEnd(true);
        }
        
        // Apply colors and fix label spacing
        Platform.runLater(() -> {
            lineChart.applyCss();
            lineChart.layout();
            
            PauseTransition pause = new PauseTransition(Duration.millis(200));
            pause.setOnFinished(e -> {
                // Apply colors
                lineChart.lookupAll(".default-color0.chart-line-symbol").forEach(node -> {
                    node.setStyle("-fx-background-color: #22c55e, white;");
                });
                lineChart.lookupAll(".default-color0.chart-series-line").forEach(node -> {
                    node.setStyle("-fx-stroke: #22c55e;");
                });
                lineChart.lookupAll(".default-color1.chart-line-symbol").forEach(node -> {
                    node.setStyle("-fx-background-color: #ef4444, white;");
                });
                lineChart.lookupAll(".default-color1.chart-series-line").forEach(node -> {
                    node.setStyle("-fx-stroke: #ef4444;");
                });
                
                // Fix category axis labels spacing - ensure they don't overlap
                lineChart.lookupAll(".axis-label").forEach(node -> {
                    node.setStyle("-fx-font-size: 12px;");
                });
                
                // Style category tick labels to prevent overlapping
                lineChart.lookupAll(".axis-tick-mark").forEach(node -> {
                    node.setStyle("-fx-stroke: #666;");
                });
                
                // Ensure proper spacing for category labels
                lineChart.lookupAll(".chart-category-axis .axis-label").forEach(node -> {
                    node.setStyle("-fx-font-size: 12px; -fx-padding: 0 5 0 5;");
                });
            });
            pause.play();
        });
    }
    
    /**
     * Φορτώνει ένα bar chart που δείχνει το ισοζύγιο προϋπολογισμού για όλα τα διαθέσιμα έτη.
     */
    public static void loadOverviewBalanceChart(BarChart<String, Number> barChart, BudgetData budgetData, List<String> availableYears) {
        if (barChart == null || budgetData == null || availableYears == null || availableYears.isEmpty()) {
            return;
        }
        
        barChart.getData().clear();
        barChart.setTitle("Ισοζύγιο Προϋπολογισμού ανά Έτος");
        
        // Sort years
        List<String> sortedYears = new ArrayList<>(availableYears);
        sortedYears.sort((a, b) -> Integer.compare(Integer.parseInt(a), Integer.parseInt(b)));
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ισοζύγιο");
        
        double minValue = 0;
        double maxValue = 0;
        
        for (String yearStr : sortedYears) {
            try {
                int year = Integer.parseInt(yearStr);
                double balance = budgetData.getBalance(year);
                
                series.getData().add(new XYChart.Data<>(yearStr, balance));
                
                minValue = Math.min(minValue, balance);
                maxValue = Math.max(maxValue, balance);
            } catch (NumberFormatException e) {
                // Skip invalid years
            }
        }
        
        barChart.getData().add(series);
        
        // Configure Y-axis - allow negative values for deficit
        if (barChart.getYAxis() instanceof NumberAxis) {
            NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
            double tickUnit = calculateAppropriateTickUnitForYearComparison(Math.max(Math.abs(minValue), Math.abs(maxValue)));
            double lowerBound = Math.floor(minValue * 1.2 / tickUnit) * tickUnit;
            double upperBound = Math.ceil(maxValue * 1.2 / tickUnit) * tickUnit;
            
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(lowerBound);
            yAxis.setUpperBound(upperBound);
            yAxis.setTickUnit(tickUnit);
            yAxis.setMinorTickCount(0);
        }
        
        // Apply colors - green for positive, red for negative
        Platform.runLater(() -> {
            barChart.applyCss();
            barChart.layout();
            
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(e -> {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null && data.getYValue() != null) {
                        double value = data.getYValue().doubleValue();
                        if (value >= 0) {
                            data.getNode().setStyle("-fx-bar-fill: #22c55e;");
                        } else {
                            data.getNode().setStyle("-fx-bar-fill: #ef4444;");
                        }
                    }
                }
            });
            pause.play();
        });
    }
    
    /**
     * Φορτώνει ένα pie chart που δείχνει την ανάλυση εσόδων για το πιο πρόσφατο έτος.
     */
    public static void loadOverviewRevenuePieChart(PieChart pieChart, BudgetData budgetData, int year) {
        if (pieChart == null || budgetData == null) return;
        Map<String, Double> revenueData = budgetData.getRevenueBreakdownForGraphs(year);
        fillPieChart(pieChart, revenueData, "Κατανομή Εσόδων (" + year + ")");
    }
    
    /**
     * Φορτώνει ένα pie chart που δείχνει την ανάλυση δαπανών για το πιο πρόσφατο έτος.
     */
    public static void loadOverviewExpensesPieChart(PieChart pieChart, BudgetData budgetData, int year) {
        if (pieChart == null || budgetData == null) return;
        Map<String, Double> expenseData = budgetData.getExpenseBreakdownForGraphs(year);
        fillPieChart(pieChart, expenseData, "Κατανομή Δαπανών (" + year + ")");
    }
}
