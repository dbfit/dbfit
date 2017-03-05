package dbfit.util.sql;

import org.junit.Test;

import static dbfit.util.sql.PreparedStatements.storedRoutineCall;
import static org.junit.Assert.assertEquals;

public class PreparedStatementsTest {
    @Test public void functionWithoutParameters() {
        assertEquals("{ ? = call func() }", storedRoutineCall("func", 1, true));
    }

    @Test public void functionWithParameters() {
        assertEquals("{ ? = call func(?, ?) }", storedRoutineCall("func", 3, true));
    }

    @Test public void procedureWithoutParameters() {
        assertEquals("{ call storedProc() }", storedRoutineCall("storedProc", 0, false));
    }

    @Test public void procedureWithParameters() {
        assertEquals("{ call storedProc(?, ?) }", storedRoutineCall("storedProc", 2, false));
    }
}
