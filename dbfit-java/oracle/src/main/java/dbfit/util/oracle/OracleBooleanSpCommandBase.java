package dbfit.util.oracle;

import java.util.List;

import static dbfit.util.oracle.OraclePlSqlGenerateUtils.findNonConflictingPrefix;

public class OracleBooleanSpCommandBase {
    protected SpGeneratorOutput out = null;
    protected String procName;
    protected String prefix;
    protected List<OracleSpParameter> arguments;
    protected OracleSpParameter returnValue;

    protected OracleBooleanSpCommandBase(String spName, 
            List<OracleSpParameter> args, OracleSpParameter returnValue) {
        this.procName = spName;
        this.arguments = args;
        this.returnValue = returnValue;

        setPrefix(findNonConflictingPrefix(procName));
        initParameterIds();
    }

    private void initPrefixes() {
        initArgsPrefixes();
        initReturnValuePrefix();
    }

    private void initParameterIds() {
        if (returnValue != null) {
            returnValue.setId("ret");
        }

        int i = 1;
        for (OracleSpParameter arg: arguments) {
            arg.setId("p" + i);
            ++i;
        }
    }

    private void initReturnValuePrefix() {
        if (isFunction()) {
            returnValue.setPrefix(prefix);
        }
    }

    private void initArgsPrefixes() {
        for (OracleSpParameter arg: arguments) {
            arg.setPrefix(prefix);
        }
    }

    protected void setPrefix(String prefix) {
        if (procName.toLowerCase().startsWith(prefix.toLowerCase())) {
            throw new IllegalArgumentException("Invalid prefix " + prefix +
                    " for procedure " + procName);
        }

        this.prefix = prefix;
        initPrefixes();
    }

    private void initArgsOutputs() {
        for (OracleSpParameter arg: arguments) {
            arg.setOutput(out);
        }
    }

    private void initReturnValueOutput() {
        if (isFunction()) {
            returnValue.setOutput(out);
        }
    }

    public void setOutput(SpGeneratorOutput out) {
        this.out = out;
        initArgsOutputs();
        initReturnValueOutput();
    }

    public SpGeneratorOutput getOutput() {
        return out;
    }

    public SpGeneratorOutput append(String s) {
        if (null != out) {
            out.append(s);
        }

        return out;
    }

    public String toString() {
        if (null == out) {
            return "";
        }

        return out.toString();
    }

    public String getPrefix() { return prefix; }

    public boolean isFunction() {
        return returnValue != null;
    }

    protected boolean isBooleanOutputOrReturn(OracleSpParameter param) {
        return (param != null) && param.isBoolean()
            && param.direction.isOutputOrReturnValue();
    }

    protected boolean hasBooleanReturn() {
        return isBooleanOutputOrReturn(returnValue);
    }

    protected boolean hasBooleanOutOrInoutOrReturn() {
        return hasBooleanReturn() || hasBooleanOutOrInout();
    }

    protected boolean hasBooleanOutOrInout() {
        for (OracleSpParameter arg: arguments) {
            if(arg.isBooleanOutOrInout()) {
                return true;
            }
        }

        return false;
    }

    protected boolean hasBooleanInOrInout() {
        for (OracleSpParameter arg: arguments) {
            if (arg.isBooleanInOrInout()) {
                return true;
            }
        }

        return false;
    }

    public OracleSpParameter getReturnValue() {
        return returnValue;
    }

    public List<OracleSpParameter> getArguments() {
        return arguments;
    }

    public String getProcName() {
        return procName;
    }

}

