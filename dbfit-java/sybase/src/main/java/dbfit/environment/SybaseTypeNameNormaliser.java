package dbfit.environment;

public class SybaseTypeNameNormaliser {
    public static String normaliseTypeName(String dataType) {
        return dataType.trim().toUpperCase();
    }
}
