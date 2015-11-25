package dbfit.util;

import org.junit.Test;
import org.junit.Before;

import java.sql.SQLException;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class TypeTransformerFactoryTest {

    private String expectedString = new String("Top");
    private Integer expectedInteger = new Integer(5);

    private class classTop {
    }

    private class classMid extends classTop {
    }

    private class classLow extends classMid {
    }

    private class TransformerTop implements TypeTransformer {
        public Object transform(Object value) {
            return expectedString;
        }
    }

    private class TransformerMid implements TypeTransformer {
        public Object transform(Object value) {
            return expectedInteger;
        }
    }

    private final Class<?> ctop = classTop.class;
    private final Class<?> cmid = classMid.class;

    private final classTop otop = new classTop();
    private final classMid omid = new classMid();
    private final classLow olow = new classLow();

    private final TransformerTop ttop = new TransformerTop();
    private final TransformerMid tmid = new TransformerMid();

    private TypeTransformerFactory ttf = new TypeTransformerFactory(); 

    @Before
    public void init() {
        ttf.setTransformer(ctop, ttop);
        ttf.setTransformer(cmid, tmid);
    }

    @Test
    public void transformerFactoryUsesClosestParentTransformerIfNoExactMatch() throws SQLException {
        Object transformed = ttf.transform(olow);
        assertEquals((Integer) transformed, expectedInteger);
    }

    @Test
    public void transformerFactoryUsesExactMatchTransformerIfAny() throws SQLException {
        Object transformed = ttf.transform(omid);
        assertEquals((Integer) transformed, expectedInteger);
        transformed = ttf.transform(otop);
        assertEquals((String) transformed, expectedString);
    }

    @Test
    public void transformerFactoryReturnsInputValueWhenNoExactMatchNorParents() throws SQLException {
        String inputValue = "A string";
        Object transformed = ttf.transform(inputValue);
        assertEquals(transformed, inputValue);
    }
}
