package dbfit.util.oracle;

import java.util.List;

import static dbfit.util.oracle.OraclePlSqlGenerateUtils.callExpr;
import static dbfit.util.oracle.OraclePlSqlGenerateUtils.getSpCallLeftSide;
import static dbfit.util.oracle.OracleBooleanConversions.*;

public class OracleBooleanSpCommand extends OracleBooleanSpCommandBase {

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
            List<OracleSpParameter> args, OracleSpParameter returnValue) {
        super(spName, args, returnValue);
    }

    private boolean needsWrapperSp() {
        return hasBooleanOutOrInout();
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

    private void genChr2Bool() {
        if (needsChr2Bool()) {
            append(getChr2Bool(getPrefix()));
        }
    }

    private void genBool2Chr() {
        if (needsBool2Chr()) {
            append(getBool2Chr(getPrefix()));
        }
    }

    public String getCallArguments() {
        StringBuilder sb = new StringBuilder();
        String separator = "";

        for (OracleSpParameter arg: arguments) {
            sb.append(separator);
            sb.append(arg.getCallArgument());
            separator = ", ";
        }
        return sb.toString();
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

}

