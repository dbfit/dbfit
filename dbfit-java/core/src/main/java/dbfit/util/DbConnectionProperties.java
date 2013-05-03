package dbfit.util;

import dbfit.util.crypto.CryptoService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DbConnectionProperties {

    public String Service;
    public String Username;
    public String Password;
    public String DbName;
    public String FullConnectionString;

    private DbConnectionProperties() {
    }

    private static DbConnectionProperties createFrom(Map<String, String> map) {
        DbConnectionProperties props = new DbConnectionProperties();

        for (Map.Entry<String, String> entry: map.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();

            if ("username".equals(key)) {
                props.Username = val;
            } else if ("password".equals(key)) {
                props.Password = val;
            } else if ("service".equals(key)) {
                props.Service = val;
            } else if ("database".equals(key)) {
                props.DbName = val;
            } else if ("connection-string".equals(key)) {
                props.FullConnectionString = val;
            } else {
                throw new UnsupportedOperationException(
                        "Unsupported key in properties file:" + key);
            }
        }

        if (props.FullConnectionString != null) {
            return props;
        }

        if (props.Service != null && props.Username != null
                && props.Password != null) {
            return props;
        }

        throw new Error(
                "You have to define either the full connection string; or service, username and password in the properties file");
    }

    public static DbConnectionProperties CreateFromString(List<String> lines) {
        return CreateFromString(lines, null);
    }

    public static DbConnectionProperties CreateFromString(List<String> lines,
            CryptoService crypto) {
        return createFrom(getLoader(crypto).loadFromList(lines));
    }

    public static DbConnectionProperties CreateFromFile(String path)
            throws FileNotFoundException, IOException {

        return createFrom(getLoader(null).loadFromFile(path));
    }

    private static PropertiesLoader getLoader(CryptoService crypto) {
        if (crypto == null) {
            return new PropertiesLoader();
        } else {
            return new PropertiesLoader(crypto);
        }
    }
}

