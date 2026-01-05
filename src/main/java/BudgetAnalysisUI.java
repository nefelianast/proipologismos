package ui;

import java.util.Scanner;
import service.BudgetAnalysisService;

public class BudgetAnalysisUI {

    public static void start() {
        Scanner scan = new Scanner(System.in);

        System.out.println("==============================================");
        System.out.println("        ΚΡΑΤΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ");
        System.out.println("==============================================");
        System.out.println("1. Ανάλυση έτους");
        System.out.println("2. Σύγκριση δύο ετών");
        System.out.print("Επιλογή: ");

        int choice = scan.nextInt();

        if (choice == 1) {
            int year = readYear(scan);
        
            long revenue = BudgetAnalysisService.getTotalRevenue(year);
            long expenses = BudgetAnalysisService.getTotalExpenses(year);
            long result = BudgetAnalysisService.getBudgetResult(year);
        
            System.out.println();
            System.out.println("==============================================");
            System.out.println("ΑΝΑΛΥΣΗ ΚΡΑΤΙΚΟΥ ΠΡΟΫΠΟΛΟΓΙΣΜΟΥ " + year);
            System.out.println("==============================================");
            System.out.printf("%-30s %15d%n", "Συνολικά Έσοδα:", revenue);
            System.out.printf("%-30s %15d%n", "Συνολικά Έξοδα:", expenses);
            System.out.printf("%-30s %15d%n", "Αποτέλεσμα Προϋπολογισμού:", result);
        
            if (year > 2023) {
                long prevRevenue = BudgetAnalysisService.getTotalRevenue(year - 1);
                long diff = revenue - prevRevenue;
                double percent = prevRevenue == 0 ? 0 : (diff * 100.0 / prevRevenue);
        
                System.out.println("----------------------------------------------");
                System.out.printf(
                        "%-30s %+15d (%+.2f%%)%n",
                        "Μεταβολή εσόδων από πέρυσι:",
                        diff,
                        percent
                );
            } else if (choice == 2) {

                int year1 = readYear(scan);
                int year2 = readDifferentYear(scan, year1);
            
                BudgetAnalysisService.compareYears(year1, year2);
            }
            else {
                System.out.println("Μη έγκυρη επιλογή");
            }
            
        }
        
    }

    // ----------------- ΒΟΗΘΗΤΙΚΕΣ ΜΕΘΟΔΟΙ -----------------

    private static int readYear(Scanner scan) {
        int year;
        do {
            System.out.print("Δώσε έτος (2023-2026): ");
            year = scan.nextInt();
        } while (year < 2023 || year > 2026);
        return year;
    }

    private static int readDifferentYear(Scanner scan, int firstYear) {
        int year;
        do {
            System.out.print("Δώσε διαφορετικό έτος (2023-2026): ");
            year = scan.nextInt();
        } while (year < 2023 || year > 2026 || year == firstYear);
        return year;
    }
}
