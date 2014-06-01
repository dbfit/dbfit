package dbfit.api;

import dbfit.fixture.StatementExecution;

import java.sql.SQLException;

public class DbStatement {
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

    public StatementExecution buildPreparedStatement() throws SQLException {
        return new StatementExecution(environment.createStatementWithBoundFixtureSymbols(testHost, statementText), false);
    }
}
