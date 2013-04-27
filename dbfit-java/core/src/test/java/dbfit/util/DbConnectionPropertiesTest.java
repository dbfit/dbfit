package dbfit.util;

import dbfit.util.crypto.CryptoService;
import dbfit.util.crypto.CryptoServiceFactory;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DbConnectionPropertiesTest {

    private static final String DB_PASSWORD = "Test Password";

    private CryptoService cryptoService;
    @Mock private CryptoService mockedCryptoService;

    @Before
    public void prepare() {
        cryptoService = CryptoServiceFactory.getCryptoService();
    }

    @After
    public void cleanup() {
        CryptoServiceFactory.setCryptoService(null);
    }

    private String getOriginalPassword() {
        return DB_PASSWORD;
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

        lines.add("password=ENC(" + cryptoService.encrypt(password) + ")");

        return lines;
    }

    private List<String> prepareEncryptedSettings() {
        return prepareEncryptedSettings(getOriginalPassword());
    }

    @Test
    public void shouldCallDecryptWhenLoadingEncryptedPassword() {
        List<String> lines = prepareEncryptedSettings();
        CryptoServiceFactory.setCryptoService(mockedCryptoService);

        DbConnectionProperties.CreateFromString(lines);
        verify(mockedCryptoService, times(1)).decrypt(getOriginalPassword());
    }

    @Test
    public void decryptedPasswordShouldMatchOriginalOne() {
        List<String> lines = prepareEncryptedSettings();

        DbConnectionProperties connProps =
            DbConnectionProperties.CreateFromString(lines);

        assertEquals(getOriginalPassword(), connProps.Password);
    }
}

