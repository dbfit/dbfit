package dbfit.api;

import dbfit.util.DbParameterAccessor;
import dbfit.util.NameNormaliser;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static dbfit.util.DbParameterAccessor.Direction;
import static dbfit.util.DbParameterAccessor.Direction.*;

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

        return call.toCallableStatement();
    }

    public DbParameterAccessor getDbParameterAccessor(String name,
            Direction expectedDirection) throws SQLException{
        DbParameterAccessor accessor = findAccessorForParamWithName(name);
        if (accessor.getDirection() == INPUT_OUTPUT) {
            // clone, separate into input and output
            accessor = accessor.clone();
            accessor.setDirection(expectedDirection);
        }
        // sql server quirk. if output parameter is used in an input column,
        // then the param should be cloned and remapped to IN/OUT
        if (expectedDirection!=Direction.OUTPUT &&
                accessor.getDirection() == Direction.OUTPUT) {
            accessor = accessor.clone();
            accessor.setDirection(Direction.INPUT);
        }
        return accessor;
    }

    private DbParameterAccessor findAccessorForParamWithName(String name) throws SQLException {
        String paramName = NameNormaliser.normaliseName(name);
        DbParameterAccessor accessor = getAllParams().get(paramName);
        if (accessor == null)
            throw new SQLException("Cannot find parameter \"" + paramName + "\"");
        return accessor;
    }

    private Map<String, DbParameterAccessor> getAllParams() throws SQLException {
        if (allParams==null){
            allParams = environment.getAllProcedureParameters(this.name);
            if (allParams.isEmpty()) {
                throw new SQLException("Cannot retrieve list of parameters for "
                        + this.name + " - check spelling and access rights");
            }
        }
        return allParams;
    }

    public String getName() {
        return name;
    }

    public DBEnvironment getDbEnvironment() {
        return environment;
    }

}

