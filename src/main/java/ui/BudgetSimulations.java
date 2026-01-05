package ui;

/**
 * Class for budget simulations and hypothetical scenarios.
 * Provides methods for analyzing budget status, comparing budgets,
 * and creating simulation scenarios for future budget planning.
 */
public class BudgetSimulations {
    
    private BudgetSimulations() {
        // Utility class - no instantiation needed
    }
    
    /**
     * Analyzes a budget balance and returns a descriptive message about the budget status.
     * 
     * @param balance The budget balance (revenues - expenses)
     * @return A descriptive message in Greek about whether the budget is surplus, balanced, or deficit
     */
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
    
    /**
     * Compares two budget years and returns a detailed comparison message.
     * 
     * @param year1 First year identifier
     * @param year2 Second year identifier
     * @param balance1 Balance for year 1
     * @param balance2 Balance for year 2
     * @param revenue1 Total revenue for year 1
     * @param revenue2 Total revenue for year 2
     * @param expense1 Total expenses for year 1
     * @param expense2 Total expenses for year 2
     * @return A detailed comparison message in Greek
     */
    public static String compareBudgetsDetailed(int year1, int year2, 
                                                 double balance1, double balance2,
                                                 double revenue1, double revenue2,
                                                 double expense1, double expense2) {
        // Calculate percentage difference for balance
        double percentageDiff = 0;
        if (balance1 != 0) {
            percentageDiff = ((balance2 - balance1) / Math.abs(balance1)) * 100;
        }
        
        StringBuilder msg = new StringBuilder();
        msg.append("Σύγκριση προϋπολογισμού μεταξύ των ετών ").append(year1).append(" και ").append(year2).append(":\n\n");
        
        // Compare balance
        if (balance1 > balance2) {
            msg.append("Το έτος ").append(year1).append(" έχει καλύτερο οικονομικό αποτέλεσμα από το έτος ").append(year2).append(".\n");
            msg.append("Το ισοζύγιο είναι υψηλότερο κατά ").append(String.format("%.2f", Math.abs(percentageDiff))).append("%.\n\n");
        } else if (balance1 < balance2) {
            msg.append("Το έτος ").append(year2).append(" έχει καλύτερο οικονομικό αποτέλεσμα από το έτος ").append(year1).append(".\n");
            msg.append("Το ισοζύγιο είναι υψηλότερο κατά ").append(String.format("%.2f", Math.abs(percentageDiff))).append("%.\n\n");
        } else {
            msg.append("Τα έτη έχουν ακριβώς το ίδιο οικονομικό αποτέλεσμα.\n\n");
        }
        
        // Compare revenues
        if (revenue1 > revenue2) {
            msg.append("Τα έσοδα ήταν υψηλότερα στο έτος ").append(year1).append(" (");
            msg.append(String.format("%.2f", revenue1)).append(" έναντι ").append(String.format("%.2f", revenue2)).append(").\n");
        } else if (revenue1 < revenue2) {
            msg.append("Τα έσοδα ήταν υψηλότερα στο έτος ").append(year2).append(" (");
            msg.append(String.format("%.2f", revenue2)).append(" έναντι ").append(String.format("%.2f", revenue1)).append(").\n");
        } else {
            msg.append("Τα έσοδα ήταν ίδια και στα δύο έτη (").append(String.format("%.2f", revenue1)).append(").\n");
        }
        
        // Compare expenses
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
    
    /**
     * Compares expense amounts for a specific category between two years.
     * 
     * @param year1 First year identifier
     * @param year2 Second year identifier
     * @param category Category name
     * @param amount1 Expense amount for year 1
     * @param amount2 Expense amount for year 2
     * @return A comparison message in Greek, or error message if data is missing
     */
    public static String compareExpenseByCategory(int year1, int year2, String category, 
                                                   Double amount1, Double amount2) {
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
        msg.append("Σύγκριση κατηγορίας '").append(category).append("' μεταξύ των ετών ");
        msg.append(year1).append(" και ").append(year2).append(":\n");
        msg.append("Έτος ").append(year1).append("  Ποσό: ").append(String.format("%.2f", amount1)).append("\n");
        msg.append("Έτος ").append(year2).append("  Ποσό: ").append(String.format("%.2f", amount2)).append("\n");
        
        if (amount2 > amount1) {
            msg.append("Το ποσό αυξήθηκε κατά ").append(String.format("%.2f", percentageChange));
            msg.append("% σε σχέση με το προηγούμενο έτος.\n");
            msg.append("Η κατηγορία έχει μεγαλύτερη επιβάρυνση.");
        } else if (amount2 < amount1) {
            msg.append("Το ποσό μειώθηκε κατά ").append(String.format("%.2f", Math.abs(percentageChange)));
            msg.append("% σε σχέση με το προηγούμενο έτος.\n");
            msg.append("Η κατηγορία έχει μικρότερη επιβάρυνση.");
        } else {
            msg.append("Δεν παρατηρείται αλλαγή στο ποσό της κατηγορίας μεταξύ των δύο ετών.");
        }
        
        return msg.toString();
    }
    
    /**
     * Compares revenue amounts for a specific category between two years.
     * 
     * @param year1 First year identifier
     * @param year2 Second year identifier
     * @param category Category name
     * @param amount1 Revenue amount for year 1
     * @param amount2 Revenue amount for year 2
     * @return A comparison message in Greek, or error message if data is missing
     */
    public static String compareRevenueByCategory(int year1, int year2, String category,
                                                   Double amount1, Double amount2) {
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
        msg.append("Σύγκριση εσόδων για την κατηγορία '").append(category).append("' μεταξύ των ετών ");
        msg.append(year1).append(" και ").append(year2).append(":\n");
        msg.append("Έτος ").append(year1).append(" Ποσό: ").append(String.format("%.2f", amount1)).append("\n");
        msg.append("Έτος ").append(year2).append(" Ποσό: ").append(String.format("%.2f", amount2)).append("\n");
        
        if (amount2 > amount1) {
            msg.append("Τα έσοδα αυξήθηκαν κατά ").append(String.format("%.2f", percentageChange));
            msg.append("% σε σχέση με το προηγούμενο έτος.\n");
            msg.append("Η κατηγορία έχει μεγαλύτερη εισροή εσόδων.");
        } else if (amount2 < amount1) {
            msg.append("Τα έσοδα μειώθηκαν κατά ").append(String.format("%.2f", Math.abs(percentageChange)));
            msg.append("% σε σχέση με το προηγούμενο έτος.\n");
            msg.append("Η κατηγορία έχει μικρότερη εισροή εσόδων.");
        } else {
            msg.append("Δεν παρατηρείται αλλαγή στα έσοδα της κατηγορίας μεταξύ των δύο ετών.");
        }
        
        return msg.toString();
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
        result.append(analyzeBudget(newBalance));
        
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
        result.append(analyzeBudget(newBalance));
        
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
        result.append(analyzeBudget(newBalance));
        
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
        result.append(analyzeBudget(projectedBalance));
        
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
        result.append(analyzeBudget(newBalance));
        
        return result.toString();
    }
}
