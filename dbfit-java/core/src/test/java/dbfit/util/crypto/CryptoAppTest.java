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

    @Override
    protected CryptoApp createCryptoApp() {
        return new CryptoApp(mockedKSFactory, mockedCryptoServiceFactory);
    }

    @Test
    public void createKeyStoreInDefaultLocationTest() throws Exception {
        execApp("-encryptPassword", "dummy");

        verify(mockedKSFactory).newInstance();
        verify(mockedKS).createKeyStore();
    }

    @Test
    public void createKeyStoreInCustomLocationTest() throws Exception {
        execApp("-encryptPassword", "dummy", "-keyStoreLocation", getTempKeyStore2Path());

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
        inOrder.verify(mockedCryptoServiceFactory).getCryptoService(any(CryptoKeyAccessor.class));
        inOrder.verify(mockedCryptoService).encrypt(password);
    }

}

