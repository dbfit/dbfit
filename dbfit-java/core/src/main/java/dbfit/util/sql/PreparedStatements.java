package dbfit.util.sql;

import static dbfit.util.LangUtils.join;
import static dbfit.util.LangUtils.repeat;

public class PreparedStatements {
    private static String buildParamList(int numberOfParameters) {
        return join(repeat("?", numberOfParameters), ", ");
    }

    public static String buildStoredRoutineCallCmdText(
            String routineName, int numberOfPararameters, boolean paramsIncludeReturnValue) {
        return "{ " + (paramsIncludeReturnValue ? "? = " : "") + "call " + routineName + "(" +
            buildParamList((paramsIncludeReturnValue ? numberOfPararameters - 1 : numberOfPararameters)) + ") }";
    }
}
