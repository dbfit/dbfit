package dbfit.util.oracle;

import dbfit.util.Direction;

public class OracleSpParameter extends OracleSpParameterBase {

    public static OracleSpParameter newInstance(String paramName, Direction direction,
                            String dataType) {
        return newInstance(paramName, direction, dataType, "x");
    }

    public static OracleSpParameter newInstance(String paramName, Direction direction,
                            String dataType, String prefix) {
        return new OracleSpParameter(paramName, direction, dataType, prefix);
    }

    protected OracleSpParameter(String paramName, Direction direction, String dataType,
                                String prefix) {
        super(paramName, direction, dataType, prefix);
    }

    private boolean needsArgumentTypeChange() {
        return isBooleanOutOrInout();
    }

    private String getWrapperArgumentType() {
        return needsArgumentTypeChange() ? "VARCHAR2" : getDataType();
    }

    public String getShortDirectionName() {
        switch (getDirection()) {
            case INPUT_OUTPUT:
                return "inout";
            case OUTPUT:
                return "out";
            case INPUT:
                return "in";
            default:
                return "ret";
        }
    }

    private String getWrapperArgumentName() {
        return direction.isReturnValue() ? "" : prefixed(id);
    }

    public String getWrapperVarName() {
        String varid = direction.isReturnValue() ? "" : id + "_";
        return prefixed("v_" + varid + getShortDirectionName());
    }

    private void declareArgumentOrReturnValue() {
        out.append(getWrapperArgumentName())
            .append(" ")
            .append(getDirectionName())
            .append(" ").append(getWrapperArgumentType());
    }

    public void declareArgument() {
        declareArgumentOrReturnValue();
    }

    public void declareReturnValue() {
        declareArgumentOrReturnValue();
    }

    private void initializeVariable() {
        if (getDirection() == Direction.INPUT_OUTPUT) {
            out.append(" := ").append(chr2bool(getWrapperArgumentName()));
        }
    }

    public void declareVariable() {
        if (needsArgumentTypeChange() || direction.isReturnValue()) {
            out.append("        ")
                .append(getWrapperVarName())
                .append(" ").append(getDataType());

            initializeVariable();

            out.append(";\n");
        }
    }

    public void assignOutputVariable() {
        if (needsArgumentTypeChange()) {
            out.append("        ")
                .append(getWrapperArgumentName())
                .append(" := ")
                .append(bool2chr(getWrapperVarName()))
                .append(";\n");
        }
    }

    public String getCallArgument() {
        return getCallArgument("?");
    }

    private String getCallArgument(String varname) {
        StringBuilder sb = new StringBuilder();
        if (isBoolean() && direction.isInput()) {
            sb.append(chr2bool(varname));
        } else {
            sb.append(varname);
        }
        return sb.toString();
    }

    public void genSpCallArgumentWithinWrapper() {
        if (needsArgumentTypeChange()) {
            out.append(getWrapperVarName());
        } else {
            out.append(getWrapperArgumentName());
        }
    }

    private String chr2bool(String arg) {
        return prefixedCallExpr("chr2bool", arg);
    }

    private String bool2chr(String arg) {
        return prefixedCallExpr("bool2chr", arg);
    }
}

