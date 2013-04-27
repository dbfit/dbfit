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
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            s.append(list.get(i));
            if (i < list.size() - 1)
                s.append(separator);
        };
        return s.toString();
    }
}
