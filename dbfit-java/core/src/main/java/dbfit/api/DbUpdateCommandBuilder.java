package dbfit.api;

import dbfit.util.DbParameterAccessor;
import static dbfit.util.Direction.INPUT;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

public class DbUpdateCommandBuilder {

    private DBEnvironment environment;
    private String objectName;
    private Map<String, DbParameterAccessor> columnsMap;
    private List<DbParameterAccessor> updateAccessors;
    private List<DbParameterAccessor> selectAccessors;

    public DbUpdateCommandBuilder() {
    }

    public DbUpdateCommandBuilder(DBEnvironment environment, String objectName) {
        withDbEnvironment(environment);
        withObjectName(objectName);
    }

    public void withDbEnvironment(DBEnvironment environment) {
        this.environment = environment;
        initColumnsMap();
    }

    public void withObjectName(String objectName) {
        this.objectName = objectName;
        updateAccessors = new ArrayList<>();
        selectAccessors = new ArrayList<>();
        initColumnsMap();
    }

    public DbParameterAccessor addUpdateAccessor(String name) {
        return addAccessor(name, updateAccessors);
    }

    public DbParameterAccessor addSelectAccessor(String name) {
        return addAccessor(name, selectAccessors);
    }

    public List<DbParameterAccessor> getUpdateAccessors() {
        return updateAccessors;
    }

    public List<DbParameterAccessor> getSelectAccessors() {
        return selectAccessors;
    }

    public DbCommand build() {
        try {
            return new DbTable(environment, objectName).buildUpdateCommand(
                selectAccessors.toArray(new DbParameterAccessor[0]),
                updateAccessors.toArray(new DbParameterAccessor[0]));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, DbParameterAccessor> getColumnsMap() {
        try {
            if (null == columnsMap) {
                columnsMap = environment.getAllColumns(objectName);
                verifyColumnsExist();
            }

            return columnsMap;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyColumnsExist() {
        if (getColumnsMap().isEmpty()) {
            throw new RuntimeException(
                "Cannot retrieve list of columns for " +
                objectName + " - check spelling and access rights");
        }
    }

    private void initColumnsMap() {
        if (environment != null && objectName != null) {
            getColumnsMap();
        }
    }

    private DbParameterAccessor addAccessor(
            String name, List<DbParameterAccessor> list) {
        DbParameterAccessor acc = getColumnsMap().get(name);
        if (null == acc) {
            throw new IllegalArgumentException("Cannot find column " + name);
        }
        // clone because same column may be in both the update and select part
        acc = acc.clone();
        acc.setDirection(INPUT);
        list.add(acc);
        return acc;
    }
}
