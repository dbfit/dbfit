package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.NameNormaliser;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

@DatabaseEnvironment(name="MySql", driver="com.mysql.jdbc.Driver")
public class MySqlEnvironment extends AbstractDbEnvironment {
    public MySqlEnvironment(String driverClassName) {
        super(driverClassName);
    }

    public boolean supportsOuputOnInsert() {
        return false;
    }

    protected String getConnectionString(String dataSource) {
        return "jdbc:mysql://" + dataSource;
    }

    protected String getConnectionString(String dataSource, String database) {
        return "jdbc:mysql://" + dataSource + "/" + database;
    }

    private static String paramNamePattern = "@([A-Za-z0-9_]+)";
    private static Pattern paramRegex = Pattern.compile(paramNamePattern);

    public Pattern getParameterPattern() {
        return paramRegex;
    }

    // mysql jdbc driver does not support named parameters - so just map them
    // to standard jdbc question marks
    protected String parseCommandText(String commandText) {
        commandText = commandText.replaceAll(paramNamePattern, "?");
        return super.parseCommandText(commandText);
    }

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
            throws SQLException {
        String[] qualifiers = NameNormaliser.normaliseName(tableOrViewName)
                .split("\\.");
        String qry = " select column_name, data_type, character_maximum_length "
                + "	as direction from information_schema.columns where ";
        if (qualifiers.length == 2) {
            qry += " lower(table_schema)=? and lower(table_name)=? ";
        } else {
            qry += " (table_schema=database() and lower(table_name)=?)";
        }
        qry += " order by ordinal_position";
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
                DbParameterAccessor dbp = new DbParameterAccessor(columnName,
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
    private static List<String> stringTypes = Arrays.asList(new String[] {
            "VARCHAR", "CHAR", "TEXT" });
    private static List<String> intTypes = Arrays.asList(new String[] {
            "TINYINT", "SMALLINT", "MEDIUMINT", "INT", "INTEGER" });
    private static List<String> longTypes = Arrays.asList(new String[] {
            "BIGINT", "INTEGER UNSIGNED", "INT UNSIGNED" });
    private static List<String> floatTypes = Arrays
            .asList(new String[] { "FLOAT" });
    private static List<String> doubleTypes = Arrays
            .asList(new String[] { "DOUBLE" });
    private static List<String> decimalTypes = Arrays.asList(new String[] {
            "DECIMAL", "DEC" });
    private static List<String> dateTypes = Arrays
            .asList(new String[] { "DATE" });
    private static List<String> timestampTypes = Arrays.asList(new String[] {
            "TIMESTAMP", "DATETIME" });
    private static List<String> timeTypes = Arrays.asList("TIME");
    private static List<String> refCursorTypes = Arrays.asList(new String[] {});

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
        if (timeTypes.contains(dataType))
            return java.sql.Types.TIME;
        if (refCursorTypes.contains(dataType))
            return java.sql.Types.REF;
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
        if (floatTypes.contains(dataType))
            return Float.class;
        if (dateTypes.contains(dataType))
            return java.sql.Date.class;
        if (refCursorTypes.contains(dataType))
            return RowSet.class;
        if (doubleTypes.contains(dataType))
            return Double.class;
        if (longTypes.contains(dataType))
            return Long.class;
        if (timestampTypes.contains(dataType))
            return java.sql.Timestamp.class;
        if (timeTypes.contains(dataType))
            return java.sql.Time.class;
        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {

        String[] qualifiers = NameNormaliser.normaliseName(procName).split(
                "\\.");
        String qry = " select type,param_list,returns from mysql.proc where ";
        if (qualifiers.length == 2) {
            qry += " lower(db)=? and lower(name)=? ";
        } else {
            qry += " (db=database() and lower(name)=?)";
        }

        String type;
        String paramList;
        String returns;

        try (PreparedStatement dc = currentConnection.prepareStatement(qry)) {
            for (int i = 0; i < qualifiers.length; i++) {
                dc.setString(i + 1, NameNormaliser.normaliseName(qualifiers[i]));
            }
            ResultSet rs = dc.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Unknown procedure " + procName);
            }

            type = rs.getString(1);
            paramList = rs.getString(2);
            returns = rs.getString(3);
            rs.close();
        }

        MySqlProcedureParametersParser parser = new MySqlProcedureParametersParser();
        Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();

        int position = 0;
        for (ParamDescriptor pd: parser.parseParameters(paramList)) {
            DbParameterAccessor dbp = new DbParameterAccessor(
                    pd.name, pd.direction,
                    getSqlType(pd.type), getJavaClass(pd.type),
                    position++);
            allParams.put(NameNormaliser.normaliseName(pd.name), dbp);
        }

        if ("FUNCTION".equals(type)) {
            ParamDescriptor rd = parser.parseReturnType(returns);
            allParams.put("", new DbParameterAccessor("",
                    Direction.RETURN_VALUE, getSqlType(rd.type),
                    getJavaClass(rd.type), -1));
        }

        return allParams;
    }
}

