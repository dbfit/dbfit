package dbfit.diff;

import dbfit.api.Diff;
import dbfit.util.MatchResult;
import dbfit.util.DiffListener;

import java.util.Collection;
import java.util.ArrayList;

public abstract class DiffBase<T1, T2> implements Diff<T1, T2> {

    protected Collection<DiffListener> listeners;

    public DiffBase(final Collection<DiffListener> listeners) {
        this.listeners = listeners;
    }

    public DiffBase() {
        this(new ArrayList<DiffListener>());
    }

    protected abstract DiffRunner getDiffRunner(T1 object1, T2 object2);

    @Override
    public void diff(final T1 object1, final T2 object2) {
        getDiffRunner(object1, object2).runDiff();
    }

    @Override
    public void addListener(final DiffListener listener) {
        listeners.add(listener);
    }

    @Override
    public void addListeners(final Collection<DiffListener> newListeneres) {
        listeners.addAll(newListeneres);
    }

    protected void notifyListeners(final MatchResult result) {
        for (DiffListener listener: listeners) {
            listener.onEvent(result);
        }
    }

    protected abstract class DiffRunner {
        abstract public MatchResult getResult();
        abstract protected void uncheckedDiff();

        public void runDiff() {
            try {
                uncheckedDiff();
            } catch (Exception ex) {
                getResult().setException(ex);
            } finally {
                notifyListeners(getResult());
            }
        }
    }

}
