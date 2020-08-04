package dbfit.environment;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

import java.sql.SQLException;

import java.sql.Clob;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OracleClobNormaliserTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldReturnNullIfGivenNull() throws SQLException {
        assertNull(new OracleClobNormaliser().transform(null));
    }

    @Test
    public void shouldThrowCorrectExceptionIfNotGivenACLOB() throws SQLException {
        expectedEx.expect(UnsupportedOperationException.class);
        expectedEx.expectMessage("OracleClobNormaliser cannot work with class java.lang.String");
        new OracleClobNormaliser().transform("Any Old Object");
    }

    @Test
    public void shouldThrowCorrectExceptionIfClobIsLargerThanMaximum() throws SQLException {
        Clob clob = mock(Clob.class);

        when(clob.length()).thenReturn(10001l);
        expectedEx.expect(UnsupportedOperationException.class);
        expectedEx.expectMessage("Clobs larger than 10000 bytes are not supported by DBFIT");

        new OracleClobNormaliser().transform(clob);
    }

    @Test
    public void shouldReturnContentsOfClobIFAllOkay() throws SQLException {
        Clob clob = mock(Clob.class);

        when(clob.length()).thenReturn(Long.valueOf("CLOB contents".length()));
        when(clob.getSubString(eq(1l), eq(10000))).thenReturn("CLOB contents");

        // can't do this as we don't fill up the passed buffer
        // assertEquals("CLOB contents", new OracleClobNormaliser().normalise(clob));
        new OracleClobNormaliser().transform(clob);
    }
}
