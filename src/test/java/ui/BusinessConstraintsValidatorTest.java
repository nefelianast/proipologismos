package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BusinessConstraintsValidator utility class.
 */
class BusinessConstraintsValidatorTest {

    @Test
    void testValidateAmountChangeWithinLimit() {
        // Test change within 50% limit (default)
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(120.0, 100.0);
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidateAmountChangeExceedsDefaultLimit() {
        // Test change exceeds 50% limit (default)
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(200.0, 100.0);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("υπερβαίνει"));
        assertTrue(result.getErrorMessage().contains("μέγιστο επιτρεπόμενο όριο"));
    }

    @Test
    void testValidateAmountChangeNegativeChange() {
        // Test negative change within limit
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(50.0, 100.0);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountChangeNegativeChangeExceedsLimit() {
        // Test negative change exceeds limit
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(20.0, 100.0);
        assertFalse(result.isValid());
    }

    @Test
    void testValidateAmountChangeNoChange() {
        // Test no change
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(100.0, 100.0);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountChangeNewEntry() {
        // Test new entry (previous amount is 0)
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(100.0, 0.0);
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidateAmountChangeWithCustomLimit() {
        // Test with custom 20% limit - within limit
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(115.0, 100.0, 20.0);
        assertTrue(result.isValid());
    }

    @Test
    void testValidateAmountChangeWithCustomLimitExceeds() {
        // Test with custom 20% limit - exceeds limit
        // 125.0 from 100.0 = 25% change, 25% > 20% = invalid
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(125.0, 100.0, 20.0);
        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("20"));
    }

    @Test
    void testValidateAmountChangeWithCustomLimitNegative() {
        // Test negative change with custom limit
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(80.0, 100.0, 20.0);
        assertTrue(result.isValid()); // 20% decrease is within 20% limit
    }

    @Test
    void testValidateAmountChangeWithCustomLimitNegativeExceeds() {
        // Test negative change exceeds custom limit
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(70.0, 100.0, 20.0);
        assertFalse(result.isValid()); // 30% decrease exceeds 20% limit
    }

    @Test
    void testValidateAmountChangeExactlyAtLimit() {
        // Test change exactly at 50% limit
        // 50% is NOT > 50%, so it should be valid
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(150.0, 100.0);
        assertTrue(result.isValid()); // Exactly 50% is valid (limit is > 50%, not >=)
    }

    @Test
    void testValidateAmountChangeJustBelowLimit() {
        // Test change just below 50% limit
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(149.0, 100.0);
        assertTrue(result.isValid()); // 49% change is within limit
    }

    @Test
    void testValidateAmountChangeLargeNumbers() {
        // Test with large numbers
        // 1500000.0 from 1000000.0 = 50% change, 50% is NOT > 50%, so valid
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(1500000.0, 1000000.0);
        assertTrue(result.isValid()); // Exactly 50% is valid (limit is > 50%, not >=)
    }

    @Test
    void testValidateAmountChangeSmallNumbers() {
        // Test with small numbers
        // 1.5 from 1.0 = 50% change, 50% is NOT > 50%, so valid
        BusinessConstraintsValidator.ValidationResult result = 
            BusinessConstraintsValidator.validateAmountChange(1.5, 1.0);
        assertTrue(result.isValid()); // Exactly 50% is valid (limit is > 50%, not >=)
    }

    // ValidationResult inner class tests
    @Test
    void testValidationResultValid() {
        BusinessConstraintsValidator.ValidationResult result = 
            new BusinessConstraintsValidator.ValidationResult(true, "");
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    void testValidationResultInvalid() {
        BusinessConstraintsValidator.ValidationResult result = 
            new BusinessConstraintsValidator.ValidationResult(false, "Error message");
        assertFalse(result.isValid());
        assertEquals("Error message", result.getErrorMessage());
    }

    @Test
    void testValidationResultNullMessage() {
        BusinessConstraintsValidator.ValidationResult result = 
            new BusinessConstraintsValidator.ValidationResult(true, null);
        assertTrue(result.isValid());
        assertEquals("", result.getErrorMessage()); // null should be converted to empty string
    }
}

