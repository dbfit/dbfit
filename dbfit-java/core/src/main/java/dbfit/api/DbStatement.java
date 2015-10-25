package dbfit.api;

import java.sql.SQLException;

import dbfit.fixture.StatementExecution;

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
        return environment.createStatementExecution(environment.createStatementWithBoundFixtureSymbols(testHost, statementText), false);
    }
}
