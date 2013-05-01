package dbfit.util.crypto;

import java.security.NoSuchAlgorithmException;
import java.security.Key;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

import static org.hamcrest.core.IsNot.not;
import static org.junit.matchers.JUnitMatchers.*;

import java.io.File;

public class CryptoServiceTests {

    public static final char[] TEST_KS_PASS = "dbfit-demo-pass".toCharArray();

    private static void initTestCryptoKeyServiceFactory(final File ksPath) {
        CryptoAdmin.setCryptoKeyServiceFactory(
            new CryptoKeyServiceFactory() {
                @Override public CryptoKeyService getKeyService() {
                    return new JKSCryptoKeyService(getKsManager(ksPath));
                }
            });
    }

    private static JKSCryptoKeyStoreManager getKsManager(File ksPath) {
        return (JKSCryptoKeyStoreManager) CryptoAdmin.getKSManagerFactory()
                                        .newInstance(ksPath, TEST_KS_PASS);
    }

    public static void initTestCryptoKeyStore(File ksPath) throws Exception {
        getKsManager(ksPath).createKeyStore();
        initTestCryptoKeyServiceFactory(ksPath);
    }

    public static void resetTestKeyServiceFactory() {
        CryptoAdmin.setCryptoKeyServiceFactory(null);
    }

    public static void encryptedPasswordShouldNotContainOriginalOne(
                                CryptoService cryptoService) {
        String pwd = "My Test Password";
        String encryptedPwd = cryptoService.encrypt(pwd);
        assertThat(encryptedPwd, not(containsString(pwd)));
    }

    public static void encryptionShouldBeReversable(
                                CryptoService cryptoService) {
        String pwd = "My Secret Password";
        String encryptedPwd = cryptoService.encrypt(pwd);
        String decryptedPwd = cryptoService.decrypt(encryptedPwd);

        assertEquals(decryptedPwd, pwd);
    }
}

