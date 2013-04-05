package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.FitNesseTestHost;
import fit.Parse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

public class DatabaseEnvironment extends fitlibrary.SequenceFixture {

    public DatabaseEnvironment() {
        FitNesseTestHost.getInstance();
    }

    public void doTable(Parse table) {
        if (args.length > 0) {
            setDatabaseEnvironment(args[0]);
        }
        super.doTable(table);
    }

    public void setDatabaseEnvironment(String requestedEnv) {
        try {
            DBEnvironment oe =
                DbEnvironmentFactory.newEnvironmentInstance(requestedEnv);
            DbEnvironmentFactory.setDefaultEnvironment(oe);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void rollback() throws SQLException {
        DbEnvironmentFactory.getDefaultEnvironment().rollback();
    }

    public void commit() throws SQLException {
        DbEnvironmentFactory.getDefaultEnvironment().commit();
    }

    public void connect(String connectionString) throws SQLException {
        DbEnvironmentFactory.getDefaultEnvironment().connect(connectionString);
    }

    public void close() throws SQLException {
        DbEnvironmentFactory.getDefaultEnvironment().closeConnection();
    }

    public void connect(String dataSource, String username, String password, String database) throws SQLException {
        DbEnvironmentFactory.getDefaultEnvironment().connect(dataSource, username, password, database);
    }

    public void connect(String dataSource, String username, String password) throws SQLException {
        DbEnvironmentFactory.getDefaultEnvironment().connect(dataSource, username, password);
    }

    public void connectUsingFile(String file) throws IOException, SQLException, FileNotFoundException {
        DbEnvironmentFactory.getDefaultEnvironment().connectUsingFile(file);
    }

    public void setOption(String option, String value) {
        dbfit.util.Options.setOption(option, value);
    }
}
