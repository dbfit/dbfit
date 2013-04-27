package dbfit.util.crypto;

import dbfit.util.crypto.CryptoService;

public class CryptoServiceFactory {

    private static CryptoService CRYPTO_SERVICE_INSTANCE = null;

    public static void setCryptoService(CryptoService svc) {
        CRYPTO_SERVICE_INSTANCE = svc;
    }

    private static void initCryptoService() {
        CRYPTO_SERVICE_INSTANCE = new CryptoService() {
            @Override
            public String encrypt(String msg) {
                return msg;
            }

            @Override
            public String decrypt(String msg) {
                return msg;
            }
        };
    }

    public static CryptoService getCryptoService() {
        if (CRYPTO_SERVICE_INSTANCE == null) {
            initCryptoService();
        }

        return CRYPTO_SERVICE_INSTANCE;
    }
}

