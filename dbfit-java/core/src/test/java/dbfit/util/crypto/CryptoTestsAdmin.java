package dbfit.util.crypto;

import java.security.NoSuchAlgorithmException;
import java.io.File;

public class CryptoTestsAdmin {

    private static void initTestCryptoServiceFactory(final File ksPath) {
        CryptoAdmin.setCryptoServiceFactory(
            new CryptoServiceFactory() {
                @Override public CryptoService getCryptoService() {
                    return new AESCryptoService(
                        getCryptoKeyAccessor(ksPath).getKey());
            }
        });
    }

    private static JKSCryptoKeyStoreManager getJKStoreManager(File ksPath) {
        return new JKSCryptoKeyStoreManager(ksPath);
    }

    public static CryptoKeyAccessor getCryptoKeyAccessor(File ksPath) {
        return getJKStoreManager(ksPath);
    }

    public static CryptoKeyStoreManager getKsManager(File ksPath) {
        return getJKStoreManager(ksPath);
    }

    public static void initTestCryptoKeyStore(File ksPath) throws Exception {
        getKsManager(ksPath).createKeyStore();
        initTestCryptoServiceFactory(ksPath);
    }

    public static void resetTestCryptoServiceFactory() {
        CryptoAdmin.setCryptoServiceFactory(null);
    }

}

