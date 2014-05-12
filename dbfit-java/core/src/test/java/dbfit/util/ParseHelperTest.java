package dbfit.util;

import fit.Fixture;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class ParseHelperTest {
    private ParseHelper parseHelper;

    @Before
    public void prepare() {
        SymbolUtil.setSymbol("NULL_SYMBOL", null);
        SymbolUtil.setSymbol("SYMBOL_X", "X");
        parseHelper = new ParseHelper(new Fixture(), String.class);
    }

    @Test
    public void canParseNonNullSymbols() throws Exception {
        assertEquals("X", parseHelper.parse("<<SYMBOL_X"));
    }

    @Test
    public void canParseNullSymbols() throws Exception {
        assertNull(parseHelper.parse("<<NULL_SYMBOL"));
    }

    @Test
    public void undefinedSymbolDefaultsToNull() throws Exception {
        assertNull(parseHelper.parse("<<MISSING_SYMBOL"));
    }
}
