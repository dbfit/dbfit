package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

@DatabaseEnvironment(name="Snowflake", driver="net.snowflake.client.jdbc.SnowflakeDriver")
public class SnowflakeEnvironment extends AbstractDbEnvironment {

    public SnowflakeEnvironment(String driverClassName) {
        super(driverClassName);
        defaultParamPatternString = "@([A-Za-z0-9_]+)";
    }

    public boolean supportsOuputOnInsert() {
        return false;
    }

    @Override
    protected String getConnectionString(String dataSource) {
        return "jdbc:snowflake://" + dataSource;
    }

    @Override
    protected String getConnectionString(String dataSource, String database) {
        return getConnectionString(dataSource) + "?db=" + database;
    }

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
            throws SQLException {
        String[] qualifiers = NameNormaliser.normaliseName(tableOrViewName)
                .split("\\.");
        String qry = "select column_name, data_type, character_maximum_length from "
                + objectDatabasePrefix(tableOrViewName) + "information_schema.columns where ";
        if (qualifiers.length == 3) {
            qry += "lower(table_catalog)=? and lower(table_schema)=? and lower(table_name)=? ";
        } else if (qualifiers.length >= 2) {
            qry += "table_catalog=current_database() and lower(table_schema)=? and lower(table_name)=? ";
        } else {
            qry += "table_catalog=current_database() and table_schema=current_schema() and lower(table_name)=? ";
        }
        qry += "order by ordinal_position";
        return readColumnsFromDb(qualifiers, qry);
    }

    private Map<String, DbParameterAccessor> readColumnsFromDb(
            String[] parametersForColumnQuery, String query) throws SQLException {
        try (PreparedStatement dc = currentConnection.prepareStatement(query)) {
            for (int i = 0; i < parametersForColumnQuery.length; i++) {
                dc.setString(i + 1,
                        NameNormaliser.normaliseName(parametersForColumnQuery[i]));
            }
            ResultSet rs = dc.executeQuery();
            Map<String, DbParameterAccessor> columns = new HashMap<String, DbParameterAccessor>();
            int position = 0;
            while (rs.next()) {
                String columnName = rs.getString(1);
                if (columnName == null)
                    columnName = "";
                String dataType = rs.getString(2);
                DbParameterAccessor dbp = createDbParameterAccessor(
                        columnName,
                        Direction.INPUT, getSqlType(dataType),
                        getJavaClass(dataType), position++);
                columns.put(NameNormaliser.normaliseName(columnName), dbp);
            }
            rs.close();
            return columns;
        }
    }
    
    // List interface has sequential search, so using list instead of array to 
    // map types
    private static List<String> stringTypes = Arrays.asList("VARCHAR", "CHAR", "TEXT", "STRING");
    private static List<String> booleanTypes = Arrays.asList("BOOLEAN");
    private static List<String> decimalTypes = Arrays.asList("DECIMAL", "NUMBER", "NUMERIC", "INT", "INTEGER",
            "TINYINT", "SMALLINT" , "BIGINT");
    private static List<String> timestampTypes = Arrays.asList("DATETIME", "TIMESTAMP_LTZ", "TIMESTAMP_NTZ",
            "TIMESTAMP_TZ" );
    private static List<String> dateTypes = Arrays.asList("DATE");
    private static List<String> timeTypes = Arrays.asList("TIME");

    private static List<String> variantTypes = Arrays.asList("VARIANT");

    private static int getSqlType(String dataType) {
        dataType = normaliseTypeName(dataType);

        if (stringTypes.contains(dataType))
            return java.sql.Types.VARCHAR;
        if (decimalTypes.contains(dataType))
            return Types.DECIMAL;
        if (timestampTypes.contains(dataType))
            return java.sql.Types.TIMESTAMP;
        if (dateTypes.contains(dataType))
            return java.sql.Types.DATE;
        if (timeTypes.contains(dataType))
            return java.sql.Types.TIME;
        if (booleanTypes.contains(dataType))
            return java.sql.Types.BOOLEAN;
        if (variantTypes.contains(dataType))
            return java.sql.Types.BOOLEAN;

        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Class<?> getJavaClass(String dataType) {
        dataType = normaliseTypeName(dataType);
        if (stringTypes.contains(dataType))
            return String.class;
        if (decimalTypes.contains(dataType))
            return BigDecimal.class;
        if (timestampTypes.contains(dataType))
            return java.sql.Timestamp.class;
        if (dateTypes.contains(dataType))
            return java.sql.Date.class;
        if (timeTypes.contains(dataType))
            return java.sql.Time.class;
        if (booleanTypes.contains(dataType))
            return Boolean.class;
        if (variantTypes.contains(dataType))
            return String.class;

        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    private static String normaliseTypeName(String dataType) {
        return  dataType.toUpperCase().trim();
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {

        String[] qualifiers = NameNormaliser.normaliseName(procName).split("\\.");
        String qry = "select FUNCTION_LANGUAGE, ARGUMENT_SIGNATURE, DATA_TYPE from information_schema.functions"
         + " where lower(function_name)=?";

        String functionLanguage;
        String argumentSignature;
        String returnType;

        try (PreparedStatement dc = currentConnection.prepareStatement(qry)) {
            for (int i = 0; i < qualifiers.length; i++) {
                dc.setString(i + 1, NameNormaliser.normaliseName(qualifiers[i]));
            }
            ResultSet rs = dc.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Unknown procedure " + procName);
            }

            functionLanguage = rs.getString(1);
            argumentSignature = rs.getString(2);
            returnType = rs.getString(3);
            rs.close();
        }

        SnowflakeProcedureParametersParser parser = new SnowflakeProcedureParametersParser();
        Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();

        int position = 0;
        for (ParamDescriptor pd: parser.parseParameters(argumentSignature)) {
            DbParameterAccessor dbp = createDbParameterAccessor(
                    pd.name, pd.direction,
                    getSqlType(pd.type), getJavaClass(pd.type),
                    position++);
            allParams.put(NameNormaliser.normaliseName(pd.name), dbp);
        }

        return allParams;
    }

    public String buildInsertCommand(String tableName, DbParameterAccessor[] accessors) {
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(tableName).append("(");
        String comma = "";

        StringBuilder values = new StringBuilder();

        for (DbParameterAccessor accessor : accessors) {
            if (accessor.hasDirection(Direction.INPUT)) {
                sb.append(comma);
                values.append(comma);
                //This will allow column names that have spaces or are keywords.
                sb.append(accessor.getName());
                values.append("?");
                comma = ",";
            }
        }
        sb.append(") values (");
        sb.append(values);
        sb.append(")");
        return sb.toString();
    }

    public PreparedStatement buildInsertPreparedStatement(String tableName,
                                                          DbParameterAccessor[] accessors) throws SQLException {
        return getConnection().prepareStatement(
                buildInsertCommand(tableName, accessors),
                Statement.NO_GENERATED_KEYS);
    }

    private String objectDatabasePrefix(String dbObjectName) {
        String objectDatabasePrefix = "";
        String[] objnameParts = dbObjectName.split("\\.");
        if (objnameParts.length == 3) {
            objectDatabasePrefix = objnameParts[0] + ".";
        }
        return objectDatabasePrefix;
    }
}

