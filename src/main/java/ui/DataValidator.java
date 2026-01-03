package ui;

import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import java.util.regex.Pattern;

public class DataValidator {
    
    // Constants for validation rules
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 100;
    private static final int MIN_CATEGORY_LENGTH = 2;
    private static final int MAX_CATEGORY_LENGTH = 100;
    private static final double MIN_AMOUNT = 0.0;
    
    // Pattern for positive numeric input
    private static final Pattern POSITIVE_NUMERIC_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");
    // Pattern for percentage input
    private static final Pattern PERCENTAGE_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");
    
    /**
     * Validates username input
     * @param username The username to validate
     * @return ValidationResult with isValid flag and error message
     */
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
        
        // Check for invalid characters (only letters, numbers, underscore, hyphen allowed)
        if (!trimmed.matches("^[a-zA-Z0-9_\\-α-ωΑ-ΩάέήίόύώΆΈΉΊΌΎΏ]+$")) {
            return new ValidationResult(false, 
                "Το όνομα χρήστη μπορεί να περιέχει μόνο γράμματα, αριθμούς, παύλα και κάτω παύλα.");
        }
        
        return new ValidationResult(true, "");
    }
    
    /**
     * Validates password input
     * @param password The password to validate
     * @return ValidationResult with isValid flag and error message
     */
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
    
    /**
     * Validates category name input
     * @param category The category name to validate
     * @return ValidationResult with isValid flag and error message
     */
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
    
    /**
     * Validates amount input (must be positive number)
     * @param amountText The amount as text to validate
     * @return ValidationResult with isValid flag and error message
     */
    public static ValidationResult validateAmount(String amountText) {
        if (amountText == null || amountText.trim().isEmpty()) {
            return new ValidationResult(false, "Το ποσό είναι υποχρεωτικό.");
        }
        
        String trimmed = amountText.trim();
        
        // Check if it's a valid number format
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
    
    /**
     * Validates percentage input (can be negative or positive)
     * @param percentageText The percentage as text to validate
     * @return ValidationResult with isValid flag and error message
     */
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
    
    /**
     * Validates year input
     * @param yearText The year as text to validate
     * @param minYear Minimum allowed year
     * @param maxYear Maximum allowed year
     * @return ValidationResult with isValid flag and error message
     */
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
    
    /**
     * Applies visual validation feedback to a TextField
     * @param textField The TextField to style
     * @param isValid Whether the validation passed
     * @param errorLabel Optional label to display error message (can be null)
     * @param errorMessage The error message to display
     */
    public static void applyValidationStyle(TextField textField, boolean isValid, Label errorLabel, String errorMessage) {
        if (textField == null) return;
        
        if (isValid) {
            // Valid state - remove error styling
            textField.getStyleClass().removeAll("validation-error");
            textField.setStyle("");
        } else {
            // Invalid state - add error styling
            if (!textField.getStyleClass().contains("validation-error")) {
                textField.getStyleClass().add("validation-error");
            }
            textField.setStyle("-fx-border-color: #dc2626; -fx-border-width: 2px;");
        }
        
        // Update error label if provided
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
    
    /**
     * Validates that a ComboBox has a selected value
     * @param value The selected value (can be null)
     * @param fieldName The name of the field for error message
     * @return ValidationResult with isValid flag and error message
     */
    public static ValidationResult validateComboBoxSelection(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return new ValidationResult(false, 
                String.format("Παρακαλώ επιλέξτε %s.", fieldName));
        }
        return new ValidationResult(true, "");
    }
    
    /**
     * Inner class to hold validation results
     */
    public static class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;
        
        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage != null ? errorMessage : "";
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}

