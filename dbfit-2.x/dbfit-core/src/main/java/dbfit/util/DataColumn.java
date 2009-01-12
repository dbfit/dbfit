package dbfit.util;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * minimal meta-data about a result-set column. 
 * @see DataTable
 */
public class DataColumn {
	private String name;
	private String javaClassName;
	private String dbTypeName;
	public DataColumn(String name, String javaClassName, String dbTypeName) {
		this.name = name;
		this.javaClassName = javaClassName;
		this.dbTypeName = dbTypeName;
	}
	public DataColumn(ResultSetMetaData r, int columnIndex) throws SQLException{
		this.name=r.getColumnName(columnIndex);
		this.javaClassName=r.getColumnClassName(columnIndex);
		this.dbTypeName=r.getColumnTypeName(columnIndex);
	}
	public String getDbTypeName() {
		return dbTypeName;
	}
	public String getJavaClassName() {
		return javaClassName;
	}
	public String getName() {
		return name;
	}
}
