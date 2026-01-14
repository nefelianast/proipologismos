package ui;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

// κύρια JavaFX Application κλάση για το Σύστημα Ανάλυσης Προϋπολογισμού
// αρχικοποιεί και εμφανίζει την splash screen, η οποία μετά μεταβαίνει στην οθόνη σύνδεσης
public class MainGUI extends Application {
    public static boolean downloading = false;

    // ξεκινά την JavaFX εφαρμογή φορτώνοντας την splash screen
    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/ui/SplashScreen.fxml"));

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/ui/styles.css").toExternalForm());

        stage.setTitle("Κρατικός Προϋπολογισμός - Σύστημα Ανάλυσης");
        stage.setScene(scene);
        stage.show();
    }

    // κύριο entry point της εφαρμογής
    // αρχικοποιεί αυτόματα τη βάση δεδομένων αν χρειάζεται πριν το launch του GUI
    public static void main(String[] args) throws IOException {
        /* 
        // Αυτόματη αρχικοποίηση βάσης δεδομένων
        try {
            Class<?> sqlMakerClass = Class.forName("SQLmaker");
            Object sqlMaker = sqlMakerClass.getDeclaredConstructor().newInstance();
            sqlMakerClass.getMethod("make").invoke(sqlMaker);
        } catch (Exception e) {
            System.err.println("Warning: Could not initialize database: " + e.getMessage());
            System.err.println("Application will continue with sample data.");
        }
*/
        // Έλεγχος αν υπάρχει έστω ένα pdf
        File pdfFile = new File("proipologismos2024.pdf");
        if (!pdfFile.exists()) {
            downloading = true;

           
            javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "Παρακαλώ πατήστε οκ για να κατεβούν οι προϋπολογισμοί.\n ΑΝΑΜΟΝΗ:15 δευτερόλεπτα",
                    "Loading",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
            );

           
            DataDownload download = new DataDownload();
            download.Download();
            try {
                Class<?> sqlMakerClass = Class.forName("SQLmaker");
                Object sqlMaker = sqlMakerClass.getDeclaredConstructor().newInstance();
                sqlMakerClass.getMethod("make").invoke(sqlMaker);
            } catch (Exception e) {
                System.err.println("Warning: Could not initialize database: " + e.getMessage());
            }

            javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "✅ ΕΠΙΤΥΧΙΑ\nΠατήστε οκ για έναρξη της εφαρμογής.",
                    "SUCCESS",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        }

        // Εκκίνηση της JavaFX εφαρμογής
        launch(args);
    }
}
