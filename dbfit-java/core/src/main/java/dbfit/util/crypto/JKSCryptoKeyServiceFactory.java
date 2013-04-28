package dbfit.util.crypto;

import java.io.File;

public class JKSCryptoKeyServiceFactory implements CryptoKeyServiceFactory {

    private CryptoKeyService keyService = null;
    private File keyStoreLocation = null;
    private char[] keyStorePassword = null;

    public JKSCryptoKeyServiceFactory(File ksLocation) {
        this(ksLocation, null);
    }

    public JKSCryptoKeyServiceFactory(File ksLocation, char[] password) {
        setKeyStoreLocation(ksLocation, password);
    }

    @Override
    public CryptoKeyService getKeyService() {
        if (null == keyService) {
            keyService = createKeyService(getKeyStoreLocation());
        }

        return keyService;
    }

    private void setKeyStoreLocation(File ksLocation, char[] password) {
        this.keyStoreLocation = ksLocation;
        this.keyStorePassword = password;
    }

    private File getKeyStoreLocation() {
        return keyStoreLocation;
    }

    private CryptoKeyService createKeyService(File keyStorePath) {
        return new JKSCryptoKeyService(keyStoreLocation, keyStorePassword);
    }
}

