package dbfit.util.crypto;

import java.io.File;

public class CryptoAdmin {

    private static CryptoKeyStoreManagerFactory ksManagerFactory = null;
    private static CryptoKeyServiceFactory keyFactory = null;

    public static void setKSManagerFactory(CryptoKeyStoreManagerFactory factory) {
        ksManagerFactory = factory;
    }

    public static CryptoKeyStoreManagerFactory getKSManagerFactory() {
        if (null == ksManagerFactory) {
            return new JKSCryptoKeyStoreManagerFactory();
        }

        return ksManagerFactory;
    }

    public static void setCryptoKeyServiceFactory(CryptoKeyServiceFactory factory) {
        keyFactory = factory;
    }

    public static CryptoKeyServiceFactory getCryptoKeyServiceFactory() {
        if (null == keyFactory) {
            return new JKSCryptoKeyServiceFactory(getDefaultKeyStoreLocation());
        }

        return keyFactory;
    }

    public static File getDefaultKeyStoreLocation() {
        String ksLocation = System.getProperty("dbfit.keystore.path");
        if (ksLocation == null) {
            ksLocation = System.getProperty("user.home");
        }

        return new File(ksLocation);
    }

}

