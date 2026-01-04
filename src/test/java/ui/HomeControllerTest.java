package ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HomeController class.
 */
class HomeControllerTest {

    @Test
    void testUserTypeEnum() {
        // Test UserType enum values
        assertNotNull(HomeController.UserType.CITIZEN);
        assertNotNull(HomeController.UserType.GOVERNMENT);
    }

    @Test
    void testSetUserType() {
        HomeController.setUserType(HomeController.UserType.CITIZEN);
        assertEquals(HomeController.UserType.CITIZEN, HomeController.getUserType());
        
        HomeController.setUserType(HomeController.UserType.GOVERNMENT);
        assertEquals(HomeController.UserType.GOVERNMENT, HomeController.getUserType());
    }

    @Test
    void testGetUserType() {
        HomeController.setUserType(HomeController.UserType.CITIZEN);
        assertEquals(HomeController.UserType.CITIZEN, HomeController.getUserType());
    }

    @Test
    void testIsGovernmentUser() {
        HomeController.setUserType(HomeController.UserType.CITIZEN);
        assertFalse(HomeController.isGovernmentUser());
        
        HomeController.setUserType(HomeController.UserType.GOVERNMENT);
        assertTrue(HomeController.isGovernmentUser());
    }

    @Test
    void testCategoryDataConstructor() {
        HomeController.CategoryData data = new HomeController.CategoryData(
            "Test Category", 1000.0, 10.0, "Test Change"
        );
        assertNotNull(data);
        assertEquals("Test Category", data.getCategory());
        assertEquals(1000.0, data.getAmount());
        assertEquals(10.0, data.getPercentage());
        assertEquals("Test Change", data.getChange());
    }

    @Test
    void testCategoryDataWithPreviousYear() {
        HomeController.CategoryData data = new HomeController.CategoryData(
            "Test Category", 1000.0, 10.0, "Test Change", "Έσοδο", 900.0
        );
        assertNotNull(data);
        assertEquals(900.0, data.getPreviousYearAmount());
    }
}

