package dbfit.api;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class DbStoredProcedureTest {
    @Test
    public void executeFunctionWithoutParameters() {
        assertEquals("{ ? =call func}", new DbStoredProcedure(null, "").buildCallString("func", true, Collections.<String>emptyList()));
    }

    @Test
    public void executeProcedureWithoutParameters() {
        assertEquals("{ call storedProc}", new DbStoredProcedure(null, "").buildCallString("storedProc", false, Collections.<String>emptyList()));
    }
}