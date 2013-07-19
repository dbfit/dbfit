package dbfit.util;

import fit.Binding;
import fit.Fixture;
import fit.Parse;

public class SymbolAccessQueryBinding extends Binding.QueryBinding {

    public void doCell(final Fixture fixture, final Parse cell) {
        new CellTest(new TestResultHandler() {
            public void pass() {
                fixture.right(cell);
            }

            public void fail(String actualValue) {
                fixture.wrong(cell, actualValue);
            }

            public void exception(Throwable e) {
                fixture.exception(cell, e);
            }

            public void annotate(String message) {
                cell.addToBody(Fixture.gray(message));
            }
        }).test(cell.text(), this.adapter);
	}

}
