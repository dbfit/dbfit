package dbfit.fixture.report;

import dbfit.util.MatchResult;
import static dbfit.util.MatchStatus.*;

import fit.Fixture;
import fit.Parse;

public class FitFixtureReportingSystem implements ReportingSystem {

    protected final Fixture fixture;
    protected final Parse table;
    protected Parse lastRow;
    protected Parse newRowTail;

    public FitFixtureReportingSystem(final Fixture fixture, final Parse table) {
        this.fixture = fixture;
        this.table = table;

        newRowTail = new Parse("tr", null, null, null);
        lastRow = table.parts.last();
    }

    public Parse getOutputTable() {
        return table;
    }

    protected void addCell(final Parse cell) {
        if (null == newRowTail.parts) {
            newRowTail.parts = cell;
        } else {
            newRowTail.more = cell;
        }

        newRowTail = cell;
    }

    @Override
    public void addCell(final MatchResult res) {
        Parse cell = new Parse("td", res.getStringValue2(), null, null);

        switch (res.getStatus()) {
        case SUCCESS:
            fixture.right(cell);
            break;
        /*
        case WRONG:
        case EXCEPTION:
            fixture.exception(cell, res.getException());
            break;
        case SURPLUS:
            fixture.wrong(cell);
            break;
        case MISSING:
            cell.value = res.getStringValue1();
            fixture.wrong(cell);
            break;
        */
        default:
            throw new UnsupportedOperationException("Not implemented yet");
        }

        addCell(cell);
    }

    @Override
    public void endRow(final MatchResult res) {
    }
}
