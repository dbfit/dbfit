package dbfit.util.actions;

import dbfit.util.Cell;
import dbfit.util.TestResultHandler;

public class AssignSpecifiedValueToAccessor implements Action {
    public void run(Cell cell, TestResultHandler resultHandler) throws Exception {
        cell.set(cell.parse(cell.getSpecifiedText()));
    }

    public boolean appliesTo(Cell cell) {
        return (cell.isInput() && !cell.getSpecifiedContent().isSymbolGetter());
    }
}
