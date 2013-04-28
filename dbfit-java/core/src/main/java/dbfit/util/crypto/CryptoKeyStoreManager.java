package dbfit.util.crypto;

import java.security.KeyStore;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public interface CryptoKeyStoreManager {
    public KeyStore loadKeyStore() throws Exception;
    public void createKeyStore() throws Exception;
    public boolean initKeyStore() throws Exception;
    public boolean keyStoreExists();
    public Key generateKey() throws NoSuchAlgorithmException;
}

