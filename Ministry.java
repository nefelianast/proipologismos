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

}
