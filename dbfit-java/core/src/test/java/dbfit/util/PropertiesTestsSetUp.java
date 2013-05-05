package dbfit.util;

import java.util.List;
import java.util.ArrayList;

public class PropertiesTestsSetUp {

    private static List<String> prepareGeneralSettings() {
        List<String> lines = new java.util.ArrayList<String>();
        lines.add("# Some comments");
        lines.add("service=mydemoservice");
        lines.add("username=mydemouser");
        lines.add("database=mydemodb");
        lines.add("connection-string=myconnection");
        lines.add(""); // empty line
        lines.add(" "); // empty line
        lines.add(" # indented comment");
        lines.add("#=another commented line");
        return lines;
    }

    public static List<String> prepareNonEncryptedSettings(String pwd) {
        List<String> lines = prepareGeneralSettings();
        lines.add("password=" + pwd);
        return lines;
    }

    /**
     * Generate dummy configuration settings with the given password in
     * encrypted format
     */
    public static List<String> prepareEncryptedSettings(String encPwd) {
        List<String> lines = prepareGeneralSettings();
        lines.add("password=" + wrapEncryptedValue(encPwd));
        return lines;
    }

    public static String wrapEncryptedValue(String value) {
        return "ENC(" + value + ")";
    }
}

