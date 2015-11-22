package dbfit.environment;

import static org.junit.Assert.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;

import dbfit.util.NormalisedBigDecimal;

public class InformixBigDecimalTransformerTest {

    private InformixBigDecimalTransformer bdt = new InformixBigDecimalTransformer();
    private BigDecimal inBigDec = new BigDecimal("123.45");

    @Test
    public void transformsNormalisedBigDecimaltoBigDecimalTest() throws SQLException {
        NormalisedBigDecimal nbd = new NormalisedBigDecimal(inBigDec);
        BigDecimal outBigDec = (BigDecimal) bdt.transform(nbd);
        assertEquals(outBigDec, inBigDec);
    }

    @Test
    public void rejectsNonNormalisedBigDecimalInputTest() {
        String expectedMsg = "InformixBigDecimalTransformer cannot transform objects of type java.math.BigDecimal";
        try {
            bdt.transform(inBigDec);
            fail("InformixBigDecimalTransformer did not throw SQLException transforming java.math.BigDecimal input");
        } catch (SQLException e) {
            assertTrue(e.getMessage().equals(expectedMsg));
        }
    }
}
