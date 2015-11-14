package dbfit.util;

import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.AbstractCollection;
import java.util.AbstractList;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

public class TypeTransformerFactoryTest {
    private static TypeTransformer createTypeTransformerFake(final String tag) {
        return new TypeTransformer() {
            @Override
            public Object transform(Object o) {
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

    private final TypeTransformer normaliserTop = createTypeTransformerFake("normaliser Top");
    private final TypeTransformer normaliserMid = createTypeTransformerFake("normaliser Mid");
    private final TypeTransformer normaliserLow = createTypeTransformerFake("normaliser Low");

    private TypeTransformerFactory ttf = new TypeTransformerFactory(); 

    @Before
    public void init() {
        ttf.setTransformer(ctop, normaliserTop);
        ttf.setTransformer(cmid, normaliserMid);
    }

    @Test
    public void normaliserLookupReturnsClosestParentIfNoExactMatch() {
        TypeTransformer normaliser = ttf.getTransformer(clow);
        assertEquals(normaliserMid, normaliser);
    }

    @Test
    public void normaliserLookupReturnsExactMatchIfAny() {
        TypeTransformer normaliser = ttf.getTransformer(cmid);
        assertEquals(normaliserMid, normaliser);
        normaliser = ttf.getTransformer(ctop);
        assertEquals(normaliserTop, normaliser);
    }

    @Test
    public void normaliserLookupReturnsNullWhenNolExactMatchNorParents() {
        TypeTransformer normaliser = ttf.getTransformer(String.class);
        assertNull(normaliser);
    }

}
