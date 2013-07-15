package dbfit.util.oracle;

import dbfit.util.Direction;
import static dbfit.util.oracle.OraclePlSqlGenerateUtils.callExpr;

public class OracleSpParameterBase {
    public Direction direction; // In terms of DbParameterAccessor constants
    protected SpGeneratorOutput out = null;
    protected String dataType; // original type name in the original sp
    protected String id; // id to be used for generating param/arg names
    protected String prefix; // prefix to be used for all names to avoid conflicts

    protected OracleSpParameterBase(String paramName, Direction direction, String dataType,
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

    protected String prefixed(String expr) {
        return prefix + "_" + expr;
    }

    protected String prefixedCallExpr(String func, String args) {
        return callExpr(prefixed(func), args);
    }

}

