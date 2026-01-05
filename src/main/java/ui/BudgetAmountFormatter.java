package ui;

import java.text.DecimalFormat;


public class BudgetAmountFormatter {
    
 
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.0");
    
   
    private static final DecimalFormat CURRENCY_FORMAT_NO_DECIMALS = new DecimalFormat("#,##0");
    
   
    private BudgetAmountFormatter() {
        
    }
    
  
    public static String formatCurrency(double amount) {
        return "€" + CURRENCY_FORMAT.format(amount);
    }
    
    
    public static String formatCurrencyNoDecimals(double amount) {
        return "€" + CURRENCY_FORMAT_NO_DECIMALS.format(amount);
    }
    
   
    public static String formatPercentage(double percentage) {
        return String.format("%.2f%%", percentage);
    }
    
 
    public static String formatPercentageOneDecimal(double percentage) {
        return String.format("%.1f%%", percentage);
    }
    
  
    public static String formatPercentageChange(double change) {
        return String.format("%+.2f%%", change);
    }
    
   
    public static String formatPercentageChangeOneDecimal(double change) {
        return String.format("%+.1f%%", change);
    }
    
    
    public static String formatChange(double changePercent, double changeValue) {
        return String.format("%.2f%% (%.2f €)", changePercent, changeValue);
    }
    
   
    public static String formatAmount(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }
}
