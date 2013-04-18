package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;

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
            && param.isOutputOtReturnValue();
    }

    private boolean hasBooleanOutputOrReturn() {
        if (isBooleanOutputOrReturn(returnValue)) {
            return true;
        }

        for (OracleSpParameter arg: arguments) {
            if (isBooleanOutputOrReturn(arg)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Generate the whole database call on the configured SpGeneratorOutput
     */
    public void generate() {
        if (hasBooleanOutputOrReturn()) {
            generateWithBooleanOutput();
        } else {
            generateWithoutBooleanOutput();
        }
    }

    private void generateWithoutBooleanOutput() {
        String template = loadChr2BoolTemplate();
        String result = template.replace("${sp_call}", getWrapperCall());
        result = result.replace("${prefix}", getPrefix());
        out.append(result);
    }

    private void generateWithBooleanOutput() {
        out.append("declare\n");
        out.append(getBool2Chr());
        out.append("\n");
        out.append("begin\n");
        out.append("    ").append(getWrapperCall()).append("\n");
        out.append("end;\n");
        out.append("\n");
    }

    protected void callWhitespace() {
        if (arguments.size() > 0) {
            out.append(" ");
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

    private String getBool2Chr() {
        String template = loadBool2ChrTemplate();
        return template.replace("${prefix}", getPrefix());
    }

    public void genWrapperCallArguments() {
        String separator = "";

        callWhitespace();

        for (OracleSpParameter arg: arguments) {
            out.append(separator);
            arg.genWrapperCallArgument();
            separator = ", ";
        }

        callWhitespace();
    }

    public void genWrapperArguments() {
        String separator = "";

        callWhitespace();

        for (OracleSpParameter arg: arguments) {
            out.append(separator);
            arg.declareArgument();
            separator = ", ";
        }

        callWhitespace();
    }

    private String getWrapperReturnVar() {
        if (hasBooleanOutputOrReturn()) {
            return getPrefix() + getPrefix() + "_ret";
        } else {
            return "?";
        }
    }

    private void genWrapperCallLeftSide() {
        if (isFunction()) {
            out.append(getWrapperReturnVar()).append(" := ");
        }
    }

    public void genWrapperCall() {
        genWrapperCallLeftSide();

        out.append(getWrapperName());
        out.append("(");
        genWrapperCallArguments();
        out.append(")");
    }

    protected String loadChr2BoolTemplate() {
        StringBuilder sb = new StringBuilder();

        sb.append("declare\n");
        sb.append("    function ${prefix}_chr2bool( p_arg VARCHAR2 ) return BOOLEAN\n");
        sb.append("    is\n");
        sb.append("    begin\n");
        sb.append("        if ( p_arg = 'true' )\n");
        sb.append("        then\n");
        sb.append("            return true;\n");
        sb.append("        elsif ( p_arg = 'false' )\n");
        sb.append("        then\n");
        sb.append("            return false;\n");
        sb.append("        elsif ( p_arg is null )\n");
        sb.append("        then\n");
        sb.append("            return null;\n");
        sb.append("        else\n");
        sb.append("            raise_application_error( -20013, 'Error. Expected true or false, got: ' || p_arg );\n");
        sb.append("        end if;\n");
        sb.append("    end ${prefix}_chr2bool;\n");
        sb.append("\n");
        sb.append("begin\n");
        sb.append("    ${sp_call};\n");
        sb.append("end;\n");
        sb.append("\n");

        return sb.toString();
    }

    private String getSpKind() {
        return isFunction() ? "function" : "procedure";
    }

    public void genWrapperHeader() {
        out.append("    ").append(getSpKind()).append(" ");
        out.append(getWrapperName());

        out.append("(");
        genWrapperArguments();
        out.append(")");
    }

    protected void genWrapperDefinition() {
        if (!hasBooleanOutputOrReturn()) {
            return;
        }

        out.append("    ").append(getSpKind()).append(" ");
        genWrapperHeader();
    }

    private String getWrapperName() {
        if (hasBooleanOutputOrReturn()) {
            return getPrefix() + "_wrapper";
        } else {
            // no need of real wrapper sp
            return procName;
        }
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

