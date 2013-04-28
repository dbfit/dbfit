package dbfit.util.crypto;

import static dbfit.util.crypto.JKSCryptoKeyService.KEY_ALIAS;
import static dbfit.util.crypto.JKSCryptoKeyService.KS_TYPE;
import static dbfit.util.crypto.JKSCryptoKeyService.KS_NAME;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.SecretKey;
import java.security.KeyStore;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

import static org.hamcrest.core.IsNot.not;
import static org.junit.matchers.JUnitMatchers.*;

import java.io.File;
import java.io.FileOutputStream;

public class CryptoServiceTests {

    public static final char[] TEST_KS_PASS = "dbfit-demo-pass".toCharArray();

    public static void initTestCryptoKeyStore(File ksPath) throws Exception {
        createKeyStore(ksPath);
        CryptoKeyServiceFactory.setKeyStoreLocation(ksPath, TEST_KS_PASS);
        CryptoKeyServiceFactory.setKeyService(null);
    }

    public static void resetTestKeyServiceFactory() {
        CryptoKeyServiceFactory.setKeyStoreLocation(null);
        CryptoKeyServiceFactory.setKeyService(null);
    }

    private static void createKeyStore(File path) throws Exception {
        KeyStore ks = KeyStore.getInstance(KS_TYPE);
        ks.load(null, TEST_KS_PASS);
        SecretKey mySecretKey = (SecretKey) generateKey();

        FileOutputStream fos = null;

        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(mySecretKey);
        ks.setEntry(KEY_ALIAS, skEntry, new KeyStore.PasswordProtection(
                                                    TEST_KS_PASS));

        try {
            File ksFile = new File(path, KS_NAME);
            fos = new java.io.FileOutputStream(ksFile);
            ks.store(fos, TEST_KS_PASS);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
 
    }

    public static Key generateKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        return kgen.generateKey();
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

