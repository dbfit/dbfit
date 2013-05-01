package dbfit.util.crypto;

import java.security.Key;

public class AESCryptoServiceFactory implements CryptoServiceFactory {

    private static CryptoService cryptoServiceInstance = null; // caching
    private CryptoKeyAccessor keyAccessor;

    public AESCryptoServiceFactory(CryptoKeyAccessor keyAccessor) {
        this.keyAccessor = keyAccessor;
    }

    public AESCryptoServiceFactory() {
        this.keyAccessor = new JKSCryptoKeyStoreManager();
    }

    @Override
    public CryptoService getCryptoService() {
        if (cryptoServiceInstance == null) {
            cryptoServiceInstance = new AESCryptoService(keyAccessor.getKey());
        }

        return cryptoServiceInstance;
    }

}

