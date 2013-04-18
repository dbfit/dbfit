package dbfit.util.oracle;

import dbfit.util.DbParameterAccessor;
import static dbfit.util.DbParameterAccessor.INPUT;
import static dbfit.util.DbParameterAccessor.OUTPUT;
import static dbfit.util.DbParameterAccessor.INPUT_OUTPUT;
import static dbfit.util.DbParameterAccessor.RETURN_VALUE;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class OracleBooleanSpTestsFactory {
    private SpGeneratorOutput output;

    public static final String SP_ARG_CHR_IN = "p_1_in";
    public static final String SP_ARG_BOOL_IN = "p_bool_in";
    public static final String SP_ARG_NUM_IN = "p_num_in";
    public static final String SP_ARG_BOOL_OUT = "p_bool_out";
    public static final String SP_RETVAL_NUM = "";

    private void addSpParameter(Map<String, OracleSpParameter> spParams,
            String name, int direction, String type) {
        spParams.put(name, makeSpParameter(name, direction, type, "z"));
    }

    public Map<String, OracleSpParameter> createSampleSpParameters() {
        Map<String, OracleSpParameter> spParams = new HashMap<String, OracleSpParameter>();
        addSpParameter(spParams, SP_ARG_CHR_IN, INPUT, "VARCHAR2");
        addSpParameter(spParams, SP_ARG_BOOL_IN, INPUT, "BOOLEAN");
        addSpParameter(spParams, SP_ARG_NUM_IN, INPUT, "NUMBER");
        addSpParameter(spParams, SP_ARG_BOOL_OUT, OUTPUT, "BOOLEAN");
        addSpParameter(spParams, SP_RETVAL_NUM, RETURN_VALUE, "NUMBER");

        return spParams;
    }

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
            List<OracleSpParameter> args,
            OracleSpParameter returnValue) {
        OracleBooleanSpCommand command = OracleBooleanSpCommand.newInstance(
                spName, args, returnValue);
        command.setOutput(output);
        return command;
    }

    public OracleBooleanSpCommand makeSpCommand(String spName,
            List<OracleSpParameter> args) {
        return makeSpCommand(spName, args, null);
    }

}

