package dbfit.util.crypto;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.hamcrest.CoreMatchers.containsString;


public class CryptoKeyStoreTest {

    @Rule public TemporaryFolder tempKeyStoreFolder = new TemporaryFolder();
    @Rule public ExpectedException thrown = ExpectedException.none();

    private JKSCryptoKeyStore ks;

    @Before
    public void prepare() {
        ks = new JKSCryptoKeyStore(tempKeyStoreFolder.getRoot());
    }

    @Test
    public void createKeyStoreTest() throws Exception {
        ks.createKeyStore();
    }

    @Test
    public void createKeyStoreOnTopOfExistingOneShouldFail() throws Exception {
        ks.createKeyStore();

        String expectedMessage = "Cannot create KeyStore on top of existing one";
        thrown.expect(Exception.class);
        thrown.expectMessage(containsString(expectedMessage));

        ks.createKeyStore();
    }

    @Test(expected = java.io.IOException.class)
    public void shouldFailToLoadFromNonExistingKeyStore() throws Exception {
        ks.loadKeyStore();
    }

    @Test
    public void shouldSucceedToLoadFromExistingKeyStore() throws Exception {
        ks.createKeyStore();
        assertNotNull(ks.loadKeyStore());
    }

    @Test
    public void shouldBeAbleToGetKeyFromExistingKeyStore() throws Exception {
        ks.createKeyStore();
        assertNotNull(ks.getKey());
    }

    @Test
    public void keyStoreExistenceCheck() throws Exception {
        assertFalse(ks.keyStoreExists());
        ks.createKeyStore();
        assertTrue(ks.keyStoreExists());
    }

    @Test
    public void createInEmptyLocationShouldSucceed() throws Exception {
        ks.createKeyStore();
    }

    @Test(expected = CryptoKeyStoreException.class)
    public void createOnTopOfExistingKSShouldFail() throws Exception {
        ks.createKeyStore();
        ks.createKeyStore();
    }

}

