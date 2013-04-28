package dbfit.util.crypto;

import java.io.OutputStream;

public class CryptoApp {

    private CryptoKeyStoreManagerFactory ksManagerFactory;
    private CryptoService cryptoService;
    private OutputStream out = System.out;

    public CryptoApp(CryptoKeyStoreManagerFactory factory, CryptoService cryptoService) {
        this.ksManagerFactory = factory;
        this.cryptoService = cryptoService;
    }

    public void setOutput(OutputStream out) {
        this.out = out;
    }

    public void resetOutput() {
        this.out = System.out;
    }

    public void execute(String[] args) throws Exception {
    }

    public static void main(String[] args) {

    }

}

