package dbfit.environment;

import java.util.Map;
import java.util.regex.Pattern;

import java.sql.SQLException;
import java.util.HashMap;

import dbfit.annotations.DatabaseEnvironment;
import dbfit.api.AbstractDbEnvironment;
import dbfit.util.DbParameterAccessor;

@DatabaseEnvironment(name="Dummy", driver="dbfit.environment.DummyDriver")
public class DummyEnvironment extends AbstractDbEnvironment {

    public DummyEnvironment(String driverClassName) {
        super("dbfit.environment.DummyDriver");
    }

    @Override
    public String getConnectionString(String dataSource) {
        return "jdbc:dummy";
    }

    @Override
    protected String getConnectionString(String dataSource, String database) {
        return "jdbc:dummy";
    }

    @Override
    public Map<String, DbParameterAccessor> getAllColumns(final String tableOrViewName) {
        return new HashMap<String, DbParameterAccessor>();
    }

    @Override
    public Map<String, DbParameterAccessor> getAllProcedureParameters(String procName) throws SQLException {
        return null;
    }

    @Override
    public Class<?> getJavaClass(String dataType) {
        return String.class;
    }

    @Override
    public Pattern getParameterPattern() {
        return Pattern.compile("");
    }
}
