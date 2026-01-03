package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class BudgetAnalysisService {

    private static final String DB =
            "jdbc:sqlite:src/main/resources/database/BudgetData.db";

    // ================== ΣΥΓΚΡΙΣΗ ΔΥΟ ΕΤΩΝ ==================
    public static void compareYears(int year1, int year2) {

        long rev1 = getValue("budget_summary_" + year1, "total_revenue");
        long exp1 = getValue("budget_summary_" + year1, "total_expenses");

        long rev2 = getValue("budget_summary_" + year2, "total_revenue");
        long exp2 = getValue("budget_summary_" + year2, "total_expenses");

        System.out.println();
        System.out.println("==============================================");
        System.out.println("ΣΥΓΚΡΙΣΗ ΚΡΑΤΙΚΟΥ ΠΡΟΫΠΟΛΟΓΙΣΜΟΥ");
        System.out.println("Έτη: " + year1 + " vs " + year2);
        System.out.println("==============================================");

        printRow("Συνολικά Έσοδα", rev1, rev2);
        printRow("Συνολικά Έξοδα", exp1, exp2);
        printRow("Αποτέλεσμα", rev1 - exp1, rev2 - exp2);
    }

    // ================== ΒΟΗΘΗΤΙΚΕΣ ==================

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

    private static void printRow(String label, long v1, long v2) {
        long diff = v2 - v1;
        String percent = v1 == 0 ? "-" :
                String.format("%.2f%%", (double) diff / v1 * 100);

        System.out.printf(
                "%-25s | %15d | %15d | %+15d | %8s%n",
                label, v1, v2, diff, percent
        );
    }
}


