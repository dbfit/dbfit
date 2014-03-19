package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.NameNormaliser;
import dbfit.util.TypeNormaliserFactory;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Properties;

@DatabaseEnvironment(name="SqlServer", driver="com.microsoft.sqlserver.jdbc.SQLServerDriver")
public class SqlServerEnvironment extends AbstractDbEnvironment {

    public SqlServerEnvironment(String driverClassName) {
        super(driverClassName);

        TypeNormaliserFactory.setNormaliser(java.sql.Time.class,
                new MillisecondTimeNormaliser());
    }

    public boolean supportsOuputOnInsert() {
        return false;
    }

    private String getInstanceString(String s) {

        int idx = s.indexOf('\\');
        if (idx > 0) {
            throw new UnsupportedOperationException(
                    "Java SQL Server Driver does not work with instance names. "
                            + "Create an alias for your SQL Server Instance.");
            // String server = s.substring(0, idx);
            // String instance = s.substring(idx + 1);
            // System.out.println(server + ";instanceName=" + instance);
            // return "localhost;instanceName=" + instance;
        }
        return s;
    }

    @Override
    protected String getConnectionString(String dataSource) {
        return "jdbc:sqlserver://" + getInstanceString(dataSource);
    }

    @Override
    protected String getConnectionString(String dataSource, String database) {
        return getConnectionString(dataSource) + ";database=" + database;
    }

    @Override
    public void connect(String connectionString, Properties info) throws SQLException {
        // Add sendTimeAsDatetime=false option to enforce sending Time as
        // java.sql.Time (otherwise some precision is lost in conversions)
        super.connect(connectionString + ";sendTimeAsDatetime=false", info);
    }

    private static String paramNamePattern = "@([A-Za-z0-9_]+)";
    private static Pattern paramRegex = Pattern.compile(paramNamePattern);

    public Pattern getParameterPattern() {
        return paramRegex;
    }

    protected String parseCommandText(String commandText) {
        commandText = commandText.replaceAll(paramNamePattern, "?");
        return super.parseCommandText(commandText);
    }

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
            throws SQLException {
        String qry = " select c.[name], TYPE_NAME(c.system_type_id) as [Type], c.max_length, "
                + " 0 As is_output, 0 As is_cursor_ref "
                + " from sys.columns c "
                + " where c.object_id = OBJECT_ID(?) "
                + " order by column_id";
        return readIntoParams(tableOrViewName, qry);
    }

    private Map<String, DbParameterAccessor> readIntoParams(String objname,
            String query) throws SQLException {
        if (objname.contains(".")) {
            String[] schemaAndName = objname.split("[\\.]", 2);
            objname = "[" + schemaAndName[0] + "].[" + schemaAndName[1] + "]";
        } else {
            objname = "[" + NameNormaliser.normaliseName(objname) + "]";
        }
        PreparedStatement dc = currentConnection.prepareStatement(query);
        dc.setString(1, NameNormaliser.normaliseName(objname));
        ResultSet rs = dc.executeQuery();
        Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();
        int position = 0;
        while (rs.next()) {
            String paramName = rs.getString(1);
            if (paramName == null)
                paramName = "";
            String dataType = rs.getString(2);
            // int length = rs.getInt(3);
            int direction = rs.getInt(4);
            Direction paramDirection;
            if (paramName.trim().length() == 0)
                paramDirection = Direction.RETURN_VALUE;
            else
                paramDirection = getParameterDirection(direction);
            DbParameterAccessor dbp = new DbParameterAccessor(paramName,
                    paramDirection, getSqlType(dataType),
                    getJavaClass(dataType), position++);
            allParams.put(NameNormaliser.normaliseName(paramName), dbp);
        }
        rs.close();
        return allParams;
    }

    // List interface has sequential search, so using list instead of array to
    // map types
    private static List<String> stringTypes = Arrays.asList(new String[] {
            "VARCHAR", "NVARCHAR", "CHAR", "NCHAR", "TEXT", "NTEXT",
            "UNIQUEIDENTIFIER" });
    private static List<String> intTypes = Arrays
            .asList(new String[] { "INT" });
    private static List<String> booleanTypes = Arrays
            .asList(new String[] { "BIT" });
    private static List<String> floatTypes = Arrays
            .asList(new String[] { "REAL" });
    private static List<String> doubleTypes = Arrays
            .asList(new String[] { "FLOAT" });
    private static List<String> longTypes = Arrays
            .asList(new String[] { "BIGINT" });
    private static List<String> shortTypes = Arrays.asList(new String[] {
            "TINYINT", "SMALLINT" });

    private static List<String> decimalTypes = Arrays.asList(new String[] {
            "DECIMAL", "NUMERIC", "MONEY", "SMALLMONEY" });
    private static List<String> timestampTypes = Arrays.asList(new String[] {
            "SMALLDATETIME", "DATETIME", "DATETIME2", "TIMESTAMP" });
    private static List<String> dateTypes = Arrays.asList("DATE");
    private static List<String> timeTypes = Arrays.asList("TIME");

    // private static List<String> refCursorTypes = Arrays.asList(new String[] {
    // });
    // private static List<String> doubleTypes=Arrays.asList(new
    // String[]{"DOUBLE"});

    // private static string[] BinaryTypes=new string[] {"BINARY","VARBINARY"};
    // private static string[] GuidTypes = new string[] { "UNIQUEIDENTIFIER" };
    // private static string[] VariantTypes = new string[] { "SQL_VARIANT" };

    private static Direction getParameterDirection(int isOutput) {
        if (isOutput == 1)
            return Direction.OUTPUT;
        else
            return Direction.INPUT;
    }

    private static String normaliseTypeName(String dataType) {
        dataType = dataType.toUpperCase().trim();
        int idx = dataType.indexOf(" ");
        if (idx >= 0)
            dataType = dataType.substring(0, idx);
        idx = dataType.indexOf("(");
        if (idx >= 0)
            dataType = dataType.substring(0, idx);
        return dataType;
    }

    private static int getSqlType(String dataType) {
        // todo:strip everything from first blank
        dataType = normaliseTypeName(dataType);

        if (stringTypes.contains(dataType))
            return java.sql.Types.VARCHAR;
        if (decimalTypes.contains(dataType))
            return java.sql.Types.NUMERIC;
        if (intTypes.contains(dataType))
            return java.sql.Types.INTEGER;
        if (timestampTypes.contains(dataType))
            return java.sql.Types.TIMESTAMP;
        if (dateTypes.contains(dataType))
            return java.sql.Types.DATE;
        if (timeTypes.contains(dataType))
            return java.sql.Types.TIME;
        if (booleanTypes.contains(dataType))
            return java.sql.Types.BOOLEAN;
        if (floatTypes.contains(dataType))
            return java.sql.Types.FLOAT;
        if (doubleTypes.contains(dataType))
            return java.sql.Types.DOUBLE;

        if (longTypes.contains(dataType))
            return java.sql.Types.BIGINT;
        if (shortTypes.contains(dataType))
            return java.sql.Types.SMALLINT;

        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Class<?> getJavaClass(String dataType) {
        dataType = normaliseTypeName(dataType);
        if (stringTypes.contains(dataType))
            return String.class;
        if (decimalTypes.contains(dataType))
            return BigDecimal.class;
        if (intTypes.contains(dataType))
            return Integer.class;
        if (timestampTypes.contains(dataType))
            return java.sql.Timestamp.class;
        if (dateTypes.contains(dataType))
            return java.sql.Date.class;
        if (timeTypes.contains(dataType))
            return java.sql.Time.class;
        if (booleanTypes.contains(dataType))
            return Boolean.class;
        if (floatTypes.contains(dataType))
            return Float.class;
        if (doubleTypes.contains(dataType))
            return Double.class;
        if (longTypes.contains(dataType))
            return Long.class;
        if (shortTypes.contains(dataType))
            return Short.class;

        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {
        return readIntoParams(
                procName,
                "select p.[name], TYPE_NAME(p.system_type_id) as [Type],  "
                        + " p.max_length, p.is_output, p.is_cursor_ref from sys.parameters p "
                        + " where p.object_id = OBJECT_ID(?) order by parameter_id ");

    }

    public String buildInsertCommand(String tableName,
            DbParameterAccessor[] accessors) {
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(tableName).append("(");
        String comma = "";

        StringBuilder values = new StringBuilder();

        for (DbParameterAccessor accessor : accessors) {
            if (accessor.hasDirection(Direction.INPUT)) {
                sb.append(comma);
                values.append(comma);
                //This will allow column names that have spaces or are keywords.
                sb.append("[" + accessor.getName() + "]");
                values.append("?");
                comma = ",";
            }
        }
        sb.append(") values (");
        sb.append(values);
        sb.append(")");
        return sb.toString();
    }
}

