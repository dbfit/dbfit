package dbfit.util.crypto;

import java.io.File;

public class CryptoTestsConfig {

    public static JKSCryptoKeyStore getJKSCryptoKeyStore(File ksPath) {
        return new JKSCryptoKeyStore(ksPath);
    }

    public static CryptoKeyAccessor getCryptoKeyAccessor(File ksPath) {
        return getCryptoKeyStore(ksPath);
    }

    public static CryptoKeyStore getCryptoKeyStore(File ksPath) {
        return getJKSCryptoKeyStore(ksPath);
    }

    public static CryptoServiceFactory getCryptoServiceFactory(File ksPath) {
        return new AESCryptoServiceFactory(getCryptoKeyAccessor(ksPath));
    }

    public static CryptoService getCryptoService(File ksPath) {
        return getCryptoServiceFactory(ksPath).getCryptoService();
    }

    public static CryptoKeyStore createTestKeyStore(File ksPath) throws Exception {
        CryptoKeyStore ks = getCryptoKeyStore(ksPath);
        ks.createKeyStore();
        return ks;
    }

    public static CryptoService getFakeCryptoService() {
        return new CryptoService() {
            @Override public String encrypt(String msg) {
                return "XE-" + msg;
            }

            @Override public String decrypt(String msg) {
                return msg.substring(3);
            }
        };
    }
}

