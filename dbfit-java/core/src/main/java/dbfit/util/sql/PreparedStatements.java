package dbfit.util.sql;

import static dbfit.util.LangUtils.join;
import static dbfit.util.LangUtils.repeat;

/**
 * Created with IntelliJ IDEA.
 * User: benilovj
 * Date: 4/25/13
 * Time: 8:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreparedStatements {
    public static String buildStoredProcedureCall(String procName, int numberOfInputParameters) {
        String inputs = join(repeat("?", numberOfInputParameters), ",");
        return "{ call " + procName + "(" + inputs + ")}";
    }

    public static String buildFunctionCall(String procName, int numberOfInputParameters) {
        String inputs = join(repeat("?", numberOfInputParameters), ",");
        return "{ ? =call " + procName + "(" + inputs + ")}";
    }
}
