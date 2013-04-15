package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;

import java.util.List;

public class OracleBooleanSpCommand {
    protected SpGeneratorOutput out = null;
    protected String procName;
    protected String prefix;
    protected List<OracleSpParameter> arguments;

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

        initPrefix();
        initArgsPrefixes();
    }

    public void initPrefix() {
        char p = Character.toLowerCase(procName.charAt(0));
        char c = 'a';

        while ((c == p) && (c < 'z')) {
            ++c;
        }

        prefix = String.valueOf(c);
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

    /**
     * Generate the whole database call on the configured SpGeneratorOutput
     */
    public void generate() {
        String template = loadChr2BoolTemplate();
        String result = template.replace("${sp_name}", procName);
        result = result.replace("${sp_params}", getWrapperCallArguments());
        result = result.replace("${prefix}", getPrefix());
        out.append(result);
    }

    protected void callWhitespace() {
        if (arguments.size() > 0) {
            out.append(" ");
        }
    }

    private String getWrapperCallArguments() {
        SpGeneratorOutput savedOut = out;
        SpGeneratorOutput wrkOut = new SpGeneratorOutput();
        setOutput(wrkOut);
        genWrapperCallArguments();
        String result = toString();
        setOutput(savedOut);
        return result;
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
        sb.append("    ${sp_name}(${sp_params});\n");
        sb.append("end;\n");
        sb.append("\n");

        return sb.toString();
    }
}

