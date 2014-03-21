package dbfit.environment;

public class SqlServerTypeNameNormaliser {
    public static String normaliseTypeName(String dataType) {
        return dataType.trim().toUpperCase().split("[\\s(]")[0];
    }
}
