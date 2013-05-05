package dbfit.util.crypto;

import java.security.Key;
import javax.crypto.SecretKey;
import java.security.KeyStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class JKSCryptoKeyStore implements CryptoKeyStore {

    public static final String KS_TYPE = "JCEKS";
    public static final String KS_NAME = ".dbfit.jks";
    public static final char[] KS_PASS = "DbFit Access Key".toCharArray();
    public static final String KEY_ALIAS = "dbfit";

    private File keyStoreLocation;

    public JKSCryptoKeyStore(final File keyStorePath) {
        if (null != keyStorePath) {
            this.keyStoreLocation = keyStorePath;
        } else {
            this.keyStoreLocation = getDefaultKeyStoreLocation();
        }
    }

    public JKSCryptoKeyStore() {
        this.keyStoreLocation = getDefaultKeyStoreLocation();
    }

    private File getKeyStoreLocation() {
        return keyStoreLocation;
    }

    private char[] getKeyStorePassword() {
        return KS_PASS;
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
        if (!getKeyStoreLocation().exists()) {
            throw new RuntimeException("No such folder: " + getKeyStoreLocation());
        }

        if (keyStoreExists()) {
            throw new CryptoKeyStoreException(this,
                    "Cannot create KeyStore on top of existing one! ["
                    + getKeyStoreFile() + "]");
        }

        createKeyStoreNoCheck();
    }

    private void setKsFilePermissions() throws Exception {
        File ksFile = getKeyStoreFile();
        ksFile.setReadable(false, false);
        ksFile.setWritable(false, false);
        ksFile.setExecutable(false, false);
        ksFile.setReadable(true);
    }

    private void createKeyStoreNoCheck() throws Exception {
        KeyStore ks = KeyStore.getInstance(KS_TYPE);
        ks.load(null, getKeyStorePassword());
        SecretKey mySecretKey = AESKeyGenerator.generateKey();

        KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(mySecretKey);
        ks.setEntry(KEY_ALIAS, skEntry, new KeyStore.PasswordProtection(
                    getKeyStorePassword()));

        FileOutputStream fos = null;
        try {
            File ksFile = getKeyStoreFile();
            fos = new java.io.FileOutputStream(ksFile);
            ks.store(fos, getKeyStorePassword());
            fos.close();
            setKsFilePermissions();
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
    public Key getKey() {
        try {
            return loadKeyStore().getKey(KEY_ALIAS, getKeyStorePassword());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static File getDefaultKeyStoreLocation() {
        String ksLocation = System.getProperty("dbfit.keystore.path");
        if (ksLocation == null) {
            ksLocation = System.getProperty("user.home");
        }

        return new File(ksLocation);
    }

}

