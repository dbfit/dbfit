package dbfit.util;

import static dbfit.util.PropertiesTestsSetUp.wrapEncryptedValue;

import dbfit.util.crypto.CryptoTestsConfig;
import dbfit.util.crypto.CryptoService;
import static dbfit.util.crypto.CryptoTestsConfig.getFakeCryptoService;

import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesLoaderTest {

    public static final String DB_PASSWORD = "Test Password";
    private final String ENCRYPTED_PASSWORD = encrypt(DB_PASSWORD);

    @Mock private CryptoService mockedCryptoService;
    @Rule public TemporaryFolder tempKeyStoreFolder = new TemporaryFolder();
    private PropertiesLoader fakeLoader;

    @Before
    public void initMockedCryptoService() {
        when(mockedCryptoService.decrypt(ENCRYPTED_PASSWORD)).thenReturn(DB_PASSWORD);
        fakeLoader = new PropertiesLoader(getFakeCryptoService());
    }

    private String encrypt(String password) {
        return getFakeCryptoService().encrypt(password);
    }

    private List<String> prepareEncryptedSettings() {
        return PropertiesTestsSetUp.prepareEncryptedSettings(ENCRYPTED_PASSWORD);
    }

    //----- Whole list loading tests -----/
    @Test
    public void shouldCallDecryptWhenLoadingEncryptedValue() {
        PropertiesLoader loader = new PropertiesLoader(mockedCryptoService);

        loader.loadFromList(prepareEncryptedSettings());

        verify(mockedCryptoService, times(1)).decrypt(ENCRYPTED_PASSWORD);
    }

    @Test
    public void testNonEncryptedPropertyLoad() throws Exception {
        Map<String, String> props =
            fakeLoader.loadFromList(prepareEncryptedSettings());

        assertEquals("mydemodb", props.get("database"));
    }

    @Test
    public void testEncryptedPropertyLoad() throws Exception {
        Map<String, String> props =
            fakeLoader.loadFromList(prepareEncryptedSettings());

        assertEquals(DB_PASSWORD, props.get("password"));
    }

    @Test
    public void shouldSkipCommentedLines() throws Exception {
        List<String> lines = new java.util.ArrayList<String>();
        lines.add("#=A comment here");
        lines.add("username=myname");

        Map<String, String> props = fakeLoader.loadFromList(lines);

        assertThat(props, not(hasKey(startsWith("#"))));
    }

    // Integration test with real crypto svc
    @Test
    public void testEncryptedPropertyLoadRealCryptoSvc() throws Exception {
        java.io.File ksRoot = tempKeyStoreFolder.getRoot();
        CryptoTestsConfig.createTestKeyStore(ksRoot);
        CryptoService crypto = CryptoTestsConfig.getCryptoService(ksRoot);

        PropertiesLoader loader = new PropertiesLoader(crypto);

        Map<String, String> props = loader.loadFromList(
            PropertiesTestsSetUp.prepareEncryptedSettings(crypto.encrypt(DB_PASSWORD)));

        assertEquals(DB_PASSWORD, props.get("password"));
    }
    //----- End of whole list loading tests -/

    //----- Line-level processing tests -----/
    @Test
    public void parseEncryptedValueTest() throws Exception {
        String decPwd = fakeLoader.parseValue(wrapEncryptedValue(ENCRYPTED_PASSWORD));
        assertEquals(DB_PASSWORD, decPwd);
    }

    @Test
    public void parseNonEncryptedValueTest() throws Exception {
        String decPwd = fakeLoader.parseValue(DB_PASSWORD);
        assertEquals(DB_PASSWORD, decPwd);
    }

    //----- Start of static methods tests ------/
    @Test
    public void unwrapEncryptedValueTest() {
        String value = "XYZ";
        String wrapped = wrapEncryptedValue(value);

        assertEquals(value, PropertiesLoader.unwrapEncryptedValue(wrapped));
    }

    @Test
    public void unwrapNonEncryptedValueShouldGiveNull() {
        assertNull(PropertiesLoader.unwrapEncryptedValue("XYZ"));
    }

    @Test
    public void splitKeyValueSettingsLineTest() {
        String[] keyval = PropertiesLoader.splitKeyVal("password=crap");

        assertEquals("password", keyval[0]);
        assertEquals("crap", keyval[1]);
    }

    @Test
    public void shouldAllowEqualsSignInSettingsValue() {
        String key = "password";
        String value = "One=Two-Three==";
        String[] keyval = PropertiesLoader.splitKeyVal(
                key + "=" + value);


        assertEquals(key, keyval[0]);
        assertEquals(value, keyval[1]);
    }
    //------- End of static methods tests ------/
}

