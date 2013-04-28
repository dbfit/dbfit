package dbfit.util.crypto;

import java.io.PrintStream;
import java.io.File;

public class CryptoApp {

    private CryptoKeyStoreManagerFactory ksManagerFactory;
    private PrintStream out = System.out;

    public CryptoApp(CryptoKeyStoreManagerFactory factory) {
        this.ksManagerFactory = factory;
    }

    public void setOutput(PrintStream out) {
        this.out = out;
    }

    public void resetOutput() {
        this.out = System.out;
    }

    private void updateStatus(String msg) {
        out.println(msg);
    }

    private void updateErrStatus(String msg) {
        out.println(msg);
    }

    private void createKeyStore(CryptoKeyStoreManager ksMgr) throws Exception {
        if (ksMgr.initKeyStore()) {
            updateStatus("KeyStore created: " + ksMgr.getKeyStoreFile());
        } else {
            updateStatus("KeyStore already exists: "
                    + ksMgr.getKeyStoreFile()
                    + ". You should clean it up manually if you want to re-create.");
        }
    }

    private void createKeyStore() throws Exception {
        CryptoKeyStoreManager ksMgr = ksManagerFactory.newInstance();
        createKeyStore(ksMgr);
    }

    private void createKeyStore(String customPath) throws Exception {
        File ksRoot = new File(customPath);
        CryptoKeyStoreManager ksMgr = ksManagerFactory.newInstance(ksRoot);
        createKeyStore(ksMgr);
    }

    private void encryptPassword(String password) throws Exception {
        CryptoKeyStoreManager ksMgr = ksManagerFactory.newInstance();
        if (!ksMgr.keyStoreExists()) {
            createKeyStore(ksMgr);
        }

        String encPwd = getCryptoService().encrypt(password);
        updateStatus("Encrypted Password\n: ENC(" + encPwd + ")");
    }

    public void execute(String[] args) throws Exception {
        if (args.length < 1) {
            return;
        }

        String cmd = args[0];

        if (cmd.equalsIgnoreCase("-createKeyStore")) {
            if (args.length > 1) {
                createKeyStore(args[1]);
            } else {
                createKeyStore();
            }
        } else if (cmd.equalsIgnoreCase("-encryptPassword")) {
            encryptPassword(args[1]);
        }
    }

    public static void main(String[] args) throws Exception {
        CryptoApp app = new CryptoApp(CryptoAdmin.getKSManagerFactory());

        app.execute(args);
    }

    private CryptoService getCryptoService() {
        return CryptoServiceFactory.getCryptoService();
    }

}

