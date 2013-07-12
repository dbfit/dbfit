package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbObject;
import dbfit.api.DbTable;

import java.sql.SQLException;

public class Insert extends DbObjectExecutionFixture {
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

    @Override
    protected DbObject getTargetDbObject() throws SQLException {
        if ((tableName == null || tableName.trim().length() == 0) && args.length > 0) {
            tableName = args[0];
        };
        return new DbTable(environment, tableName);
    }
}
