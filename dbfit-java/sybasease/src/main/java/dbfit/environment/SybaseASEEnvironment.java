package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessorsMapBuilder;
import dbfit.util.Direction;
import dbfit.util.SqlTimestampNormaliser;

import static dbfit.environment.SybaseTypeNameNormaliser.normaliseTypeName;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import dbfit.util.Options;
import dbfit.util.TypeNormaliserFactory;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@DatabaseEnvironment(name="SybaseASE", driver="com.sybase.jdbc4.jdbc.SybDriver")
public class SybaseASEEnvironment extends SybaseEnvironment {

    public SybaseASEEnvironment(String driverClassName) {
        super(driverClassName);
        defaultParamPatternString = "@([A-Za-z0-9_]+)";
        Options.setOption(Options.OPTION_PARAMETER_PATTERN, paramNamePattern);
        TypeNormaliserFactory.setNormaliser(com.sybase.jdbc4.tds.SybTimestamp.class,
            new SqlTimestampNormaliser());
    }

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
                + " join dbo.systypes t on t.usertype = c.usertype "
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

    // Types.BIT, java.lang.Boolean
    private static List<String> bitBooleanTypes = Arrays.asList(new String[] {
        "BIT" } );
    // Types.TINYINT, java.lang.Integer
    private static List<String> tinyintIntegerTypes = Arrays.asList(new String[] {
        "TINYINT" } );
    // Types.BIGINT, java.lang.Long;
    private static List<String> bigintLongTypes = Arrays.asList(new String[] {
        "BIGINT" } );
    // Types.BIGINT, java.math.BigDecimal
    private static List<String> bigintBigDecimalTypes = Arrays.asList(new String[] {
        "UBIGINT", "UNSIGNED BIGINT" } );
    // Types.LONGVARCHAR, java.lang.String
    private static List<String> longvarcharStringTypes = Arrays.asList(new String[] {
        "TEXT" } );
    // Types.CHAR, java.lang.String
    private static List<String> charStringTypes = Arrays.asList(new String[] {
        "CHAR" } );
    // Types.MONEY/SMALLMONEY/NUMERIC, java.math.BigDecimal
    private static List<String> numericBigDecimalTypes = Arrays.asList(new String[] {
        "MONEY", "SMALLMONEY", "NUMERIC" } );
    // Types.DECIMAL, java.math.BigDecimal
    private static List<String> decimalBigDecimalTypes = Arrays.asList(new String[] {
        "DECIMAL" } );
    // Types.INTEGER, java.lang.Integer
    // For queries returning an UNSIGNED INT the Sybase JDBC driver says that ResultSetMetadata will
    // create a Long but in fact it creates an Integer;
    private static List<String> integerIntegerTypes = Arrays.asList(new String[] {
        "INT", "INTEGER", "UINT", "UNSIGNED INT" } );
    // Types.SMALLINT, java.lang.Integer
    private static List<String> smallintIntegerTypes = Arrays.asList(new String[] {
        "SMALLINT" } );
    // Types.REAL, java.lang.Float
    private static List<String> realFloatTypes = Arrays.asList(new String[] {
        "REAL" } );
    // Types.DOUBLE, java.lang.Double
    private static List<String> doubleDoubleTypes = Arrays.asList(new String[] {
        "DOUBLE PRECISION", "FLOAT" } );
    // Types.VARCHAR, java.lang.String
    private static List<String> varcharStringTypes = Arrays.asList(new String[] {
        "VARCHAR" } );
    // Types.DATE, java.sql.Date
    private static List<String> dateDateTypes = Arrays.asList(new String[] {
        "DATE" } );
    // Types.TIME, java.sql.Timestamp
    // Sybase creates a timestamp subtype of java.sql.Time;
    private static List<String> timeTimeTypes = Arrays.asList(new String[] {
        "TIME" } );
    // Types.TIME, java.sql.Time
    private static List<String> timestampTimestampTypes = Arrays.asList(new String[] {
        "DATETIME", "SMALLDATETIME", "BIGDATETIME" } );

    private static int getSqlType(String dataType) {
        // todo:strip everything from first blank
        dataType = normaliseTypeName(dataType);

        if (bitBooleanTypes.contains(dataType))
            return Types.BIT;
        if (tinyintIntegerTypes.contains(dataType))
            return Types.INTEGER;
        if (bigintLongTypes.contains(dataType))
            return Types.BIGINT;
        if (bigintBigDecimalTypes.contains(dataType))
            return Types.BIGINT;
        if (longvarcharStringTypes.contains(dataType))
            return Types.LONGVARCHAR;
        if (charStringTypes.contains(dataType))
            return Types.CHAR;
        if (numericBigDecimalTypes.contains(dataType))
            return Types.NUMERIC;
        if (decimalBigDecimalTypes.contains(dataType))
            return Types.DECIMAL;
        if (integerIntegerTypes.contains(dataType))
            return Types.INTEGER;
        if (smallintIntegerTypes.contains(dataType))
            return Types.SMALLINT;
        if (realFloatTypes.contains(dataType))
            return Types.REAL;
        if (doubleDoubleTypes.contains(dataType))
            return Types.DOUBLE;
        if (varcharStringTypes.contains(dataType))
            return Types.VARCHAR;
        if (dateDateTypes.contains(dataType))
            return Types.DATE;
        if (timeTimeTypes.contains(dataType)) {
            return Types.TIME;
        }
        if (timestampTimestampTypes.contains(dataType)) {
            return Types.TIMESTAMP;
        }
        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Class<?> getJavaClass(String dataType) {
        dataType = normaliseTypeName(dataType);

        if (bitBooleanTypes.contains(dataType))
            return Boolean.class;
        if (tinyintIntegerTypes.contains(dataType))
            return Integer.class;
        if (bigintLongTypes.contains(dataType))
            return Long.class;
        if (bigintBigDecimalTypes.contains(dataType))
            return BigDecimal.class;
        if (longvarcharStringTypes.contains(dataType))
            return String.class;
        if (charStringTypes.contains(dataType))
            return String.class;
        if (numericBigDecimalTypes.contains(dataType))
            return BigDecimal.class;
        if (decimalBigDecimalTypes.contains(dataType))
            return BigDecimal.class;
        if (integerIntegerTypes.contains(dataType))
            return Integer.class;
        if (smallintIntegerTypes.contains(dataType))
            return Integer.class;
        if (realFloatTypes.contains(dataType))
            return Float.class;
        if (doubleDoubleTypes.contains(dataType))
            return Double.class;
        if (varcharStringTypes.contains(dataType))
            return String.class;
        if (dateDateTypes.contains(dataType))
            return java.sql.Date.class;
        if (timeTimeTypes.contains(dataType)) {
            return java.sql.Time.class;
        }
        if (timestampTimestampTypes.contains(dataType))
            return java.sql.Timestamp.class;
//return com.sybase.jdbc4.tds.SybTimestamp.class;
        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {
        String query = "sp_sproc_columns ?";
    	String qualifiers[] = procName.split("\\.");
        if (qualifiers.length > 1) {
            query += ", ?";   
        }
        DbParameterAccessorsMapBuilder params = new DbParameterAccessorsMapBuilder(dbfitToJdbcTransformerFactory);
        try (PreparedStatement dc = currentConnection.prepareStatement(query)) {
            if (qualifiers.length > 1) {
            	dc.setString(1, qualifiers[1]);
                dc.setString(2, qualifiers[0]);
            } else {
            	dc.setString(1, qualifiers[0]);
            }
            ResultSet rs = dc.executeQuery();
            while (rs.next()) {
                String paramName = rs.getString("column_name");
                String paramType = rs.getString("mode").trim();
                String paramDataType = rs.getString("type_name");
                Direction direction;
                if ("in".equals(paramType)) {
                    direction = Direction.INPUT;
                } else {
                    if ("out".equals(paramType)) {
                        direction = Direction.INPUT_OUTPUT;
                    } else {
                        if ("Return Type".equals(paramType)) {
                            direction = Direction.RETURN_VALUE;
                        } else {
                        	throw new SQLException("Unpexpected parm_mode value: " + paramType);
                        }
                    }
                }
                params.add("Return Type".equals(paramType) ? "" : paramName,
                           direction,
                           getSqlType(paramDataType),
                           getJavaClass(paramDataType));
            }
        }
        return params.toMap();
    }
}
