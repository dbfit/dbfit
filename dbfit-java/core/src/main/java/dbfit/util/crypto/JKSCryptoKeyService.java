package dbfit.util.crypto;

import dbfit.util.crypto.CryptoKeyService;

import java.security.KeyStore;
import java.security.Key;
import java.io.File;
import java.io.FileInputStream;

/**
 * Java KeyStore-based key service
 */
public class JKSCryptoKeyService implements CryptoKeyService {

    public static final String KS_TYPE = "JCEKS";
    public static final String KS_NAME = ".dbfit.jks";
    public static final char[] KS_PASS = "DbFit Access Key".toCharArray();
    public static final String KEY_ALIAS = "dbfit";

    private KeyStore keyStore;
    private char[] password;

    public JKSCryptoKeyService(File keyStorePath) {
        this(keyStorePath, KS_PASS); 
    }

    public JKSCryptoKeyService(File keyStorePath, char[] password) {
        try {
            this.password = password;
            this.keyStore = loadKeyStore(keyStorePath, password);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyStore loadKeyStore(File keyStorePath, char[] password)
                                                        throws Exception {
        KeyStore ks = KeyStore.getInstance(KS_TYPE);
        File file = new File(keyStorePath, KS_NAME);
        FileInputStream in = null;

        try {
            in = new FileInputStream(file);
            ks.load(in, password);
            return ks;
        } finally {
            if (null != in) {
                in.close();
            }
        }
    }

    @Override
    public Key getKey() {
        try {
            return keyStore.getKey(KEY_ALIAS, password);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}

