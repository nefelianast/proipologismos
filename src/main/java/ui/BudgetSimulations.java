package ui;

/**
 * Class for budget simulations and hypothetical scenarios.
 * Provides methods for creating simulation scenarios for future budget planning.
 * For budget comparisons and analysis, use StatisticalAnalysis class.
 */
public class BudgetSimulations {
    
    private BudgetSimulations() {
        // Utility class - no instantiation needed
    }
    
    // ========== SIMULATION METHODS ==========
    
    /**
     * Simulates a budget scenario with a percentage increase in revenues.
     * 
     * @param currentRevenue Current total revenue
     * @param currentExpenses Current total expenses
     * @param revenueIncreasePercent Percentage increase in revenues (e.g., 5.0 for 5%)
     * @return A simulation result message describing the new budget status
     */
    public static String simulateRevenueIncrease(double currentRevenue, double currentExpenses, double revenueIncreasePercent) {
        double newRevenue = currentRevenue * (1 + revenueIncreasePercent / 100.0);
        double newBalance = newRevenue - currentExpenses;
        double balanceChange = newBalance - (currentRevenue - currentExpenses);
        
        StringBuilder result = new StringBuilder();
        result.append("Σενάριο: Αύξηση εσόδων κατά ").append(String.format("%.2f", revenueIncreasePercent)).append("%\n\n");
        result.append("Τρέχοντα Έσοδα: ").append(BudgetAmountFormatter.formatCurrency(currentRevenue)).append("\n");
        result.append("Νέα Έσοδα: ").append(BudgetAmountFormatter.formatCurrency(newRevenue)).append("\n");
        result.append("Διαφορά: ").append(BudgetAmountFormatter.formatCurrency(newRevenue - currentRevenue)).append("\n\n");
        result.append("Τρέχοντα Έξοδα: ").append(BudgetAmountFormatter.formatCurrency(currentExpenses)).append("\n");
        result.append("Νέο Ισοζύγιο: ").append(BudgetAmountFormatter.formatCurrency(newBalance)).append("\n");
        result.append("Αλλαγή Ισοζυγίου: ").append(BudgetAmountFormatter.formatCurrency(balanceChange)).append("\n\n");
        result.append(StatisticalAnalysis.analyzeBudget(newBalance));
        
        return result.toString();
    }
    
    /**
     * Simulates a budget scenario with a percentage decrease in expenses.
     * 
     * @param currentRevenue Current total revenue
     * @param currentExpenses Current total expenses
     * @param expenseDecreasePercent Percentage decrease in expenses (e.g., 5.0 for 5%)
     * @return A simulation result message describing the new budget status
     */
    public static String simulateExpenseDecrease(double currentRevenue, double currentExpenses, double expenseDecreasePercent) {
        double newExpenses = currentExpenses * (1 - expenseDecreasePercent / 100.0);
        double newBalance = currentRevenue - newExpenses;
        double balanceChange = newBalance - (currentRevenue - currentExpenses);
        
        StringBuilder result = new StringBuilder();
        result.append("Σενάριο: Μείωση δαπανών κατά ").append(String.format("%.2f", expenseDecreasePercent)).append("%\n\n");
        result.append("Τρέχοντα Έσοδα: ").append(BudgetAmountFormatter.formatCurrency(currentRevenue)).append("\n");
        result.append("Τρέχοντα Έξοδα: ").append(BudgetAmountFormatter.formatCurrency(currentExpenses)).append("\n");
        result.append("Νέα Έξοδα: ").append(BudgetAmountFormatter.formatCurrency(newExpenses)).append("\n");
        result.append("Εξοικονόμηση: ").append(BudgetAmountFormatter.formatCurrency(currentExpenses - newExpenses)).append("\n\n");
        result.append("Νέο Ισοζύγιο: ").append(BudgetAmountFormatter.formatCurrency(newBalance)).append("\n");
        result.append("Αλλαγή Ισοζυγίου: ").append(BudgetAmountFormatter.formatCurrency(balanceChange)).append("\n\n");
        result.append(StatisticalAnalysis.analyzeBudget(newBalance));
        
        return result.toString();
    }
    
    /**
     * Simulates a combined scenario with changes in both revenues and expenses.
     * 
     * @param currentRevenue Current total revenue
     * @param currentExpenses Current total expenses
     * @param revenueChangePercent Percentage change in revenues (positive for increase, negative for decrease)
     * @param expenseChangePercent Percentage change in expenses (positive for increase, negative for decrease)
     * @return A simulation result message describing the new budget status
     */
    public static String simulateCombinedScenario(double currentRevenue, double currentExpenses,
                                                   double revenueChangePercent, double expenseChangePercent) {
        double newRevenue = currentRevenue * (1 + revenueChangePercent / 100.0);
        double newExpenses = currentExpenses * (1 + expenseChangePercent / 100.0);
        double newBalance = newRevenue - newExpenses;
        double currentBalance = currentRevenue - currentExpenses;
        double balanceChange = newBalance - currentBalance;
        
        StringBuilder result = new StringBuilder();
        result.append("Σενάριο: Συνδυασμένη Αλλαγή\n\n");
        result.append("Αλλαγή Εσόδων: ").append(String.format("%.2f", revenueChangePercent)).append("%\n");
        result.append("Αλλαγή Δαπανών: ").append(String.format("%.2f", expenseChangePercent)).append("%\n\n");
        
        result.append("Τρέχοντα Έσοδα: ").append(BudgetAmountFormatter.formatCurrency(currentRevenue)).append("\n");
        result.append("Νέα Έσοδα: ").append(BudgetAmountFormatter.formatCurrency(newRevenue)).append("\n");
        result.append("Διαφορά: ").append(BudgetAmountFormatter.formatCurrency(newRevenue - currentRevenue)).append("\n\n");
        
        result.append("Τρέχοντα Έξοδα: ").append(BudgetAmountFormatter.formatCurrency(currentExpenses)).append("\n");
        result.append("Νέα Έξοδα: ").append(BudgetAmountFormatter.formatCurrency(newExpenses)).append("\n");
        result.append("Διαφορά: ").append(BudgetAmountFormatter.formatCurrency(newExpenses - currentExpenses)).append("\n\n");
        
        result.append("Τρέχον Ισοζύγιο: ").append(BudgetAmountFormatter.formatCurrency(currentBalance)).append("\n");
        result.append("Νέο Ισοζύγιο: ").append(BudgetAmountFormatter.formatCurrency(newBalance)).append("\n");
        result.append("Αλλαγή Ισοζυγίου: ").append(BudgetAmountFormatter.formatCurrency(balanceChange)).append("\n\n");
        result.append(StatisticalAnalysis.analyzeBudget(newBalance));
        
        return result.toString();
    }
    
    /**
     * Simulates a future year budget based on historical trends.
     * Uses linear regression to project future revenues and expenses.
     * 
     * @param currentYear Current year
     * @param futureYear Year to project
     * @param currentRevenue Current total revenue
     * @param currentExpenses Current total expenses
     * @param revenueTrendPerYear Average annual revenue change (can be negative)
     * @param expenseTrendPerYear Average annual expense change (can be negative)
     * @return A simulation result message for the projected year
     */
    public static String simulateFutureYear(int currentYear, int futureYear,
                                             double currentRevenue, double currentExpenses,
                                             double revenueTrendPerYear, double expenseTrendPerYear) {
        int yearsAhead = futureYear - currentYear;
        if (yearsAhead <= 0) {
            return "Το μελλοντικό έτος πρέπει να είναι μεγαλύτερο από το τρέχον έτος.";
        }
        
        double projectedRevenue = currentRevenue + (revenueTrendPerYear * yearsAhead);
        double projectedExpenses = currentExpenses + (expenseTrendPerYear * yearsAhead);
        double projectedBalance = projectedRevenue - projectedExpenses;
        double currentBalance = currentRevenue - currentExpenses;
        
        StringBuilder result = new StringBuilder();
        result.append("Προβολή Προϋπολογισμού για το έτος ").append(futureYear).append("\n");
        result.append("(Βασισμένη σε τάσεις από το ").append(currentYear).append(")\n\n");
        
        result.append("Τρέχοντα Έσοδα (").append(currentYear).append("): ");
        result.append(BudgetAmountFormatter.formatCurrency(currentRevenue)).append("\n");
        result.append("Προβλεπόμενα Έσοδα (").append(futureYear).append("): ");
        result.append(BudgetAmountFormatter.formatCurrency(projectedRevenue)).append("\n");
        result.append("Μέση Ετήσια Αλλαγή: ").append(BudgetAmountFormatter.formatCurrency(revenueTrendPerYear)).append("\n\n");
        
        result.append("Τρέχοντα Έξοδα (").append(currentYear).append("): ");
        result.append(BudgetAmountFormatter.formatCurrency(currentExpenses)).append("\n");
        result.append("Προβλεπόμενα Έξοδα (").append(futureYear).append("): ");
        result.append(BudgetAmountFormatter.formatCurrency(projectedExpenses)).append("\n");
        result.append("Μέση Ετήσια Αλλαγή: ").append(BudgetAmountFormatter.formatCurrency(expenseTrendPerYear)).append("\n\n");
        
        result.append("Τρέχον Ισοζύγιο: ").append(BudgetAmountFormatter.formatCurrency(currentBalance)).append("\n");
        result.append("Προβλεπόμενο Ισοζύγιο: ").append(BudgetAmountFormatter.formatCurrency(projectedBalance)).append("\n");
        result.append("Αλλαγή: ").append(BudgetAmountFormatter.formatCurrency(projectedBalance - currentBalance)).append("\n\n");
        result.append(StatisticalAnalysis.analyzeBudget(projectedBalance));
        
        return result.toString();
    }
    
    /**
     * Simulates what would happen if a specific category's expenses were reduced by a percentage.
     * 
     * @param categoryName Name of the category
     * @param currentCategoryExpense Current expense for this category
     * @param totalCurrentExpenses Total current expenses
     * @param totalRevenue Total revenue
     * @param reductionPercent Percentage reduction (e.g., 10.0 for 10%)
     * @return A simulation result message
     */
    public static String simulateCategoryExpenseReduction(String categoryName, double currentCategoryExpense,
                                                          double totalCurrentExpenses, double totalRevenue,
                                                          double reductionPercent) {
        double reduction = currentCategoryExpense * (reductionPercent / 100.0);
        double newCategoryExpense = currentCategoryExpense - reduction;
        double newTotalExpenses = totalCurrentExpenses - reduction;
        double newBalance = totalRevenue - newTotalExpenses;
        double currentBalance = totalRevenue - totalCurrentExpenses;
        
        StringBuilder result = new StringBuilder();
        result.append("Σενάριο: Μείωση Δαπανών Κατηγορίας\n\n");
        result.append("Κατηγορία: ").append(categoryName).append("\n");
        result.append("Μείωση: ").append(String.format("%.2f", reductionPercent)).append("%\n\n");
        
        result.append("Τρέχοντα Έξοδα Κατηγορίας: ").append(BudgetAmountFormatter.formatCurrency(currentCategoryExpense)).append("\n");
        result.append("Νέα Έξοδα Κατηγορίας: ").append(BudgetAmountFormatter.formatCurrency(newCategoryExpense)).append("\n");
        result.append("Εξοικονόμηση: ").append(BudgetAmountFormatter.formatCurrency(reduction)).append("\n\n");
        
        result.append("Συνολικά Τρέχοντα Έξοδα: ").append(BudgetAmountFormatter.formatCurrency(totalCurrentExpenses)).append("\n");
        result.append("Νέα Συνολικά Έξοδα: ").append(BudgetAmountFormatter.formatCurrency(newTotalExpenses)).append("\n\n");
        
        result.append("Τρέχον Ισοζύγιο: ").append(BudgetAmountFormatter.formatCurrency(currentBalance)).append("\n");
        result.append("Νέο Ισοζύγιο: ").append(BudgetAmountFormatter.formatCurrency(newBalance)).append("\n");
        result.append("Βελτίωση: ").append(BudgetAmountFormatter.formatCurrency(newBalance - currentBalance)).append("\n\n");
        result.append(StatisticalAnalysis.analyzeBudget(newBalance));
        
        return result.toString();
    }
}
