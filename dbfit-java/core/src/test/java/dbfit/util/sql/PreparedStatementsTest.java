package dbfit.util.sql;

import org.junit.Test;

import static dbfit.util.sql.PreparedStatements.buildStoredRoutineCallText;
import static org.junit.Assert.assertEquals;

public class PreparedStatementsTest {
    @Test public void functionWithoutParameters() {
        assertEquals("{ ? = call func() }", buildStoredRoutineCallText("func", 1, true, false, false));
    }

    @Test public void functionWithParameters() {
        assertEquals("{ ? = call func(?, ?) }", buildStoredRoutineCallText("func", 3, true, false, false));
    }

    @Test public void functionExecutedAsQueryWithoutParameters() {
        assertEquals("{ SELECT func() }", buildStoredRoutineCallText("func", 1, true, true, true));
    }

    @Test public void functionExecutedAsQueryWithParameters() {
        assertEquals("{ SELECT func(?, ?) }", buildStoredRoutineCallText("func", 3, true, true, true));
    }

    @Test public void procedureWithoutParameters() {
        assertEquals("{ call storedProc() }", buildStoredRoutineCallText("storedProc", 0, false, false, false));
    }

    @Test public void procedureWithParameters() {
        assertEquals("{ call storedProc(?, ?) }", buildStoredRoutineCallText("storedProc", 2, false, false, false));
    }
}

