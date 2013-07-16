package dbfit.api;

import dbfit.fixture.StatementExecution;
import dbfit.util.ParameterOrColumn;
import dbfit.util.ParametersOrColumns;

import java.sql.SQLException;
import java.util.List;

import static dbfit.util.sql.PreparedStatements.buildFunctionCall;
import static dbfit.util.sql.PreparedStatements.buildStoredProcedureCall;

public class DbStoredProcedureCall {
    private DBEnvironment environment;
    private String name;
    private ParameterOrColumn[] accessors;

    public DbStoredProcedureCall(DBEnvironment environment, String name, ParameterOrColumn[] accessors) {
        this.environment = environment;
        this.name = name;
        this.accessors = accessors;
    }
    public String getName() {
        return name;
    }

    public ParameterOrColumn[] getAccessors() {
        return accessors;
    }

    public boolean isFunction() {
        return new ParametersOrColumns(accessors).containsReturnValue();
    }

    public int getNumberOfInputParameters() {
        List<String> accessorNames = new ParametersOrColumns(getAccessors()).getSortedNames();
        int numberOfAccessors = accessorNames.size();
        return isFunction() ? numberOfAccessors - 1 : numberOfAccessors;
    }

    public String toSqlString() {
        if (isFunction()) {
            return buildFunctionCall(getName(), getNumberOfInputParameters());
        } else {
            return buildStoredProcedureCall(getName(), getNumberOfInputParameters());
        }
    }

    void bindParametersTo(StatementExecution cs) throws SQLException {
        new ParametersOrColumns(getAccessors()).bindParameters(cs);
    }

    public StatementExecution toStatementExecution() throws SQLException {
        StatementExecution cs = new StatementExecution(this.environment.getConnection().prepareCall(toSqlString()));
        bindParametersTo(cs);
        return cs;
    }
}
