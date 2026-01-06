package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// κύρια JavaFX Application κλάση για το Σύστημα Ανάλυσης Προϋπολογισμού
// αρχικοποιεί και εμφανίζει την splash screen, η οποία μετά μεταβαίνει στην οθόνη σύνδεσης
public class MainGUI extends Application {

    // ξεκινά την JavaFX εφαρμογή φορτώνοντας την splash screen
    @Override
    public void start(Stage stage) throws Exception {
        // φόρτωση splash screen
        Parent root = FXMLLoader.load(getClass().getResource("/ui/SplashScreen.fxml"));

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(
                getClass().getResource("/ui/styles.css").toExternalForm());

        stage.setTitle("Κρατικός Προϋπολογισμός - Σύστημα Ανάλυσης");
        stage.setScene(scene);
        stage.show();
    }

    // κύριο entry point της εφαρμογής
    // αρχικοποιεί αυτόματα τη βάση δεδομένων αν χρειάζεται πριν το launch του GUI
    public static void main(String[] args) {
        // αυτόματη αρχικοποίηση βάσης δεδομένων στην πρώτη εκτέλεση
        try {
            // χρήση reflection αφού το SQLmaker είναι στο default package
            Class<?> sqlMakerClass = Class.forName("SQLmaker");
            Object sqlMaker = sqlMakerClass.getDeclaredConstructor().newInstance();
            sqlMakerClass.getMethod("make").invoke(sqlMaker);
        } catch (Exception e) {
            System.err.println("Warning: Could not initialize database: " + e.getMessage());
            System.err.println("Application will continue with sample data.");
        }
        
        // εκκίνηση της JavaFX εφαρμογής
        launch(args);
    }
}
