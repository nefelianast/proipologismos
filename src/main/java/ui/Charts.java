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
            
            // Επιπλέον: χρήση PauseTransition για delayed styling και εξασφάλιση ότι όλα τα labels είναι ορατά
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
}
