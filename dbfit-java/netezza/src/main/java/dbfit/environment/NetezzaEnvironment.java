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

@DatabaseEnvironment(name="Netezza", driver="org.netezza.Driver")
public class NetezzaEnvironment extends AbstractDbEnvironment {
    public NetezzaEnvironment(String driverClassName) {
        super(driverClassName);
    }

    protected String getConnectionString(String dataSource) {
        return "jdbc:netezza://" + dataSource;
    }

    protected String getConnectionString(String dataSource, String database) {
        return "jdbc:netezza://" + dataSource + "/" + database;
    }

    private static String paramNamePattern = "_:([A-Za-z0-9_]+)";
    private static Pattern paramsNames = Pattern.compile(paramNamePattern);

    public Pattern getParameterPattern() {
        return paramsNames;
    }

    // override the buildInsertPreparedStatement to leave out RETURN_GENERATED_KEYS
    public PreparedStatement buildInsertPreparedStatement(String tableName,
            DbParameterAccessor[] accessors) throws SQLException {
        return getConnection().prepareStatement(
                buildInsertCommand(tableName, accessors));
    }

    // netezza jdbc driver does not support named parameters - so just map them
    // to standard jdbc question marks
    protected String parseCommandText(String commandText) {
        commandText = commandText.replaceAll(paramNamePattern, "?");
        return super.parseCommandText(commandText);
    }

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
            throws SQLException {
        String[] qualifiers = NameNormaliser.normaliseName(tableOrViewName)
                .split("\\.");
        String qry = " select ATTNAME, FORMAT_TYPE, ATTLEN from _v_relation_column where ";

        if (qualifiers.length == 2) {
            qry += " lower(owner)=? and lower(name)=? ";
        } else {
            qry += " (lower(name)=?)";
        }
        qry += " order by attnum";
        return readIntoParams(qualifiers, qry);
    }

    private Map<String, DbParameterAccessor> readIntoParams(
            String[] queryParameters, String query) throws SQLException {
        try (PreparedStatement dc = currentConnection.prepareStatement(query)) {
            for (int i = 0; i < queryParameters.length; i++) {
                dc.setString(i + 1,
                        NameNormaliser.normaliseName(queryParameters[i]));
            }
            ResultSet rs = dc.executeQuery();
            Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();
            int position = 0;
            while (rs.next()) {
                String paramName = rs.getString(1);
                if (paramName == null) {
                    paramName = "";
                }
                String dataType = rs.getString(2);
                DbParameterAccessor dbp = new DbParameterAccessor(paramName,
                        Direction.INPUT, getSqlType(dataType),
                        getJavaClass(dataType), position++);
                allParams.put(NameNormaliser.normaliseName(paramName), dbp);
            }
            rs.close();
            return allParams;
        }
    }

    // List interface has sequential search, so using list instead of array to
    // map types
    private static List<String> stringTypes = Arrays.asList(new String[] {
            "VARCHAR", "CHAR", "CHARACTER", "CHARACTER VARYING", "VARYING", "TEXT",
            "NAME", "XML", "BPCHAR", "UNKNOWN", "NVCHAR" ,"NCHAR", "NATIONAL CHARACTER VARYING", "NATIONAL CHARACTER"});
    private static List<String> intTypes = Arrays.asList(new String[] {
            "INT", "INT4", "INTEGER", "SERIAL" });
    private static List<String> tinyintTypes = Arrays.asList(new String[] {
            "BYTEINT","INT1"});
    private static List<String> smallintTypes = Arrays.asList(new String[] {
            "SMALLINT", "INT2"});
    private static List<String> longTypes = Arrays.asList(new String[] {
            "BIGINT", "BIGSERIAL", "INT8" });
    private static List<String> floatTypes = Arrays.asList(new String[] {
            "REAL", "FLOAT4" });
    private static List<String> doubleTypes = Arrays.asList(new String[] {
            "DOUBLE PRECISION", "FLOAT8", "FLOAT","DOUBLE" });
    private static List<String> decimalTypes = Arrays.asList(new String[] {
            "DECIMAL", "NUMERIC" });
    private static List<String> dateTypes = Arrays
            .asList(new String[] { "DATE" });
    private static List<String> timestampTypes = Arrays.asList(new String[] {
            "TIMESTAMP", "TIMESTAMP WITHOUT TIME ZONE",
            "TIMESTAMP WITH TIME ZONE", "TIMESTAMPTZ" });
    private static List<String> refCursorTypes = Arrays
        .asList(new String[] { "REFTABLE" });
    private static List<String> booleanTypes = Arrays.asList(new String[] {
            "BOOL", "BOOLEAN" });

    private static String normaliseTypeName(String dataType) {
        if (dataType.indexOf("(") <= 0) {
           dataType = dataType.toUpperCase().trim();
        } else {
           dataType = dataType.toUpperCase().trim().substring(0,dataType.indexOf("("));
        }
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
        if (tinyintTypes.contains(dataType))
            return java.sql.Types.TINYINT;
        if (smallintTypes.contains(dataType))
            return java.sql.Types.SMALLINT;
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
        if (refCursorTypes.contains(dataType))
            return java.sql.Types.REF;
        if (booleanTypes.contains(dataType))
            return java.sql.Types.BOOLEAN;
        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Class getJavaClass(String dataType) {
        dataType = normaliseTypeName(dataType);
        if (stringTypes.contains(dataType))
            return String.class;
        if (decimalTypes.contains(dataType))
            return BigDecimal.class;
        if (intTypes.contains(dataType))
            return Integer.class;
        if (tinyintTypes.contains(dataType))
            return Byte.class;
        if (smallintTypes.contains(dataType))
            return Short.class;
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
        if (booleanTypes.contains(dataType))
            return Boolean.class;
        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {

        String[] qualifiers = NameNormaliser.normaliseName(procName).split(
                "\\.");
        String qry = "select btrim(btrim(arguments,'('),')') as param_list from _v_procedure where 1=1";
        if (qualifiers.length == 3) {
            qry += " and lower(database)=? and lower(schema)=? and lower(procedure)=? ";
        } else if (qualifiers.length == 2) {
            qry += " and lower(schema)=? and lower(procedure)=? ";
        } else {
            qry += " and lower(procedure)=? ";
        }

        String paramList;

        try (PreparedStatement dc = currentConnection.prepareStatement(qry)) {
            for (int i = 0; i < qualifiers.length; i++) {
                dc.setString(i + 1, NameNormaliser.normaliseName(qualifiers[i]));
            }
            ResultSet rs = dc.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Unknown procedure " + procName);
            }
            paramList = rs.getString(1);
            rs.close();
        }

        int position = 0;
        Direction direction = Direction.INPUT;
        String paramName;
        String dataType;
        String token;
        Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();

        if (paramList.length() != 0) {
            for (String param : paramList.split(",")) {

                StringTokenizer s = new StringTokenizer(param.trim().toLowerCase(),
                        " ()");

                token = s.nextToken();
                paramName = "$" + (position + 1);

                dataType = normaliseTypeName(param);

                DbParameterAccessor dbp = new DbParameterAccessor(paramName,
                        direction, getSqlType(dataType), getJavaClass(dataType),
                        position++);
                allParams.put(NameNormaliser.normaliseName(paramName), dbp);
            }
        }
        return allParams;
    }

    public String buildInsertCommand(String tableName,
            DbParameterAccessor[] accessors) {
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(tableName).append("(");
        String comma = "";
        String retComma = "";

        StringBuilder values = new StringBuilder();
        StringBuilder retNames = new StringBuilder();
        StringBuilder retValues = new StringBuilder();

        for (DbParameterAccessor accessor : accessors) {
            if (accessor.hasDirection(Direction.INPUT)) {
                sb.append(comma);
                values.append(comma);
                sb.append(accessor.getName());
                values.append("?");
                comma = ",";
            } else {
                retNames.append(retComma);
                retValues.append(retComma);
                retNames.append(accessor.getName());
                retValues.append("?");
                retComma = ",";
            }
        }
        sb.append(") values (");
        sb.append(values);
        sb.append(")");
        return sb.toString();
    }
}
