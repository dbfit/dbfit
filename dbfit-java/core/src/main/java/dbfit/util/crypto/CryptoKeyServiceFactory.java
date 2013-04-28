package dbfit.util.crypto;

import java.security.Key;
import java.io.File;

public class CryptoKeyServiceFactory {

    private static CryptoKeyService keyService = null;
    private static File keyStoreLocation = null;
    private static char[] keyStorePassword = null;

    private static File getKeyStoreLocationFromProperties() {
        String ksLocation = System.getProperty("dbfit.keystore.path");
        if (ksLocation == null) {
            ksLocation = System.getProperty("user.home");
        }

        return new File(ksLocation);
    }

    public static File getKeyStoreLocation() {
        if (keyStoreLocation != null) {
            return keyStoreLocation;
        }

        return getKeyStoreLocationFromProperties();
    }

    public static void setKeyStoreLocation(File ksLocation, char[] password) {
        keyStoreLocation = ksLocation;
        keyStorePassword = password;
    }

    public static void setKeyStoreLocation(File ksLocation) {
        setKeyStoreLocation(ksLocation, null);
    }

    public static void setKeyService(CryptoKeyService keySvc) {
        keyService = keySvc;
    }

    public static CryptoKeyService getKeyService() {
        if (null == keyService) {
            keyService = createKeyService(getKeyStoreLocation());
        }

        return keyService;
    }

    public static CryptoKeyService createKeyService(File keyStorePath) {
        return new JKSCryptoKeyService(getKeyStoreLocation(), keyStorePassword);
    }
}


