package dbfit.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SqlDateNormaliserTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldReturnNullIfGivenNull() throws SQLException {
        assertNull(new SqlDateNormaliser().normalise(null));
    }

    @Test
    public void shouldThrowCorrectExceptionIfNotGivenADate() throws SQLException {
        expectedEx.expect(UnsupportedOperationException.class);
        expectedEx.expectMessage("SqlDateNormaliser cannot work with class java.lang.String");
        new SqlDateNormaliser().normalise("Any Old Object");
    }

    @Test
    public void shouldReturnContentsOfDateIfAllOkay() throws SQLException {
        Date dt = mock(Date.class);
        when(dt.getTime()).thenReturn(103340l);
        assertEquals(new Timestamp(103340l), new SqlDateNormaliser().normalise(dt));
    }

}
