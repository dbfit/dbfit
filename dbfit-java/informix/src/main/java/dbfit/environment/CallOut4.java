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
 *  Title:       CallOut4.java
 *
 *  Description: 
 *     Execute a SPL function that has an OUT parameter:
 *     		set(int8 not null) spl_out_clob ( i int, OUT c clob )
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
 *          java CallOut4
 *          'jdbc:informix-sqli://testdb:1533:
 *          user=<username>;password=<password>'
 *
 *  3. Expected Result
 *           See README file.
 *
 ***************************************************************************
 */

import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

public class CallOut4 {
    String     url  = null;
    Connection conn = null;
    
    public static void mainxxx(String args[]) {
        new CallOut4(args);
    }
    
    CallOut4(String args[]) {
        CallableStatement cstmt = null;
        String s;
        
        System.out.println("--------------------------------------");
        System.out.println("- Start - Callable Statement Demo 4   ");
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
            s = "drop function spl_out_clob";
            System.out.print(s + "...");
            
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(s);
            stmt.close();
        } catch (SQLException e) {
            // -674 Routine (spl_out_clob) can not be resolved.
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
        
        // -------------------------------
        // Drop the table the UDR uses
        // -------------------------------
        try {
            s = "drop table clob_tab";
            System.out.print(s + "...");
            
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(s);
            stmt.close();
        } catch (SQLException e) {
        }
        System.out.println();
        System.out.println();
        
        // ----------------------------------
        // Create the table that the UDR uses
        // ----------------------------------
        try {
            s = "create table clob_tab ( c clob)";
            System.out.print(s + "...");
            
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(s);
            
            s = "\ninsert into clob_tab values " + "( filetoclob(\"callable.dat\", \"client\"))";
            System.out.print(s + "...");
            stmt.executeUpdate(s);
            
            stmt.close();
        } catch (SQLException e) {
            System.out.println("\n***FAILED: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        System.out.println();
        System.out.println();
        
        // --------------
        // Creating UDR
        // --------------
        try {
            s = "create function  spl_out_clob ( i int, OUT b clob ) \n" + "returning set(int8 not null) ; \n" + " define ret_val set(lvarchar not null) ; \n" +
            
            " let ret_val='set{21474836477, -21474836477, 1234567890123}'; \n" +
            
            " select * into b from clob_tab; \n" +
            
            "return ret_val; \n" + "end function; \n";
            
            System.out.print(s);
            
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(s);
            System.out.println("...ok");
            stmt.close();
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
        System.out.println();
        
        try {
            System.out.println("-------------------------------------------------");
            System.out.println("    First run use getClob() to get the clob");
            System.out.println("    and getArray() to get the collection.");
            
            System.out.println("-------------------------------------------------");
            String command = "{? = call spl_out_clob(?, ?)}  ";
            cstmt = conn.prepareCall(command);
            System.out.println("prepareCall(" + command + ")...okay");
            
            System.out.println("Warnings on CallableStatement object: ");
            showWarnings(cstmt);
            System.out.println("");
            
            System.out.println("setInt(1, -55)... ");
            cstmt.setDouble(1, -55);
            
            System.out.println("setNull(2, Types.CLOB)");
            cstmt.setNull(2, Types.CLOB);
            
            System.out.println("registerOutParameter(2, Types.CLOB)... ");
            cstmt.registerOutParameter(2, Types.CLOB);
            
            execute(cstmt, false); // execute the function an display the results
            
            System.out.println();
            System.out.println();
            System.out.println("-------------------------------------------------");
            System.out.println("    Second run use getObject() to get the clob");
            System.out.println("    and to get the collection.");
            
            System.out.println("-------------------------------------------------");
            cstmt = conn.prepareCall(command);
            System.out.println("prepareCall(" + command + ")...okay");
            
            System.out.println("Warnings on CallableStatement object: ");
            showWarnings(cstmt);
            System.out.println("");
            
            System.out.println("setInt(1, 55)... ");
            cstmt.setDouble(1, 55);
            
            System.out.println("setNull(2, Types.CLOB)");
            cstmt.setNull(2, Types.CLOB);
            
            System.out.println("registerOutParameter(2, Types.CLOB)... ");
            cstmt.registerOutParameter(2, Types.CLOB);
            
            execute(cstmt, true); // execute the function an display the results
        } catch (SQLException e) {
            System.out.println("\n***FAILED: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        System.out.println("--------------------------------------");
        System.out.println("- Start - Callable Statement Demo 4   ");
        System.out.println("          (OUT parameter)             ");
        System.out.println("--------------------------------------");
    }
    
    private void execute(CallableStatement cstmt, boolean useGetObject) {
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
                System.out.println("    Return value: ");
                if (useGetObject == true) {
                    java.util.Collection coll = (Collection) rs.getObject(1);
                    dumpCollection(coll, "    ");
                } else {
                    java.sql.Array arr = rs.getArray(1);
                    dumpArray(arr, "    ");
                }
                if (rs.wasNull() == true)
                    System.out.println("\treturn value was null");
            }
            rs.close();
            System.out.println("");
            if (cstmt.wasNull() == true) {
                System.out.println("    Out parameter value is null");
            } else {
                System.out.println("    Out parameter object: ");
                java.sql.Clob c = (java.sql.Clob) cstmt.getObject(2);
                dumpClob(c, "    ");
            }
            System.out.println();
            System.out.println();
        } catch (SQLException e) {
            System.out.println("\n***FAILED: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
    
    /*
     * Display a java.util.Collection object
     */
    private void dumpCollection(Collection coll, String pretty) {
        InputStream stream = null;
        byte[] b = null;
        String s = null;
        
        try {
            // Let's see what's in it
            if (coll != null) {
                boolean classPrinted = false;
                Iterator it = coll.iterator();
                if (it.hasNext() == false)
                    System.out.println(pretty + "Collection object class: Collection is empty");
                else {
                    while (it.hasNext()) {
                        Object obj = it.next();
                        if (classPrinted == false) {
                            System.out.println(pretty + "Collection object class: " + obj.getClass().getName());
                            classPrinted = true;
                        }
                        System.out.println(pretty + "  element: " + obj.toString());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("dump Collection failed: " + e.toString());
        }
    }
    
    /*
     * Display a java.sql.Array object
     */
    private void dumpArray(java.sql.Array arr, String pretty) {
        byte[] b = null;
        String s = null;
        
        try {
            // Let's see what's in it
            if (arr != null) {
                boolean classPrinted = false;
                long[] longArray = (long[]) arr.getArray();
                if (longArray.length == 0)
                    System.out.println(pretty + "Array object class: Array is empty");
                else {
                    for (int i = 0; i < longArray.length; i++) {
                        System.out.println(pretty + "  element: " + longArray[i]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("dump Array failed: " + e.toString());
        }
    }
    
    /*
     * Display a java.sql.Clob object.
     */
    private void dumpClob(Clob clob, String pretty) throws SQLException {
        InputStream stream = null;
        byte[] b = null;
        String s = null;
        
        stream = clob.getAsciiStream();
        try {
            b = new byte[stream.available()];
            stream.read(b);
        } catch (Exception e) {
            System.out.println("dumpClob(Clob) InputStream failed: " + e.toString());
            return;
        }
        s = new String(b).trim();
        System.out.println("****** clob data start ***************");
        if (!s.equals("This the character data for a clob."))
            System.out.println("FAILED: Expected = This the character data for a clob. Returned = " + s);
        else
            System.out.println(s);
        System.out.println("****** clob data end ***************");
        System.out.println();
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
