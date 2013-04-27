package dbfit.util.crypto;

import dbfit.util.crypto.CryptoService;

public class CryptoServiceFactory {

    public static CryptoService getCryptoService() {
        return new CryptoService() {
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
}

