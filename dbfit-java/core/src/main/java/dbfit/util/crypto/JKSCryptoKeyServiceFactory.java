package dbfit.util.crypto;

import java.io.File;

public class JKSCryptoKeyServiceFactory implements CryptoKeyServiceFactory {

    private CryptoKeyStoreManager ksManager = null;
    private CryptoKeyService keyService = null; // caching instance

    public JKSCryptoKeyServiceFactory(CryptoKeyStoreManager ksManager) {
        this.ksManager = ksManager;
    }

    @Override
    public CryptoKeyService getKeyService() {
        if (null == keyService) {
            keyService = new JKSCryptoKeyService(ksManager);
        }

        return keyService;
    }
}

