package dbfit.util;

public class ByteArrayNormaliser implements TypeTransformer {

    @Override
    public Object transform(Object o) {
        if (o == null) {
            return null;
        }
        byte[] ba = (byte[]) o;
        return new Binary(ba);
    }
}
