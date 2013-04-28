package dbfit.util.crypto;

import static dbfit.util.crypto.CryptoKeyStoreManager.KEY_ALIAS;
import static dbfit.util.crypto.CryptoKeyStoreManager.KS_PASS;

import java.security.KeyStore;
import java.security.Key;
import java.io.File;
import java.io.FileInputStream;

/**
 * Java KeyStore-based key service
 */
public class JKSCryptoKeyService implements CryptoKeyService {

    private KeyStore keyStore;
    private char[] password;

    public JKSCryptoKeyService(File keyStorePath) {
        this(keyStorePath, KS_PASS); 
    }

    public JKSCryptoKeyService(File keyStorePath, char[] password) {
        this.password = password;
        this.keyStore = loadKeyStore(keyStorePath, password);
    }

    @Override
    public Key getKey() {
        try {
            return keyStore.getKey(KEY_ALIAS, password);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static KeyStore loadKeyStore(File location, char[] password) {
        try {
            return new CryptoKeyStoreManager(location, password).loadKeyStore();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

