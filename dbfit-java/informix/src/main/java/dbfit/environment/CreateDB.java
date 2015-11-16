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
/*****************************************************************************
 *
 *  Title:        CreateDB.java
 *
 *  Description:    Demo how to create a database 
 *        
 *  An example of running the program: 
 *        
 *   java CreateDB 
 *      'jdbc:informix-sqli://myhost:1533:user=<username>;password=<password>'
 *        
 *   Expected result:
 * 
 * >>>Create Database test.
 * URL = "jdbc:informix-sqli://myhost:1533:user=<username>;password=<password>"
 * >>>End of Create Database test.
 * 
 ***************************************************************************
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDB {
    
    public static void mainxxx(String[] args) {
        if (args.length == 0) {
            System.out.println("FAILED: connection URL must be provided in order to run the demo!");
            return;
        }
        
        String url = args[0];
        Connection conn = null;
        int rc;
        String cmd = null;
        
        String testName = "Create Database";
        
        System.out.println(">>>" + testName + " test.");
        System.out.println("URL = \"" + url + "\"");
        
        // INFORMIX_EXTEXT_BEGIN Create1.jav
        try {
            Class.forName("com.informix.jdbc.IfxDriver");
        } catch (Exception e) {
            System.out.println("FAILED: failed to load Informix JDBC driver.");
            e.printStackTrace();
            return;
        }
        // INFORMIX_EXTEXT_END Create1.jav
        // INFORMIX_EXTEXT_BEGIN Create2.jav
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("FAILED: failed to connect!");
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        // INFORMIX_EXTEXT_END Create2.jav
        
        // Drop database before starting - ignore errors 
        try {
            Statement dstmt = conn.createStatement();
            dstmt.executeUpdate("drop database testDB");
        } catch (SQLException e) {
            ;
        }
        
        try {
            Statement stmt = conn.createStatement();
            cmd = "create database testDB;";
            rc = stmt.executeUpdate(cmd);
            stmt.close();
        } catch (SQLException e) {
            System.out.println("FAILED: execution failed - statement: " + cmd);
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("FAILED: failed to close the connection!");
            e.printStackTrace();
            return;
        }
        System.out.println(">>>End of " + testName + " test.");
    }
}
