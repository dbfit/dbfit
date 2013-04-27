package dbfit.util.crypto;

import dbfit.util.crypto.CryptoService;

public class CryptoServiceFactory {

    private static CryptoService cryptoServiceInstance = null;

    public static void setCryptoService(CryptoService svc) {
        cryptoServiceInstance = svc;
    }

    private static void initCryptoService() {
        cryptoServiceInstance = new CryptoService() {
            @Override
            public String encrypt(String msg) {
                return "E" + msg;
            }

            @Override
            public String decrypt(String msg) {
                return msg.substring(1);
            }
        };
    }

    public static CryptoService getCryptoService() {
        if (cryptoServiceInstance == null) {
            initCryptoService();
        }

        return cryptoServiceInstance;
    }
}

