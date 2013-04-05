package dbfit.api;

import dbfit.api.DBEnvironment;
import dbfit.annotations.DatabaseEnvironment;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;
import org.atteo.evo.classindex.ClassIndex;

public class DbEnvironmentFactory {
    private void initDefaultEnvironments() {
        for (Class<?> c: ClassIndex.getAnnotated(DatabaseEnvironment.class)) {
            DatabaseEnvironment envAnnotation =
                c.getAnnotation(DatabaseEnvironment.class);
            registerEnv(envAnnotation.name(), envAnnotation.driver());
        }
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

        private void checkDriver() {
            try {
                Class.forName(driverClassName);
            } catch (Exception e) {
                throw new Error("Cannot load " + environmentName
                        + " database driver " + driverClassName + ". Is the JDBC driver on the classpath?", e);
            }
        }

        public String getEnvironmentClassName() {
            return "dbfit.environment." + environmentName + "Environment";
        }

        public EnvironmentDescriptor(String environmentName, String driverClassName) {
            this.environmentName = environmentName;
            this.driverClassName = driverClassName;
        }

        public DBEnvironment createEnvironmentInstance() {
            checkDriver();

            try {
                Class<?> envClass = Class.forName(getEnvironmentClassName());
                Constructor ctor = envClass.getConstructor(String.class);
                DBEnvironment oe = (DBEnvironment) ctor.newInstance(driverClassName);
                return oe;
            } catch (Exception e) {
                throw new Error(e);
            }
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

    public DBEnvironment createEnvironmentInstance(String requestedEnv) {
        EnvironmentDescriptor descriptor = getEnvironmentDescriptor(requestedEnv);
        if (null == descriptor) {
            throw new IllegalArgumentException("DB Environment not supported:" + requestedEnv);
        }

        return descriptor.createEnvironmentInstance();
    }

    public static DBEnvironment newEnvironmentInstance(String requestedEnv) {
        return newFactoryInstance().createEnvironmentInstance(requestedEnv);
    }
}

