package dbfit.util.crypto;

import java.io.File;

public class CryptoKeyServiceFactory {

    private static CryptoKeyService keyService = null;
    private static File keyStoreLocation = null;
    private static char[] keyStorePassword = null;

    public static File getKeyStoreLocation() {
        if (keyStoreLocation != null) {
            return keyStoreLocation;
        }

        return CryptoKeyStoreManager.getDefaultKeyStoreLocation();
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


