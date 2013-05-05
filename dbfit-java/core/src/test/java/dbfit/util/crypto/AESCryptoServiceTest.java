package dbfit.util.crypto;

import java.security.Key;

import org.junit.Test;
import org.junit.Before;

public class AESCryptoServiceTest {
    private CryptoService cryptoService;

    @Before
    public void prepare() throws Exception {
        cryptoService = new AESCryptoServiceFactory(new CryptoKeyAccessor() {
            private Key key = AESKeyGenerator.generateKey();
            @Override public Key getKey() {
                return key;
            }
        }).getCryptoService();
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

