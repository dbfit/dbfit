package dbfit.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static dbfit.util.LangUtils.join;
import static dbfit.util.LangUtils.repeat;

public class DbStoredProcedureCommandHelper {

    public String buildPreparedStatementString(String procName, boolean isFunction, int numberOfAccessors) {
        StringBuilder ins = new StringBuilder("{ ");
        if (isFunction) {
            ins.append("? =");
        }
        ins.append("call ").append(procName);
        ins.append("(");
        ins.append(join(repeat("?", (isFunction ? numberOfAccessors - 1 : numberOfAccessors)), ","));
        ins.append(")");
        ins.append("}");
        return ins.toString();
    }

    public String buildPreparedStatementString(String procName,
            DbParameterAccessor[] accessors) {
        List<String> accessorNames = new DbParameterAccessors(accessors).getSortedAccessorNames();
        boolean isFunction = new DbParameterAccessors(accessors).containsReturnValue();

        return buildPreparedStatementString(procName, isFunction, accessorNames.size());
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

