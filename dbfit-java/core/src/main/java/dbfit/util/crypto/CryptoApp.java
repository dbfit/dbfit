package dbfit.util.crypto;

import java.io.PrintStream;

public class CryptoApp {

    private CryptoKeyStoreManagerFactory ksManagerFactory;
    private CryptoService cryptoService;
    private PrintStream out = System.out;

    public CryptoApp(CryptoKeyStoreManagerFactory factory, CryptoService cryptoService) {
        this.ksManagerFactory = factory;
        this.cryptoService = cryptoService;
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

    private void createKeyStore() throws Exception {
        CryptoKeyStoreManager ksMgr = ksManagerFactory.newInstance();
        if (ksMgr.initKeyStore()) {
            updateStatus("KeyStore created: " + ksMgr.getKeyStoreFile());
        } else {
            updateStatus("KeyStore already exists: "
                    + ksMgr.getKeyStoreFile()
                    + ". You should clean it up manually if you want to re-create.");
        }
    }

    public void execute(String[] args) throws Exception {
        if (args.length < 1) {
            return;
        }

        String cmd = args[0].toLowerCase();

        if (cmd.equals("-createkeystore")) {
            createKeyStore();
        }
    }

    public static void main(String[] args) throws Exception {
        CryptoApp app = new CryptoApp(CryptoAdmin.getKSManagerFactory(),
                CryptoServiceFactory.getCryptoService());

        app.execute(args);
    }

}

