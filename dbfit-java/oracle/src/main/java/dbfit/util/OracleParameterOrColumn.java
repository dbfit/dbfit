package dbfit.util;

public class OracleParameterOrColumn extends ParameterOrColumn {
    private String originalTypeName;

    public OracleParameterOrColumn(String name, Direction direction, int sqlType,
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
    public OracleParameterOrColumn clone() {
        OracleParameterOrColumn copy = new OracleParameterOrColumn(
                getName(), getDirection(), getSqlType(), getJavaType(), getPosition(), originalTypeName);
        copy.cs = null;

        return copy;
    }
}

