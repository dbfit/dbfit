package dbfit.util.crypto;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Key;

import org.junit.Test;
import org.junit.Before;

public class AESCryptoServiceTest {
    private CryptoService cryptoService;

    @Before
    public void prepare() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        Key key = kgen.generateKey();

        cryptoService = AESCryptoServiceFactory.createAESCryptoService(key);
    }

    @Test
    public void encryptedPasswordShouldNotContainOriginalOne() {
        CryptoServiceTests.encryptedPasswordShouldNotContainOriginalOne(
                cryptoService);
    }

    @Test
    public void encryptionShouldBeReversable() {
        CryptoServiceTests.encryptionShouldBeReversable(cryptoService);
    }
}

