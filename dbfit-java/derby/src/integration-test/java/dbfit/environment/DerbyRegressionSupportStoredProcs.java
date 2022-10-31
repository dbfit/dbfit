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

    public static void CalcLength(String Name, int[] StrLength) throws SQLException {
        StrLength[0] = Name.length();
    }

    public static double Multiply(double n1, double n2) throws SQLException {
        return n1 * n2;
    }

    public static void raise_error_with_params(String name, int[] strlength) throws SQLException {
        try (
            // "jdbc:default:connection" tells the DriverManager to use the existing connection.
            Connection conn = DriverManager.getConnection("jdbc:default:connection");
            Statement stmt = conn.createStatement();
        ) {
            if (name.equals("xx")) {
                throw new SQLException("test exception", "38123");
            }
        }
    }

    public static void raise_error_no_params() throws SQLException {
        try (
            // "jdbc:default:connection" tells the DriverManager to use the existing connection.
            Connection conn = DriverManager.getConnection("jdbc:default:connection");
            Statement stmt = conn.createStatement();
        ) {
            throw new SQLException("test exception", "38123");
        }
    }
}
