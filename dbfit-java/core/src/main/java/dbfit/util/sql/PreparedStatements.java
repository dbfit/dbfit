package dbfit.util.sql;

import static dbfit.util.LangUtils.join;
import static dbfit.util.LangUtils.repeat;

public class PreparedStatements {
    private static String buildParamList(int numberOfParameters) {
        return join(repeat("?", numberOfParameters), ", ");
    }

    private static String returnValueParam(boolean returnsValue) {
        return returnsValue ? "? = " : "";
    }

    private static String storedRoutineExecution(String routineName, int numberOfParameters) {
        return "call " + routineName + "(" + buildParamList(numberOfParameters) + ")";
    }

    public static String storedRoutineStatement(
            String routineName, int numberOfParams, boolean hasReturnValueParam) {
        int executionParams = numberOfParams - (hasReturnValueParam ? 1 : 0);
        return returnValueParam(hasReturnValueParam) +
            storedRoutineExecution(routineName, executionParams);
    }

    public static String storedRoutineCall(
            String routineName, int numberOfParams, boolean hasReturnValueParam) {
        return "{ " + storedRoutineStatement(routineName, numberOfParams, hasReturnValueParam) + " }";
    }
}
