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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CryptoAppTest {

    @Mock private CryptoService mockedCryptoService;
    @Mock private CryptoKeyStoreManager mockedKSManager;
    @Mock private CryptoKeyStoreManagerFactory mockedKSManagerFactory;
    @Rule public TemporaryFolder tempKeyStoreFolder = new TemporaryFolder();
    @Rule public TemporaryFolder tempKeyStoreFolder2 = new TemporaryFolder();

    private String getTempKeyStorePath() throws IOException {
        return tempKeyStoreFolder.getRoot().getCanonicalPath();
    }

    private String getTempKeyStore2Path() throws IOException {
        return tempKeyStoreFolder2.getRoot().getCanonicalPath();
    }

    @Before
    public void prepare() throws IOException {
        when(mockedKSManagerFactory.newInstance()).thenReturn(mockedKSManager);
        when(mockedKSManagerFactory.newInstance(any(File.class))).thenReturn(mockedKSManager);

        CryptoAdmin.setKSManagerFactory(mockedKSManagerFactory);
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
        CryptoApp app = new CryptoApp(mockedKSManagerFactory, mockedCryptoService);
        String[] args = { "-createKeyStore" };

        app.execute(args);

        verify(mockedKSManagerFactory).newInstance();
        verify(mockedKSManager).initKeyStore();
    }

    @Test
    public void createKeyStoreInCustomLocationTest() throws Exception {
        CryptoApp app = new CryptoApp(mockedKSManagerFactory, mockedCryptoService);
        String[] args = { "-createKeyStore", getTempKeyStore2Path() };

        app.execute(args);

        verify(mockedKSManagerFactory).newInstance(tempKeyStoreFolder2.getRoot());
        verify(mockedKSManager).initKeyStore();
    }

    @Test
    public void encryptPasswordTest() throws Exception {
        CryptoApp app = new CryptoApp(mockedKSManagerFactory, mockedCryptoService);
        String password = "Demo Password CLI";
        String[] args = { "-encryptPassword", password };

        app.execute(args);

        verify(mockedCryptoService).encrypt(password);
    }

    @Test
    public void shouldCreateKeyStoreIfOneIsMissingOnEncrypt() throws Exception {
        CryptoApp app = new CryptoApp(mockedKSManagerFactory, mockedCryptoService);
        String password = "Demo Password CLI 2";
        String[] args = { "-encryptPassword", password };

        app.execute(args);

        verify(mockedKSManagerFactory).newInstance();
    }
}

