package dbfit.util.actions;

import dbfit.util.Cell;
import dbfit.util.SymbolUtil;
import dbfit.util.TestResultHandler;

import java.lang.reflect.InvocationTargetException;

public class SaveActualValueToSymbolAction implements Action {
    public void run(Cell cell, TestResultHandler resultHandler) throws InvocationTargetException, IllegalAccessException {
        String symbolName = cell.getSpecifiedText();
        SymbolUtil.setSymbol(symbolName, cell.getActual());
        resultHandler.annotate("= " + String.valueOf(cell.getActual()));
    }

    public boolean appliesTo(Cell cell) {
        return (!cell.isInput() && cell.getSpecifiedContent().isSymbolSetter());
    }
}
