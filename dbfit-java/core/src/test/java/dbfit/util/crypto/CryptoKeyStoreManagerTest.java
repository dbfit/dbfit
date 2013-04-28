package dbfit.util.crypto;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.SecretKey;
import java.security.KeyStore;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.matchers.JUnitMatchers.containsString;

public class CryptoKeyStoreManagerTest {

    @Rule public TemporaryFolder tempKeyStoreFolder = new TemporaryFolder();
    @Rule public ExpectedException thrown = ExpectedException.none();

    private CryptoKeyStoreManager ksManager;

    @Before
    public void prepare() {
        ksManager = CryptoAdmin.getKSManagerFactory().newInstance(
                                            tempKeyStoreFolder.getRoot());
    }

    @Test
    public void createKeyStoreTest() throws Exception {
        ksManager.createKeyStore();
    }

    @Test
    public void createKeyStoreOnTopOfExistingOneShouldFail() throws Exception {
        ksManager.createKeyStore();

        String expectedMessage = "Cannot create KeyStore on top of existing one";
        thrown.expect(Exception.class);
        thrown.expectMessage(containsString(expectedMessage));

        ksManager.createKeyStore();
    }

    @Test(expected = java.io.IOException.class)
    public void shouldFailToLoadFromNonExistingKeyStore() throws Exception {
        KeyStore ks = ksManager.loadKeyStore();
    }

    @Test
    public void shouldSucceedToLoadFromExistingKeyStore() throws Exception {
        ksManager.createKeyStore();
        KeyStore ks = ksManager.loadKeyStore();
        assertNotNull(ks);
    }

    @Test
    public void generateKeyTest() throws NoSuchAlgorithmException {
        assertNotNull(ksManager.generateKey());
    }

    @Test
    public void keyStoreExistenceCheck() throws Exception {
        assertFalse(ksManager.keyStoreExists());
        ksManager.createKeyStore();
        assertTrue(ksManager.keyStoreExists());
    }

    @Test
    public void initNonExistingKeyStoreShouldCreateOne() throws Exception {
        assertTrue(ksManager.initKeyStore());
    }

    @Test
    public void initExistingKeyStoreShouldReturnFalse() throws Exception {
        ksManager.createKeyStore();
        assertFalse(ksManager.initKeyStore());
    }

}

