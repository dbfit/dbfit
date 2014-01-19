package dbfit.util;

import java.sql.*;
import java.util.*;

/**
 * Vendor-invariant detached rowset implementation.
 * Because oracle-specific extensions effectively prevent us from using
 * a generic cached result set, this class plays that role instead.
 */
public class DataTable {
    private List <DataRow> rows = new LinkedList<DataRow>();
    private List <DataColumn> columns = new LinkedList<DataColumn>();

    public DataTable(ResultSet rs) throws SQLException {
        try {
            init(rs);
        } finally {
            rs.close();
        }
    }

    public DataTable(List<DataRow> rows, List<DataColumn> columns) {
        this.rows = rows;
        this.columns = columns;
    }

    private void init(final ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();

        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            columns.add(new DataColumn(rsmd,i));
        }

        while (rs.next()) {
            rows.add(new DataRow(rs,rsmd));
        }
    }

    public List<DataColumn> getColumns() {
        return columns;
    }

    public List<DataRow> getRows() {
        return rows;
    }
}

