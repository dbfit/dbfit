package dbfit.util;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class TypeNormaliserFactory {
    private static Map<Class, TypeNormaliser> normalisers = new HashMap<Class, TypeNormaliser>();

    public static void setNormaliser(Class targetClass, TypeNormaliser normaliser) {
        normalisers.put(targetClass, normaliser);
    }

    public static TypeNormaliser getNormaliser(Class targetClass) {
        TypeNormaliser normaliser = normalisers.get(targetClass);

        if (normaliser == null) {
            Class bestCandidate = null;
            for (Class c: normalisers.keySet()) {
                if (bestCandidate == null) {
                    if (c.isAssignableFrom(targetClass)) {
                        bestCandidate = c;
                    }
                } else if (bestCandidate.isAssignableFrom(c)) {
                    bestCandidate = c;
                }
            }

            if (bestCandidate != null) {
                normaliser = normalisers.get(bestCandidate);
                normalisers.put(targetClass, normaliser);
            }
        }

        return normaliser;
    }
}
