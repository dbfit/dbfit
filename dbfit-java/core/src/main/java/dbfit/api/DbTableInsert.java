package dbfit.api;

import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;

import java.sql.SQLException;

public class DbTableInsert implements DbObject {

    private final DbTable dbTable;

    public DbTableInsert(DBEnvironment dbEnvironment, String tableName)
            throws SQLException {
        this.dbTable = new DbTable(dbEnvironment, tableName);
    }

    @Override
    public DbCommand buildDbCommand(DbParameterAccessor[] accessors)
            throws SQLException {
        return dbTable.buildInsertCommand(accessors);
    }

    @Override
    public DbParameterAccessor getDbParameterAccessor(String columnName,
            Direction expectedDirection) {
        return dbTable.getDbParameterAccessor(columnName, expectedDirection);
    }
}
