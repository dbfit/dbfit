package dbfit.util.sql;

public class HSQLDBPreparedStatements extends PreparedStatements {

    public static String buildFunctionCall(String procName, int numberOfParameters) {
        return "{ call " + procName + "(" + buildParamList(numberOfParameters - 1) + ")}";
    }
}
