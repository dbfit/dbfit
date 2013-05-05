package dbfit.util.crypto;

import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;

public class AESKeyGenerator {
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance("AES").generateKey();
    }
}

