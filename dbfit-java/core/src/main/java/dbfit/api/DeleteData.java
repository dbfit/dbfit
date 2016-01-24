package dbfit.api;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeleteData {
    private final DBEnvironment environment;
    private final TestHost testHost;

    public DeleteData(DBEnvironment environment, TestHost testHost) {
        this.environment = environment;
        this.testHost = testHost;
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
        try (DbCommand command = environment.createCommandWithBoundSymbols(
                    testHost, q.toString())) {
            command.execute();
        }
    }

    private static class SQLQuery {
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

    private static class DeleteQuery extends SQLQuery {
        public DeleteQuery(String tableName, String... whereClauses) {
            super("delete from " + tableName, whereClauses);
        }
    }

    private static class DeleteByInclusionQuery extends DeleteQuery {
        public DeleteByInclusionQuery(String tableName, String whereClause, String column, Object[] values, boolean quoted) {
            super(tableName, whereClause, whereColumnIn(column, values, quoted));
        }
    }
}
