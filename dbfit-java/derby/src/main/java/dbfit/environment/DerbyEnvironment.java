package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;
import dbfit.util.NameNormaliser;
import dbfit.util.Direction;

import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * Encapsulates support for the Derby database (also known as JavaDB). Operates
 * in Client mode.
 *
 * @see EmbeddedDerbyEnvironment
 * @author P&aring;l Brattberg, pal.brattberg@acando.com
 */
@DatabaseEnvironment(name="Derby", driver="org.apache.derby.jdbc.ClientDriver")
public class DerbyEnvironment extends AbstractDbEnvironment {
    private TypeMapper typeMapper = new DerbyTypeMapper();

    public DerbyEnvironment(String driverClassName) {
        super(driverClassName);
    }

    @Override
    protected String getConnectionString(String dataSource) {
        return String.format("jdbc:derby://%s", dataSource);
    }

    @Override
    protected String getConnectionString(String dataSource, String database) {
        return String.format("jdbc:derby://%s/%s", dataSource, database);
    }

    private static final String paramNamePattern = "@([A-Za-z0-9_]+)";
    private static final Pattern paramRegex = Pattern.compile(paramNamePattern);

    @Override
    public Pattern getParameterPattern() {
        return paramRegex;
    }

    @Override
    public Map<String, DbParameterAccessor> getAllColumns(
            final String tableOrViewName) throws SQLException {
        DatabaseObjectName name = buildDatabaseObjectName(tableOrViewName);
        String qry =
        "SELECT C.COLUMNNAME" +
        "           AS COLUMN_NAME" +
        "     , C.COLUMNDATATYPE" +
        "           AS TYPE_NAME" +
        "     , ROW_NUMBER() OVER()" +
        "           AS ORDINAL_POSITION" +
        "     , 'INPUT'" +
        "           AS COLUMN_DIRECTION" +
        "  FROM SYS.SYSCOLUMNS C" +
        "     , SYS.SYSTABLES T" +
        "     , SYS.SYSSCHEMAS S" +
        " WHERE C.REFERENCEID = T.TABLEID" +
        "   AND T.TABLENAME = ?" +
        "   AND T.SCHEMAID = S.SCHEMAID" +
        "   AND S.SCHEMANAME = ?" +
        " ORDER" +
        "    BY C.COLUMNNUMBER";
        return fetchIntoParams(name, qry);
    }

    private Map<String, DbParameterAccessor> fetchIntoParams(DatabaseObjectName name, String query)
            throws SQLException {
        try (PreparedStatement dc = getConnection().prepareStatement(query)) {
            dc.setString(2, name.getSchemaName());
            dc.setString(1, name.getObjectName());
            try (ResultSet rs = dc.executeQuery()) {
                return createParamMap(rs);
            }
        }
    }

    private Map<String, DbParameterAccessor> createParamMap(ResultSet rs)
            throws SQLException {
        Map<String, DbParameterAccessor> allParams = new HashMap<>();
        while (rs.next()) {
            String paramName = defaultIfNull(rs.getString("COLUMN_NAME"), "");
            String paramTypeName = rs.getString("TYPE_NAME");
            allParams.put(NameNormaliser.normaliseName(paramName),
                createDbParameterAccessor(
                    paramName, getParamDirection(rs.getString("COLUMN_DIRECTION")),
                    typeMapper.getJDBCSQLTypeForDBType(paramTypeName),
                    getJavaClass(paramTypeName), rs.getInt("ORDINAL_POSITION") - 1));
        }
        return allParams;
    }

    private Direction getParamDirection(String direction) throws SQLException {
        switch (direction) {
            case "INPUT":
                return Direction.INPUT;
            case "INPUT_OUTPUT":
                return Direction.INPUT_OUTPUT;
            case "OUTPUT":
                return Direction.OUTPUT;
            case "RETURN_VALUE":
                return Direction.RETURN_VALUE;
            default:
                throw new SQLException("Invalid column/parameter direction string " + direction);
        }
    }

    private static class DatabaseObjectName {
        String schemaName, objectName;

        private DatabaseObjectName(String objSchemaName, String objName) {
            schemaName = objSchemaName;
            objectName = objName;
        }

        private String getSchemaName() {
            return schemaName;
        }

        String getObjectName() {
            return objectName;
        }
    }

    DatabaseObjectName buildDatabaseObjectName(String objName) throws SQLException {
        String[] qualifiers = objName.toUpperCase().split("\\.");
        String schemaName = (qualifiers.length == 2) ? qualifiers[0] : getConnection().getSchema();
        String objectName = (qualifiers.length == 2) ? qualifiers[1] : qualifiers[0];
        return new DatabaseObjectName(schemaName, objectName);
    }

    @Override
    public Map<String, DbParameterAccessor> getAllProcedureParameters(String procName)
            throws SQLException {
        DatabaseObjectName name = buildDatabaseObjectName(procName);
        // https://github.com/apache/derby/blob/10.13/java/engine/org/apache/derby/impl/jdbc/metadata.properties
        String sql =
        "SELECT V.COLUMN_NAME" +
        "           AS COLUMN_NAME" +
        "     , CASE WHEN A.ALIASTYPE = 'P'" +
        "            THEN CASE WHEN V.COLUMN_TYPE = " + DatabaseMetaData.procedureColumnIn +
        "                      THEN 'INPUT'" +
        "                      WHEN V.COLUMN_TYPE = " + DatabaseMetaData.procedureColumnInOut +
        "                      THEN 'INPUT_OUTPUT'" +
        "                      WHEN V.COLUMN_TYPE = " + DatabaseMetaData.procedureColumnOut +
        "                      THEN 'OUTPUT'" +
        "                      ELSE NULL" +
        "                  END" +
        "            WHEN A.ALIASTYPE = 'F'" +
        "            THEN CASE WHEN V.COLUMN_TYPE = " + DatabaseMetaData.functionColumnIn +
        "                      THEN 'INPUT'" +
        "                      WHEN V.COLUMN_TYPE = " + DatabaseMetaData.functionReturn +
        "                      THEN 'RETURN_VALUE'" +
        "                      ELSE NULL" +
        "             END" +
        "        END" +
        "           AS COLUMN_DIRECTION" +
        "     , V.DATA_TYPE" +
        "           AS DATA_TYPE" +
        "     , V.TYPE_NAME" +
        "           AS TYPE_NAME" +
        "     , CASE WHEN (V.COLUMN_TYPE = 5)" +
        "            THEN CAST((V.PARAMETER_ID + 1 - V.METHOD_ID) AS INT)" +
        "            ELSE CAST((V.PARAMETER_ID + 1) AS INT)" +
        "        END" +
        "           AS ORDINAL_POSITION" +
        "     , V.PARAMETER_ID" +
        "           AS PARAMETER_ID" +
        "  FROM SYS.SYSALIASES A" +
        "     , SYS.SYSSCHEMAS S" +
        "     , NEW org.apache.derby.catalog.GetProcedureColumns(A.ALIASINFO, A.ALIASTYPE) V" +
        " WHERE A.ALIASTYPE = '<<ProcFuncTypeFlag>>'" +
        "   AND A.SCHEMAID = S.SCHEMAID" +
        "   AND A.ALIAS = ?" +
        "   AND S.SCHEMANAME = ?" +
        " ORDER" +
        "    BY PARAMETER_ID" +
        "     , ORDINAL_POSITION";
        String query = sql.replaceAll("<<ProcFuncTypeFlag>>", "P");
        Map<String, DbParameterAccessor> params = fetchIntoParams(name, query);
        if (!params.isEmpty()) {
            return params;
        }
        query = sql.replaceAll("<<ProcFuncTypeFlag>>", "F");
        return fetchIntoParams(name, query);
    }

    @Override
    public Class<?> getJavaClass(final String dataType) {
        return typeMapper.getJavaClassForDBType(dataType);
    }

    /**
     * Interface for mapping of db types to java types.
     */
    public static interface TypeMapper {
        Class<?> getJavaClassForDBType(final String dbDataType);

        int getJDBCSQLTypeForDBType(final String dbDataType);
    }

    /**
     * From http://db.apache.org/derby/docs/10.4/ref/ref-single.html
     */
    public static class DerbyTypeMapper implements TypeMapper {
        private static final List<String> stringTypes = Arrays
                .asList(new String[] { "CHAR", "CHARACTER", "LONG VARCHAR",
                        "VARCHAR", "XML", "CHAR VARYING", "CHARACTER VARYING",
                        "LONG VARCHAR FOR BIT DATA", "VARCHAR FOR BIT DATA" });
        private static final List<String> intTypes = Arrays
                .asList(new String[] { "INTEGER", "INT" });
        private static final List<String> longTypes = Arrays
                .asList(new String[] { "BIGINT", });
        private static final List<String> doubleTypes = Arrays
                .asList(new String[] { "DOUBLE", "DOUBLE PRECISION", "FLOAT" });
        private static final List<String> floatTypes = Arrays
                .asList(new String[] { "REAL" });
        private static final List<String> shortTypes = Arrays
                .asList(new String[] { "SMALLINT" });
        private static final List<String> decimalTypes = Arrays
                .asList(new String[] { "DECIMAL", "DEC", "NUMERIC" });
        private static final List<String> dateTypes = Arrays
                .asList(new String[] { "DATE" });
        private static final List<String> timestampTypes = Arrays
                .asList(new String[] { "TIMESTAMP", });
        private static final List<String> timeTypes = Arrays
                .asList(new String[] { "TIME" });

        @Override
        public Class<?> getJavaClassForDBType(final String dbDataType) {
            String dataType = normaliseTypeName(dbDataType);
            if (stringTypes.contains(dataType))
                return String.class;
            if (decimalTypes.contains(dataType))
                return BigDecimal.class;
            if (intTypes.contains(dataType))
                return Integer.class;
            if (timeTypes.contains(dataType))
                return Time.class;
            if (dateTypes.contains(dataType))
                return java.sql.Date.class;
            if (floatTypes.contains(dataType))
                return Float.class;
            if (shortTypes.contains(dataType))
                return Short.class;
            if (doubleTypes.contains(dataType))
                return Double.class;
            if (longTypes.contains(dataType))
                return Long.class;
            if (timestampTypes.contains(dataType))
                return java.sql.Timestamp.class;
            throw new UnsupportedOperationException("Type '" + dbDataType
                    + "' is not supported for Derby");
        }

        @Override
        public int getJDBCSQLTypeForDBType(final String dbDataType) {
            String dataType = normaliseTypeName(dbDataType);
            if (stringTypes.contains(dataType))
                return java.sql.Types.VARCHAR;
            if (decimalTypes.contains(dataType))
                return java.sql.Types.DECIMAL;
            if (intTypes.contains(dataType) || shortTypes.contains(dataType))
                return java.sql.Types.INTEGER;
            if (floatTypes.contains(dataType))
                return java.sql.Types.FLOAT;
            if (doubleTypes.contains(dataType))
                return java.sql.Types.DOUBLE;
            if (longTypes.contains(dataType))
                return java.sql.Types.BIGINT;
            if (timestampTypes.contains(dataType))
                return java.sql.Types.TIMESTAMP;
            if (timeTypes.contains(dataType))
                return java.sql.Types.TIME;
            if (dateTypes.contains(dataType))
                return java.sql.Types.DATE;
            throw new UnsupportedOperationException("Type '" + dbDataType
                    + "' is not supported for Derby");
        }

        private static String normaliseTypeName(String type) {
            if (type != null && !"".equals(type)) {
                String dataType = type.toUpperCase().trim();
                // remove any size declarations such as CHAR(nn)
                int idxLeftPara = dataType.indexOf('(');
                if (idxLeftPara > 0) {
                    dataType = dataType.substring(0, idxLeftPara);
                }
                // remove any modifiers such as CHAR NOT NULL, but keep support
                // for INTEGER UNSIGNED. Yes, I know this is funky coding, but
                // it works, just see the unit tests! ;)
                idxLeftPara = dataType.indexOf(" NOT NULL");
                if (idxLeftPara > 0) {
                    dataType = dataType.substring(0, idxLeftPara);
                }
                idxLeftPara = dataType.indexOf(" NULL");
                if (idxLeftPara > 0) {
                    dataType = dataType.substring(0, idxLeftPara);
                }
                return dataType;
            } else {
                throw new IllegalArgumentException(
                        "You must specify a valid type for conversions");
            }
        }
    }
}
