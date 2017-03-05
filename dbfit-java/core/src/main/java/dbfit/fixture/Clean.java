package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DeleteData;
import dbfit.util.FitNesseTestHost;

import fit.Parse;

import java.math.BigDecimal;
import java.sql.SQLException;

public class Clean extends fit.ColumnFixture {
    private DeleteData deleteData;
    private boolean hadRowOperation = false;

    public String table;
    public String columnName;
    public BigDecimal[] ids;
    public String[] keys;
    public String where = null;

    public Clean(DBEnvironment environment) {
        this.deleteData = new DeleteData(environment, FitNesseTestHost.getInstance());
    }

    public Clean() {
        this(DbEnvironmentFactory.getDefaultEnvironment());
    }

    public boolean clean() throws SQLException {
        deleteData.deleteTable(table, where);
        return true;
    }

    public boolean DeleteRowsForIDs() throws SQLException {
        deleteData.deleteTableByIds(table, where, columnName, ids);
        hadRowOperation = true;
        return true;
    }

    public boolean DeleteRowsForKeys() throws SQLException {
        deleteData.deleteTableByKeys(table, where, columnName, keys);
        hadRowOperation = true;
        return true;
    }

    public void doRow(Parse row) {
        hadRowOperation = false;
        super.doRow(row);
        if (!hadRowOperation) {
            try {
                clean();
            } catch (SQLException sqle) {
                exception(row, sqle);
            }
        }
    }
}
