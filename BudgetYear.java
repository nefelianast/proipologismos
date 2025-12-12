package cac_cor;
import java.util.ArrayList;
import java.util.List;

public class BudgetYear {
    private int year;
    private List<Ministry> ministries;
    

     public BudgetYear(int year) {
        this.year = year;
        this.ministries = new ArrayList<>();
     }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
     }

    public List<Ministry> getMinistries() {
        return ministries;
    }

     public void addMinistry(Ministry m) {
        if (m != null) {
            ministries.add(m);
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
