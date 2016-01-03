package dbfit.api;

import dbfit.fixture.StatementExecution;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessors;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import static dbfit.util.sql.PreparedStatements.buildFunctionCall;
import static dbfit.util.sql.PreparedStatements.buildStoredProcedureCall;

public class DbStoredProcedureCall {
    private DBEnvironment environment;
    private String name;
    private DbParameterAccessor[] accessors;

    public DbStoredProcedureCall(DBEnvironment environment, String name, DbParameterAccessor[] accessors) {
        this.environment = environment;
        this.name = name;
        this.accessors = accessors;
    }
    public String getName() {
        return name;
    }

    public DbParameterAccessor[] getAccessors() {
        return accessors;
    }

    public boolean isFunction() {
        return new DbParameterAccessors(accessors).containsReturnValue();
    }

    private int getNumberOfParameters() {
        return new DbParameterAccessors(getAccessors()).getNumberOfParameters();
    }

    public String toSqlString() {
        if (isFunction()) {
            return buildFunctionCall(getName(), getNumberOfParameters());
        } else {
            return buildStoredProcedureCall(getName(), getNumberOfParameters());
        }
    }

    void bindParametersTo(StatementExecution cs) throws SQLException {
        new DbParameterAccessors(getAccessors()).bindParameters(cs);
    }

    public StatementExecution toStatementExecution() throws SQLException {
        String sql = toSqlString();
        PreparedStatement ps = environment.getConnection().prepareCall(sql);
        StatementExecution cs;
        if (isFunction()) {
            cs = environment.createFunctionStatementExecution(ps);
        } else {
            cs = environment.createStatementExecution(ps);
        }
        bindParametersTo(cs);
        return cs;
    }
}
