package dbfit.util.crypto;

import static dbfit.util.crypto.CryptoAdmin.getDefaultKeyStoreLocation;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.SecretKey;
import java.security.KeyStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class JKSCryptoKeyStoreManager implements CryptoKeyStoreManager {
    public static final String KS_TYPE = "JCEKS";
    public static final String KS_NAME = ".dbfit.jks";
    public static final char[] KS_PASS = "DbFit Access Key".toCharArray();
    public static final String KEY_ALIAS = "dbfit";

    private File keyStoreLocation;
    private char[] keyStorePassword;

    public JKSCryptoKeyStoreManager(File keyStorePath, char[] ksPassword) {
        this.keyStoreLocation = keyStorePath;
        this.keyStorePassword = ksPassword;
    }

    public JKSCryptoKeyStoreManager(File keyStorePath) {
        this(keyStorePath, KS_PASS);
    }

    public JKSCryptoKeyStoreManager() {
        this(getDefaultKeyStoreLocation());
    }

    public File getKeyStoreLocation() {
        return keyStoreLocation;
    }

    public char[] getKeyStorePassword() {
        return keyStorePassword;
    }

    @Override
    public File getKeyStoreFile() {
        return new File(getKeyStoreLocation(), KS_NAME);
    }

    @Override
    public boolean keyStoreExists() {
        return getKeyStoreFile().exists();
    }

    @Override
    public void createKeyStore() throws Exception {
        if (!initKeyStore()) {
            throw new UnsupportedOperationException(
                    "Cannot create KeyStore on top of existing one! ["
                    + getKeyStoreFile() + "]");
        }
    }

    @Override
    public boolean initKeyStore() throws Exception {
        if (keyStoreExists()) {
            return false;
        }

        createKeyStoreNoCheck();
        return true;
    }

    private void createKeyStoreNoCheck() throws Exception {
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

    @Override
    public Key generateKey() throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance("AES").generateKey();
    }

    @Override
    public Key loadKey() {
        try {
            return loadKeyStore().getKey(KEY_ALIAS, getKeyStorePassword());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}

