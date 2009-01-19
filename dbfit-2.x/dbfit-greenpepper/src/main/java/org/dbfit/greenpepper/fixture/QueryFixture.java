package org.dbfit.greenpepper.fixture;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.dbfit.core.DBEnvironment;
import org.dbfit.greenpepper.util.GreenPepperTestHost;

import dbfit.util.DataTable;

public class QueryFixture extends DataTableFixture{


	public QueryFixture(DBEnvironment dbEnvironment, String query) throws SQLException{
		
		PreparedStatement st = dbEnvironment.createStatementWithBoundFixtureSymbols(GreenPepperTestHost.getInstance(),query);
		
		this.dataTable=new DataTable(st.executeQuery());
	}
}
