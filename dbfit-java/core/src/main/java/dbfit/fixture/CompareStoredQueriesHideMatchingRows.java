package dbfit.fixture;

import dbfit.api.DBEnvironment;
import dbfit.fixture.report.FitFixtureReportingSystem;
import dbfit.util.DataRow;
import dbfit.util.DataCell;
import dbfit.util.MatchResult;
import static dbfit.util.MatchStatus.*;

import fit.Parse;

import java.util.List;
import java.util.ArrayList;

public class CompareStoredQueriesHideMatchingRows extends CompareStoredQueries {

    public CompareStoredQueriesHideMatchingRows() {
        super();
    }

    public CompareStoredQueriesHideMatchingRows(DBEnvironment environment, String symbol1, String symbol2) {
        super(environment, symbol1, symbol2);
    }

    @Override
    public void doTable(Parse table) {
        super.doTable(table);
        addSummary(table);
    }

    private void addSummary(Parse table) {
        Parse summary = getSummary();
        summary.parts.addToTag(" colspan=\"" + numColumns(table) + "\"");
        Parse lastRow = table.parts.last().more = summary;
    }

    public Parse getSummary() {
        Parse summary = new Parse("tr", null, null, null);
        summary.addToTag(" class=\"pass\"");
        Parse firstCell = new Parse("td", this.counts(), null, null);
        summary.parts = firstCell;
        return summary;
    }

    private int numColumns(Parse table) {
        int cnt = 0;
        for (Parse row = table.parts; row != null; row = row.more) {
            cnt = Math.max(cnt, row.size());
        }

        return cnt;
    }

    @Override
    protected FitFixtureReporter getReporter(final Parse table) {
        return new FitFixtureReporter(new FitFixtureReportingSystem(this, table)) {
            List<MatchResult<DataCell, DataCell>> lastRow = new ArrayList<>();

            @Override
            public void endRow(MatchResult<DataRow, DataRow> result) {
                for (MatchResult cellRes: lastRow) {
                    if (result.getStatus() != SUCCESS) {
                        reportingSystem.addCell(cellRes);
                    } else {
                        reportingSystem.incRight();
                    }
                }

                if (result.getStatus() != SUCCESS) {
                    reportingSystem.endRow(result);
                }

                lastRow.clear();
            }

            @Override
            public void endCell(MatchResult<DataCell, DataCell> result) {
                lastRow.add(result);
            }
        };
    }

}
