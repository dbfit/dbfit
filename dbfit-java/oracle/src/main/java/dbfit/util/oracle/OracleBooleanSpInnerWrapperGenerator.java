package dbfit.util.oracle;

import static dbfit.util.oracle.OraclePlSqlGenerateUtils.callExpr;
import static dbfit.util.oracle.OraclePlSqlGenerateUtils.getSpCallLeftSide;

import org.apache.commons.io.IOUtils;

public class OracleBooleanSpInnerWrapperGenerator {

    OracleBooleanSpCommand cmd;

    public OracleBooleanSpInnerWrapperGenerator(OracleBooleanSpCommand cmd) {
        this.cmd = cmd;
    }

    public void generate() {
        genWrapperHeader();
        append(IOUtils.LINE_SEPARATOR);
        append("    is").append(IOUtils.LINE_SEPARATOR);
        genWrapperVariables();
        append("    begin").append(IOUtils.LINE_SEPARATOR);
        append("        ");
        genSpCallWithinWrapper();
        append(";").append(IOUtils.LINE_SEPARATOR);
        assignOutputVariables();
        genWrapperReturnStatement();
        append("    end ").append(getWrapperName()).append(";").append(IOUtils.LINE_SEPARATOR);
        append(IOUtils.LINE_SEPARATOR);
    }

    private String getWrapperArguments() {
        return new Gen() {{ genWrapperArguments(); }}.toString();
    }

    private String getSpCallArguments() {
        return new Gen() {{ genSpCallArguments(); }}.toString();
    }

    public void genSpCallArguments() {
        String separator = "";

        for (OracleSpParameter arg: cmd.getArguments()) {
            append(separator);
            arg.genSpCallArgumentWithinWrapper();
            separator = ", ";
        }
    }

    public void genWrapperArguments() {
        String separator = "";

        for (OracleSpParameter arg: cmd.getArguments()) {
            append(separator);
            arg.declareArgument();
            separator = ", ";
        }
    }

    private void genWrapperVariables() {
        declareArgumentVars();
        declareReturnValueVar();
    }

    private void declareArgumentVars() {
        for (OracleSpParameter arg: cmd.getArguments()) {
            arg.declareVariable();
        }
    }

    private void declareReturnValueVar() {
        if (cmd.isFunction()) {
            cmd.getReturnValue().declareVariable();
        }
    }

    private void assignOutputVariables() {
        for (OracleSpParameter arg: cmd.getArguments()) {
            arg.assignOutputVariable();
        }
    }

    private String getWrapperReturnVar() {
        return cmd.isFunction() ? cmd.getReturnValue().getWrapperVarName() : null;
    }

    private void genWrapperReturnStatement() {
        if (cmd.isFunction()) {
            append("        ").append("return ")
               .append(getWrapperReturnVar()).append(";").append(IOUtils.LINE_SEPARATOR);
        }
    }

    public void genSpCallWithinWrapper() {
        append(getSpCallLeftSide(getWrapperReturnVar()));
        append(callExpr(cmd.getProcName(), getSpCallArguments()));
    }

    private String getSpKind() {
        return cmd.isFunction() ? "function" : "procedure";
    }

    public void genWrapperHeader() {
        append("    ").append(getSpKind()).append(" ");
        append(callExpr(getWrapperName(), getWrapperArguments()));
        if (cmd.isFunction()) {
            cmd.getReturnValue().declareReturnValue();
        }
    }

    public String getWrapperName() {
        return cmd.getPrefix() + "_wrapper";
    }

    protected SpGeneratorOutput append(String s) {
        return cmd.append(s);
    }

    @Override
    public String toString() {
        return cmd.toString();
    }

    /*
     * Utility class for short notation of capturing append() output.
     *
     * Usage:
     * String result = new Gen() {{ doStuff(); }}.toString();
     *
     * Will temporariliy redirect output and return result as string.
     */
    class Gen {
        SpGeneratorOutput savedOut = cmd.getOutput();
        SpGeneratorOutput wrkOut = new SpGeneratorOutput();

        public Gen() {
            cmd.setOutput(wrkOut);
        }

        @Override public String toString() {
            cmd.setOutput(savedOut);
            return wrkOut.toString();
        }
    }

}

