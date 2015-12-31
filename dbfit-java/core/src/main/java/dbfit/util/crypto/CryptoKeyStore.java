package dbfit.util.crypto;

import java.io.File;

public interface CryptoKeyStore extends CryptoKeyAccessor {

    public File getKeyStoreFile();
    public void createKeyStore() throws Exception;
    public boolean keyStoreExists();
}

