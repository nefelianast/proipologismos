public class BifBudget3 {
    public String difPresBudget(double budget1, double budget2) {
        if (budget2 != 0) {
            double dif = budget1 - budget2;
            double pre = (dif / budget2) * 100;

            if (dif > 0) {
                return "Αυξήθηκε από προηγούμενο έτος κατά " + pre + "%";
            } else {
                return "Μειώθηκε από προηγούμενο έτος κατά " + (-pre) + "%";
            }
        } else {
            return "Δεν μπορούσε να γίνει σύγκριση (budget2 = 0)";
        }
    }

    public static void main(String[] args) {
        double revenue = 74_600_000_000.0;
        double expenses = 79_000_000_000.0;

        // αύξηση επενδύσεων 20%
        double vatIncrease = 20.0;
        double increaseAmount = revenue * (vatIncrease / 100.0);
        revenue *= increaseAmount;

        double deficit = expenses - revenue;
        System.out.printf("Έλλειμμα με αύξηση επενδύσεων: %.2f δισ.", deficit / 1_000_000_000);
        BifBudget3 b = new BifBudget3();

        double lastYearBudget = 75_000_000_000.0;
        double thisYearBudget = revenue;

        String comparison = b.difPresBudget(thisYearBudget, lastYearBudget);
        System.out.println("Σύγκριση προϋπολογισμών: " + comparison);
    }
}
