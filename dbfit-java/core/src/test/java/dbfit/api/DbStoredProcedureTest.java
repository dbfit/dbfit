package dbfit.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DbStoredProcedureTest {
    @Test
    public void preparedStatementStringForFunctionWithoutParameters() {
        assertEquals("{ ? =call func()}", new DbStoredProcedure(null, "").buildPreparedStatementString("func", true, 0));
    }

    @Test
    public void preparedStatementStringForFunctionWithParameters() {
        assertEquals("{ ? =call func(?,?)}", new DbStoredProcedure(null, "").buildPreparedStatementString("func", true, 3));
    }

    @Test
    public void preparedStatementStringForProcedureWithoutParameters() {
        assertEquals("{ call storedProc()}", new DbStoredProcedure(null, "").buildPreparedStatementString("storedProc", false, 0));
    }

    @Test
    public void preparedStatementStringForProcedureWithParameters() {
        assertEquals("{ call storedProc(?,?)}", new DbStoredProcedure(null, "").buildPreparedStatementString("storedProc", false, 2));
    }
}