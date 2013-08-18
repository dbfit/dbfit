package dbfit.util;

import fit.Fixture;
import fit.Parse;
import fit.TypeAdapter;

import java.lang.reflect.InvocationTargetException;

import static dbfit.util.Direction.INPUT;

public class Cell {
    private TestResultHandler testResultHandler;
    private String specifiedText;
    private ParseHelper parseHelper;
    private DbParameterAccessor parameterOrColumn;
    private Object actual; // this needs to be memoized because Oracle ResultSets can't be fetched twice

    private Cell(String specifiedText, ParseHelper parseHelper, DbParameterAccessor parameterOrColumn) {
        this.specifiedText = specifiedText;
        this.parseHelper = parseHelper;
        this.parameterOrColumn = parameterOrColumn;
    }

    public Cell(DbParameterAccessor accessor, Parse fitCell, Fixture parentFixture) {
        Class<?> type = accessor.getJavaType();

        this.specifiedText = fitCell.text();
        this.parseHelper = new ParseHelper(TypeAdapter.on(parentFixture, type), type);
        this.parameterOrColumn = accessor;

        testResultHandler = new DbFitActionResultHandler(fitCell, parentFixture);
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

    public TestResultHandler getTestResultHandler() {
        return testResultHandler;
    }
}
