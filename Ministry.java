public class Ministry {

    private String ministryName;
    private double lastYearUsage;
    private double thisYearUsage;

    public Ministry(String ministryName, double lastYearUsage, double thisYearUsage) {
        this.ministryName = ministryName;
        this.lastYearUsage = lastYearUsage;
        this.thisYearUsage = thisYearUsage;
    }

    public String compareUsage() {

        if (lastYearUsage == 0) {
            return ministryName + ": Δεν μπορεί να γίνει σύγκριση (πέρυσι = 0).";
        }

        double difference = thisYearUsage - lastYearUsage;
        double percent = (difference / lastYearUsage) * 100;

        if (difference > 0) {
            return ministryName + ": Αύξηση κατανάλωσης " + percent + "%";
        } else if (difference < 0) {
            return ministryName + ": Μείωση κατανάλωσης " + (-percent) + "%";
        } else {
            return ministryName + ": Ίδια κατανάλωση με πέρυσι.";
        }
    }

    public static void main(String[] args) {

        Ministry m1 = new Ministry("Υπουργείο Παιδείας", 40000, 45000);
        Ministry m2 = new Ministry("Υπουργείο Υγείας", 52000, 50000);
        Ministry m3 = new Ministry("Υπουργείο Οικονομικών", 60000, 60000);

        System.out.println(m1.compareUsage());
        System.out.println(m2.compareUsage());
        System.out.println(m3.compareUsage());

    }

}
