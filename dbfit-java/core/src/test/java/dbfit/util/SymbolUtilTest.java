package dbfit.util;

import static dbfit.util.SymbolUtil.getSymbol;
import static dbfit.util.SymbolUtil.isSymbolGetter;
import static dbfit.util.SymbolUtil.isSymbolSetter;
import static dbfit.util.SymbolUtil.isSymbolHidden;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

public class SymbolUtilTest {

    @Before
    public void prepare() {
        SymbolUtil.setSymbol("NULL_SYMBOL", null);
        SymbolUtil.setSymbol("SYMBOL_X", "X");
    }

    @Test
    public void canGetNonNullGetSymbols() {
        assertEquals("X", getSymbol("<<SYMBOL_X"));
    }

    @Test
    public void canGetNonNullSetSymbols() {
        assertEquals("X", getSymbol(">>SYMBOL_X"));
    }

    @Test
    public void canGetNullSymbols() {
        assertNull(getSymbol("<<NULL_SYMBOL"));
    }

    @Test
    public void undefinedSymbolDefaultsToNull() {
        assertNull(getSymbol("<<MISSING_SYMBOL"));
    }

    @RunWith(Parameterized.class)
    public static class IsSymbolGetterOrSetterTest {
        private String symbolFullName;
        private boolean expectedIsSymbolGetter;
        private boolean expectedIsSymbolSetter;
        private boolean expectedIsSymbolHidden;

        public IsSymbolGetterOrSetterTest(
                String symbolFullName,
                Boolean expectedIsSymbolGetter,
                Boolean expectedIsSymbolSetter,
                Boolean expectedIsSymbolHidden) {
            this.symbolFullName = symbolFullName;
            this.expectedIsSymbolGetter = expectedIsSymbolGetter;
            this.expectedIsSymbolSetter = expectedIsSymbolSetter;
            this.expectedIsSymbolHidden = expectedIsSymbolHidden;
        }

        @Parameters(name = "({index}): symbol {0} -> expecting {1}")
        public static Collection<Object[]> data() throws Exception {
            return java.util.Arrays.asList(new Object[][] {
                {"<<SYMBOL_X",  true,  false, false},
                {">>SYMBOL_X",  false, true,  false},
                {"<<<SYMBOL_X", true,  false, true},
                {">>>SYMBOL_X", false, true,  true},
                {"SYMBOL_X",    false, false, false},
                {null,          false, false, false}
            });
        }

        @Test
        public void testIsSymbolGetter() {
            assertEquals(expectedIsSymbolGetter, isSymbolGetter(symbolFullName));
        }

        @Test
        public void testIsSymbolSetter() {
            assertEquals(expectedIsSymbolSetter, isSymbolSetter(symbolFullName));
        }

        @Test
        public void testIsSymbolHidden() {
            assertEquals(expectedIsSymbolHidden, isSymbolHidden(symbolFullName));
        }
    }
}
