package ui;

// κλάση για προσομοιώσεις & υποθετικά σενάρια.
public class Simulations {
    
    private Simulations() {}
        
    // σενάριο ποσοστιαίας αύξησης εσόδων
    public static String simulateRevenueIncrease(double currentRevenue, double currentExpenses, double revenueIncreasePercent) {
        // υπολογισμός νέων εσόδων
        double newRevenue = currentRevenue * (1 + revenueIncreasePercent / 100.0);
        // υπολογισμός νέου ισοζυγίου 
        double newBalance = newRevenue - currentExpenses;
        // υπολογισμός μεταβολής ισοζυγίου 
        double balanceChange = newBalance - (currentRevenue - currentExpenses);
        
        // μήνυμα αποτελέσματος
        StringBuilder result = new StringBuilder();
        result.append("Σενάριο: Αύξηση εσόδων κατά ").append(String.format("%.2f", revenueIncreasePercent)).append("%\n\n");
        result.append("Τρέχοντα Έσοδα: ").append(AmountFormatter.formatCurrency(currentRevenue)).append("\n");
        result.append("Νέα Έσοδα: ").append(AmountFormatter.formatCurrency(newRevenue)).append("\n");
        result.append("Διαφορά: ").append(AmountFormatter.formatCurrency(newRevenue - currentRevenue)).append("\n\n");
        result.append("Τρέχοντα Έξοδα: ").append(AmountFormatter.formatCurrency(currentExpenses)).append("\n");
        result.append("Νέο Ισοζύγιο: ").append(AmountFormatter.formatCurrency(newBalance)).append("\n");
        result.append("Αλλαγή Ισοζυγίου: ").append(AmountFormatter.formatCurrency(balanceChange)).append("\n\n");
        result.append(StatisticalAnalysis.analyzeBudget(newBalance));
        
        return result.toString();
    }
    
    // σενάριο ποσοστιαίας μείωσης δαπανών
    public static String simulateExpenseDecrease(double currentRevenue, double currentExpenses, double expenseDecreasePercent) {
        // υπολογισμός νέων δαπανών 
        double newExpenses = currentExpenses * (1 - expenseDecreasePercent / 100.0);
        // υπολογισμός νέου ισοζυγίου
        double newBalance = currentRevenue - newExpenses;
        // υπολογισμός μεταβολής ισοζυγίου
        double balanceChange = newBalance - (currentRevenue - currentExpenses);
        
        // μήνυμα αποτελέσματος
        StringBuilder result = new StringBuilder();
        result.append("Σενάριο: Μείωση δαπανών κατά ").append(String.format("%.2f", expenseDecreasePercent)).append("%\n\n");
        result.append("Τρέχοντα Έσοδα: ").append(AmountFormatter.formatCurrency(currentRevenue)).append("\n");
        result.append("Τρέχοντα Έξοδα: ").append(AmountFormatter.formatCurrency(currentExpenses)).append("\n");
        result.append("Νέα Έξοδα: ").append(AmountFormatter.formatCurrency(newExpenses)).append("\n");
        result.append("Εξοικονόμηση: ").append(AmountFormatter.formatCurrency(currentExpenses - newExpenses)).append("\n\n");
        result.append("Νέο Ισοζύγιο: ").append(AmountFormatter.formatCurrency(newBalance)).append("\n");
        result.append("Αλλαγή Ισοζυγίου: ").append(AmountFormatter.formatCurrency(balanceChange)).append("\n\n");
        result.append(StatisticalAnalysis.analyzeBudget(newBalance));
        
        return result.toString();
    }
    
    // σενάριο αλλαγών σε έσοδα και δαπάνες 
    public static String simulateCombinedScenario(double currentRevenue, double currentExpenses,
                                                   double revenueChangePercent, double expenseChangePercent) {
        // υπολογισμός νέων εσόδων και δαπανών 
        double newRevenue = currentRevenue * (1 + revenueChangePercent / 100.0);
        double newExpenses = currentExpenses * (1 + expenseChangePercent / 100.0);
        // υπολογισμός νέου και τρέχοντος ισοζυγίου
        double newBalance = newRevenue - newExpenses;
        double currentBalance = currentRevenue - currentExpenses;
        // υπολογισμός μεταβολής ισοζυγίου
        double balanceChange = newBalance - currentBalance;
        
        // μήνυμα αποτελέσματος
        StringBuilder result = new StringBuilder();
        result.append("Σενάριο: Συνδυασμένη Αλλαγή\n\n");
        result.append("Αλλαγή Εσόδων: ").append(String.format("%.2f", revenueChangePercent)).append("%\n");
        result.append("Αλλαγή Δαπανών: ").append(String.format("%.2f", expenseChangePercent)).append("%\n\n");
        
        result.append("Τρέχοντα Έσοδα: ").append(AmountFormatter.formatCurrency(currentRevenue)).append("\n");
        result.append("Νέα Έσοδα: ").append(AmountFormatter.formatCurrency(newRevenue)).append("\n");
        result.append("Διαφορά: ").append(AmountFormatter.formatCurrency(newRevenue - currentRevenue)).append("\n\n");
        
        result.append("Τρέχοντα Έξοδα: ").append(AmountFormatter.formatCurrency(currentExpenses)).append("\n");
        result.append("Νέα Έξοδα: ").append(AmountFormatter.formatCurrency(newExpenses)).append("\n");
        result.append("Διαφορά: ").append(AmountFormatter.formatCurrency(newExpenses - currentExpenses)).append("\n\n");
        
        result.append("Τρέχον Ισοζύγιο: ").append(AmountFormatter.formatCurrency(currentBalance)).append("\n");
        result.append("Νέο Ισοζύγιο: ").append(AmountFormatter.formatCurrency(newBalance)).append("\n");
        result.append("Αλλαγή Ισοζυγίου: ").append(AmountFormatter.formatCurrency(balanceChange)).append("\n\n");
        result.append(StatisticalAnalysis.analyzeBudget(newBalance));
        
        return result.toString();
    }
    
    // προσομοιώνει προϋπολογισμό μελλοντικού έτους με βάση ιστορικές τάσεις.
    public static String simulateFutureYear(int currentYear, int futureYear,
                                             double currentRevenue, double currentExpenses,
                                             double revenueTrendPerYear, double expenseTrendPerYear) {
        // υπολογισμός διαφοράς ετών
        int yearsAhead = futureYear - currentYear;
        // έλεγχος εγκυρότητας (το μελλοντικό έτος πρέπει να είναι μεγαλύτερο)
        if (yearsAhead <= 0) {
            return "Το μελλοντικό έτος πρέπει να είναι μεγαλύτερο από το τρέχον έτος.";
        }
        
        // έσοδα & δαπάνες με βάση τις ετήσιες τάσεις
        double projectedRevenue = currentRevenue + (revenueTrendPerYear * yearsAhead);
        double projectedExpenses = currentExpenses + (expenseTrendPerYear * yearsAhead);
        // υπολογισμός προβλεπόμενου και τρέχοντος ισοζυγίου
        double projectedBalance = projectedRevenue - projectedExpenses;
        double currentBalance = currentRevenue - currentExpenses;
        
        // μήνυμα αποτελέσματος
        StringBuilder result = new StringBuilder();
        result.append("Προβολή Προϋπολογισμού για το έτος ").append(futureYear).append("\n");
        result.append("(Βασισμένη σε τάσεις από το ").append(currentYear).append(")\n\n");
        
        result.append("Τρέχοντα Έσοδα (").append(currentYear).append("): ");
        result.append(AmountFormatter.formatCurrency(currentRevenue)).append("\n");
        result.append("Προβλεπόμενα Έσοδα (").append(futureYear).append("): ");
        result.append(AmountFormatter.formatCurrency(projectedRevenue)).append("\n");
        result.append("Μέση Ετήσια Αλλαγή: ").append(AmountFormatter.formatCurrency(revenueTrendPerYear)).append("\n\n");
        
        result.append("Τρέχοντα Έξοδα (").append(currentYear).append("): ");
        result.append(AmountFormatter.formatCurrency(currentExpenses)).append("\n");
        result.append("Προβλεπόμενα Έξοδα (").append(futureYear).append("): ");
        result.append(AmountFormatter.formatCurrency(projectedExpenses)).append("\n");
        result.append("Μέση Ετήσια Αλλαγή: ").append(AmountFormatter.formatCurrency(expenseTrendPerYear)).append("\n\n");
        
        result.append("Τρέχον Ισοζύγιο: ").append(AmountFormatter.formatCurrency(currentBalance)).append("\n");
        result.append("Προβλεπόμενο Ισοζύγιο: ").append(AmountFormatter.formatCurrency(projectedBalance)).append("\n");
        result.append("Αλλαγή: ").append(AmountFormatter.formatCurrency(projectedBalance - currentBalance)).append("\n\n");
        result.append(StatisticalAnalysis.analyzeBudget(projectedBalance));
        
        return result.toString();
    }
    
    //προσομοιώνει τι θα συνέβαινε αν οι δαπάνες μιας συγκεκριμένης κατηγορίας μειώνονταν κατά ένα ποσοστό
    public static String simulateCategoryExpenseReduction(String categoryName, double currentCategoryExpense,
                                                          double totalCurrentExpenses, double totalRevenue,
                                                          double reductionPercent) {
        // υπολογισμός ποσού μείωσης για την κατηγορία
        double reduction = currentCategoryExpense * (reductionPercent / 100.0);
        // υπολογισμός νέων δαπανών κατηγορίας και συνολικών δαπανών
        double newCategoryExpense = currentCategoryExpense - reduction;
        double newTotalExpenses = totalCurrentExpenses - reduction;
        // υπολογισμός νέου και τρέχοντος ισοζυγίου
        double newBalance = totalRevenue - newTotalExpenses;
        double currentBalance = totalRevenue - totalCurrentExpenses;
        
        // μήνυμα αποτελέσματος
        StringBuilder result = new StringBuilder();
        result.append("Σενάριο: Μείωση Δαπανών Κατηγορίας\n\n");
        result.append("Κατηγορία: ").append(categoryName).append("\n");
        result.append("Μείωση: ").append(String.format("%.2f", reductionPercent)).append("%\n\n");
        
        result.append("Τρέχοντα Έξοδα Κατηγορίας: ").append(AmountFormatter.formatCurrency(currentCategoryExpense)).append("\n");
        result.append("Νέα Έξοδα Κατηγορίας: ").append(AmountFormatter.formatCurrency(newCategoryExpense)).append("\n");
        result.append("Εξοικονόμηση: ").append(AmountFormatter.formatCurrency(reduction)).append("\n\n");
        
        result.append("Συνολικά Τρέχοντα Έξοδα: ").append(AmountFormatter.formatCurrency(totalCurrentExpenses)).append("\n");
        result.append("Νέα Συνολικά Έξοδα: ").append(AmountFormatter.formatCurrency(newTotalExpenses)).append("\n\n");
        
        result.append("Τρέχον Ισοζύγιο: ").append(AmountFormatter.formatCurrency(currentBalance)).append("\n");
        result.append("Νέο Ισοζύγιο: ").append(AmountFormatter.formatCurrency(newBalance)).append("\n");
        result.append("Βελτίωση: ").append(AmountFormatter.formatCurrency(newBalance - currentBalance)).append("\n\n");
        result.append(StatisticalAnalysis.analyzeBudget(newBalance));
        
        return result.toString();
    }
}
