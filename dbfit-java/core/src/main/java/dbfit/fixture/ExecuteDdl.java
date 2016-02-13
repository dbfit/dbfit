package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbEnvironmentFacade;
import dbfit.util.FitNesseTestHost;

import fit.Fixture;
import fit.Parse;

public class ExecuteDdl extends Fixture {
    private String statementText;
    private DbEnvironmentFacade environmentFacade;

    public ExecuteDdl() {
        this(DbEnvironmentFactory.getDefaultEnvironment(), null);
    }

    public ExecuteDdl(DBEnvironment env, String statement) {
        this.statementText = statement;
        this.environmentFacade =
            new DbEnvironmentFacade(env, FitNesseTestHost.getInstance());
    }

    public void doRows(Parse rows) {
        try {
            environmentFacade.runDdl(getStatementText());
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
