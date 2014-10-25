package dbfit.environment;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.api.DbStoredProcedureCall;
import dbfit.util.*;
import oracle.jdbc.OracleTypes;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

import fit.TypeAdapter;

@DatabaseEnvironment(name="Oracle", driver="oracle.jdbc.OracleDriver")
public class OracleEnvironment extends AbstractDbEnvironment {
    private static String SKIP_ORACLE_SYNONYMS = "SKIPORACLESYNONYMS";

    public static class OracleTimestampParser {
        public static Object parse(String s) throws Exception {
            return new oracle.sql.TIMESTAMP(
                    (java.sql.Timestamp) SqlTimestampParseDelegate.parse(s));
        }
    }

    private static enum InfoSource {
        DB_DICTIONARY,
        JDBC_RESULT_SET_META_DATA
    }

    private static class DbParameterOrColumnInfo {
        String name = null;
        String direction = null;
        String dataType = null;
        String userDefinedTypeName;
        int position = -1;
    }

    private static boolean isReturnValueParameter(String paramName) {
        return (paramName == null) || paramName.trim().isEmpty();
    }

    static abstract class AbstractParamsOrColumnsIterator implements Iterator<DbParameterOrColumnInfo> {
        protected int position;
        protected DbParameterOrColumnInfo info;

        abstract protected boolean readNext() throws SQLException;

        protected void incrementPosition() {
            if (!isReturnValueParameter(info.name)) {
                ++position;
            }
        }

        protected boolean fetchNext() throws SQLException {
            if (readNext()) {
                incrementPosition();
                return true;
            }
            return false;
        }

        @Override
        public boolean hasNext() {
            try {
                if (info != null) {
                    return true;
                } else {
                    return fetchNext();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public DbParameterOrColumnInfo next() {
            if (hasNext()) {
                DbParameterOrColumnInfo result = info;
                info = null;
                return result;
            } else {
                throw new java.util.NoSuchElementException();
            }
        }

        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Iterate over ResultSet with db dictionary meta data about parameters or columns
     */
    static class DbDictionaryParamsOrColumnsIterator extends AbstractParamsOrColumnsIterator
            implements Iterator<DbParameterOrColumnInfo> {

        private ResultSet rs;
        
        private DbDictionaryParamsOrColumnsIterator(ResultSet rs) {
            this.rs = rs;
            this.position = 0;
            this.info = null;
        }

        public static DbDictionaryParamsOrColumnsIterator newInstance(ResultSet rs) {
            return new DbDictionaryParamsOrColumnsIterator(rs);
        }

        @Override
        protected boolean readNext() throws SQLException {
            if (rs.next()) {
                readToInfo();
                return true;
            }

            return false;
        }

        private void readToInfo() throws SQLException {
            info = new DbParameterOrColumnInfo();
            info.name = rs.getString(1);
            info.dataType = rs.getString(2);
            info.direction = rs.getString(4);
            info.position = position;
            info.userDefinedTypeName = rs.getString(6);
        }
    }

    /**
     * Iterator over JdbcRsMeta of columns
     */
    static class JdbcRsMetaParamsOrColumnsIterator extends AbstractParamsOrColumnsIterator
            implements Iterator<DbParameterOrColumnInfo> {
        private ResultSetMetaData md;
        private int currentColumn = -1;
        private int columnCount;
        
        private JdbcRsMetaParamsOrColumnsIterator(ResultSetMetaData md) throws SQLException {
            this.md = md;
            this.position = 0;
            this.info = null;
            this.currentColumn = 0;
            this.columnCount = md.getColumnCount();
        }

        public static JdbcRsMetaParamsOrColumnsIterator newInstance(ResultSetMetaData md) throws SQLException {
            return new JdbcRsMetaParamsOrColumnsIterator(md);
        }

        @Override
        protected boolean readNext() throws SQLException {
            if (currentColumn < columnCount) {
                readToInfo();
                ++currentColumn;
                return true;
            }
            
            return false;
        }

        private String getColumnType(int columnID) throws SQLException {
            return md.getColumnTypeName(columnID);
        }

        private void readToInfo() throws SQLException {
            info = new DbParameterOrColumnInfo();

            int columnIndex = currentColumn + 1;

            info.name = md.getColumnName(columnIndex);
            info.dataType = getColumnType(columnIndex);

            setUserDefinedTypes(columnIndex);
            info.direction = "IN";
            info.position = position;
        }

        private void setUserDefinedTypes(int columnIndex) throws SQLException {
            switch (md.getColumnType(columnIndex)) {
                case OracleTypes.ARRAY:
                    info.userDefinedTypeName = info.dataType;
                    info.dataType = "TABLE";
                    break;
                case OracleTypes.STRUCT:
                    info.userDefinedTypeName = info.dataType;
                    info.dataType = "OBJECT";
            }
        }
    }

    private static Iterator<DbParameterOrColumnInfo> createParamsOrColumnsIterator(
            ResultSet rs, InfoSource infoSrc) throws SQLException {

        switch (infoSrc) {
            case DB_DICTIONARY:
                return DbDictionaryParamsOrColumnsIterator.newInstance(rs);
            case JDBC_RESULT_SET_META_DATA:
                return JdbcRsMetaParamsOrColumnsIterator.newInstance(rs.getMetaData());
            default:
                return null;
        }
    }

    @Override
    protected void afterConnectionEstablished() throws SQLException {
        super.afterConnectionEstablished();
        TypeAdapter.registerParseDelegate(java.sql.Struct.class,
                new OracleObjectTypeParseDelegate(this));
        TypeAdapter.registerParseDelegate(java.sql.Array.class,
                new OracleObjectTypeParseDelegate(this));
    }

    public OracleEnvironment(String driverClassName) {
        super(driverClassName);

        // TypeAdapter.registerParseDelegate(oracle.sql.TIMESTAMP.class,
        // OracleTimestampParser.class);
        TypeNormaliserFactory.setNormaliser(oracle.sql.TIMESTAMP.class,
                new OracleTimestampNormaliser());
        TypeNormaliserFactory.setNormaliser(oracle.sql.DATE.class,
                new OracleDateNormaliser());
        TypeNormaliserFactory.setNormaliser(oracle.sql.CLOB.class,
                new OracleClobNormaliser());
        TypeNormaliserFactory.setNormaliser(oracle.jdbc.rowset.OracleSerialClob.class,
                new OracleSerialClobNormaliser());
        TypeNormaliserFactory.setNormaliser(java.sql.Date.class,
                new SqlDateNormaliser());
        try {
            TypeNormaliserFactory.setNormaliser(
                    Class.forName("oracle.jdbc.driver.OracleResultSetImpl"),
                    new OracleRefNormaliser());
        } catch (Exception e) {
            throw new Error("Cannot initialise oracle rowset", e);
        }
    }

    public boolean supportsOuputOnInsert() {
        return true;
    }

    protected String getConnectionString(String dataSource) {
        return "jdbc:oracle:thin:@" + dataSource;
    }

    // for oracle, data source has to be host:port
    protected String getConnectionString(String dataSource, String databaseName) {
        if (dataSource.indexOf(":") == -1)
            throw new UnsupportedOperationException(
                    "data source should be in host:port format - " + dataSource
                            + " specified");
        return "jdbc:oracle:thin:@" + dataSource + ":" + databaseName;
    }

    private static Pattern paramsNames = Pattern.compile(":([A-Za-z0-9_]+)");

    public Pattern getParameterPattern() {
        return paramsNames;
    }

    public Map<String, DbParameterAccessor> getAllProcedureParameters(
            String procName) throws SQLException {
        String[] qualifiers = NameNormaliser.normaliseName(procName).split(
                "\\.");
        String cols = " argument_name, data_type, data_length,  IN_OUT, sequence, " +
                "(case when type_name is null then null else type_owner ||'.'|| type_name end) as type";
        String qry = "select " + cols
                + "  from all_arguments where data_level=0 and ";
        if (qualifiers.length == 3) {
            qry += " owner=? and package_name=? and object_name=? ";
        } else if (qualifiers.length == 2) {
            qry += " ((owner=? and package_name is null and object_name=?) or "
                    + " (owner=user and package_name=? and object_name=?))";
        } else {
            qry += " (owner=user and package_name is null and object_name=?)";
        }

        // map to public synonyms also
        if (qualifiers.length < 3 && (!Options.is(SKIP_ORACLE_SYNONYMS))) {
            qry += " union all "
                    + " select "
                    + cols
                    + " from all_arguments, all_synonyms "
                    + " where data_level=0 and all_synonyms.owner='PUBLIC' and all_arguments.owner=table_owner and ";
            if (qualifiers.length == 2) { // package
                qry += " package_name=table_name and synonym_name=? and object_name=? ";
            } else {
                qry += " package_name is null and object_name=table_name and synonym_name=? ";
            }
        }
        qry += " order by sequence ";
        if (qualifiers.length == 2) {
            String[] newQualifiers = new String[6];
            newQualifiers[0] = qualifiers[0];
            newQualifiers[1] = qualifiers[1];
            newQualifiers[2] = qualifiers[0];
            newQualifiers[3] = qualifiers[1];
            newQualifiers[4] = qualifiers[0];
            newQualifiers[5] = qualifiers[1];
            qualifiers = newQualifiers;
        } else if (qualifiers.length == 1) {
            String[] newQualifiers = new String[2];
            newQualifiers[0] = qualifiers[0];
            newQualifiers[1] = qualifiers[0];
            qualifiers = newQualifiers;
        }
        return readIntoParams(qualifiers, qry);
    }

    public Map<String, DbParameterAccessor> getAllColumns(String tableOrViewName)
            throws SQLException {
        String query = "select * from " + tableOrViewName + " where 1 = 2";
        return readIntoParams(new String[]{}, query, InfoSource.JDBC_RESULT_SET_META_DATA); 
    }

    private DbParameterAccessor addSingleParam(Map<String, DbParameterAccessor> allParams,
            DbParameterOrColumnInfo info) {
        DbParameterAccessor dbp = makeSingleParam(info);

        Log.log("read out " + dbp.getName() + " of " + info.dataType);

        allParams.put(NameNormaliser.normaliseName(dbp.getName()), dbp);

        return dbp;
    }

    private DbParameterAccessor makeSingleParam(DbParameterOrColumnInfo info) {
        return makeSingleParam(info.name, info.dataType, info.userDefinedTypeName, info.direction, info.position);
    }

    private DbParameterAccessor makeSingleParam(
            String paramName, String dataType, String userTypeName, String direction, int position) {
        if (paramName == null)
            paramName = "";
        Direction paramDirection;
        int paramPosition = position;
        if (isReturnValueParameter(paramName)) {
            paramDirection = Direction.RETURN_VALUE;
            paramPosition = -1;
        }
        else {
            paramDirection = getParameterDirection(direction);
        }

        DbParameterAccessor dbp = new OracleDbParameterAccessor(paramName,
                paramDirection, getSqlType(dataType), getJavaClass(dataType),
                paramPosition, normaliseTypeName(dataType), userTypeName);

        return dbp;
    }

    private Map<String, DbParameterAccessor> readIntoParams(
            String[] queryParameters, String query, InfoSource infoSrc) throws SQLException {

        try (CallableStatement dc = openDbCallWithParameters(query, queryParameters)) {
            Log.log("executing query");
            ResultSet rs = dc.executeQuery();
            Map<String, DbParameterAccessor> allParams = new HashMap<String, DbParameterAccessor>();
            Iterator<DbParameterOrColumnInfo> iter = createParamsOrColumnsIterator(rs, infoSrc);

            while (iter.hasNext()) {
                addSingleParam(allParams, iter.next());
            }

            return allParams;
        }
    }

    private Map<String, DbParameterAccessor> readIntoParams(
            String[] queryParameters, String query) throws SQLException {
        return readIntoParams(queryParameters, query, InfoSource.DB_DICTIONARY);
    }

    private CallableStatement openDbCallWithParameters(String query,
            String[] queryParameters) throws SQLException {
        Log.log("preparing call " + query, (Object[]) queryParameters);
        CallableStatement dc = currentConnection.prepareCall(query);
        Log.log("setting parameters");
        for (int i = 0; i < queryParameters.length; i++) {
            dc.setString(i + 1, queryParameters[i].toUpperCase());
        }

        return dc;
    }


    // List interface has sequential search, so using list instead of array to
    // map types
    private static List<String> stringTypes = Arrays.asList(new String[] {
            "VARCHAR", "VARCHAR2", "NVARCHAR2", "CHAR", "NCHAR", "CLOB",
            "NCLOB", "ROWID", "BOOLEAN"
        });
    private static List<String> decimalTypes = Arrays.asList(new String[] {
            "BINARY_INTEGER", "NUMBER", "FLOAT" });
    private static List<String> dateTypes = Arrays.asList(new String[] {});
    private static List<String> timestampTypes = Arrays.asList(new String[] {
            "DATE", "TIMESTAMP" });
    private static List<String> refCursorTypes = Arrays
            .asList(new String[] { "REF" });
    private static List<String> objectTypes = Arrays.asList(new String[] {
            "OBJECT", "MDSYS.SDO_GEOMETRY" });

    private static List<String> recordTypes = Arrays.asList(new String[] {
            "VARRAY", "TABLE" });

    private static String normaliseTypeName(String dataType) {
        dataType = dataType.toUpperCase().trim();
        if (dataType.endsWith("BOOLEAN")) {
            return "BOOLEAN";
        }

        int idx = dataType.indexOf(" ");
        if (idx >= 0)
            dataType = dataType.substring(0, idx);
        idx = dataType.indexOf("(");
        if (idx >= 0)
            dataType = dataType.substring(0, idx);
        return dataType;
    }

    private static int getSqlType(String dataType) {
        // todo:strip everything from first blank
        String dataTypeNormalised = normaliseTypeName(dataType);

        if (stringTypes.contains(dataTypeNormalised))
            return java.sql.Types.VARCHAR;
        if (decimalTypes.contains(dataTypeNormalised))
            return java.sql.Types.NUMERIC;
        if (dateTypes.contains(dataTypeNormalised))
            return java.sql.Types.DATE;
        if (refCursorTypes.contains(dataTypeNormalised))
            return OracleTypes.CURSOR;
        if (timestampTypes.contains(dataTypeNormalised))
            return java.sql.Types.TIMESTAMP;
        if (objectTypes.contains(dataTypeNormalised))
            return OracleTypes.STRUCT;
        if (recordTypes.contains(dataTypeNormalised))
            return OracleTypes.ARRAY;

        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    public Class<?> getJavaClass(String dataType) {
        dataType = normaliseTypeName(dataType);
        if (stringTypes.contains(dataType))
            return String.class;
        if (decimalTypes.contains(dataType))
            return BigDecimal.class;
        if (dateTypes.contains(dataType))
            return java.sql.Date.class;
        if (refCursorTypes.contains(dataType))
            return ResultSet.class;
        if (timestampTypes.contains(dataType))
            return java.sql.Timestamp.class;
        if (objectTypes.contains(dataType))
            return java.sql.Struct.class;
        if (recordTypes.contains(dataType))
            return java.sql.Array.class;

        throw new UnsupportedOperationException("Type " + dataType
                + " is not supported");
    }

    private static Direction getParameterDirection(String direction) {
        if ("IN".equals(direction))
            return Direction.INPUT;
        if ("OUT".equals(direction))
            return Direction.OUTPUT;
        if ("IN/OUT".equals(direction))
            return Direction.INPUT_OUTPUT;
        // todo return val
        throw new UnsupportedOperationException("Direction " + direction
                + " is not supported");
    }

    public String buildInsertCommand(String tableName,
            DbParameterAccessor[] accessors) {
        Log.log("buiding insert command for " + tableName);

        /*
         * oracle jdbc interface with callablestatement has problems with
         * returning into...
         * http://forums.oracle.com/forums/thread.jspa?threadID
         * =438204&tstart=0&messageID=1702717 so begin/end block has to be built
         * around it
         */
        StringBuilder sb = new StringBuilder("begin insert into ");
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
                // values.append(":").append(accessor.getName());
                values.append("?");
                comma = ",";
            } else {
                retNames.append(retComma);
                retValues.append(retComma);
                retNames.append(accessor.getName());
                // retValues.append(":").append(accessor.getName());
                retValues.append("?");
                retComma = ",";
            }
        }
        sb.append(") values (");
        sb.append(values);
        sb.append(")");
        if (retValues.length() > 0) {
            sb.append(" returning ").append(retNames).append(" into ")
                    .append(retValues);
        }
        sb.append("; end;");
        Log.log("built " + sb.toString());
        return sb.toString();
    }

    @Override
    public DbParameterAccessor createAutogeneratedPrimaryKeyAccessor(
            DbParameterAccessor template) {
        DbParameterAccessor accessor2 = template.clone();
        accessor2.setDirection(Direction.OUTPUT);
        return accessor2;
    }

    @Override
    public PreparedStatement buildInsertPreparedStatement(String tableName,
            DbParameterAccessor[] accessors) throws SQLException {
        return getConnection().prepareCall(
                buildInsertCommand(tableName, accessors));
    }

    @Override
    public DbStoredProcedureCall newStoredProcedureCall(String name, DbParameterAccessor[] accessors) {
        return new OracleStoredProcedureCall(this, name, accessors);
    }
}
