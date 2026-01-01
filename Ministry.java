package cac_cor;

import java.util.ArrayList;
import java.util.List;

public class Ministry {
    private String name;
    private List<Revenue> revenues;
    private List<Expense> expenses;

     public Ministry(String name) {
        this.name = name;
        this.revenues = new ArrayList<>();
        this.expenses = new ArrayList<>();

    }
    
    public String getName() {
       return name;
    }
     public void addRevenue(Revenue r) {
        if (r != null) {
            revenues.add(r);
        }
    }
    public void addExpense(Expense e) {
        if (e != null) {
            expenses.add(e);
        }
    }
     public List<Revenue> getRevenues() {
        return revenues;
    }

    // Λίστα εξόδων
    public List<Expense> getExpenses() {
        return expenses;
    }
    public double getTotalRevenue() {
        double sum = 0;
        for (Revenue r : revenues) {
            sum += r.getAmount(); // Προσθέτουμε κάθε έσοδο
        }
        return sum;
    }
    public double getTotalExpenses() {
        double sum = 0;
        for (Expense e : expenses) {
            sum += e.getAmount(); // Προσθέτουμε κάθε έξοδο
        }
        return sum;
    }
     public double getBalance() {
        return getTotalRevenue() - getTotalExpenses();
    }
// epistrefei poso sygkekrimenis katigorias esodon
public double getRevenueAmountByCategory(String categoryName) {
    for (Revenue r : revenues) {
        if (r.getCategory().equalsIgnoreCase(categoryName)) {
            return r.getAmount();
        }
    }
    return 0;
}

// epistrefei poso sygkekrimenis katigorias eksodon
public double getExpenseAmountByCategory(String categoryName) {
    for (Expense e : expenses) {
        if (e.getCategory().equalsIgnoreCase(categoryName)) {
            return e.getAmount();
        }
    }
    return 0;
}
}


