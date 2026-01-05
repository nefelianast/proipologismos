package ui;

/**
 * Utility class for budget statistics calculations.
 * Provides static methods for calculating percentages, changes, balances, and other budget statistics.
 */
public class BudgetStatisticsCalculator {
    
    /**
     * Private constructor to prevent instantiation
     */
    private BudgetStatisticsCalculator() {
        // Utility class - no instantiation needed
    }
    
    /**
     * Calculates the percentage change between two values
     * @param currentValue The current value
     * @param previousValue The previous value
     * @return The percentage change (positive for increase, negative for decrease)
     *         Returns 0.0 if previousValue is 0 to avoid division by zero
     */
    public static double calculatePercentageChange(double currentValue, double previousValue) {
        if (previousValue == 0) {
            return 0.0;
        }
        return ((currentValue - previousValue) / previousValue) * 100;
    }
    
    /**
     * Calculates the balance (surplus or deficit) between revenues and expenses
     * @param revenues Total revenues
     * @param expenses Total expenses
     * @return The balance (positive for surplus, negative for deficit)
     */
    public static double calculateBalance(double revenues, double expenses) {
        return revenues - expenses;
    }
    
    /**
     * Calculates the percentage of an amount relative to a total
     * @param amount The amount to calculate percentage for
     * @param total The total amount
     * @return The percentage (0.0 if total is 0)
     */
    public static double calculatePercentage(double amount, double total) {
        if (total == 0) {
            return 0.0;
        }
        return (amount / total) * 100;
    }
    
    /**
     * Calculates the total of an array of values
     * @param values Array of double values
     * @return The sum of all values
     */
    public static double calculateTotal(double[] values) {
        double sum = 0;
        for (double value : values) {
            sum += value;
        }
        return sum;
    }
    
    /**
     * Calculates the average of an array of values
     * @param values Array of double values
     * @return The average (0.0 if array is empty)
     */
    public static double calculateAverage(double[] values) {
        if (values.length == 0) {
            return 0.0;
        }
        return calculateTotal(values) / values.length;
    }
    
    /**
     * Calculates the growth rate between two values
     * @param currentValue The current value
     * @param previousValue The previous value
     * @return The growth rate as a percentage (0.0 if previousValue is 0)
     */
    public static double calculateGrowthRate(double currentValue, double previousValue) {
        return calculatePercentageChange(currentValue, previousValue);
    }
    
    /**
     * Determines if a change is significant based on a threshold
     * @param percentageChange The percentage change
     * @param threshold The threshold percentage (default: 5.0)
     * @return true if the change is significant (absolute value > threshold)
     */
    public static boolean isSignificantChange(double percentageChange, double threshold) {
        return Math.abs(percentageChange) > threshold;
    }
    
    /**
     * Determines if a change is significant (using default threshold of 5.0%)
     * @param percentageChange The percentage change
     * @return true if the change is significant
     */
    public static boolean isSignificantChange(double percentageChange) {
        return isSignificantChange(percentageChange, 5.0);
    }
}
