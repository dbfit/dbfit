package dbfit.api;

public interface Diff<T1, T2> {

    public void diff(T1 object1, T2 object2);
}
