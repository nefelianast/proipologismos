package ui;

import java.text.DecimalFormat;

/**
 * Utility class for formatting budget amounts and percentages.
 * Provides centralized formatting methods for currency, percentages, and change displays.
 */
public class BudgetAmountFormatter {
    
    /**
     * DecimalFormat for currency amounts with thousand separators
     */
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.0");
    
    /**
     * DecimalFormat for currency amounts without decimals
     */
    private static final DecimalFormat CURRENCY_FORMAT_NO_DECIMALS = new DecimalFormat("#,##0");
    
    /**
     * Private constructor to prevent instantiation
     */
    private BudgetAmountFormatter() {
        // Utility class - no instantiation needed
    }
    
    /**
     * Formats a currency amount with Euro symbol and thousand separators
     * @param amount The amount to format
     * @return Formatted string (e.g., "€1,234.5")
     */
    public static String formatCurrency(double amount) {
        return "€" + CURRENCY_FORMAT.format(amount);
    }
    
    /**
     * Formats a currency amount with Euro symbol, thousand separators, and no decimals
     * @param amount The amount to format
     * @return Formatted string (e.g., "€1,234")
     */
    public static String formatCurrencyNoDecimals(double amount) {
        return "€" + CURRENCY_FORMAT_NO_DECIMALS.format(amount);
    }
    
    /**
     * Formats a percentage with 2 decimal places
     * @param percentage The percentage value (e.g., 15.5 for 15.5%)
     * @return Formatted string (e.g., "15.50%")
     */
    public static String formatPercentage(double percentage) {
        return String.format("%.2f%%", percentage);
    }
    
    /**
     * Formats a percentage with 1 decimal place
     * @param percentage The percentage value (e.g., 15.5 for 15.5%)
     * @return Formatted string (e.g., "15.5%")
     */
    public static String formatPercentageOneDecimal(double percentage) {
        return String.format("%.1f%%", percentage);
    }
    
    /**
     * Formats a percentage change with sign (+ or -) and 2 decimal places
     * @param change The percentage change value
     * @return Formatted string (e.g., "+5.25%" or "-3.10%")
     */
    public static String formatPercentageChange(double change) {
        return String.format("%+.2f%%", change);
    }
    
    /**
     * Formats a percentage change with sign (+ or -) and 1 decimal place
     * @param change The percentage change value
     * @return Formatted string (e.g., "+5.2%" or "-3.1%")
     */
    public static String formatPercentageChangeOneDecimal(double change) {
        return String.format("%+.1f%%", change);
    }
    
    /**
     * Formats a change display with both percentage and currency amount
     * @param changePercent The percentage change
     * @param changeValue The currency change amount
     * @return Formatted string (e.g., "5.25% (1,234.5 €)")
     */
    public static String formatChange(double changePercent, double changeValue) {
        return String.format("%.2f%% (%.2f €)", changePercent, changeValue);
    }
    
    /**
     * Formats a currency amount without Euro symbol (for internal use)
     * @param amount The amount to format
     * @return Formatted string (e.g., "1,234.5")
     */
    public static String formatAmount(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }
}
