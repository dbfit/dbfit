package dbfit.util.crypto;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class CryptoServiceTest {
    private CryptoService cryptoService;
    @Rule
    public TemporaryFolder tempKeyStoreFolder = new TemporaryFolder();

    @Before
    public void prepare() throws Exception {
        final java.io.File ksRoot = tempKeyStoreFolder.getRoot();
        CryptoTestsConfig.createTestKeyStore(ksRoot);
        cryptoService = CryptoTestsConfig.getCryptoService(ksRoot);
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

