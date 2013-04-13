package dbfit.environment;

import dbfit.util.DbStoredProcedureCommandHelper;
import dbfit.util.DbParameterAccessor;
import dbfit.util.DbStoredProcedureCommandHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class OracleStoredProcedureCommandHelper extends DbStoredProcedureCommandHelper { 
    private String loadWrapperTemplate() {
        StringBuilder sb = new StringBuilder();

        sb.append("declare\n");
        sb.append("    function chr2bool( p_arg varchar2 ) return boolean\n");
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
        sb.append("            raise_application_error( -20013, 'Error. Expected true or false got: ' || p_arg );\n");
        sb.append("        end if;\n");
        sb.append("    end chr2bool;\n");
        sb.append("\n");
        sb.append("begin\n");
        sb.append("    ${sp_name}(${sp_params});\n");
        sb.append("end;\n");
        sb.append("\n");

        return sb.toString();
    }

    private String wrapperTemplate = loadWrapperTemplate();

    private Map<String, DbParameterAccessor> getAccessorsMap(
            DbParameterAccessor[] accessors) {
        Map<String, DbParameterAccessor> map = new HashMap<String, DbParameterAccessor>();
        for (DbParameterAccessor ac: accessors) {
            map.put(ac.getName(), ac);
        }

        return map;
    }

    @Override
    public String buildPreparedStatementString(String procName,
            DbParameterAccessor[] accessors) {
        if (!containsBooleanType(accessors)) {
            return super.buildPreparedStatementString(procName, accessors);
        }

        boolean isFunction = accessorUtils.containsReturnValue(accessors);

        if (isFunction) {
            throw new RuntimeException("Boolean PL/SQL functions are unsupported");
        }

        List<String> accessorNames = accessorUtils.getSortedAccessorNames(accessors);
        Map<String, DbParameterAccessor> accessorsMap = getAccessorsMap(accessors);

        StringBuilder sbParams = new StringBuilder();
        String separator = "";
        for (String acName: accessorNames) {
            sbParams.append(separator);

            DbParameterAccessor ac = accessorsMap.get(acName);
            if (isBooleanAccessor(ac)) {
                sbParams.append("chr2bool( ? )");
            } else {
                sbParams.append("?");
            }

            separator = ",";
        }

        return wrapperTemplate
            .replace("${sp_name}", procName)
            .replace("${sp_params}", sbParams.toString());
    }

    private boolean isBooleanAccessor(DbParameterAccessor accessor) {
        return accessor.getTag("ORIGINAL_DB_TYPE").contains("BOOLEAN");
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

