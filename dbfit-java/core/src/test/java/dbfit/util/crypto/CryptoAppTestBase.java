package dbfit.util.crypto;

import static dbfit.util.crypto.JKSCryptoKeyStore.KS_NAME;

import java.io.File;
import java.io.IOException;
import static java.util.Arrays.asList;
import static dbfit.util.LangUtils.*;

import org.junit.Rule;
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

    protected void setupMocks() throws IOException {
        when(mockedKSFactory.newInstance()).thenReturn(mockedKS);
        when(mockedKSFactory.newInstance(any(File.class))).thenReturn(mockedKS);
        when(mockedCryptoServiceFactory.getCryptoService()).thenReturn(mockedCryptoService);
        when(mockedCryptoServiceFactory.getCryptoService(any(CryptoKeyAccessor.class))).thenReturn(mockedCryptoService);
    }

    protected int execApp(String... args) throws Exception {
        return createCryptoApp().execute(args);
    }

    protected int execApp(ArgList argList) throws Exception {
        return createCryptoApp().execute(argList.args);
    }

    protected String getTempKeyStorePath() throws IOException {
        return tempKeyStoreFolder.getRoot().getPath();
    }

    protected String getTempKeyStore2Path() throws IOException {
        return tempKeyStoreFolder2.getRoot().getPath();
    }

    // Create a real (non-mocked app)
    protected CryptoApp createCryptoApp() {
        return new CryptoApp();
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

    // Used to init before @Parameters method
    protected static TemporaryFolder initStaticTemp(boolean createFile) {
        try {
            TemporaryFolder tmp = new TemporaryFolder() { { before(); } };
            if (createFile) {
                tmp.newFile(KS_NAME);
            }
            return tmp;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}

