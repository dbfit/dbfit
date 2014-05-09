package dbfit.fixture;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.FitNesseTestHost;

public class QueryStats extends fit.ColumnFixture {
    private DBEnvironment environment;
    private boolean hasExecuted = false;
    private int _rows;

    public String tableName;
    public String where;
    public String query;

    public QueryStats() {
        environment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public QueryStats(DBEnvironment environment) {
        this.environment = environment;
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

        try (PreparedStatement st =
                environment.createStatementWithBoundFixtureSymbols(
                    FitNesseTestHost.getInstance(),
                    "select count(*) from (" + query + ") temp")) {
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                _rows = rs.getInt(1);
            }
        }

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
