package dbfit.environment;

public class SqlServerTypeNameNormaliser {
    public static String normaliseTypeName(String dataType) {
        dataType = dataType.toUpperCase().trim();
        int idx = dataType.indexOf(" ");
        if (idx >= 0)
            dataType = dataType.substring(0, idx);
        idx = dataType.indexOf("(");
        if (idx >= 0)
            dataType = dataType.substring(0, idx);
        return dataType;
    }
}
