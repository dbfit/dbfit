package dbfit.fixture.report;

import fit.Fixture;
import fit.Parse;

public class FitFixtureReportingSystem implements ReportingSystem {

    protected final Fixture fixture;
    protected final Parse table;
    protected Parse lastRow;
    protected Parse newRow;

    public FitFixtureReportingSystem(final Fixture fixture, final Parse table) {
        this.fixture = fixture;
        this.table = table;

        newRow = new Parse("tr", null, null, null);
        lastRow = table.parts.last();
    }

    @Override
    public void cellRight(final String value) {
        Parse cell = new Parse("td", value, null, null);
        fixture.right(cell);
    }

    @Override
    public void cellWrong(final String actual, final String expected) {
    }

    @Override
    public void cellMissing(final String expected) {
    }

    @Override
    public void cellSurplus(final String actual) {
    }

    @Override
    public void cellException(final String actual, final String expected,
            final Exception e) {
    }
}
