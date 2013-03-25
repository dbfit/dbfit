package dbfit.util;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class TypeNormaliserFactory {
    private static Map<Class, TypeNormaliser> normalisers = new HashMap<Class, TypeNormaliser>();

    private static Class getCandidateIfAncestor(Class target, Class candidate) {
        return candidate.isAssignableFrom(target) ? candidate : null;
    }

    private static Class getCandidateIfBetter(Class currentBest, Class nextCandidate) {
        return currentBest.isAssignableFrom(nextCandidate) ? nextCandidate : currentBest;
    }

    private static Class findClosestAncestor(Class targetClass) {
        Class currentBest = null;

        for (Class candidate: normalisers.keySet()) {
            if (currentBest == null) {
                currentBest = getCandidateIfAncestor(targetClass, candidate);
            } else {
                currentBest = getCandidateIfBetter(currentBest, candidate);
            }
        }

        return currentBest;
    }

    public static void setNormaliser(Class targetClass, TypeNormaliser normaliser) {
        normalisers.put(targetClass, normaliser);
    }

    public static TypeNormaliser getNormaliser(Class targetClass) {
        TypeNormaliser normaliser = normalisers.get(targetClass);

        if (normaliser == null) {
            Class bestCandidate = findClosestAncestor(targetClass);

            if (bestCandidate != null) {
                normaliser = normalisers.get(bestCandidate);
                normalisers.put(targetClass, normaliser);
            }
        }

        return normaliser;
    }
}
