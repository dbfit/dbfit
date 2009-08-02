package dbfit.fixture;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.DataColumn;
import dbfit.util.DataTable;
import dbfit.util.FitNesseTestHost;
import dbfit.util.Log;

public class Query extends RowSetFixture {
	private DBEnvironment dbEnvironment;
	private String query;
	private boolean isOrdered;

	public Query() {
		dbEnvironment = DbEnvironmentFactory.getDefaultEnvironment();
		isOrdered = false;
	}

	public Query(DBEnvironment environment, String query) {
		this(environment, query, false);
	}

	public Query(DBEnvironment environment, String query, boolean isOrdered) {
		this.dbEnvironment = environment;
		this.query = query;
		this.isOrdered = isOrdered;
	}

	public DataTable getDataTable() throws SQLException {
		if (query == null)
			query = args[0];
		if (query.startsWith("<<"))
			return getFromSymbol();
		Log.log("Query: '%s'", query);
		PreparedStatement st = dbEnvironment.createStatementWithBoundFixtureSymbols(FitNesseTestHost.getInstance(),query);
		return new DataTable(st.executeQuery());
	}

	private DataTable getFromSymbol() throws SQLException {
		Object o = dbfit.util.SymbolUtil.getSymbol(query.substring(2).trim());
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
		// System.out.println(col.getName()+":"+col.getJavaClassName());
		return dbEnvironment.getJavaClass(col.getDbTypeName());
	}
}
