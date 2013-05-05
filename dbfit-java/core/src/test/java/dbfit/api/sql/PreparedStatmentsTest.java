package dbfit.api.sql;

import org.junit.Test;

import static dbfit.util.sql.PreparedStatements.buildFunctionCall;
import static dbfit.util.sql.PreparedStatements.buildStoredProcedureCall;
import static org.junit.Assert.assertEquals;

public class PreparedStatmentsTest {
    @Test public void functionWithoutParameters() {
        assertEquals("{ ? =call func()}", buildFunctionCall("func", 0));
    }

    @Test public void functionWithParameters() {
        assertEquals("{ ? =call func(?,?)}", buildFunctionCall("func", 2));
    }

    @Test public void procedureWithoutParameters() {
        assertEquals("{ call storedProc()}", buildStoredProcedureCall("storedProc", 0));
    }

    @Test public void procedureWithParameters() {
        assertEquals("{ call storedProc(?,?)}", buildStoredProcedureCall("storedProc", 2));
    }
}

