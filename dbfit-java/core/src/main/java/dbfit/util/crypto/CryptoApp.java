package dbfit.util.crypto;

import java.io.PrintStream;
import java.io.File;

public class CryptoApp {

    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_INVALID_COMMAND = 1;
    public static final int EXIT_COMMAND_FAILED = 2;
    
    private CryptoKeyStoreFactory ksFactory;
    private CryptoServiceFactory cryptoServiceFactory;
    private PrintStream out = System.out;

    public CryptoApp(
            CryptoKeyStoreFactory ksFactory, CryptoServiceFactory csFactory) {
        this.ksFactory = ksFactory;
        this.cryptoServiceFactory = csFactory;
    }

    public CryptoApp() {
        this(CryptoFactories.getCryptoKeyStoreFactory(),
                CryptoFactories.getCryptoServiceFactory());
    }

    private void updateStatus(String msg) {
        out.println(msg);
    }

    private int createKeyStore(CryptoKeyStore ks) throws Exception {
        try {
            ks.createKeyStore();
            updateStatus("KeyStore created: " + ks.getKeyStoreFile());
            return EXIT_SUCCESS;
        } catch (CryptoKeyStoreException e) {
            updateStatus("KeyStore create failed: " + e.getMessage());
            return EXIT_COMMAND_FAILED;
        }
    }

    private int encryptPassword(final String password, final CryptoKeyStore ks)
                                                    throws Exception {
        if (!ks.keyStoreExists()) {
            createKeyStore(ks);
        }
        updateStatus("Using KeyStore: " + ks.getKeyStoreFile());

        String encPwd = getCryptoService(ks).encrypt(password);
        updateStatus("Encrypted Password:\nENC(" + encPwd + ")");

        return EXIT_SUCCESS;
    }

    private int encryptPassword(final String password, final String path)
                                                    throws Exception {
        return encryptPassword(password, ksFactory.newInstance(new File(path)));
    }

    private int encryptPassword(final String password) throws Exception {
        return encryptPassword(password, ksFactory.newInstance());
    }

    private void showUsage() {
        updateStatus("Usage arguments:");
        updateStatus(" <password> [-keyStoreLocation <keyStoreLocation>]");
        updateStatus("     Encrypt the given password and show the result.");
        updateStatus("     Password is encrypted using key from keyStoreLocation.");
        updateStatus("     If no keyStoreLocation is specified - default location is used.");
        updateStatus("     If no dbfit keystore and key exist - they're automatically created.");
    }

    public int execute(String[] args) throws Exception {
        String cmd = (args.length == 0) ? "" : args[0];

        if (args.length == 1) {
            return encryptPassword(args[0]);
        } else if ((args.length == 3) && (args[1].equals("-keyStoreLocation"))) {
            return encryptPassword(args[0], args[2]);
        } else {
            showUsage();
            return EXIT_INVALID_COMMAND;
        }
    }

    public static void main(String[] args) throws Exception {
        CryptoApp app = new CryptoApp();

        int exitCode = app.execute(args);
        System.exit(exitCode);
    }

    private CryptoService getCryptoService(final CryptoKeyStore ks) {
        return cryptoServiceFactory.getCryptoService(ks);
    }

}

