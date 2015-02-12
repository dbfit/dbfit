package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.NameNormaliser;
import dbfit.util.TypeNormaliserFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

import static dbfit.util.Direction.*;

/**
 * Created by muppana on 12/18/2014.
 */
@DatabaseEnvironment(name="Informix", driver="com.informix.jdbc.IfxDriver")
public class InformixEnvironment extends AbstractDbEnvironment  {

   private static  String connectionString;

     @Override
     public void afterConnectionEstablished() throws SQLException {
          if(!currentConnection.getAutoCommit()){
              currentConnection.setAutoCommit(true);
          }else{
              currentConnection.setAutoCommit(false);
          }
    }


    public  InformixEnvironment(String driverClassName) {
        super(driverClassName);
//        TypeNormaliserFactory.setNormaliser( com.informix.jdbc.IfxDateTime.class,
//                new OracleTimestampNormaliser());
        TypeNormaliserFactory.setNormaliser( com.informix.jdbc.IfxDate.class,
                new InformixDateNormalizer());
//        TypeNormaliserFactory.setNormaliser(java.sql.Date.class,
//                new SqlDateNormaliser());
    }


    protected String parseCommandText(String commandText) {
        commandText = commandText.replaceAll(paramNamePattern, "?");
        return super.parseCommandText(commandText);
    }

    private static String paramNamePattern = "[@:]([A-Za-z0-9_]+)";
    private static Pattern paramRegex = Pattern.compile(paramNamePattern);

    public Pattern getParameterPattern() {
        return paramRegex;
    }

    protected String getConnectionString(String dataSource) {
      //  System.out.println("getConnection="+dataSource);
        return "jdbc:informix-sqli://" + dataSource;
    }

    protected String getConnectionString(String dataSource, String database) {
       // System.out.println("getConnection=" + dataSource + "/" + database);
        return "jdbc:informix-sqli://" + dataSource + "/" + database;
    }

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
            throws SQLException {
        String[] qualifiers = NameNormaliser.normaliseName(tableOrViewName)
                .split("\\.");
        /*  String qry = "  select sc.colname as column_name,'VARCHAR' as data_type ,'P' as direction from systables st,syscolumns sc,sysdefaults sd  where st.tabid=sc.tabid  and ";
        
        if (qualifiers.length == 2) {
            qry += " lower(st.owner)=? and lower(st.tabname)=? ";
        } else {
            qry += " (lower(st.tabname)=?)";
        }
        qry += " order by st.owner";
        
        System.out.println("Query="+qry);
        return readIntoParams(qualifiers, qry); */
        
          String qry1=" SELECT  c.colname[1,20] column_name ,";                          
          qry1+= " CASE coltype   ";                                                             
          qry1+= " WHEN 0 THEN 'char' ";      
          qry1+= " WHEN 1 THEN 'smallint'  ";                                                
          qry1+= " WHEN 2 THEN 'integer'  ";                                                 
          qry1+= " WHEN 3 THEN 'float'      ";                                               
          qry1+= " WHEN 4 THEN 'smallfloat'  ";  
         // qry1+= " WHEN 5 THEN 'decimal' "; 
          qry1+= "  WHEN 5 THEN 'decimal(' ||  ";                                             
          qry1+= "     TRIM(CAST(TRUNC(c.collength/256) AS VARCHAR(8)) || ',' ||    ";       
          qry1+= "     CAST(c.collength - TRUNC(c.collength/256)*256 AS VARCHAR(8))) || ')'";
          qry1+= " WHEN 6 THEN 'serial'  ";                                                  
          qry1+= " WHEN 7 THEN 'date'    ";                                                  
          qry1+= " WHEN 8 THEN 'money(' ||  ";                                               
          qry1+= " TRIM(CAST(TRUNC(c.collength/256) AS VARCHAR(8)) || ',' ||  ";         
          qry1+= " CAST(c.collength - TRUNC(c.collength/256)*256 AS VARCHAR(8))) || ')'";
          qry1+= " WHEN 9 THEN 'null'  ";                                                    
          qry1+= " WHEN 10 THEN 'datetime'  ";                                               
          qry1+= " WHEN 11 THEN 'byte'        ";                                             
          qry1+= " WHEN 12 THEN 'text'          ";                                           
          qry1+= " WHEN 13 THEN 'varchar'";    
          qry1+= " WHEN 14 THEN 'interval' ";                                                
          qry1+= " WHEN 15 THEN 'nchar(' || TRIM(CAST(c.collength AS CHAR(5))) || ')'";      
          qry1+= " WHEN 16 THEN 'nvarchar(' || TRIM(CAST(c.collength AS CHAR(5))) || ')'";   
          qry1+= " WHEN 17 THEN 'int8'";                                                     
          qry1+= " WHEN 18 THEN 'serial8'";                                                  
          qry1+= " WHEN 19 THEN 'set'";                                                      
          qry1+= " WHEN 20 THEN 'multiset'";                                                 
          qry1+= " WHEN 21 THEN 'list'";                                                     
          qry1+= " WHEN 22 THEN 'row'";                                                      
          qry1+= " WHEN 23 THEN 'collection'";                                               
          qry1+= " WHEN 24 THEN 'rowdef'";                                                   
          qry1+= " WHEN 256 THEN 'char'";                                                        
          qry1+= " WHEN 257 THEN 'smallint'";                                       
          qry1+= " WHEN 258 THEN 'integer'";                                        
          qry1+= " WHEN 259 THEN 'float'";                                          
          qry1+= " WHEN 260 THEN 'smallfloat'"; 
          //qry1+= " WHEN 5 THEN 'decimal not null' "; 
          qry1+= "  WHEN 261 THEN 'decimal('||";                                              
          qry1+= "      TRIM(CAST(TRUNC(c.collength/256) AS VARCHAR(8)) || ',' ||";           
          qry1+= "      CAST(c.collength - TRUNC(c.collength/256)*256 AS VARCHAR(8))) ||";    
          qry1+= "      ') not null'";                                                        
          qry1+= " WHEN 262 THEN 'serial'";                                         
          qry1+= " WHEN 263 THEN 'date'";                                           
          qry1+= " WHEN 264 THEN 'money(' ||";                                               
          qry1+= " TRIM(CAST(TRUNC(c.collength/256) AS VARCHAR(8)) || ',' ||";           
          qry1+= " CAST(c.collength - TRUNC(c.collength/256)*256 AS VARCHAR(8))) ||";    
          qry1+= " ') not null'";                                                        
          qry1+= " WHEN 265 THEN 'null'";                                           
          qry1+= " WHEN 266 THEN 'datetime'";                                       
          qry1+= " WHEN 267 THEN 'byte'";                                           
          qry1+= " WHEN 268 THEN 'text'";                                           
          qry1+= " WHEN 269 THEN 'varchar'";                                                        
          qry1+= " WHEN 270 THEN 'interval'";                                       
          qry1+= " WHEN 271 THEN 'nchar(' || TRIM(CAST(c.collength AS CHAR(5))) ||";         
          qry1+= " ') not null'";                                                        
          qry1+= " WHEN 272 THEN 'nvarchar(' || TRIM(CAST(c.collength AS CHAR(5))) ||";      
          qry1+= " ') not null'";                                                        
          qry1+= " WHEN 273 THEN 'int8'";                                           
          qry1+= " WHEN 274 THEN 'serial8'";                                        
          qry1+= " WHEN 275 THEN 'set'";                                            
          qry1+= " WHEN 276 THEN 'multiset'";                                       
          qry1+= " WHEN 277 THEN 'list'";                                           
          qry1+= " WHEN 278 THEN 'row'";                                            
          qry1+= " WHEN 279 THEN 'collection'";                                     
          qry1+= " WHEN 280 THEN 'rowdef'";                                         
          qry1+= " ELSE CAST(coltype AS CHAR(10))";                                          
          qry1+= " END data_type ,'P' as direction ";                                                                
          qry1+= " FROM systables t, syscolumns c";                                                  
          qry1+= " WHERE  t.tabid = c.tabid and ";                                                           
           
        if (qualifiers.length == 2) {
            qry1 += " lower(t.owner)=? and lower(t.tabname)=? ";
        } else {
            qry1 += " (lower(t.tabname)=?)";
        }
        qry1 += " order by t.owner";
        
        System.out.println("Query="+qry1);
        return readIntoParams(qualifiers, qry1); 
    }
    
    private Map<String, DbParameterAccessor> readIntoParams(
            String[] queryParameters, String query) throws SQLException {
      System.out.println("query="+query);
        PreparedStatement dc = currentConnection.prepareStatement(query);
        try {
            for (int i = 0; i < queryParameters.length; i++) {
            	if(queryParameters[i].length()==0)
            		queryParameters[i]="return_value";
            		
                dc.setString(i + 1,
                        NameNormaliser.normaliseName(queryParameters[i]));
 //System.out.println("parametersare ="+queryParameters[i]+"and length is "+queryParameters[i].length());
            }

            ResultSet rs = dc.executeQuery();
            Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();
            int position = 0;
            while (rs.next()) {
                String paramName = rs.getString(1);
                if (paramName == null)
                    paramName = "";
                String dataType = rs.getString(2);
                // int length=rs.getInt(3);
                // System.out.println("length="+length);
                String direction = rs.getString(3);
                Direction paramDirection = getParameterDirection(direction);
                DbParameterAccessor dbp = new DbParameterAccessor(paramName,
                        paramDirection, getSqlType(dataType),
                        getJavaClass(dataType),
                        paramDirection == RETURN_VALUE ? -1
                                : position++);
                allParams.put(NameNormaliser.normaliseName(paramName), dbp);


            }
            rs.close();
            return allParams;
        } finally {
            dc.close();
        }
    } 
    
    

    
    private static Direction getParameterDirection(int isOutput, String name) {
        if (name.isEmpty()) {
            return RETURN_VALUE;
        }

        return (isOutput == 1) ? OUTPUT : INPUT;
    }
    private static Direction getParameterDirection(String direction) {
    	/* if ("P".equals(direction))
            return INPUT;
        if ("O".equals(direction))
            return OUTPUT;
        if ("B".equals(direction))
            return INPUT_OUTPUT;
        if ("C".equals(direction))
            return RETURN_VALUE; 
            
            0 = Parameter is of unknown type 
            1 = Parameter is INPUT mode 
            2 = Parameter is INOUT mode 
            3 = Parameter is multiple return value 
            4 = Parameter is OUT mode 
            5 = Parameter is a return value
            */
        // todo return val
    	/* if ("P".equals(direction))
            return INPUT;
        if ("P".equals(direction))
            return OUTPUT;
        if ("P".equals(direction))
            return INPUT_OUTPUT;
        if ("C".equals(direction))
            return RETURN_VALUE;
        throw new UnsupportedOperationException("Direction " + direction
                + " is not supported"); */
    	if ("P".equals(direction))
            return INPUT;
        if ("P".equals(direction))
            return OUTPUT;
        if ("P".equals(direction))
            return INPUT_OUTPUT;
        if ("C".equals(direction))
            return RETURN_VALUE;
    	if ("1".equals(direction))
            return INPUT;
        if ("4".equals(direction))
            return OUTPUT;
        if ("2".equals(direction))
            return INPUT_OUTPUT;
        if ("3".equals(direction))
            return RETURN_VALUE;
        throw new UnsupportedOperationException("Direction " + direction
                + " is not supported");
    }

    // List interface has sequential search, so using list instead of array to
    // map types
    private static List<String> stringTypes = Arrays.asList(new String[] {
            "VARCHAR","VARCHAR(257)","VARCHAR2","LVARCHAR", "CHAR", "CHARACTER", "GRAPHIC", "VARGRAPHIC","BYTE" });
    private static List<String> intTypes = Arrays.asList(new String[] {
            "SMALLINT", "INT", "INTEGER","SERIAL" });
    private static List<String> longTypes = Arrays
            .asList(new String[] { "BIGINT" });
    private static List<String> floatTypes = Arrays
            .asList(new String[] { "FLOAT", "REAL" });
    private static List<String> doubleTypes = Arrays
            .asList(new String[] { "DOUBLE" });
    private static List<String> decimalTypes = Arrays.asList(new String[] {
            "DECIMAL","DECIMAL(p,s)" , "DEC", "DECFLOAT", "NUMERIC" });
    private static List<String> dateTypes = Arrays
            .asList(new String[] { "DATE","DATETIME YEAR TO DAY" });
    private static List<String> timestampTypes = Arrays
            .asList(new String[] { "TIMESTAMP" ,"DATETIME","DATETIME YEAR TO FRACTION(5)","DATETIME YEAR TO FRACTION(3)","DATETIME YEAR TO SECOND"});

    private static String NormaliseTypeName(String dataType) {
        dataType = dataType.toUpperCase().trim();
        return dataType;
    }

    private static int getSqlType(String dataType) {
        // todo:strip everything from first blank
        dataType = NormaliseTypeName(dataType);

        if (stringTypes.contains(dataType))
            return java.sql.Types.VARCHAR;
        if (decimalTypes.contains(dataType))
            return java.sql.Types.NUMERIC;
        if (intTypes.contains(dataType))
            return java.sql.Types.INTEGER;
        if (floatTypes.contains(dataType))
            return java.sql.Types.FLOAT;
        if (doubleTypes.contains(dataType))
            return java.sql.Types.DOUBLE;
        if (longTypes.contains(dataType))
            return java.sql.Types.BIGINT;
        if (timestampTypes.contains(dataType))
            return java.sql.Types.TIMESTAMP;
        if (dateTypes.contains(dataType))
            return java.sql.Types.DATE;
        
        //java.sql.Types.CLOB;
        //java.sql.Types.BLOB;
        //java.sql.Types.NCLOB;
        //java.sql.Types.
        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Class<?> getJavaClass(String dataType) {
        dataType = NormaliseTypeName(dataType);
        if (stringTypes.contains(dataType))
            return String.class;
        if (decimalTypes.contains(dataType))
            return BigDecimal.class;
        if (intTypes.contains(dataType))
            return Integer.class;
        if (floatTypes.contains(dataType))
            return Float.class;
        if (dateTypes.contains(dataType))
            return java.sql.Date.class;
        if (doubleTypes.contains(dataType))
            return Double.class;
        if (longTypes.contains(dataType))
            return Long.class;
        if (timestampTypes.contains(dataType))
            return java.sql.Timestamp.class;
        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {
        String[] qualifiers = NameNormaliser.normaliseName(procName).split(
                "\\.");
       // String qry = " select spi.paramname as column_name,'VARCHAR' as data_type,spi.direction as direction from sysprocedures sp ,storedprocinfo333 spi where sp.procname=spi.procname and ";
        String qry1 ="select spc.paramname as column_name," ;
        qry1+= " CASE paramtype   ";                                                             
        qry1+= " WHEN 0 THEN 'char' ";      
        qry1+= " WHEN 1 THEN 'smallint'  ";                                                
        qry1+= " WHEN 2 THEN 'integer'  ";                                                 
        qry1+= " WHEN 3 THEN 'float'      ";                                               
        qry1+= " WHEN 4 THEN 'smallfloat'  ";  
       // qry1+= " WHEN 5 THEN 'decimal' "; 
        qry1+= "  WHEN 5 THEN 'decimal(' ||  ";                                             
        qry1+= "     TRIM(CAST(TRUNC(spc.paramlen/256) AS VARCHAR(8)) || ',' ||    ";       
        qry1+= "     CAST(spc.paramlen - TRUNC(spc.paramlen/256)*256 AS VARCHAR(8))) || ')'";
        qry1+= " WHEN 6 THEN 'serial'  ";                                                  
        qry1+= " WHEN 7 THEN 'date'    ";                                                  
        qry1+= " WHEN 8 THEN 'money(' ||  ";                                               
        qry1+= " TRIM(CAST(TRUNC(spc.paramlen/256) AS VARCHAR(8)) || ',' ||  ";         
        qry1+= " CAST(spc.paramlen - TRUNC(spc.paramlen/256)*256 AS VARCHAR(8))) || ')'";
        qry1+= " WHEN 9 THEN 'null'  ";                                                    
        qry1+= " WHEN 10 THEN 'datetime'  ";                                               
        qry1+= " WHEN 11 THEN 'byte'        ";                                             
        qry1+= " WHEN 12 THEN 'text'          ";                                           
        qry1+= " WHEN 13 THEN 'varchar'";    
        qry1+= " WHEN 14 THEN 'interval' ";                                                
        qry1+= " WHEN 15 THEN 'nchar(' || TRIM(CAST(spc.paramlen AS CHAR(5))) || ')'";      
        qry1+= " WHEN 16 THEN 'nvarchar(' || TRIM(CAST(spc.paramlen AS CHAR(5))) || ')'";   
        qry1+= " WHEN 17 THEN 'int8'";                                                     
        qry1+= " WHEN 18 THEN 'serial8'";                                                  
        qry1+= " WHEN 19 THEN 'set'";                                                      
        qry1+= " WHEN 20 THEN 'multiset'";                                                 
        qry1+= " WHEN 21 THEN 'list'";                                                     
        qry1+= " WHEN 22 THEN 'row'";                                                      
        qry1+= " WHEN 23 THEN 'collection'";                                               
        qry1+= " WHEN 24 THEN 'rowdef'";                                                   
        qry1+= " WHEN 256 THEN 'char(' || TRIM(CAST(spc.paramlen AS CHAR(5))) ||";          
        qry1+= " ') not null'";                                                        
        qry1+= " WHEN 257 THEN 'smallint'";                                       
        qry1+= " WHEN 258 THEN 'integer not null'";                                        
        qry1+= " WHEN 259 THEN 'float not null'";                                          
        qry1+= " WHEN 260 THEN 'smallfloat not null'"; 
        //qry1+= " WHEN 5 THEN 'decimal not null' "; 
        qry1+= "  WHEN 261 THEN 'decimal('||";                                              
        qry1+= "      TRIM(CAST(TRUNC(spc.paramlen/256) AS VARCHAR(8)) || ',' ||";           
        qry1+= "      CAST(spc.paramlen - TRUNC(spc.paramlen/256)*256 AS VARCHAR(8))) ||";    
        qry1+= "      ') not null'";                                                        
        qry1+= " WHEN 262 THEN 'serial not null'";                                         
        qry1+= " WHEN 263 THEN 'date not null'";                                           
        qry1+= " WHEN 264 THEN 'money(' ||";                                               
        qry1+= " TRIM(CAST(TRUNC(spc.paramlen/256) AS VARCHAR(8)) || ',' ||";           
        qry1+= " CAST(spc.paramlen - TRUNC(spc.paramlen/256)*256 AS VARCHAR(8))) ||";    
        qry1+= " ') not null'";                                                        
        qry1+= " WHEN 265 THEN 'null not null'";                                           
        qry1+= " WHEN 266 THEN 'datetime not null'";                                       
        qry1+= " WHEN 267 THEN 'byte'";                                           
        qry1+= " WHEN 268 THEN 'text not null'";                                           
        qry1+= " WHEN 269 THEN 'varchar'";                                                        
        qry1+= " WHEN 270 THEN 'interval'";                                       
        qry1+= " WHEN 271 THEN 'nchar(' || TRIM(CAST(spc.paramlen AS CHAR(5))) ||";         
        qry1+= " ') not null'";                                                        
        qry1+= " WHEN 272 THEN 'nvarchar(' || TRIM(CAST(spc.paramlen AS CHAR(5))) ||";      
        qry1+= " ') not null'";                                                        
        qry1+= " WHEN 273 THEN 'int8'";                                           
        qry1+= " WHEN 274 THEN 'serial8'";                                        
        qry1+= " WHEN 275 THEN 'set'";                                            
        qry1+= " WHEN 276 THEN 'multiset'";                                       
        qry1+= " WHEN 277 THEN 'list'";                                           
        qry1+= " WHEN 278 THEN 'row'";                                            
        qry1+= " WHEN 279 THEN 'collection'";                                     
        qry1+= " WHEN 280 THEN 'rowdef'";                                         
        qry1+= " ELSE CAST(paramtype AS CHAR(10))";                                          
        qry1+= " END  data_type, spc.paramattr  as direction  from sysprocedures sp,sysproccolumns spc where sp.procid=spc.procid and";
        if (qualifiers.length == 2) {
            qry1 += " lower(sp.owner)=? and lower(sp.procname)=? ";
        } else {
            qry1 += " (lower(sp.procname)=?)";
        }
        qry1 += " order by sp.owner";
        
        System.out.println("Procedure is ="+qry1);
      /*  System.out.println("Excecuting procedure");
        for (int i = 0; i < qualifiers.length; i++) {

            System.out.println("proc parametersare ="+qualifiers[i]);
        } */

        return readIntoParams(qualifiers, qry1);
    }
//select sc.colname as column_name, 'VARCHAR'  as data_type    //,'P' as direction from systables st,syscolumns sc where //st.tabid=sc.tabid and lower(st.tabname)='test_dbfit'
    
}


