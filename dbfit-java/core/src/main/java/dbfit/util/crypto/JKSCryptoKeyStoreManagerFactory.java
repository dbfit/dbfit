package dbfit.util.crypto;

import java.io.File;

public class JKSCryptoKeyStoreManagerFactory implements CryptoKeyStoreManagerFactory {

    @Override
    public CryptoKeyStoreManager newInstance() {
        return new JKSCryptoKeyStoreManager();
    }

    @Override
    public CryptoKeyStoreManager newInstance(File keyStorePath) {
        return new JKSCryptoKeyStoreManager(keyStorePath);
    }

    @Override
    public CryptoKeyStoreManager newInstance(File keyStorePath,
                                            char[] ksPassword) {
        return new JKSCryptoKeyStoreManager(keyStorePath, ksPassword);
    }
}

