package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class BudgetAnalysisService {

    private static final String DB =
            "jdbc:sqlite:src/main/resources/database/BudgetData.db";

    // ================== ΣΥΝΟΨΗ ==================
    public static long getTotalRevenue(int year) {
        return getSingleValue("budget_summary_" + year, "total_revenue");
    }

    public static long getTotalExpenses(int year) {
        return getSingleValue("budget_summary_" + year, "total_expenses");
    }

    public static long getBudgetResult(int year) {
        return getSingleValue("budget_summary_" + year, "budget_result");
    }

    // ================== ΣΥΓΚΡΙΣΗ ==================
    public static void compareYears(int year1, int year2) {

        long rev1 = getTotalRevenue(year1);
        long rev2 = getTotalRevenue(year2);

        long exp1 = getTotalExpenses(year1);
        long exp2 = getTotalExpenses(year2);

        long res1 = getBudgetResult(year1);
        long res2 = getBudgetResult(year2);

        System.out.println();
        System.out.println("==============================================");
        System.out.println("ΣΥΓΚΡΙΣΗ ΚΡΑΤΙΚΟΥ ΠΡΟΫΠΟΛΟΓΙΣΜΟΥ");
        System.out.println("Έτη: " + year1 + " vs " + year2);
        System.out.println("==============================================");

        printRow("Συνολικά Έσοδα", rev1, rev2);
        printRow("Συνολικά Έξοδα", exp1, exp2);
        printRow("Αποτέλεσμα Προϋπολογισμού", res1, res2);

        compareRevenues(year1, year2);
        compareExpenses(year1, year2);
        compareMinistries(year1, year2);
        compareDecentralizedAdmins(year1, year2);

        printConclusion(year1, year2, rev1, rev2, exp1, exp2, res1, res2);
        printNarrativeSummary(year1, year2, rev1, rev2, exp1, exp2, res1, res2); //ΜΟΛΙΣ
    }

    // ================== ΣΥΜΠΕΡΑΣΜΑ ==================
    private static void printConclusion(
            int year1,
            int year2,
            long rev1,
            long rev2,
            long exp1,
            long exp2,
            long res1,
            long res2
    ) {
        System.out.println();
        System.out.println("==============================================");
        System.out.println("ΣΥΜΠΕΡΑΣΜΑ");
        System.out.println("==============================================");

        double revPct = percentValue(rev1, rev2);
        double expPct = percentValue(exp1, exp2);

        if (rev2 > rev1) {
            System.out.printf("Το %d παρουσίασε αύξηση εσόδων (%.2f%%).%n", year2, revPct);
        } else {
            System.out.printf("Το %d παρουσίασε μείωση εσόδων (%.2f%%).%n", year2, revPct);
        }

        if (exp2 > exp1) {
            System.out.printf("Τα έξοδα αυξήθηκαν (%.2f%%).%n", expPct);
        } else {
            System.out.printf("Τα έξοδα μειώθηκαν (%.2f%%).%n", expPct);
        }

        if (res2 > res1) {
            System.out.println("Το δημοσιονομικό αποτέλεσμα βελτιώθηκε.");
        } else {
            System.out.println("Το δημοσιονομικό αποτέλεσμα επιδεινώθηκε.");
        }

        if (expPct > revPct) {
            System.out.println("Η αύξηση εξόδων ήταν ταχύτερη από την αύξηση εσόδων.");
        } else {
            System.out.println("Η αύξηση εσόδων ήταν ισχυρότερη από την αύξηση εξόδων.");
        }
    }

    // ================== ΕΣΟΔΑ ==================
    private static void compareRevenues(int year1, int year2) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("ΑΝΑΛΥΤΙΚΗ ΣΥΓΚΡΙΣΗ ΕΣΟΔΩΝ");
        System.out.println("==============================================");

        String[] columns = {
                "taxes",
                "social_contributions",
                "transfers",
                "sales_of_goods_and_services",
                "other_current_revenue"
        };

        String[] labels = {
                "Φόροι",
                "Κοινωνικές Εισφορές",
                "Μεταβιβάσεις",
                "Πωλήσεις αγαθών & υπηρεσιών",
                "Λοιπά τρέχοντα έσοδα"
        };

        for (int i = 0; i < columns.length; i++) {
            printRow(
                    labels[i],
                    getSingleValue("revenue_" + year1, columns[i]),
                    getSingleValue("revenue_" + year2, columns[i])
            );
        }
    }

    // ================== ΕΞΟΔΑ ==================
    private static void compareExpenses(int year1, int year2) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("ΑΝΑΛΥΤΙΚΗ ΣΥΓΚΡΙΣΗ ΕΞΟΔΩΝ");
        System.out.println("==============================================");

        String[] columns = {
                "employee_benefits",
                "social_benefits",
                "transfers",
                "purchases_of_goods_and_services",
                "interest",
                "other_expenditures"
        };

        String[] labels = {
                "Αμοιβές προσωπικού",
                "Κοινωνικά επιδόματα",
                "Μεταβιβάσεις",
                "Αγορές αγαθών & υπηρεσιών",
                "Τόκοι",
                "Λοιπά έξοδα"
        };

        for (int i = 0; i < columns.length; i++) {
            printRow(
                    labels[i],
                    getSingleValue("expenses_" + year1, columns[i]),
                    getSingleValue("expenses_" + year2, columns[i])
            );
        }
    }

    // ================== ΥΠΟΥΡΓΕΙΑ ==================
    private static void compareMinistries(int year1, int year2) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("ΑΝΑΛΥΤΙΚΗ ΣΥΓΚΡΙΣΗ ΥΠΟΥΡΓΕΙΩΝ");
        System.out.println("==============================================");

        String[] columns = {
                "presidency_of_the_republic",
                "hellenic_parliament",
                "presidency_of_the_government",
                "ministry_of_interior",
                "ministry_of_foreign_affairs",
                "ministry_of_national_defence",
                "ministry_of_health",
                "ministry_of_justice"
        };

        String[] labels = {
                "Προεδρία Δημοκρατίας",
                "Βουλή",
                "Προεδρία Κυβέρνησης",
                "Υπ. Εσωτερικών",
                "Υπ. Εξωτερικών",
                "Υπ. Εθνικής Άμυνας",
                "Υπ. Υγείας",
                "Υπ. Δικαιοσύνης"
        };

        for (int i = 0; i < columns.length; i++) {
            printRow(
                    labels[i],
                    getSingleValue("ministries_" + year1, columns[i]),
                    getSingleValue("ministries_" + year2, columns[i])
            );
        }
    }

    // ================== ΑΠΟΚΕΝΤΡΩΜΕΝΕΣ ==================
    private static void compareDecentralizedAdmins(int year1, int year2) {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("ΑΝΑΛΥΤΙΚΗ ΣΥΓΚΡΙΣΗ ΑΠΟΚΕΝΤΡΩΜΕΝΩΝ ΔΙΟΙΚΗΣΕΩΝ");
        System.out.println("==============================================");

        String[] columns = {
                "decentralized_administration_of_attica",
                "decentralized_administration_of_crete",
                "decentralized_administration_of_macedonia_thrace"
        };

        String[] labels = {
                "Αττικής",
                "Κρήτης",
                "Μακεδονίας & Θράκης"
        };

        for (int i = 0; i < columns.length; i++) {
            printRow(
                    labels[i],
                    getSingleValue("decentralized_administrations_" + year1, columns[i]),
                    getSingleValue("decentralized_administrations_" + year2, columns[i])
            );
        }
    }

    private static void printNarrativeSummary(
        int year1,
        int year2,
        long rev1,
        long rev2,
        long exp1,
        long exp2,
        long res1,
        long res2
) {
    double revPct = rev1 == 0 ? 0 : (rev2 - rev1) * 100.0 / rev1;
    double expPct = exp1 == 0 ? 0 : (exp2 - exp1) * 100.0 / exp1;

    System.out.println();
    System.out.println("==============================================");
    System.out.println("ΛΕΚΤΙΚΗ ΑΝΑΛΥΣΗ");
    System.out.println("==============================================");

    // Έσοδα
    if (rev2 > rev1) {
        System.out.printf(
                "Το %d παρουσίασε αύξηση εσόδων κατά %.2f%% σε σύγκριση με το %d.%n",
                year2, revPct, year1
        );
    } else if (rev2 < rev1) {
        System.out.printf(
                "Το %d παρουσίασε μείωση εσόδων κατά %.2f%% σε σύγκριση με το %d.%n",
                year2, Math.abs(revPct), year1
        );
    } else {
        System.out.printf(
                "Τα έσοδα παρέμειναν σταθερά μεταξύ %d και %d.%n",
                year1, year2
        );
    }

    // Έξοδα
    if (exp2 > exp1) {
        System.out.printf(
                "Παράλληλα, οι συνολικές δαπάνες αυξήθηκαν κατά %.2f%%.%n",
                expPct
        );
    } else if (exp2 < exp1) {
        System.out.printf(
                "Παράλληλα, οι συνολικές δαπάνες μειώθηκαν κατά %.2f%%.%n",
                Math.abs(expPct)
        );
    } else {
        System.out.println(
                "Οι συνολικές δαπάνες παρέμειναν αμετάβλητες."
        );
    }

    // Δημοσιονομικό αποτέλεσμα
    if (res2 > res1) {
        System.out.println(
                "Το δημοσιονομικό αποτέλεσμα παρουσίασε βελτίωση σε σχέση με το προηγούμενο έτος."
        );
    } else if (res2 < res1) {
        System.out.println(
                "Το δημοσιονομικό αποτέλεσμα επιδεινώθηκε σε σχέση με το προηγούμενο έτος."
        );
    } else {
        System.out.println(
                "Το δημοσιονομικό αποτέλεσμα παρέμεινε αμετάβλητο."
        );
    }

    // Τελικό συμπέρασμα
    if (expPct > revPct) {
        System.out.println(
                "Συνολικά, η αύξηση των δαπανών ήταν ταχύτερη από την αύξηση των εσόδων."
        );
    } else {
        System.out.println(
                "Συνολικά, η αύξηση των εσόδων υπερίσχυσε της αύξησης των δαπανών."
        );
    }
}

    // ================== ΒΟΗΘΗΤΙΚΑ ==================
    private static long getSingleValue(String table, String column) {
        try (Connection con = DriverManager.getConnection(DB);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT " + column + " FROM " + table)) {

            return rs.next() ? rs.getLong(column) : 0;

        } catch (Exception e) {
            return 0;
        }
    }

    private static void printRow(String label, long v1, long v2) {
        long diff = v2 - v1;
        String percent = (v1 == 0) ? "-" :
                String.format("%+.2f%%", diff * 100.0 / v1);

        System.out.printf(
                "%-35s | %12d | %12d | %+12d | %8s%n",
                label, v1, v2, diff, percent
        );
    }

    private static double percentValue(long v1, long v2) {
        if (v1 == 0) return 0;
        return (v2 - v1) * 100.0 / v1;
    }
}
