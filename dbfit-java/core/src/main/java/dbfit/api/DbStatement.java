package dbfit.api;

import dbfit.fixture.StatementExecution;
import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;

import java.sql.SQLException;

public class DbStatement implements DbObject {
    private DBEnvironment environment;
    private String statementText;
    private TestHost testHost;

    public DbStatement() {
        environment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public DbStatement(DBEnvironment environment, String statementText, TestHost testHost) {
        this.environment = environment;
        this.statementText = statementText;
        this.testHost = testHost;
    }

    @Override
    public StatementExecution buildPreparedStatement(DbParameterAccessor[] accessors) throws SQLException {
        return new StatementExecution(environment.createStatementWithBoundFixtureSymbols(testHost, statementText), false);
    }

    @Override
    public DbParameterAccessor getDbParameterAccessor(String paramName, Direction expectedDirection) throws SQLException {
        throw new Error("Argument rows not supported for Execute statements");
    }
}
