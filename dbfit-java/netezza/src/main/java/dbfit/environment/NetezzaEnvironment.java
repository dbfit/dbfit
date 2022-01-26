package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.Direction;
import dbfit.util.NameNormaliser;
import dbfit.fixture.StatementExecution;
import dbfit.fixture.StatementExecutionCapturingResultSetValue;
import static dbfit.util.Direction.*;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@DatabaseEnvironment(name="Netezza", driver="org.netezza.Driver")
public class NetezzaEnvironment extends AbstractDbEnvironment {
    public NetezzaEnvironment(String driverClassName) {
        super(driverClassName);
        defaultParamPatternString = "_:([A-Za-z0-9_]+)";
    }

    protected String getConnectionString(String dataSource) {
        return "jdbc:netezza://" + dataSource;
    }

    protected String getConnectionString(String dataSource, String database) {
        return "jdbc:netezza://" + dataSource + "/" + database;
    }

    // override the buildInsertPreparedStatement to leave out RETURN_GENERATED_KEYS
    public PreparedStatement buildInsertPreparedStatement(String tableName,
            DbParameterAccessor[] accessors) throws SQLException {
        return getConnection().prepareStatement(
                buildInsertCommand(tableName, accessors));
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
                DbParameterAccessor dbp = createDbParameterAccessor(
                        paramName,
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

    @Override
    public StatementExecution createFunctionStatementExecution(PreparedStatement statement) {
        return new StatementExecutionCapturingResultSetValue(statement, 0);
    }

    @Override
    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {
        try (PreparedStatement ps = getProcedureParametersStatement(procName);
             ResultSet rs = ps.executeQuery()
        ) {
            if (!rs.next()) {
                throw new SQLException("Unknown procedure " + procName);
            }

            String[] paramList = rs.getString(1).split(",");
            String returnType = rs.getString(2);

            return new ProcedureParameters(paramList, returnType).getAllProcedureParameters();
        }
    }

    private PreparedStatement getProcedureParametersStatement(String procName) throws SQLException {
        String[] qualifiers = NameNormaliser.normaliseName(procName).split("\\.");
        String qry = "select btrim(btrim(arguments,'('),')') as param_list, returns from _v_procedure where 1 = 1";

        if (qualifiers.length == 3) {
            qry += " and lower(database) = ? and lower(schema) = ? and lower(procedure) = ? ";
        } else if (qualifiers.length == 2) {
            qry += " and lower(schema) = ? and lower(procedure) = ? ";
        } else {
            qry += " and lower(procedure)=? ";
        }

        return prepareStatement(qry, qualifiers);
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
        private String[] paramTypes;
        private String returnType;

        ProcedureParameters(String[] paramTypes, String returnType) {
            this.paramTypes = paramTypes;
            this.returnType = returnType;
        }

        Map<String, DbParameterAccessor> getAllProcedureParameters() {
            Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();

            for (int i = 0; i < paramTypes.length; ++i) {
                addSingleParam(allParams, parameterAt(i));
            }

            if (returnType != null) {
                addSingleParam(allParams, returnValueOf(returnType));
            }

            return allParams;
        }

        private DbParameterAccessor parameterAt(int pos) {
            return createAccessor("$" + (pos + 1), INPUT, paramTypes[pos], pos);
        }

        private DbParameterAccessor returnValueOf(String returnType) {
            return createAccessor("", RETURN_VALUE, returnType, -1);
        }

        private DbParameterAccessor createAccessor(
                String name, Direction direction, String type, int pos) {
            String dataType = reduceType(type);
            return createDbParameterAccessor(
                    name, direction, getSqlType(dataType), getJavaClass(dataType), pos);
        }

        private void addSingleParam(Map<String, DbParameterAccessor> allParams, DbParameterAccessor dbp) {
            allParams.put(NameNormaliser.normaliseName(dbp.getName()), dbp);
        }

        private String reduceType(String paramType) {
            return paramType.split("\\(")[0].trim();
        }
    }
}
