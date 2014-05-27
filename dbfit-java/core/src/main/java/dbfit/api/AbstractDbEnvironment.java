package dbfit.api;

import dbfit.util.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;
import java.math.BigDecimal;

public abstract class AbstractDbEnvironment implements DBEnvironment {

    protected Connection currentConnection;
    protected String driverClassName;

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
        // empty stub
    }

    public void connect(String connectionString, Properties info) throws SQLException {
        System.out.println("Entering AbstractDbEnvironment: connect(2)");
        registerDriver();
        currentConnection = DriverManager.getConnection(connectionString, info);
        currentConnection.setAutoCommit(false);
        //System.out.println("AbstractDbEnvironment: connect(2): connected to DB of version " + currentConnection.getMetaData().getDatabaseMajorVersion());
        afterConnectionEstablished();
    }

    @Override
    public void connect(String connectionString) throws SQLException {
        System.out.println("Entering AbstractDbEnvironment: connect(2)");
        //System.out.println("AbstractDbEnvironment: connect(2): connected to DB of version " + currentConnection.getMetaData().getDatabaseMajorVersion());
        connect(connectionString, new Properties());
    }

    @Override
    public void connect(String dataSource, String username, String password)
            throws SQLException {
        System.out.println("Entering AbstractDbEnvironment: connect(3)");
        connect(dataSource, username, password, null);
        //System.out.println("AbstractDbEnvironment: connect(2): connected to DB of version " + currentConnection.getMetaData().getDatabaseMajorVersion());
    }

    @Override
    public void connect(String dataSource, String username, String password,
            String database) throws SQLException {
        System.out.println("Entering AbstractDbEnvironment: connect(4)");
        String connectionString = (database == null)
            ? getConnectionString(dataSource)
            : getConnectionString(dataSource, database);

        Properties props = new Properties();
        props.put("user", username);
        props.put("password", new PropertiesLoader().parseValue(password));
        
        connect(connectionString, props);
        //System.out.println("AbstractDbEnvironment: connect(2): connected to DB of version " + currentConnection.getMetaData().getDatabaseMajorVersion());
    }

    @Override
    public void connectUsingFile(String file) throws SQLException, IOException,
            FileNotFoundException {
        System.out.println("Entering AbstractDbEnvironment: connectUsingFile");
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
    protected String parseCommandText(String commandText) {
        commandText = commandText.replace("\n", " ");
        commandText = commandText.replace("\r", " ");
        return commandText;
    }

    public PreparedStatement createStatementWithBoundFixtureSymbols(
            TestHost testHost, String commandText) throws SQLException {
        String command = Options.isBindSymbols() ? parseCommandText(commandText) : commandText;
        PreparedStatement cs = getConnection().prepareStatement(
                command);

        if (Options.isBindSymbols()) {
            String paramNames[] = extractParamNames(commandText);
            for (int i = 0; i < paramNames.length; i++) {
                Object value = testHost.getSymbolValue(paramNames[i]);
                if (value == null) {
                    System.out.println("AbstractDBEnvironment: createStatementWithBoundFixtureSymbols: value for param " + i + " is null");
                    if (supportsSetObjectNull()) {
                        System.out.println("AbstractDBEnvironment: createStatementWithBoundFixtureSymbols: DB env supports set object null");
                        cs.setObject(i + 1, value);
                    }
                    else {
                        System.out.println("AbstractDBEnvironment: createStatementWithBoundFixtureSymbols: DB env does not support set object null");
                        System.out.println("AbstractDBEnvironment: createStatementWithBoundFixtureSymbols: calling getSymbolType");
                        Class<?> clazz = testHost.getSymbolType(paramNames[i]);
                        System.out.println("AbstractDBEnvironment: createStatementWithBoundFixtureSymbols: back from getSymbolType");
                        System.out.println("AbstractDBEnvironment: createStatementWithBoundFixtureSymbols: clazz == null?: " + (clazz == null));
                        System.out.println("AbstractDBEnvironment: createStatementWithBoundFixtureSymbols: clazz.getName(): " + clazz.getName());
                        System.out.println("AbstractDBEnvironment: createStatementWithBoundFixtureSymbols: calling getJavaClassSqlType");
                        int sqlType = getJavaClassSqlType(clazz);
                        System.out.println("AbstractDBEnvironment: createStatementWithBoundFixtureSymbols: back from getJavaClassSqlType");
                        System.out.println("AbstractDBEnvironment: createStatementWithBoundFixtureSymbols: sqlType: " + sqlType);
                        cs.setNull(i + 1, sqlType);
                        System.out.println("AbstractDBEnvironment: createStatementWithBoundFixtureSymbols: back setNull");
                    }
                }
                else {
                    System.out.println("AbstractDBEnvironment: createStatementWithBoundFixtureSymbols: value for param " + i + " is not null");
                    cs.setObject(i + 1, value);
                }
            }
        }
        return cs;
    }

    public void closeConnection() throws SQLException {
        if (currentConnection != null) {
            currentConnection.rollback();
            currentConnection.close();
            currentConnection = null;
        }
    }

    public void commit() throws SQLException {
        currentConnection.commit();
        currentConnection.setAutoCommit(false);
    }

    public void rollback() throws SQLException {
        checkConnectionValid(currentConnection);
        currentConnection.rollback();
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

    protected abstract Pattern getParameterPattern();

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
        System.out.println("AbstractDBEnvironment: buildInsertCommand: returning: " + sb.toString());
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
        if (conn == null || conn.isClosed()) {
            throw new IllegalArgumentException(
                    "No open connection to a database is available. "
                            + "Make sure your database is running and that you have connected before performing any queries.");
        }
    }
    
    public boolean supportsSavepoints() {
        boolean supportsSavepoints;
        
        if (currentConnection == null)
            return false;
        
        try {
            supportsSavepoints = currentConnection.getMetaData().supportsSavepoints();
        }
        catch (SQLException e){
            supportsSavepoints = false;
            throw new Error("SQLEception occured getting connection metadata", e);
        }
        return supportsSavepoints;
    }
    
    public boolean supportsSetObjectNull() {
        System.out.println("AbstractDBEnvironment: supportsSetObjectNull: entering");
        return true;
/*         boolean supportsSetObjectNull;
        
        if (currentConnection == null) {
            System.out.println("AbstractDBEnvironment: supportsSetObjectNull: connection closed, returning false");
            return false;
        }
        
        DbParameterAccessor[] pa = new DbParameterAccessor[1];
        System.out.println("AbstractDBEnvironment: supportsSetObjectNull: created PA array");
        pa[0] = new DbParameterAccessor("TestAttribute ", Direction.INPUT, Types.VARCHAR, String.class, 0);
        System.out.println("AbstractDBEnvironment: supportsSetObjectNull: created PA");
        PreparedStatement ps;
        // try {
            // ps = buildInsertPreparedStatement("TestTable ", pa);
            // System.out.println("AbstractDBEnvironment: supportsSetObjectNull: created PS");
        // }
        // catch (Exception e) {
            // System.out.println("AbstractDBEnvironment: supportsSetObjectNull: caught exception of type: " + e.getClass().getName());
            // throw new Error("SQLException occured building insert statement", e);
        // }
        try {
            ps = getConnection().prepareStatement("insert into ? (?) values (?)");
        }
        catch (SQLException e) {
            System.out.println("AbstractDBEnvironment: supportsSetObjectNull: caught exception of type: " + e.getClass().getName());
            throw new Error("SQLException occured building insert statement", e);
        }
        System.out.println("AbstractDBEnvironment: supportsSetObjectNull: created ps");
        
        supportsSetObjectNull = true;
        
        try {
            ps.setObject(1, null);
        }
        catch (SQLException e) {
            System.out.println("AbstractDBEnvironment: supportsSetObjectNull: caught SQLException, msg: " + e.getMessage());
            supportsSetObjectNull = false;
        }
        System.out.println("AbstractDBEnvironment: supportsSetObjectNull: returning with: " + supportsSetObjectNull);
        return supportsSetObjectNull; */
    }
    
    public int getJavaClassSqlType(Class<?> javaClass) {
        
        System.out.println("AbstractDBEnvironment: getJavaClassSqlType: entering");
        System.out.println("AbstractDBEnvironment: getJavaClassSqlType: javaClass == null: " + (javaClass == null));
        System.out.println("AbstractDBEnvironment: getJavaClassSqlType: javaClass.getName: " + javaClass.getName());
        
        if (javaClass.equals(Long.class))
            return java.sql.Types.BIGINT;
        if (javaClass.equals(String.class))
            return java.sql.Types.VARCHAR;
        if (javaClass.equals(Date.class))
            return java.sql.Types.DATE;
        if (javaClass.equals(Timestamp.class))
            return java.sql.Types.TIMESTAMP;
        if (javaClass.equals(Time.class))
            return java.sql.Types.TIME;
        if (javaClass.equals(Integer.class))
            return java.sql.Types.INTEGER;
        if (javaClass.equals(Double.class))
            return java.sql.Types.DOUBLE;
        if (javaClass == BigDecimal.class)
            return java.sql.Types.DECIMAL;
        
        System.out.println("AbstractDBEnvironment: getJavaClassSqlType: about to error with no defined class");
        throw new Error("No SQL Type mapping defined for Java class " + javaClass.getName());
    }
}

