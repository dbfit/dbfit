package dbfit.util.crypto;

import java.io.File;

public interface CryptoKeyStoreManagerFactory {

    public CryptoKeyStoreManager newInstance();
    public CryptoKeyStoreManager newInstance(File keyStorePath);
}

