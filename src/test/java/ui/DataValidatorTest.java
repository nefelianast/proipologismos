package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DataValidator utility class.
 */
class DataValidatorTest {

    // Username validation tests
    @Test
    void testValidateUsernameValid() {
        DataValidator.ValidationResult result = DataValidator.validateUsername("testuser");
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidateUsernameWithGreek() {
        DataValidator.ValidationResult result = DataValidator.validateUsername("χρήστης123");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateUsernameNull() {
        DataValidator.ValidationResult result = DataValidator.validateUsername(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτικό"));
    }

    @Test
    void testValidateUsernameEmpty() {
        DataValidator.ValidationResult result = DataValidator.validateUsername("");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτικό"));
    }

    @Test
    void testValidateUsernameTooShort() {
        DataValidator.ValidationResult result = DataValidator.validateUsername("ab");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("τουλάχιστον"));
    }

    @Test
    void testValidateUsernameTooLong() {
        String longUsername = "a".repeat(51);
        DataValidator.ValidationResult result = DataValidator.validateUsername(longUsername);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υπερβαίνει"));
    }

    @Test
    void testValidateUsernameWithSpaces() {
        // Username with spaces - after trim, "test user" still has space, which is not allowed by regex
        DataValidator.ValidationResult result = DataValidator.validateUsername("test user");
        assertFalse(result.isValid()); // Spaces are not allowed even after trim
        assertTrue(result.getErrorMessage().contains("μόνο γράμματα"));
    }

    @Test
    void testValidateUsernameInvalidCharacters() {
        DataValidator.ValidationResult result = DataValidator.validateUsername("test@user");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("μόνο γράμματα"));
    }

    // Password validation tests
    @Test
    void testValidatePasswordValid() {
        DataValidator.ValidationResult result = DataValidator.validatePassword("password123");
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidatePasswordNull() {
        DataValidator.ValidationResult result = DataValidator.validatePassword(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτικός"));
    }

    @Test
    void testValidatePasswordEmpty() {
        DataValidator.ValidationResult result = DataValidator.validatePassword("");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτικός"));
    }

    @Test
    void testValidatePasswordTooShort() {
        DataValidator.ValidationResult result = DataValidator.validatePassword("12345");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("τουλάχιστον"));
    }

    @Test
    void testValidatePasswordTooLong() {
        String longPassword = "a".repeat(101);
        DataValidator.ValidationResult result = DataValidator.validatePassword(longPassword);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υπερβαίνει"));
    }

    @Test
    void testValidatePasswordMinimumLength() {
        DataValidator.ValidationResult result = DataValidator.validatePassword("123456");
        assertTrue(result.isValid());
    }

    // Category validation tests
    @Test
    void testValidateCategoryValid() {
        DataValidator.ValidationResult result = DataValidator.validateCategory("Υπουργείο");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateCategoryNull() {
        DataValidator.ValidationResult result = DataValidator.validateCategory(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτική"));
    }

    @Test
    void testValidateCategoryEmpty() {
        DataValidator.ValidationResult result = DataValidator.validateCategory("");
        assertFalse(result.isValid());
    }

    @Test
    void testValidateCategoryTooShort() {
        DataValidator.ValidationResult result = DataValidator.validateCategory("A");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("τουλάχιστον"));
    }

    @Test
    void testValidateCategoryTooLong() {
        String longCategory = "A".repeat(101);
        DataValidator.ValidationResult result = DataValidator.validateCategory(longCategory);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υπερβαίνει"));
    }

    // Amount validation tests
    @Test
    void testValidateAmountValid() {
        DataValidator.ValidationResult result = DataValidator.validateAmount("1000.50");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountInteger() {
        DataValidator.ValidationResult result = DataValidator.validateAmount("1000");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountNull() {
        DataValidator.ValidationResult result = DataValidator.validateAmount(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτικό"));
    }

    @Test
    void testValidateAmountEmpty() {
        DataValidator.ValidationResult result = DataValidator.validateAmount("");
        assertFalse(result.isValid());
    }

    @Test
    void testValidateAmountNegative() {
        // Negative amount - pattern doesn't match, so returns error about positive number
        DataValidator.ValidationResult result = DataValidator.validateAmount("-100");
        assertFalse(result.isValid());
        // The error message is about positive number format, not specifically "αρνητικό"
        assertTrue(result.getErrorMessage().contains("θετικός") || result.getErrorMessage().contains("αρνητικό"));
    }

    @Test
    void testValidateAmountZero() {
        DataValidator.ValidationResult result = DataValidator.validateAmount("0");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountInvalidFormat() {
        DataValidator.ValidationResult result = DataValidator.validateAmount("abc");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("θετικός αριθμός"));
    }

    @Test
    void testValidateAmountWithSpaces() {
        DataValidator.ValidationResult result = DataValidator.validateAmount(" 1000 ");
        assertTrue(result.isValid()); // Spaces are trimmed
    }

    // Percentage validation tests
    @Test
    void testValidatePercentageValid() {
        DataValidator.ValidationResult result = DataValidator.validatePercentage("15.5");
        assertTrue(result.isValid());
    }

    @Test
    void testValidatePercentageNegative() {
        DataValidator.ValidationResult result = DataValidator.validatePercentage("-5.2");
        assertTrue(result.isValid());
    }

    @Test
    void testValidatePercentageNull() {
        DataValidator.ValidationResult result = DataValidator.validatePercentage(null);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτική"));
    }

    @Test
    void testValidatePercentageEmpty() {
        DataValidator.ValidationResult result = DataValidator.validatePercentage("");
        assertFalse(result.isValid());
    }

    @Test
    void testValidatePercentageInvalidFormat() {
        DataValidator.ValidationResult result = DataValidator.validatePercentage("abc");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("αριθμός"));
    }

    @Test
    void testValidatePercentageZero() {
        DataValidator.ValidationResult result = DataValidator.validatePercentage("0");
        assertTrue(result.isValid());
    }

    // Year validation tests
    @Test
    void testValidateYearValid() {
        DataValidator.ValidationResult result = DataValidator.validateYear("2024", 2020, 2030);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateYearAtMinimum() {
        DataValidator.ValidationResult result = DataValidator.validateYear("2020", 2020, 2030);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateYearAtMaximum() {
        DataValidator.ValidationResult result = DataValidator.validateYear("2030", 2020, 2030);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateYearBelowMinimum() {
        DataValidator.ValidationResult result = DataValidator.validateYear("2019", 2020, 2030);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("μεταξύ"));
    }

    @Test
    void testValidateYearAboveMaximum() {
        DataValidator.ValidationResult result = DataValidator.validateYear("2031", 2020, 2030);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("μεταξύ"));
    }

    @Test
    void testValidateYearNull() {
        DataValidator.ValidationResult result = DataValidator.validateYear(null, 2020, 2030);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υποχρεωτικό"));
    }

    @Test
    void testValidateYearEmpty() {
        DataValidator.ValidationResult result = DataValidator.validateYear("", 2020, 2030);
        assertFalse(result.isValid());
    }

    @Test
    void testValidateYearInvalidFormat() {
        DataValidator.ValidationResult result = DataValidator.validateYear("abc", 2020, 2030);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("έγκυρος αριθμός"));
    }

    // ComboBox validation tests
    @Test
    void testValidateComboBoxSelectionValid() {
        DataValidator.ValidationResult result = DataValidator.validateComboBoxSelection("Option1", "Κατηγορία");
        assertTrue(result.isValid());
    }

    @Test
    void testValidateComboBoxSelectionNull() {
        DataValidator.ValidationResult result = DataValidator.validateComboBoxSelection(null, "Κατηγορία");
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Παρακαλώ επιλέξτε"));
    }

    @Test
    void testValidateComboBoxSelectionEmpty() {
        DataValidator.ValidationResult result = DataValidator.validateComboBoxSelection("", "Κατηγορία");
        assertFalse(result.isValid());
    }

    @Test
    void testValidateComboBoxSelectionWithSpaces() {
        DataValidator.ValidationResult result = DataValidator.validateComboBoxSelection("  Option  ", "Κατηγορία");
        assertTrue(result.isValid()); // Spaces are trimmed
    }

    // ValidationResult inner class tests
    @Test
    void testValidationResultValid() {
        DataValidator.ValidationResult result = new DataValidator.ValidationResult(true, "");
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidationResultInvalid() {
        DataValidator.ValidationResult result = new DataValidator.ValidationResult(false, "Error message");
        assertFalse(result.isValid());
        assertEquals("Error message", result.getErrorMessage());
    }

    @Test
    void testValidationResultNullMessage() {
        DataValidator.ValidationResult result = new DataValidator.ValidationResult(true, null);
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage()); // null should be converted to empty string
    }
}

