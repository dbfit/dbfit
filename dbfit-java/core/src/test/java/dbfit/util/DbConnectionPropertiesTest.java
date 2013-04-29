package dbfit.util;

import dbfit.util.crypto.CryptoServiceTests;
import dbfit.util.crypto.CryptoService;
import dbfit.util.crypto.CryptoServiceFactory;
import dbfit.util.crypto.CryptoAdmin;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DbConnectionPropertiesTest {

    private static final String DB_PASSWORD = "Test Password";

    @Mock private CryptoService mockedCryptoService;
    @Rule public TemporaryFolder tempKeyStoreFolder = new TemporaryFolder();

    @After
    public void resetKeyServiceFactory() {
        CryptoServiceTests.resetTestKeyServiceFactory();
    }

    @After
    public void cleanup() {
        CryptoAdmin.setCryptoServiceFactory(null);
    }

    private String getOriginalPassword() {
        return DB_PASSWORD;
    }

    private String encrypt(String password) {
        return CryptoAdmin.getCryptoService().encrypt(password);
    }

    private String naiveEncrypt(String pwd) {
        return "XE-" + pwd;
    }

    /**
     * Generate dummy configuration settings with the given password in
     * encrypted format
     */
    private List<String> prepareEncryptedSettings(String password) {
        List<String> lines = new java.util.ArrayList<String>();
        lines.add("service=mydemoservice");
        lines.add("username=mydemouser");
        lines.add("database=mydemodb");

        lines.add("password=" + wrapEncryptedPassword(password));

        return lines;
    }

    private String wrapEncryptedPassword(String pwd) {
        return "ENC(" + pwd + ")";
    }

    private void setFakeCryptoService(String pwd) {
        String encPwd = naiveEncrypt(pwd);
        when(mockedCryptoService.decrypt(encPwd)).thenReturn(pwd);

        CryptoAdmin.setCryptoServiceFactory(new CryptoServiceFactory() {
            @Override public CryptoService getCryptoService() {
                return mockedCryptoService;
            }
        });
    }

    @Test
    public void unwrapEncryptedPasswordTest() {
        String pwd = "XYZ";
        String encPwd = wrapEncryptedPassword(pwd);

        assertEquals(pwd, DbConnectionProperties.unwrapEncryptedPassword(encPwd));
    }

    @Test
    public void unwrapNonEncryptedPasswordTest() {
        assertNull(DbConnectionProperties.unwrapEncryptedPassword("XYZ"));
    }

    @Test
    public void shouldCallDecryptWhenLoadingEncryptedPassword() {
        setFakeCryptoService(getOriginalPassword());
        String encryptedPassword = naiveEncrypt(getOriginalPassword());
        List<String> lines = prepareEncryptedSettings(encryptedPassword);

        DbConnectionProperties.CreateFromString(lines);

        verify(mockedCryptoService, times(1)).decrypt(encryptedPassword);
    }

    private DbConnectionProperties prepareAndtLoadDbConnProperties(
                                        boolean useFakeCryptoService) {
        String encryptedPassword;

        if (useFakeCryptoService) {
            setFakeCryptoService(getOriginalPassword());
            encryptedPassword = naiveEncrypt(getOriginalPassword());
        } else {
            encryptedPassword = encrypt(getOriginalPassword());
        }

        List<String> lines = prepareEncryptedSettings(encryptedPassword);

        return DbConnectionProperties.CreateFromString(lines);
    }

    private void checkPasswordLoad(boolean useFakeCryptoService) {
        DbConnectionProperties connProps = prepareAndtLoadDbConnProperties(
                                                    useFakeCryptoService);

        assertEquals(getOriginalPassword(), connProps.Password);
    }

    @Test
    public void shouldStoreDecryptedPasswordInDbConnProperties() {
        checkPasswordLoad(true);
    }

    @Test
    public void parseEncryptedPasswordValueTest() throws Exception {
        CryptoServiceTests.initTestCryptoKeyStore(tempKeyStoreFolder.getRoot());

        String wrappedPwd = wrapEncryptedPassword(encrypt(getOriginalPassword()));
        String decPwd = DbConnectionProperties.parsePassword(wrappedPwd);

        assertEquals(getOriginalPassword(), decPwd);
    }

    @Test
    public void parseNonEncryptedPasswordValueTest() throws Exception {
        CryptoServiceTests.initTestCryptoKeyStore(tempKeyStoreFolder.getRoot());

        String decPwd = DbConnectionProperties.parsePassword(getOriginalPassword());

        assertEquals(getOriginalPassword(), decPwd);
    }

    /**
     * Integration test - using real decryption
     */
    @Test
    public void decryptedPasswordShouldMatchOriginalOne() throws Exception {
        CryptoServiceTests.initTestCryptoKeyStore(tempKeyStoreFolder.getRoot());

        checkPasswordLoad(false);
    }

    @Test
    public void splitKeyValueSettingsLineTest() {
        String[] keyval = DbConnectionProperties.splitKeyVal("password=crap");

        assertEquals("password", keyval[0]);
        assertEquals("crap", keyval[1]);
    }

    @Test
    public void shouldAllowEqualsSignInSettingsValue() {
        String key = "password";
        String value = "One=Two-Three==";
        String[] keyval = DbConnectionProperties.splitKeyVal(
                key + "=" + value);

        assertEquals(key, keyval[0]);
        assertEquals(value, keyval[1]);
    }
}

