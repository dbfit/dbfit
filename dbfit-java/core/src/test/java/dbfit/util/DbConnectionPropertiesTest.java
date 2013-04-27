package dbfit.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.List;

public class DbConnectionPropertiesTest {

    private static final String DB_PASSWORD = "Test Password";

    private String getOriginalPassword() {
        return DB_PASSWORD;
    }

    private String encrypt(String msg) {
        // return cryptoService.encrypt(msg);
        // TODO: encrypt msg here
        return msg;
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

        lines.add("password=ENC(" + encrypt(password) + ")");

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

