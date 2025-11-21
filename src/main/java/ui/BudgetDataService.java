package ui;

import org.json.JSONObject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class BudgetDataService {
    
    private static BudgetDataService instance;
    private Map<Integer, BudgetYearData> budgetData = new HashMap<>();
    
    private BudgetDataService() {
        loadData();
    }
    
    public static BudgetDataService getInstance() {
        if (instance == null) {
            instance = new BudgetDataService();
        }
        return instance;
    }
    
    private void loadData() {
            try {
            File jsonFile = new File("proipologismos.json");
            if (jsonFile.exists()) {
                String content = new String(Files.readAllBytes(Paths.get(jsonFile.getPath())));
                @SuppressWarnings("unused")
                JSONObject json = new JSONObject(content);
            }
        } catch (Exception e) {
            System.out.println("Could not load JSON file, using sample data: " + e.getMessage());
        }
        
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Sample data for 2023
        BudgetYearData data2023 = new BudgetYearData(2023);
        data2023.setTotalRevenues(95.8);
        data2023.setTotalExpenses(97.4);
        data2023.addCategory("Οικονομικά", 20.5, 21.0);
        data2023.addCategory("Κοινωνική Προστασία", 17.2, 17.7);
        data2023.addCategory("Εκπαίδευση", 14.8, 15.2);
        data2023.addCategory("Υγεία", 11.5, 11.8);
        data2023.addCategory("Άλλα", 12.2, 12.5);
        data2023.addCategory("Υποδομές", 9.8, 10.1);
        data2023.addCategory("Άμυνα", 8.0, 8.2);
        budgetData.put(2023, data2023);
        
        // Sample data for 2024
        BudgetYearData data2024 = new BudgetYearData(2024);
        data2024.setTotalRevenues(98.0);
        data2024.setTotalExpenses(97.4);
        data2024.addCategory("Οικονομικά", 21.3, 21.8);
        data2024.addCategory("Κοινωνική Προστασία", 17.8, 18.3);
        data2024.addCategory("Εκπαίδευση", 15.0, 15.4);
        data2024.addCategory("Υγεία", 12.2, 12.5);
        data2024.addCategory("Άλλα", 12.5, 12.8);
        data2024.addCategory("Υποδομές", 10.1, 10.4);
        data2024.addCategory("Άμυνα", 8.2, 8.4);
        budgetData.put(2024, data2024);
        
        // Sample data for 2025
        BudgetYearData data2025 = new BudgetYearData(2025);
        data2025.setTotalRevenues(100.2);
        data2025.setTotalExpenses(98.5);
        data2025.addCategory("Οικονομικά", 22.1, 22.4);
        data2025.addCategory("Κοινωνική Προστασία", 18.3, 18.6);
        data2025.addCategory("Εκπαίδευση", 15.2, 15.4);
        data2025.addCategory("Υγεία", 12.8, 13.0);
        data2025.addCategory("Άλλα", 12.7, 12.9);
        data2025.addCategory("Υποδομές", 10.4, 10.6);
        data2025.addCategory("Άμυνα", 8.5, 8.6);
        budgetData.put(2025, data2025);
    }
    
    /**
     * Get budget data for a specific year
     */
    public BudgetYearData getBudgetData(int year) {
        return budgetData.get(year);
    }
    
    /**
     * Get total revenues for a year
     */
    public double getTotalRevenues(int year) {
        BudgetYearData data = budgetData.get(year);
        return data != null ? data.getTotalRevenues() : 0.0;
    }
    
    /**
     * Get total expenses for a year
     */
    public double getTotalExpenses(int year) {
        BudgetYearData data = budgetData.get(year);
        return data != null ? data.getTotalExpenses() : 0.0;
    }
    
    /**
     * Get balance (surplus/deficit) for a year
     */
    public double getBalance(int year) {
        BudgetYearData data = budgetData.get(year);
        if (data == null) return 0.0;
        return data.getTotalRevenues() - data.getTotalExpenses();
    }
    
    /**
     * Get percentage change from previous year
     */
    public double getRevenuesChange(int year) {
        BudgetYearData current = budgetData.get(year);
        BudgetYearData previous = budgetData.get(year - 1);
        if (current == null || previous == null) return 0.0;
        return ((current.getTotalRevenues() - previous.getTotalRevenues()) / previous.getTotalRevenues()) * 100;
    }
    
    public double getExpensesChange(int year) {
        BudgetYearData current = budgetData.get(year);
        BudgetYearData previous = budgetData.get(year - 1);
        if (current == null || previous == null) return 0.0;
        return ((current.getTotalExpenses() - previous.getTotalExpenses()) / previous.getTotalExpenses()) * 100;
    }
    
    /**
     * Get category data for a year
     */
    public List<CategoryInfo> getCategories(int year) {
        BudgetYearData data = budgetData.get(year);
        if (data == null) return new ArrayList<>();
        return data.getCategories();
    }
    
    /**
     * Inner class to hold budget data for a year
     */
    public static class BudgetYearData {
        private int year;
        private double totalRevenues;
        private double totalExpenses;
        private List<CategoryInfo> categories = new ArrayList<>();
        
        public BudgetYearData(int year) {
            this.year = year;
        }
        
        public void setTotalRevenues(double totalRevenues) {
            this.totalRevenues = totalRevenues;
        }
        
        public void setTotalExpenses(double totalExpenses) {
            this.totalExpenses = totalExpenses;
        }
        
        public void addCategory(String name, double amount, double percentage) {
            categories.add(new CategoryInfo(name, amount, percentage));
        }
        
        public int getYear() { return year; }
        public double getTotalRevenues() { return totalRevenues; }
        public double getTotalExpenses() { return totalExpenses; }
        public List<CategoryInfo> getCategories() { return categories; }
    }
    
    /**
     * Inner class to hold category information
     */
    public static class CategoryInfo {
        private String name;
        private double amount;
        private double percentage;
        
        public CategoryInfo(String name, double amount, double percentage) {
            this.name = name;
            this.amount = amount;
            this.percentage = percentage;
        }
        
        public String getName() { return name; }
        public double getAmount() { return amount; }
        public double getPercentage() { return percentage; }
    }
}

