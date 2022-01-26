package dbfit.util;

import org.junit.*;
import static org.junit.Assert.assertEquals;

import static java.util.Arrays.asList;

public class DbParameterAccessorsTest {
    private DbParameterAccessors accessors;

    @Before
    public void prepare() {
        accessors = new DbParameterAccessors(new DbParameterAccessor[] {
            createDummyAccessor(2),
            createDummyAccessor(3),
            createDummyAccessor(1),
            createDummyAccessor(2),
            createDummyAccessor(1)
        });
    }

    @Test
    public void canGetDistinctNamesSortedByPosition() {
        assertEquals(
                asList("dummy1", "dummy2", "dummy3"),
                accessors.getSortedAccessorNames());
    }

    private DbParameterAccessor createDummyAccessor(int position) {
        int sqlType = java.sql.Types.VARCHAR;
        String inputValue = "The input value";
        Class<?> javaType = String.class;
        String userDefinedTypeName = "whatever";
        TypeTransformerFactory inputTransformerFactory = null;

        return new DbParameterAccessor("dummy" + position,
            Direction.INPUT_OUTPUT,
            sqlType, userDefinedTypeName, javaType, position,
            inputTransformerFactory);
    }
}
