package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.environment.postgres.NameNormaliserPostgres;
import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.NameNormaliser;
import dbfit.util.DatabaseObjectName;
import static dbfit.util.Direction.*;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

@DatabaseEnvironment(name = "Postgres", driver = "org.postgresql.Driver")
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

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName) throws SQLException {
        DatabaseObjectName objName = DatabaseObjectName.splitWithDelimiter(tableOrViewName, "\\.",
                getConnection().getSchema());

        return readIntoParams(
                new String[] { NameNormaliserPostgres.normaliseName(objName.getSchemaName()),
                        NameNormaliserPostgres.normaliseName(objName.getObjectName()) },
                "select " + "    column_name, data_type, character_maximum_length as direction " + "from "
                        + "    information_schema.columns " + "where " + "    table_schema = ? and table_name = ? "
                        + "order by " + "    ordinal_position");
    }

    private Map<String, DbParameterAccessor> readIntoParams(String[] queryParameters, String query)
            throws SQLException {
        try (PreparedStatement dc = prepareStatement(query, queryParameters); ResultSet rs = dc.executeQuery();) {
            Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();
            int position = 0;
            while (rs.next()) {
                String paramName = rs.getString(1);
                if (paramName == null)
                    paramName = "";
                // fix escaping
                paramName = paramName.replace("\"", "\"\"");
                String dataType = rs.getString(2);
                DbParameterAccessor dbp = createDbParameterAccessor('"' + paramName + '"', Direction.INPUT,
                        getSqlType(dataType), getJavaClass(dataType), position++);
                allParams.put(NameNormaliser.normaliseName(paramName), dbp);
            }

            return allParams;
        }
    }

    // List interface has sequential search, so using list instead of array to
    // map types
    private static List<String> stringTypes = Arrays.asList(new String[] { "VARCHAR", "CHAR", "CHARACTER",
            "CHARACTER VARYING", "TEXT", "NAME", "XML", "BPCHAR", "UNKNOWN" });
    private static List<String> intTypes = Arrays
            .asList(new String[] { "SMALLINT", "INT", "INT4", "INT2", "INTEGER", "SERIAL" });
    private static List<String> longTypes = Arrays.asList(new String[] { "BIGINT", "BIGSERIAL", "INT8" });
    private static List<String> floatTypes = Arrays.asList(new String[] { "REAL", "FLOAT4" });
    private static List<String> doubleTypes = Arrays.asList(new String[] { "DOUBLE PRECISION", "FLOAT8", "FLOAT" });
    private static List<String> decimalTypes = Arrays.asList(new String[] { "DECIMAL", "NUMERIC" });
    private static List<String> dateTypes = Arrays.asList(new String[] { "DATE" });
    private static List<String> timestampTypes = Arrays.asList(
            new String[] { "TIMESTAMP", "TIMESTAMP WITHOUT TIME ZONE", "TIMESTAMP WITH TIME ZONE", "TIMESTAMPTZ" });
    private static List<String> refCursorTypes = Arrays.asList(new String[] { "REFCURSOR" });
    private static List<String> booleanTypes = Arrays.asList(new String[] { "BOOL", "BOOLEAN" });

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
        throw new UnsupportedOperationException("Type " + dataType + " is not supported");
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(String procName) throws SQLException {
        DatabaseObjectName objName = buildProcedureName(procName);

        try (PreparedStatement dc = getProcedureParametersStatement(objName); ResultSet rs = dc.executeQuery()) {
            if (!rs.next()) {
                throw new SQLException("Unknown procedure " + procName);
            }

            return procedureParametersFrom(rs).getAllProcedureParameters();
        }
    }

    private DatabaseObjectName buildProcedureName(String procName) throws SQLException {
        return DatabaseObjectName.splitWithDelimiter(NameNormaliser.normaliseName(procName), "\\.",
                getConnection().getSchema());
    }

    private PreparedStatement getProcedureParametersStatement(DatabaseObjectName procName) throws SQLException {
        return prepareStatement("select "
                + "    coalesce(pro.proargnames, array_fill(''::\"char\", ARRAY[array_length(arg_types, 1)])) as param_names, "
                + "    array( " + "        select " + "            pt.typname " + "        from "
                + "            generate_series(array_lower(pro.arg_types, 1), array_upper(pro.arg_types, 1)) as t(id) "
                + "            join pg_type pt on (pt.oid = pro.arg_types[t.id]) " + "        order by t.id "
                + "    ) as param_types, "
                + "    coalesce(proargmodes, array_fill('i'::\"char\", ARRAY[array_length(arg_types, 1)])) as param_modes, "
                + "    (select typname from pg_type pt where pt.oid = pro.prorettype) as return_type " + "from "
                + "    (select coalesce(p.proallargtypes, p.proargtypes) as arg_types, p.* from pg_proc p) pro "
                + "    join pg_namespace ns on (ns.oid = pro.pronamespace) " + "where "
                + "    lower(ns.nspname) = ? and lower(pro.proname) = ?", procName.getQualifiers());
    }

    private ProcedureParameters procedureParametersFrom(ResultSet rs) throws SQLException {
        return new ProcedureParameters((String[]) rs.getArray("param_names").getArray(),
                (String[]) rs.getArray("param_types").getArray(), (String[]) rs.getArray("param_modes").getArray(),
                rs.getString("return_type"));
    }

    private PreparedStatement prepareStatement(String query, String[] queryParameters) throws SQLException {
        PreparedStatement statement = currentConnection.prepareStatement(query);
        try {
            for (int i = 0; i < queryParameters.length; i++) {
                statement.setString(i + 1, queryParameters[i]);
            }
        } catch (Throwable t) {
            statement.close();
            throw t;
        }

        return statement;
    }

    private class ProcedureParameters {
        private String[] paramNames;
        private String[] paramTypes;
        private String[] paramModes;
        private String returnType;

        ProcedureParameters(String[] paramNames, String[] paramTypes, String[] paramModes, String returnType) {
            this.paramNames = paramNames;
            this.paramTypes = paramTypes;
            this.paramModes = paramModes;
            this.returnType = returnType;
        }

        Map<String, DbParameterAccessor> getAllProcedureParameters() {
            Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();
            String returnType = this.returnType;

            for (int i = 0; i < paramTypes.length; ++i) {
                DbParameterAccessor dbp = parameterAt(i);
                addSingleParam(allParams, dbp);

                if (dbp.getDirection().isOutOrInout()) {
                    returnType = paramTypes[i];
                }
            }

            if (!returnType.equals("void")) {
                addSingleParam(allParams, returnValueOf(returnType));
            }

            return allParams;
        }

        private DbParameterAccessor parameterAt(int pos) {
            return createDbParameterAccessor(paramNames[pos].isEmpty() ? ("$" + (pos + 1)) : paramNames[pos],
                    parseDirection(paramModes[pos]), getSqlType(paramTypes[pos]), getJavaClass(paramTypes[pos]), pos);
        }

        private DbParameterAccessor returnValueOf(String returnType) {
            return createDbParameterAccessor("", RETURN_VALUE, getSqlType(returnType), getJavaClass(returnType), -1);
        }

        private void addSingleParam(Map<String, DbParameterAccessor> allParams, DbParameterAccessor dbp) {
            allParams.put(NameNormaliser.normaliseName(dbp.getName()), dbp);
        }

        private Direction parseDirection(final String mode) {
            switch (mode) {
            case "i":
                return INPUT;
            case "b":
                return INPUT_OUTPUT;
            case "o":
                return OUTPUT;
            default:
                throw new RuntimeException("Unknown direction: " + mode);
            }
        }
    }
}
