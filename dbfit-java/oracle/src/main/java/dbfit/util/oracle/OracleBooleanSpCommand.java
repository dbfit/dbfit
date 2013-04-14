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
    }

}

