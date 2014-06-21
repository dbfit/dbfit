package dbfit.util;

import fit.Binding;
import fit.Fixture;
import fit.Parse;

public class SymbolAccessQueryBinding extends Binding.QueryBinding {
    public void doCell(Fixture fixture, Parse cell) {
        ContentOfTableCell content = new ContentOfTableCell(cell.text());
        try {
            if (content.isSymbolSetter()) {
                Object actual = this.adapter.get();
                dbfit.util.SymbolUtil.setSymbol(content.text(), actual);
                cell.addToBody(Fixture.gray("= " + String.valueOf(actual)));
                // fixture.ignore(cell);
            } else if (content.isSymbolGetter()) {
                Object actual = this.adapter.get();
                Object expected = this.adapter.parse(content.text());
                cell.addToBody(Fixture.gray("= " + String.valueOf(expected)));

                if (adapter.equals(actual, expected)) {
                    fixture.right(cell);
                } else {
                    fixture.wrong(cell, String.valueOf(actual));
                }
            } else if (content.isExpectingInequality()) {
                //expect failing comparison
                Object actual = this.adapter.get();
                String expectedVal = content.getExpectedFailureValue();
                cell.addToBody(Fixture.gray("= " + String.valueOf(actual)));

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

        private boolean isExpectingInequality() {
            return content.startsWith("fail[") || content.endsWith("]");
        }

        public String getExpectedFailureValue() {
            return content.substring(5, content.length() - 1);
        }
    }
}
