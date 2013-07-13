package dbfit.util;

import fit.Binding;
import fit.Fixture;
import fit.Parse;
import fit.TypeAdapter;

public class SymbolAccessQueryBinding extends Binding.QueryBinding {
    public static interface TestResultHandler {
        void pass();
        void fail(String actualValue);
        void exception(Throwable e);
        void annotate(String message);
    }

    public static class CellTest {
        private TestResultHandler resultHandler;

        public CellTest(TestResultHandler resultHandler) {
            this.resultHandler = resultHandler;
        }

        public void test(String expectedString, TypeAdapter adapter) {
            ContentOfTableCell content = new ContentOfTableCell(expectedString);
            try{
                if (content.isSymbolSetter()){
                    Object actual=adapter.get();
                    dbfit.util.SymbolUtil.setSymbol(content.text(), actual);
                    resultHandler.annotate("= " + String.valueOf(actual));
                } else if (content.isSymbolGetter()){
                    Object actual=adapter.get();
                    Object expected=adapter.parse(content.text());
                    resultHandler.annotate("= " + String.valueOf(expected));
                    if (adapter.equals(actual,expected))
                        resultHandler.pass();
                    else
                        resultHandler.fail(String.valueOf(actual));
                } else if (content.isExpectingInequality()){
                    //expect failing comparison
                    Object actual=adapter.get();
                    String expectedVal=content.getExpectedFailureValue();
                    resultHandler.annotate("= " + String.valueOf(actual));
                    if (adapter.equals(actual,adapter.parse(expectedVal)))
                        resultHandler.fail(String.valueOf(actual));
                    else
                        resultHandler.pass();
                } else if (content.isEmpty()) {
                    Object actual=adapter.get();
                    resultHandler.annotate(actual.toString());
                } else {
                    Object actual=adapter.get();
                    Object expected=adapter.parse(content.text());
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

	public void doCell(final Fixture fixture, final Parse cell) {
        new CellTest(new TestResultHandler() {
            public void pass() {
                fixture.right(cell);
            }

            public void fail(String actualValue) {
                fixture.wrong(cell, actualValue);
            }

            public void exception(Throwable e) {
                fixture.exception(cell, e);
            }

            public void annotate(String message) {
                cell.addToBody(Fixture.gray(message));
            }
        }).test(cell.text(), this.adapter);
	}

    static class ContentOfTableCell {
        private String content;

        ContentOfTableCell(String content) {
            this.content = content;
        }

        public boolean isSymbolSetter() {
            return SymbolUtil.isSymbolSetter(content);
        }

        public String text() {
            return content;
        }

        public boolean isSymbolGetter() {
            return SymbolUtil.isSymbolGetter(content);
        }

        private boolean isExpectingInequality() {
            return content.startsWith("fail[")|| content.endsWith("]");
        }

        public String getExpectedFailureValue() {
            return content.substring(5, content.length()-1);
        }

        public boolean isEmpty() {
            return content.isEmpty();
        }
    }
}
