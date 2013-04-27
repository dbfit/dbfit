package dbfit.util;

import dbfit.util.crypto.CryptoService;
import dbfit.util.crypto.CryptoServiceFactory;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;

import java.util.List;

public class DbConnectionPropertiesTest {

    private static final String DB_PASSWORD = "Test Password";

    private CryptoService cryptoService;


    @Before
    public void prepare() {
        cryptoService = CryptoServiceFactory.getCryptoService();
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

    @Test
    public void decryptedPasswordShouldMatchOriginalOne() {
        String originalPassword = getOriginalPassword();
        List<String> lines = prepareEncryptedSettings(originalPassword);

        DbConnectionProperties connProps =
            DbConnectionProperties.CreateFromString(lines);

        assertEquals(originalPassword, connProps.Password);
    }
}

