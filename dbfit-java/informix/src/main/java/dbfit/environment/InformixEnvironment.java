package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.fixture.StatementExecution;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DdlStatementExecution;
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
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@DatabaseEnvironment(name="Informix", driver="com.informix.jdbc.IfxDriver")
public class InformixEnvironment extends AbstractDbEnvironment {

    public InformixEnvironment(String driverClassName) {
        super(driverClassName);
        dbfitToJDBCTransformers.setTransformer(dbfit.util.NormalisedBigDecimal.class, new InformixBigDecimalSpecifier());
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

        String qry = "SELECT c." + (forProcedureParameters ? "paramname" : "colname")
                   + "           AS object_name"
                   + "     , CASE ("
                   + "            CASE WHEN c." + (forProcedureParameters ? "paramtype" : "coltype") + " >= 256"
                   + "                 THEN c." + (forProcedureParameters ? "paramtype" : "coltype") + " - 256"
                   + "                 ELSE c." + (forProcedureParameters ? "paramtype" : "coltype")
                   + "             END"
                   + "            )"
                   + "            WHEN    0 THEN 'CHAR'"
                   + "            WHEN    1 THEN 'SMALLINT'"
                   + "            WHEN    2 THEN 'INTEGER'"
                   + "            WHEN    3 THEN 'FLOAT'"
                   + "            WHEN    4 THEN 'SMALLFLOAT'"
                   + "            WHEN    5 THEN 'DECIMAL'"
                   + "            WHEN    6 THEN 'SERIAL'"
                   + "            WHEN    7 THEN 'DATE'"
                   + "            WHEN    8 THEN 'MONEY'"
                   + "            WHEN    9 THEN 'NULL'"
                   + "            WHEN   10 THEN 'DATETIME ' || CASE BITAND(c." + (forProcedureParameters ? "paramtype" : "coltype") + ", 240)"
                   + "                                               WHEN  0 THEN 'YEAR'"
                   + "                                               WHEN  2 THEN 'MONTH'"
                   + "                                               WHEN  4 THEN 'DAY'"
                   + "                                               WHEN  6 THEN 'HOUR'"
                   + "                                               WHEN  8 THEN 'MINUTE'"
                   + "                                               WHEN 10 THEN 'SECOND'"
                   + "                                               WHEN 11 THEN 'FRACTION(1)'"
                   + "                                               WHEN 12 THEN 'FRACTION(2)'"
                   + "                                               WHEN 13 THEN 'FRACTION(3)'"
                   + "                                               WHEN 14 THEN 'FRACTION(4)'"
                   + "                                               WHEN 15 THEN 'FRACTION(5)'"
                   + "                                           END"
                   + "                                       || ' TO '"
                   + "                                       || CASE BITAND(c." + (forProcedureParameters ? "paramtype" : "coltype") + ", 15)"
                   + "                                               WHEN  0 THEN 'YEAR'"
                   + "                                               WHEN  2 THEN 'MONTH'"
                   + "                                               WHEN  4 THEN 'DAY'"
                   + "                                               WHEN  6 THEN 'HOUR'"
                   + "                                               WHEN  8 THEN 'MINUTE'"
                   + "                                               WHEN 10 THEN 'SECOND'"
                   + "                                               WHEN 11 THEN 'FRACTION(1)'"
                   + "                                               WHEN 12 THEN 'FRACTION(2)'"
                   + "                                               WHEN 13 THEN 'FRACTION(3)'"
                   + "                                               WHEN 14 THEN 'FRACTION(4)'"
                   + "                                               WHEN 15 THEN 'FRACTION(5)'"
                   + "                                           END"
                   + "            WHEN   11 THEN 'BYTE'"
                   + "            WHEN   12 THEN 'TEXT'"
                   + "            WHEN   13 THEN 'VARCHAR'"
                   + "            WHEN   14 THEN 'INTERVAL'"
                   + "            WHEN   15 THEN 'NCHAR'"
                   + "            WHEN   16 THEN 'NVARCHAR'"
                   + "            WHEN   17 THEN 'INT8'"
                   + "            WHEN   18 THEN 'SERIAL8'"
                   + "            WHEN   19 THEN 'SET'"
                   + "            WHEN   20 THEN 'MULTISET'"
                   + "            WHEN   21 THEN 'LIST'"
                   + "            WHEN   22 THEN 'ROW'"
                   + "            WHEN   23 THEN 'COLLECTION'"
                   + "            WHEN   24 THEN 'ROWDEF'"
                   + "            WHEN   40 THEN 'LVARCHAR'"
                   + "            WHEN   43 THEN 'LVARCHAR'"
                   + "            WHEN   45 THEN 'BOOLEAN'"
                   + "            WHEN   52 THEN 'BIGINT'"
                   + "            WHEN   53 THEN 'BIGSERIAL'"
                   + "            WHEN 2061 THEN 'IDSSECURITYLABEL'"
                   + "            WHEN 4118 THEN 'ROW'"
                   + "        END"
                   + "           AS data_type"
                   + "     , " + (forProcedureParameters ? "c.paramattr" : "1") // Dummy value for table/view columns.
                   + "           AS direction"
                   + "     , c." + (forProcedureParameters ? "paramid" : "colno")
                   + "           AS position"
                   + "  FROM informix." + (forProcedureParameters ? "sysprocedures p" : "systables p")
                   + "     , informix." + (forProcedureParameters ? "sysproccolumns c" : "syscolumns c")
                   + " WHERE p." + (forProcedureParameters ? "procid" : "tabid") + " = c." + (forProcedureParameters ? "procid" : "tabid")
                   + "   AND ";

        if (qualifiers.length == 2) {
            qry += "LOWER(p.owner) = ? AND ";
        }
        qry += "LOWER(p." + (forProcedureParameters ? "procname" : "tabname") + ") = ?";

        return qry;
    }

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
            throws SQLException {
        String[] qualifiers = NameNormaliser.normaliseName(tableOrViewName).split("\\.");
        return readIntoParams(qualifiers, columnsOrParamtersQueryText(false, tableOrViewName));
    }

    private Map<String, DbParameterAccessor> readIntoParams(String[] queryParameters, String query) throws SQLException {
        try (PreparedStatement dc = currentConnection.prepareStatement(query)) {
            for (int i = 0; i < queryParameters.length; i++) {
                dc.setString(i + 1, NameNormaliser.normaliseName(queryParameters[i]));
            }

            ResultSet rs = dc.executeQuery();
            Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();

            while (rs.next()) {
                String paramName = defaultIfNull(rs.getString(1), "");
                String dataType = rs.getString(2);
                String direction = rs.getString(3);
                int position = rs.getInt(4);
                Direction paramDirection = getParameterDirection(direction);
                if (paramDirection != RETURN_VALUE && paramName.isEmpty()) {
                    throw new SQLException("Missing column or procedure parameter name");
                }
                DbParameterAccessor dbp = createDbParameterAccessor(paramName,
                                                                    paramDirection,
                                                                    getSqlType(dataType),
                                                                    getJavaClass(dataType),
                                                                    paramDirection == RETURN_VALUE ? -1 : position);
                allParams.put(NameNormaliser.normaliseName(paramName), dbp);
            }
            rs.close();
            return allParams;
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
        throw new UnsupportedOperationException("Direction " + direction + " is not supported");
    }

    // List interface has sequential search, so using list instead of array to map types.
    private static List<String> stringTypes = Arrays.asList(
            "VARCHAR", "LVARCHAR", "CHAR", "TEXT", "NCHAR", "NVARCHAR");
    private static List<String> shortTypes = Arrays.asList(
            "SMALLINT");
    private static List<String> intTypes = Arrays.asList(
            "INT", "INTEGER", "SERIAL");
    private static List<String> longTypes = Arrays.asList(
            "BIGINT", "INT8", "BIGSERIAL", "SERIAL8");
    private static List<String> floatTypes = Arrays.asList(
            "SMALLFLOAT");
    private static List<String> doubleTypes = Arrays.asList(
            "FLOAT");
    private static List<String> decimalTypes = Arrays.asList(
            "DECIMAL", "MONEY");
    private static List<String> dateTypes = Arrays.asList(
            "DATE");
    private static List<String> timestampTypes = Arrays.asList(
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
            "DATETIME SECOND TO FRACTION(1)", "DATETIME SECOND TO FRACTION(2)", "DATETIME SECOND TO FRACTION(3)", "DATETIME SECOND TO FRACTION(4)", "DATETIME SECOND TO FRACTION(5)");
    private static List<String> timeTypes = Arrays.asList(
            "DATETIME HOUR TO HOUR", "DATETIME HOUR TO MINUTE", "DATETIME HOUR TO SECOND",
            "DATETIME MINUTE TO MINUTE", "DATETIME MINUTE TO SECOND", 
            "DATETIME SECOND TO SECOND");
    private static List<String> binaryTypes = Arrays.asList(
            "BYTE");
    private static List<String> booleanTypes = Arrays.asList(
            "BOOLEAN");

    private static String normaliseTypeName(String dataType) {
        dataType = dataType.toUpperCase().trim();
        return dataType;
    }

    private static int getSqlType(String dataType) {
        // todo:strip everything from first blank
        dataType = normaliseTypeName(dataType);

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
        dataType = normaliseTypeName(dataType);
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

    @Override
    public StatementExecution createFunctionStatementExecution(PreparedStatement statement, boolean clearParameters) {
        return new InformixFunctionStatementExecution(statement, clearParameters);
    }
}
