package dbfit.diff;

import dbfit.util.MatchResult;
import dbfit.util.DiffListener;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;

public class DiffBase {

    protected Collection<DiffListener> listeners;

    public DiffBase(final Collection<DiffListener> listeners) {
        this.listeners = listeners;
    }

    public DiffBase(final DiffListener... listeners) {
        this(new ArrayList<DiffListener>(Arrays.asList(listeners)));
    }

    public void addListener(final DiffListener listener) {
        listeners.add(listener);
    }

    public void removeListener(final DiffListener listener) {
        listeners.remove(listener);
    }

    public void addListeners(final Collection<DiffListener> newListeneres) {
        listeners.addAll(newListeneres);
    }

    public void removeListeners(final Collection<DiffListener> removed) {
        listeners.removeAll(removed);
    }

    public void clearListeners() {
        listeners.clear();
    }

    protected void notifyListeners(final MatchResult result) {
        for (DiffListener listener: listeners) {
            listener.onEvent(result);
        }
    }

}
