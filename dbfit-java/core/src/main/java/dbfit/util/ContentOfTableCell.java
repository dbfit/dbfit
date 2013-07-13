package dbfit.util;

/**
 * This class is for identifying any kind of conventions
 * that DbFit has for cell contents (eg ?, >>, << etc)
 */
public class ContentOfTableCell {
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

    public boolean isExpectingInequality() {
        return content.startsWith("fail[")|| content.endsWith("]");
    }

    public String getExpectedFailureValue() {
        return content.substring(5, content.length()-1);
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }
}
