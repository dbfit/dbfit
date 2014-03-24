package dbfit.util;

import java.math.BigDecimal;

import org.junit.Test;
import static org.junit.Assert.*;

public class BigDecimalNormaliserTest {
    private BigDecimalNormaliser normaliser = new BigDecimalNormaliser();
    private BigDecimal oneDigitScaleDecimal = new BigDecimal("3.1");
    private BigDecimal fourDigitScaleDecimal = new BigDecimal("3.1000");

    @Test
    public void valuesEqualityDoesNotDependOnScale() {
        assertEquals(normalise(oneDigitScaleDecimal), normalise(fourDigitScaleDecimal));
    }

    @Test
    public void equalsToOriginalValue() {
        assertEquals(oneDigitScaleDecimal, normalise(oneDigitScaleDecimal));
        assertEquals(fourDigitScaleDecimal, normalise(fourDigitScaleDecimal));
    }

    @Test
    public void shouldNotBeEqualToNull() {
        assertFalse(normalise(oneDigitScaleDecimal).equals(null));
    }

    @Test
    public void normalisedNullIsNull() {
        assertNull(normalise(null));
    }

    private BigDecimal normalise(BigDecimal val) {
        return (BigDecimal) normaliser.normalise(val);
    }
}
