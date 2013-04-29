package dbfit.util.crypto;

import dbfit.util.crypto.CryptoKeyService;
import dbfit.util.crypto.CryptoService;

import java.security.Key;

public class AESCryptoServiceFactory implements CryptoServiceFactory {

    private static CryptoService cryptoServiceInstance = null;
    private static CryptoKeyService keyService = null; // for overwriting default

    public AESCryptoServiceFactory(CryptoKeyService keyService) {
        this.keyService = keyService;
    }

    @Override
    public CryptoService getCryptoService() {
        if (cryptoServiceInstance == null) {
            initCryptoService();
        }

        return cryptoServiceInstance;
    }

    public static AESCryptoService createAESCryptoService(Key key) {
        return new AESCryptoService(key);
    }

    private static void initCryptoService() {
        Key key = keyService.getKey();
        cryptoServiceInstance = createAESCryptoService(key);
    }

}

