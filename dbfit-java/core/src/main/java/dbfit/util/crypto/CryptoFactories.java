package dbfit.util.crypto;

public class CryptoFactories {

    private static CryptoServiceFactory cryptoServiceFactory = null;

    public static void setCryptoServiceFactory(CryptoServiceFactory factory) {
        cryptoServiceFactory = factory;
    }

    public static CryptoKeyStoreFactory getCryptoKeyStoreFactory() {
        return new JKSCryptoKeyStoreFactory();
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

