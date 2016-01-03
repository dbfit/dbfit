package dbfit.util.sql;

import static dbfit.util.LangUtils.join;
import static dbfit.util.LangUtils.repeat;

public class PreparedStatements {
    private static String buildParamList(int numberOfParameters) {
        return join(repeat("?", numberOfParameters), ", ");
    }

    public static String buildStoredProcedureCall(String procName, int numberOfPararameters) {
        return "{ call " + procName + "(" + buildParamList(numberOfPararameters) + ")}";
    }

    public static String buildFunctionCall(String procName, int numberOfParameters) {
        return "{ ? = call " + procName + "(" + buildParamList(numberOfParameters - 1) + ")}";
    }
}
