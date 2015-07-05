package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;

import fit.Fixture;
import fit.Parse;

public class Commit extends Fixture {
    private DBEnvironment dbEnvironment;

    public Commit() {
        this(DbEnvironmentFactory.getDefaultEnvironment());
    }

    public Commit(DBEnvironment env) {
        this.dbEnvironment = env;
    }

    @Override
    public void doTable(Parse table) {
        try {
            dbEnvironment.commit();
        } catch (Throwable e) {
            throw new Error(e);
        }
    }
}
