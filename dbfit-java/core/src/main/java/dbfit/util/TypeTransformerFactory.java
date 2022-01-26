package dbfit.util;

import java.util.HashMap;
import java.util.Map;

public class TypeTransformerFactory {
    private Map<Class<?>, TypeTransformer> transformers = new HashMap<Class<?>, TypeTransformer>();

    private Class<?> getCandidateIfAncestor(Class<?> target, Class<?> candidate) {
        return candidate.isAssignableFrom(target) ? candidate : null;
    }

    private Class<?> getCandidateIfBetter(Class<?> currentBest, Class<?> nextCandidate) {
        return currentBest.isAssignableFrom(nextCandidate) ? nextCandidate : currentBest;
    }

    private Class<?> findClosestAncestor(Class<?> targetClass) {
        Class<?> currentBest = null;

        for (Class<?> candidate: transformers.keySet()) {
            if (currentBest == null) {
                currentBest = getCandidateIfAncestor(targetClass, candidate);
            } else {
                currentBest = getCandidateIfBetter(currentBest, candidate);
            }
        }

        return currentBest;
    }

    public void setTransformer(Class<?> targetClass, TypeTransformer normaliser) {
        transformers.put(targetClass, normaliser);
    }

    public TypeTransformer getTransformer(Class<?> targetClass) {
        TypeTransformer normaliser = transformers.get(targetClass);

        if (normaliser == null) {
            Class<?> bestCandidate = findClosestAncestor(targetClass);

            if (bestCandidate != null) {
                normaliser = transformers.get(bestCandidate);
                transformers.put(targetClass, normaliser);
            }
        }

        return normaliser;
    }
}
