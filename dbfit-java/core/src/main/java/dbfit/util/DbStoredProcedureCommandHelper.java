package dbfit.util;

import dbfit.util.DbParameterAccessorUtils;

import java.util.List;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbStoredProcedureCommandHelper {
    protected DbParameterAccessorUtils accessorUtils = DbParameterAccessorUtils.newInstance();

    public String buildPreparedStatementString(String procName, boolean isFunction, int numberOfAccessors) {
        StringBuilder ins = new StringBuilder("{ ");
        if (isFunction) {
            ins.append("? =");
        }
        ins.append("call ").append(procName);
        ins.append("(");
        for (int i = (isFunction ? 1 : 0); i < numberOfAccessors; i++) {
            ins.append("?");
            if (i < numberOfAccessors - 1)
                ins.append(",");
        }
        ins.append(")");
        ins.append("}");
        return ins.toString();
    }

    public String buildPreparedStatementString(String procName,
            DbParameterAccessor[] accessors) {
        List<String> accessorNames = accessorUtils.getSortedAccessorNames(accessors);
        boolean isFunction = accessorUtils.containsReturnValue(accessors);

        return buildPreparedStatementString(procName, isFunction, accessorNames.size());
    }

    public void bindParameters(PreparedStatement statement,
            DbParameterAccessor[] accessors) throws SQLException {
        List<String> accessorNames = accessorUtils.getSortedAccessorNames(accessors);
        for (DbParameterAccessor ac : accessors) {
            int realindex = accessorNames.indexOf(ac.getName());
            ac.bindTo(statement, realindex + 1); // jdbc params are 1-based
            if (ac.getDirection() == DbParameterAccessor.RETURN_VALUE) {
                ac.bindTo(statement, Math.abs(ac.getPosition()));
            }
        }
    }
}

