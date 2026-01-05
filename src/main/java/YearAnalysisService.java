package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class YearAnalysisService {

    private static final String DB =
            "jdbc:sqlite:src/main/resources/database/BudgetData.db";

    // ================== ΑΝΑΛΥΣΗ ΕΤΟΥΣ ==================
    public static void analyzeYear(int year) {

        long revenue = getValue("budget_summary_" + year, "total_revenue");
        long expenses = getValue("budget_summary_" + year, "total_expenses");
        long result = getValue("budget_summary_" + year, "budget_result");

        System.out.println();
        System.out.println("==============================================");
        System.out.println("ΑΝΑΛΥΣΗ ΚΡΑΤΙΚΟΥ ΠΡΟΫΠΟΛΟΓΙΣΜΟΥ " + year);
        System.out.println("==============================================");

        System.out.printf("Συνολικά Έσοδα:        %,d%n", revenue);
        System.out.printf("Συνολικά Έξοδα:        %,d%n", expenses);
        System.out.printf("Αποτέλεσμα:            %,d%n", result);

        analyzeRevenues(year);
        analyzeExpenses(year);
        analyzeMinistries(year);
        analyzeDecentralizedAdmins(year);

        printNarrative(year, revenue, expenses, result);
    }

    // ================== ΕΣΟΔΑ ==================
    private static void analyzeRevenues(int year) {

        System.out.println();
        System.out.println("ΑΝΑΛΥΤΙΚΗ ΑΝΑΛΥΣΗ ΕΣΟΔΩΝ");

        String[] cols = {
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

        for (int i = 0; i < cols.length; i++) {
            printRow(labels[i],
                    getValue("revenue_" + year, cols[i]));
        }
    }

    // ================== ΕΞΟΔΑ ==================
    private static void analyzeExpenses(int year) {

        System.out.println();
        System.out.println("ΑΝΑΛΥΤΙΚΗ ΑΝΑΛΥΣΗ ΕΞΟΔΩΝ");

        String[] cols = {
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

        for (int i = 0; i < cols.length; i++) {
            printRow(labels[i],
                    getValue("expenses_" + year, cols[i]));
        }
    }

    // ================== ΥΠΟΥΡΓΕΙΑ ==================
    private static void analyzeMinistries(int year) {

        System.out.println();
        System.out.println("ΑΝΑΛΥΣΗ ΥΠΟΥΡΓΕΙΩΝ");

        String[] cols = {
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

        for (int i = 0; i < cols.length; i++) {
            printRow(labels[i],
                    getValue("ministries_" + year, cols[i]));
        }
    }

    // ================== ΑΠΟΚΕΝΤΡΩΜΕΝΕΣ ==================
    private static void analyzeDecentralizedAdmins(int year) {

        System.out.println();
        System.out.println("ΑΝΑΛΥΣΗ ΑΠΟΚΕΝΤΡΩΜΕΝΩΝ ΔΙΟΙΚΗΣΕΩΝ");

        String[] cols = {
                "decentralized_administration_of_attica",
                "decentralized_administration_of_crete",
                "decentralized_administration_of_macedonia_thrace"
        };

        String[] labels = {
                "Αττικής",
                "Κρήτης",
                "Μακεδονίας & Θράκης"
        };

        for (int i = 0; i < cols.length; i++) {
            printRow(labels[i],
                    getValue("decentralized_administrations_" + year, cols[i]));
        }
    }

    // ================== ΛΕΚΤΙΚΗ ΑΝΑΛΥΣΗ ==================
    private static void printNarrative(
            int year,
            long revenue,
            long expenses,
            long result
    ) {
        System.out.println();
        System.out.println("ΛΕΚΤΙΚΗ ΑΝΑΛΥΣΗ");

        System.out.printf(
                "Το %d παρουσίασε συνολικά έσοδα ύψους %,d ευρώ.%n",
                year, revenue
        );

        System.out.printf(
                "Οι συνολικές δαπάνες ανήλθαν σε %,d ευρώ.%n",
                expenses
        );

        if (result < 0) {
            System.out.printf(
                    "Το δημοσιονομικό αποτέλεσμα ήταν ελλειμματικό, με έλλειμμα %,d ευρώ.%n",
                    Math.abs(result)
            );
        } else {
            System.out.printf(
                    "Το δημοσιονομικό αποτέλεσμα ήταν πλεονασματικό, ύψους %,d ευρώ.%n",
                    result
            );
        }
    }

    // ================== ΒΟΗΘΗΤΙΚΑ ==================
    private static long getValue(String table, String column) {
        try (Connection con = DriverManager.getConnection(DB);
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT " + column + " FROM " + table)) {

            return rs.next() ? rs.getLong(column) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private static void printRow(String label, long value) {
        System.out.printf("%-35s : %,d%n", label, value);
    }
}
