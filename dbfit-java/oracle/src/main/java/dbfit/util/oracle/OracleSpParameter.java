package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;

public class OracleSpParameter {
    protected int direction; // In terms of DbParameterAccessor constants
    protected SpGeneratorOutput out = null;
    protected String dataType; // original type name in the original sp
    protected String id; // id to be used for generating param/arg names
    protected String prefix; // prefix to be used for all names to avoid conflicts

    public static OracleSpParameter newInstance(String paramName, int direction,
                            String dataType, String prefix) {
        return new OracleSpParameter(paramName, direction, dataType, prefix);
    }

    protected OracleSpParameter(String paramName, int direction, String dataType,
                                String prefix) {
        this.direction = direction;
        this.dataType = dataType;
        this.id = paramName;
        this.prefix = prefix;
    }

    protected int getDirection() {
        return direction;
    }
    
    protected boolean isReturnValue() {
        return getDirection() == DbParameterAccessor.RETURN_VALUE;
    }

    protected String getDataType() {
        return dataType;
    }

    protected boolean isBoolean() {
        return getDataType().equals("BOOLEAN");
    }

    public String getDirectionName() {
        switch (getDirection()) {
            case DbParameterAccessor.INPUT_OUTPUT:
                return "IN OUT";
            case DbParameterAccessor.OUTPUT:
                return "OUT";
            case DbParameterAccessor.INPUT:
                return "IN";
            default:
                return "RETURN";
        }
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

    public void declareArgument() {

    }

    public void genWrapperCallArgument() {
        if (isBoolean()) {
            out.append(prefix).append("_chr2bool( ? )");
        } else {
            out.append("?");
        }
    }

}

