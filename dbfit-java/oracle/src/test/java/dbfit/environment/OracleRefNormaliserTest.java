package dbfit.environment;

import static org.junit.Assert.assertNull;

import java.sql.SQLException;

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

}
