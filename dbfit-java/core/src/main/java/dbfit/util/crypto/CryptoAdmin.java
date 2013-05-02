package dbfit.util.crypto;

import java.io.File;

public class CryptoAdmin {

    private static CryptoKeyStoreFactory ksFactory = null;
    private static CryptoServiceFactory cryptoServiceFactory = null;

    public static void setCryptoKeyStoreFactory(CryptoKeyStoreFactory factory) {
        ksFactory = factory;
    }

    public static void setCryptoServiceFactory(CryptoServiceFactory factory) {
        cryptoServiceFactory = factory;
    }

    public static CryptoKeyStoreFactory getCryptoKeyStoreFactory() {
        if (null == ksFactory) {
            return new JKSCryptoKeyStoreFactory();
        }

        return ksFactory;
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

