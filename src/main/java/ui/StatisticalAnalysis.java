package ui;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

// κλάση για στατιστικές αναλύσεις 
public class StatisticalAnalysis {
    
    private StatisticalAnalysis() {
    }
    
    // υπολογίζει τυπική απόκλιση 
    public static double calculateStandardDeviation(double[] values) {  
        double variance = calculateVariance(values);
        return Math.sqrt(variance);
    }
    
    // υπολογίζει διακύμανση 
    public static double calculateVariance(double[] values) {
        if (values == null || values.length <= 1) {
            return 0.0;
        }
        
        double mean = calculateMean(values);
        double sumSquaredDifferences = 0.0;
        
        // υπολογίζει άθροισμα τετραγωνικών διαφορών
        for (double value : values) {
            double difference = value - mean;
            sumSquaredDifferences += difference * difference;
        }
        
        // διακύμανση 
        return sumSquaredDifferences / (values.length - 1);
    }
    
    // υπολογίζει μέση τιμή 
    public static double calculateMean(double[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }
    
    // υπολογίζει ποσοστιαία μεταβολή μεταξύ δύο τιμών
    public static double calculatePercentageChange(double currentValue, double previousValue) {
        if (previousValue == 0) {
            return 0.0;
        }
        return ((currentValue - previousValue) / previousValue) * 100;
    }
    
    // υπολογίζει ισοζύγιο (έσοδα - δαπάνες)
    public static double calculateBalance(double revenues, double expenses) {
        return revenues - expenses;
    }
    
    // υπολογίζει διάμεσο
    public static double calculateMedian(double[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        
        // ταξινόμηση
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        
        int middle = sorted.length / 2;
        
        // για άρτιο αριθμό στοιχείων
        if (sorted.length % 2 == 0) {
            return (sorted[middle - 1] + sorted[middle]) / 2.0;
        } else {
            // για περιττό
            return sorted[middle];
        }
    }
    
    // υπολογίζει mode
    public static double calculateMode(double[] values) {
        if (values == null || values.length == 0) {
            return Double.NaN;
        }
        
        // μέτρηση συχνότητας κάθε τιμής
        java.util.Map<Double, Integer> frequencyMap = new java.util.HashMap<>();
        for (double value : values) {
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
        }
        
        // εύρεση τιμής με τη μεγαλύτερη συχνότητα
        int maxFrequency = 0;
        double mode = Double.NaN;
        
        for (java.util.Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mode = entry.getKey();
            }
        }
        
        // αν όλες οι τιμές εμφανίζονται μόνο μία φορά, δεν υπάρχει mode
        if (maxFrequency == 1 && values.length > 1) {
            return Double.NaN;
        }
        
        return mode;
    }
    
    // υπολογίζει κοινά στοιχεία για συσχέτιση και παλινδρόμηση
    private static double[] calculateCorrelationComponents(double[] xValues, double[] yValues) {
        if (xValues == null || yValues == null || 
            xValues.length != yValues.length || 
            xValues.length < 2) {
            return null;
        }
        
        double meanX = calculateMean(xValues);
        double meanY = calculateMean(yValues);
        
        double sumXY = 0.0;
        double sumXSquared = 0.0;
        double sumYSquared = 0.0;
        
        // υπολογίζει αθροίσματα
        for (int i = 0; i < xValues.length; i++) {
            double diffX = xValues[i] - meanX;
            double diffY = yValues[i] - meanY;

            sumXY += diffX * diffY;
            sumXSquared += diffX * diffX;
            sumYSquared += diffY * diffY;
        }
        
        return new double[]{sumXY, sumXSquared, sumYSquared};
    }
    
    // υπολογίζει τον συντελεστή συσχέτισης μεταξύ δύο μεταβλητών
    public static double calculateCorrelation(double[] xValues, double[] yValues) {
        double[] components = calculateCorrelationComponents(xValues, yValues);
        if (components == null) {
            return Double.NaN;
        }
        
        double sumXY = components[0];
        double sumXSquared = components[1];
        double sumYSquared = components[2];
        
        double denominator = Math.sqrt(sumXSquared * sumYSquared);      
        if (denominator == 0.0) {
            return Double.NaN;
        }
        
        return sumXY / denominator;
    }
    
    // υπολογίζει διαφορά μέγιστης & ελάχιστης τιμής
    public static double calculateRange(double[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        
        double min = values[0];
        double max = values[0];
        
        // βρίσκουμε μικρότερη & μεγαλύτερη τιμή
        for (double value : values) {
            if (value < min) min = value;
            if (value > max) max = value;
        }
        
        return max - min;
    }
    
    // υπολογίζουμε τεταρτημόρια 
        public static double[] calculateQuartiles(double[] values) {        
        if (values == null || values.length < 3) {
            return null;
        }
        
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        
        // Q2 είναι η διάμεσος
        double q2 = calculateMedian(sorted);
        
        int middle = sorted.length / 2;
        // Χωρίζουμε τα δεδομένα στο μισό
        double[] lowerHalf = Arrays.copyOfRange(sorted, 0, middle);     
        double[] upperHalf;
        
        if (sorted.length % 2 == 0) {
            upperHalf = Arrays.copyOfRange(sorted, middle, sorted.length);
        } else {
            upperHalf = Arrays.copyOfRange(sorted, middle + 1, sorted.length);
        }
        
        // Q1 = διάμεσος του κάτω μισού, Q3 = διάμεσος του άνω μισού
        double q1 = calculateMedian(lowerHalf);
        double q3 = calculateMedian(upperHalf);
        
        return new double[]{q1, q2, q3};
    }
    
    // υπολογίζει το διατεταρτημοριακό εύρος (Q3 - Q1)
    // μετράει τη διασπορά του μεσαίου 50% των δεδομένων
    public static double calculateIQR(double[] values) {
        double[] quartiles = calculateQuartiles(values);
        if (quartiles == null) {
            return Double.NaN;
        }
        return quartiles[2] - quartiles[0];
    }
    
    // εντοπίζει outliers
    public static List<Double> identifyOutliers(double[] values) {      
        List<Double> outliers = new ArrayList<>();
        
        if (values == null || values.length < 4) {
            return outliers;
        }
        
        double[] quartiles = calculateQuartiles(values);
        if (quartiles == null) {
            return outliers;
        }
        
        double q1 = quartiles[0];
        double q3 = quartiles[2];
        double iqr = q3 - q1;
        // όρια για outliers
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;
        
        // ελέγχουμε κάθε τιμή αν είναι outlier
        for (double value : values) {
            if (value < lowerBound || value > upperBound) {
                outliers.add(value);
            }
        }
        
        return outliers;
    }
    
    // υπολογίζει γραμμική παλινδρόμηση 
    public static double[] calculateLinearRegression(double[] xValues, double[] yValues) {
        double[] components = calculateCorrelationComponents(xValues, yValues);
        if (components == null) {
            return null;
        }
        
        double sumXY = components[0];
        double sumXSquared = components[1];
        
        if (sumXSquared == 0.0) {
            return null;
        }
        
        // υπολογισμός κλίσης και σταθεράς
        double meanX = calculateMean(xValues);
        double meanY = calculateMean(yValues);
        double slope = sumXY / sumXSquared;
        double intercept = meanY - slope * meanX;
        
        return new double[]{slope, intercept};
    }
    
    // υπολογίζει τον συντελεστή μεταβλητότητας (τυπική απόκλιση / μέση τιμή) * 100
    public static double calculateCoefficientOfVariation(double[] values) {
        if (values == null || values.length == 0) {
            return Double.NaN;
        }
        
        double mean = calculateMean(values);
        if (mean == 0.0) {
            return Double.NaN;
        }
        
        double stdDev = calculateStandardDeviation(values);
        return (stdDev / mean) * 100.0;
    }
    
    // υπολογίζει κινητό μέσο όρο 
    public static double[] calculateMovingAverage(double[] values, int windowSize) {
        if (values == null || values.length == 0 || 
            windowSize <= 0 || windowSize > values.length) {
            return null;
        }
        
        double[] movingAverages = new double[values.length - windowSize + 1];
        
        for (int i = 0; i <= values.length - windowSize; i++) {
            double sum = 0.0;
            for (int j = i; j < i + windowSize; j++) {
                sum += values[j];
            }
            movingAverages[i] = sum / windowSize;
        }
        
        return movingAverages;
    }
    
    // υπολογίζει z-score (πόσες τυπικές αποκλίσεις απέχει μια τιμή από τη μέση)
    public static double calculateZScore(double value, double[] values) {
        if (values == null || values.length == 0) {
            return Double.NaN;
        }
        
        double mean = calculateMean(values);
        double stdDev = calculateStandardDeviation(values);
        
        if (stdDev == 0.0) {
            return Double.NaN;
        }
        
        // Z-score = (τιμή - μέση τιμή) / τυπική απόκλιση
        return (value - mean) / stdDev;
    }
    
    // συνοπτική αναφορά με τις βασικές στατιστικές
    public static String generateStatisticalSummary(double[] values) {  
        if (values == null || values.length == 0) {
            return "No data available for statistical analysis.";       
        }
        
        StringBuilder summary = new StringBuilder();
        summary.append("Statistical Summary:\n");
        summary.append("===================\n");
        summary.append(String.format("Count: %d\n", values.length));    
        summary.append(String.format("Mean: %.2f\n", calculateMean(values)));
        summary.append(String.format("Median: %.2f\n", calculateMedian(values)));
        summary.append(String.format("Standard Deviation: %.2f\n", calculateStandardDeviation(values)));
        summary.append(String.format("Variance: %.2f\n", calculateVariance(values)));
        summary.append(String.format("Range: %.2f\n", calculateRange(values)));
        
        double mode = calculateMode(values);
        if (!Double.isNaN(mode)) {
            summary.append(String.format("Mode: %.2f\n", mode));        
        } else {
            summary.append("Mode: No mode (all values unique)\n");      
        }
        
        double[] quartiles = calculateQuartiles(values);
        if (quartiles != null) {
            summary.append(String.format("Q1: %.2f\n", quartiles[0]));  
            summary.append(String.format("Q2 (Median): %.2f\n", quartiles[1]));
            summary.append(String.format("Q3: %.2f\n", quartiles[2]));  
            summary.append(String.format("IQR: %.2f\n", calculateIQR(values)));
        }
        
        double cv = calculateCoefficientOfVariation(values);
        if (!Double.isNaN(cv)) {
            summary.append(String.format("Coefficient of Variation: %.2f%%\n", cv));
        }
        
        List<Double> outliers = identifyOutliers(values);
        if (!outliers.isEmpty()) {
            summary.append(String.format("Outliers: %d found\n", outliers.size()));
        }
        
        return summary.toString();
    }
    
    // αναλύει και εξηγεί το  ισοζύγιο  
    public static String analyzeBudget(double balance) {
        if (balance > 0) {
            return "Ο προϋπολογισμός είναι πλεονασματικός όταν τα συνολικά έσοδα είναι μεγαλύτερα από τα έξοδα. " +
                   "Αυτό σημαίνει ότι υπάρχει οικονομικό πλεόνασμα, το οποίο μπορεί να χρησιμοποιηθεί για αποταμίευση, " +
                   "επενδύσεις ή κάλυψη μελλοντικών αναγκών.";
        } else if (balance == 0) {
            return "Ο προϋπολογισμός είναι ισοσκελισμένος όταν τα έσοδα και τα έξοδα είναι ίσα. " +
                   "Δεν υπάρχει ούτε κέρδος ούτε ζημία, γεγονός που δείχνει καλή οικονομική ισορροπία " +
                   "αλλά χωρίς περιθώριο ασφάλειας.";
        } else {
            return "Ο προϋπολογισμός είναι ελλειμματικός όταν τα έξοδα ξεπερνούν τα έσοδα. " +
                   "Αυτό υποδηλώνει οικονομική πίεση και την ανάγκη για μείωση δαπανών ή αύξηση εσόδων " +
                   "ώστε να αποφευχθούν προβλήματα στο μέλλον.";
        }
    }
    
    // συγκρίνει αναλυτικά δύο προϋπολογισμούς
    public static String compareBudgetsDetailed(int year1, int year2, 
                                                 double balance1, double balance2,
                                                 double revenue1, double revenue2,
                                                 double expense1, double expense2) {
        double percentageDiff = 0;
        if (balance1 != 0) {
            percentageDiff = ((balance2 - balance1) / Math.abs(balance1)) * 100;
        }
        
        StringBuilder msg = new StringBuilder();
        msg.append("Σύγκριση προϋπολογισμού μεταξύ των ετών ").append(year1).append(" και ").append(year2).append(":\n\n");
        
        // Σύγκριση αποτελέσματος
        if (balance1 > balance2) {
            msg.append("Το έτος ").append(year1).append(" έχει καλύτερο οικονομικό αποτέλεσμα από το έτος ").append(year2).append(".\n");
            msg.append("Το ισοζύγιο είναι υψηλότερο κατά ").append(String.format("%.2f", Math.abs(percentageDiff))).append("%.\n\n");
        } else if (balance1 < balance2) {
            msg.append("Το έτος ").append(year2).append(" έχει καλύτερο οικονομικό αποτέλεσμα από το έτος ").append(year1).append(".\n");
            msg.append("Το ισοζύγιο είναι υψηλότερο κατά ").append(String.format("%.2f", Math.abs(percentageDiff))).append("%.\n\n");
        } else {
            msg.append("Τα έτη έχουν ακριβώς το ίδιο οικονομικό αποτέλεσμα.\n\n");
        }
        
        // Σύγκριση εσόδων
        if (revenue1 > revenue2) {
            msg.append("Τα έσοδα ήταν υψηλότερα στο έτος ").append(year1).append(" (");
            msg.append(String.format("%.2f", revenue1)).append(" έναντι ").append(String.format("%.2f", revenue2)).append(").\n");
        } else if (revenue1 < revenue2) {
            msg.append("Τα έσοδα ήταν υψηλότερα στο έτος ").append(year2).append(" (");
            msg.append(String.format("%.2f", revenue2)).append(" έναντι ").append(String.format("%.2f", revenue1)).append(").\n");
        } else {
            msg.append("Τα έσοδα ήταν ίδια και στα δύο έτη (").append(String.format("%.2f", revenue1)).append(").\n");
        }
        
        // Σύγκριση εξόδων
        if (expense1 > expense2) {
            msg.append("Τα έξοδα ήταν υψηλότερα στο έτος ").append(year1).append(" (");
            msg.append(String.format("%.2f", expense1)).append(" έναντι ").append(String.format("%.2f", expense2)).append(").\n");
        } else if (expense1 < expense2) {
            msg.append("Τα έξοδα ήταν υψηλότερα στο έτος ").append(year2).append(" (");
            msg.append(String.format("%.2f", expense2)).append(" έναντι ").append(String.format("%.2f", expense1)).append(").\n");
        } else {
            msg.append("Τα έξοδα ήταν ίδια και στα δύο έτη (").append(String.format("%.2f", expense1)).append(").\n");
        }
        
        msg.append("\nΑυτό το αποτέλεσμα δείχνει τη συνολική οικονομική τάση και την αποδοτικότητα της διαχείρισης εσόδων και εξόδων.");
        
        return msg.toString();
    }
    
    // γενική σύγκριση κατηγορίας μεταξύ δύο ετών
    private static String compareCategoryGeneric(int year1, int year2, String category,
                                                  Double amount1, Double amount2,
                                                  String typeLabel, String increaseMsg, String decreaseMsg, 
                                                  String noChangeMsg, String higherBurdenMsg, String lowerBurdenMsg) {
        if (amount1 == null && amount2 == null) {
            return "Δεν υπάρχουν δεδομένα για την κατηγορία '" + category + "' στα δύο έτη.";
        } else if (amount1 == null) {
            return "Δεν υπάρχουν δεδομένα για την κατηγορία '" + category + "' στο έτος " + year1;
        } else if (amount2 == null) {
            return "Δεν υπάρχουν δεδομένα για την κατηγορία '" + category + "' στο έτος " + year2;
        }
        
        double percentageChange = 0;
        if (amount1 != 0) {
            percentageChange = ((amount2 - amount1) / Math.abs(amount1)) * 100;
        }
        
        StringBuilder msg = new StringBuilder();
        msg.append("Σύγκριση ").append(typeLabel).append(" για την κατηγορία '").append(category).append("' μεταξύ των ετών ");
        msg.append(year1).append(" και ").append(year2).append(":\n");
        msg.append("Έτος ").append(year1).append("  Ποσό: ").append(String.format("%.2f", amount1)).append("\n");
        msg.append("Έτος ").append(year2).append("  Ποσό: ").append(String.format("%.2f", amount2)).append("\n");
        
        if (amount2 > amount1) {
            msg.append(increaseMsg).append(String.format("%.2f", percentageChange));
            msg.append("% σε σχέση με το προηγούμενο έτος.\n");
            msg.append(higherBurdenMsg);
        } else if (amount2 < amount1) {
            msg.append(decreaseMsg).append(String.format("%.2f", Math.abs(percentageChange)));
            msg.append("% σε σχέση με το προηγούμενο έτος.\n");
            msg.append(lowerBurdenMsg);
        } else {
            msg.append(noChangeMsg);
        }
        
        return msg.toString();
    }
    
    // συγκρίνει ποσά δαπανών για συγκεκριμένη κατηγορία μεταξύ δύο ετών
    public static String compareExpenseByCategory(int year1, int year2, String category, 
                                                   Double amount1, Double amount2) {
        return compareCategoryGeneric(year1, year2, category, amount1, amount2,
            "κατηγορίας",
            "Το ποσό αυξήθηκε κατά ",
            "Το ποσό μειώθηκε κατά ",
            "Δεν παρατηρείται αλλαγή στο ποσό της κατηγορίας μεταξύ των δύο ετών.",
            "Η κατηγορία έχει μεγαλύτερη επιβάρυνση.",
            "Η κατηγορία έχει μικρότερη επιβάρυνση.");
    }
    
    // συγκρίνει ποσά εσόδων για συγκεκριμένη κατηγορία μεταξύ δύο ετών
    public static String compareRevenueByCategory(int year1, int year2, String category,
                                                   Double amount1, Double amount2) {
        return compareCategoryGeneric(year1, year2, category, amount1, amount2,
            "εσόδων",
            "Τα έσοδα αυξήθηκαν κατά ",
            "Τα έσοδα μειώθηκαν κατά ",
            "Δεν παρατηρείται αλλαγή στα έσοδα της κατηγορίας μεταξύ των δύο ετών.",
            "Η κατηγορία έχει μεγαλύτερη εισροή εσόδων.",
            "Η κατηγορία έχει μικρότερη εισροή εσόδων.");
    }
}

