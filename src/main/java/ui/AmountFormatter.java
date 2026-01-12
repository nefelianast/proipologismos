package ui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

// κλάση για μορφοποίηση ποσών & ποσοστών

public class AmountFormatter {
    
    // ελληνικό format
    private static final DecimalFormatSymbols GREEK_SYMBOLS;
    
    static {
        GREEK_SYMBOLS = new DecimalFormatSymbols();
        GREEK_SYMBOLS.setGroupingSeparator('.');  // τελεία για χιλιάδες
        GREEK_SYMBOLS.setDecimalSeparator(',');   // κόμμα για δεκαδικά
    }
    
    // για ποσά με δεκαδικά 
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.0", GREEK_SYMBOLS);
    
    // για ποσά χωρίς δεκαδικά
    private static final DecimalFormat CURRENCY_FORMAT_NO_DECIMALS = new DecimalFormat("#,##0", GREEK_SYMBOLS);
    
   
    private AmountFormatter() {
        // utility class
    }
    
    //μορφοποιεί ένα δεκαδικό ποσό με σύμβολο € & τελείες  
    
    public static String formatCurrency(double amount) {
        return "€" + CURRENCY_FORMAT.format(amount);
    }
    
    //μορφοποιεί ένα μη δεκαδικό ποσό με σύμβολο € & τελείες  

    public static String formatCurrencyNoDecimals(double amount) {
        return "€" + CURRENCY_FORMAT_NO_DECIMALS.format(amount);
    }
    
    //μορφοποιεί ποσοστό με 2 δεκαδικά ψηφία
    
    public static String formatPercentage(double percentage) {
        return String.format("%.2f%%", percentage);
    }
    
   //μορφοποιεί ποσοστό με 1 δεκαδικό ψηφίο, αλλά δείχνει περισσότερα δεκαδικά αν είναι πολύ μικρό
    public static String formatPercentageOneDecimal(double percentage) {
        // Αν το ποσοστό είναι μικρότερο από 0.1, δείξε περισσότερα δεκαδικά
        if (percentage > 0 && percentage < 0.1) {
            // Χρησιμοποιούμε scientific notation ή περισσότερα δεκαδικά
            // Βρίσκουμε πόσα δεκαδικά χρειάζονται για να δείξουμε τουλάχιστον ένα σημαντικό ψηφίο
            if (percentage < 0.01) {
                return String.format("%.4f%%", percentage);
            } else if (percentage < 0.1) {
                return String.format("%.3f%%", percentage);
            }
        }
        return String.format("%.1f%%", percentage);
    }
    
    //μορφοποιεί μια ποσοστιαία αλλαγή με πρόσημο & 2 δεκαδικά ψηφία
 
    public static String formatPercentageChange(double change) {
        return String.format("%+.2f%%", change);
    }
    
    //μορφοποιεί μια ποσοστιαία αλλαγή με πρόσημο & 1 δεκαδικό ψηφίο
    public static String formatPercentageChangeOneDecimal(double change) {
        return String.format("%+.1f%%", change);
    }
    
    //μορφοποιεί μια αλλαγή με ποσοστό & χρηματικό ποσό
  
    public static String formatChange(double changePercent, double changeValue) {
        return String.format("%.2f%% (%s €)", changePercent, CURRENCY_FORMAT.format(changeValue));
    }
    
    //μορφοποιεί ένα ποσό χωρίς σύμβολο ευρώ 
    public static String formatAmount(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }
}
