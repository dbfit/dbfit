package dbfit.environment;

import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DerbyRegressionSupportStoredProcs {

    public static void MakeUser() throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet res = null;

        try {
            // "jdbc:default:connection" tells the DriverManager to use the existing connection.
            conn = DriverManager.getConnection("jdbc:default:connection");
            String sql = "INSERT INTO Users (Name, UserName) VALUES ('user1', 'fromproc')";
            stmt = conn.prepareStatement(sql);
            stmt.execute();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
}
