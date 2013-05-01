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

