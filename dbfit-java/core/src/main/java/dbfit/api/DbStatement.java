package dbfit.api;

import dbfit.fixture.StatementExecution;
import dbfit.util.DbParameterAccessor;

import java.sql.SQLException;

import dbfit.util.Direction;

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

    public StatementExecution buildPreparedStatement(DbParameterAccessor[] accessors) throws SQLException {
        return new StatementExecution(environment.createStatementWithBoundFixtureSymbols(testHost, statementText), false);
    }

    public DbParameterAccessor getDbParameterAccessor(String paramName, Direction expectedDirection) {
        return null;
    }

    @Override
    public int getExceptionCode(SQLException e) {
        return environment.getExceptionCode(e);
    }
}
