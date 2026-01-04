package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SplashController class.
 * Note: Full testing requires JavaFX Application context.
 */
class SplashControllerTest {

    @Test
    void testSplashControllerExists() {
        // Test that the class exists and can be instantiated
        SplashController controller = new SplashController();
        assertNotNull(controller);
    }

    @Test
    void testSplashControllerClass() {
        // Test that the class can be referenced
        Class<?> clazz = SplashController.class;
        assertNotNull(clazz);
    }
}

