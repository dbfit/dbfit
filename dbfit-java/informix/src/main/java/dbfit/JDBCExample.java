package dbfit;

import com.informix.jdbc.IfxSqliConnect;

import java.sql.*;

//STEP 1. Import required packages

public class JDBCExample {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.informix.jdbc.IfxDriver";  
 //static final String DB_URL = "jdbc:informix-sqli://10.199.100.2:4128/sentryprod:informixserver=sentrydev1_tcp";
    static final String DB_URL ="jdbc:informix-sqli://grace:4161/webto3:informixserver=webtodev1_tcp";
   //  Database credentials
  // static final String USER = "ent_etl";
  // static final String PASS = "devetl01fix";
    static final String USER ="muppana";
    static final String PASS = "IMN$Crm1212";

    static Savepoint savepoint;
    static IfxSqliConnect ifxSqliConnection;

   
   public static void main(String[] args) {
   Connection conn = null;
   Statement stmt = null;
   try{
      //STEP 2: Register JDBC driver
       try {
           Class.forName("com.informix.jdbc.IfxDriver");
       } catch (ClassNotFoundException e) {
           e.printStackTrace();
       }

       //STEP 3: Open a connection
      System.out.println("Connecting to database...");
      // ifxSqliConnection = (IfxSqliConnect) DriverManager.getConnection(DB_URL, USER, PASS);
       conn=DriverManager.getConnection(DB_URL, USER, PASS);

      //STEP 4: Execute a query
      System.out.println("Creating database...");

    // savepoint=conn.setSavepoint();


       conn.setAutoCommit(false);
       //savepoint=conn.setSavepoint();
      // ifxSqliConnection=new IfxSqliConnect();
      // savepoint =ifxSqliConnection.setSavepoint();
/*
       String savepointName = "eee" ;
       if (savepointName.length() > 10) savepointName = savepointName.substring(1, 9);
       savepoint = null;

       try {
           savepoint = conn.setSavepoint(savepointName);
       } catch (SQLException e) {
           throw new RuntimeException("Exception while setting savepoint", e);
       }  */



      //conn.setAutoCommit(true);

     // conn.releaseSavepoint(savepoint);

      stmt = conn.createStatement();
      
    /*  String sql = "select first_name from ent_etl.dbfit_test  ";
       ResultSet rs = stmt.executeQuery(sql);
       while(rs.next()){
           //Retrieve by column name
           String first = rs.getString("first_name");
           System.out.print(", First: " + first);

       }
       rs.close();

        set lock mode to wait 20
      set role r_webusr
      set isolation to committed read retain update locks

        */

       CallableStatement cstmt=null;
       String sql1="call testprocout(?)";

       cstmt=conn.prepareCall("{call testprocout(?)}");
      /* cstmt.setString(1,"Sadler");
       cstmt.registerOutParameter(2, Types.CHAR);
      cstmt.registerOutParameter(3,Types.CHAR);
       Boolean  name1=cstmt.execute();
       System.out.println(name1);

       //cstmt.registerOutParameter(2,);
       System.out.println("last name="+cstmt.getString(2));
     System.out.println("first name="+cstmt.getString(3));*/
       cstmt.registerOutParameter(1, Types.INTEGER);
       Boolean  name1=cstmt.execute();

       System.out.println(name1);
       System.out.println("last name="+cstmt.getInt(1));




       // String sql = "insert into datatypetest values('2008-03-02','2008-03-02 01:41:39.0')";
       //String sql ="select * from sentrycf.text_message";
       //String sql ="Insert into Test_DBFit values ('Obi Wan',80)";

      // stmt.executeUpdate(sql);
       //stmt.executeQuery(sql);
       conn.commit();
       conn.setAutoCommit(false);
      System.out.println("Database created successfully...");
   }catch(SQLException e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }
   System.out.println("Goodbye!");
}//end main

   public  void releaseSavepoint(Savepoint savepoint)
            throws SQLException{

    }
}//end JDBCExample
