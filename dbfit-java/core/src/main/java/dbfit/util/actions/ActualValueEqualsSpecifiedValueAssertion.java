package dbfit.util.actions;

import dbfit.util.Cell;
import dbfit.util.TestResultHandler;

public class ActualValueEqualsSpecifiedValueAssertion extends Assertion {
    public void run(Cell cell, TestResultHandler resultHandler) throws Exception {
        Object expected = cell.parse(cell.getSpecifiedText());
        if (equals(cell.getActual(),expected))
            resultHandler.pass();
        else
            resultHandler.fail(String.valueOf(cell.getActual()));
    }

    public boolean appliesTo(Cell cell) {
        return (!cell.isInput() && !cell.getSpecifiedContent().hasSpecialSyntax());
    }
}
