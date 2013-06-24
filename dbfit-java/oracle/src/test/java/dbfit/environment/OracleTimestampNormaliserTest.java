package dbfit.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.sql.Timestamp;

import oracle.sql.TIMESTAMP;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OracleTimestampNormaliserTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldReturnNullIfGivenNull() throws SQLException {
        assertNull(new OracleTimestampNormaliser().normalise(null));
    }

    @Test
    public void shouldThrowCorrectExceptionIfNotGivenAnOracleTIMESTAMP() throws SQLException {
        expectedEx.expect(UnsupportedOperationException.class);
        expectedEx.expectMessage("OracleTimestampNormaliser cannot work with class java.lang.String");
        new OracleTimestampNormaliser().normalise("Any Old Object");
    }

    @Test
    public void shouldReturnContentsOfClobIFAllOkay() throws SQLException {
        TIMESTAMP ts = mock(TIMESTAMP.class);
        when(ts.timestampValue()).thenReturn(new Timestamp(0l));
        assertEquals(new Timestamp(0l), new OracleTimestampNormaliser().normalise(ts));
    }

}
