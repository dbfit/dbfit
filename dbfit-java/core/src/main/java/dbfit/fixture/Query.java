package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dbfit.util.SymbolUtil.isSymbolGetter;

public class Query extends RowSetFixture {
    private DBEnvironment dbEnvironment;
    private String queryOrSymbol;
    private boolean isOrdered;

    public Query() {
        dbEnvironment = DbEnvironmentFactory.getDefaultEnvironment();
        isOrdered = false;
    }

    public Query(DBEnvironment environment, String queryOrSymbol) {
        this(environment, queryOrSymbol, false);
    }

    public Query(DBEnvironment environment, String queryOrSymbol, boolean isOrdered) {
        this.dbEnvironment = environment;
        this.queryOrSymbol = queryOrSymbol;
        this.isOrdered = isOrdered;
    }

    public MatchableDataTable getDataTable() throws SQLException {
        if (queryOrSymbol == null) {
            queryOrSymbol = args[0];
        }

        if (isSymbolGetter(queryOrSymbol)) {
            return new MatchableDataTable(getFromSymbol());
        }

        Log.log("Query: '%s'", queryOrSymbol);
        try (PreparedStatement st =
                dbEnvironment.createStatementWithBoundFixtureSymbols(
                    FitNesseTestHost.getInstance(), queryOrSymbol)) {
            return new MatchableDataTable(new DataTable(st.executeQuery()));
        }
    }

    private DataTable getFromSymbol() throws SQLException {
        Object o = dbfit.util.SymbolUtil.getSymbol(queryOrSymbol);

        if (o instanceof ResultSet) {
            return new DataTable((ResultSet) o);
        } else if (o instanceof DataTable) {
            return (DataTable) o;
        }

        throw new UnsupportedOperationException("Stored queries can only be used on symbols that contain result sets");
    }

    protected boolean isOrdered() {
        return isOrdered;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class getJavaClassForColumn(DataColumn col) throws ClassNotFoundException, SQLException {
        return dbEnvironment.getJavaClass(col.getDbTypeName());
    }
}

