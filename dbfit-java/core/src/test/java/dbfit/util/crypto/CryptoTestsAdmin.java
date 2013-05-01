package dbfit.util.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.io.File;

public class CryptoTestsAdmin {

    public static final char[] TEST_KS_PASS = "dbfit-demo-pass".toCharArray();

    private static void initTestCryptoServiceFactory(final File ksPath) {
        CryptoAdmin.setCryptoServiceFactory(
            new CryptoServiceFactory() {
                @Override public CryptoService getCryptoService() {
                    return new AESCryptoService(
                        getCryptoKeyService(ksPath).getKey());
            }
        });
    }

    public static CryptoKeyService getCryptoKeyService(File ksPath) {
        return new JKSCryptoKeyService(getKsManager(ksPath));
    }

    public static CryptoKeyStoreManager getKsManager(File ksPath) {
        return CryptoAdmin.getKSManagerFactory().newInstance(ksPath, TEST_KS_PASS);
    }

    public static void initTestCryptoKeyStore(File ksPath) throws Exception {
        getKsManager(ksPath).createKeyStore();
        initTestCryptoServiceFactory(ksPath);
    }

    public static void resetTestCryptoServiceFactory() {
        CryptoAdmin.setCryptoServiceFactory(null);
    }

}

