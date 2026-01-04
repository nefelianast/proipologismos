// Κλάση υπολογισμού διαφορών
public class BifBudget {

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

        // Δεδομένα
        double revenue = 74_600_000_000.0; // έσοδα
        double expenses = 79_000_000_000.0; // έξοδα

        // Υποθετική αύξηση ΦΠΑ κατά 1%
        double vatIncrease = 1.0;
        double increaseAmount = revenue * (vatIncrease / 100.0);
        revenue += increaseAmount;

        // ελλημα
        double deficit = expenses - revenue;
        System.out.printf("Έλλειμμα μετά την αύξηση ΦΠΑ: %.2f δισ.\n", deficit / 1_000_000_000);

        // σύγκριση προϋπολογισμών
        BifBudget b = new BifBudget();

        double lastYearBudget = 75_000_000_000.0;
        double thisYearBudget = revenue;

        String comparison = b.difPresBudget(thisYearBudget, lastYearBudget);
        System.out.println("Σύγκριση προϋπολογισμών: " + comparison);
    }
}