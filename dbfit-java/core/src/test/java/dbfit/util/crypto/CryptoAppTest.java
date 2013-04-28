package dbfit.util.crypto;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CryptoAppTest {

    @Mock private CryptoService mockedCryptoService;
    @Mock private CryptoKeyStoreManager mockedKSManager;
    @Mock private CryptoKeyStoreManagerFactory mockedKSManagerFactory;
    @Mock private CryptoKeyServiceFactory mockedKeyServiceFactory;
    @Mock private CryptoKeyService mockedKeyService;

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
        when(mockedKeyServiceFactory.getKeyService()).thenReturn(mockedKeyService);

        CryptoAdmin.setKSManagerFactory(mockedKSManagerFactory);
        CryptoAdmin.setCryptoKeyServiceFactory(mockedKeyServiceFactory);
        CryptoServiceFactory.setCryptoService(mockedCryptoService);

        System.setProperty("dbfit.keystore.path", getTempKeyStorePath());
    }

    @After
    public void tearDown() {
        CryptoServiceFactory.setCryptoService(null);
        CryptoAdmin.setKSManagerFactory(null);
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
    public void shouldCreateKeyStoreBeforeGettingKey() throws Exception {
        when(mockedKSManager.keyStoreExists()).thenReturn(false);
        CryptoApp app = createCryptoApp();
        String password = "Demo Password CLI 2";
        String[] args = { "-encryptPassword", password };


        app.execute(args);

        InOrder inOrder = inOrder(mockedKSManagerFactory, mockedKSManager,
                mockedCryptoService, mockedKeyService);

        inOrder.verify(mockedKSManagerFactory).newInstance();
        inOrder.verify(mockedKSManager).initKeyStore();
        inOrder.verify(mockedKeyService).getKey();
        inOrder.verify(mockedCryptoService).encrypt(password);
    }
}

