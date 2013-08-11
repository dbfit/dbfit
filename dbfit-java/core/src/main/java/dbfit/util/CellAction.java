package dbfit.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

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
            new NoSpecifiedValueAction(),
            new ActualValueEqualsSpecifiedValueAssertion());

    public void test(String expectedString, ParseHelper parseHelper, DbParameterAccessor parameterOrColumn) {
        Cell cell = new Cell(expectedString, parseHelper, parameterOrColumn);
        try {
            for (Action action : possibleActions) {
                if (action.appliesTo(cell)) {
                    action.run(cell, resultHandler);
                    break;
                }
            }
        }
        catch (Throwable t){
            resultHandler.exception(t);
        }
    }

    public static class SaveActualValueToSymbolAction implements Action {
        public void run(Cell cell, TestResultHandler resultHandler) throws InvocationTargetException, IllegalAccessException {
            String symbolName = cell.getSpecifiedText();
            SymbolUtil.setSymbol(symbolName, cell.getActual());
            resultHandler.annotate("= " + String.valueOf(cell.getActual()));
        }

        public boolean appliesTo(Cell cell) {
            return cell.getSpecifiedContent().isSymbolSetter();
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
            return cell.getSpecifiedContent().isSymbolGetter();
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
            return cell.getSpecifiedContent().isExpectingInequality();
        }
    }

    public static class NoSpecifiedValueAction implements Action {
        public void run(Cell cell, TestResultHandler resultHandler) throws InvocationTargetException, IllegalAccessException {
            resultHandler.annotate(cell.getActual().toString());
        }

        public boolean appliesTo(Cell cell) {
            return cell.getSpecifiedContent().isEmpty();
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
            return true;
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