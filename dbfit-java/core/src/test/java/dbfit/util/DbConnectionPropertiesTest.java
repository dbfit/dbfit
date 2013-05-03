package dbfit.util;

import dbfit.util.crypto.CryptoTestsConfig;
import static dbfit.util.crypto.CryptoTestsConfig.getFakeCryptoService;
import static dbfit.util.PropertiesTestsSetUp.prepareEncryptedSettings;
import static dbfit.util.PropertiesTestsSetUp.prepareNonEncryptedSettings;

import dbfit.util.crypto.CryptoService;
import dbfit.util.crypto.CryptoServiceFactory;
import dbfit.util.crypto.CryptoFactories;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DbConnectionPropertiesTest {

    private static final String DB_PASSWORD = "Test Password";

    @Rule public TemporaryFolder tempKeyStoreFolder = new TemporaryFolder();
    @Mock private CryptoService mockedCryptoService;

    @Before
    public void prepare() {
        when(mockedCryptoService.decrypt(anyString())).thenReturn("NonEmptyRet");
    }

    private static DbConnectionProperties loadConnProps(
            List<String> lines, CryptoService crypto) {
        return DbConnectionProperties.CreateFromString(lines, crypto);
    }

    @Test
    public void shouldCallDecryptWhenLoadingEncryptedPassword() {
        List<String> lines = prepareEncryptedSettings("ZYX");

        try {
            loadConnProps(lines, mockedCryptoService);
        } catch (Exception e) {
            // ignore
        }

        verify(mockedCryptoService, times(1)).decrypt("ZYX");
    }

    private void checkEncryptedPropertiesLoad(CryptoService crypto) {
        List<String> lines = prepareEncryptedSettings(crypto.encrypt(DB_PASSWORD));

        checkLoadedProperties(loadConnProps(lines, crypto));
    }

    @Test
    public void testNonEncryptedPropertiesLoad() {
        List<String> lines = prepareNonEncryptedSettings(DB_PASSWORD);

        checkLoadedProperties(loadConnProps(lines, mockedCryptoService));
        verifyZeroInteractions(mockedCryptoService);
    }

    @Test
    public void testEncryptedPropertiesLoadWithFakeCrypto() {
        checkEncryptedPropertiesLoad(CryptoTestsConfig.getFakeCryptoService());
    }

    @Test
    public void testEncryptedPropertiesLoadWithRealCrypto() throws Exception {
        CryptoService crypto = initTestKeyStore();
        checkEncryptedPropertiesLoad(crypto);
    }

    private CryptoService initTestKeyStore() throws Exception {
        java.io.File ksRoot = tempKeyStoreFolder.getRoot();
        CryptoTestsConfig.createTestKeyStore(ksRoot);
        return CryptoTestsConfig.getCryptoService(ksRoot);
    }

    private void checkLoadedProperties(DbConnectionProperties props) {

        assertEquals("Service", "mydemoservice", props.Service);
        assertEquals("Password", DB_PASSWORD, props.Password);
        assertEquals("Username", "mydemouser", props.Username);
        assertEquals("Database", "mydemodb", props.DbName);
        assertEquals("connection-string", "myconnection", props.FullConnectionString);
    }

}

