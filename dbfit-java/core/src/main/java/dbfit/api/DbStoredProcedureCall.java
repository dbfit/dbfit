package dbfit.api;

import dbfit.fixture.StatementExecution;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessors;
import static dbfit.util.sql.PreparedStatements.buildStoredRoutineCallText;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbStoredProcedureCall {
    private final DBEnvironment environment;
    private final String name;
    private final DbParameterAccessors accessors;

    public DbStoredProcedureCall(DBEnvironment environment, String name, DbParameterAccessor[] accessors) {
        this.environment = environment;
        this.name = name;
System.out.println("DbStoredProcedureCall(ctor): name: " + name);
        this.accessors = new DbParameterAccessors(accessors);
    }

    public String getName() {
        return name;
    }

    protected DbParameterAccessors getAccessors() {
        return accessors;
    }

    public boolean hasReturnValue() {
        return getAccessors().containsReturnValue();
    }

    private int getNumberOfParameters() {
        return getAccessors().getNumberOfParameters();
    }

    public String toSqlString() {
        return buildStoredRoutineCallText(getName(), getNumberOfParameters(), hasReturnValue());
    }

    void bindParametersTo(StatementExecution cs) throws SQLException {
        getAccessors().bindParameters(cs);
    }

    public StatementExecution toStatementExecution() throws SQLException {
        String sql = toSqlString();
        PreparedStatement ps = environment.getConnection().prepareCall(sql);
        StatementExecution cs;
        if (hasReturnValue()) {
            cs = environment.createFunctionStatementExecution(ps);
        } else {
            cs = environment.createStatementExecution(ps);
        }
        bindParametersTo(cs);
        return cs;
    }
}
