package dbfit.api;

import dbfit.fixture.StatementExecution;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessors;
import dbfit.util.Direction;
import dbfit.util.NameNormaliser;
import static dbfit.util.Direction.INPUT_OUTPUT;
import static dbfit.util.sql.PreparedStatements.storedRoutineCall;

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

    public StatementExecution buildPreparedStatement(
            DbParameterAccessor[] accessors) throws SQLException {
        DbParameterAccessors paramAccessors = new DbParameterAccessors(accessors);
        PreparedStatement ps = environment.getConnection().prepareCall(toSqlString(paramAccessors));
        StatementExecution cs;
        if (paramAccessors.containsReturnValue()) {
            cs = environment.createFunctionStatementExecution(ps);
        } else {
            cs = environment.createStatementExecution(ps);
        }
        paramAccessors.bindParameters(cs);
        return cs;
    }

    public DbParameterAccessor getDbParameterAccessor(
            String name, Direction expectedDirection) throws SQLException {
        DbParameterAccessor parameter = findAccessorForParamWithName(name);
        if (parameter.hasDirection(INPUT_OUTPUT)) {
            // clone, separate into input and output
            parameter = parameter.clone();
            parameter.setDirection(expectedDirection);
        }
        return parameter;
    }

    private DbParameterAccessor findAccessorForParamWithName(String name) throws SQLException {
        String paramName = NameNormaliser.normaliseName(name);
        DbParameterAccessor accessor = getAllParams().get(paramName);
        if (accessor == null) {
            throw new SQLException("Cannot find parameter \"" + paramName + "\"");
        }
        return accessor;
    }

    private Map<String, DbParameterAccessor> getAllParams() throws SQLException {
        if (allParams == null) {
            allParams = environment.getAllProcedureParameters(this.name);
            if (allParams.isEmpty()) {
                throw new SQLException("Retrieved empty list of parameters for "
                        + this.name + " - check spelling and access rights");
            }
        }
        return allParams;
    }

    protected String getName() {
        return name;
    }

    protected String toSqlString(DbParameterAccessors accessors) {
        return storedRoutineCall(getName(), accessors.getNumberOfParameters(),
            accessors.containsReturnValue());
    }
}
