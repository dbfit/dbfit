package dbfit.util.crypto;

import java.io.File;

public interface CryptoKeyStoreFactory {
    public CryptoKeyStore newInstance();
    public CryptoKeyStore newInstance(File rootPath);
}

