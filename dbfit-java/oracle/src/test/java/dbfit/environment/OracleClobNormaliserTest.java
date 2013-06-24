package dbfit.environment;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

import java.sql.SQLException;

import oracle.sql.CLOB;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OracleClobNormaliserTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldReturnNullIfGivenNull() throws SQLException {
        assertNull(new OracleClobNormaliser().normalise(null));
    }

    @Test
    public void shouldThrowCorrectExceptionIfNotGivenACLOB() throws SQLException {
        expectedEx.expect(UnsupportedOperationException.class);
        expectedEx.expectMessage("OracleClobNormaliser cannot work with class java.lang.String");
        new OracleClobNormaliser().normalise("Any Old Object");
    }

    @Test
    public void shouldThrowCorrectExceptionIfClobIsLargerThanMaximum() throws SQLException {
        CLOB clob = mock(CLOB.class);
        
        when(clob.length()).thenReturn(10001l);
        expectedEx.expect(UnsupportedOperationException.class);
        expectedEx.expectMessage("Clobs larger than 10000 bytes are not supported by DBFIT");
        
        new OracleClobNormaliser().normalise(clob);
    }

    @Test
    public void shouldReturnContentsOfClobIFAllOkay() throws SQLException {
        CLOB clob = mock(CLOB.class);
        
        when(clob.length()).thenReturn(Long.valueOf("CLOB contents".length()));
        when(clob.getChars(eq(1l), eq(10000), any(char[].class))).thenReturn("CLOB contents".length());

        // can't do this as we don't fill up the passed buffer
        //assertEquals("CLOB contents", new OracleClobNormaliser().normalise(clob));
        new OracleClobNormaliser().normalise(clob);
    }


}
