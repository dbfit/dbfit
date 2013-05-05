package dbfit.util.crypto;

public interface CryptoServiceFactory {
    public CryptoService getCryptoService();
    public CryptoService getCryptoService(CryptoKeyAccessor keyAccessor);
}

