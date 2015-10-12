package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.NameNormaliser;
import dbfit.util.Options;
import dbfit.util.TypeNormaliserFactory;
import fit.TypeAdapter;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

import static dbfit.util.Direction.*;

@DatabaseEnvironment(name="Informix", driver="com.informix.jdbc.IfxDriver")
public class InformixEnvironment extends AbstractDbEnvironment  {

    @Override
    public void afterConnectionEstablished() throws SQLException {
        TypeAdapter.registerParseDelegate(java.math.BigDecimal.class, new dbfit.util.BigDecimalParseDelegate());
        if (currentConnection.getMetaData().supportsTransactions()) {
            Options.setOption(Options.OPTION_AUTO_COMMIT, "false");
            currentConnection.setAutoCommit(false);
        } else {
            Options.setOption(Options.OPTION_AUTO_COMMIT, "true");
            currentConnection.setAutoCommit(true);
        }
    }

    @Override
    public void connect(String connectionString, Properties info) throws SQLException {
        currentConnection = DriverManager.getConnection(connectionString, info);
        afterConnectionEstablished();
    }

    public InformixEnvironment(String driverClassName) {
        super(driverClassName);
        //TypeNormaliserFactory.setNormaliser(com.informix.jdbc.IfxDate.class, new InformixDateNormalizer());
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
        return "jdbc:informix-sqli://" + dataSource;
    }

    protected String getConnectionString(String dataSource, String database) {
        return "jdbc:informix-sqli://" + dataSource + "/" + database;
    }

    private String columnsOrParamtersQueryText(Boolean forProcedureParameters, String objectName) {
        String[] qualifiers = NameNormaliser.normaliseName(objectName).split("\\.");

        String qry = "";
        qry += "SELECT c." + (forProcedureParameters ? "paramname" : "colname");
        qry += "           AS object_name";
        qry += "     , CASE (";
        qry += "            CASE WHEN c." + (forProcedureParameters ? "paramtype" : "coltype") + " >= 256";
        qry += "                 THEN c." + (forProcedureParameters ? "paramtype" : "coltype") + " - 256";
        qry += "                 ELSE c." + (forProcedureParameters ? "paramtype" : "coltype");
        qry += "             END";
        qry += "            )";
        qry += "            WHEN    0 THEN 'CHAR'";
        qry += "            WHEN    1 THEN 'SMALLINT'";
        qry += "            WHEN    2 THEN 'INTEGER'";
        qry += "            WHEN    3 THEN 'FLOAT'";
        qry += "            WHEN    4 THEN 'SMALLFLOAT'";
        qry += "            WHEN    5 THEN 'DECIMAL'";
        qry += "            WHEN    6 THEN 'SERIAL'";
        qry += "            WHEN    7 THEN 'DATE'";
        qry += "            WHEN    8 THEN 'MONEY'";
        qry += "            WHEN    9 THEN 'NULL'";
        qry += "            WHEN   10 THEN 'DATETIME ' || CASE BITAND(c." + (forProcedureParameters ? "paramtype" : "coltype") + ", 240)";
        qry += "                                               WHEN  0 THEN 'YEAR'";
        qry += "                                               WHEN  2 THEN 'MONTH'";
        qry += "                                               WHEN  4 THEN 'DAY'";
        qry += "                                               WHEN  6 THEN 'HOUR'";
        qry += "                                               WHEN  8 THEN 'MINUTE'";
        qry += "                                               WHEN 10 THEN 'SECOND'";
        qry += "                                               WHEN 11 THEN 'FRACTION(1)'";
        qry += "                                               WHEN 12 THEN 'FRACTION(2)'";
        qry += "                                               WHEN 13 THEN 'FRACTION(3)'";
        qry += "                                               WHEN 14 THEN 'FRACTION(4)'";
        qry += "                                               WHEN 15 THEN 'FRACTION(5)'";
        qry += "                                           END";
        qry += "                                       || ' TO '";
        qry += "                                       || CASE BITAND(c." + (forProcedureParameters ? "paramtype" : "coltype") + ", 15)";
        qry += "                                               WHEN  0 THEN 'YEAR'";
        qry += "                                               WHEN  2 THEN 'MONTH'";
        qry += "                                               WHEN  4 THEN 'DAY'";
        qry += "                                               WHEN  6 THEN 'HOUR'";
        qry += "                                               WHEN  8 THEN 'MINUTE'";
        qry += "                                               WHEN 10 THEN 'SECOND'";
        qry += "                                               WHEN 11 THEN 'FRACTION(1)'";
        qry += "                                               WHEN 12 THEN 'FRACTION(2)'";
        qry += "                                               WHEN 13 THEN 'FRACTION(3)'";
        qry += "                                               WHEN 14 THEN 'FRACTION(4)'";
        qry += "                                               WHEN 15 THEN 'FRACTION(5)'";
        qry += "                                           END";
        qry += "            WHEN   11 THEN 'BYTE'";
        qry += "            WHEN   12 THEN 'TEXT'";
        qry += "            WHEN   13 THEN 'VARCHAR'";
        qry += "            WHEN   14 THEN 'INTERVAL'";
        qry += "            WHEN   15 THEN 'NCHAR'";
        qry += "            WHEN   16 THEN 'NVARCHAR'";
        qry += "            WHEN   17 THEN 'INT8'";
        qry += "            WHEN   18 THEN 'SERIAL8'";
        qry += "            WHEN   19 THEN 'SET'";
        qry += "            WHEN   20 THEN 'MULTISET'";
        qry += "            WHEN   21 THEN 'LIST'";
        qry += "            WHEN   22 THEN 'ROW'";
        qry += "            WHEN   23 THEN 'COLLECTION'";
        qry += "            WHEN   24 THEN 'ROWDEF'";
        qry += "            WHEN   40 THEN 'LVARCHAR'";
        qry += "            WHEN   43 THEN 'LVARCHAR'";
        qry += "            WHEN   45 THEN 'BOOLEAN'";
        qry += "            WHEN   52 THEN 'BIGINT'";
        qry += "            WHEN   53 THEN 'BIGSERIAL'";
        qry += "            WHEN 2061 THEN 'IDSSECURITYLABEL'";
        qry += "            WHEN 4118 THEN 'ROW'";
        qry += "        END";
        qry += "           AS data_type";
        qry += "     , " + (forProcedureParameters ? "c.paramattr" : "1"); // Dummy value for table/view columns.
        qry += "           AS direction";
        qry += "     , c." + (forProcedureParameters ? "paramid" : "colno");
        qry += "           AS position";
        qry += "  FROM informix." + (forProcedureParameters ? "sysprocedures p" : "systables p");
        qry += "     , informix." + (forProcedureParameters ? "sysproccolumns c" : "syscolumns c");
        qry += " WHERE p." + (forProcedureParameters ? "procid" : "tabid") + " = c." + (forProcedureParameters ? "procid" : "tabid");
        qry += "   AND ";

        if (qualifiers.length == 2) {
            qry += "LOWER(p.owner) = ? AND LOWER(p." + (forProcedureParameters ? "procname" : "tabname") + ") = ?";
        } else {
            qry += "LOWER(p." + (forProcedureParameters ? "procname" : "tabname") + ") = ?";
        }

        return qry;
    }

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
            throws SQLException {
        String[] qualifiers = NameNormaliser.normaliseName(tableOrViewName).split("\\.");
        return readIntoParams(qualifiers, columnsOrParamtersQueryText(false, tableOrViewName));
    }

    private Map<String, DbParameterAccessor> readIntoParams(String[] queryParameters, String query) throws SQLException {
        PreparedStatement dc = currentConnection.prepareStatement(query);
        try {
            for (int i = 0; i < queryParameters.length; i++) {
                if (queryParameters[i].length() == 0) {
                    queryParameters[i] = "return_value";
                }
                dc.setString(i + 1, NameNormaliser.normaliseName(queryParameters[i]));
            }

            ResultSet rs = dc.executeQuery();
            Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();

            while (rs.next()) {
                String paramName = rs.getString(1);
System.out.println("InformixEnvironment: readIntoParams: paramName: " + paramName);
                if (paramName == null)
                    paramName = "";
                String dataType = rs.getString(2);
System.out.println("InformixEnvironment: readIntoParams: dataType: " + dataType);
                String direction = rs.getString(3);
                int position = rs.getInt(4);
                Direction paramDirection = getParameterDirection(direction);
System.out.println("InformixEnvironment: readIntoParams: getJavaClass(dataType): " + getJavaClass(dataType).getName()); 
                DbParameterAccessor dbp = new DbParameterAccessor(paramName,
                        paramDirection, getSqlType(dataType),
                        getJavaClass(dataType),
                        paramDirection == RETURN_VALUE ? -1
                                : position);
                allParams.put(NameNormaliser.normaliseName(paramName), dbp);
            }
            rs.close();
            Iterator it = allParams.values().iterator();
            while(it.hasNext()) {
                DbParameterAccessor dbp = (DbParameterAccessor)it.next();
            }
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
        /*  0 = Parameter is of unknown type
            1 = Parameter is INPUT mode
            2 = Parameter is INOUT mode
            3 = Parameter is multiple return value
            4 = Parameter is OUT mode
            5 = Parameter is a return value
        */
        if ("1".equals(direction))
            return INPUT;
        if ("2".equals(direction))
            return INPUT_OUTPUT;
        if ("3".equals(direction))
            return RETURN_VALUE;
        if ("4".equals(direction))
            return OUTPUT;
        if ("5".equals(direction))
            return RETURN_VALUE;
        throw new UnsupportedOperationException("Direction " + direction
                + " is not supported");
    }

    // List interface has sequential search, so using list instead of array to map types.
    private static List<String> stringTypes = Arrays.asList(new String[] {
            "VARCHAR", "LVARCHAR", "CHAR", "TEXT", "NCHAR", "NVARCHAR" });
    private static List<String> shortTypes = Arrays.asList(new String[] {
            "SMALLINT" });
    private static List<String> intTypes = Arrays.asList(new String[] {
            "INT", "INTEGER", "SERIAL" });
    private static List<String> longTypes = Arrays.asList(new String[] {
            "BIGINT", "INT8", "BIGSERIAL", "SERIAL8" });
    private static List<String> floatTypes = Arrays.asList(new String[] {
            "SMALLFLOAT" });
    private static List<String> doubleTypes = Arrays.asList(new String[] {
            "FLOAT" });
    private static List<String> decimalTypes = Arrays.asList(new String[] {
            "DECIMAL", "MONEY" });
    private static List<String> dateTypes = Arrays.asList(new String[] {
            "DATE" });
    private static List<String> timestampTypes = Arrays.asList(new String[] {
            "DATETIME YEAR TO YEAR", "DATETIME YEAR TO MONTH", "DATETIME YEAR TO DAY", "DATETIME YEAR TO HOUR",
            "DATETIME YEAR TO MINUTE", "DATETIME YEAR TO SECOND",
            "DATETIME YEAR TO FRACTION(1)", "DATETIME YEAR TO FRACTION(2)", "DATETIME YEAR TO FRACTION(3)",
            "DATETIME YEAR TO FRACTION(4)","DATETIME YEAR TO FRACTION(5)",
            "DATETIME MONTH TO MONTH", "DATETIME MONTH TO DAY", "DATETIME MONTH TO HOUR",
            "DATETIME MONTH TO MINUTE", "DATETIME MONTH TO SECOND",
            "DATETIME MONTH TO FRACTION(1)", "DATETIME MONTH TO FRACTION(2)", "DATETIME MONTH TO FRACTION(3)",
            "DATETIME MONTH TO FRACTION(4)", "DATETIME MONTH TO FRACTION(5)",
            "DATETIME DAY TO DAY", "DATETIME DAY TO HOUR", "DATETIME DAY TO MINUTE", "DATETIME DAY TO SECOND",
            "DATETIME DAY TO FRACTION(1)", "DATETIME DAY TO FRACTION(2)", "DATETIME DAY TO FRACTION(3)",
            "DATETIME DAY TO FRACTION(4)", "DATETIME DAY TO FRACTION(5)", 
            "DATETIME HOUR TO FRACTION(1)", "DATETIME HOUR TO FRACTION(2)", "DATETIME HOUR TO FRACTION(3)", "DATETIME HOUR TO FRACTION(4)", "DATETIME HOUR TO FRACTION(5)",
            "DATETIME MINUTE TO FRACTION(1)", "DATETIME MINUTE TO FRACTION(2)", "DATETIME MINUTE TO FRACTION(3)", "DATETIME MINUTE TO FRACTION(4)", "DATETIME HOUR TO FRACTION(5)",
            "DATETIME SECOND TO FRACTION(1)", "DATETIME SECOND TO FRACTION(2)", "DATETIME SECOND TO FRACTION(3)", "DATETIME SECOND TO FRACTION(4)", "DATETIME SECOND TO FRACTION(5)" });
    private static List<String> timeTypes = Arrays.asList(new String[] {
            "DATETIME HOUR TO HOUR", "DATETIME HOUR TO MINUTE", "DATETIME HOUR TO SECOND",
            "DATETIME MINUTE TO MINUTE", "DATETIME MINUTE TO SECOND", 
            "DATETIME SECOND TO SECOND",  });
    private static List<String> binaryTypes = Arrays.asList(new String[] {
            "BYTE" });
    private static List<String> booleanTypes = Arrays.asList(new String[] {
            "BOOLEAN" });

    private static String NormaliseTypeName(String dataType) {
System.out.println("InformixEnvironment: NormaliseTypeName: dataType: " + dataType);
        dataType = dataType.toUpperCase().trim();
        return dataType;
    }

    private static int getSqlType(String dataType) {
        // todo:strip everything from first blank
        dataType = NormaliseTypeName(dataType);

        if (stringTypes.contains(dataType))
            return java.sql.Types.VARCHAR;
        if (decimalTypes.contains(dataType))
            return java.sql.Types.DECIMAL;
        if (shortTypes.contains(dataType))
            return java.sql.Types.SMALLINT;
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
        if (timeTypes.contains(dataType))
            return java.sql.Types.TIME;
        if (dateTypes.contains(dataType))
            return java.sql.Types.DATE;
        if (binaryTypes.contains(dataType))
            return java.sql.Types.BINARY;
        if (booleanTypes.contains(dataType))
            return java.sql.Types.BOOLEAN;
        throw new UnsupportedOperationException("Type " + dataType + " is not supported");
    }

    public Class<?> getJavaClass(String dataType) {
        dataType = NormaliseTypeName(dataType);
        if (stringTypes.contains(dataType))
            return String.class;
        if (decimalTypes.contains(dataType))
            return BigDecimal.class;
        if (intTypes.contains(dataType))
            return Integer.class;
        if (shortTypes.contains(dataType))
            return Short.class;
        if (floatTypes.contains(dataType))
            return Float.class;
        if (dateTypes.contains(dataType))
            return java.sql.Date.class;
        if (timestampTypes.contains(dataType))
            return java.sql.Timestamp.class;
        if (timeTypes.contains(dataType))
            return java.sql.Time.class;
        if (doubleTypes.contains(dataType))
            return Double.class;
        if (longTypes.contains(dataType))
            return Long.class;
        if (binaryTypes.contains(dataType))
            return byte.class;
        if (booleanTypes.contains(dataType))
            return Boolean.class;

        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {
        String[] qualifiers = NameNormaliser.normaliseName(procName).split("\\.");
        return readIntoParams(qualifiers, columnsOrParamtersQueryText(true, procName));
    }
}
