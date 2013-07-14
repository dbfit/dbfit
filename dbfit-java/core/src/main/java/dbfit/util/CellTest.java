package dbfit.util;

import fit.TypeAdapter;

public class CellTest {
    private TestResultHandler resultHandler;

    public CellTest(TestResultHandler resultHandler) {
        this.resultHandler = resultHandler;
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
                if (adapter.equals(actual,expected))
                    resultHandler.pass();
                else
                    resultHandler.fail(String.valueOf(actual));
            } else if (content.isExpectingInequality()){
                String expectedVal = content.getExpectedFailureValue();
                resultHandler.annotate("= " + String.valueOf(actual));
                if (adapter.equals(actual,adapter.parse(expectedVal)))
                    resultHandler.fail(String.valueOf(actual));
                else
                    resultHandler.pass();
            } else if (content.isEmpty()) {
                resultHandler.annotate(actual.toString());
            } else {
                Object expected = adapter.parse(content.text());
                if (adapter.equals(actual,expected))
                    resultHandler.pass();
                else
                    resultHandler.fail(String.valueOf(actual));
            }
        }
        catch (Throwable t){
            resultHandler.exception(t);
        }
    }
}
