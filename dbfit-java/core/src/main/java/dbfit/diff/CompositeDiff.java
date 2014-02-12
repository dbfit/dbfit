package dbfit.diff;

import dbfit.api.Diff;
import dbfit.util.MatchResult;
import dbfit.util.DiffResultsSummarizer;

public abstract class CompositeDiff<P, C> extends DiffBase<P, P> {

    protected Diff<C, C> childDiff;

    protected abstract Class getChildType();

    public CompositeDiff(final Diff<C, C> childDiff) {
        this.childDiff = childDiff;
    }

    protected Diff<C, C> getChildDiff() {
        return childDiff;
    }

    abstract class CompositeDiffRunner extends DiffRunner {
        protected final DiffResultsSummarizer summer;

        public CompositeDiffRunner(MatchResult<P, P> request) {
            super(request);
            this.summer = new DiffResultsSummarizer(request, getChildType());
        }

        @Override
        public void beforeDiff() {
            getChildDiff().addListeners(listeners);
            getChildDiff().addListener(summer);
        }

        @Override
        public void afterDiff() {
            getChildDiff().removeListener(summer);
            getChildDiff().removeListeners(listeners);
        }
    }
}
