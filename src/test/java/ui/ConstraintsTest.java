package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConstraintsTest {

    @Test
    void testValidateUsernameValid() {
        Constraints.ValidationResult result = Constraints.validateUsername("testuser");
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidateUsernameWithGreek() {
        Constraints.ValidationResult result = Constraints.validateUsername("χρήστης123");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateUsernameNull() {
        Constraints.ValidationResult result = Constraints.validateUsername(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτικό"));
    }

    @Test
    void testValidateUsernameEmpty() {
        Constraints.ValidationResult result = Constraints.validateUsername("");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτικό"));
    }

    @Test
    void testValidateUsernameTooShort() {
        Constraints.ValidationResult result = Constraints.validateUsername("ab");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("τουλάχιστον"));
    }

    @Test
    void testValidateUsernameTooLong() {
        String longUsername = "a".repeat(51);
        Constraints.ValidationResult result = Constraints.validateUsername(longUsername);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υπερβαίνει"));
    }

    @Test
    void testValidateUsernameWithSpaces() {
        Constraints.ValidationResult result = Constraints.validateUsername("test user");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("μόνο γράμματα"));
    }

    @Test
    void testValidateUsernameInvalidCharacters() {
        Constraints.ValidationResult result = Constraints.validateUsername("test@user");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("μόνο γράμματα"));
    }

    @Test
    void testValidatePasswordValid() {
        Constraints.ValidationResult result = Constraints.validatePassword("password123");
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidatePasswordNull() {
        Constraints.ValidationResult result = Constraints.validatePassword(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτικός"));
    }

    @Test
    void testValidatePasswordEmpty() {
        Constraints.ValidationResult result = Constraints.validatePassword("");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτικός"));
    }

    @Test
    void testValidatePasswordTooShort() {
        Constraints.ValidationResult result = Constraints.validatePassword("12345");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("τουλάχιστον"));
    }

    @Test
    void testValidatePasswordTooLong() {
        String longPassword = "a".repeat(101);
        Constraints.ValidationResult result = Constraints.validatePassword(longPassword);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υπερβαίνει"));
    }

    @Test
    void testValidatePasswordMinimumLength() {
        Constraints.ValidationResult result = Constraints.validatePassword("123456");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateCategoryValid() {
        Constraints.ValidationResult result = Constraints.validateCategory("Υπουργείο");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateCategoryNull() {
        Constraints.ValidationResult result = Constraints.validateCategory(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτική"));
    }

    @Test
    void testValidateCategoryEmpty() {
        Constraints.ValidationResult result = Constraints.validateCategory("");
        assertFalse(result.isValid());
    }

    @Test
    void testValidateCategoryTooShort() {
        Constraints.ValidationResult result = Constraints.validateCategory("A");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("τουλάχιστον"));
    }

    @Test
    void testValidateCategoryTooLong() {
        String longCategory = "A".repeat(101);
        Constraints.ValidationResult result = Constraints.validateCategory(longCategory);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υπερβαίνει"));
    }

    @Test
    void testValidateAmountValid() {
        Constraints.ValidationResult result = Constraints.validateAmount("1000.50");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountInteger() {
        Constraints.ValidationResult result = Constraints.validateAmount("1000");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountNull() {
        Constraints.ValidationResult result = Constraints.validateAmount(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτικό"));
    }

    @Test
    void testValidateAmountEmpty() {
        Constraints.ValidationResult result = Constraints.validateAmount("");
        assertFalse(result.isValid());
    }

    @Test
    void testValidateAmountNegative() {
        Constraints.ValidationResult result = Constraints.validateAmount("-100");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("θετικός") || result.getErrorMessage().contains("αρνητικό"));
    }

    @Test
    void testValidateAmountZero() {
        Constraints.ValidationResult result = Constraints.validateAmount("0");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountInvalidFormat() {
        Constraints.ValidationResult result = Constraints.validateAmount("abc");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("θετικός αριθμός"));
    }

    @Test
    void testValidateAmountWithSpaces() {
        Constraints.ValidationResult result = Constraints.validateAmount(" 1000 ");
        assertTrue(result.isValid());
    }

    @Test
    void testValidatePercentageValid() {
        Constraints.ValidationResult result = Constraints.validatePercentage("15.5");
        assertTrue(result.isValid());
    }

    @Test
    void testValidatePercentageNegative() {
        Constraints.ValidationResult result = Constraints.validatePercentage("-5.2");
        assertTrue(result.isValid());
    }

    @Test
    void testValidatePercentageNull() {
        Constraints.ValidationResult result = Constraints.validatePercentage(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτική"));
    }

    @Test
    void testValidatePercentageEmpty() {
        Constraints.ValidationResult result = Constraints.validatePercentage("");
        assertFalse(result.isValid());
    }

    @Test
    void testValidatePercentageInvalidFormat() {
        Constraints.ValidationResult result = Constraints.validatePercentage("abc");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("αριθμός"));
    }

    @Test
    void testValidatePercentageZero() {
        Constraints.ValidationResult result = Constraints.validatePercentage("0");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateYearValid() {
        Constraints.ValidationResult result = Constraints.validateYear("2024", 2020, 2030);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateYearAtMinimum() {
        Constraints.ValidationResult result = Constraints.validateYear("2020", 2020, 2030);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateYearAtMaximum() {
        Constraints.ValidationResult result = Constraints.validateYear("2030", 2020, 2030);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateYearBelowMinimum() {
        Constraints.ValidationResult result = Constraints.validateYear("2019", 2020, 2030);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("μεταξύ"));
    }

    @Test
    void testValidateYearAboveMaximum() {
        Constraints.ValidationResult result = Constraints.validateYear("2031", 2020, 2030);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("μεταξύ"));
    }

    @Test
    void testValidateYearNull() {
        Constraints.ValidationResult result = Constraints.validateYear(null, 2020, 2030);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτικό"));
    }

    @Test
    void testValidateYearEmpty() {
        Constraints.ValidationResult result = Constraints.validateYear("", 2020, 2030);
        assertFalse(result.isValid());
    }

    @Test
    void testValidateYearInvalidFormat() {
        Constraints.ValidationResult result = Constraints.validateYear("abc", 2020, 2030);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("έγκυρος αριθμός"));
    }

    @Test
    void testValidateComboBoxSelectionValid() {
        Constraints.ValidationResult result = Constraints.validateComboBoxSelection("Option1", "Κατηγορία");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateComboBoxSelectionNull() {
        Constraints.ValidationResult result = Constraints.validateComboBoxSelection(null, "Κατηγορία");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Παρακαλώ επιλέξτε"));
    }

    @Test
    void testValidateComboBoxSelectionEmpty() {
        Constraints.ValidationResult result = Constraints.validateComboBoxSelection("", "Κατηγορία");
        assertFalse(result.isValid());
    }

    @Test
    void testValidateComboBoxSelectionWithSpaces() {
        Constraints.ValidationResult result = Constraints.validateComboBoxSelection("  Option  ", "Κατηγορία");
        assertTrue(result.isValid());
    }

    @Test
    void testValidationResultValid() {
        Constraints.ValidationResult result = new Constraints.ValidationResult(true, "");
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidationResultInvalid() {
        Constraints.ValidationResult result = new Constraints.ValidationResult(false, "Error message");
        assertFalse(result.isValid());
        assertEquals("Error message", result.getErrorMessage());
    }

    @Test
    void testValidationResultNullMessage() {
        Constraints.ValidationResult result = new Constraints.ValidationResult(true, null);
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidateAmountChangeWithinLimit() {
        Constraints.ValidationResult result = 
            Constraints.validateAmountChange(120.0, 100.0);
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidateAmountChangeExceedsDefaultLimit() {
        Constraints.ValidationResult result = 
            Constraints.validateAmountChange(200.0, 100.0);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υπερβαίνει"));
        assertTrue(result.getErrorMessage().contains("μέγιστο επιτρεπόμενο όριο"));
    }

    @Test
    void testValidateAmountChangeNegativeChange() {
        Constraints.ValidationResult result = 
            Constraints.validateAmountChange(50.0, 100.0);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountChangeNegativeChangeExceedsLimit() {
        Constraints.ValidationResult result = 
            Constraints.validateAmountChange(20.0, 100.0);
        assertFalse(result.isValid());
    }

    @Test
    void testValidateAmountChangeNoChange() {
        Constraints.ValidationResult result = 
            Constraints.validateAmountChange(100.0, 100.0);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountChangeNewEntry() {
        Constraints.ValidationResult result = 
            Constraints.validateAmountChange(100.0, 0.0);
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidateAmountChangeExactlyAtLimit() {
        Constraints.ValidationResult result = 
            Constraints.validateAmountChange(150.0, 100.0);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountChangeJustBelowLimit() {
        Constraints.ValidationResult result = 
            Constraints.validateAmountChange(149.0, 100.0);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountChangeLargeNumbers() {
        Constraints.ValidationResult result = 
            Constraints.validateAmountChange(1500000.0, 1000000.0);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountChangeSmallNumbers() {
        Constraints.ValidationResult result = 
            Constraints.validateAmountChange(1.5, 1.0);
        assertTrue(result.isValid());
    }
}
