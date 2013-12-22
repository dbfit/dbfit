package dbfit.util.actions;

import dbfit.util.Cell;
import dbfit.util.TestResultHandler;

public class AssignStoredValueToAccessor implements Action {
    public void run(Cell cell) throws Exception {
        Object storedValue = cell.parse(cell.getSpecifiedText());
        cell.getTestResultHandler().annotate(String.valueOf(storedValue));
        cell.set(storedValue);
    }

    public boolean appliesTo(Cell cell) {
        return (cell.isInput() && cell.getSpecifiedContent().isSymbolGetter());
    }
}
