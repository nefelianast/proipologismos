
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DataStorer class.
 */
class DataStorerTest {

    @Test
    void testStore() {
        DataStorer storer = new DataStorer();
        assertDoesNotThrow(() -> storer.Store());
    }

    @Test
    void testStoreWithConnection() {
        DataStorer storer = new DataStorer();
        // Test that Store() can be called without errors
        assertDoesNotThrow(() -> storer.Store());
    }
}

