package dbfit.util;

public class OracleDbParameterAccessor extends DbParameterAccessor {
    private String originalTypeName;

    public OracleDbParameterAccessor(String name, Direction direction, int sqlType, Class<?> javaType, int position,
            TypeTransformerFactory dbfitToJdbcTransformerFactory, String originalTypeName, String userTypeName) {
        super(name, direction, sqlType, userTypeName, javaType, position, dbfitToJdbcTransformerFactory);
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
    protected DbParameterAccessor copy() {
        return new OracleDbParameterAccessor(getName(), getDirection(), getSqlType(), getJavaType(), getPosition(),
                getDbfitToJdbcTransformerFactory(), originalTypeName, getUserDefinedTypeName());
    }
}
