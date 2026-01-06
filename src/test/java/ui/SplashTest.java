package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Splash class.
 * Note: Full testing requires JavaFX Application context.
 */
class SplashTest {

    @Test
    void testSplashExists() {
        // Test that the class exists and can be instantiated
        Splash controller = new Splash();
        assertNotNull(controller);
    }

    @Test
    void testSplashClass() {
        // Test that the class can be referenced
        Class<?> clazz = Splash.class;
        assertNotNull(clazz);
    }
}
