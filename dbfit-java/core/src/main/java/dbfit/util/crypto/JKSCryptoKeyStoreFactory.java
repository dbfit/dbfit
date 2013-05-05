package dbfit.util.crypto;

import java.io.File;

public class JKSCryptoKeyStoreFactory implements CryptoKeyStoreFactory {

    @Override
    public CryptoKeyStore newInstance() {
        return new JKSCryptoKeyStore();
    }

    @Override
    public CryptoKeyStore newInstance(File keyStorePath) {
        return new JKSCryptoKeyStore(keyStorePath);
    }

}

