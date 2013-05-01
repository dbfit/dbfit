package dbfit.util.crypto;

import java.security.Key;

/**
 * Java KeyStore-based key service
 */
public class JKSCryptoKeyService implements CryptoKeyService {

    private CryptoKeyStoreManager ksManager;
    private Key key = null; // caching the key

    public JKSCryptoKeyService(CryptoKeyStoreManager ksManager) {
        this.ksManager = ksManager;
    }

    @Override
    public Key getKey() {
        if (null == key) {
            key = ksManager.loadKey();
        }

        return key;
    }

}

