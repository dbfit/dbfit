package dbfit.util.crypto;

import org.junit.Test;

import org.mockito.InOrder;
import static org.mockito.Mockito.*;


public class CryptoAppTest extends CryptoAppTestBase {
    @Test
    public void createKeyStoreInDefaultLocationTest() throws Exception {
        execApp("-createKeyStore");

        verify(mockedKSManagerFactory).newInstance();
        verify(mockedKSManager).initKeyStore();
    }

    @Test
    public void createKeyStoreInCustomLocationTest() throws Exception {
        execApp("-createKeyStore", getTempKeyStore2Path());

        verify(mockedKSManagerFactory).newInstance(tempKeyStoreFolder2.getRoot());
        verify(mockedKSManager).initKeyStore();
    }

    @Test
    public void encryptPasswordTest() throws Exception {
        when(mockedKSManager.keyStoreExists()).thenReturn(true);
        String password = "Demo Password CLI";

        execApp("-encryptPassword", password);

        verify(mockedCryptoService).encrypt(password);
    }

    @Test
    public void shouldCreateKeyStoreBeforeGettingKeyService() throws Exception {
        when(mockedKSManager.keyStoreExists()).thenReturn(false);
        String password = "Demo Password CLI 2";

        execApp("-encryptPassword", password);

        InOrder inOrder = inOrder(
                mockedKSManagerFactory, mockedKSManager,
                mockedCryptoServiceFactory, mockedCryptoService);

        inOrder.verify(mockedKSManagerFactory).newInstance();
        inOrder.verify(mockedKSManager).initKeyStore();
        inOrder.verify(mockedCryptoServiceFactory).getCryptoService();
        inOrder.verify(mockedCryptoService).encrypt(password);
    }

}

