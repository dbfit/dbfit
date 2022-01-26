package dbfit.util;

import org.junit.Test;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class SymbolReferenceTest {

    public static abstract class AbstractSymbolReferenceTest {

        private String expectedName;
        private String expectedPrefix;
        private SymbolReference symbolReference;

        protected AbstractSymbolReferenceTest(String expectedName, String expectedPrefix, String fullName) {
            this.expectedName = expectedName;
            this.expectedPrefix = expectedPrefix;
            this.symbolReference = SymbolReference.fromFullName(fullName);
        }

        @Test
        public void testName() {
            assertEquals(expectedName, symbolReference.getName());
        }

        @Test
        public void textPrefix() {
            assertEquals(expectedPrefix, symbolReference.getPrefix());
        }
    }

    @Test
    public void testParseNullFullName() {
        SymbolReference symbolReference = SymbolReference.fromFullName(null);
        assertNull(symbolReference.getName());
        assertEquals("", symbolReference.getPrefix());
    }

    public static class StandardSymbolGetterReferenceTest extends AbstractSymbolReferenceTest {
        public StandardSymbolGetterReferenceTest() {
            super("SYMBOL_X", "<<", "<<SYMBOL_X");
        }
    }

    public static class StandardSymbolSetterReferenceTest extends AbstractSymbolReferenceTest {
        public StandardSymbolSetterReferenceTest() {
            super("SYMBOL_X", ">>", ">>SYMBOL_X");
        }
    }

    public static class HiddenSymbolGetterReferenceTest extends AbstractSymbolReferenceTest {
        public HiddenSymbolGetterReferenceTest() {
            super("SYMBOL_X", "<<<", "<<<SYMBOL_X");
        }
    }

    public static class HiddenSymbolSetterReferenceTest extends AbstractSymbolReferenceTest {
        public HiddenSymbolSetterReferenceTest() {
            super("SYMBOL_X", ">>>", ">>>SYMBOL_X");
        }
    }
}
