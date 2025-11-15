public class Revenue {
    private String category;   
    private double amount;     
    private int year;  

    public Revenue(String category, double amount, int year) {
        this.category = category;
        this.amount = amount;
        this.year = year;
    }
     public String getCategory() { 
        return category; 
    }
    public void setCategory(String category) {
         this.category = category; 
        }

    public double getAmount() {
         return amount; 
        }
    public void setAmount(double amount) {
         this.amount = amount; 
        }

    public int getYear() {
         return year;
         }
    public void setYear(int year) {
         this.year = year; 
        }
         @Override
    public String toString() {
        return "Revenue{" +
                "category='" + category + '\'' +
                ", amount=" + amount +
                ", year=" + year +
                '}';
    }
}