package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessorsMapBuilder;
import dbfit.util.Direction;
import static dbfit.util.Direction.*;
import static dbfit.environment.SybaseTypeNameNormaliser.normaliseTypeName;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Properties;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@DatabaseEnvironment(name="Sybase", driver="com.sybase.jdbc4.jdbc.SybDriver")
public class SybaseEnvironment extends AbstractDbEnvironment {

    public SybaseEnvironment(String driverClassName) {
        super(driverClassName);

        /*TypeNormaliserFactory.setNormaliser(java.sql.Time.class,
                new MillisecondTimeNormaliser());*/
    }

    public boolean supportsOuputOnInsert() {
        return false;
    }

    @Override
    protected String getConnectionString(String dataSource) {
        return "jdbc:sybase:Tds:" + dataSource;
    }

    @Override
    protected String getConnectionString(String dataSource, String database) {
        return getConnectionString(dataSource) + "?ServiceName=" + database;
    }

    @Override
    public void connect(String connectionString, Properties info) throws SQLException {
        // Add sendTimeAsDatetime=false option to enforce sending Time as
        // java.sql.Time (otherwise some precision is lost in conversions)
        super.connect(connectionString, info);
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
        String qry = " select c.name, t.name as type, 0 as is_output "
                + " from dbo.sysobjects o "
                + " join dbo.sysusers u on u.uid = o.uid "
                + " join dbo.syscolumns c on c.id = o.id "
                + " join dbo.systypes t on t.type = c.type and t.usertype = c.usertype "
                + " where o.type in ('U', 'V') and o.name = ? "
                //+ " where o.type in ('U', 'V') and u.name || '.' || o.name = ? "
                + " order by colid";
        return readIntoParams(tableOrViewName, qry);
    }

    private Map<String, DbParameterAccessor> readIntoParams(String objname,
            String query) throws SQLException {
        DbParameterAccessorsMapBuilder params = new DbParameterAccessorsMapBuilder(dbfitToJdbcTransformerFactory);

        try (PreparedStatement dc = currentConnection.prepareStatement(query)) {
            dc.setString(1, objname);
            ResultSet rs = dc.executeQuery();

            while (rs.next()) {
                String paramName = defaultIfNull(rs.getString(1), "");
                params.add(paramName,
                           INPUT,
                           getSqlType(rs.getString(2)),
                           getJavaClass(rs.getString(2)));
            }
        }

        return params.toMap();
    }

    // List interface has sequential search, so using list instead of array to
    // map types
    private static List<String> stringTypes = Arrays.asList(new String[] {
            "VARCHAR", "NVARCHAR", "CHAR", "NCHAR", "TEXT", "NTEXT" });
    private static List<String> intTypes = Arrays.asList(new String[] {
            "INT", "INTN", "UNSIGNED INT", "UNSIGNED INTN", "INTEGER" });
    private static List<String> booleanTypes = Arrays
            .asList(new String[] { "BIT" });
    private static List<String> floatTypes = Arrays
            .asList(new String[] { "REAL" });
    private static List<String> doubleTypes = Arrays
            .asList(new String[] { "FLOAT", "FLOATN" });
    private static List<String> longTypes = Arrays.asList(new String[] {
            "BIGINT", "BIGINTN", "UNSIGNED BIGINT", "UNSIGNED BIGINTN" });
    private static List<String> shortTypes = Arrays.asList(new String[] {
            "TINYINT", "SMALLINT", "UNSIGNED SMALLINT", "UNSIGNED SMALLINTN" });

    private static List<String> numericTypes = Arrays.asList(new String[] {
            "NUMERIC", "NUMERICN"});
    private static List<String> decimalTypes = Arrays.asList(new String[] {
            "DECIMAL", "DECIMALN", "MONEY", "MONEYN", "SMALLMONEY" });
    private static List<String> timestampTypes = Arrays.asList(new String[] {
            "SMALLDATETIME", "DATETIME", "DATETIMN", "TIMESTAMP" });
    private static List<String> dateTypes = Arrays.asList("DATE");
    //private static List<String> binaryTypes = Arrays.asList(new String[] {
    //        "BINARY", "VARBINARY"});

/*
    private String objectDatabasePrefix(String dbObjectName) {
        String objectDatabasePrefix = "";
        String[] objnameParts = dbObjectName.split("\\.");
        if (objnameParts.length == 3) {
            objectDatabasePrefix = objnameParts[0] + ".";
        }
        return objectDatabasePrefix;
    }
*/

    private static int getSqlType(String dataType) {
        // todo:strip everything from first blank
        dataType = normaliseTypeName(dataType);

        if (stringTypes.contains(dataType))
            return java.sql.Types.VARCHAR;
        if (numericTypes.contains(dataType))
            return java.sql.Types.NUMERIC;
        if (decimalTypes.contains(dataType))
            return java.sql.Types.DECIMAL;
        if (intTypes.contains(dataType))
            return java.sql.Types.INTEGER;
        if (timestampTypes.contains(dataType))
            return java.sql.Types.TIMESTAMP;
        if (dateTypes.contains(dataType))
                return java.sql.Types.DATE;
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
        if (numericTypes.contains(dataType))
            return BigDecimal.class;
        if (decimalTypes.contains(dataType))
            return BigDecimal.class;
        if (intTypes.contains(dataType))
            return Integer.class;
        if (timestampTypes.contains(dataType))
            return java.sql.Timestamp.class;
        if (dateTypes.contains(dataType))
            return java.sql.Date.class;
        if (booleanTypes.contains(dataType))
            return Boolean.class;
        if (floatTypes.contains(dataType))
            return Float.class;
        if (doubleTypes.contains(dataType))
            return Double.class;
        if (longTypes.contains(dataType))
            return Long.class;
        if (shortTypes.contains(dataType))
            return Integer.class;

        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {
        String query = "exec sp_sproc_columns ?";

        DbParameterAccessorsMapBuilder params = new DbParameterAccessorsMapBuilder(dbfitToJdbcTransformerFactory);
        try (PreparedStatement dc = currentConnection.prepareStatement(query)) {
            dc.setString(1, procName);
            ResultSet rs = dc.executeQuery();

            while (rs.next()) {
                String paramName = rs.getString("column_name").substring(1).trim();
                String paramType = rs.getString("column_type").trim();
                String paramDataType = rs.getString("type_name").trim();

                Direction direction;
                if ("IN".equals(paramType)) direction = INPUT;
                    else if ("INOUT".equals(paramType)) direction = INPUT_OUTPUT;
                    else direction = RETURN_VALUE;

                params.add(("RETURN_VALUE".equals(paramName))? "" : paramName,
                           direction,
                           getSqlType(paramDataType),
                           getJavaClass(paramDataType));
            }
        }

        return params.toMap();
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
