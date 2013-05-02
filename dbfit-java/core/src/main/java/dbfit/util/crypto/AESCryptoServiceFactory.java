package dbfit.util.crypto;

public class AESCryptoServiceFactory implements CryptoServiceFactory {

    private static CryptoService cryptoServiceInstance = null; // caching
    private CryptoKeyAccessor keyAccessor;

    public AESCryptoServiceFactory(CryptoKeyAccessor keyAccessor) {
        this.keyAccessor = keyAccessor;
    }

    public AESCryptoServiceFactory() {
        this(CryptoFactories.getCryptoKeyStoreFactory().newInstance());
    }

    @Override
    public CryptoService getCryptoService() {
        if (cryptoServiceInstance == null) {
            cryptoServiceInstance = new AESCryptoService(keyAccessor);
        }

        return cryptoServiceInstance;
    }

}

