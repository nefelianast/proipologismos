package cac_cor;
import java.util.ArrayList;
import java.util.List;

public class BudgetYear {
    private int year;
    private List<Revenue> revenues;
    private List<Expense> expenses;

     public BudgetYear(int year) {
        this.year = year;
        this.revenues = new ArrayList<>();
        this.expenses = new ArrayList<>();
     }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
     }

    public List<Revenue> getRevenues() { 
        return revenues;
    }

    public List<Expense> getExpenses() {
        return expenses;
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

    // calculations//
    public double getTotalRevenue() {
        double sum = 0;
        for (Revenue r : revenues) {
              sum += r.getAmount();
        }
        return sum;
    }

    public double getTotalExpenses() {
        double sum = 0;
        for (Expense e : expenses) {
              sum += e.getAmount();
        }
        return sum;
    }

    public double getBalance() {
        return getTotalRevenue() - getTotalExpenses();
    }

    

}
