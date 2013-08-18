package dbfit.util;

import dbfit.api.DbObject;

import java.sql.SQLException;
import java.util.List;

import static dbfit.util.Direction.INPUT;
import static dbfit.util.Direction.OUTPUT;

public class HeaderRow {
    private List<String> columnNames;
    private DbObject dbObject;

    public HeaderRow(List<String> columnNames, DbObject dbObject) {
        this.columnNames = columnNames;
        this.dbObject = dbObject;
    }

    public DbParameterAccessors getAccessors() throws SQLException {
        DbParameterAccessors accessors = new DbParameterAccessors();
        for (String name : columnNames) {
            DbParameterAccessor accessor = dbObject.getDbParameterAccessor(name, isOutput(name) ? OUTPUT : INPUT);
            if (accessor == null) throw new IllegalArgumentException("Parameter/column " + name + " not found");
            accessors.add(accessor);
        }
        return accessors;
    }

    private static boolean isOutput(String name) {
        return name.endsWith("?");
    }
}
