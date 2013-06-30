package dbfit.util;

import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.AbstractCollection;
import java.util.AbstractList;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

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

    private final Class ctop = AbstractCollection.class;
    private final Class cmid = AbstractList.class;
    private final Class clow = ArrayList.class;

    private final TypeNormaliser normaliserTop = createTypeNormaliserFake("normaliser Top");
    private final TypeNormaliser normaliserMid = createTypeNormaliserFake("normaliser Mid");
    private final TypeNormaliser normaliserLow = createTypeNormaliserFake("normaliser Low");

    @Before
    public void init() {
        TypeNormaliserFactory.setNormaliser(ctop, normaliserTop);
        TypeNormaliserFactory.setNormaliser(cmid, normaliserMid);
    }

    @Test
    public void normaliserLookupReturnsClosestParentIfNoExactMatch() {
        TypeNormaliser normaliser = TypeNormaliserFactory.getNormaliser(clow);
        assertEquals(normaliserMid, normaliser);
    }

    @Test
    public void normaliserLookupReturnsExactMatchIfAny() {
        TypeNormaliser normaliser = TypeNormaliserFactory.getNormaliser(cmid);
        assertEquals(normaliserMid, normaliser);
        normaliser = TypeNormaliserFactory.getNormaliser(ctop);
        assertEquals(normaliserTop, normaliser);
    }

    @Test
    public void normaliserLookupReturnsNullWhenNolExactMatchNorParents() {
        TypeNormaliser normaliser = TypeNormaliserFactory.getNormaliser(String.class);
        assertNull(normaliser);
    }

}
