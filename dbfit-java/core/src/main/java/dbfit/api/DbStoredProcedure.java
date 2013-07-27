package dbfit.api;

import dbfit.fixture.StatementExecution;
import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.NameNormaliser;

import java.sql.SQLException;
import java.util.Map;

import static dbfit.util.Direction.INPUT_OUTPUT;

public class DbStoredProcedure implements DbObject {
    private DBEnvironment environment;
    private String name;
    private Map<String, DbParameterAccessor> allParams;

    public DbStoredProcedure(DBEnvironment environment, String name) {
        this.environment = environment;
        this.name = name;
    }

    public StatementExecution buildPreparedStatement(
            DbParameterAccessor[] accessors) throws SQLException {
        DbStoredProcedureCall call = environment.newStoredProcedureCall(name, accessors);

        return call.toStatementExecution();
    }

    public DbParameterAccessor getDbParameterAccessor(String name,
                                                      Direction expectedDirection) throws SQLException{
        DbParameterAccessor parameter = findAccessorForParamWithName(name);
        if (parameter.hasDirection(INPUT_OUTPUT)) {
            // clone, separate into input and output
            parameter = parameter.clone();
            parameter.setDirection(expectedDirection);
        }
        // sql server quirk. if output parameter is used in an input column,
        // then the param should be cloned and remapped to IN/OUT
        if (expectedDirection!=Direction.OUTPUT && parameter.hasDirection(Direction.OUTPUT)) {
            parameter = parameter.clone();
            parameter.setDirection(Direction.INPUT);
        }
        return parameter;
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

}
