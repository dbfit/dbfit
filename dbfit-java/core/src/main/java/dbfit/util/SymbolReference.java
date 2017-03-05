package dbfit.util;

public class SymbolReference {

    private String name;
    private String prefix;

    private static final String[] PREFIXES = { "<<", ">>" };

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isSymbolGetter() {
        return getPrefix().startsWith("<<");
    }

    public boolean isSymbolSetter() {
        return getPrefix().startsWith(">>");
    }

    private SymbolReference(String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    public static SymbolReference fromFullName(String symbolFullName) {
        if (null == symbolFullName) {
            return new SymbolReference(null, "");
        }

        String prefix = findPrefix(symbolFullName);
        String name = symbolFullName.substring(prefix.length()).trim();

        return new SymbolReference(name, prefix);
    }

    private static String findPrefix(String symbolFullName) {
        for (String candidatePrefix : PREFIXES) {
            if (symbolFullName.startsWith(candidatePrefix)) {
                return candidatePrefix;
            }
        }

        return "";
    }
}
