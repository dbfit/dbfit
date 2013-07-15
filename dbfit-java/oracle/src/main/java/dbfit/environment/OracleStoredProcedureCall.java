package dbfit.environment;

import dbfit.api.DBEnvironment;
import dbfit.api.DbStoredProcedureCall;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbParameterAccessors;
import dbfit.util.OracleDbParameterAccessor;
import dbfit.util.oracle.OracleBooleanSpCommand;
import dbfit.util.oracle.OracleSpParameter;
import dbfit.util.oracle.SpGeneratorOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dbfit.util.Direction;

public class OracleStoredProcedureCall extends DbStoredProcedureCall {
    public OracleStoredProcedureCall(DBEnvironment environment, String name, DbParameterAccessor[] accessors) {
        super(environment, name, accessors);
    }

    @Override
    public String toSqlString() {
        if (!containsBooleanType()) {
            return super.toSqlString();
        }

        OracleBooleanSpCommand command = initSpCommand();
        command.generate();
        return command.toString();
    }

    private void addAccessor(Map<String, DbParameterAccessor> map, DbParameterAccessor acc) {
        DbParameterAccessor prevAcc = map.get(acc.getName());
        if (prevAcc != null) {
            // Duplicated parameters are indication for single IN/OUT one.
            // So merging them here.
            prevAcc.setDirection(Direction.INPUT_OUTPUT);
        } else {
            // Put a copy - we don't want to change shared state
            map.put(acc.getName(), acc.clone());
        }
    }

    private Map<String, DbParameterAccessor> getAccessorsMap(
            DbParameterAccessor[] accessors) {
        Map<String, DbParameterAccessor> map = new HashMap<String, DbParameterAccessor>();
        for (DbParameterAccessor ac: accessors) {
            addAccessor(map, ac);
        }

        return map;
    }

    private OracleSpParameter makeOracleSpParameter(DbParameterAccessor ac) {
        String originalType = ((OracleDbParameterAccessor) ac).getOriginalTypeName();
        return OracleSpParameter.newInstance(
                ac.getName(), ac.getDirection(), originalType);
    }

    private static class SpParamsSpec {
        public List<OracleSpParameter> arguments = new ArrayList<OracleSpParameter>();

        public OracleSpParameter returnValue = null;
        public void add(OracleSpParameter param) {
            if (param.direction.isReturnValue()) {
                returnValue = param;
            } else {
                arguments.add(param);
            }
        }

    }

    private SpParamsSpec initSpParams() {
        List<String> accessorNames = new DbParameterAccessors(getAccessors()).getSortedAccessorNames();
        Map<String, DbParameterAccessor> accessorsMap = getAccessorsMap(getAccessors());

        SpParamsSpec params = new SpParamsSpec();

        for (String acName: accessorNames) {
            OracleSpParameter param = makeOracleSpParameter(accessorsMap.get(acName));
            params.add(param);
        }

        return params;
    }

    private OracleBooleanSpCommand initSpCommand() {
        SpParamsSpec params = initSpParams();
        OracleBooleanSpCommand command = OracleBooleanSpCommand.newInstance(
                getName(), params.arguments, params.returnValue);
        command.setOutput(new SpGeneratorOutput());

        return command;
    }

    private boolean containsBooleanType() {
        for (DbParameterAccessor ac: getAccessors()) {
            if (((OracleDbParameterAccessor) ac).isOriginalTypeBoolean()) {
                return true;
            }
        }
        return false;
    }
}
