package dbfit.util.crypto;

import java.io.File;
import java.io.IOException;
import static java.util.Arrays.asList;
import static dbfit.util.LangUtils.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Rule;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mock;
import static org.mockito.Mockito.*;
import dbfit.util.MockitoTestBase;


public class CryptoAppTestBase extends MockitoTestBase {
    @Mock protected CryptoService mockedCryptoService;
    @Mock protected CryptoKeyStore mockedKS;
    @Mock protected CryptoKeyStoreFactory mockedKSFactory;
    @Mock protected CryptoServiceFactory mockedCryptoServiceFactory;

    @Rule public TemporaryFolder tempKeyStoreFolder = new TemporaryFolder();
    @Rule public TemporaryFolder tempKeyStoreFolder2 = new TemporaryFolder();
    @ClassRule public static TemporaryFolder existingFakeKSFolder = new TemporaryFolder();

    @BeforeClass
    public static void createFakeKSFile() throws IOException {
        existingFakeKSFolder.newFile(JKSCryptoKeyStore.KS_NAME);
    }

    @Before
    public void prepare() throws IOException {
        when(mockedKSFactory.newInstance()).thenReturn(mockedKS);
        when(mockedKSFactory.newInstance(any(File.class))).thenReturn(mockedKS);
        when(mockedCryptoServiceFactory.getCryptoService()).thenReturn(mockedCryptoService);

        CryptoAdmin.setCryptoKeyStoreFactory(mockedKSFactory);
        CryptoAdmin.setCryptoServiceFactory(mockedCryptoServiceFactory);

        System.setProperty("dbfit.keystore.path", getTempKeyStorePath());
    }

    @After
    public void tearDown() {
        CryptoAdmin.setCryptoServiceFactory(null);
        CryptoAdmin.setCryptoKeyStoreFactory(null);
    }

    protected int execApp(String... args) throws Exception {
        return createCryptoApp().execute(args);
    }

    protected int execApp(ArgList argList) throws Exception {
        return createCryptoApp().execute(argList.args);
    }

    protected String getTempKeyStorePath() throws IOException {
        return tempKeyStoreFolder.getRoot().getCanonicalPath();
    }

    protected String getTempKeyStore2Path() throws IOException {
        return tempKeyStoreFolder2.getRoot().getCanonicalPath();
    }

    protected CryptoApp createCryptoApp() {
        return new CryptoApp(mockedKSFactory);
    }

    protected static class ArgList {
        String[] args;
        public ArgList(String[] args) { this.args = args; }
        @Override public String toString() {
            return join(asList(args), " ");
        }
    }

    protected static ArgList args(String... params) {
        return new ArgList(params);
    }

}

