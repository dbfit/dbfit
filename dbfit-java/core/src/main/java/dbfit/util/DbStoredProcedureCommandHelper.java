package dbfit.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    public void bindParameters(PreparedStatement statement,
            DbParameterAccessor[] accessors) throws SQLException {
        List<String> accessorNames = new DbParameterAccessors(accessors).getSortedAccessorNames();
        for (DbParameterAccessor ac : accessors) {
            int realindex = accessorNames.indexOf(ac.getName());
            ac.bindTo(statement, realindex + 1); // jdbc params are 1-based
            if (ac.getDirection() == DbParameterAccessor.RETURN_VALUE) {
                ac.bindTo(statement, Math.abs(ac.getPosition()));
            }
        }
    }

}

