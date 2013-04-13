package dbfit.api;

import dbfit.util.DbStoredProcedureCommandHelper;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;

public class DbStoredProcedureTest {
    private DbStoredProcedureCommandHelper helper = new DbStoredProcedureCommandHelper();

    public String buildPreparedStatementString(String procName, boolean isFunction, int numberOfAccessors) {
        return helper.buildPreparedStatementString(procName, isFunction, numberOfAccessors);
    }

    @Test
    public void preparedStatementStringForFunctionWithoutParameters() {
        assertEquals("{ ? =call func()}", buildPreparedStatementString("func", true, 0));
    }

    @Test
    public void preparedStatementStringForFunctionWithParameters() {
        assertEquals("{ ? =call func(?,?)}", buildPreparedStatementString("func", true, 3));
    }

    @Test
    public void preparedStatementStringForProcedureWithoutParameters() {
        assertEquals("{ call storedProc()}", buildPreparedStatementString("storedProc", false, 0));
    }

    @Test
    public void preparedStatementStringForProcedureWithParameters() {
        assertEquals("{ call storedProc(?,?)}", buildPreparedStatementString("storedProc", false, 2));
    }
}

