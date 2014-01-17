package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.util.*;
import fit.Fixture;
import fit.Parse;

public class CompareStoredQueriesHideMatchingRows extends CompareStoredQueries  {

    public CompareStoredQueriesHideMatchingRows() {
        super();
    }

    public CompareStoredQueriesHideMatchingRows(DBEnvironment environment, String symbol1, String symbol2) {
        super(environment, symbol1, symbol2);
    }

    public void doTable(Parse table) {
        super.doTable(table);
        if(counts.wrong == 0 && counts.exceptions == 0) {
            Parse lastRow = table.parts.more;
            lastRow.more = this.getSummary();
        }
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
                if(rememberWrongMatchings != counts.wrong) {
                    screenRow.more = newRow;
                    screenRow = newRow;        
                }
            }
        }

        DataTablesMatchProcessor processor = new DataTablesMatchProcessor();
        t1.processDataRows(processor);
        return processor.screenRow;
    }

    public Parse getSummary() {
        Parse summary = new Parse("tr", null, null, null);
        summary.addToTag(" class=\"pass\"");
        Parse firstCell = new Parse("td", this.counts(), null, null);
        firstCell.addToTag(" colspan=\"" + (columnNames.length + 1)+ "\"");
        summary.parts = firstCell;
        return summary;
    }
}
