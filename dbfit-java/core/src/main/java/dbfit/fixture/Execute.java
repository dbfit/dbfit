package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbStatement;
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
        try {
            DbStatement dbObject = new DbStatement(dbEnvironment, getStatementText(), FitNesseTestHost.getInstance());
            try (StatementExecution preparedStatement = dbObject.buildPreparedStatement()) {
                preparedStatement.run();
            }
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
