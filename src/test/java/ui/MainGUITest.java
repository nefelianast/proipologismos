package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MainGUI class.
 * Note: Full testing requires JavaFX Application context.
 */
class MainGUITest {

    @Test
    void testMainGUIClassExists() {
        // Test that the class exists
        Class<?> clazz = MainGUI.class;
        assertNotNull(clazz);
    }

    @Test
    void testMainMethodExists() {
        // Test that main method exists
        try {
            MainGUI.class.getMethod("main", String[].class);
            assertTrue(true);
        } catch (NoSuchMethodException e) {
            fail("main method should exist");
        }
    }

    @Test
    void testMainGUIExtendsApplication() {
        // Test that MainGUI extends Application
        assertTrue(javafx.application.Application.class.isAssignableFrom(MainGUI.class));
    }
}

