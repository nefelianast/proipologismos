public class BifBudget4 {
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
        double revenue = 74_600_000_000.0 + 500_000_000.0;
        double expenses = 79_000_000_000.0 + 800_000_000.0;

        double deficit = expenses - revenue;

        System.out.printf("Έλλειμμα μετά την αύξηση κατώτατου μισθού",
                deficit / 1_000_000_000);

        BifBudget4 b = new BifBudget4();

        double lastYearBudget = 75_000_000_000.0;
        double thisYearBudget = revenue;

        String comparison = b.difPresBudget(thisYearBudget, lastYearBudget);
        System.out.println("Σύγκριση προϋπολογισμών:" + comparison);
    }
}
