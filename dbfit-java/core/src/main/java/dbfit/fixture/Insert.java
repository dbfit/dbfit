package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbObject;
import dbfit.api.DbTable;
import fit.Fixture;
import fit.Parse;

import java.sql.SQLException;

public class Insert extends Fixture {
    private DBEnvironment environment;
    private String tableName;

    public Insert() {
        environment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public Insert(DBEnvironment dbEnvironment) {
        this.environment = dbEnvironment;
    }

    public Insert(DBEnvironment dbEnvironment, String tableName) {
        this.tableName = tableName;
        this.environment = dbEnvironment;
    }

    public void doRows(Parse rows) {
        try {
            new InsertTable(environment, getTableName(), this, rows).run();
        } catch (Throwable e) {
            e.printStackTrace();
            if (rows == null) throw new Error(e);
            exception(rows.parts, e);
        }
    }

    public static class InsertTable extends ExecutionTable {
        private DBEnvironment environment;
        private String tableName;

        public InsertTable(DBEnvironment environment, String tableName, Fixture fixture, Parse rows) {
            super(fixture, rows);

            this.environment = environment;
            this.tableName = tableName;
        }

        @Override
        protected DbObject getTargetDbObject() throws SQLException {
            return new DbTable(environment, tableName);
        }
    }

    protected String getTableName() {
        if ((tableName == null || tableName.trim().length() == 0) && args.length > 0) {
            tableName = args[0];
        };
        return tableName;
    }
}
