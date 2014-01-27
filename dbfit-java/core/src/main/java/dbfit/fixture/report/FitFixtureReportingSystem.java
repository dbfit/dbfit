package dbfit.fixture.report;

import dbfit.util.MatchResult;
import static dbfit.util.MatchStatus.*;

import fit.Fixture;
import fit.Parse;

public class FitFixtureReportingSystem implements ReportingSystem {

    protected final Fixture fixture;
    protected final Parse table;

    protected Parse lastClosedRow;
    protected Parse newRow; // new row. attached on 1st cell or on empty row end
    protected Parse newRowTail;

    public FitFixtureReportingSystem(final Fixture fixture, final Parse table) {
        this.fixture = fixture;
        this.table = table;

        lastClosedRow = table.parts.last();
        createNewRow();
    }

    /*
     * Initially "hanging" new row (not attached to main table)
     */
    protected void createNewRow() {
        newRow = new Parse("tr", null, null, null);
        newRowTail = newRow.parts; // null
    }

    /*
     * Ensure new row is attached (but still not closed)
     * OK to call that several times.
     */
    protected Parse newRowAttached() {
        lastClosedRow.more = newRow;
        return newRow;
    }

    protected void closeNewRow() {
        lastClosedRow = newRowAttached();
        createNewRow();
    }

    protected void addCell(final Parse cell) {
        if (newRowTail == null) {
            newRowAttached().parts = cell;
        } else {
            newRowTail.more = cell;
        }

        newRowTail = cell;
    }

    @Override
    public void endRow(final MatchResult res) {
        closeNewRow();
    }

    @Override
    public void addCell(final MatchResult res) {
        Parse cell = new Parse("td", res.getStringValue1(), null, null);

        switch (res.getStatus()) {
        case SUCCESS:
            fixture.right(cell);
            break;
        case WRONG:
            fixture.wrong(cell, res.getStringValue2());
            break;
        /*
        case EXCEPTION:
            fixture.exception(cell, res.getException());
            break;
        case SURPLUS:
            cell.value = res.getStringValue2();
            fixture.wrong(cell);
            break;
        case MISSING:
            fixture.wrong(cell);
            break;
        */
        default:
            throw new UnsupportedOperationException("Not implemented yet");
        }

        addCell(cell);
    }

    public Parse getTable() {
        return table;
    }
}
