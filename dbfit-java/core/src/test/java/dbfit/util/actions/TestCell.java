package dbfit.util.actions;

import dbfit.util.Cell;
import dbfit.util.DbParameterAccessor;
import dbfit.util.TestResultHandler;

import static org.mockito.Mockito.mock;

public class TestCell extends Cell {
    public TestResultHandler resultHandler;
    public Object actual;

    public TestCell(String specifiedText) {
        super(specifiedText, null);
        this.resultHandler = mock(TestResultHandler.class);
    }

    public TestCell(String specifiedText, DbParameterAccessor parameterOrColumn) {
        super(specifiedText, parameterOrColumn);
        this.resultHandler = mock(TestResultHandler.class);
    }

    @Override
    public Object parse(String string) throws Exception {
        return string;
    }

    @Override
    public Object getActual() {
        return actual;
    }

    @Override
    public TestResultHandler getTestResultHandler() {
        return resultHandler;
    }
}
