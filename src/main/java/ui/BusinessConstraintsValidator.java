package ui;


public class BusinessConstraintsValidator {
    
    /**
     * Validates if the amount change is within reasonable limits compared to previous year
     */
    public static ValidationResult validateAmountChange(double newAmount, double previousAmount, double maxChangePercent) {
        if (previousAmount == 0) {
            return new ValidationResult(true, ""); // New entry, no previous data
        }
        
        double changePercent = Math.abs(BudgetStatisticsCalculator.calculatePercentageChange(newAmount, previousAmount));
        
        if (changePercent > maxChangePercent) {
            return new ValidationResult(false, 
                String.format("Η αλλαγή (%.1f%%) υπερβαίνει το μέγιστο επιτρεπόμενο όριο (%.1f%%). Παρακαλώ επιβεβαιώστε ότι η αλλαγή είναι σωστή.", 
                    changePercent, maxChangePercent));
        }
        
        return new ValidationResult(true, "");
    }
    
    /**
     * Validates if the amount change is within reasonable limits (default 50% change)
     */
    public static ValidationResult validateAmountChange(double newAmount, double previousAmount) {
        return validateAmountChange(newAmount, previousAmount, 50.0);
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

