package dbfit.util.actions;

import dbfit.util.Cell;
import dbfit.util.TestResultHandler;

public class ActualValueDoesNotEqualSpecifiedValueAssertion extends Assertion {
    public void run(Cell cell, TestResultHandler resultHandler) throws Exception {
        String shouldntBeString = cell.getSpecifiedContent().getExpectedFailureValue();
        Object shouldntBe = cell.parse(shouldntBeString);
        resultHandler.annotate("= " + String.valueOf(cell.getActual()));
        if (equals(cell.getActual(), shouldntBe))
            resultHandler.fail(String.valueOf(cell.getActual()));
        else
            resultHandler.pass();
    }

    public boolean appliesTo(Cell cell) {
        return (!cell.isInput() && cell.getSpecifiedContent().isExpectingInequality());
    }
}
