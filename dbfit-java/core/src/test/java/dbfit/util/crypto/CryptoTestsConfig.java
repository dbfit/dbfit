package dbfit.util.crypto;

import java.security.NoSuchAlgorithmException;
import java.io.File;

public class CryptoTestsConfig {

    private static void initTestCryptoServiceFactory(final File ksPath) {
        CryptoFactories.setCryptoServiceFactory(
            new CryptoServiceFactory() {
                @Override public CryptoService getCryptoService() {
                    return new AESCryptoService(
                        getCryptoKeyAccessor(ksPath).getKey());
            }
        });
    }

    public static JKSCryptoKeyStore getJKSCryptoKeyStore(File ksPath) {
        return new JKSCryptoKeyStore(ksPath);
    }

    public static CryptoKeyAccessor getCryptoKeyAccessor(File ksPath) {
        return getCryptoKeyStore(ksPath);
    }

    public static CryptoKeyStore getCryptoKeyStore(File ksPath) {
        return getJKSCryptoKeyStore(ksPath);
    }

    public static void initTestCryptoKeyStore(File ksPath) throws Exception {
        getJKSCryptoKeyStore(ksPath).createKeyStore();
        initTestCryptoServiceFactory(ksPath);
    }

    public static void resetTestCryptoServiceFactory() {
        CryptoFactories.setCryptoServiceFactory(null);
    }

}

