package dbfit.api;

import dbfit.util.*;
import dbfit.fixture.StatementExecution;
import static dbfit.util.Options.OPTION_AUTO_COMMIT;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;

public abstract class AbstractDbEnvironment implements DBEnvironment {

    protected Connection currentConnection;
    protected String driverClassName;
    protected TypeTransformerFactory dbfitToJdbcTransformerFactory = new TypeTransformerFactory();

    protected String getDriverClassName() {
        return driverClassName;
    }

    private boolean driverRegistered = false;

    protected AbstractDbEnvironment(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    private void registerDriver() throws SQLException {
        String driverName = getDriverClassName();
        try {
            if (driverRegistered)
                return;
            DriverManager.registerDriver((Driver) Class.forName(driverName)
                    .newInstance());
            driverRegistered = true;
        } catch (Exception e) {
            throw new Error("Cannot register SQL driver " + driverName);
        }
    }

    /**
     * Intended to be overriden for post-connect activities
     */
    protected void afterConnectionEstablished() throws SQLException {
        setAutoCommit();
    }

    public void connect(String connectionString, Properties info) throws SQLException {
        registerDriver();
        closeConnection();
        currentConnection = DriverManager.getConnection(connectionString, info);
        afterConnectionEstablished();
    }

    @Override
    public void connect(String connectionString) throws SQLException {
        connect(connectionString, new Properties());
    }

    @Override
    public void connect(String dataSource, String username, String password)
            throws SQLException {
        connect(dataSource, username, password, null);
    }

    @Override
    public void connect(String dataSource, String username, String password,
            String database) throws SQLException {

        String connectionString = (database == null)
            ? getConnectionString(dataSource)
            : getConnectionString(dataSource, database);

        Properties props = new Properties();
        props.put("user", username);
        props.put("password", new PropertiesLoader().parseValue(password));

        connect(connectionString, props);
    }

    @Override
    public void connectUsingFile(String file) throws SQLException, IOException,
            FileNotFoundException {
        DbConnectionProperties dbp = DbConnectionProperties
                .CreateFromFile(file);
        if (dbp.FullConnectionString != null)
            connect(dbp.FullConnectionString);
        else if (dbp.DbName != null)
            connect(dbp.Service, dbp.Username, dbp.Password, dbp.DbName);
        else
            connect(dbp.Service, dbp.Username, dbp.Password);
    }

    /**
     * any processing required to turn a string into something jdbc driver can
     * process, can be used to clean up CRLF, externalise parameters if required
     * etc.
     */
    private String parseCommandText(String commandText) {
        commandText = commandText.replaceAll(getParameterPatternString(), "?");
        commandText = commandText.replace("\n", " ");
        commandText = commandText.replace("\r", " ");
        return commandText;
    }

    protected void bindStatementParameter(PreparedStatement statement,
            int parameterIndex, Object value) throws SQLException {
        statement.setObject(parameterIndex, value);
    }

    public final PreparedStatement createStatementWithBoundFixtureSymbols(
            TestHost testHost, String commandText) throws SQLException {
        String command = Options.isBindSymbols() ? parseCommandText(commandText) : commandText;
        PreparedStatement cs = getConnection().prepareStatement(
                command);

        if (Options.isBindSymbols()) {
            String paramNames[] = extractParamNames(commandText);
            for (int i = 0; i < paramNames.length; i++) {
                Object value = testHost.getSymbolValue(paramNames[i]);
                bindStatementParameter(cs, i + 1, value);
            }
        }
        return cs;
    }

    @Override
    public DdlStatementExecution createDdlStatementExecution(String ddl)
            throws SQLException {
        return new DdlStatementExecution(getConnection().createStatement(), ddl);
    }

    @Override
    public StatementExecution createStatementExecution(PreparedStatement statement) {
        return new StatementExecution(statement);
    }

    @Override
    public StatementExecution createFunctionStatementExecution(PreparedStatement statement) {
        return new StatementExecution(statement);
    }

    protected DbParameterAccessor createDbParameterAccessor(String name, Direction direction, int sqlType, Class javaType, int position) {
        return new DbParameterAccessor(name, direction, sqlType, javaType, position, dbfitToJdbcTransformerFactory);
    }

    public void closeConnection() throws SQLException {
        if (currentConnection != null) {
            rollback();
            currentConnection.close();
            currentConnection = null;
        }
    }

    public void commit() throws SQLException {
        if (!getConnection().getAutoCommit()) {
            currentConnection.commit();
        }
    }

    public void rollback() throws SQLException {
        if (!getConnection().getAutoCommit()) {
            currentConnection.rollback();
        }
    }

    @Override
    public void setAutoCommit() throws SQLException {
        String autoCommitMode = Options.get(OPTION_AUTO_COMMIT);
        if (!"auto".equals(autoCommitMode) && isConnected(currentConnection)) {
            if (currentConnection.getMetaData().supportsTransactions()) {
                currentConnection.setAutoCommit(Options.is(OPTION_AUTO_COMMIT));
            }
        }
    }

    /*****/
    protected abstract String getConnectionString(String dataSource);

    protected abstract String getConnectionString(String dataSource,
            String database);

    public Connection getConnection() throws SQLException {
        checkConnectionValid(currentConnection);
        return currentConnection;
    }

    /**
     * MUST RETURN PARAMETER NAMES IN EXACT ORDER AS IN STATEMENT. IF SINGLE
     * PARAMETER APPEARS MULTIPLE TIMES, MUST BE LISTED MULTIPLE TIMES IN THE
     * ARRAY ALSO
     */
    public String[] extractParamNames(String commandText) {
        ArrayList<String> hs = new ArrayList<String>();
        Matcher mc = getParameterPattern().matcher(commandText);
        while (mc.find()) {
            hs.add(mc.group(1));
        }
        String[] array = new String[hs.size()];
        return hs.toArray(array);
    }

    protected String defaultParamPatternString = "";

    private String getParameterPatternString() {
        return Options.get(Options.OPTION_PARAMETER_PATTERN).equals("") ?
            defaultParamPatternString : Options.get(Options.OPTION_PARAMETER_PATTERN);
    }

    private Pattern getParameterPattern() {
        return Pattern.compile(getParameterPatternString());
    }

    /**
     * by default, uses a string generated by buildInsertCommand and creates a
     * statement that returns generated keys via JDBC
     */
    public PreparedStatement buildInsertPreparedStatement(String tableName,
            DbParameterAccessor[] accessors) throws SQLException {
        return getConnection().prepareStatement(
                buildInsertCommand(tableName, accessors),
                Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * This method should generate a valid insert statement which is used by
     * buildInsertPreparedStatement to create the actual statement. It is
     * isolated into a separate method so that subclasses can override one or
     * the other depending on db specifics
     */
    public String buildInsertCommand(String tableName,
            DbParameterAccessor[] accessors) {
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(tableName).append("(");
        String comma = "";

        StringBuilder values = new StringBuilder();

        for (DbParameterAccessor accessor : accessors) {
            if (accessor.hasDirection(Direction.INPUT)) {
                sb.append(comma);
                values.append(comma);
                sb.append(accessor.getName());
                // values.append(":").append(accessor.getName());
                values.append("?");
                comma = ",";
            }
        }
        sb.append(") values (");
        sb.append(values);
        sb.append(")");
        return sb.toString();
    }

    public DbStoredProcedureCall newStoredProcedureCall(String name, DbParameterAccessor[] accessors) {
        return new DbStoredProcedureCall(this, name, accessors);
    }

    public DbParameterAccessor createAutogeneratedPrimaryKeyAccessor(
            DbParameterAccessor template) {
        return new DbAutoGeneratedKeyAccessor(template);
    }

    /** Check the validity of the supplied connection. */
    public static void checkConnectionValid(final Connection conn)
            throws SQLException {
        if (! isConnected(conn)) {
            throw new IllegalArgumentException(
                    "No open connection to a database is available. "
                            + "Make sure your database is running and that you have connected before performing any queries.");
        }
    }

    private static boolean isConnected(final Connection conn) throws SQLException {
        return (conn != null && !conn.isClosed());
    }

    @Override
    public String getActualErrorCode(SQLException e) {
        return e.getSQLState();
    }
}

