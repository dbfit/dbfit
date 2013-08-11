package dbfit.util.actions;

import dbfit.util.Cell;
import dbfit.util.TestResultHandler;

public class AssignStoredValueToAccessor implements Action {
    public void run(Cell cell, TestResultHandler resultHandler) throws Exception {
        Object storedValue = cell.parse(cell.getSpecifiedText());
        resultHandler.annotate(String.valueOf(storedValue));
        cell.set(storedValue);
    }

    public boolean appliesTo(Cell cell) {
        return (cell.isInput() && cell.getSpecifiedContent().isSymbolGetter());
    }
}
