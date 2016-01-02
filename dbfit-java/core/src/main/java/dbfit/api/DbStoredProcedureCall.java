package dbfit.api;

import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessors;
import dbfit.util.PreparedDbStatement;
import static dbfit.util.sql.PreparedStatements.buildFunctionCall;
import static dbfit.util.sql.PreparedStatements.buildStoredProcedureCall;

import java.sql.SQLException;

public class DbStoredProcedureCall {
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

    void bindParametersTo(PreparedDbStatement cs) throws SQLException {
        getAccessors().bindParameters(cs);
    }

    public PreparedDbStatement toPreparedDbStatement() throws SQLException {
        PreparedDbStatement statement =
            environment.createCallableStatement(toSqlString(), isFunction());
        bindParametersTo(statement);
        return statement;
    }
}
