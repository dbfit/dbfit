package dbfit.util;

import java.util.List;

import static dbfit.util.sql.PreparedStatements.buildFunctionCall;
import static dbfit.util.sql.PreparedStatements.buildStoredProcedureCall;

public class DbStoredProcedureCommandHelper {

    public String buildPreparedStatementString(String procName,
            DbParameterAccessor[] accessors) {
        List<String> accessorNames = new DbParameterAccessors(accessors).getSortedAccessorNames();
        boolean isFunction = new DbParameterAccessors(accessors).containsReturnValue();

        int numberOfAccessors = accessorNames.size();
        int numberOfInputParameters = isFunction ? numberOfAccessors - 1 : numberOfAccessors;
        if (isFunction) {
            return buildFunctionCall(procName, numberOfInputParameters);
        } else {
            return buildStoredProcedureCall(procName, numberOfInputParameters);
        }
    }

}

