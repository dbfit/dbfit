package dbfit.util.crypto;

import java.io.File;

public class CryptoAdmin {

    private static CryptoKeyStoreManagerFactory ksManagerFactory = null;
    private static CryptoServiceFactory cryptoServiceFactory = null;

    public static void setKSManagerFactory(CryptoKeyStoreManagerFactory factory) {
        ksManagerFactory = factory;
    }

    public static void setCryptoServiceFactory(CryptoServiceFactory factory) {
        cryptoServiceFactory = factory;
    }

    public static CryptoKeyStoreManagerFactory getKSManagerFactory() {
        if (null == ksManagerFactory) {
            return new JKSCryptoKeyStoreManagerFactory();
        }

        return ksManagerFactory;
    }

    public static CryptoServiceFactory getCryptoServiceFactory() {
        if (null == cryptoServiceFactory) {
            CryptoKeyStoreManager ksMgr = getKSManagerFactory().newInstance(getDefaultKeyStoreLocation());
            cryptoServiceFactory = new AESCryptoServiceFactory(new JKSCryptoKeyService(ksMgr));
        }

        return cryptoServiceFactory;
    }

    public static File getDefaultKeyStoreLocation() {
        String ksLocation = System.getProperty("dbfit.keystore.path");
        if (ksLocation == null) {
            ksLocation = System.getProperty("user.home");
        }

        return new File(ksLocation);
    }

    /*** Shortcut Methods ***/
    public static CryptoService getCryptoService() {
        return getCryptoServiceFactory().getCryptoService();
    }

}

