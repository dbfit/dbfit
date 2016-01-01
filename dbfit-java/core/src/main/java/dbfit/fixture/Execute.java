package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.FitNesseTestHost;
import fit.Fixture;
import fit.Parse;

public class Execute extends Fixture {
    private String statementText;
    private DBEnvironment dbEnvironment;

    public Execute() {
        dbEnvironment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public Execute(DBEnvironment env, String statement) {
        this.statementText = statement;
        this.dbEnvironment = env;
    }

    public void doRows(Parse rows) {
        try (StatementExecution statement =
                dbEnvironment.createStatementExecutionWithBoundFixtureSymbols(
                    FitNesseTestHost.getInstance(), getStatementText())) {
            statement.run();
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    private String getStatementText() {
        if (statementText == null) {
            statementText = args[0];
        }
        return statementText;
    }
}
