package dbfit.util.actions;

import dbfit.util.Cell;
import dbfit.util.TestResultHandler;

public class ActualValueEqualsSpecifiedValueAssertion extends Assertion {
    public void run(Cell cell) throws Exception {
        Object expected = cell.parse(cell.getSpecifiedText());
        if (equals(cell.getActual(),expected))
            cell.getTestResultHandler().pass();
        else
            cell.getTestResultHandler().fail(String.valueOf(cell.getActual()));
    }

    public boolean appliesTo(Cell cell) {
        return (!cell.isInput() && !cell.getSpecifiedContent().hasSpecialSyntax());
    }
}
