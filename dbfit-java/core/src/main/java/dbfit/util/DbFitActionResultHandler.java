package dbfit.util;

import fit.Fixture;
import fit.Parse;

public class DbFitActionResultHandler implements TestResultHandler {
    private Parse fitCell;
    private Fixture fixture;

    public DbFitActionResultHandler(Parse fitCell, Fixture fixture) {

        this.fitCell = fitCell;
        this.fixture = fixture;
    }

    public void pass() {
        fixture.right(fitCell);
    }

    public void fail(String actualValue) {
        fixture.wrong(fitCell, actualValue);
    }

    public void exception(Throwable e) {
        fixture.exception(fitCell, e);
    }

    public void annotate(String message) {
        fitCell.addToBody(Fixture.gray(message));
    }
}
