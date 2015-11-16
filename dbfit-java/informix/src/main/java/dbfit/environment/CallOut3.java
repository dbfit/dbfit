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
 *  Title:       CallOut3.java
 *
 *  Description: 
 *     Execute a C function that has an OUT parameter:
 *     		decimal (16,3) c_out_boolean ( i int, OUT b boolean)
 *
 *  - Since java.sql.Types doesn't have a boolean type this demo needs
 *    to use IfmxCallableStatement's IfxRegisterOutParameter() method
 *    to register the OUT parameters type.
 *
 *  IMPORTANT: OUT parameters are currently only supported with
 *  9.x servers or higher. A syntax error will be returned by
 *  server which don't support OUT parameters.
 *
 *  1. Compile outparam.c which contains c_out_boolean, use the makefile:
 *        make -f makefile.callable
 *     This demo program assumes that outparam.so is located in the 
 *     directory from which this demo program is being run.
 *
 *  2. Use CreateDB to create the Database testdb if not already done.
 *           java CreateDB 'jdbc:informix-sqli:
 *           //myhost:1533:user=<username>;password=<password>'
 *
 *  3. Run the program 
 *          java CallOut3
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
import java.sql.Statement;
import java.util.StringTokenizer;

import com.informix.jdbc.IfmxCallableStatement;
import com.informix.lang.IfxTypes;

public class CallOut3 {
    String     url  = null;
    Connection conn = null;
    
    public static void mainxxx(String args[]) {
        new CallOut3(args);
    }
    
    CallOut3(String args[]) {
        IfmxCallableStatement cstmt = null;
        String s;
        
        System.out.println("--------------------------------------");
        System.out.println("- Start - Callable Statement Demo 3   ");
        System.out.println("          (OUT parameter)             ");
        System.out.println("--------------------------------------");
        
        // -----------
        // Getting URL
        // -----------
        if (args.length == 0) {
            System.out.println("ERROR: connection URL must be provided in " + "order to run the demo!");
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
            System.out.println("\n***ERROR: " + e.getMessage());
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
            System.out.println("\n***ERROR: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        System.out.println();
        
        // -------------------------------
        // Drop C UDR if it already exists
        // -------------------------------
        try {
            s = "drop function c_out_boolean";
            System.out.print(s + "...");
            
            Statement stmt = conn.createStatement();
            int count = stmt.executeUpdate(s);
            stmt.close();
        } catch (SQLException e) {
            // -674 Routine (c_out_boolean) can not be resolved.
            if (e.getErrorCode() != -674) {
                System.out.println("\n***ERROR: " + e.getErrorCode() + " " + e.getMessage());
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
            s = "create function  c_out_boolean ( i int, OUT b boolean) \n" + "returns decimal(16,3) \n" + "external name  " + "'" + System.getProperty("user.dir")
                    + java.io.File.separatorChar + "outparams.so" + "'\n" + "language c";
            
            System.out.print(s);
            
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(s);
            System.out.println("...ok");
        } catch (SQLException e) {
            System.out.println("\n***ERROR: " + e.getErrorCode() + " " + e.getMessage());
            if (e.getErrorCode() == -201) {
                s = "\nServer must not be version 9.x since it doesn't " + "recognize 'OUT' keyword\n";
                System.out.println(s);
            }
            e.printStackTrace();
            return;
        }
        System.out.println();
        
        try {
            //
            //  Call with positive integer, boolean should be false
            //
            System.out.println("-------------------------------------------------");
            System.out.println("    First run");
            System.out.println("        return should be: 123.456");
            System.out.println("        OUT should be:    true");
            System.out.println("-------------------------------------------------");
            String command = "{? = call c_out_boolean(?, ?)}  ";
            cstmt = (IfmxCallableStatement) conn.prepareCall(command);
            System.out.println("prepareCall(" + command + ")...okay");
            
            System.out.println("");
            System.out.println(" *** Has an output parameter: " + ((IfmxCallableStatement) cstmt).hasOutParameter());
            System.out.println("");
            
            System.out.println("setInteger(1, 45)... ");
            cstmt.setInt(1, 45);
            
            System.out.println("setBoolean(2, true)... ");
            cstmt.setBoolean(2, true);
            
            System.out.println("IfxRegisterOutParameter(2, (int) IfxTypes.IFX_TYPE_BOOL)... ");
            cstmt.IfxRegisterOutParameter(2, (int) IfxTypes.IFX_TYPE_BOOL);
            
            execute(cstmt); // execute the function an display the results
            
            //
            //  Call with negative integer, boolean should be false
            //
            System.out.println();
            System.out.println("-------------------------------------------------");
            System.out.println("    Second run");
            System.out.println("        return should be:  -123.456");
            System.out.println("        OUT should be:     false");
            System.out.println("-------------------------------------------------");
            cstmt = (IfmxCallableStatement) conn.prepareCall(command);
            System.out.println("prepareCall(" + command + ")...okay");
            
            System.out.println("");
            System.out.println(" *** Has an output parameter: " + ((IfmxCallableStatement) cstmt).hasOutParameter());
            System.out.println("");
            
            System.out.println("setInt(1, -70)... ");
            cstmt.setInt(1, -70);
            
            System.out.println("setBoolean(2, false)... ");
            cstmt.setBoolean(2, false);
            
            System.out.println("IfxRegisterOutParameter(2, (int) IfxTypes.IFX_TYPE_BOOL)... ");
            cstmt.IfxRegisterOutParameter(2, (int) IfxTypes.IFX_TYPE_BOOL);
            
            System.out.println("");
            System.out.println("OUT parameter should NOT be null");
            System.out.println("");
            execute(cstmt); // execute the function an display the results
            
        } catch (SQLException e) {
            System.out.println("\n***ERROR: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        System.out.println("--------------------------------------");
        System.out.println("- Start - Callable Statement Demo 3   ");
        System.out.println("          (OUT parameter)             ");
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
                System.out.println("\treturn value: " + rs.getBigDecimal(1));
                if (rs.wasNull() == true)
                    System.out.println("\treturn value was null");
            }
            
            System.out.println("");
            if (cstmt.wasNull() == true) {
                System.out.println("\tOut parameter value is null");
            } else {
                boolean b = cstmt.getBoolean(2);
                if (b == true)
                    System.out.println("FAILED: Expected = false Returned = " + b);
                else
                    System.out.println("\tOut parameter value: " + b);
            }
            System.out.println("");
            System.out.println("");
        } catch (SQLException e) {
            System.out.println("\n***ERROR: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
}
