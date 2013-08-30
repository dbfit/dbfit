package dbfit.util;

/**
 * This class is for identifying any kind of conventions
 * that DbFit has for cell contents (eg ?, >>, << etc)
 */
public class ContentOfTableCell {
    public static final String INEQUALITY_START_TOKEN = "fail[";
    public static final String INEQUALITY_END_TOKEN = "]";
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
        return content.startsWith(INEQUALITY_START_TOKEN)|| content.endsWith(INEQUALITY_END_TOKEN);
    }

    public String getExpectedFailureValue() {
        return content.substring(INEQUALITY_START_TOKEN.length(), content.length()-INEQUALITY_END_TOKEN.length());
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }

    public boolean isNull() {
        return getTrimmedContent().toLowerCase().equals("null");
    }

    public boolean doesFixedLengthParsingApply() {
        return Options.isFixedLengthStringParsing() &&
                getTrimmedContent().startsWith("'") &&
                getTrimmedContent().endsWith("'");
    }

    private String getTrimmedContent() {
        return content.trim();
    }

    public String getFixedLengthParsedString() {
        return getTrimmedContent().substring(1, getTrimmedContent().length() - 1);
    }

    public boolean hasSpecialSyntax() {
        return (isSymbolGetter() || isSymbolSetter() || isExpectingInequality());
    }
}