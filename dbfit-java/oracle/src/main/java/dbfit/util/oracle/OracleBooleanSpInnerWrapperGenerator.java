package dbfit.util.oracle;

import static dbfit.util.oracle.OraclePlSqlGenerateUtils.callExpr;
import static dbfit.util.oracle.OraclePlSqlGenerateUtils.getSpCallLeftSide;

public class OracleBooleanSpInnerWrapperGenerator {

    OracleBooleanSpCommand cmd;

    public OracleBooleanSpInnerWrapperGenerator(OracleBooleanSpCommand cmd) {
        this.cmd = cmd;
    }

    public void generate() {
        genWrapperHeader();
        append("\n");
        append("    is\n");
        genWrapperVariables();
        append("    begin\n");
        append("        ");
        genSpCallWithinWrapper();
        append(";\n");
        assignOutputVariables();
        genWrapperReturnStatement();
        append("    end ").append(getWrapperName()).append(";\n");
        append("\n");
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
               .append(getWrapperReturnVar()).append(";\n");
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

    private interface Generator {
        void generate();
    }

    private String getIsolatedOutput(Generator g) {
        SpGeneratorOutput savedOut = cmd.getOutput();;
        SpGeneratorOutput wrkOut = new SpGeneratorOutput();
        cmd.setOutput(wrkOut);
        g.generate();
        String result = cmd.toString();
        cmd.setOutput(savedOut);
        return result;
    }

    @Override
    public String toString() {
        return cmd.toString();
    }
}

