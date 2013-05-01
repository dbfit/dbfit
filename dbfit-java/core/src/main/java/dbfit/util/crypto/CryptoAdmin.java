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
            cryptoServiceFactory = new AESCryptoServiceFactory();
        }

        return cryptoServiceFactory;
    }

    /*** Shortcut Methods ***/
    public static CryptoService getCryptoService() {
        return getCryptoServiceFactory().getCryptoService();
    }

}

