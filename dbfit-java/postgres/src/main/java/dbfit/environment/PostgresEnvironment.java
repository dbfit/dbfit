package dbfit.environment;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.sql.RowSet;

import org.postgresql.util.PGobject;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.NameNormaliser;

@DatabaseEnvironment(name = "Postgres", driver = "org.postgresql.Driver")
public class PostgresEnvironment extends AbstractDbEnvironment {
    private static final String paramNamePattern = "_:([A-Za-z0-9_]+)";
    private static final Pattern paramsNames;
    private static final List<String> stringTypes;
    private static final List<String> intTypes;
    private static final List<String> longTypes;
    private static final List<String> floatTypes;
    private static final List<String> doubleTypes;
    private static final List<String> decimalTypes;
    private static final List<String> dateTypes;
    private static final List<String> timestampTypes;
    private static final List<String> refCursorTypes;
    private static final List<String> booleanTypes;
    private static final List<String> jsonTypes;

    public PostgresJsonEnvironment(final String driverClassName) {
        super(driverClassName);
    }

    @Override
    protected String getConnectionString(final String dataSource) {
        return "jdbc:postgresql://" + dataSource;
    }

    @Override
    protected String getConnectionString(final String dataSource, final String database) {
        return "jdbc:postgresql://" + dataSource + "/" + database;
    }

    @Override
    public Pattern getParameterPattern() {
        return paramsNames;
    }

    @Override
    protected String parseCommandText(String commandText) {
        commandText = commandText.replaceAll(paramNamePattern, "?");
        return super.parseCommandText(commandText);
    }

    @Override
    public Map<String, DbParameterAccessor> getAllColumns(final String tableOrViewName) throws SQLException {
        final String[] qualifiers = NameNormaliser.normaliseName(tableOrViewName).split("\\.");
        String qry = " select column_name, data_type, character_maximum_length \tas direction from information_schema.columns where ";
        if (qualifiers.length == 2) {
            qry = qry + " lower(table_schema)=? and lower(table_name)=? ";
        }
        else {
            qry = qry + " (table_schema=current_schema() and lower(table_name)=?)";
        }

        qry = qry + " order by ordinal_position";
        return this.readIntoParams(qualifiers, qry);
    }

    private Map<String, DbParameterAccessor> readIntoParams(final String[] queryParameters, final String query)
            throws SQLException {
        final PreparedStatement dc = this.currentConnection.prepareStatement(query);
        Throwable var4 = null;

        Map var21;
        try {
            for (int i = 0; i < queryParameters.length; ++i) {
                dc.setString(i + 1, NameNormaliser.normaliseName(queryParameters[i]));
            }

            final ResultSet rs = dc.executeQuery();
            final Map<String, DbParameterAccessor> allParams = new HashMap();
            int var7 = 0;

            while (rs.next()) {
                String paramName = rs.getString(1);
                if (paramName == null) {
                    paramName = "";
                }

                final String dataType = rs.getString(2);
                final DbParameterAccessor dbp = new DbParameterAccessor(paramName, Direction.INPUT,
                        getSqlType(dataType), this.getJavaClass(dataType), var7++);
                allParams.put(NameNormaliser.normaliseName(paramName), dbp);
            }

            rs.close();
            var21 = allParams;
        }
        catch (final Throwable var18) {
            var4 = var18;
            throw var18;
        }
        finally {
            if (dc != null) {
                if (var4 != null) {
                    try {
                        dc.close();
                    }
                    catch (final Throwable var17) {
                        var4.addSuppressed(var17);
                    }
                }
                else {
                    dc.close();
                }
            }
        }

        return var21;
    }

    private static String normaliseTypeName(String dataType) {
        dataType = dataType.toUpperCase().trim();
        return dataType;
    }

    private static int getSqlType(String dataType) {
        dataType = normaliseTypeName(dataType);
        if (stringTypes.contains(dataType)) {
            return 12;
        }
        else if (decimalTypes.contains(dataType)) {
            return 2;
        }
        else if (intTypes.contains(dataType)) {
            return 4;
        }
        else if (floatTypes.contains(dataType)) {
            return 6;
        }
        else if (doubleTypes.contains(dataType)) {
            return 8;
        }
        else if (longTypes.contains(dataType)) {
            return -5;
        }
        else if (timestampTypes.contains(dataType)) {
            return 93;
        }
        else if (dateTypes.contains(dataType)) {
            return 91;
        }
        else if (refCursorTypes.contains(dataType)) {
            return 2006;
        }
        else if (booleanTypes.contains(dataType)) {
            return 16;
        }
        else if (jsonTypes.contains(dataType)) {
            return java.sql.Types.OTHER;
        }
        else {
            throw new UnsupportedOperationException("Type denis 1" + dataType + " is not supported");
        }
    }

    @Override
    public Class getJavaClass(String dataType) {
        dataType = normaliseTypeName(dataType);
        if (stringTypes.contains(dataType)) {
            return String.class;
        }
        else if (decimalTypes.contains(dataType)) {
            return BigDecimal.class;
        }
        else if (intTypes.contains(dataType)) {
            return Integer.class;
        }
        else if (floatTypes.contains(dataType)) {
            return Float.class;
        }
        else if (dateTypes.contains(dataType)) {
            return Date.class;
        }
        else if (refCursorTypes.contains(dataType)) {
            return RowSet.class;
        }
        else if (doubleTypes.contains(dataType)) {
            return Double.class;
        }
        else if (longTypes.contains(dataType)) {
            return Long.class;
        }
        else if (timestampTypes.contains(dataType)) {
            return Timestamp.class;
        }
        else if (booleanTypes.contains(dataType)) {
            return Boolean.class;
        }
        else if (jsonTypes.contains(dataType)) {
            return PGobject.class;
        }
        else {
            throw new UnsupportedOperationException("Type denis 2" + dataType + " is not supported");
        }
    }

    @Override
    public Map<String, DbParameterAccessor> getAllProcedureParameters(final String procName) throws SQLException {
        final String[] qualifiers = NameNormaliser.normaliseName(procName).split("\\.");
        String qry = "select 'FUNCTION' as type, array_to_string(array(select coalesce(pro.proargnames[t.id+1],'') || ' ' || pt.typname from generate_series(0, array_upper(pro.proargtypes, 1)) as t(id), pg_type pt where pt.oid = pro.proargtypes[t.id] order by t.id), ',') as param_list, (select typname from pg_type pt where pt.oid = pro.prorettype) as returns from pg_proc pro, pg_namespace ns where ns.oid = pro.pronamespace";
        if (qualifiers.length == 2) {
            qry = qry + " and lower(ns.nspname)=? and lower(proname)=? ";
        }
        else {
            qry = qry + " and (lower(ns.nspname)=current_schema() and lower(proname)=?)";
        }

        final PreparedStatement dc = this.currentConnection.prepareStatement(qry);
        Throwable var8 = null;

        String type;
        String paramList;
        String returns;
        try {
            for (int i = 0; i < qualifiers.length; ++i) {
                dc.setString(i + 1, NameNormaliser.normaliseName(qualifiers[i]));
            }

            final ResultSet rs = dc.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Unknown procedure " + procName);
            }

            type = rs.getString(1);
            paramList = rs.getString(2);
            returns = rs.getString(3);
            rs.close();
        }
        catch (final Throwable var24) {
            var8 = var24;
            throw var24;
        }
        finally {
            if (dc != null) {
                if (var8 != null) {
                    try {
                        dc.close();
                    }
                    catch (final Throwable var23) {
                        var8.addSuppressed(var23);
                    }
                }
                else {
                    dc.close();
                }
            }
        }

        int position = 0;
        Direction direction = Direction.INPUT;
        final Map<String, DbParameterAccessor> allParams = new HashMap();
        final String[] arr$ = paramList.split(",");
        final int len$ = arr$.length;

        String dataType;
        for (int i$ = 0; i$ < len$; ++i$) {
            final String param = arr$[i$];
            final StringTokenizer s = new StringTokenizer(param.trim().toLowerCase(), " ()");
            String token = s.nextToken();
            if (token.equals("in")) {
                token = s.nextToken();
            }
            else if (token.equals("inout")) {
                direction = Direction.INPUT_OUTPUT;
                token = s.nextToken();
            }
            else if (token.equals("out")) {
                direction = Direction.OUTPUT;
                token = s.nextToken();
            }

            final String paramName;
            if (s.hasMoreTokens()) {
                paramName = token;
                dataType = s.nextToken();
            }
            else {
                paramName = "$" + (position + 1);
                dataType = token;
            }

            final DbParameterAccessor dbp = new DbParameterAccessor(paramName, direction, getSqlType(dataType),
                    this.getJavaClass(dataType), position++);
            allParams.put(NameNormaliser.normaliseName(paramName), dbp);
        }

        if ("FUNCTION".equals(type)) {
            final StringTokenizer s = new StringTokenizer(returns.trim().toLowerCase(), " ()");
            dataType = s.nextToken();
            if (!dataType.equals("void")) {
                allParams.put(
                        "",
                        new DbParameterAccessor("", Direction.RETURN_VALUE, getSqlType(dataType),
                                this.getJavaClass(dataType), -1));
            }
        }

        return allParams;
    }

    @Override
    public String buildInsertCommand(final String tableName, final DbParameterAccessor[] accessors) {
        final StringBuilder sb = new StringBuilder("insert into ");
        sb.append(tableName).append("(");
        String comma = "";
        String retComma = "";
        final StringBuilder values = new StringBuilder();
        final StringBuilder retNames = new StringBuilder();
        final StringBuilder retValues = new StringBuilder();
        final DbParameterAccessor[] arr$ = accessors;
        final int len$ = accessors.length;

        for (int i$ = 0; i$ < len$; ++i$) {
            final DbParameterAccessor accessor = arr$[i$];
            if (accessor.hasDirection(Direction.INPUT)) {
                sb.append(comma);
                values.append(comma);
                sb.append(accessor.getName());
                values.append("?");
                comma = ",";
            }
            else {
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

    static {
        paramsNames = Pattern.compile(paramNamePattern);
        stringTypes = Arrays.asList(
                "VARCHAR",
                "CHAR",
                "CHARACTER",
                "CHARACTER VARYING",
                "TEXT",
                "NAME",
                "XML",
                "BPCHAR",
                "UNKNOWN");
        intTypes = Arrays.asList("SMALLINT", "INT", "INT4", "INT2", "INTEGER", "SERIAL");
        longTypes = Arrays.asList("BIGINT", "BIGSERIAL", "INT8");
        floatTypes = Arrays.asList("REAL", "FLOAT4");
        doubleTypes = Arrays.asList("DOUBLE PRECISION", "FLOAT8", "FLOAT");
        decimalTypes = Arrays.asList("DECIMAL", "NUMERIC");
        dateTypes = Arrays.asList("DATE");
        timestampTypes = Arrays
                .asList("TIMESTAMP", "TIMESTAMP WITHOUT TIME ZONE", "TIMESTAMP WITH TIME ZONE", "TIMESTAMPTZ");
        refCursorTypes = Arrays.asList("REFCURSOR");
        booleanTypes = Arrays.asList("BOOL", "BOOLEAN");
        jsonTypes = Arrays.asList(new String[]{"JSONB", "JSON"});
    }
}
