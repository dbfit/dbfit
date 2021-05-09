package dbfit.environment;

import dbfit.util.TypeTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.postgresql.util.PGobject;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class PostgresPGobjectNormaliserTest {

    @Test
    public void transform() throws SQLException {
        PGobject pGobject = new PGobject();
        pGobject.setValue("{\"attribute\":\"value\"");
        pGobject.setType("JSON");
        Object actual = new PostgresPGobjectNormaliser().transform(pGobject);
        assertTrue(actual instanceof String);
        assertEquals("{\"attribute\":\"value\"", actual);
        assertNull(new PostgresPGobjectNormaliser().transform(null));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void transformWithNonPGobject() throws SQLException {
        new PostgresPGobjectNormaliser().transform("string");
    }
}
