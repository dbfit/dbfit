package dbfit.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValueParserTest {

    private ValueParser parser = new ValueParser();

    @Test
    public void parsingWithoutCustomDelegateReturnsItsInput() {
        assertEquals("abc", parser.parse("abc"));
    }

    @Test
    public void canParseEmptyString() {
        assertEquals("", parser.parse(""));
    }

    @Test
    public void canParseNullValue() {
        assertEquals(null, parser.parse(null));
    }

    @Test
    public void canParseNullString() {
        assertEquals(null, parser.parse("null"));
    }

    @Test
    public void canParseMixedCaseNullString() {
        assertEquals(null, parser.parse("NulL"));
    }

    @Test
    public void canParseFromSymbolReference() {
        SymbolUtil.setSymbol("SYMBOL_X", "X");
        assertEquals("X", parser.parse("<<SYMBOL_X"));
    }
}
