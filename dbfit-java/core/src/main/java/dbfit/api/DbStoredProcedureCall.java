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

    public String toSqlString(boolean routineIsFunction) {
        return buildStoredRoutineCallText(getName(), getNumberOfParameters(), hasReturnValue(),
            environment.executeFunctionAsQuery(), routineIsFunction);
    }

    void bindParametersTo(StatementExecution cs) throws SQLException {
        getAccessors().bindParameters(cs);
    }

    public StatementExecution toStatementExecution() throws SQLException {
System.out.println("DbStoredProcedureCall: toStatementExecution");
        boolean routineIsFunction = environment.routineIsFunction(getName());
        String sql = toSqlString(routineIsFunction);
        PreparedStatement ps = environment.getConnection().prepareCall(sql);
        StatementExecution cs;
        if (hasReturnValue()) {
System.out.println("DbStoredProcedureCall: toStatementExecution: it has a return value");
            cs = environment.createFunctionStatementExecution(ps);
        } else {
System.out.println("DbStoredProcedureCall: toStatementExecution: it has NO return value");
            cs = environment.createStatementExecution(ps);
        }
        bindParametersTo(cs);
        return cs;
    }
}
