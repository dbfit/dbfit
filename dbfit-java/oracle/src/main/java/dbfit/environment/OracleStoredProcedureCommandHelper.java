package dbfit.environment;

import dbfit.util.DbParameterAccessors;
import dbfit.util.DbStoredProcedureCommandHelper;
import dbfit.util.DbParameterAccessor;
import dbfit.util.OracleDbParameterAccessor;
import dbfit.util.oracle.OracleSpParameter;
import dbfit.util.oracle.OracleBooleanSpCommand;
import dbfit.util.oracle.SpGeneratorOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class OracleStoredProcedureCommandHelper extends DbStoredProcedureCommandHelper {

    @Override
    public String buildPreparedStatementString(String procName,
                                               DbParameterAccessor[] accessors) {
        if (!containsBooleanType(accessors)) {
            return super.buildPreparedStatementString(procName, accessors);
        }

        OracleBooleanSpCommand command = initSpCommand(procName, accessors);
        command.generate();
        return command.toString();
    }

    private void addAccessor(Map<String, DbParameterAccessor> map, DbParameterAccessor acc) {
        DbParameterAccessor prevAcc = map.get(acc.getName());
        if (prevAcc != null) {
            // Duplicated parameters are indication for single IN/OUT one.
            // So merging them here.
            prevAcc.setDirection(DbParameterAccessor.INPUT_OUTPUT);
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
            if (param.isReturnValue()) {
                returnValue = param;
            } else {
                arguments.add(param);
            }
        }

    }

    private SpParamsSpec initSpParams(DbParameterAccessor[] accessors) {
        List<String> accessorNames = new DbParameterAccessors(accessors).getSortedAccessorNames();
        Map<String, DbParameterAccessor> accessorsMap = getAccessorsMap(accessors);

        SpParamsSpec params = new SpParamsSpec();

        for (String acName: accessorNames) {
            OracleSpParameter param = makeOracleSpParameter(accessorsMap.get(acName));
            params.add(param);
        }

        return params;
    }

    private OracleBooleanSpCommand initSpCommand(String procName,
                                    DbParameterAccessor[] accessors) {
        SpParamsSpec params = initSpParams(accessors);
        OracleBooleanSpCommand command = OracleBooleanSpCommand.newInstance(
                procName, params.arguments, params.returnValue);
        command.setOutput(new SpGeneratorOutput());

        return command;
    }

    private boolean isBooleanAccessor(DbParameterAccessor accessor) {
        return ((OracleDbParameterAccessor) accessor).isOriginalTypeBoolean();
    }

    private boolean containsBooleanType(DbParameterAccessor[] accessors) {
        for (DbParameterAccessor ac: accessors) {
            if (isBooleanAccessor(ac)) {
                return true;
            }
        }
        return false;
    }
}

