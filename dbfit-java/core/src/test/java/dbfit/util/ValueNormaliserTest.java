package dbfit.util;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;

public class ValueNormaliserTest {

    @Before
    public void prepare() {
        TypeNormaliserFactory.setNormaliser(String.class, new TypeTransformer() {
            @Override
            public Object transform(Object o) {
                return o.toString().toUpperCase();
            }
        });
    }

    @After
    public void cleanup() {
        TypeNormaliserFactory.setNormaliser(String.class, null);
    }

    @Test
    public void canNormaliseValuesForRegisteredTypes() throws SQLException {
        assertEquals("TEST", ValueNormaliser.normaliseValue("test"));
    }

    @Test
    public void normalisedNullIsNull() throws SQLException {
        assertNull(ValueNormaliser.normaliseValue(null));
    }

    @Test
    public void normaliseValueIsIdentityForUnregisteredTypes() throws SQLException {
        Integer value = Integer.valueOf(13);
        assertEquals(value, ValueNormaliser.normaliseValue(value));
    }
}
