package dbfit.api;

import dbfit.util.DataTable;

import java.util.Map;
import java.sql.SQLException;

public class DbEnvironmentFacade {

    private DBEnvironment environment;
    private TestHost testHost;

    public DbEnvironmentFacade(DBEnvironment environment, TestHost testHost) {
        this.environment = environment;
        this.testHost = testHost;
    }

    public Class<?> getJavaClass(String dataType) {
        return environment.getJavaClass(dataType);
    }

    public void setAutoCommit() throws SQLException {
        environment.setAutoCommit();
    }

    public void rollback() throws SQLException {
        environment.rollback();
    }

    public DbCommand createCommandWithBoundSymbols(String commandText) throws SQLException {
        return environment.createStatementWithBoundSymbols(testHost, commandText);
    }

    public DbQuery createQueryWithBoundSymbols(String queryText) throws SQLException {
        return environment.createStatementWithBoundSymbols(testHost, queryText);
    }

    public DbCommand createDdlCommand(String commandText) throws SQLException {
        return environment.createDdlCommand(commandText);
    }

    public void runCommandWithBoundSymbols(String commandText) throws SQLException {
        runCommand(createCommandWithBoundSymbols(commandText));
    }

    public void runDdl(String commandText) throws SQLException {
        runCommand(createDdlCommand(commandText));
    }

    public DataTable getQueryTable(String queryText) throws SQLException {
        try (DbQuery qry = createQueryWithBoundSymbols(queryText)) {
            return new DataTable(qry.executeQuery());
        }
    }

    public Map<String, ? extends DbParameterDescriptor> describeProcedureParameters(String procName)
            throws SQLException {
        return environment.getAllProcedureParameters(procName);
    }

    public Map<String, ? extends DbParameterDescriptor> describeColumns(String tableOrViewName)
            throws SQLException {
        return environment.getAllColumns(tableOrViewName);
    }

    /*
     * Run and close the given command
     */
    private void runCommand(DbCommand command) throws SQLException {
        try {
            command.execute();
        } finally {
            command.close();
        }
    }
}
