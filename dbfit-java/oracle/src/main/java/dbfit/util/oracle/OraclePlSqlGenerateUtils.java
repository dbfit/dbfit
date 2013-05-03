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
}

