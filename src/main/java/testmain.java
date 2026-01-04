import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Test class for demonstrating and testing various budget data operations.
 * Provides a menu-driven CLI interface for testing backend operations:
 * - DataDownload: Download budget PDFs from Ministry of Finance
 * - SQLmaker: Create database tables and populate with data
 * - Comparisons: Compare budget data between different years
 * 
 * This class is useful for:
 * - Initial project setup (downloading PDFs, creating database)
 * - Testing backend operations without GUI
 * - Demonstrating data processing workflow
 */
public class testmain {
    
    /**
     * Main method that provides an interactive menu for testing different budget operations.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        
        try {
            printWelcomeMessage();
            
            boolean running = true;
            while (running) {
                printMenu();
                
                try {
                    int choice = scan.nextInt();
                    scan.nextLine(); // Consume newline
                    
                    switch (choice) {
                        case 1:
                            handleDownload(scan);
                            break;
                        case 2:
                            handleSQLSetup();
                            break;
                        case 3:
                            handleComparisons(scan);
                            break;
                        case 4:
                            running = false;
                            System.out.println("\nΈξοδος από το πρόγραμμα. Αντίο!");
                            break;
                        default:
                            System.out.println("\n⚠️  Μη έγκυρη επιλογή. Παρακαλώ επιλέξτε 1-4.\n");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("\n⚠️  Παρακαλώ εισάγετε έναν αριθμό.\n");
                    scan.nextLine(); // Clear invalid input
                }
            }
        } catch (Exception e) {
            System.err.println("Σφάλμα: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scan.close();
        }
    }
    
    /**
     * Prints welcome message
     */
    private static void printWelcomeMessage() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  Budget Analysis System - Test/Demo Interface");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("Αυτή η κλάση επιτρέπει τη δοκιμή backend operations:");
        System.out.println("  • DataDownload: Κατέβασμα PDFs από το minfin.gov.gr");
        System.out.println("  • SQLmaker: Δημιουργία database tables και εισαγωγή δεδομένων");
        System.out.println("  • Comparisons: Σύγκριση budget data μεταξύ ετών");
        System.out.println("═══════════════════════════════════════════════════════════\n");
    }
    
    /**
     * Prints the main menu
     */
    private static void printMenu() {
        System.out.println("Επιλέξτε μια λειτουργία:");
        System.out.println("  1. Κατέβασμα Budget PDFs (DataDownload)");
        System.out.println("  2. Δημιουργία Database Tables (SQLmaker)");
        System.out.println("  3. Σύγκριση Budget Data (Comparisons)");
        System.out.println("  4. Έξοδος");
        System.out.print("\nΕπιλογή: ");
    }
    
    /**
     * Handles the download operation
     * 
     * @param scan Scanner for user input
     */
    private static void handleDownload(Scanner scan) {
        System.out.println("\n📥 Κατέβασμα Budget PDFs...");
        System.out.println("Σημείωση: Αυτή η λειτουργία θα κατεβάσει PDFs για όλα τα έτη (2023-2026)");
        System.out.print("Συνέχεια; (y/n): ");
        
        String confirm = scan.nextLine().trim().toLowerCase();
        if (!confirm.equals("y") && !confirm.equals("yes") && !confirm.equals("ναι")) {
            System.out.println("Ακυρώθηκε.\n");
            return;
        }
        
        try {
            DataDownload downloader = new DataDownload();
            downloader.Download();
            System.out.println("\n✅ Κατέβασμα ολοκληρώθηκε!\n");
        } catch (IOException e) {
            System.err.println("\n❌ Σφάλμα κατά το κατέβασμα: " + e.getMessage());
            System.out.println();
        }
    }
    
    /**
     * Handles the SQL setup operation
     */
    private static void handleSQLSetup() {
        System.out.println("\n🗄️  Δημιουργία Database Tables...");
        System.out.println("Σημείωση: Αυτή η λειτουργία θα:");
        System.out.println("  • Δημιουργήσει όλα τα απαραίτητα tables");
        System.out.println("  • Εισάγει δεδομένα από CSV files (αν υπάρχουν)");
        
        try {
            SQLmaker sql = new SQLmaker();
            sql.make();
            System.out.println("\n✅ Database setup ολοκληρώθηκε!\n");
        } catch (Exception e) {
            System.err.println("\n❌ Σφάλμα κατά τη δημιουργία tables: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }
    
    /**
     * Handles the comparisons operation
     * 
     * @param scan Scanner for user input
     */
    private static void handleComparisons(Scanner scan) {
        System.out.println("\n📊 Σύγκριση Budget Data...");
        
        int year1 = getYearInput(scan, "Πρώτο έτος (2023-2026): ");
        if (year1 == -1) {
            System.out.println("Ακυρώθηκε.\n");
            return;
        }
        
        int year2 = getYearInput(scan, "Δεύτερο έτος (2023-2026): ");
        if (year2 == -1) {
            System.out.println("Ακυρώθηκε.\n");
            return;
        }
        
        if (year1 == year2) {
            System.out.println("⚠️  Τα έτη πρέπει να είναι διαφορετικά.\n");
            return;
        }
        
        try {
            Comparisons com = new Comparisons();
            com.comparisons_of_two_years(year1, year2);
            System.out.println("\n✅ Σύγκριση ολοκληρώθηκε!\n");
        } catch (Exception e) {
            System.err.println("\n❌ Σφάλμα κατά τη σύγκριση: " + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }
    
    /**
     * Gets a valid year input from the user
     * 
     * @param scan Scanner for user input
     * @param prompt The prompt message
     * @return The year (2023-2026) or -1 if cancelled
     */
    private static int getYearInput(Scanner scan, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int year = scan.nextInt();
                scan.nextLine(); // Consume newline
                
                if (year >= 2023 && year <= 2026) {
                    return year;
                } else {
                    System.out.println("⚠️  Παρακαλώ εισάγετε έτος μεταξύ 2023-2026.");
                }
            } catch (InputMismatchException e) {
                System.out.println("⚠️  Παρακαλώ εισάγετε έναν έγκυρο αριθμό.");
                scan.nextLine(); // Clear invalid input
            }
        }
    }
}
