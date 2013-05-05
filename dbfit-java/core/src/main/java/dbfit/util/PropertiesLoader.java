package dbfit.util;

import dbfit.util.crypto.CryptoFactories;
import dbfit.util.crypto.CryptoService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PropertiesLoader {

    private CryptoService crypto;

    public PropertiesLoader(CryptoService cryptoService) {
        this.crypto = cryptoService;
    }

    public PropertiesLoader() {
        this(CryptoFactories.getCryptoService());
    }

    public Map<String, String> loadFromList(List<String> lines) {
        Map<String, String> props = new HashMap<String, String>();

        for (String line : lines) {
            String[] keyval = parseLine(line);
            if (keyval != null) {
                props.put(keyval[0], keyval[1]);
            }
        }

        return props;
    }

    public Map<String, String> loadFromFile(String path)
            throws FileNotFoundException, IOException {
        FileReader reader = null;
        try {
            reader = new FileReader(new File(path));
            BufferedReader br = new BufferedReader(reader);
            List<String> lines = new ArrayList<String>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            return loadFromList(lines);
        } finally {
            if (null != reader) {
                reader.close();
            }
        }
    }

    /**
     * Unwrap encrypted form value "ENC(value)".
     *
     * @return text if wrapped in ENC(text)
     *         null if format is not ENC(...)
     */
    public static String unwrapEncryptedValue(String encValue) {
        if (encValue == null) {
            return null;
        }

        String encryptedFormPrefix = "ENC(";
        if (encValue.startsWith(encryptedFormPrefix)) {
            return encValue.substring(encryptedFormPrefix.length(),
                                        encValue.length() - 1);
        } else {
            return null;
        }
    }

    public String parseValue(String value) {
        String encValue = unwrapEncryptedValue(value);

        if (encValue == null) {
            return value;
        } else {
            return crypto.decrypt(encValue);
        }
    }

    public static String[] splitKeyVal(String line) {
        return line.split("=", 2);
    }

    private boolean isIgnorableLine(String line) {
        if (line == null) {
            return true;
        }

        String trimline = line.trim();

        if ((trimline.length() == 0) || (trimline.startsWith("#"))) {
            return true;
        }

        return false;
    }

    private String[] parseLine(String line) {
        if (isIgnorableLine(line)) {
            return null;
        }

        String[] keyval = splitKeyVal(line.trim());
        String key = keyval[0].trim().toLowerCase();
        String val;

        if (keyval.length == 1) {
            val = "";
        } else {
            val = parseValue(keyval[1].trim());
        }

        return new String[] {key, val};
    }
}

