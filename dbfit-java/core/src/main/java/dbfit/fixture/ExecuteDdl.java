package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DdlStatement;

import fit.Fixture;
import fit.Parse;

public class ExecuteDdl extends Fixture {
    private String statementText;
    private DBEnvironment dbEnvironment;

    public ExecuteDdl() {
        dbEnvironment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public ExecuteDdl(DBEnvironment env, String statement) {
        this.statementText = statement;
        this.dbEnvironment = env;
    }

    public void doRows(Parse rows) {
        try (DdlStatement ddl = createDdlExecution()) {
            ddl.run();
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

    private DdlStatement createDdlExecution() throws Exception {
        return dbEnvironment.createDdlStatement(getStatementText());
    }
}
