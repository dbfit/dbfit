package dbfit.environment;

import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;

public class DerbyRegressionSupportStoredProcs {

    public static void MakeUser() throws SQLException {

        try (
            // "jdbc:default:connection" tells the DriverManager to use the existing connection.
            Connection conn = DriverManager.getConnection("jdbc:default:connection");
            Statement stmt = conn.createStatement();
        ) {
            String sql = "INSERT INTO Users (Name, UserName) VALUES ('user1', 'fromproc')";
            stmt.execute(sql);
        }
    }

    public static void calcLength(String name, int[] strLength) throws SQLException {
        strLength[0] = name.length();
    }

    public static double multiply(double n1, double n2) throws SQLException {
        return n1 * n2;
    }
}
