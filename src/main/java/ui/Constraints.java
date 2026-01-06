package ui;

import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import java.util.regex.Pattern;

// κλάση για ελέγχους περιορισμών
// ελέγχει αν οι αλλαγές στα ποσά είναι εντός λογικών ορίων και αν οι τιμές είναι έγκυρες
public class Constraints {
    
    // σταθερές για validation rules 
    private static final int MIN_USERNAME_LENGTH = 3;        // ελάχιστος αριθμός χαρακτήρων για username
    private static final int MAX_USERNAME_LENGTH = 50;       // μέγιστος αριθμός χαρακτήρων για username
    private static final int MIN_PASSWORD_LENGTH = 6;        // ελάχιστος αριθμός χαρακτήρων για password
    private static final int MAX_PASSWORD_LENGTH = 100;      // μέγιστος αριθμός χαρακτήρων για password
    private static final int MIN_CATEGORY_LENGTH = 2;        // ελάχιστος αριθμός χαρακτήρων για κατηγορία
    private static final int MAX_CATEGORY_LENGTH = 100;      // μέγιστος αριθμός χαρακτήρων για κατηγορία
    private static final double MIN_AMOUNT = 0.0;            // ελάχιστο επιτρεπόμενο ποσό
    
    private static final Pattern POSITIVE_NUMERIC_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");
    private static final Pattern PERCENTAGE_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");
    
    //ελέγχει το username 
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "Το όνομα χρήστη είναι υποχρεωτικό.");
        }
        
        String trimmed = username.trim();
        
        if (trimmed.length() < MIN_USERNAME_LENGTH) {
            return new ValidationResult(false, 
                String.format("Το όνομα χρήστη πρέπει να έχει τουλάχιστον %d χαρακτήρες.", MIN_USERNAME_LENGTH));
        }
        
        if (trimmed.length() > MAX_USERNAME_LENGTH) {
            return new ValidationResult(false, 
                String.format("Το όνομα χρήστη δεν μπορεί να υπερβαίνει τους %d χαρακτήρες.", MAX_USERNAME_LENGTH));
        }
        
        // έλεγχος για μη έγκυρους χαρακτήρες (επιτρέπονται μόνο γράμματα, αριθμοί, κάτω παύλα, παύλα)
        if (!trimmed.matches("^[a-zA-Z0-9_\\-α-ωΑ-ΩάέήίόύώΆΈΉΊΌΎΏ]+$")) {
            return new ValidationResult(false, 
                "Το όνομα χρήστη μπορεί να περιέχει μόνο γράμματα, αριθμούς, παύλα και κάτω παύλα.");
        }
        
        return new ValidationResult(true, "");
    }
    
    //ελέγχει τον κωδικό πρόσβασης
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "Ο κωδικός πρόσβασης είναι υποχρεωτικός.");
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return new ValidationResult(false, 
                String.format("Ο κωδικός πρόσβασης πρέπει να έχει τουλάχιστον %d χαρακτήρες.", MIN_PASSWORD_LENGTH));
        }
        
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return new ValidationResult(false, 
                String.format("Ο κωδικός πρόσβασης δεν μπορεί να υπερβαίνει τους %d χαρακτήρες.", MAX_PASSWORD_LENGTH));
        }
        
        return new ValidationResult(true, "");
    }
    
    //ελέγχει το όνομα κατηγορίας	
    public static ValidationResult validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return new ValidationResult(false, "Η κατηγορία είναι υποχρεωτική.");
        }
        
        String trimmed = category.trim();
        
        if (trimmed.length() < MIN_CATEGORY_LENGTH) {
            return new ValidationResult(false, 
                String.format("Η κατηγορία πρέπει να έχει τουλάχιστον %d χαρακτήρες.", MIN_CATEGORY_LENGTH));
        }
        
        if (trimmed.length() > MAX_CATEGORY_LENGTH) {
            return new ValidationResult(false, 
                String.format("Η κατηγορία δεν μπορεί να υπερβαίνει τους %d χαρακτήρες.", MAX_CATEGORY_LENGTH));
        }
        
        return new ValidationResult(true, "");
    }
    
    //ελέγχει το ποσό (>0)
    public static ValidationResult validateAmount(String amountText) {
        if (amountText == null || amountText.trim().isEmpty()) {
            return new ValidationResult(false, "Το ποσό είναι υποχρεωτικό.");
        }
        
        String trimmed = amountText.trim();
        
        // έλεγχος αν είναι έγκυρη μορφή αριθμού
        if (!POSITIVE_NUMERIC_PATTERN.matcher(trimmed).matches()) {
            return new ValidationResult(false, 
                "Το ποσό πρέπει να είναι ένας θετικός αριθμός.");
        }
        
        try {
            double amount = Double.parseDouble(trimmed);
            
            if (amount < MIN_AMOUNT) {
                return new ValidationResult(false, 
                    "Το ποσό δεν μπορεί να είναι αρνητικό.");
            }
            
            return new ValidationResult(true, "");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Το ποσό δεν είναι έγκυρος αριθμός.");
        }
    }
    
    //ελέγχει την ποσοστιαία τιμή 
    public static ValidationResult validatePercentage(String percentageText) {
        if (percentageText == null || percentageText.trim().isEmpty()) {
            return new ValidationResult(false, "Η ποσοστιαία τιμή είναι υποχρεωτική.");
        }
        
        String trimmed = percentageText.trim();
        
        if (!PERCENTAGE_PATTERN.matcher(trimmed).matches()) {
            return new ValidationResult(false, 
                "Η ποσοστιαία τιμή πρέπει να είναι ένας αριθμός (π.χ. 10.5 ή -5.2).");
        }
        
        try {
            Double.parseDouble(trimmed);
            return new ValidationResult(true, "");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Η ποσοστιαία τιμή δεν είναι έγκυρος αριθμός.");
        }
    }
    
    // ελέγχει το έτος
    public static ValidationResult validateYear(String yearText, int minYear, int maxYear) {
        if (yearText == null || yearText.trim().isEmpty()) {
            return new ValidationResult(false, "Το έτος είναι υποχρεωτικό.");
        }
        
        try {
            int year = Integer.parseInt(yearText.trim());
            
            if (year < minYear || year > maxYear) {
                return new ValidationResult(false, 
                    String.format("Το έτος πρέπει να είναι μεταξύ %d και %d.", minYear, maxYear));
            }
            
            return new ValidationResult(true, "");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "Το έτος πρέπει να είναι ένας έγκυρος αριθμός.");
        }
    }
    
    //styling ανάλογα με τον έλεγχο 
    public static void applyValidationStyle(TextField textField, boolean isValid, Label errorLabel, String errorMessage) {
        if (textField == null) return;
        
        if (isValid) {
            // έγκυρο - αφαίρεση error styling
            textField.getStyleClass().removeAll("validation-error");
            String currentStyle = textField.getStyle();
            if (currentStyle != null && currentStyle.contains("-fx-border-color: #dc2626")) {   
                textField.setStyle(currentStyle.replace("-fx-border-color: #dc2626", "-fx-border-color: #d1d5db"));
            }
        } else {
            // μη έγκυρο - προσθήκη error styling
            if (!textField.getStyleClass().contains("validation-error")) {
                textField.getStyleClass().add("validation-error");
            }
            String currentStyle = textField.getStyle();
            if (currentStyle != null && currentStyle.contains("-fx-border-color:")) {
                textField.setStyle(currentStyle.replaceAll("-fx-border-color:[^;]+", "-fx-border-color: #dc2626"));
            } else {
                textField.setStyle((currentStyle != null ? currentStyle + " " : "") + "-fx-border-color: #dc2626;");
            }
        }
        
        // ενημέρωση error label 
        if (errorLabel != null) {
            if (isValid) {
                errorLabel.setText("");
                errorLabel.setVisible(false);
            } else {
                errorLabel.setText(errorMessage);
                errorLabel.setVisible(true);
                errorLabel.setTextFill(Color.web("#dc2626"));
            }
        }
    }
    
    //ελέγχει ότι ένα ComboBox έχει επιλεγμένη τιμή
    public static ValidationResult validateComboBoxSelection(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return new ValidationResult(false, 
                String.format("Παρακαλώ επιλέξτε %s.", fieldName));
        }
        return new ValidationResult(true, "");
    }
    
    //ελέγχει ότι μια αλλαγή ποσού είναι εντός λογικών ορίων (μέγιστο όριο 50%)
    public static ValidationResult validateAmountChange(double newAmount, double previousAmount) {
        // αν δεν υπάρχει προηγούμενο ποσό, δεχόμαστε την αλλαγή
        if (previousAmount == 0) {
            return new ValidationResult(true, "");
        }
        
        // μέγιστο επιτρεπόμενο όριο αλλαγής
        double maxChangePercent = 50.0;
        
        // υπολογισμός απόλυτης τιμής της ποσοστιαίας μεταβολής
        double changePercent = Math.abs(StatisticalAnalysis.calculatePercentageChange(newAmount, previousAmount));
        
        // έλεγχος αν η αλλαγή υπερβαίνει το μέγιστο όριο
        if (changePercent > maxChangePercent) {
            return new ValidationResult(false, 
                String.format("Η αλλαγή (%.1f%%) υπερβαίνει το μέγιστο επιτρεπόμενο όριο (%.1f%%). Παρακαλώ επιβεβαιώστε ότι η αλλαγή είναι σωστή.", 
                    changePercent, maxChangePercent));
        }
        
        // η αλλαγή είναι εντός ορίων
        return new ValidationResult(true, "");
    }
    
    //εσωτερική κλάση για αποθήκευση αποτελεσμάτων επικύρωσης
    public static class ValidationResult {
        private final boolean isValid;        // true αν η επικύρωση πέρασε, false αν απέτυχε
        private final String errorMessage;     // μήνυμα σφάλματος 
        
        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage != null ? errorMessage : "";
        }
        
        //επιστρέφει true αν η επικύρωση πέρασε, false αν απέτυχε
        public boolean isValid() {
            return isValid;
        }
        
        //επιστρέφει το μήνυμα σφάλματος 
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
