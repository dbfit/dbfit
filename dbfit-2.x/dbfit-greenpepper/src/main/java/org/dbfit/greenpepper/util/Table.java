package org.dbfit.greenpepper.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.dbfit.core.DBEnvironment;
import dbfit.util.DbParameterAccessor;

public class Table{

	private DBEnvironment dbEnvironment;
	private String tableOrViewName;
	private Map<String, DbParameterAccessor> allParams;
	public Table (DBEnvironment dbEnvironment, String tableName) throws SQLException{
		this.dbEnvironment=dbEnvironment;
		this.tableOrViewName=tableName;		
		allParams=	dbEnvironment.getAllColumns(tableName);
		if (allParams.isEmpty()){
			throw new SQLException("Cannot retrieve list of columns for "+tableName+" - check spelling and access rights");
		}
	}
	public Map<String,DbParameterAccessor> getAllAccessors(){
		return allParams;
	}
	public String getTableName() {
		return tableOrViewName;
	}
	public DBEnvironment getDbEnvironment() {
		return dbEnvironment;
	}

}
