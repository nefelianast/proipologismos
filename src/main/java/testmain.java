import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class testmain {
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
                            running = false;
                            System.out.println("\nΈξοδος από το πρόγραμμα. Αντίο!");
                            break;
                        default:
                            System.out.println("\n⚠️  Μη έγκυρη επιλογή. Παρακαλώ επιλέξτε 1-3.\n");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("\n⚠️  Παρακαλώ εισάγετε έναν αριθμό.\n");
                    scan.nextLine(); 
                }
            }
        } catch (Exception e) {
            System.err.println("Σφάλμα: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scan.close();
        }
    }
    
    private static void printWelcomeMessage() {
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("  Budget Analysis System - Test/Demo Interface");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("Αυτή η κλάση επιτρέπει τη δοκιμή backend operations:");
        System.out.println("  • DataDownload: Κατέβασμα PDFs από το minfin.gov.gr");
        System.out.println("  • SQLmaker: Δημιουργία database tables και εισαγωγή δεδομένων");
        System.out.println("═══════════════════════════════════════════════════════════\n");
    }
    
    private static void printMenu() {
        System.out.println("Επιλέξτε μια λειτουργία:");
        System.out.println("  1. Κατέβασμα Budget PDFs (DataDownload)");
        System.out.println("  2. Δημιουργία Database Tables (SQLmaker)");
        System.out.println("  3. Έξοδος");
        System.out.print("\nΕπιλογή: ");
    }
    
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
    
}
