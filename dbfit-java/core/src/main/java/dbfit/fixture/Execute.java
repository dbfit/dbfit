package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFacade;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.FitNesseTestHost;

import fit.Fixture;
import fit.Parse;

public class Execute extends Fixture {
    private String statementText;
    private DbEnvironmentFacade dbEnvironment;

    public Execute() {
        this(DbEnvironmentFactory.getDefaultEnvironment(), null);
    }

    public Execute(DBEnvironment env, String statement) {
        this.statementText = statement;
        this.dbEnvironment =
            new DbEnvironmentFacade(env, FitNesseTestHost.getInstance());
    }

    public void doRows(Parse rows) {
        try {
            dbEnvironment.runCommandWithBoundSymbols(getStatementText());
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
