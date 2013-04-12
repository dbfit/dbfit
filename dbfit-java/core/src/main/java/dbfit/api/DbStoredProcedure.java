package dbfit.api;

import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessorUtils;
import dbfit.util.NameNormaliser;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class DbStoredProcedure implements DbObject {
    private DBEnvironment environment;
    private String storedProcName;
    private Map<String, DbParameterAccessor> allParams;
    private DbParameterAccessorUtils accessorUtils =
                                DbParameterAccessorUtils.newInstance();
    
    public DbStoredProcedure(DBEnvironment environment, String storedProcName) {
        this.environment = environment;
        this.storedProcName = storedProcName;
    }
    
    private List<String> getSortedAccessorNames(DbParameterAccessor[] accessors) {
        return accessorUtils.getSortedAccessorNames(accessors);
    }

    private boolean containsReturnValue(DbParameterAccessor[] accessors) {
        return accessorUtils.containsReturnValue(accessors);
    }

    private CallableStatement buildCommand(String procName,
            DbParameterAccessor[] accessors) throws SQLException {
        List<String> accessorNames = getSortedAccessorNames(accessors);
        boolean isFunction = containsReturnValue(accessors);
        String callString = buildPreparedStatementString(procName, isFunction, accessorNames.size());

        CallableStatement cs = environment.getConnection().prepareCall(callString);
        for (DbParameterAccessor ac : accessors) {
            int realindex = accessorNames.indexOf(ac.getName());
            ac.bindTo(cs, realindex + 1); // jdbc params are 1-based
            if (ac.getDirection() == DbParameterAccessor.RETURN_VALUE) {
                ac.bindTo(cs, Math.abs(ac.getPosition()));
            }
        }
        return cs;
    }

    String buildPreparedStatementString(String procName, boolean isFunction, int numberOfAccessors) {
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

    public PreparedStatement buildPreparedStatement(
            DbParameterAccessor[] accessors) throws SQLException {
        return buildCommand(storedProcName, accessors);
    }

    public DbParameterAccessor getDbParameterAccessor(String name,
            int expectedDirection) throws SQLException{
    
        if (allParams==null){
            allParams = environment.getAllProcedureParameters(storedProcName);
            if (allParams.isEmpty()) {
                throw new SQLException("Cannot retrieve list of parameters for "
                        + storedProcName + " - check spelling and access rights");
            }
        }
        String paramName = NameNormaliser.normaliseName(name);
        DbParameterAccessor accessor = allParams.get(paramName);
        if (accessor == null)
            throw new SQLException("Cannot find parameter \"" + paramName + "\"");
        if (accessor.getDirection() == DbParameterAccessor.INPUT_OUTPUT) {
            // clone, separate into input and output
            accessor = new DbParameterAccessor(accessor);
            accessor.setDirection(expectedDirection);
        }
        // sql server quirk. if output parameter is used in an input column,
        // then the param should be cloned and remapped to IN/OUT
        if (expectedDirection!=DbParameterAccessor.OUTPUT && 
                accessor.getDirection() == DbParameterAccessor.OUTPUT) {
            accessor = new DbParameterAccessor(accessor);
            accessor.setDirection(DbParameterAccessor.INPUT);
        }
        return accessor;
    }

    public DBEnvironment getDbEnvironment() {
        return environment;
    }

}

