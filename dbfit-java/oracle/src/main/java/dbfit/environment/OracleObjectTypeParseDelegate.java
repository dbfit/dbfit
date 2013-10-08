package dbfit.environment;

import java.sql.*;

public class OracleObjectTypeParseDelegate {
    private OracleEnvironment dbEnvironment;

    public OracleObjectTypeParseDelegate(OracleEnvironment environment) {
        this.dbEnvironment = environment;
    }

    private Connection getConnection() throws SQLException {
        return dbEnvironment.getConnection();
    }

    public Object parse(String constructorExpression) {
        Statement stmt = null;

        try {
            stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(
                    "select " + constructorExpression + " from dual");
            rs.next();
            return rs.getObject(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }
}

