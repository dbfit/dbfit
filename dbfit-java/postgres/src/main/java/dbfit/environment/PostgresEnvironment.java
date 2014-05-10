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

@DatabaseEnvironment(name="Postgres", driver="org.postgresql.Driver")
public class PostgresEnvironment extends AbstractDbEnvironment {
    public PostgresEnvironment(String driverClassName) {
        super(driverClassName);
    }

    protected String getConnectionString(String dataSource) {
        return "jdbc:postgresql://" + dataSource;
    }

    protected String getConnectionString(String dataSource, String database) {
        return "jdbc:postgresql://" + dataSource + "/" + database;
    }

    private static String paramNamePattern = "_:([A-Za-z0-9_]+)";
    private static Pattern paramsNames = Pattern.compile(paramNamePattern);

    public Pattern getParameterPattern() {
        return paramsNames;
    }

    // postgres jdbc driver does not support named parameters - so just map them
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
            qry += " (table_schema=current_schema() and lower(table_name)=?)";
        }
        qry += " order by ordinal_position";
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
                if (paramName == null)
                    paramName = "";
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
            "VARCHAR", "CHAR", "CHARACTER", "CHARACTER VARYING", "TEXT",
            "NAME", "XML", "BPCHAR", "UNKNOWN" });
    private static List<String> intTypes = Arrays.asList(new String[] {
            "SMALLINT", "INT", "INT4", "INT2", "INTEGER", "SERIAL" });
    private static List<String> longTypes = Arrays.asList(new String[] {
            "BIGINT", "BIGSERIAL", "INT8" });
    private static List<String> floatTypes = Arrays.asList(new String[] {
            "REAL", "FLOAT4" });
    private static List<String> doubleTypes = Arrays.asList(new String[] {
            "DOUBLE PRECISION", "FLOAT8", "FLOAT" });
    private static List<String> decimalTypes = Arrays.asList(new String[] {
            "DECIMAL", "NUMERIC" });
    private static List<String> dateTypes = Arrays
            .asList(new String[] { "DATE" });
    private static List<String> timestampTypes = Arrays.asList(new String[] {
            "TIMESTAMP", "TIMESTAMP WITHOUT TIME ZONE",
            "TIMESTAMP WITH TIME ZONE", "TIMESTAMPTZ" });
    private static List<String> refCursorTypes = Arrays
            .asList(new String[] { "REFCURSOR" });
    private static List<String> booleanTypes = Arrays.asList(new String[] {
            "BOOL", "BOOLEAN" });

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
        String qry = "select 'FUNCTION' as type, "
                + "array_to_string(array(select coalesce(pro.proargnames[t.id+1],'') || ' ' || pt.typname from generate_series(0, array_upper(pro.proargtypes, 1)) as t(id), pg_type pt where pt.oid = pro.proargtypes[t.id] order by t.id), ',') as param_list, "
                + "(select typname from pg_type pt where pt.oid = pro.prorettype) as returns "
                + "from pg_proc pro, pg_namespace ns where ns.oid = pro.pronamespace";
        if (qualifiers.length == 2) {
            qry += " and lower(ns.nspname)=? and lower(proname)=? ";
        } else {
            qry += " and (lower(ns.nspname)=current_schema() and lower(proname)=?)";
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

        int position = 0;
        Direction direction = Direction.INPUT;
        String paramName;
        String dataType;
        String token;
        Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();

        for (String param : paramList.split(",")) {
            StringTokenizer s = new StringTokenizer(param.trim().toLowerCase(),
                    " ()");

            token = s.nextToken();

            if (token.equals("in")) {
                token = s.nextToken();
            } else if (token.equals("inout")) {
                direction = Direction.INPUT_OUTPUT;
                token = s.nextToken();
            } else if (token.equals("out")) {
                direction = Direction.OUTPUT;
                token = s.nextToken();
            }

            if (s.hasMoreTokens()) {
                paramName = token;
                dataType = s.nextToken();
            } else {
                paramName = "$" + (position + 1);
                dataType = token;
            }

            DbParameterAccessor dbp = new DbParameterAccessor(paramName,
                    direction, getSqlType(dataType), getJavaClass(dataType),
                    position++);
            allParams.put(NameNormaliser.normaliseName(paramName), dbp);
        }

        if ("FUNCTION".equals(type)) {
            StringTokenizer s = new StringTokenizer(returns.trim()
                    .toLowerCase(), " ()");
            dataType = s.nextToken();

            if (!dataType.equals("void")) {
                allParams.put("", new DbParameterAccessor("",
                        Direction.RETURN_VALUE, getSqlType(dataType),
                        getJavaClass(dataType), -1));
            }
        }

        return allParams;
    }

    public String buildInsertCommand(String tableName,
            DbParameterAccessor[] accessors) {
        /*
         * oracle jdbc interface with callablestatement has problems with
         * returning into...
         * http://forums.oracle.com/forums/thread.jspa?threadID
         * =438204&tstart=0&messageID=1702717 so begin/end block has to be built
         * around it
         */
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
