package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX Application class for the Budget Analysis System.
 * Initializes and displays the splash screen, which then transitions to the login screen.
 */
public class MainGUI extends Application {

    /**
     * Starts the JavaFX application by loading the splash screen.
     * 
     * @param stage The primary stage for the application
     * @throws Exception if there is an error loading the FXML or CSS files
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Load splash screen first
        Parent root = FXMLLoader.load(getClass().getResource("/ui/SplashScreen.fxml"));

        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(
                getClass().getResource("/ui/styles.css").toExternalForm());

        stage.setTitle("Κρατικός Προϋπολογισμός - Σύστημα Ανάλυσης");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main entry point for the application.
     * Automatically initializes the database if needed before launching the GUI.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Initialize database automatically on first run
        try {
            // Use reflection since SQLmaker is in default package
            Class<?> sqlMakerClass = Class.forName("SQLmaker");
            Object sqlMaker = sqlMakerClass.getDeclaredConstructor().newInstance();
            sqlMakerClass.getMethod("make").invoke(sqlMaker);
        } catch (Exception e) {
            System.err.println("Warning: Could not initialize database: " + e.getMessage());
            System.err.println("Application will continue with sample data.");
        }
        
        // Launch the JavaFX application
        launch(args);
    }
}
