package dbfit.util.crypto;

public class CryptoKeyStoreException extends Exception {
    /**
     * generated ID
     */
    private static final long serialVersionUID = 649497271988490541L;
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
