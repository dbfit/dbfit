package dbfit.util;

import fit.TypeAdapter;

public class CellTest {
    private TestResultHandler resultHandler;

    public CellTest(TestResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }

    public void test(String expectedString, ParseHelper parseHelper, DbParameterAccessor parameterOrColumn) {
        ContentOfTableCell content = new ContentOfTableCell(expectedString);
        try{
            Object actual = parameterOrColumn.get();
            if (content.isSymbolSetter()){
                SymbolUtil.setSymbol(content.text(), actual);
                resultHandler.annotate("= " + String.valueOf(actual));
            } else if (content.isSymbolGetter()){
                Object expected = parseHelper.parse(content.text());
                resultHandler.annotate("= " + String.valueOf(expected));
                if (equals(actual,expected))
                    resultHandler.pass();
                else
                    resultHandler.fail(String.valueOf(actual));
            } else if (content.isExpectingInequality()){
                String expectedVal = content.getExpectedFailureValue();
                resultHandler.annotate("= " + String.valueOf(actual));
                if (equals(actual,parseHelper.parse(expectedVal)))
                    resultHandler.fail(String.valueOf(actual));
                else
                    resultHandler.pass();
            } else if (content.isEmpty()) {
                resultHandler.annotate(actual.toString());
            } else {
                Object expected = parseHelper.parse(content.text());
                if (equals(actual,expected))
                    resultHandler.pass();
                else
                    resultHandler.fail(String.valueOf(actual));
            }
        }
        catch (Throwable t){
            resultHandler.exception(t);
        }
    }

    public void test(String expectedString, TypeAdapter adapter) {
        ContentOfTableCell content = new ContentOfTableCell(expectedString);
        try{
            Object actual = adapter.get();
            if (content.isSymbolSetter()){
                SymbolUtil.setSymbol(content.text(), actual);
                resultHandler.annotate("= " + String.valueOf(actual));
            } else if (content.isSymbolGetter()){
                Object expected = adapter.parse(content.text());
                resultHandler.annotate("= " + String.valueOf(expected));
                if (equals(actual,expected))
                    resultHandler.pass();
                else
                    resultHandler.fail(String.valueOf(actual));
            } else if (content.isExpectingInequality()){
                String expectedVal = content.getExpectedFailureValue();
                resultHandler.annotate("= " + String.valueOf(actual));
                if (equals(actual,adapter.parse(expectedVal)))
                    resultHandler.fail(String.valueOf(actual));
                else
                    resultHandler.pass();
            } else if (content.isEmpty()) {
                resultHandler.annotate(actual.toString());
            } else {
                Object expected = adapter.parse(content.text());
                if (equals(actual,expected))
                    resultHandler.pass();
                else
                    resultHandler.fail(String.valueOf(actual));
            }
        }
        catch (Throwable t){
            resultHandler.exception(t);
        }
    }

    private boolean equals(Object a, Object b) {
        if (a == null)
            return (b == null);
        else
            return a.equals(b);
    }
}