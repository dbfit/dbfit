package dbfit.util.actions;

import dbfit.util.Cell;
import dbfit.util.TestResultHandler;

public class ActualValueDoesNotEqualSpecifiedValueAssertion extends Assertion {
    public void run(Cell cell) throws Exception {
        String shouldntBeString = cell.getSpecifiedContent().getExpectedFailureValue();
        Object shouldntBe = cell.parse(shouldntBeString);
        cell.getTestResultHandler().annotate("= " + String.valueOf(cell.getActual()));
        if (equals(cell.getActual(), shouldntBe))
            cell.getTestResultHandler().fail(String.valueOf(cell.getActual()));
        else
            cell.getTestResultHandler().pass();
    }

    public boolean appliesTo(Cell cell) {
        return (!cell.isInput() && cell.getSpecifiedContent().isExpectingInequality());
    }
}
