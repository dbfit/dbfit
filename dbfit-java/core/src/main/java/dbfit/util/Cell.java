package dbfit.util;

import java.lang.reflect.InvocationTargetException;

import static dbfit.util.Direction.INPUT;

public abstract class Cell {
    private String specifiedText;
    private DbParameterAccessor parameterOrColumn;
    private Object actual; // this needs to be memoized because Oracle ResultSets can't be fetched twice

    protected Cell(String specifiedText, DbParameterAccessor parameterOrColumn) {
        this.specifiedText = specifiedText;
        this.parameterOrColumn = parameterOrColumn;
    }

    public Object getActual() throws InvocationTargetException, IllegalAccessException {
        if (actual == null)
            actual = parameterOrColumn.get();
        return actual;
    }

    public abstract Object parse(String string) throws Exception;

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

    public abstract TestResultHandler getTestResultHandler();
}
