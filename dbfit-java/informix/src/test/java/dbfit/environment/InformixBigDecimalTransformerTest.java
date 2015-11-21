package dbfit.environment;

import static org.junit.Assert.*;
import org.junit.Test;
import java.math.BigDecimal;
import java.sql.SQLException;

import dbfit.util.NormalisedBigDecimal;

public class InformixBigDecimalTransformerTest {

    @Test
    public void transformsTest() {
        InformixBigDecimalTransformer bdt = new InformixBigDecimalTransformer();
        BigDecimal inBigDec = new BigDecimal("123.45");
        NormalisedBigDecimal nbd = new NormalisedBigDecimal(inBigDec);
        BigDecimal outBigDec = null;
        try {
            outBigDec = (BigDecimal) bdt.transform(nbd);
        } catch (SQLException e) {
            fail("InformixBigDecimalTransformer threw SQLException transforming NormalisedBigDecimal input");
        }
        assertEquals(outBigDec, inBigDec);
    }

    @Test
    public void rejectsInputTest() {
        InformixBigDecimalTransformer bdt = new InformixBigDecimalTransformer();
        BigDecimal inBigDec = new BigDecimal("123.45");
        boolean caught = false;
        String expectedMsg = "InformixBigDecimalTransformer cannot transform objects of type java.math.BigDecimal";
        String actualMsg = "";
        try {
            bdt.transform(inBigDec);
        } catch (SQLException e) {
            caught = true;
            actualMsg = e.getMessage();
        }
        if (caught) {
            assertTrue(expectedMsg.equals(actualMsg));
        } else {
            fail("InformixBigDecimalTransformer did not throw SQLException transforming java.math.BigDecimal input");
        }
    }
}
