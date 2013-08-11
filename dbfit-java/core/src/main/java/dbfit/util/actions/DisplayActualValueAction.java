package dbfit.util.actions;

import dbfit.util.Cell;
import dbfit.util.TestResultHandler;

import java.lang.reflect.InvocationTargetException;

public class DisplayActualValueAction implements Action {
    public void run(Cell cell, TestResultHandler resultHandler) throws InvocationTargetException, IllegalAccessException {
        resultHandler.annotate(cell.getActual().toString());
    }

    public boolean appliesTo(Cell cell) {
        return (!cell.isInput() && cell.getSpecifiedContent().isEmpty());
    }
}
