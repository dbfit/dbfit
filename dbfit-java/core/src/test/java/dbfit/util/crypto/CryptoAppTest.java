package dbfit.util.crypto;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CryptoAppTest {

    @Mock private CryptoService mockedCryptoService;
    @Mock private CryptoKeyStoreManager mockedKSManager;
    @Mock private CryptoKeyStoreManagerFactory mockedKSManagerFactory;
    @Mock private CryptoServiceFactory mockedCryptoServiceFactory;

    @Rule public TemporaryFolder tempKeyStoreFolder = new TemporaryFolder();
    @Rule public TemporaryFolder tempKeyStoreFolder2 = new TemporaryFolder();

    private String getTempKeyStorePath() throws IOException {
        return tempKeyStoreFolder.getRoot().getCanonicalPath();
    }

    private String getTempKeyStore2Path() throws IOException {
        return tempKeyStoreFolder2.getRoot().getCanonicalPath();
    }

    private CryptoApp createCryptoApp() {
        return new CryptoApp(mockedKSManagerFactory);
    }

    @Before
    public void prepare() throws IOException {
        when(mockedKSManagerFactory.newInstance()).thenReturn(mockedKSManager);
        when(mockedKSManagerFactory.newInstance(any(File.class))).thenReturn(mockedKSManager);
        when(mockedCryptoServiceFactory.getCryptoService()).thenReturn(mockedCryptoService);

        CryptoAdmin.setKSManagerFactory(mockedKSManagerFactory);
        CryptoAdmin.setCryptoServiceFactory(mockedCryptoServiceFactory);

        System.setProperty("dbfit.keystore.path", getTempKeyStorePath());
    }

    @After
    public void tearDown() {
        CryptoAdmin.setCryptoServiceFactory(null);
        CryptoAdmin.setKSManagerFactory(null);
        CryptoAdmin.setCryptoKeyServiceFactory(null);
    }

    @Test
    public void createKeyStoreInDefaultLocationTest() throws Exception {
        CryptoApp app = createCryptoApp();
        String[] args = { "-createKeyStore" };

        app.execute(args);

        verify(mockedKSManagerFactory).newInstance();
        verify(mockedKSManager).initKeyStore();
    }

    @Test
    public void createKeyStoreInCustomLocationTest() throws Exception {
        CryptoApp app = createCryptoApp();
        String[] args = { "-createKeyStore", getTempKeyStore2Path() };

        app.execute(args);

        verify(mockedKSManagerFactory).newInstance(tempKeyStoreFolder2.getRoot());
        verify(mockedKSManager).initKeyStore();
    }

    @Test
    public void encryptPasswordTest() throws Exception {
        when(mockedKSManager.keyStoreExists()).thenReturn(true);
        CryptoApp app = createCryptoApp();
        String password = "Demo Password CLI";
        String[] args = { "-encryptPassword", password };

        app.execute(args);

        verify(mockedCryptoService).encrypt(password);
    }

    @Test
    public void shouldCreateKeyStoreBeforeGettingKeyService() throws Exception {
        when(mockedKSManager.keyStoreExists()).thenReturn(false);
        CryptoApp app = createCryptoApp();
        String password = "Demo Password CLI 2";
        String[] args = { "-encryptPassword", password };

        app.execute(args);

        InOrder inOrder = inOrder(
                mockedKSManagerFactory, mockedKSManager,
                mockedCryptoServiceFactory, mockedCryptoService);

        inOrder.verify(mockedKSManagerFactory).newInstance();
        inOrder.verify(mockedKSManager).initKeyStore();
        inOrder.verify(mockedCryptoServiceFactory).getCryptoService();
        inOrder.verify(mockedCryptoService).encrypt(password);
    }

    @Test
    public void shouldReturnNonZeroOnEmptyArgs() throws Exception {
        assertEquals(1, execApp(new String[] {}));
    }

    @Test
    public void shouldReturnOneOnInvalidCommand() throws Exception {
        assertEquals(1, execApp(new String[] {"-non-existing-command"}));
        assertEquals(1, execApp(new String[] {"another invalid command"}));
    }

    @Test
    public void shouldReturnTwoOnInvalidNumberOfOptions() throws Exception {
        assertEquals(2, execApp(new String[] {"-encryptPassword", "too", "many", "args"}));
        assertEquals(2, execApp(new String[] {"-createKeyStore", "too", "many"}));
    }

    private int execApp(String[] args) throws Exception {
        return createCryptoApp().execute(args);
    }
}

