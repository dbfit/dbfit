package dbfit.util.crypto;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.SecretKey;
import java.security.KeyStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CryptoKeyStoreManager {
    public static final String KS_TYPE = "JCEKS";
    public static final String KS_NAME = ".dbfit.jks";
    public static final char[] KS_PASS = "DbFit Access Key".toCharArray();
    public static final String KEY_ALIAS = "dbfit";

    private File keyStoreLocation;
    private char[] keyStorePassword;

    public static CryptoKeyStoreManager getDefaultInstance() {
        return new CryptoKeyStoreManager();
    }

    public CryptoKeyStoreManager(File keyStorePath, char[] ksPassword) {
        this.keyStoreLocation = keyStorePath;
        this.keyStorePassword = ksPassword;
    }

    public CryptoKeyStoreManager(File keyStorePath) {
        this(keyStorePath, KS_PASS);
    }

    public CryptoKeyStoreManager() {
        this(getDefaultKeyStoreLocation());
    }

    public File getKeyStoreLocation() {
        return keyStoreLocation;
    }

    public char[] getKeyStorePassword() {
        return keyStorePassword;
    }

    public File getKeyStoreFile() {
        return new File(getKeyStoreLocation(), KS_NAME);
    }

    private void verifyNoExistingKeyStore() throws UnsupportedOperationException {
        if (getKeyStoreFile().exists()) {
            throw new UnsupportedOperationException(
                    "Cannot create KeyStore on top of existing one! ["
                    + getKeyStoreFile() + "]");
        }
    }

    public void createKeyStore() throws Exception {
        verifyNoExistingKeyStore();

        KeyStore ks = KeyStore.getInstance(KS_TYPE);
        ks.load(null, getKeyStorePassword());
        SecretKey mySecretKey = (SecretKey) generateKey();


        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(mySecretKey);
        ks.setEntry(KEY_ALIAS, skEntry, new KeyStore.PasswordProtection(
                    getKeyStorePassword()));

        FileOutputStream fos = null;
        try {
            File ksFile = getKeyStoreFile();
            fos = new java.io.FileOutputStream(ksFile);
            ks.store(fos, getKeyStorePassword());
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    public KeyStore loadKeyStore() throws Exception {
        KeyStore ks = KeyStore.getInstance(KS_TYPE);
        FileInputStream in = null;

        try {
            in = new FileInputStream(getKeyStoreFile());
            ks.load(in, getKeyStorePassword());
            return ks;
        } finally {
            if (null != in) {
                in.close();
            }
        }
    }

    public static Key generateKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        return kgen.generateKey();
    }

    public static File getDefaultKeyStoreLocation() {
        String ksLocation = System.getProperty("dbfit.keystore.path");
        if (ksLocation == null) {
            ksLocation = System.getProperty("user.home");
        }

        return new File(ksLocation);
    }

}

