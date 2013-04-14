package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;

import java.util.List;

public class OracleBooleanSpCommand {
    protected SpGeneratorOutput out = null;
    protected String procName;
    protected String prefix;

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

        initPrefix();
    }

    public void initPrefix() {
        char p = Character.toLowerCase(procName.charAt(0));
        char c = 'a';

        while ((c == p) && (c < 'z')) {
            ++c;
        }

        prefix = String.valueOf(c);
    }

    protected void setPrefix(String prefix) {
        if (procName.toLowerCase().startsWith(prefix.toLowerCase())) {
            throw new IllegalArgumentException("Invalid prefix " + prefix +
                    " for procedure " + procName);
        }

        this.prefix = prefix;
    }

    public void setOutput(SpGeneratorOutput out) {
        this.out = out;
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
        result = result.replace("${sp_params}", " t_chr2bool( ? ) ");
        result = result.replace("${prefix}", getPrefix());
        out.append(result);
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

