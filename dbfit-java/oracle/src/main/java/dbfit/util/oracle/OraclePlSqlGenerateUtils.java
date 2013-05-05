package dbfit.util.oracle;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class OraclePlSqlGenerateUtils {

    public static String callExpr(String func, String args) {
        String ws = (args.trim().length() == 0) ? "" : " ";
        return func + "(" + ws + args + ws + ")";
    }

    public static String getSpCallLeftSide(String var) {
        return (var == null) ? "" : (var + " := ");
    }

    public static String findNonConflictingPrefix(String name) {
        if (name == null) {
            return "a";
        }

        char p = Character.toLowerCase(name.charAt(0));
        char c = 'a';

        while ((c == p) && (c < 'z')) {
            ++c;
        }

        return String.valueOf(c);
    }
}

