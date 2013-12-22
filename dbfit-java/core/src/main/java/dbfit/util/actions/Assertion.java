package dbfit.util.actions;

public abstract class Assertion implements Action {
    public boolean equals(Object a, Object b) {
        if (a == null)
            return (b == null);
        else
            return a.equals(b);
    }
}
