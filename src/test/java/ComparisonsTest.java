
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Comparisons class.
 */
class ComparisonsTest {

    @Test
    void testComparisonsOfTwoYears() {
        Comparisons comparisons = new Comparisons();
        // Test that comparisons_of_two_years can be called
        assertDoesNotThrow(() -> comparisons.comparisons_of_two_years(2023, 2024));
    }

    @Test
    void testComparisonsOfTwoYearsWithSameYear() {
        Comparisons comparisons = new Comparisons();
        // Test with same year
        assertDoesNotThrow(() -> comparisons.comparisons_of_two_years(2023, 2023));
    }

    @Test
    void testComparisonsOfTwoYearsWithDifferentYears() {
        Comparisons comparisons = new Comparisons();
        // Test with different years
        assertDoesNotThrow(() -> comparisons.comparisons_of_two_years(2024, 2025));
        assertDoesNotThrow(() -> comparisons.comparisons_of_two_years(2025, 2026));
    }

    @Test
    void testComparisonsOfTwoYearsWithReversedOrder() {
        Comparisons comparisons = new Comparisons();
        // Test with reversed order
        assertDoesNotThrow(() -> comparisons.comparisons_of_two_years(2026, 2023));
    }
}

