package dbfit.util.sql;

import static dbfit.util.LangUtils.join;
import static dbfit.util.LangUtils.repeat;

public class PreparedStatements {
    private static String buildParamList(int numberOfParameters) {
        return join(repeat("?", numberOfParameters), ", ");
    }

    public static String buildStoredRoutineCallText(
            String name, int numberOfParams, boolean hasReturnValueParam) {
        int numArgs = numberOfParams - (hasReturnValueParam ? 1 : 0);
        String resultAssignment = hasReturnValueParam ? "? = " : "";
        String voidInvocation = name + "(" + buildParamList(numArgs) + ")";
        return "{ " + resultAssignment + "call " + voidInvocation + " }";
    }

    public static String buildFunctionCall(String procName, int numberOfParameters) {
        return "{ ? = call " + procName + "(" + buildParamList(numberOfParameters - 1) + ")}";
    }
}
