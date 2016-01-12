package dbfit.environment;

import dbfit.api.DBEnvironment;
import dbfit.api.DbCommand;
import dbfit.api.PreparedDbCommand;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessors;
import static dbfit.util.sql.PreparedStatements.buildFunctionCall;
import static dbfit.util.sql.PreparedStatements.buildStoredProcedureCall;

import java.sql.SQLException;

class DbStoredProcedureCall {
    private final DBEnvironment environment;
    private final String name;
    private final DbParameterAccessors accessors;

    public DbStoredProcedureCall(DBEnvironment environment, String name, DbParameterAccessor[] accessors) {
        this.environment = environment;
        this.name = name;
        this.accessors = new DbParameterAccessors(accessors);
    }

    public String getName() {
        return name;
    }

    protected DbParameterAccessors getAccessors() {
        return accessors;
    }

    public boolean isFunction() {
        return getAccessors().containsReturnValue();
    }

    private int getNumberOfParameters() {
        return getAccessors().getNumberOfParameters();
    }

    public String toSqlString() {
        if (isFunction()) {
            return buildFunctionCall(getName(), getNumberOfParameters());
        } else {
            return buildStoredProcedureCall(getName(), getNumberOfParameters());
        }
    }

    void bindParametersTo(PreparedDbCommand statement) throws SQLException {
        getAccessors().bindParameters(statement);
    }

    public DbCommand buildCallCommand() throws SQLException {
        PreparedDbCommand statement =
            environment.createCallCommand(toSqlString(), isFunction());
        bindParametersTo(statement);
        return statement;
    }
}
