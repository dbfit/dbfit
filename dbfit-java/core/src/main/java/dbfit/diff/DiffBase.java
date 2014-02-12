package dbfit.diff;

import dbfit.api.Diff;
import dbfit.util.MatchResult;
import dbfit.util.DiffListener;

import java.util.Collection;
import java.util.ArrayList;

public abstract class DiffBase<T1, T2> implements Diff<T1, T2> {

    protected Collection<DiffListener> listeners;

    protected abstract Class getType();

    protected abstract DiffRunner getDiffRunner(MatchResult<T1, T2> request);

    public DiffBase(final Collection<DiffListener> listeners) {
        this.listeners = listeners;
    }

    public DiffBase() {
        this(new ArrayList<DiffListener>());
    }

    @Override
    public void diff(final T1 object1, final T2 object2) {
        diff(MatchResult.create(object1, object2, getType()));
    }

    public void diff(final MatchResult<T1, T2> request) {
        getDiffRunner(request).runDiff();
    }

    @Override
    public void addListener(final DiffListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(final DiffListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void addListeners(final Collection<DiffListener> newListeneres) {
        listeners.addAll(newListeneres);
    }

    @Override
    public void removeListeners(final Collection<DiffListener> removed) {
        listeners.removeAll(removed);
    }

    protected void notifyListeners(final MatchResult result) {
        for (DiffListener listener: listeners) {
            listener.onEvent(result);
        }
    }

    protected abstract class DiffRunner {
        protected final T1 obj1;
        protected final T2 obj2;
        protected final MatchResult<T1, T2> result;

        abstract protected void uncheckedDiff();

        public DiffRunner(final MatchResult<T1, T2> result) {
            this.result = result;
            this.obj1 = result.getObject1();
            this.obj2 = result.getObject2();
        }

        public void runDiff() {
            try {
                beforeDiff();
                uncheckedDiff();
            } catch (Exception ex) {
                getResult().setException(ex);
            } finally {
                afterDiff();
                notifyListeners(getResult());
            }
        }

        public final MatchResult getResult() {
            return result;
        }

        protected void beforeDiff() {}
        protected void afterDiff() {}
    }

}
