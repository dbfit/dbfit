package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.util.*;
import fit.Parse;

public class CompareStoredQueriesHideMatchingRows extends CompareStoredQueries  {

    public CompareStoredQueriesHideMatchingRows() {
        super();
    }

    public CompareStoredQueriesHideMatchingRows(DBEnvironment environment, String symbol1, String symbol2) {
        super(environment, symbol1, symbol2);
    }

    protected Parse processDataTable(final MatchableDataTable t1, final MatchableDataTable t2, final Parse lastScreenRow, final String queryName) {
        class DataTablesMatchProcessor implements DataRowProcessor {
            Parse screenRow = lastScreenRow;

            public void process(DataRow dr) {
                int rememberWrongMatchings = counts.wrong;
                Parse newRow = null;
                try {
                    DataRow dr2 = t2.findMatching(buildMatchingMask(dr));
                    t2.markProcessed(dr2);
                    newRow = parseDataRows(dr, dr2);
                } catch (NoMatchingRowFoundException nex) {
                    newRow = parseDataRowAsError(dr, " missing from " + queryName);
                }
                if(rememberWrongMatchings == counts.wrong) {
                    screenRow.more = newRow;
                    screenRow = newRow;        
                }
            }
        }

        DataTablesMatchProcessor processor = new DataTablesMatchProcessor();
        t1.processDataRows(processor);
        return processor.screenRow;
    }
}
