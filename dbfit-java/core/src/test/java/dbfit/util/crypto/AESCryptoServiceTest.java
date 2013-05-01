package dbfit.util.crypto;

import org.junit.Test;
import org.junit.Before;

public class AESCryptoServiceTest {
    private CryptoService cryptoService;

    @Before
    public void prepare() throws Exception {
        cryptoService = new AESCryptoService(AESKeyGenerator.generateKey());
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

