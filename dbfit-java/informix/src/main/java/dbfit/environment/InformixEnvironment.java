package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.NameNormaliser;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

import static dbfit.util.Direction.*;

/**
 * Created by muppana on 6/5/2014.
 */
@DatabaseEnvironment(name="Informix", driver="com.informix.jdbc.IfxDriver")
public class InformixEnvironment extends AbstractDbEnvironment  {
    private boolean driverRegistered = false;
    private static String Role;

    // IfxSqliConnect ifxSqliConnection;
    Savepoint savepoint;


    public InformixEnvironment(String driverClassName) {

        super(driverClassName);
        System.out.println("driverClassName"+driverClassName);

    }



    @Override
    public void connect(String connectionString, Properties info) throws SQLException {
        registerDriver();
        currentConnection = DriverManager.getConnection(connectionString, info);
        if(connectionString.contains("sentryprod")){
            currentConnection.setAutoCommit(true);
            Role="dontdo";
        }else{
            currentConnection.setAutoCommit(false);
        }


        System.out.println(" currentConnection.setAutoCommit(true);");

        afterConnectionEstablished();
    }

    @Override
    public void commit() throws SQLException {
        //Savepoint savepoint=currentConnection.setSavepoint("Test");
        //StatementExecution.Savepoint savepoints= (StatementExecution.Savepoint) currentConnection.setSavepoint("Test");
        //currentConnection.releaseSavepoint((Savepoint) savepoint);
        //currentConnection.setTransactionIsolation(2);
        currentConnection.commit();

        //StatementExecution.Savepoint  sss;
        // new StatementExecution.Savepoint((Connection) ifxSqliConnection.setSavepoint("test"));

        //currentConnection.setAutoCommit(false);


    }
    // StatementExecution. Savepoint()
    @Override
    public void rollback() throws SQLException {
       System.out.println("Rolling back");

        if(Role.equals("dontdo")){
            System.out.println("DontDo");
            currentConnection.setAutoCommit(true);
        }else {
            currentConnection.rollback();
            currentConnection.setAutoCommit(false);
        }   }





    private void registerDriver() throws SQLException {
        String driverName = getDriverClassName();
        try {
            if (driverRegistered)
                return;
            DriverManager.registerDriver((Driver) Class.forName(driverName)
                    .newInstance());
            driverRegistered = true;
        } catch (Exception e) {
            throw new Error("Cannot register SQL driver " + driverName);
        }
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
        System.out.println("getConnection="+dataSource);
        return "jdbc:informix-sqli://" + dataSource;
    }

    protected String getConnectionString(String dataSource, String database) {
        System.out.println("getConnection=" + dataSource + "/" + database);
        return "jdbc:informix-sqli://" + dataSource + "/" + database;
    }

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
            throws SQLException {
        String[] qualifiers = NameNormaliser.normaliseName(tableOrViewName)
                .split("\\.");
      /* String qry = "SELECT first 10 * from sentrycf.scprofil where ";

        if (qualifiers.length == 2) {
            qry += " lower(tabschema)=? and lower(tabname)=? ";
        } else {
            qry += " (lower(tabname)=?)";
        }
        qry += " order by colname"; */

        String qry = "  select sc.colname as column_name,'VARCHAR' as data_type ,'P' as direction from systables st,syscolumns sc,sysdefaults sd  where st.tabid=sc.tabid and sc.colno=sd.colno and ";
        if (qualifiers.length == 2) {
            qry += " lower(st.owner)=? and lower(st.tabname)=? ";
        } else {
            qry += " (lower(st.tabname)=?)";
        }
        qry += " order by st.owner";
        return readIntoParams(qualifiers, qry);
    }

    private Map<String, DbParameterAccessor> readIntoParams(
            String[] queryParameters, String query) throws SQLException {
        // currentConnection.setAutoCommit(false);
        System.out.println("query="+query);
        PreparedStatement dc = currentConnection.prepareStatement(query);
        try {
            for (int i = 0; i < queryParameters.length; i++) {
                dc.setString(i + 1,
                        NameNormaliser.normaliseName(queryParameters[i]));
                System.out.println("parametersare ="+queryParameters[i]);
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

    private static Direction getParameterDirection(String direction) {
        if ("P".equals(direction))
            return INPUT;
        if ("O".equals(direction))
            return OUTPUT;
        if ("B".equals(direction))
            return INPUT_OUTPUT;
        if ("C".equals(direction))
            return RETURN_VALUE;
        // todo return val
        throw new UnsupportedOperationException("Direction " + direction
                + " is not supported");
    }

    // List interface has sequential search, so using list instead of array to
    // map types
    private static List<String> stringTypes = Arrays.asList(new String[] {
            "VARCHAR","VARCHAR2","LVARCHAR", "CHAR", "CHARACTER", "GRAPHIC", "VARGRAPHIC" });
    private static List<String> intTypes = Arrays.asList(new String[] {
            "SMALLINT", "INT", "INTEGER" });
    private static List<String> longTypes = Arrays
            .asList(new String[] { "BIGINT" });
    private static List<String> floatTypes = Arrays
            .asList(new String[] { "FLOAT", "REAL" });
    private static List<String> doubleTypes = Arrays
            .asList(new String[] { "DOUBLE" });
    private static List<String> decimalTypes = Arrays.asList(new String[] {
            "DECIMAL", "DEC", "DECFLOAT", "NUMERIC" });
    private static List<String> dateTypes = Arrays
            .asList(new String[] { "DATE" });
    private static List<String> timestampTypes = Arrays
            .asList(new String[] { "TIMESTAMP" ,"DATETIME YEAR TO FRACTION(5)"});

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
        String qry = "  select procname as column_name,owner as data_type ,numargs as direction from sysprocedures where";
        if (qualifiers.length == 2) {
            qry += " lower(owner)=? and lower(procname)=? ";
        } else {
            qry += " (lower(procname)=?)";
        }
        qry += " order by owner";
        System.out.println("Excecuting procedure");
        for (int i = 0; i < qualifiers.length; i++) {

            System.out.println("proc parametersare ="+qualifiers[i]);
        }

        return readIntoParams(qualifiers, qry);
    }
}

