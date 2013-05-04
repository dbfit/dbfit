package dbfit.util.oracle;

import static dbfit.util.DbParameterAccessor.Direction;

public class OracleSpParameter {
    public Direction direction; // In terms of DbParameterAccessor constants
    protected SpGeneratorOutput out = null;
    protected String dataType; // original type name in the original sp
    protected String id; // id to be used for generating param/arg names
    protected String prefix; // prefix to be used for all names to avoid conflicts

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
        this.direction = direction;
        this.dataType = dataType;
        this.id = paramName;
        this.prefix = prefix;
    }

    protected Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isBooleanInOrInout() {
        return isBoolean() && direction.isInOrInout();
    }

    public boolean isBooleanOutOrInout() {
        return isBoolean() && direction.isOutOrInout();
    }

    protected String getDataType() {
        return dataType;
    }

    private boolean needsArgumentTypeChange() {
        return isBooleanOutOrInout();
    }

    private String getWrapperArgumentType() {
        return needsArgumentTypeChange() ? "VARCHAR2" : getDataType();
    }

    public boolean isBoolean() {
        return getDataType().equals("BOOLEAN");
    }

    public String getDirectionName() {
        switch (getDirection()) {
            case INPUT_OUTPUT:
                return "IN OUT";
            case OUTPUT:
                return "OUT";
            case INPUT:
                return "IN";
            default:
                return "RETURN";
        }
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

    public void setId(String id) {
        this.id = id;
    }

    public void setOutput(SpGeneratorOutput out) {
        this.out = out;
    }

    protected SpGeneratorOutput append(String s) {
        if (out != null) {
            out.append(s);
        }

        return out;
    }

    private String getWrapperArgumentName() {
        return direction.isReturnValue() ? "" : prefixed(id);
    }

    public String getWrapperVarName() {
        String varid = direction.isReturnValue() ? "" : id + "_";
        return prefixed("v_" + varid + getShortDirectionName());
    }

    public String toString() {
        if (out == null) {
            return "";
        } else {
            return out.toString();
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
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

    private String prefixed(String expr) {
        return prefix + "_" + expr;
    }

    public static String callExpr(String func, String args) {
        String ws = (args.trim().length() == 0) ? "" : " ";
        return func + "(" + ws + args + ws + ")";
    }

    private String prefixedCallExpr(String func, String args) {
        return callExpr(prefixed(func), args);
    }

    private String chr2bool(String arg) {
        return prefixedCallExpr("chr2bool", arg);
    }

    private String bool2chr(String arg) {
        return prefixedCallExpr("bool2chr", arg);
    }
}

