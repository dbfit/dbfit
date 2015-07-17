package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;

import fit.Fixture;
import fit.Parse;

public class Rollback extends Fixture {
    private DBEnvironment dbEnvironment;

    public Rollback() {
        this(DbEnvironmentFactory.getDefaultEnvironment());
    }

    public Rollback(DBEnvironment env) {
        this.dbEnvironment = env;
    }

    @Override
    public void doTable(Parse table) {
        try {
            dbEnvironment.rollback();
        } catch (Throwable e) {
            throw new Error(e);
        }
    }
}
