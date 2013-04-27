package dbfit.api;

import dbfit.util.DbParameterAccessor;
import dbfit.util.NameNormaliser;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class DbStoredProcedure implements DbObject {
    private DBEnvironment environment;

    private String name;

    private Map<String, DbParameterAccessor> allParams;
    public DbStoredProcedure(DBEnvironment environment, String name) {
        this.environment = environment;
        this.name = name;
    }

    public PreparedStatement buildPreparedStatement(
            DbParameterAccessor[] accessors) throws SQLException {
        DbStoredProcedureCall call = environment.newStoredProcedureCall(name, accessors);

        return call.toCallableStatement(environment.getConnection());
    }

    public DbParameterAccessor getDbParameterAccessor(String name,
            int expectedDirection) throws SQLException{

        if (allParams==null){
            allParams = environment.getAllProcedureParameters(this.name);
            if (allParams.isEmpty()) {
                throw new SQLException("Cannot retrieve list of parameters for "
                        + this.name + " - check spelling and access rights");
            }
        }
        String paramName = NameNormaliser.normaliseName(name);
        DbParameterAccessor accessor = allParams.get(paramName);
        if (accessor == null)
            throw new SQLException("Cannot find parameter \"" + paramName + "\"");
        if (accessor.getDirection() == DbParameterAccessor.INPUT_OUTPUT) {
            // clone, separate into input and output
            accessor = accessor.clone();
            accessor.setDirection(expectedDirection);
        }
        // sql server quirk. if output parameter is used in an input column,
        // then the param should be cloned and remapped to IN/OUT
        if (expectedDirection!=DbParameterAccessor.OUTPUT &&
                accessor.getDirection() == DbParameterAccessor.OUTPUT) {
            accessor = accessor.clone();
            accessor.setDirection(DbParameterAccessor.INPUT);
        }
        return accessor;
    }

    public String getName() {
        return name;
    }

    public DBEnvironment getDbEnvironment() {
        return environment;
    }

}

