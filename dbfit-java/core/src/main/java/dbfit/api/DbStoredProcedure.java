package dbfit.api;

import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessors;
import dbfit.util.DbStoredProcedureCommandHelper;
import dbfit.util.NameNormaliser;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class DbStoredProcedure implements DbObject {
    private DBEnvironment environment;
    private String storedProcName;
    private Map<String, DbParameterAccessor> allParams;
    
    public DbStoredProcedure(DBEnvironment environment, String storedProcName) {
        this.environment = environment;
        this.storedProcName = storedProcName;
    }

    public PreparedStatement buildPreparedStatement(
            DbParameterAccessor[] accessors) throws SQLException {
        DbStoredProcedureCommandHelper spHelper = ((AbstractDbEnvironment) environment).getDbStoredProcedureCommandHelper();

        String callString = spHelper.buildPreparedStatementString(storedProcName, accessors);
        CallableStatement cs = environment.getConnection().prepareCall(callString);
        new DbParameterAccessors(accessors).bindParameters(cs);

        return cs;
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

