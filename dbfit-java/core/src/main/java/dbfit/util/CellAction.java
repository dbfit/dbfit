package dbfit.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import static dbfit.util.Direction.INPUT;

public class CellAction {
    public static class Cell {
        private String specifiedText;
        private ParseHelper parseHelper;
        private DbParameterAccessor parameterOrColumn;

        public Cell(String specifiedText, ParseHelper parseHelper, DbParameterAccessor parameterOrColumn) {
            this.specifiedText = specifiedText;
            this.parseHelper = parseHelper;
            this.parameterOrColumn = parameterOrColumn;
        }

        public Object getActual() throws InvocationTargetException, IllegalAccessException {
            return parameterOrColumn.get();
        }

        public Object parse(String string) throws Exception {
            return parseHelper.parse(string);
        }

        public ContentOfTableCell getSpecifiedContent() {
            return new ContentOfTableCell(specifiedText);
        }

        public String getSpecifiedText() {
            return specifiedText;
        }

        public void set(Object newValue) throws Exception {
            parameterOrColumn.set(newValue);
        }

        public boolean isInput() {
            return parameterOrColumn.hasDirection(INPUT);
        }
    }

    private TestResultHandler resultHandler;

    public CellAction(TestResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    // order is important here, because the last action is a catch-all
    public List<? extends Action> possibleActions = Arrays.asList(
            new SaveActualValueToSymbolAction(),
            new StoredValueEqualsSpecifiedValueAssertion(),
            new ActualValueDoesNotEqualSpecifiedValueAssertion(),
            new DisplayActualValueAction(),
            new ActualValueEqualsSpecifiedValueAssertion(),
            new AssignSpecifiedValueToAccessor(),
            new AssignStoredValueToAccessor());

    public void test(String expectedString, ParseHelper parseHelper, DbParameterAccessor parameterOrColumn) {
        Cell cell = new Cell(expectedString, parseHelper, parameterOrColumn);
        Action mostAppropriateAction = null;
        try {
            for (Action action : possibleActions) {
                if (action.appliesTo(cell)) {
                    mostAppropriateAction = action;
                }
                if (mostAppropriateAction != null) break;
            }
            if (mostAppropriateAction != null) {
                mostAppropriateAction.run(cell, resultHandler);
            } else {
                noSuitableActionFoundFor(cell);
            }
        }
        catch (Throwable t){
            resultHandler.exception(t);
        }
    }

    private void noSuitableActionFoundFor(Cell cell) throws IllegalArgumentException {
        String cellDirection = (cell.isInput() ? "input" : "output or return value");
        throw new IllegalArgumentException("Unexpected text [" + cell.getSpecifiedText() +
                "] specified in " + cellDirection + " cell. No suitable action found.");
    }

    public static class SaveActualValueToSymbolAction implements Action {
        public void run(Cell cell, TestResultHandler resultHandler) throws InvocationTargetException, IllegalAccessException {
            String symbolName = cell.getSpecifiedText();
            SymbolUtil.setSymbol(symbolName, cell.getActual());
            resultHandler.annotate("= " + String.valueOf(cell.getActual()));
        }

        public boolean appliesTo(Cell cell) {
            return (!cell.isInput() && cell.getSpecifiedContent().isSymbolSetter());
        }
    }

    public static class StoredValueEqualsSpecifiedValueAssertion extends Assertion {
        public void run(Cell cell, TestResultHandler resultHandler) throws Exception {
            Object expected = cell.parse(cell.getSpecifiedText());
            resultHandler.annotate("= " + String.valueOf(expected));
            if (equals(cell.getActual(),expected))
                resultHandler.pass();
            else
                resultHandler.fail(String.valueOf(cell.getActual()));
        }

        public boolean appliesTo(Cell cell) {
            return (!cell.isInput() && cell.getSpecifiedContent().isSymbolGetter());
        }
    }

    public static class ActualValueDoesNotEqualSpecifiedValueAssertion extends Assertion {
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

    public static class DisplayActualValueAction implements Action {
        public void run(Cell cell, TestResultHandler resultHandler) throws InvocationTargetException, IllegalAccessException {
            resultHandler.annotate(cell.getActual().toString());
        }

        public boolean appliesTo(Cell cell) {
            return (!cell.isInput() && cell.getSpecifiedContent().isEmpty());
        }
    }

    public static class ActualValueEqualsSpecifiedValueAssertion extends Assertion {
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

    public static class AssignSpecifiedValueToAccessor implements Action {
        public void run(Cell cell, TestResultHandler resultHandler) throws Exception {
            cell.set(cell.parse(cell.getSpecifiedText()));
        }

        public boolean appliesTo(Cell cell) {
            return (cell.isInput() && !cell.getSpecifiedContent().isSymbolGetter());
        }
    }

    public static class AssignStoredValueToAccessor implements Action {
        public void run(Cell cell, TestResultHandler resultHandler) throws Exception {
            Object storedValue = cell.parse(cell.getSpecifiedText());
            resultHandler.annotate(String.valueOf(storedValue));
            cell.set(storedValue);
        }

        public boolean appliesTo(Cell cell) {
            return (cell.isInput() && cell.getSpecifiedContent().isSymbolGetter());
        }
    }

    public static abstract class Assertion implements Action {
        protected boolean equals(Object a, Object b) {
            if (a == null)
                return (b == null);
            else
                return a.equals(b);
        }
    }

    public static interface Action {
        public void run(Cell cell, TestResultHandler resultHandler) throws Exception;
        public boolean appliesTo(Cell cell);
    }
}