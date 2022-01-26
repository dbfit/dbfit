package dbfit.environment;

import org.junit.Test;
import org.postgresql.util.PGobject;

import static org.junit.Assert.*;

public class PGobjectParseDelegateTest {
    @Test
    public void parse() throws Exception {
        Object actual = new PGobjectParseDelegate().parse("{\"attribute\":\"value\"");
        assertTrue(actual instanceof PGobject);
        assertEquals("{\"attribute\":\"value\"", ((PGobject) actual).getValue());
        assertEquals("JSONB", ((PGobject) actual).getType());
        assertNull(new PGobjectParseDelegate().parse(null));
    }
}
