package dbfit.api;

import dbfit.api.DBEnvironment;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;

public class DbEnvironmentFactory {
    private void initDefaultEnvironments() {
        registerEnv("Oracle", "oracle.jdbc.OracleDriver");
        registerEnv("Teradata", "com.teradata.jdbc.TeraDriver");
        registerEnv("MySql", "com.mysql.jdbc.Driver");
        registerEnv("SqlServer", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        registerEnv("DB2", "com.ibm.db2.jcc.DB2Driver");
        registerEnv("Derby", "org.apache.derby.jdbc.ClientDriver");
        registerEnv("EmbeddedDerby", "org.apache.derby.jdbc.EmbeddedDriver");
        registerEnv("Postgres", "org.postgresql.Driver");
        registerEnv("HSQLDB", "org.hsqldb.jdbcDriver");
    }

    private static DBEnvironment environment;

    public static DBEnvironment getDefaultEnvironment(){
        return environment;
    }

    public static void setDefaultEnvironment(DBEnvironment newDefaultEnvironment){
        environment=newDefaultEnvironment;
    }

    public static DbEnvironmentFactory newFactoryInstance() {
        DbEnvironmentFactory factory = new DbEnvironmentFactory();
        factory.initDefaultEnvironments();

        return factory;
    } 

    public static class EnvironmentDescriptor {
        public String environmentName;
        public String driverClassName;

        public String getEnvironmentClassName() {
            return "dbfit.environment." + environmentName + "Environment";
        }

        public EnvironmentDescriptor(String environmentName, String driverClassName) {
            this.environmentName = environmentName;
            this.driverClassName = driverClassName;
        }
    }

    private Map<String, EnvironmentDescriptor> environments =
        new HashMap<String, EnvironmentDescriptor>();

    private DbEnvironmentFactory() {
    }

    private static String normalise(String environmentName) {
        return environmentName.trim().toUpperCase();
    }

    public void registerEnv(String environmentName, String driverClassName) {
        environments.put(normalise(environmentName),
                new EnvironmentDescriptor(environmentName, driverClassName));
    }

    public EnvironmentDescriptor unregisterEnv(String environmentName) {
        return environments.remove(normalise(environmentName));
    }

    private EnvironmentDescriptor getEnvironmentDescriptor(String requestedEnv) {
        return environments.get(normalise(requestedEnv));
    }

    private DBEnvironment createEnvironmentInstance(
                EnvironmentDescriptor descriptor) {
        try {
            Class<?> envClass = Class.forName(descriptor.getEnvironmentClassName());
            Constructor ctor = envClass.getConstructor();
            DBEnvironment oe = (DBEnvironment) ctor.newInstance();
            return oe;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public DBEnvironment createEnvironmentInstance(String requestedEnv) {
        EnvironmentDescriptor descriptor = getEnvironmentDescriptor(requestedEnv);
        if (null == descriptor) {
            throw new IllegalArgumentException("DB Environment not supported:" + requestedEnv);
        }

        return createEnvironmentInstance(descriptor);
    }

    public static DBEnvironment newEnvironmentInstance(String requestedEnv) {
        return newFactoryInstance().createEnvironmentInstance(requestedEnv);
    }
}

