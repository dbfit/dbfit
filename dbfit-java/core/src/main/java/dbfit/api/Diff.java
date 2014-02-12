package dbfit.api;

import dbfit.util.DiffListener;

import java.util.Collection;

public interface Diff<T1, T2> {

    public void diff(T1 object1, T2 object2);

    public void addListener(DiffListener listener);

    public void addListeners(Collection<DiffListener> newListeneres);

    public void removeListener(DiffListener listener);

    public void removeListeners(Collection<DiffListener> removed);
}
