package dbfit.environment;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import oracle.jdbc.rowset.OracleSerialClob;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OracleSerialClobNormaliserTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldReturnNullIfGivenNull() throws SQLException {
        assertNull(new OracleSerialClobNormaliser().normalise(null));
    }

    @Test
    public void shouldThrowCorrectExceptionIfNotGivenAnOracleSerialClob() throws SQLException {
        expectedEx.expect(UnsupportedOperationException.class);
        expectedEx.expectMessage("OracleSerialClobNormaliser cannot work with class java.lang.String");
        new OracleSerialClobNormaliser().normalise("Any Old Object");
    }

    @Test
    public void shouldThrowCorrectExceptionIfClobIsLargerThanMaximum() throws SQLException {
        OracleSerialClob clob = mock(OracleSerialClob.class);
        
        when(clob.length()).thenReturn(10001l);
        expectedEx.expect(UnsupportedOperationException.class);
        expectedEx.expectMessage("Clobs larger than 10000 bytes are not supported by DBFIT");
        
        new OracleSerialClobNormaliser().normalise(clob);
    }

    @Test
    public void shouldReturnContentsOfClobIFAllOkay() throws SQLException {
        OracleSerialClob clob = mock(OracleSerialClob.class);
        
        when(clob.length()).thenReturn(Long.valueOf("CLOB contents".length()));
        when(clob.getSubString(1l, "CLOB contents".length())).thenReturn("CLOB contents");

        assertEquals("CLOB contents", new OracleSerialClobNormaliser().normalise(clob));
    }

}
