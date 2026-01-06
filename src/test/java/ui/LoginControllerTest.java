package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Login class.
 * Note: Full testing requires JavaFX Application context.
 */
class LoginControllerTest {

    @Test
    void testLoginExists() {
        // Test that the class exists and can be instantiated
        Login controller = new Login();
        assertNotNull(controller);
    }

    @Test
    void testLoginClass() {
        // Test that the class can be referenced
        Class<?> clazz = Login.class;
        assertNotNull(clazz);
    }
}

