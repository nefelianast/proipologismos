package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainGUI extends Application {

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

    public static void main(String[] args) {
        launch(args);
    }
}
