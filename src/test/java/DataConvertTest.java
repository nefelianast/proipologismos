
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DataConvert class.
 */
class DataConvertTest {

    @Test
    void testConvertiontoolWithNonExistentFile() {
        // Test with non-existent file
        assertDoesNotThrow(() -> {
            DataConvert.convertiontool(9999);
        });
    }

    @Test
    void testConvertiontoolWithValidYear() {
        // Test with valid year 
        assertDoesNotThrow(() -> {
            DataConvert.convertiontool(2023);
        });
    }

    @Test
    void testConvertiontoolWithDifferentYears() {
        // Test with different years
        assertDoesNotThrow(() -> {
            DataConvert.convertiontool(2024);
            DataConvert.convertiontool(2025);
            DataConvert.convertiontool(2026);
        });
    }
}

