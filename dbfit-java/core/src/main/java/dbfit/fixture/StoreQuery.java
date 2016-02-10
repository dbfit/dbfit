package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFactory;
import dbfit.api.DbQuery;
import dbfit.util.DataTable;
import dbfit.util.FitNesseTestHost;

import fit.Parse;

import java.sql.SQLException;

public class StoreQuery extends fit.Fixture {

    private DBEnvironment dbEnvironment;
    private String query;
    private String symbolName;

    public StoreQuery() {
        dbEnvironment = DbEnvironmentFactory.getDefaultEnvironment();
    }

    public StoreQuery(DBEnvironment environment, String query, String symbolName) {
        this.dbEnvironment = environment;
        this.query = query;
        this.symbolName = symbolName;
    }

    public void doTable(Parse table) {
        if (query == null || symbolName == null) {
            if (args.length < 2) {
                throw new UnsupportedOperationException(
                        "No query and symbol name specified to StoreQuery constructor or argument list");
            }
            query = args[0];
            symbolName = args[1];
        }

        try (
            DbQuery statement =
                dbEnvironment.createStatementWithBoundSymbols(
                    FitNesseTestHost.getInstance(), query)
        ) {
            DataTable dt = new DataTable(statement.executeQuery());
            dbfit.util.SymbolUtil.setSymbol(symbolName, dt);
        } catch (SQLException sqle) {
            throw new Error(sqle);
        }
    }
}
