package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;
import static dbfit.util.oracle.OracleSpParameter.callExpr;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

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
        initArgsPrefixes();
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

    private void initArgsPrefixes() {
        for (OracleSpParameter arg: arguments) {
            arg.setPrefix(prefix);
        }
    }

    private void initArgsOutputs() {
        for (OracleSpParameter arg: arguments) {
            arg.setOutput(out);
        }
    }

    protected void setPrefix(String prefix) {
        if (procName.toLowerCase().startsWith(prefix.toLowerCase())) {
            throw new IllegalArgumentException("Invalid prefix " + prefix +
                    " for procedure " + procName);
        }

        this.prefix = prefix;
        initArgsPrefixes();
    }

    public void setOutput(SpGeneratorOutput out) {
        this.out = out;
        initArgsOutputs();
    }

    protected SpGeneratorOutput append(String s) {
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
            && param.isOutputOrReturnValue();
    }

    private boolean hasBooleanReturn() {
        return isBooleanOutputOrReturn(returnValue);
    }

    private boolean hasBooleanOutOrInoutOrReturn() {
        return hasBooleanReturn() || hasBooleanOutOrInout();
    }

    private boolean hasBooleanOutOrInout() {
        for (OracleSpParameter arg: arguments) {
            if (isBooleanOutputOrReturn(arg)) {
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

    /**
     * Generate the whole database call on the configured SpGeneratorOutput
     */
    public void generate() {
        out.append("declare\n");
        genBool2Chr();
        genChr2Bool();
        genWrapperSp();
        out.append("begin\n");
        out.append("    ").append(getWrapperCall()).append(";\n");
        out.append("end;\n");
        out.append("\n");
    }

    private void genWrapperSp() {
        if (needsWrapperSp()) {
            out.append(getWrapperHeader()).append("\n");
            out.append("    is\n");
            genWrapperVariables();
            out.append("    begin\n");
            out.append("        ");
            genSpCallWithinWrapper();
            out.append(";\n");
            assignOutputVariables();
            out.append("    end ").append(getWrapperName()).append(";\n");
            out.append("\n");
        }
    }

    public String getWrapperCall() {
        return getIsolatedOutput(new Generator() {
            @Override public void generate() {
                genWrapperCall();
            }
        });
    }

    public String getWrapperHeader() {
        return getIsolatedOutput(new Generator() {
            @Override public void generate() {
                genWrapperHeader();
            }
        });
    }

    private String getWrapperCallArguments() {
        return getIsolatedOutput(new Generator() {
            @Override public void generate() {
                genWrapperCallArguments();
            }
        });
    }

    private String getWrapperArguments() {
        return getIsolatedOutput(new Generator() {
            @Override public void generate() {
                genWrapperArguments();
            }
        });
    }


    private String getSpCallArguments() {
        return getIsolatedOutput(new Generator() {
            @Override public void generate() {
                genSpCallArguments();
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
            out.append(getChr2Bool());
        }
    }

    private void genBool2Chr() {
        if (needsBool2Chr()) {
            out.append(getBool2Chr());
        }
    }

    public void genWrapperCallArguments() {
        String separator = "";

        for (OracleSpParameter arg: arguments) {
            out.append(separator);
            arg.genWrapperCallArgument();
            separator = ", ";
        }
    }

    public void genSpCallArguments() {
        String separator = "";

        for (OracleSpParameter arg: arguments) {
            out.append(separator);
            arg.genSpCallArgumentWithinWrapper();
            separator = ", ";
        }
    }

    public void genWrapperArguments() {
        String separator = "";

        for (OracleSpParameter arg: arguments) {
            out.append(separator);
            arg.declareArgument();
            separator = ", ";
        }
    }

    private void genWrapperVariables() {
        declareArgumentVars();
        declareReturnValueVar();
    }

    private void declareArgumentVars() {
        for (OracleSpParameter arg: arguments) {
            arg.declareVariable();
        }
    }

    private void declareReturnValueVar() {
        if (returnValue != null) {
            returnValue.declareVariable();
        }
    }

    private void assignOutputVariables() {
        for (OracleSpParameter arg: arguments) {
            arg.assignOutputVariable();
        }
    }

    private String getWrapperReturnVar() {
        return isFunction() ? returnValue.getWrapperVarName() : null;
    }

    private void genSpCallLeftSide(String var) {
        if (var != null) {
            out.append(var).append(" := ");
        }
    }

    public void genSpCallWithinWrapper() {
        genSpCallLeftSide(getWrapperReturnVar());
        out.append(callExpr(procName, getSpCallArguments()));
    }

    public void genWrapperCall() {
        genSpCallLeftSide(isFunction() ? "?" : null);
        out.append(callExpr(getWrapperName(), getWrapperCallArguments()));
    }

    private String getSpKind() {
        return isFunction() ? "function" : "procedure";
    }

    public void genWrapperHeader() {
        out.append("    ").append(getSpKind()).append(" ");
        out.append(callExpr(getWrapperName(), getWrapperArguments()));
    }

    protected void genWrapperDefinition() {
        if (!needsWrapperSp()) {
            return;
        }

        out.append("    ").append(getSpKind()).append(" ");
        genWrapperHeader();
    }

    private String getWrapperName() {
        if (needsWrapperSp()) {
            return getPrefix() + "_wrapper";
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
}

