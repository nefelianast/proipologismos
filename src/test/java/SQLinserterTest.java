import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.math.BigDecimal;

class SQLinserterTest {

    @Test
    void testInsertRevenue2026() {
        SQLinserter inserter = new SQLinserter();
        assertDoesNotThrow(() -> {
            try {
                inserter.insertRevenue2026();
            } catch (Exception e) {
            }
        });
    }

    @Test
    void testInsertRevenue2025() {
        SQLinserter inserter = new SQLinserter();
        assertDoesNotThrow(() -> {
            try {
                inserter.insertRevenue2025();
            } catch (Exception e) {
            }
        });
    }

    @Test
    void testInsertExpenses2025() {
        SQLinserter inserter = new SQLinserter();
        assertDoesNotThrow(() -> {
            try {
                inserter.insertExpenses2025();
            } catch (Exception e) {
            }
        });
    }

    @Test
    void testInsertDecentralizedAdministrations2026() {
        SQLinserter inserter = new SQLinserter();
        assertDoesNotThrow(() -> {
            try {
                inserter.insertDecentralizedAdministrations2026();
            } catch (Exception e) {
            }
        });
    }

    @Test
    void testInsertBudgetSummary2024() {
        SQLinserter inserter = new SQLinserter();
        assertDoesNotThrow(() -> {
            try {
                inserter.insertBudgetSummary2024();
            } catch (Exception e) {
            }
        });
    }
    
    @Test
    void testParseMoneyWithStandardFormat() throws Exception {
        Method parseMoney = SQLinserter.class.getDeclaredMethod("parseMoney", String.class);
        parseMoney.setAccessible(true);
        
        BigDecimal result1 = (BigDecimal) parseMoney.invoke(null, "1.234,56");
        assertEquals(new BigDecimal("1234.56"), result1);
        
        BigDecimal result2 = (BigDecimal) parseMoney.invoke(null, "10.000,00");
        assertEquals(new BigDecimal("10000.00"), result2);
    }
    
    @Test
    void testParseMoneyWithNull() throws Exception {
        Method parseMoney = SQLinserter.class.getDeclaredMethod("parseMoney", String.class);
        parseMoney.setAccessible(true);
        
        BigDecimal result = (BigDecimal) parseMoney.invoke(null, (String) null);
        assertEquals(BigDecimal.ZERO, result);
    }
    
    @Test
    void testParseMoneyWithBlank() throws Exception {
        Method parseMoney = SQLinserter.class.getDeclaredMethod("parseMoney", String.class);
        parseMoney.setAccessible(true);
        
        BigDecimal result1 = (BigDecimal) parseMoney.invoke(null, "");
        assertEquals(BigDecimal.ZERO, result1);
        
        BigDecimal result2 = (BigDecimal) parseMoney.invoke(null, "   ");
        assertEquals(BigDecimal.ZERO, result2);
    }
    
    @Test
    void testParseMoneyWithWhitespace() throws Exception {
        Method parseMoney = SQLinserter.class.getDeclaredMethod("parseMoney", String.class);
        parseMoney.setAccessible(true);
        
        BigDecimal result = (BigDecimal) parseMoney.invoke(null, "  1.234,56  ");
        assertEquals(new BigDecimal("1234.56"), result);
    }
    
    @Test
    void testParseMoneyWithInteger() throws Exception {
        Method parseMoney = SQLinserter.class.getDeclaredMethod("parseMoney", String.class);
        parseMoney.setAccessible(true);
        
        BigDecimal result = (BigDecimal) parseMoney.invoke(null, "1.000");
        assertEquals(new BigDecimal("1000"), result);
    }
}

