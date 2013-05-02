package dbfit.util.crypto;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import org.mockito.InOrder;
import static org.mockito.Mockito.*;


public class CryptoAppTest extends CryptoAppTestBase {

    @Before
    public void setMockedFactories() throws Exception {
        setupMocks();
    }

    @After
    public void tearDown() {
        CryptoFactories.setCryptoServiceFactory(null);
        CryptoFactories.setCryptoKeyStoreFactory(null);
    }

    @Override
    protected CryptoApp createCryptoApp() {
        return new CryptoApp(mockedKSFactory);
    }

    @Test
    public void createKeyStoreInDefaultLocationTest() throws Exception {
        execApp("-createKeyStore");

        verify(mockedKSFactory).newInstance();
        verify(mockedKS).createKeyStore();
    }

    @Test
    public void createKeyStoreInCustomLocationTest() throws Exception {
        execApp("-createKeyStore", getTempKeyStore2Path());

        verify(mockedKSFactory).newInstance(tempKeyStoreFolder2.getRoot());
        verify(mockedKS).createKeyStore();
    }

    @Test
    public void encryptPasswordTest() throws Exception {
        when(mockedKS.keyStoreExists()).thenReturn(true);
        String password = "Demo Password CLI";

        execApp("-encryptPassword", password);

        verify(mockedCryptoService).encrypt(password);
    }

    @Test
    public void shouldCreateKeyStoreBeforeGettingKeyService() throws Exception {
        when(mockedKS.keyStoreExists()).thenReturn(false);
        String password = "Demo Password CLI 2";

        execApp("-encryptPassword", password);

        InOrder inOrder = inOrder(
                mockedKSFactory, mockedKS,
                mockedCryptoServiceFactory, mockedCryptoService);

        inOrder.verify(mockedKSFactory).newInstance();
        inOrder.verify(mockedKS).createKeyStore();
        inOrder.verify(mockedCryptoServiceFactory).getCryptoService();
        inOrder.verify(mockedCryptoService).encrypt(password);
    }

}

