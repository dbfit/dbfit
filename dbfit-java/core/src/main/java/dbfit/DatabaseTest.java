package dbfit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import dbfit.api.DBEnvironment;
import dbfit.util.*;
import fit.Fixture;
import fit.Parse;
import fitlibrary.SequenceFixture;

public class DatabaseTest extends Fixture {
    protected DBEnvironment environment;

    // ugly workaround since fitlibrary no longer allows this to be
    // overridden; we create an inner sequence fixture and pass the
    // execution to it, but this one is now a fixture to allow things to be overridden
    public void interpretTables(Parse tables) {
        Options.reset();
        SequenceFixture sf = new SequenceFixture();
        sf.listener = listener;
        sf.counts = counts;
        sf.summary = summary;
        sf.setSystemUnderTest(this);
        sf.interpretTables(tables);
        try {
            Log.log("Rolling back");
            if (environment != null) {
                environment.closeConnection();
            }
        } catch (Exception e) {
            Log.log(e);
        }
    }

    public DatabaseTest(DBEnvironment environment) {
        FitNesseTestHost.getInstance(); // load up fitnesse test host to ensure that parsers are intialised
        this.environment = environment;
    }

    public void connect(String dataSource, String username, String password, String database) throws SQLException {
        environment.connect(dataSource, username, password, database);
    }

    public void connect(String dataSource, String username, String password) throws SQLException {
        environment.connect(dataSource, username, password);
    }

    public void connect(String connectionString) throws SQLException {
        environment.connect(connectionString);
    }

    public void connectUsingFile(String filePath) throws SQLException, IOException, FileNotFoundException {
        environment.connectUsingFile(filePath);
    }

    public void close() throws SQLException {
        environment.closeConnection();
    }

    public void setParameter(String name, String value) {
        dbfit.fixture.SetParameter.setParameter(name, value);
    }

    public void setParameter(String name, String value, String parseDelegate) {
        dbfit.fixture.SetParameter.setParameter(name, value, parseDelegate);
    }

    public void clearParameters() {
        SymbolUtil.clearSymbols();
    }

    public Fixture query(String query) {
        return new dbfit.fixture.Query(environment, query);
    }

    public Fixture orderedQuery(String query) {
        return new dbfit.fixture.Query(environment, query, true);
    }

    public Fixture execute(String statement) {
        return new dbfit.fixture.Execute(environment, statement);
    }

    public Fixture executeDdl(String statement) {
        return new dbfit.fixture.ExecuteDdl(environment, statement);
    }

    public Fixture executeProcedure(String statement) {
        return new dbfit.fixture.ExecuteProcedure(environment, statement);
    }

    public Fixture executeProcedureExpectException(String statement) {
        return new dbfit.fixture.ExecuteProcedureExpectException(environment, statement);
    }

    public Fixture executeProcedureExpectException(String statement, String code) {
        return new dbfit.fixture.ExecuteProcedureExpectException(environment, statement, code);
    }

    public Fixture insert(String tableName) {
        return new dbfit.fixture.Insert(environment, tableName);
    }

    public Fixture update(String tableName) {
        return new dbfit.fixture.Update(environment, tableName);
    }

    public Fixture clean() {
        return new dbfit.fixture.Clean(environment);
    }

    //  public Fixture testData(String type)
    //  {
    //      Log.log("Calling testData method with type '%s'", type);
    //      return new TestData(environment, type);
    //  }

    public Fixture rollback() {
        return new dbfit.fixture.Rollback(environment);
    }

    public Fixture commit() {
        return new dbfit.fixture.Commit(environment);
    }

    public Fixture queryStats() {
        return new dbfit.fixture.QueryStats(environment);
    }

    public Fixture inspectProcedure(String procName) {
        return new dbfit.fixture.Inspect(environment, dbfit.fixture.Inspect.MODE_PROCEDURE, procName);
    }

    public Fixture inspectTable(String tableName) {
        return new dbfit.fixture.Inspect(environment, dbfit.fixture.Inspect.MODE_TABLE, tableName);
    }

    public Fixture inspectView(String tableName) {
        return new dbfit.fixture.Inspect(environment, dbfit.fixture.Inspect.MODE_TABLE, tableName);
    }

    public Fixture inspectQuery(String query) {
        return new dbfit.fixture.Inspect(environment, dbfit.fixture.Inspect.MODE_QUERY, query);
    }

    public Fixture storeQuery(String query, String symbolName) {
        return new dbfit.fixture.StoreQuery(environment, query, symbolName);
    }

    public Fixture compareStoredQueries(String symbol1, String symbol2) {
        return new dbfit.fixture.CompareStoredQueries(environment, symbol1, symbol2);
    }

    public Fixture compareStoredQueriesHideMatchingRows(String symbol1, String symbol2) {
        return new dbfit.fixture.CompareStoredQueriesHideMatchingRows(environment, symbol1, symbol2);
    }

    public Fixture setOption(String option, String value) {
        return new dbfit.fixture.SetOption(environment, option, value);
    }
}
