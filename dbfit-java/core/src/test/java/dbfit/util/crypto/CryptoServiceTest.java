package dbfit.util.crypto;

import dbfit.util.crypto.CryptoService;
import dbfit.util.crypto.CryptoServiceFactory;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

import static org.hamcrest.core.IsNot.not;
import static org.junit.matchers.JUnitMatchers.*;

public class CryptoServiceTest {
    private CryptoService cryptoService;

    @Before
    public void prepare() {
        cryptoService = CryptoServiceFactory.getCryptoService();
    }

    @Test
    public void encryptedPasswordShouldNotContainOriginalOne() {
        String pwd = "My Test Password";
        String encryptedPwd = cryptoService.encrypt(pwd);
        assertThat(encryptedPwd, not(containsString(pwd)));
    }

    @Test
    public void encryptionShouldBeReversable() {
        String pwd = "My Secret Password";
        String encryptedPwd = cryptoService.encrypt(pwd);
        String decryptedPwd = cryptoService.decrypt(encryptedPwd);

        assertEquals(decryptedPwd, pwd);
    }
}

