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

}
