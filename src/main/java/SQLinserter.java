
package main.java;

import org.apache.commons.csv.*;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SQLinserter {

    // ------------------ 2025 ------------------ //

    private static void insertRevenue2025(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO revenue_2025 VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 14);
    }

    private static void insertExpenses2025(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO expenses_2025 VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 15);
    }

    private static void insertMinistries2025(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO ministries_2025 VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 25);
    }

    private static void insertDecentralizedAdministrations2025(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO decentralized_administrations_2025 VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 9);
    }

    private static void insertBudgetSummary2025(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO budget_summary_2025 VALUES
                (?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 5);
    }

    // ------------------ 2024 ------------------ //

    private static void insertRevenue2024(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO revenue_2024 VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 14);
    }

    private static void insertExpenses2024(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO expenses_2024 VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 15);
    }

    private static void insertMinistries2024(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO ministries_2024 VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 25);
    }

    private static void insertDecentralizedAdministrations2024(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO decentralized_administrations_2024 VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 9);
    }

    private static void insertBudgetSummary2024(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO budget_summary_2024 VALUES
                (?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 5);
    }

    // ------------------ 2023 ------------------ //

    private static void insertRevenue2023(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO revenue_2023 VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 14);
    }

    private static void insertExpenses2023(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO expenses_2023 VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 15);
    }

    private static void insertMinistries2023(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO ministries_2023 VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 25);
    }

    private static void insertDecentralizedAdministrations2023(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO decentralized_administrations_2023 VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 9);
    }

    private static void insertBudgetSummary2023(Connection conn, String csvPath) throws Exception {
        String sql = """
                INSERT INTO budget_summary_2023 VALUES
                (?, ?, ?, ?, ?)
                """;
        importCsv(conn, csvPath, sql, 5);
    }

    // ------------------ CSV Import Helper ------------------ //

    private static void importCsv(Connection conn, String csvPath, String sql, int columnCount) throws Exception {
        try (Reader in = new FileReader(csvPath);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
            for (CSVRecord record : records) {
                for (int i = 0; i < columnCount; i++) {
                    pstmt.setString(i + 1, record.get(i));
                }
                pstmt.executeUpdate();
            }
        }
    }
}
