package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.*;
import fit.TypeAdapter;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@DatabaseEnvironment(name="Teradata", driver="com.teradata.jdbc.TeraDriver")
public class TeradataEnvironment extends AbstractDbEnvironment {

    public static class TeradataClobNormaliser implements TypeNormaliser {

        private static final int MAX_CLOB_LENGTH = 10000;

        public Object normalise(Object o) throws SQLException {

            if (o == null)
                return null;
            if (!(o instanceof java.sql.Clob)) {
                throw new UnsupportedOperationException(
                        "TeradataClobNormaliser cannot work with "
                                + o.getClass());
            }
            Clob clob = (Clob) o;
            if (clob.length() > MAX_CLOB_LENGTH)
                throw new UnsupportedOperationException("Clobs larger than "
                        + MAX_CLOB_LENGTH + "bytes are not supported by DBFIT");
            String buffer = clob.getSubString(1, (int) clob.length());
            return buffer;
        }
    }

    public static class TeradataPeriodNormaliser implements TypeNormaliser {

        public Object normalise(Object o) throws SQLException {

            if (o == null)
                return null;

            if (!(o instanceof java.sql.Struct)) {
                throw new UnsupportedOperationException(
                        "TeradataPeriodNormaliser cannot work with "
                                + o.getClass());
            }

            java.sql.Struct os = (java.sql.Struct) o;

            if ((!(os.getSQLTypeName() == "PERIOD(DATE)"))
                    && (!(os.getSQLTypeName() == "PERIOD(TIME)"))
                    && (!(os.getSQLTypeName() == "PERIOD(TIMESTAMP)"))
                    && (!(os.getSQLTypeName() == "PERIOD(TIMESTAMP WITH TIME ZONE)")))
                throw new SQLException(
                        "TeradataEnvironment: TeradataPeriodNormaliser: unexpected SQLTypeName ("
                                + os.getSQLTypeName()
                                + ". Expected PERIOD(DATE | TIME | TIMESTAMP | TIMESTAMP WITH TIME ZONE)");

            Object[] atts = os.getAttributes();

            String output = "";

            if (!(atts[0] == null))
                output = output + atts[0].toString();
            output = output + ",";
            if (!(atts[1] == null))
                output = output + atts[1].toString();

            Object retval = null;

            if (os.getSQLTypeName() == "PERIOD(DATE)")
                retval = new TeradataDatePeriod(atts);
            if ((os.getSQLTypeName() == "PERIOD(TIMESTAMP)")
                    || (os.getSQLTypeName() == "PERIOD(TIMESTAMP WITH TIME ZONE"))
                retval = new TeradataTimestampPeriod(atts);
            if (os.getSQLTypeName() == "PERIOD(TIME)")
                retval = new TeradataTimePeriod(atts);

            return retval;
        }
    }

    public TeradataEnvironment(String driverClassName) {
        super(driverClassName);

        TypeAdapter.registerParseDelegate(TeradataDatePeriod.class,
                TeradataDatePeriodParseDelegate.class);
        TypeAdapter.registerParseDelegate(TeradataTimestampPeriod.class,
                TeradataTimestampPeriodParseDelegate.class);
        TypeAdapter.registerParseDelegate(TeradataTimePeriod.class,
                TeradataTimePeriodParseDelegate.class);
        TypeNormaliserFactory.setNormaliser(java.sql.Clob.class,
                new TeradataClobNormaliser());
        try {
            TypeNormaliserFactory.setNormaliser(
                    Class.forName("com.teradata.jdbc.ResultStruct"),
                    new TeradataPeriodNormaliser());
        } catch (ClassNotFoundException e) {
            throw new Error(
                    "Cannot initialise Teradata result struct. Is the Teradata jar in the classpath?",
                    e);
        }
    }

    public boolean supportsOuputOnInsert() {
        return false;
    }

    protected String getConnectionString(String dataSource) {
        return "jdbc:teradata://" + dataSource + "/FINALIZE_AUTO_CLOSE=ON";
    }

    protected String getConnectionString(String dataSource, String dataBase) {
        // "jdbc:teradata://"+dataSource+"/TMODE=ANSI,DATABASE="+dataBase;
        String url = "jdbc:teradata://" + dataSource;
        if (dataBase != null) {
            url = url + "/DATABASE=" + dataBase;
        }
        url = url + ",FINALIZE_AUTO_CLOSE=ON";
        return url;
    }

    @Override
    public DdlStatementExecution createDdlStatementExecution(String ddl)
            throws SQLException {
        return new DdlStatementExecution(getConnection().createStatement(), ddl) {
            @Override
            public void run() throws SQLException {
                super.run();
                getConnection().commit();
            }
        };
    }

    private static String paramNamePattern = ":([A-Za-z0-9_]+)";
    private static Pattern paramsNames = Pattern.compile(":([A-Za-z0-9_]+)");

    public Pattern getParameterPattern() {
        return paramsNames;
    }

    protected String parseCommandText(String commandText) {
        commandText = commandText.replaceAll(paramNamePattern, "?");
        return super.parseCommandText(commandText);
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {

        String[] qualifiers = procName.split("\\.");

        //Great resource: http://stackoverflow.com/questions/21587034/get-column-type-using-teradata-system-tables
        String cols = "CASE WHEN TRIM(columnname) = 'RETURN0' AND spparametertype = 'O' ";
        cols = cols + "THEN '' ";
        cols = cols + "ELSE TRIM(TRAILING FROM columnname) ";
        cols = cols + "END AS columnname, ";
        cols = cols + "CASE ";
        cols = cols + "WHEN c.columntype IN ('CF') THEN 'CHAR' ";
        cols = cols + "WHEN c.columntype IN ('CV') THEN 'VARCHAR' ";
        cols = cols + "WHEN c.columntype IN ('CO') THEN 'CLOB' ";
        cols = cols + "WHEN c.columntype IN ('I8') THEN 'LONG' ";
        cols = cols + "WHEN c.columntype IN ('I') THEN 'INTEGER' ";
        cols = cols + "WHEN c.columntype IN ('I2') THEN 'SMALLINT' ";
        cols = cols + "WHEN c.columntype IN ('I1') THEN 'BYTEINT' ";
        cols = cols + "WHEN c.columntype IN ('D') THEN 'DECIMAL' ";
        cols = cols + "WHEN c.columntype IN ('N') THEN 'NUMBER' ";
        cols = cols + "WHEN c.columntype IN ('F') THEN 'FLOAT' ";
        cols = cols + "WHEN c.columntype IN ('DA') THEN 'DATE' ";
        cols = cols + "WHEN c.columntype IN ('TS') THEN 'TIMESTAMP' ";
        cols = cols + "WHEN c.columntype IN ('TI') THEN 'TIME' ";
        cols = cols + "WHEN c.columntype IN ('BF') THEN 'BINARY' ";
        cols = cols + "WHEN c.columntype IN ('BV') THEN 'VARBINARY' ";
        cols = cols + "WHEN c.columntype IN ('PD') THEN 'PERIOD(DATE)' ";
        cols = cols + "WHEN c.columntype IN ('PT') THEN 'PERIOD(TIME)' ";
        cols = cols + "WHEN c.columntype IN ('PS') THEN 'PERIOD(TIMESTAMP)' ";
        cols = cols
                + "WHEN c.columntype IN ('PM') THEN 'PERIOD(TIMESTAMP WITH TIME ZONE)' ";
        cols = cols + "END AS colmntype, ";
        cols = cols + "columnlength, ";
        cols = cols
                + "CASE spparametertype WHEN 'I' THEN 'IN' WHEN 'O' THEN 'OUT' WHEN 'B' THEN 'IN/OUT' ";
        cols = cols + "END AS paramdirection";

        String qry = "SELECT " + cols + "  FROM dbc.columns c " + "WHERE ";
        if (qualifiers.length == 2) {
            qry += "TRIM(TRAILING FROM c.databasename) = TRIM(TRAILING FROM ?) AND TRIM(TRAILING FROM c.tablename) = TRIM(TRAILING FROM ?)";
        } else {
            // User names are always stored as upper case. For ANSI mode this is significant.
            qry += "TRIM(TRAILING FROM UPPER(c.databasename))= USER AND TRIM(TRAILING FROM c.tablename) = TRIM(TRAILING FROM ?)";
        }

        qry += " order by c.columnid";

        return readIntoParams(qualifiers, qry);
    }

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
            throws SQLException {

        String[] qualifiers = tableOrViewName.split("\\.");

        //Great resource: http://stackoverflow.com/questions/21587034/get-column-type-using-teradata-system-tables
        String cols = "TRIM(TRAILING FROM columnname) AS columnname, CASE ";
        cols = cols + "WHEN c.columntype IN ('CF') THEN 'CHAR' ";
        cols = cols + "WHEN c.columntype IN ('CV') THEN 'VARCHAR' ";
        cols = cols + "WHEN c.columntype IN ('CO') THEN 'CLOB' ";
        cols = cols + "WHEN c.columntype IN ('BO') THEN 'BLOB' ";
        cols = cols + "WHEN c.columntype IN ('I8') THEN 'BIGINT' ";
        cols = cols + "WHEN c.columntype IN ('I') THEN 'INTEGER' ";
        cols = cols + "WHEN c.columntype IN ('I2') THEN 'SMALLINT' ";
        cols = cols + "WHEN c.columntype IN ('I1') THEN 'BYTEINT' ";
        cols = cols + "WHEN c.columntype IN ('D') THEN 'DECIMAL' ";
        cols = cols + "WHEN c.columntype IN ('N') THEN 'NUMBER' ";
        cols = cols + "WHEN c.columntype IN ('F') THEN 'FLOAT' ";
        cols = cols + "WHEN c.columntype IN ('DA') THEN 'DATE' ";
        cols = cols + "WHEN c.columntype IN ('TS', 'SZ') THEN 'TIMESTAMP' ";
        cols = cols + "WHEN c.columntype IN ('AT', 'TZ') THEN 'TIME' ";
        cols = cols + "WHEN c.columntype IN ('BF') THEN 'BYTE' ";
        cols = cols + "WHEN c.columntype IN ('BV') THEN 'VARBINARY' ";
        cols = cols + "WHEN c.columntype IN ('PD') THEN 'PERIOD(DATE)' ";
        cols = cols + "WHEN c.columntype IN ('PT') THEN 'PERIOD(TIME)' ";
        cols = cols + "WHEN c.columntype IN ('PS') THEN 'PERIOD(TIMESTAMP)' ";
        cols = cols
                + "WHEN c.columntype IN ('PM') THEN 'PERIOD(TIMESTAMP WITH TIME ZONE)' ";
        cols = cols + "END AS columntype, ";
        cols = cols + "columnlength, ";
        cols = cols + "'IN' AS paramdirection";

        String qry = "SELECT " + cols + " FROM dbc.columns c " + "WHERE ";
        if (qualifiers.length == 2) {
            qry += "TRIM(TRAILING FROM c.databasename) = TRIM(TRAILING FROM ?) AND TRIM(TRAILING FROM c.tablename) = TRIM(TRAILING FROM ?)";
        } else {
            // User names are always stored as upper case. For ANSI mode this is significant.
            qry += "TRIM(TRAILING FROM UPPER(c.databasename)) = USER AND TRIM(TRAILING FROM c.tablename) = TRIM(TRAILING FROM ?)";
        }
        qry += " order by c.columnid ";
        return readIntoParams(qualifiers, qry);
    }

    private Map<String, DbParameterAccessor> readIntoParams(
            String[] queryParameters, String query) throws SQLException {

        try (CallableStatement dc = currentConnection.prepareCall(query)) {

            for (int i = 0; i < queryParameters.length; i++) {
                dc.setString(i + 1, queryParameters[i]);
            }

            ResultSet rs = dc.executeQuery();
            Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();
            int position = 0;

            while (rs.next()) {

                String paramName = rs.getString(1);

                if (paramName == null) {
                    paramName = ""; // Function return values get empty parameter
                                    // names.
                }
                if (paramName.equals("")) {
                    paramName = ""; // Function return values get empty parameter
                                    // names.
                }
                String dataType = rs.getString(2);
                String direction = rs.getString(4);
                Direction paramDirection;
                if (paramName.trim().equals("")) {
                    paramDirection = Direction.RETURN_VALUE;
                } else {
                    paramDirection = getParameterDirection(direction);
                }

                int intSqlType = getSqlType(dataType);
                Class<?> clsJavaClass = getJavaClass(dataType);
                DbParameterAccessor dbp = new DbParameterAccessor(paramName,
                        paramDirection, intSqlType, clsJavaClass,
                        paramDirection == Direction.RETURN_VALUE ? -1
                                : position++);
                // Note that the HashMap key case must match the normalised name access by DbTable.getDbParameterAccessor.
                allParams.put(NameNormaliser.normaliseName(paramName), dbp);
            }
            return allParams;
        }
    }

    // List interface has sequential search, so using list instead of array to
    // map types
    private static List<String> stringTypes = Arrays.asList(new String[] {
            "VARCHAR", "CHAR", "CLOB" });
    private static List<String> longTypes = Arrays
            .asList(new String[] { "BIGINT" });
    private static List<String> intTypes = Arrays
            .asList(new String[] { "INTEGER" });
    private static List<String> byteTypes = Arrays
            .asList(new String[] { "BYTEINT" });
    private static List<String> shortTypes = Arrays
            .asList(new String[] { "SMALLINT" });
    private static List<String> decimalTypes = Arrays
            .asList(new String[] { "DECIMAL", "NUMBER" });
    private static List<String> doubleTypes = Arrays
            .asList(new String[] { "FLOAT" });
    private static List<String> dateTypes = Arrays
            .asList(new String[] { "DATE" });
    private static List<String> timestampTypes = Arrays
            .asList(new String[] { "TIMESTAMP", "TIMESTAMP WITH TIME ZONE" });
    private static List<String> timeTypes = Arrays
            .asList(new String[] { "TIME", "TIME WITH TIME ZONE" });
    private static List<String> datePeriodTypes = Arrays
            .asList(new String[] { "PERIOD(DATE)" });
    private static List<String> timePeriodTypes = Arrays
            .asList(new String[] { "PERIOD(TIME)", "PERIOD(TIME WITH TIME ZONE)" });
    private static List<String> timestampPeriodTypes = Arrays
            .asList(new String[] { "PERIOD(TIMESTAMP)",
                    "PERIOD(TIMESTAMP WITH TIME ZONE)" });
    private static List<String> binaryTypes = Arrays
            .asList(new String[] { "BYTE" });
    private static List<String> varBinaryTypes = Arrays
            .asList(new String[] { "VARBINARY" });

    private static String normaliseTypeName(String dataType) {

        dataType = dataType.toUpperCase().trim();

        int idx = 0;

        if ((!datePeriodTypes.contains(dataType))
                && (!timePeriodTypes.contains(dataType))
                && (!timestampPeriodTypes.contains(dataType))) {

            idx = dataType.indexOf(" ");
            if (idx >= 0)
                dataType = dataType.substring(0, idx);

            idx = dataType.indexOf("(");
            if (idx >= 0)
                dataType = dataType.substring(0, idx);
        }

        return dataType;
    }

    private static int getSqlType(String dataType) {
        // todo:strip everything from first blank
        dataType = normaliseTypeName(dataType);

        if (stringTypes.contains(dataType))
            return java.sql.Types.VARCHAR;
        if (longTypes.contains(dataType))
            return java.sql.Types.BIGINT;
        if (intTypes.contains(dataType))
            return java.sql.Types.INTEGER;
        if (byteTypes.contains(dataType))
            return java.sql.Types.INTEGER;
        if (shortTypes.contains(dataType))
            return java.sql.Types.INTEGER;
        if (decimalTypes.contains(dataType))
            return java.sql.Types.NUMERIC;
        if (doubleTypes.contains(dataType))
            return java.sql.Types.DOUBLE;
        if (dateTypes.contains(dataType))
            return java.sql.Types.DATE;
        if (timestampTypes.contains(dataType))
            return java.sql.Types.TIMESTAMP;
        if (timeTypes.contains(dataType))
            return java.sql.Types.TIME;
        if (binaryTypes.contains(dataType))
            return java.sql.Types.BINARY;
        if (varBinaryTypes.contains(dataType))
            return java.sql.Types.VARBINARY;
        if (datePeriodTypes.contains(dataType))
            return java.sql.Types.STRUCT;
        if (timePeriodTypes.contains(dataType))
            return java.sql.Types.STRUCT;
        if (timestampPeriodTypes.contains(dataType))
            return java.sql.Types.STRUCT;

        throw new UnsupportedOperationException(
                "TeradataEnvironment: getSqlType: Type " + dataType
                        + " is not supported");
    }

    @Override
    public Class<?> getJavaClass(String dataType) {

        dataType = normaliseTypeName(dataType);

        // Be sure to align the returned Class types with those returned
        // by ResultSetMetaData.getColumnTypeName.

        if (stringTypes.contains(dataType))
            return String.class;
        if (longTypes.contains(dataType))
            return Long.class;
        if (intTypes.contains(dataType))
            return Integer.class;
        if (byteTypes.contains(dataType))
            return Integer.class;
        if (shortTypes.contains(dataType))
            return Integer.class;
        if (doubleTypes.contains(dataType))
            return Double.class;
        if (decimalTypes.contains(dataType))
            return BigDecimal.class;
        if (dateTypes.contains(dataType))
            return java.sql.Date.class;
        if (timestampTypes.contains(dataType))
            return java.sql.Timestamp.class;
        if (timeTypes.contains(dataType))
            return java.sql.Time.class;
        if (datePeriodTypes.contains(dataType))
            return TeradataDatePeriod.class;
        if (timePeriodTypes.contains(dataType))
            return TeradataTimePeriod.class;
        if (timestampPeriodTypes.contains(dataType))
            return TeradataTimestampPeriod.class;

        throw new UnsupportedOperationException(
                "TeradataEnvironment: getJavaClass: Type " + dataType
                        + " is not supported");
    }

    private static Direction getParameterDirection(String direction) {
        if ("IN".equals(direction))
            return Direction.INPUT;
        if ("OUT".equals(direction))
            return Direction.OUTPUT;
        if ("IN/OUT".equals(direction))
            return Direction.INPUT_OUTPUT;
        // todo return val
        throw new UnsupportedOperationException(
                "TeradataEnvironment: Direction " + direction
                        + " is not supported");
    }
}
