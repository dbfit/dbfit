package dbfit.util;

import fit.Binding;
import fit.Fixture;
import fit.Parse;

import static dbfit.util.CellHelper.appendObjectValue;

public class SymbolAccessQueryBinding extends Binding.QueryBinding {

    public void doCell(Fixture fixture, Parse cell) {
        ContentOfTableCell content = new ContentOfTableCell(cell.text());
        try {
            if (content.isSymbolSetter()) {
                Object actual = this.adapter.get();
                dbfit.util.SymbolUtil.setSymbol(content.text(), actual);
                appendObjectValue(cell, actual, !content.isSymbolHidden());
                // fixture.ignore(cell);
            } else if (content.isSymbolGetter()) {
                Object actual = this.adapter.get();
                Object expected = this.adapter.parse(content.text());
                appendObjectValue(cell, expected, !content.isSymbolHidden());

                if (adapter.equals(actual, expected)) {
                    fixture.right(cell);
                } else {
                    fixture.wrong(cell, String.valueOf(actual));
                }
            } else if (content.isExpectingInequality()) {
                //expect failing comparison
                Object actual = this.adapter.get();
                String expectedVal = content.getExpectedFailureValue();
                appendObjectValue(cell, actual);

                if (adapter.equals(actual, adapter.parse(expectedVal))) {
                    fixture.wrong(cell);
                } else {
                    fixture.right(cell);
                }
            } else {
                super.doCell(fixture, cell);
            }
        } catch (Throwable t) {
            fixture.exception(cell, t);
        }
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

        public boolean isSymbolHidden() {
            return SymbolUtil.isSymbolHidden(content);
        }

        private boolean isExpectingInequality() {
            return content.startsWith("fail[") || content.endsWith("]");
        }

        public String getExpectedFailureValue() {
            return content.substring(5, content.length() - 1);
        }
    }
}
