package ui;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

// controller για την splash screen
// εμφανίζει animated logo και τίτλο, μετά μεταβαίνει αυτόματα στην οθόνη σύνδεσης
public class Splash {
    
    @FXML
    private StackPane splashPane;
    
    @FXML
    private ImageView logoImageView;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private void initialize() {
        startAnimations();
    }
    
    private void startAnimations() {
        logoImageView.setOpacity(0);
        titleLabel.setOpacity(0);
        
        FadeTransition logoFadeIn = new FadeTransition(Duration.millis(1200), logoImageView);
        logoFadeIn.setFromValue(0);
        logoFadeIn.setToValue(1);
        logoFadeIn.setInterpolator(Interpolator.EASE_OUT);
        
        ScaleTransition logoScale = new ScaleTransition(Duration.millis(1000), logoImageView);
        logoScale.setFromX(0.4);
        logoScale.setFromY(0.4);
        logoScale.setToX(1.0);
        logoScale.setToY(1.0);
        logoScale.setInterpolator(Interpolator.EASE_OUT);
        
        FadeTransition titleFadeIn = new FadeTransition(Duration.millis(800), titleLabel);
        titleFadeIn.setFromValue(0);
        titleFadeIn.setToValue(1);
        titleFadeIn.setDelay(Duration.millis(2200)); // Start after logo stabilizes
        titleFadeIn.setInterpolator(Interpolator.EASE_OUT);
        
        ScaleTransition titleScale = new ScaleTransition(Duration.millis(600), titleLabel);
        titleScale.setFromX(0.5);
        titleScale.setFromY(0.5);
        titleScale.setToX(1.0);
        titleScale.setToY(1.0);
        titleScale.setDelay(Duration.millis(2200)); 
        titleScale.setInterpolator(Interpolator.EASE_OUT);
        
        SequentialTransition logoAnimation = new SequentialTransition(logoFadeIn, logoScale);
        
        SequentialTransition titleAnimation = new SequentialTransition(titleFadeIn, titleScale);
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(600), splashPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.millis(3500)); 
        fadeOut.setInterpolator(Interpolator.EASE_IN);
        
        fadeOut.setOnFinished(e -> {
            try {
                goToLogin();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        logoAnimation.play();
        titleAnimation.play();
        fadeOut.play();
    }
    
    private void goToLogin() throws Exception {
        Stage stage = (Stage) splashPane.getScene().getWindow();
        
        Parent root = FXMLLoader.load(getClass().getResource("/ui/LoginView.fxml"));
        
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(
                getClass().getResource("/ui/styles.css").toExternalForm());
        
        stage.setTitle("Κρατικός Προϋπολογισμός - Σύστημα Ανάλυσης");
        stage.setScene(scene);
    }
}
