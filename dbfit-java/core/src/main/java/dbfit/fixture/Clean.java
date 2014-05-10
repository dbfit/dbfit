package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.FitNesseTestHost;
import fit.Parse;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Clean extends fit.ColumnFixture {
    private DeleteData deleteData;
    private boolean hadRowOperation = false;

    public String table;
    public String columnName;
    public BigDecimal[] ids;
    public String[] keys;
    public String where = null;

    public Clean(DBEnvironment environment) {
        this.deleteData = new DeleteData(environment);
    }

    public Clean() {
        this.deleteData = new DeleteData(DbEnvironmentFactory.getDefaultEnvironment());
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

    public static class DeleteData {
        private DBEnvironment environment;

        public DeleteData(DBEnvironment environment) {
            this.environment = environment;
        }

        public void deleteTable(String tableName, String whereClause) throws SQLException {
            SQLQuery q = new DeleteQuery(tableName, whereClause);
            executeQuery(q);
        }

        public void deleteTableByKeys(String tableName, String whereClause, String columnName, Object[] keys) throws SQLException {
            SQLQuery q = new DeleteByInclusionQuery(tableName, whereClause, columnName, keys, true);
            executeQuery(q);
        }

        public void deleteTableByIds(String tableName, String whereClause, String columnName, Object[] ids) throws SQLException {
            SQLQuery q = new DeleteByInclusionQuery(tableName, whereClause, columnName, ids, false);
            executeQuery(q);
        }

        private void executeQuery(SQLQuery q) throws SQLException {
            try (PreparedStatement st =
                    environment.createStatementWithBoundFixtureSymbols(
                        FitNesseTestHost.getInstance(), q.toString())) {
                st.execute();
            }
        }
    }

    public static class SQLQuery {
        private String statement;
        protected List<String> whereClauses = new ArrayList<String>();

        public SQLQuery(String statement, String... whereClauses) {
            this.statement = statement;
            for (String whereClause : whereClauses) {
                if (whereClause != null) {
                    this.whereClauses.add(whereClause);
                }
            }
        }

        protected static String whereColumnIn(String columnName, Object[] values, boolean quoted) {
            return columnName + " in (" + joinedWithCommas(values, quoted) + ")";
        }

        protected static String joinedWithCommas(Object[] ids, boolean quoted) {
            StringBuilder sb = new StringBuilder();
            String comma = "";
            for (Object x : ids) {
                sb.append(comma);
                if (quoted) sb.append("'");
                sb.append(x.toString());
                if (quoted) sb.append("'");
                comma = ", ";
            }
            return sb.toString();
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            s.append(statement);
            if (!whereClauses.isEmpty()) {
                s.append(" where ");
                s.append(whereClauses.get(0));
                for (String whereClause : whereClauses.subList(1, whereClauses.size())) {
                    s.append(" and ");
                    s.append(whereClause);
                }
            }
            return s.toString();
        }
    }

    public static class DeleteQuery extends SQLQuery {
        public DeleteQuery(String tableName, String... whereClauses) {
            super("delete from " + tableName, whereClauses);
        }
    }

    public static class DeleteByInclusionQuery extends DeleteQuery {
        public DeleteByInclusionQuery(String tableName, String whereClause, String column, Object[] values, boolean quoted) {
            super(tableName, whereClause, whereColumnIn(column, values, quoted));
        }
    }
}
