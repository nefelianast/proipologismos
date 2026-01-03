import java.util.Scanner;

public class LoginApp {

    public static void authenticate() {

        Scanner scan = new Scanner(System.in);
        UserRepository repo = new UserRepository();

        while (true) {

            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.print("Επιλογή: ");

            int choice = scan.nextInt();
            scan.nextLine(); // καθαρισμός buffer

            if (choice == 1) {
                // -------- LOGIN --------
                System.out.println("=== LOGIN ===");

                System.out.print("Username: ");
                String username = scan.nextLine();

                System.out.print("Password: ");
                String password = scan.nextLine();

                if (repo.checkLogin(username, password)) {
                    System.out.println("Επιτυχής σύνδεση");
                    break; // ΒΓΑΙΝΟΥΜΕ από το loop → προχωράει η main
                } else {
                    System.out.println("Λάθος username ή κωδικός");
                    System.out.println("Προσπάθησε ξανά\n");
                }

            } else if (choice == 2) {
                // -------- SIGN UP --------
                System.out.println("=== SIGN UP ===");

                System.out.print("Νέο username: ");
                String username = scan.nextLine();

                if (repo.usernameExists(username)) {
                    System.out.println("Το username υπάρχει ήδη\n");
                    continue;
                }

                System.out.print("Νέος κωδικός: ");
                String password = scan.nextLine();

                if (repo.saveUser(username, password)) {
                    System.out.println("Ο χρήστης δημιουργήθηκε");
                    System.out.println("Κάνε login για να συνεχίσεις\n");
                }

            } else {
                System.out.println("Μη έγκυρη επιλογή\n");
            }
        }
    }
}
