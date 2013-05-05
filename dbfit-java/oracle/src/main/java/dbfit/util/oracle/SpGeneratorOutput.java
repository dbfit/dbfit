package dbfit.util.oracle;

public class SpGeneratorOutput {
    protected StringBuilder sb = new StringBuilder();

    public SpGeneratorOutput append(String s) {
        sb.append(s);
        return this;
    }

    public String toString() {
        return sb.toString();
    }
}

