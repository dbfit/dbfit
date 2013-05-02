package dbfit.util.crypto;

public class CryptoKeyStoreException extends Exception {
    private final CryptoKeyStore keyStore;

    public CryptoKeyStoreException(CryptoKeyStore keyStore, String message) {
        super(message);
        this.keyStore = keyStore;
    }

    public CryptoKeyStoreException(CryptoKeyStore keyStore, String message, Throwable cause) {
        super(message, cause);
        this.keyStore = keyStore;
    }

}

