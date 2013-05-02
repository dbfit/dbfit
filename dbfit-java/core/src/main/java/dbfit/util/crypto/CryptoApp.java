package dbfit.util.crypto;

import java.io.PrintStream;
import java.io.File;

public class CryptoApp {

    private CryptoKeyStoreFactory ksFactory;
    private PrintStream out = System.out;

    public CryptoApp(CryptoKeyStoreFactory factory) {
        this.ksFactory = factory;
    }

    private void updateStatus(String msg) {
        out.println(msg);
    }

    private int createKeyStore(CryptoKeyStore ks) throws Exception {
        try {
            ks.createKeyStore();
            updateStatus("KeyStore created: " + ks.getKeyStoreFile());
            return 0;
        } catch (CryptoKeyStoreException e) {
            updateStatus("KeyStore create failed: " + e.getMessage());
            return 3;
        }
    }

    private void createKeyStore() throws Exception {
        CryptoKeyStore ks = ksFactory.newInstance();
        createKeyStore(ks);
    }

    private void createKeyStore(String customPath) throws Exception {
        File ksRoot = (null == customPath) ? null : new File(customPath);
        CryptoKeyStore ks = ksFactory.newInstance(ksRoot);
        createKeyStore(ks);
    }

    private void encryptPassword(String password) throws Exception {
        CryptoKeyStore ks = ksFactory.newInstance();
        if (!ks.keyStoreExists()) {
            createKeyStore(ks);
        }

        String encPwd = getCryptoService().encrypt(password);
        updateStatus("Encrypted Password:\nENC(" + encPwd + ")");
    }

    private void showUsage() {
        updateStatus("Usage arguments:");
        updateStatus(" -createKeyStore [<keyStoreLocation>]");
        updateStatus("     Create new key store in keyStoreLocation directory.");
        updateStatus("     If keyStoreLocation is not specified - default is used.");
        updateStatus("     Default is user home folder or 'dbfit.keystore.path' system property");
        updateStatus(" -encryptPassword <password>");
        updateStatus("     Encrypt the given password and show the result");
        updateStatus(" -help");
        updateStatus("     Show this usage note");
    }

    public int execute(String[] args) throws Exception {
        String cmd = "";
        int errCode = 0;

        if (args.length < 1) {
            errCode = 1;
        } else {
            cmd = args[0];
        }

        if (cmd.equalsIgnoreCase("-createKeyStore")) {
            if (args.length == 2) {
                createKeyStore(args[1]);
            } else if (args.length == 1) {
                createKeyStore();
            } else {
                errCode = 2;
            }
        } else if (cmd.equalsIgnoreCase("-encryptPassword")) {
            if (args.length == 2) {
                encryptPassword(args[1]);
            } else {
                errCode = 2;
            }
        } else if (cmd.equalsIgnoreCase("-help")) {
            showUsage();
        } else {
            errCode = 1;
        }

        if (errCode != 0) {
            showUsage();
        }

        return errCode;
    }

    public static void main(String[] args) throws Exception {
        CryptoApp app = new CryptoApp(CryptoAdmin.getCryptoKeyStoreFactory());

        int exitCode = app.execute(args);
        System.exit(exitCode);
    }

    private CryptoService getCryptoService() {
        return CryptoAdmin.getCryptoServiceFactory().getCryptoService();
    }

}

