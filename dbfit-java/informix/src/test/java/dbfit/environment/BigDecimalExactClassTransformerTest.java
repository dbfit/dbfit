package dbfit.environment;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.math.BigDecimal;

import dbfit.util.NormalisedBigDecimal;

public class BigDecimalExactClassTransformerTest {

    private final BigDecimalExactClassTransformer bdt = new BigDecimalExactClassTransformer();
    private final BigDecimal inBigDec = new BigDecimal("123.45");

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void transformsBigDecimaltoBigDecimalTest() {
        BigDecimal bd = new BigDecimal(inBigDec.toString());
        BigDecimal outBigDec = (BigDecimal) bdt.transform(bd);
        assertEquals(outBigDec, inBigDec);
    }

    @Test
    public void transformsNormalisedBigDecimaltoBigDecimalTest() {
        NormalisedBigDecimal nbd = new NormalisedBigDecimal(inBigDec);
        BigDecimal outBigDec = (BigDecimal) bdt.transform(nbd);
        assertEquals(outBigDec, inBigDec);
    }

    @Test
    public void rejectsNonBigDecimalInputTest() {
        Double notABigBecimal = 1.0;
        exception.expect(UnsupportedOperationException.class);
        exception.expectMessage("BigDecimalExactClassTransformer cannot transform objects of type java.lang.Double");
        bdt.transform(notABigBecimal);
    }

    @Test
    public void transformsToExactlyBigDecimalTest() {
        NormalisedBigDecimal nbd = new NormalisedBigDecimal(inBigDec);
        Object outBigDec = bdt.transform(nbd);
        assertEquals(outBigDec.getClass(), BigDecimal.class);
    }
}
