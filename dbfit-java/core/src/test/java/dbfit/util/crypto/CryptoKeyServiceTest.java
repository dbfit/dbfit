package dbfit.util.crypto;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.assertNotNull;

public class CryptoKeyServiceTest {

    @Rule
    public TemporaryFolder tempKeyStoreFolder = new TemporaryFolder();

    @Before
    public void prepare() throws Exception {
        CryptoServiceTests.initTestCryptoKeyStore(tempKeyStoreFolder.getRoot());
    }

    @After
    public void cleanup() {
        CryptoServiceTests.resetTestKeyServiceFactory();
    }

    @Test
    public void testLoadedKeyIsNotNull() {
        CryptoKeyService keySvc = CryptoKeyServiceFactory.getKeyService();

        assertNotNull("CryptoKeyService initialized to null!", keySvc);
        assertNotNull(keySvc.getKey());
    }
}

