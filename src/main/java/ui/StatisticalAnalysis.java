package ui;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * Advanced statistical analysis class for budget data.
 * Provides methods for calculating standard deviation, variance, median, mode,
 * correlation, and other advanced statistical measures.
 * 
 * This class extends the basic statistics provided by BudgetStatisticsCalculator
 * with more sophisticated analysis capabilities.
 */
public class StatisticalAnalysis {
    
    /**
     * Private constructor to prevent instantiation
     */
    private StatisticalAnalysis() {
        // Utility class - no instantiation needed
    }
    
    /**
     * Calculates the standard deviation of a dataset.
     * Standard deviation measures the amount of variation or dispersion in a set of values.
     * 
     * @param values Array of double values
     * @return The standard deviation, or 0.0 if array is empty or has only one element
     */
    public static double calculateStandardDeviation(double[] values) {  
        if (values == null || values.length <= 1) {
            return 0.0;
        }
        
        double mean = calculateMean(values);
        double sumSquaredDifferences = 0.0;
        
        for (double value : values) {
            double difference = value - mean;
            sumSquaredDifferences += difference * difference;
        }
        
        double variance = sumSquaredDifferences / (values.length - 1);  
        return Math.sqrt(variance);
    }
    
    /**
     * Calculates the variance of a dataset.
     * Variance measures how far a set of numbers is spread out from their average value.
     * 
     * @param values Array of double values
     * @return The variance, or 0.0 if array is empty or has only one element
     */
    public static double calculateVariance(double[] values) {
        if (values == null || values.length <= 1) {
            return 0.0;
        }
        
        double mean = calculateMean(values);
        double sumSquaredDifferences = 0.0;
        
        for (double value : values) {
            double difference = value - mean;
            sumSquaredDifferences += difference * difference;
        }
        
        return sumSquaredDifferences / (values.length - 1);
    }
    
    /**
     * Calculates the mean (average) of a dataset.
     * 
     * @param values Array of double values
     * @return The mean, or 0.0 if array is empty
     */
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
    
    /**
     * Calculates the median of a dataset.
     * The median is the middle value when the data is sorted.
     * 
     * @param values Array of double values
     * @return The median, or 0.0 if array is empty
     */
    public static double calculateMedian(double[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        
        int middle = sorted.length / 2;
        
        if (sorted.length % 2 == 0) {
            // Even number of elements - average the two middle values  
            return (sorted[middle - 1] + sorted[middle]) / 2.0;
        } else {
            // Odd number of elements - return the middle value
            return sorted[middle];
        }
    }
    
    /**
     * Calculates the mode of a dataset.
     * The mode is the value that appears most frequently.
     * If multiple values have the same highest frequency, returns the first one encountered.
     * 
     * @param values Array of double values
     * @return The mode, or Double.NaN if no mode exists (all values are unique)
     */
    public static double calculateMode(double[] values) {
        if (values == null || values.length == 0) {
            return Double.NaN;
        }
        
        // Count frequency of each value
        java.util.Map<Double, Integer> frequencyMap = new java.util.HashMap<>();
        for (double value : values) {
            frequencyMap.put(value, frequencyMap.getOrDefault(value, 0) + 1);
        }
        
        // Find the value with highest frequency
        int maxFrequency = 0;
        double mode = Double.NaN;
        
        for (java.util.Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mode = entry.getKey();
            }
        }
        
        // If all values appear only once, there's no mode
        if (maxFrequency == 1 && values.length > 1) {
            return Double.NaN;
        }
        
        return mode;
    }
    
    /**
     * Calculates the correlation coefficient between two datasets.     
     * Correlation measures the linear relationship between two variables.
     * Returns a value between -1 and 1, where:
     * - 1 indicates perfect positive correlation
     * - -1 indicates perfect negative correlation
     * - 0 indicates no linear correlation
     * 
     * @param xValues First dataset
     * @param yValues Second dataset
     * @return The correlation coefficient, or Double.NaN if datasets are invalid
     */
    public static double calculateCorrelation(double[] xValues, double[] yValues) {
        if (xValues == null || yValues == null || 
            xValues.length != yValues.length || 
            xValues.length < 2) {
            return Double.NaN;
        }
        
        double meanX = calculateMean(xValues);
        double meanY = calculateMean(yValues);
        
        double sumXY = 0.0;
        double sumXSquared = 0.0;
        double sumYSquared = 0.0;
        
        for (int i = 0; i < xValues.length; i++) {
            double diffX = xValues[i] - meanX;
            double diffY = yValues[i] - meanY;

            sumXY += diffX * diffY;
            sumXSquared += diffX * diffX;
            sumYSquared += diffY * diffY;
        }
        
        double denominator = Math.sqrt(sumXSquared * sumYSquared);      
        if (denominator == 0.0) {
            return Double.NaN;
        }
        
        return sumXY / denominator;
    }
    
    /**
     * Calculates the range of a dataset.
     * Range is the difference between the maximum and minimum values.  
     * 
     * @param values Array of double values
     * @return The range, or 0.0 if array is empty
     */
    public static double calculateRange(double[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        
        double min = values[0];
        double max = values[0];
        
        for (double value : values) {
            if (value < min) min = value;
            if (value > max) max = value;
        }
        
        return max - min;
    }
    
    /**
     * Calculates the quartiles of a dataset.
     * Returns an array with [Q1, Q2 (median), Q3].
     * 
     * @param values Array of double values
     * @return Array with [Q1, Q2, Q3], or null if dataset is invalid   
     */
    public static double[] calculateQuartiles(double[] values) {        
        if (values == null || values.length < 3) {
            return null;
        }
        
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        
        double q2 = calculateMedian(sorted);
        
        int middle = sorted.length / 2;
        double[] lowerHalf = Arrays.copyOfRange(sorted, 0, middle);     
        double[] upperHalf;
        
        if (sorted.length % 2 == 0) {
            upperHalf = Arrays.copyOfRange(sorted, middle, sorted.length);
        } else {
            upperHalf = Arrays.copyOfRange(sorted, middle + 1, sorted.length);
        }
        
        double q1 = calculateMedian(lowerHalf);
        double q3 = calculateMedian(upperHalf);
        
        return new double[]{q1, q2, q3};
    }
    
    /**
     * Calculates the interquartile range (IQR) of a dataset.
     * IQR is the difference between Q3 and Q1.
     * 
     * @param values Array of double values
     * @return The IQR, or Double.NaN if dataset is invalid
     */
    public static double calculateIQR(double[] values) {
        double[] quartiles = calculateQuartiles(values);
        if (quartiles == null) {
            return Double.NaN;
        }
        return quartiles[2] - quartiles[0];
    }
    
    /**
     * Identifies outliers in a dataset using the IQR method.
     * An outlier is a value that is more than 1.5 * IQR away from Q1 or Q3.
     * 
     * @param values Array of double values
     * @return List of outlier values
     */
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
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;
        
        for (double value : values) {
            if (value < lowerBound || value > upperBound) {
                outliers.add(value);
            }
        }
        
        return outliers;
    }
    
    /**
     * Calculates linear regression coefficients (slope and intercept) for two datasets.
     * Returns an array with [slope, intercept] for the equation y = slope * x + intercept.
     * 
     * @param xValues Independent variable values
     * @param yValues Dependent variable values
     * @return Array with [slope, intercept], or null if datasets are invalid
     */
    public static double[] calculateLinearRegression(double[] xValues, double[] yValues) {
        if (xValues == null || yValues == null || 
            xValues.length != yValues.length || 
            xValues.length < 2) {
            return null;
        }
        
        double meanX = calculateMean(xValues);
        double meanY = calculateMean(yValues);
        
        double sumXY = 0.0;
        double sumXSquared = 0.0;
        
        for (int i = 0; i < xValues.length; i++) {
            double diffX = xValues[i] - meanX;
            double diffY = yValues[i] - meanY;

            sumXY += diffX * diffY;
            sumXSquared += diffX * diffX;
        }
        
        if (sumXSquared == 0.0) {
            return null; // All x values are the same
        }
        
        double slope = sumXY / sumXSquared;
        double intercept = meanY - slope * meanX;
        
        return new double[]{slope, intercept};
    }
    
    /**
     * Calculates the coefficient of variation (CV).
     * CV is the ratio of standard deviation to mean, expressed as a percentage.
     * Useful for comparing variability between datasets with different means.
     * 
     * @param values Array of double values
     * @return The coefficient of variation as a percentage, or Double.NaN if mean is 0
     */
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
    
    /**
     * Calculates a simple moving average for a dataset.
     * 
     * @param values Array of double values
     * @param windowSize Size of the moving average window
     * @return Array of moving average values, or null if window size is invalid
     */
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
    
    /**
     * Calculates the z-score for a value relative to a dataset.        
     * Z-score indicates how many standard deviations a value is from the mean.
     * 
     * @param value The value to calculate z-score for
     * @param values The dataset
     * @return The z-score, or Double.NaN if dataset is invalid
     */
    public static double calculateZScore(double value, double[] values) {
        if (values == null || values.length == 0) {
            return Double.NaN;
        }
        
        double mean = calculateMean(values);
        double stdDev = calculateStandardDeviation(values);
        
        if (stdDev == 0.0) {
            return Double.NaN;
        }
        
        return (value - mean) / stdDev;
    }
    
    /**
     * Generates a statistical summary for a dataset.
     * Returns a formatted string with key statistics.
     * 
     * @param values Array of double values
     * @return Formatted statistical summary string
     */
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
}

