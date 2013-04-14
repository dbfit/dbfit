package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;

import java.util.List;

public class OracleBooleanSpTestsFactory {
    private SpGeneratorOutput output;

    public SpGeneratorOutput getSpGeneratorOutput() {
        return output;
    }

    public OracleBooleanSpTestsFactory(SpGeneratorOutput output) {
        this.output = output;
    }

    public OracleSpParameter makeSpParameter(String paramName, int direction) {
        return makeSpParameter(paramName, direction, "VARCHAR2", "t");
    }

    public OracleSpParameter makeSpParameter(String paramName,
            int direction, String dataType, String prefix) {
        OracleSpParameter param = OracleSpParameter.newInstance(paramName,
                        direction, dataType, prefix);
        param.setOutput(output);
        return param;
    }

    public OracleBooleanSpCommand makeSpCommand(String spName,
            List<OracleSpParameter> args) {
        OracleBooleanSpCommand command = OracleBooleanSpCommand.newInstance(
                spName, args);
        command.setOutput(output);
        return command;
    }

}

