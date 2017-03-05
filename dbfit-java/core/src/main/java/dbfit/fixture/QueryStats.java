package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbEnvironmentFacade;
import dbfit.util.FitNesseTestHost;
import dbfit.util.DataTable;

import java.sql.SQLException;

public class QueryStats extends fit.ColumnFixture {
    private DbEnvironmentFacade environmentFacade;
    private boolean hasExecuted = false;
    private int _rows;

    public String tableName;
    public String where;
    public String query;

    public QueryStats() {
        this(DbEnvironmentFactory.getDefaultEnvironment());
    }

    public QueryStats(DBEnvironment environment) {
        this.environmentFacade = new DbEnvironmentFacade(environment,
                FitNesseTestHost.getInstance());
    }

    public void setViewName(String value) {
        tableName = value;
    }

    public void reset() {
        hasExecuted = false;
        where = null;
        query = null;
        _rows = 0;
        tableName = null;
    }

    private void execQuery() throws SQLException {
        if (hasExecuted) {
            return;
        }

        if (query == null) {
            query = "select * from " + tableName + (where != null ? " where " + where : "");
        }

        String countQuery = "select count(*) from (" + query + ") temp";
        DataTable dt = environmentFacade.getQueryTable(countQuery);
        String columnName = dt.getColumns().get(0).getName();
        _rows = ((Number) dt.getRows().get(0).get(columnName)).intValue();

        hasExecuted = true;
    }

    public int rowCount() throws SQLException {
        execQuery();
        return _rows;
    }

    public boolean isEmpty() throws SQLException {
        return rowCount() == 0;
    }
}
