package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.api.DbEnvironmentFacade;
import dbfit.api.DbEnvironmentFactory;
import dbfit.util.FitNesseTestHost;
import dbfit.util.SymbolUtil;

import fit.Parse;

import java.sql.SQLException;

public class StoreQuery extends fit.Fixture {

    private DbEnvironmentFacade dbEnvironment;
    private String query;
    private String symbolName;

    public StoreQuery() {
        this(DbEnvironmentFactory.getDefaultEnvironment(), null, null);
    }

    public StoreQuery(DBEnvironment environment, String query, String symbolName) {
        this.dbEnvironment = new DbEnvironmentFacade(environment, FitNesseTestHost.getInstance());
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

        try {
            SymbolUtil.setSymbol(symbolName, dbEnvironment.getQueryTable(query));
        } catch (SQLException sqle) {
            throw new Error(sqle);
        }
    }
}
