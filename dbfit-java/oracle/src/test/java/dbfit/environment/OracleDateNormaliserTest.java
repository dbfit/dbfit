package dbfit.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.sql.Timestamp;

import oracle.sql.DATE;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OracleDateNormaliserTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldReturnNullIfGivenNull() throws SQLException {
        assertNull(new OracleDateNormaliser().normalise(null));
    }

    @Test
    public void shouldThrowCorrectExceptionIfNotGivenAnOracleDATE() throws SQLException {
        expectedEx.expect(UnsupportedOperationException.class);
        expectedEx.expectMessage("OracleDateNormaliser cannot work with class java.lang.String");
        new OracleDateNormaliser().normalise("Any Old Object");
    }

    @Test
    public void shouldReturnContentsOfDateIfAllOkay() throws SQLException {
        DATE dt = mock(DATE.class);
        when(dt.timestampValue()).thenReturn(new Timestamp(0l));
        assertEquals(new Timestamp(0l), new OracleDateNormaliser().normalise(dt));
    }

}
