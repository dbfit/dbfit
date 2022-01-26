package dbfit.util;

public class TypeNormaliserFactory {
    private static TypeTransformerFactory normaliserFactory = new TypeTransformerFactory();

    public static void setNormaliser(Class<?> targetClass, TypeTransformer normaliser) {
        normaliserFactory.setTransformer(targetClass, normaliser);
    }

    public static TypeTransformer getNormaliser(Class<?> targetClass) {
        return normaliserFactory.getTransformer(targetClass);
    }
}
