package dbfit.util.crypto;

import java.io.PrintStream;
import java.io.File;

public class CryptoApp {

    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_INVALID_COMMAND = 1;
    public static final int EXIT_INVALID_OPTION = 2;
    public static final int EXIT_COMMAND_FAILED = 3;
    
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

    private int createKeyStore() throws Exception {
        return createKeyStore(ksFactory.newInstance());
    }

    private int createKeyStore(final String customPath) throws Exception {
        return createKeyStore(ksFactory.newInstance(new File(customPath)));
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
        updateStatus(" -createKeyStore [<keyStoreLocation>]");
        updateStatus("     Create new key store in keyStoreLocation directory.");
        updateStatus("     If keyStoreLocation is not specified - default is used.");
        updateStatus("     Default is user home folder or 'dbfit.keystore.path' system property");
        updateStatus(" -encryptPassword <password> [-keyStoreLocation <keyStoreLocation>]");
        updateStatus("     Encrypt the given password and show the result");
        updateStatus("     Password is encrypted using key from keyStoreLocation");
        updateStatus("     If no keyStoreLocation is specified - default location is used");
        updateStatus("     If no dbfit keystore and key exists - it's automatically generated");
        updateStatus(" -help");
        updateStatus("     Show this usage note");
    }

    public int execute(String[] args) throws Exception {
        String cmd = "";
        int errCode = EXIT_SUCCESS;

        if (args.length < 1) {
            errCode = EXIT_INVALID_COMMAND;
        } else {
            cmd = args[0];
        }

        if (cmd.equalsIgnoreCase("-createKeyStore")) {
            if (args.length == 2) {
                errCode = createKeyStore(args[1]);
            } else if (args.length == 1) {
                errCode = createKeyStore();
            } else {
                errCode = EXIT_INVALID_OPTION;
            }
        } else if (cmd.equalsIgnoreCase("-encryptPassword")) {
            if (args.length == 2) {
                errCode = encryptPassword(args[1]);
            } else if ((args.length == 4) && (args[2].equals("-keyStoreLocation"))) {
                errCode = encryptPassword(args[1], args[3]);
            } else {
                errCode = EXIT_INVALID_OPTION;
            }
        } else if (cmd.equalsIgnoreCase("-help")) {
            showUsage();
        } else {
            errCode = EXIT_INVALID_COMMAND;
        }

        if ((errCode == EXIT_INVALID_OPTION) || (errCode == EXIT_INVALID_COMMAND)) {
            showUsage();
        }

        return errCode;
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

