package dbfit.util;

import org.junit.Test;
import org.junit.Before;

import static junit.framework.Assert.assertEquals;

public class TypeNormaliserFactoryTest {
    private static TypeNormaliser createTypeNormaliserFake(final String tag) {
        return new TypeNormaliser() {
            @Override
            public Object normalise(Object o) {
                return null;
            }

            @Override
            public String toString() {
                return tag;
            }
        };
    }

    private final TypeNormaliser objectNormaliser = createTypeNormaliserFake("objectNormaliser");
    private final TypeNormaliser numberNormaliser = createTypeNormaliserFake("numberNormaliser");
    private final TypeNormaliser doubleNormaliser = createTypeNormaliserFake("doubleNormaliser");

    @Before
    public void init() {
        TypeNormaliserFactory.setNormaliser(Object.class, objectNormaliser);
        TypeNormaliserFactory.setNormaliser(Number.class, numberNormaliser);
    }

    @Test
    public void normaliserLookupReturnsClosestParent() {
        TypeNormaliser normaliser = TypeNormaliserFactory.getNormaliser(Double.class);
        assertEquals(numberNormaliser, normaliser);
    }

}
