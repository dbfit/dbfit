package dbfit.util;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class TypeNormaliserFactory {
	//private static Map<Class, TypeNormaliser> normalisers = new HashMap<Class, TypeNormaliser>();
	private static Map<String, TypeNormaliser> normalisers = new HashMap<String, TypeNormaliser>();

	public static void setNormaliser(Class targetClass, TypeNormaliser normaliser) {
		normalisers.put(targetClass.getCanonicalName(), normaliser);
	}

	public static void setNormaliser(String targetClassName, TypeNormaliser normaliser) {
		normalisers.put(targetClassName, normaliser);
	}

	public static TypeNormaliser getNormaliser(Class targetClass) {
		TypeNormaliser normaliser = normalisers.get(targetClass.getCanonicalName());

		/*
		if (normaliser == null) {
			Class bestCandidate = targetClass;
			for (Class c: normalisers.keySet()) {
				if (bestCandidate.isAssignableFrom(c)) {
					bestCandidate = c;
				}
			}

			if (bestCandidate != targetClass) {
				normaliser = normalisers.get(bestCandidate);
			}
		}
		*/

		return normaliser;
	}
}
