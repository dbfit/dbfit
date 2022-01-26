package dbfit.environment.postgres;

public class NameNormaliserPostgres {
    private NameNormaliserPostgres() {
        // utility classes should not be instanciated
    }

    private static boolean isEscaped(String name) {
        // http://www.postgresql.org/docs/9.1/static/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS
        return name.charAt(0) == '"' && name.charAt(name.length() - 1) == '"';
    }

    public static String normaliseName(final String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        if (isEscaped(name)) {
            return name.substring(1, name.length() - 1).replace("\"\"", "\"");
        }
        return dbfit.util.NameNormaliser.normaliseName(name);
    }
}
