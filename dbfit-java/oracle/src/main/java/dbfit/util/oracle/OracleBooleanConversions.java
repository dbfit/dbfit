package dbfit.util.oracle;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class OracleBooleanConversions {

    public static String getChr2Bool(String prefix) {
        String template = loadChr2BoolTemplate();
        return template.replace("${prefix}", prefix);
    }

    public static String getBool2Chr(String prefix) {
        String template = loadBool2ChrTemplate();
        return template.replace("${prefix}", prefix);
    }

    private static String loadChr2BoolTemplate() {
        return loadResource("chr2bool.pls");
    }

    private static String loadBool2ChrTemplate() {
        return loadResource("bool2chr.pls");
    }

    private static String loadResource(String resource) {
        InputStream in = OracleBooleanConversions.class.getResourceAsStream(resource);
        try {
            return IOUtils.toString(in, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

}

