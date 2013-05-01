package dbfit.util.crypto;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class CryptoServiceTest {
    private CryptoService cryptoService;
    @Rule
    public TemporaryFolder tempKeyStoreFolder = new TemporaryFolder();

    @Before
    public void prepare() throws Exception {
        CryptoTestsAdmin.initTestCryptoKeyStore(tempKeyStoreFolder.getRoot());
        cryptoService = CryptoAdmin.getCryptoService();
    }

    @After
    public void cleanup() throws Exception {
        CryptoTestsAdmin.resetTestCryptoServiceFactory();
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

