package dbfit.environment;

/**************************************************************************
 *
 * Licensed Materials - Property of IBM Corporation
 *
 * Restricted Materials of IBM Corporation
 *
 * IBM Informix JDBC Driver
 * (c) Copyright IBM Corporation 1998, 2013  All rights reserved.
 *
 ****************************************************************************/
/***************************************************************************
 *  Title:       CallOut2.java
 *
 *  Description: 
 *     Execute a SPL function that has an OUT parameter:
 *     		float spl_out_int(double precision, OUT int)
 *
 *      Use IfmxCallableStatement.hasOutParameter() method.
 *
 *  IMPORTANT: OUT parameters are currently only supported with
 *  9.x servers or higher. A syntax error will be returned by
 *  server which don't support OUT parameters.
 *
 *  1. Use CreateDB to create the Database testdb if not already done.
 *           java CreateDB 'jdbc:informix-sqli:
 *           //myhost:1533:user=<username>;password=<password>'
 *
 *  2. Run the program 
 *          java CallOut2
 *          'jdbc:informix-sqli://testdb:1533:
 *          user=<username>;password=<password>'
 *
 *  3. Expected Result
 *           See README file.
 *
 ***************************************************************************
 */

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;
import java.util.StringTokenizer;

import com.informix.jdbc.IfmxCallableStatement;

public class CallOut2 {
    String     url  = null;
    Connection conn = null;
    
    public static void mainxxx(String args[]) {
        new CallOut2(args);
    }
    
    CallOut2(String args[]) {
        CallableStatement cstmt = null;
        String s;
        
        System.out.println("--------------------------------------");
        System.out.println("- Start - Callable Statement Demo 2   ");
        System.out.println("          (OUT parameter)             ");
        System.out.println("--------------------------------------");
        
        // -----------
        // Getting URL
        // -----------
        if (args.length == 0) {
            System.out.println("FAILED: connection URL must be provided in " + "order to run the demo!");
            return;
        }
        url = args[0];
        
        StringTokenizer st = new StringTokenizer(url, ":");
        String token;
        String newUrl = "";
        
        for (int i = 0; i < 4; ++i) {
            if (!st.hasMoreTokens()) {
                System.out.println("FAILED: incorrect URL format!");
                return;
            }
            token = st.nextToken();
            if (newUrl != "")
                newUrl += ":";
            newUrl += token;
        }
        
        newUrl += "/testDB";
        
        while (st.hasMoreTokens()) {
            newUrl += ":" + st.nextToken();
        }
        
        // --------------
        // Loading driver
        // --------------
        try {
            System.out.print("Loading JDBC driver...");
            Class.forName("com.informix.jdbc.IfxDriver");
            System.out.println("ok");
        } catch (java.lang.ClassNotFoundException e) {
            System.out.println("\n***FAILED: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        // ------------------
        // Getting connection
        // ------------------
        try {
            System.out.print("Getting connection...");
            conn = DriverManager.getConnection(newUrl);
            System.out.println("ok");
        } catch (SQLException e) {
            System.out.println("URL = \"" + newUrl + "\"");
            System.out.println("\n***FAILED: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        System.out.println();
        
        // -------------------------------
        // Drop SPL UDR if it already exists
        // -------------------------------
        try {
            s = "drop function spl_out_int";
            System.out.print(s + "...");
            
            Statement stmt = conn.createStatement();
            int count = stmt.executeUpdate(s);
            stmt.close();
        } catch (SQLException e) {
            // -674 Routine (spl_out_int) can not be resolved.
            if (e.getErrorCode() != -674) {
                System.out.println("\n***FAILED: " + e.getErrorCode() + " " + e.getMessage());
                if (e.getErrorCode() == -201) {
                    s = "\nServer must not be version 9.x since it doesn't " + "recognize 'function' keyword\n";
                    System.out.println(s);
                }
                e.printStackTrace();
                return;
            } else
                System.out.println("ok");
        }
        System.out.println();
        
        // --------------
        // Creating UDR
        // --------------
        try {
            s = "create function spl_out_int(db double precision , OUT i int) \n" + "returning float; \n" + "   define f float; \n" +
            
            "   let f = db + 2.3; \n" + "   let i = db + 2; \n" +
            
            "return f; \n" + "end function; \n";
            
            System.out.print(s);
            
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(s);
            System.out.println("...ok");
        } catch (SQLException e) {
            System.out.println("\n***FAILED: " + e.getErrorCode() + " " + e.getMessage());
            if (e.getErrorCode() == -201) {
                s = "\nServer must not be version 9.x since it doesn't " + "recognize 'OUT' keyword\n";
                System.out.println(s);
            }
            e.printStackTrace();
            return;
        }
        System.out.println();
        
        try {
            System.out.println("-------------------------------------------------");
            System.out.println("    First run");
            System.out.println("        return should be: 6.8");
            System.out.println("        OUT should be:    6");
            System.out.println("-------------------------------------------------");
            String command = "{? = call spl_out_int(?, ?)}  ";
            cstmt = conn.prepareCall(command);
            System.out.println("prepareCall(" + command + ")...okay");
            
            System.out.println("Warnings on CallableStatement object: ");
            showWarnings(cstmt);
            System.out.println("");
            
            System.out.println("registerOutParameter(2, java.sql.INTEGER)... ");
            cstmt.registerOutParameter(2, Types.INTEGER);
            
            System.out.println("setDouble(1, 4.5)... ");
            cstmt.setDouble(1, 4.5);
            
            //System.out.println("setInt(2, 1)... ");
            //cstmt.setInt(2, 1);
                       
            execute(cstmt); // execute the function an display the results
            
            /*
             * Pass in a NULL as the IN parameter
             */
/*            
            System.out.println("");
            System.out.println("");
            System.out.println("-------------------------------------------------");
            System.out.println("    Second run");
            System.out.println("        return should be: null");
            System.out.println("        OUT should be:    null");
            System.out.println("-------------------------------------------------");
            cstmt = conn.prepareCall(command);
            System.out.println("prepareCall(" + command + ")...okay");
            
            System.out.println("");
            System.out.println(" *** Has an output parameter: " + ((IfmxCallableStatement) cstmt).hasOutParameter());
            System.out.println("");
            
            System.out.println("setNull(1, java.sql.Types.DOUBLE)... ");
            cstmt.setNull(1, java.sql.Types.DOUBLE);
            
            System.out.println("setInt(2, 1)... ");
            cstmt.setDouble(2, 1);
            
            System.out.println("registerOutParameter(2, java.sql.INTEGER)... ");
            cstmt.registerOutParameter(2, Types.INTEGER);
            
            System.out.println("");
            System.out.println("OUT parameter wasNull() should return TRUE.");
            System.out.println("");
            execute(cstmt); // execute the function an display the results
*/            
            /*
             * Set OUT parmeter to NULL, this should have no affect on
             * the out parameter value.
             */
/*            
            System.out.println("");
            System.out.println("-------------------------------------------------");
            System.out.println("    Third run");
            System.out.println("        return should be:  70.28999999999999");
            System.out.println("        OUT should be:     69");
            System.out.println("-------------------------------------------------");
            cstmt = conn.prepareCall(command);
            System.out.println("prepareCall(" + command + ")...okay");
            
            System.out.println("Warnings on CallableStatement object: ");
            showWarnings(cstmt);
            System.out.println("");
            
            System.out.println("setDouble(1, 67.99)... ");
            cstmt.setDouble(1, 67.99);
            
            System.out.println("setNull(2, java.sql.Types.INTEGER)... ");
            cstmt.setNull(2, java.sql.Types.INTEGER);
            
            System.out.println("registerOutParameter(2, java.sql.INTEGER)... ");
            cstmt.registerOutParameter(2, Types.INTEGER);
            
            System.out.println("");
            System.out.println("OUT parameter should NOT be null");
            System.out.println("");
            execute(cstmt); // execute the function an display the results
*/            
        } catch (SQLException e) {
            System.out.println("\n***FAILED: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    
        System.out.println("--------------------------------------");
        System.out.println("- End - Callable Statement Demo 2   ");
        System.out.println("        (OUT parameter)             ");
        System.out.println("--------------------------------------");
    }
    
    private void execute(CallableStatement cstmt) {
        try {
            System.out.println("");
            System.out.println("Calling executeQuery ...");
            ResultSet rs = cstmt.executeQuery();
            System.out.println("");
            System.out.println("Results:");
            if (rs == null) {
                System.out.println("FAILED: rs is null *** this is BAD.");
            } else {
                rs.next();
                System.out.println("\treturn value: " + rs.getDouble(1));
                if (rs.wasNull() == true)
                    System.out.println("\treturn value was null");
            }
            
            System.out.println("");
            if (cstmt.wasNull() == true) {
                System.out.println("\tOut parameter value is null");
            } else {
                int i = cstmt.getInt(2);
                if (i != 69)
                    System.out.println("FAILED: Expected = 69 Returned = " + i);
                else
                    System.out.println("\tOut parameter value: " + i);
            }
            System.out.println("");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println("\n***FAILED: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
    
    private void showWarnings(CallableStatement cstmt) throws SQLException {
        SQLWarning w = cstmt.getWarnings();
        int i = 1;
        while (w != null) {
            System.out.println("  Warning #" + i + ":");
            System.out.println("    SQLState = " + w.getSQLState());
            System.out.println("    Message = " + w.getMessage());
            System.out.println("    SQLCODE = " + w.getErrorCode());
            w = w.getNextWarning();
            i++;
        }
    }
    
}
