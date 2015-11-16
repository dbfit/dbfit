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
 *  Title:       CallOut1.java
 *
 *  Description: 
 *     Execute a C function that has an OUT parameter:
 *     		lvarchar c_out_double(int, OUT double precision)
 *
 *  IMPORTANT: OUT parameters are currently only supported with
 *  9.x servers or higher. A syntax error will be returned by
 *  server which don't support OUT parameters.
 *
 *  1. Compile outparam.c which contains c_out_double, use the makefile:
 *        make -f makefile.callable
 *     This demo program assumes that outparam.so is located in the
 *     directory from which the demo program is being run.
 *
 *  2. Use CreateDB to create the Database testdb if not already done.
 *           java CreateDB 'jdbc:informix-sqli:
 *           //myhost:1533:user=<username>;password=<password>'
 *
 *  3. Run the program 
 *          java CallOut1
 *          'jdbc:informix-sqli://testdb:1533:
 *          user=<username>;password=<password>'
 *
 *  4. Expected Result
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

public class CallOut1 {
    String     url  = null;
    Connection conn = null;
    
    public static void mainxxx(String args[]) {
        new CallOut1(args);
    }
    
        CallableStatement cstmt = null;
        CallOut1(String args[]) {
        String s;
        
        System.out.println("--------------------------------------");
        System.out.println("- Start - Callable Statement Demo 1   ");
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
        // Drop C UDR if it already exists
        // -------------------------------
        try {
            s = "drop function c_out_double";
            System.out.print(s + "...");
            
            Statement stmt = conn.createStatement();
            int count = stmt.executeUpdate(s);
            stmt.close();
        } catch (SQLException e) {
            // -674 Routine (c_out_double) can not be resolved.
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
        
        // --------------
        // Creating UDR
        // --------------
        try {
            s = "create function  c_out_double ( i int, OUT d double precision)\n" + "returns lvarchar \n" + "external name  " + "'" + System.getProperty("user.dir")
                    + java.io.File.separatorChar + "outparams.so" + "'\n" + "language c";
            
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
        
        System.out.println("");
        try {
            System.out.println("-------------------------------------------------");
            System.out.println("First run");
            System.out.println("    return should be: 'This is the static lvarchar string returned.'");
            System.out.println("    OUT should be:     100.0");
            System.out.println("-------------------------------------------------");
            String command = "{? = call c_out_double(?, ?)}  ";
            cstmt = conn.prepareCall(command);
            System.out.println("prepareCall(" + command + ")...okay");
            
            System.out.println("Warnings on CallableStatement object: ");
            showWarnings(cstmt);
            System.out.println("");
            
            System.out.println("setInt(1, 10)... ");
            cstmt.setInt(1, 10);
            
//            System.out.println("setDouble(2, 12)... ");
//            cstmt.setDouble(2, 12.0);
            
            System.out.println("registerOutParameter(2, java.sql.DOUBLE)... ");
            cstmt.registerOutParameter(2, Types.DOUBLE);
            
            // Now execute the function
            System.out.println("");
            System.out.println("Calling executeQuery ...");
            ResultSet rs = cstmt.executeQuery();
            System.out.println("");
            System.out.println("Results:");
            if (rs == null) {
                System.out.println("FAILED: rs is null *** this is BAD.");
            } else {
                rs.next();
                String str = rs.getString(1);
                // verify result
                if (!str.equals("This is the static lvarchar string returned."))
                    System.out.println("FAILED: Expected = This is the static lvarchar string returned. Returned = " + str);
                else
                    System.out.println("\treturn value: '" + str + "'");
                if (rs.wasNull() == true)
                    System.out.println("\treturn value was null");
            }
            
            System.out.println("");
            if (cstmt.wasNull() == true) {
                System.out.println("\tOut parameter value is null");
            } else {
                double d = cstmt.getDouble(2);
                System.out.println("\tOut parameter value: " + d);
            }
            System.out.println("");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println("\n***FAILED: " + e.getErrorCode() + " " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        System.out.println("------------------------------------");
        System.out.println("- End - Callable Statement Demo 1   ");
        System.out.println("        (OUT parameter)             ");
        System.out.println("------------------------------------");
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
