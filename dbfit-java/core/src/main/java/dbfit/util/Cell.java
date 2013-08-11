package dbfit.util;

import java.lang.reflect.InvocationTargetException;

import static dbfit.util.Direction.INPUT;

public class Cell {
    private String specifiedText;
    private ParseHelper parseHelper;
    private DbParameterAccessor parameterOrColumn;
    private Object actual; // this needs to be memoized because Oracle ResultSets can't be fetched twice

    public Cell(String specifiedText, ParseHelper parseHelper, DbParameterAccessor parameterOrColumn) {
        this.specifiedText = specifiedText;
        this.parseHelper = parseHelper;
        this.parameterOrColumn = parameterOrColumn;
    }

    public Object getActual() throws InvocationTargetException, IllegalAccessException {
        if (actual == null)
            actual = parameterOrColumn.get();
        return actual;
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
