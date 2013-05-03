package dbfit.util.oracle;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static dbfit.util.oracle.OraclePlSqlGenerateUtils.callExpr;
import static dbfit.util.oracle.OraclePlSqlGenerateUtils.getSpCallLeftSide;

public class OracleBooleanSpCommand {
    protected SpGeneratorOutput out = null;
    protected String procName;
    protected String prefix;
    protected List<OracleSpParameter> arguments;
    protected OracleSpParameter returnValue;

    public static OracleBooleanSpCommand newInstance(String spName, 
            List<OracleSpParameter> args) {
        return newInstance(spName, args, null);
    }

    public static OracleBooleanSpCommand newInstance(String spName, 
            List<OracleSpParameter> args,
            OracleSpParameter returnValue) {
        return new OracleBooleanSpCommand(spName, args, returnValue);
    }

    protected OracleBooleanSpCommand(String spName, 
            List<OracleSpParameter> args,
            OracleSpParameter returnValue) {
        this.procName = spName;
        this.arguments = args;
        this.returnValue = returnValue;

        initPrefix();
        initPrefixes();
        initParameterIds();
    }

    public void initPrefix() {
        char p = Character.toLowerCase(procName.charAt(0));
        char c = 'a';

        while ((c == p) && (c < 'z')) {
            ++c;
        }

        prefix = String.valueOf(c);
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

    private void initPrefixes() {
        initArgsPrefixes();
        initReturnValuePrefix();
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

    private void initOutputs() {
        initArgsOutputs();
        initReturnValueOutput();
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

    protected void setPrefix(String prefix) {
        if (procName.toLowerCase().startsWith(prefix.toLowerCase())) {
            throw new IllegalArgumentException("Invalid prefix " + prefix +
                    " for procedure " + procName);
        }

        this.prefix = prefix;
        initPrefixes();
    }

    public void setOutput(SpGeneratorOutput out) {
        this.out = out;
        initOutputs();
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

    private boolean isBooleanOutputOrReturn(OracleSpParameter param) {
        return (param != null) && param.isBoolean()
            && param.direction.isOutputOrReturnValue();
    }

    private boolean hasBooleanReturn() {
        return isBooleanOutputOrReturn(returnValue);
    }

    private boolean hasBooleanOutOrInoutOrReturn() {
        return hasBooleanReturn() || hasBooleanOutOrInout();
    }

    private boolean hasBooleanOutOrInout() {
        for (OracleSpParameter arg: arguments) {
            if(arg.isBooleanOutOrInout()) {
                return true;
            }
        }

        return false;
    }

    private boolean needsWrapperSp() {
        return hasBooleanOutOrInout();
    }

    private boolean hasBooleanInOrInout() {
        for (OracleSpParameter arg: arguments) {
            if (arg.isBooleanInOrInout()) {
                return true;
            }
        }

        return false;
    }

    private boolean needsChr2Bool() {
        return hasBooleanInOrInout();
    }

    private boolean needsBool2Chr() {
        return hasBooleanOutOrInoutOrReturn();
    }

    public OracleBooleanSpInnerWrapperGenerator getWrapper() {
        return new OracleBooleanSpInnerWrapperGenerator(this);
    }

    /**
     * Generate the whole database call on the configured SpGeneratorOutput
     */
    public void generate() {
        append("declare\n");
        genBool2Chr();
        genChr2Bool();
        genWrapperSp();
        append("begin\n");
        append("    ");
        genCall();
        append(";\n");
        append("end;\n");
        append("\n");
    }

    private void genWrapperSp() {
        if (needsWrapperSp()) {
            getWrapper().generate();
        }
    }

    private String getCallArguments() {
        return getIsolatedOutput(new Generator() {
            @Override public void generate() {
                genCallArguments();
            }
        });
    }

    private String getChr2Bool() {
        String template = loadChr2BoolTemplate();
        return template.replace("${prefix}", getPrefix());
    }

    private String getBool2Chr() {
        String template = loadBool2ChrTemplate();
        return template.replace("${prefix}", getPrefix());
    }

    private void genChr2Bool() {
        if (needsChr2Bool()) {
            append(getChr2Bool());
        }
    }

    private void genBool2Chr() {
        if (needsBool2Chr()) {
            append(getBool2Chr());
        }
    }

    public void genCallArguments() {
        String separator = "";

        for (OracleSpParameter arg: arguments) {
            append(separator);
            arg.genCallArgument();
            separator = ", ";
        }
    }

    private void assignOutputVariables() {
        for (OracleSpParameter arg: arguments) {
            arg.assignOutputVariable();
        }
    }

    public void genCall() {
        append(getSpCallLeftSide(isFunction() ? "?" : null));
        String callText = callExpr(getCallName(), getCallArguments());
        if (hasBooleanReturn()) {
            callText = callExpr(getPrefix() + "_bool2chr", callText);
        }

        append(callText);
    }

    private String getCallName() {
        if (needsWrapperSp()) {
            return getWrapper().getWrapperName();
        } else {
            // no need of real wrapper sp
            return procName;
        }
    }

    protected String loadChr2BoolTemplate() {
        return loadResource("chr2bool.pls");
    }

    protected String loadBool2ChrTemplate() {
        return loadResource("bool2chr.pls");
    }

    private String loadResource(String resource) {
        InputStream in = getClass().getResourceAsStream(resource);
        try {
            return IOUtils.toString(in, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private interface Generator {
        void generate();
    }

    private String getIsolatedOutput(Generator g) {
        SpGeneratorOutput savedOut = out;
        SpGeneratorOutput wrkOut = new SpGeneratorOutput();
        setOutput(wrkOut);
        g.generate();
        String result = toString();
        setOutput(savedOut);
        return result;
    }

    /* ------------ */
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

