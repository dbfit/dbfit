package dbfit.util;

import dbfit.util.crypto.CryptoService;
import dbfit.util.crypto.CryptoServiceFactory;

import org.junit.Test;
import org.junit.After;
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

    @After
    public void cleanup() {
        CryptoServiceFactory.setCryptoService(null);
    }

    private String getOriginalPassword() {
        return DB_PASSWORD;
    }

    private String getEncryptedPassword() {
        return CryptoServiceFactory.getCryptoService().encrypt(
                getOriginalPassword());
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

        lines.add("password=" + wrapEncryptedPassword(getEncryptedPassword()));

        return lines;
    }

    private List<String> prepareEncryptedSettings() {
        return prepareEncryptedSettings(getOriginalPassword());
    }

    private String wrapEncryptedPassword(String pwd) {
        return "ENC(" + pwd + ")";
    }

    private void setFakeCryptoService(String pwd) {
        String encPwd = "XE-" + pwd;
        when(mockedCryptoService.encrypt(pwd)).thenReturn(encPwd);
        when(mockedCryptoService.decrypt(encPwd)).thenReturn(pwd);

        CryptoServiceFactory.setCryptoService(mockedCryptoService);
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
        List<String> lines = prepareEncryptedSettings();

        DbConnectionProperties.CreateFromString(lines);

        verify(mockedCryptoService, times(1)).decrypt(getEncryptedPassword());
    }

    private void checkPasswordLoad(boolean useFakeCryptoService) {
        if (useFakeCryptoService) {
            setFakeCryptoService(getOriginalPassword());
        }

        List<String> lines = prepareEncryptedSettings();

        DbConnectionProperties connProps =
            DbConnectionProperties.CreateFromString(lines);

        assertEquals(getOriginalPassword(), connProps.Password);
    }

    @Test
    public void shouldStoreDecryptedPasswordInDbConnProperties() {
        checkPasswordLoad(true);
    }

    /**
     * Integration test - using real decryption
     */
    @Test
    public void decryptedPasswordShouldMatchOriginalOne() {
        checkPasswordLoad(false);
    }

    @Test
    public void splitKeyValueSettingsLineTest() {
        String[] keyval = DbConnectionProperties.splitKeyVal("password=crap");

        assertEquals("password", keyval[0]);
        assertEquals("crap", keyval[1]);
    }

}

