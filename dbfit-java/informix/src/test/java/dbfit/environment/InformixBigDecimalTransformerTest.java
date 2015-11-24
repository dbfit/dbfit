package dbfit.environment;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;
import java.lang.Double;
import java.sql.SQLException;

import dbfit.util.NormalisedBigDecimal;

public class InformixBigDecimalTransformerTest {

    private final InformixBigDecimalTransformer bdt = new InformixBigDecimalTransformer();
    private final BigDecimal inBigDec = new BigDecimal("123.45");

    @Test
    public void transformsBigDecimaltoBigDecimalTest() throws SQLException {
        BigDecimal bd = new BigDecimal(inBigDec.toString());
        BigDecimal outBigDec = (BigDecimal) bdt.transform(bd);
        assertEquals(outBigDec, inBigDec);
    }

    @Test
    public void transformsNormalisedBigDecimaltoBigDecimalTest() throws SQLException {
        NormalisedBigDecimal nbd = new NormalisedBigDecimal(inBigDec);
        BigDecimal outBigDec = (BigDecimal) bdt.transform(nbd);
        assertEquals(outBigDec, inBigDec);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void rejectsNonBigDecimalInputTest() {
        Double notABigBecimal = 1.0;
        exception.expect(UnsupportedOperationException.class);
        exception.expectMessage("InformixBigDecimalTransformer cannot transform objects of type java.lang.Double");
        try {
            bdt.transform(notABigBecimal);
        } catch (java.sql.SQLException e) {
        }
    }
}
