package dbfit.util;

import java.util.ArrayList;
import java.util.List;

public class LangUtils {
    public static <T> List<T> repeat(T element, int number) {
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < number; i++) {
            list.add(element);
        }
        return list;
    }

    public static String join(List<String> list, String separator) {
        return enquoteAndJoin(list, separator, "", "");
    }

    public static String enquoteAndJoin(Iterable<String> list,
            String separator, String prefix, String suffix) {
        StringBuilder s = new StringBuilder();
        String sep = "";
        for (String item: list) {
            s.append(sep).append(prefix).append(item).append(suffix);
            sep = separator;
        }
        return s.toString();
    }

    public static String enquoteAndJoin(String[] list,
            String separator, String prefix, String suffix) {
        return enquoteAndJoin(java.util.Arrays.asList(list), separator, prefix, suffix);
    }
}
