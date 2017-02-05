package dbfit.api.vendor;

import dbfit.api.DBEnvironment;
import dbfit.fixture.StatementExecution;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessors;
import static dbfit.util.sql.PreparedStatements.buildStoredRoutineCallCmdText;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DefaultDbStoredProcedureCall implements DbStoredProcedureCall {
    private final DBEnvironment environment;
    private final String name;
    private final DbParameterAccessors accessors;

    public DefaultDbStoredProcedureCall(DBEnvironment environment, String name, DbParameterAccessor[] accessors) {
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

    private boolean hasReturnValue() {
        return getAccessors().containsReturnValue();
    }

    private int getNumberOfParameters() {
        return getAccessors().getNumberOfParameters();
    }

    protected String toSqlString() {
        return buildStoredRoutineCallText(getName(), getNumberOfParameters(), hasReturnValue());
    }

    void bindParametersTo(StatementExecution cs) throws SQLException {
        getAccessors().bindParameters(cs);
    }

    @Override
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
