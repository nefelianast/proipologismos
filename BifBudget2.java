// Κλάση υπολογισμού διαφορών
public class BifBudget2 {
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

        // μείωση ΦΠΑ κατά 2 μονάδες
        double vatIncrease = 2.0;
        double increaseAmount = revenue * (vatIncrease / 100.0);
        revenue -= increaseAmount;

        double deficit = expenses - revenue;
        System.out.printf("Έλλειμμα μετά τη μείωση ΦΠΑ: %.2f δισ", deficit / 1_000_000_000);

        BifBudget2 b = new BifBudget2();

        double lastYearBudget = 75_000_000_000.0;
        double thisYearBudget = revenue;

        String comparison = b.difPresBudget(thisYearBudget, lastYearBudget);
        System.out.println("Σύγκριση προϋπολογισμών: " + comparison);
    }
}
