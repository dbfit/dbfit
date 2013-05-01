package dbfit.util.crypto;

import java.security.Key;

/**
 * KeyStore-based key service
 */
public class KSCryptoKeyService implements CryptoKeyService {

    private CryptoKeyStoreManager ksManager;
    private Key key = null; // caching the key

    public KSCryptoKeyService(CryptoKeyStoreManager ksManager) {
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

