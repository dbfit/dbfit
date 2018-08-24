package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessorsMapBuilder;
import dbfit.util.Direction;
import static dbfit.environment.SybaseTypeNameNormaliser.normaliseTypeName;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import dbfit.util.Options;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@DatabaseEnvironment(name="Sybase", driver="com.sybase.jdbc4.jdbc.SybDriver")
public class SybaseEnvironment extends AbstractDbEnvironment {

    public SybaseEnvironment(String driverClassName) {
        super(driverClassName);
        Options.setOption(Options.OPTION_PARAMETER_PATTERN, paramNamePattern);
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

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
            throws SQLException {
        String check = " and u.name || '.' || o.name = ? ";
        // Adding current user_name if it's not provided
        if (tableOrViewName.indexOf('.') == -1)
            check = " and u.name = user_name() and o.name = ? ";
        String query = " select c.name, t.name as type "
                + " from dbo.sysobjects o "
                + " join dbo.sysusers u on u.uid = o.uid "
                + " join dbo.syscolumns c on c.id = o.id "
                + " join dbo.systypes t on t.type = c.type and t.usertype = c.usertype "
                + " where o.type in ('U', 'V') "
                + check
                + " order by colid";

        DbParameterAccessorsMapBuilder params = new DbParameterAccessorsMapBuilder(dbfitToJdbcTransformerFactory);

        try (PreparedStatement dc = currentConnection.prepareStatement(query)) {
            dc.setString(1, tableOrViewName);
            ResultSet rs = dc.executeQuery();

            while (rs.next()) {
                String paramName = defaultIfNull(rs.getString("name"), "");
                params.add(paramName,
                           Direction.INPUT,
                           getSqlType(rs.getString("type")),
                           getJavaClass(rs.getString("type")));
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
    private static List<String> timeTypes = Arrays.asList("TIME");

    private static int getSqlType(String dataType) {
System.out.println("SybaseEnvironment: getSqlType: dataType: " + dataType);
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
System.out.println("SybaseEnvironment: getJavaClass: " + dataType);
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
            return Integer.class;

        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {
        String query = "exec sp_sproc_columns ?";
        // Adding current user_name if it's not provided
        if (procName.indexOf('.') == -1)
            query = "exec sp_sproc_columns user_name() || '.' || ?";

        DbParameterAccessorsMapBuilder params = new DbParameterAccessorsMapBuilder(dbfitToJdbcTransformerFactory);
        try (PreparedStatement dc = currentConnection.prepareStatement(query)) {
            dc.setString(1, procName);
            ResultSet rs = dc.executeQuery();

            while (rs.next()) {
                String paramName = rs.getString("column_name").substring(1).trim();
                String paramType = rs.getString("column_type").trim();
                String paramDataType = rs.getString("type_name").trim();

                Direction direction;
                if ("IN".equals(paramType)) direction = Direction.INPUT;
                    else if ("INOUT".equals(paramType)) direction = Direction.INPUT_OUTPUT;
                    else direction = Direction.RETURN_VALUE;

                params.add(("RETURN_VALUE".equals(paramName))? "" : paramName,
                           direction,
                           getSqlType(paramDataType),
                           getJavaClass(paramDataType));
            }
        }

        return params.toMap();
    }

    @Override
    public void bindStatementParameter(PreparedStatement statement, int parameterIndex, Object value)
            throws SQLException {
        if (value == null) {
            statement.setNull(parameterIndex, Types.CHAR);
        } else {
            statement.setObject(parameterIndex, value);
        }
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
