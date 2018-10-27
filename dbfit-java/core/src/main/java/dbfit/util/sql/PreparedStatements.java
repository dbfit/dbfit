package dbfit.util.sql;

import static dbfit.util.LangUtils.join;
import static dbfit.util.LangUtils.repeat;

public class PreparedStatements {
    private static String buildParamList(int numberOfParameters) {
        return join(repeat("?", numberOfParameters), ", ");
    }

    public static String buildStoredRoutineCallText(
            String name, int numberOfParams, boolean hasReturnValueParam, boolean execFuncAsQuery,
            boolean routineIsFunction) {
        String sql = "";
        int numArgs = numberOfParams - (hasReturnValueParam ? 1 : 0);
        String voidInvocation = name + "(" + buildParamList(numArgs) + ")";
        if (routineIsFunction && execFuncAsQuery) {
            sql += "SELECT " + voidInvocation;
        } else {
            sql += "{ ";
            if (hasReturnValueParam) {
                sql += "? = ";
            }
            sql += "call " + voidInvocation + " }";
        }
System.out.println("PreparedStatements: buildStoredRoutineCallText: returning: " + sql);
        return sql;
    }
}
