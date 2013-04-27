package dbfit.util.crypto;

import dbfit.util.crypto.CryptoService;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

import static org.hamcrest.core.IsNot.not;
import static org.junit.matchers.JUnitMatchers.*;

public class CryptoServiceTests {

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

