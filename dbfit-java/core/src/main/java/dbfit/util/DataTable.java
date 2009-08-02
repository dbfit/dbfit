package dbfit.util;

import java.sql.*;
import java.util.*;

/** 
 * vendor-invariant detached rowset implementation. Because oracle-specific extensions effectively
 * prevent us from using a generic cached result set, this class plays that role instead. It is also responsible
 * for efficient data row matching and tracking processed/unprocessed rows.
 */
public class DataTable{
	private List <DataRow> rows=new LinkedList<DataRow>();
	private List <DataColumn> columns =new LinkedList<DataColumn>();
	public DataTable(ResultSet rs) throws SQLException{
		ResultSetMetaData rsmd=rs.getMetaData();
		for(int i=1; i<=rsmd.getColumnCount(); i++){
			columns.add(new DataColumn(rsmd,i));
		}
		while (rs.next()){
			rows.add(new DataRow(rs,rsmd));
		}
		rs.close();
	}
	public DataRow findMatching (Map<String,Object> keyProperties) throws NoMatchingRowFoundException{
		for (DataRow dr: rows)
			if (!dr.isProcessed() && dr.matches(keyProperties)) return dr;
		throw new NoMatchingRowFoundException();
	}
	public DataRow findFirstUnprocessedRow() throws NoMatchingRowFoundException{
		for (DataRow dr: rows)
			if (!dr.isProcessed()) return dr;
		throw new NoMatchingRowFoundException();
	}
	public List<DataRow> getUnprocessedRows(){
		List<DataRow> l=new ArrayList<DataRow>();
		for (DataRow dr: rows)
			if (!dr.isProcessed()) l.add(dr);
		return l;
	}
	public List<DataColumn> getColumns() {
		return columns;
	}
}
