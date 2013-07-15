package dbfit.util.oracle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dbfit.util.Direction;
import static dbfit.util.Direction.*;

public class OracleBooleanSpTestsFactory {
    private SpGeneratorOutput output;

    public static final String SP_ARG_CHR_IN = "p_1_in";
    public static final String SP_ARG_BOOL_IN = "p_bool_in";
    public static final String SP_ARG_NUM_IN = "p_num_in";
    public static final String SP_ARG_BOOL_OUT = "p_bool_out";
    public static final String SP_ARG_BOOL_INOUT = "p_bool_inout";
    public static final String SP_RETVAL_NUM = "";

    public class OracleSpCommandBuilder {
        private List<OracleSpParameter> args = new ArrayList<OracleSpParameter>();
        private OracleSpParameter returnValue = null;
        private String spName;
        private String prefix = null;

        private String genArgName() {
            return "p_arg" + (args.size() + 1);
        }

        public OracleSpCommandBuilder() {
            this("spdemo");
        }

        public OracleSpCommandBuilder(String spName) {
            this.spName = spName;
        }

        public OracleSpCommandBuilder withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public OracleSpCommandBuilder withBooleanArgument(String name, Direction direction) {
            return withArgument(name, direction, "BOOLEAN");
        }

        public OracleSpCommandBuilder withBooleanArgument(Direction direction) {
            return withBooleanArgument(genArgName(), direction);
        }

        public OracleSpCommandBuilder withArgument(String name, Direction direction,
                                String dataType) {
            args.add(makeSpParameter(name, direction, dataType, prefix));
            return this;
        }

        public OracleSpCommandBuilder withArgument(Direction direction, String dataType) {
            return withArgument(genArgName(), direction, dataType);
        }

        public OracleSpCommandBuilder withArgument(OracleSpParameter p) {
            args.add(p);
            return this;
        }

        public OracleSpCommandBuilder withReturnValue(String dataType) {
            OracleSpParameter ret = makeSpParameter("", RETURN_VALUE, dataType, prefix);
            return withReturnValue(ret);
        }

        public OracleSpCommandBuilder withReturnValue(OracleSpParameter p) {
            returnValue = p;
            return this;
        }

        public OracleSpCommandBuilder withName(String spName) {
            this.spName = spName;
            return this;
        }

        public OracleBooleanSpCommand build() {
            OracleBooleanSpCommand cmd = makeSpCommand(spName, args, returnValue);
            if (null != prefix) {
                cmd.setPrefix(prefix);
            }

            return cmd;
        }
    }

    public OracleSpCommandBuilder getSpCommandBuilder(String spName) {
        return new OracleSpCommandBuilder(spName);
    }

    private void addSpParameter(Map<String, OracleSpParameter> spParams,
            String name, Direction direction, String type) {
        spParams.put(name, makeSpParameter(name, direction, type, "z"));
    }

    public Map<String, OracleSpParameter> createSampleSpParameters() {
        Map<String, OracleSpParameter> spParams = new HashMap<String, OracleSpParameter>();
        addSpParameter(spParams, SP_ARG_CHR_IN, INPUT, "VARCHAR2");
        addSpParameter(spParams, SP_ARG_NUM_IN, INPUT, "NUMBER");
        addSpParameter(spParams, SP_ARG_BOOL_IN, INPUT, "BOOLEAN");
        addSpParameter(spParams, SP_ARG_BOOL_INOUT, INPUT_OUTPUT, "BOOLEAN");
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

    public OracleSpParameter makeSpParameter(String paramName, Direction direction) {
        return makeSpParameter(paramName, direction, "VARCHAR2", "t");
    }

    public OracleSpParameter makeSpParameter(String paramName,
                                             Direction direction, String dataType, String prefix) {
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

