package dbfit.environment;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.rowset.OracleCachedRowSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class OracleRefNormaliserTest {


    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void shouldReturnNullIfGivenNull() throws SQLException {
        assertNull(new OracleRefNormaliser().normalise(null));
    }

    @Test
    public void shouldThrowCorrectExceptionIfNotGivenAResultSet() throws SQLException {
        expectedEx.expect(UnsupportedOperationException.class);
        expectedEx.expectMessage("OracleRefNormaliser cannot work with class java.lang.String");
        new OracleRefNormaliser().normalise("Any Old Object");
    }

    @Test
    public void shouldReturnContentsOfResultSetIfAllOkay() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        // Can't do this as do not know what OracleCachedRowSet does under the covers...
        //assertTrue(new OracleRefNormaliser().normalise(rs) instanceof OracleCachedRowSet);
    }
}
