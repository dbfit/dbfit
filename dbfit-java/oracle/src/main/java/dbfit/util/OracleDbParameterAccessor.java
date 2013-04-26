package dbfit.util;

public class OracleDbParameterAccessor extends DbParameterAccessor {
    private String originalTypeName;

    public OracleDbParameterAccessor(String name, int direction, int sqlType,
            Class javaType, int position) {
        this(name, direction, sqlType, javaType, position, null);
    }

    public OracleDbParameterAccessor(String name, int direction, int sqlType,
            Class javaType, int position,
            String originalTypeName) {
        super(name, direction, sqlType, javaType, position);
        setOriginalTypeName(originalTypeName);
    }

    public String getOriginalTypeName() {
        return originalTypeName;
    }

    public void setOriginalTypeName(String typeName) {
        this.originalTypeName = typeName;
    }

    public boolean isOriginalTypeBoolean() {
        return getOriginalTypeName().contains("BOOLEAN");
    }

    @Override
    public OracleDbParameterAccessor clone() {
        OracleDbParameterAccessor copy = new OracleDbParameterAccessor(
                name, direction, sqlType, javaType, position, originalTypeName);
        copy.cs = null;

        return copy;
    }
}

