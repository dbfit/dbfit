package dbfit.util.crypto;

import java.security.KeyStore;
import java.security.Key;
import java.io.File;

public interface CryptoKeyStoreManager {
    public void createKeyStore() throws Exception;
    public boolean initKeyStore() throws Exception;
    public boolean keyStoreExists();
    public File getKeyStoreFile();
}

