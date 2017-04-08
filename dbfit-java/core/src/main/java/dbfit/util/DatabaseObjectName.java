package dbfit.util;

public class DatabaseObjectName {
    private String schemaName, objectName;

    public DatabaseObjectName(String objSchemaName, String objName) {
        schemaName = objSchemaName;
        objectName = objName;
    }

    public static DatabaseObjectName splitWithDelimiter(String objName, String delimiter, String defaultSchema) {
        String[] qualifiers = objName.split("\\.");
        String schemaName = (qualifiers.length == 2) ? qualifiers[0] : defaultSchema;
        String objectName = (qualifiers.length == 2) ? qualifiers[1] : qualifiers[0];
        return new DatabaseObjectName(schemaName, objectName);
    }

    public String getSchemaName() {
        return schemaName;
    }

    public String getObjectName() {
        return objectName;
    }

    public String[] getQualifiers() {
        return new String[] { getSchemaName(), getObjectName() };
    }
}

