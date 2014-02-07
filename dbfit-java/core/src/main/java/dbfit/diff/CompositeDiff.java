package dbfit.diff;

import dbfit.api.Diff;
import dbfit.util.MatchResult;
import dbfit.util.DiffListener;
import dbfit.util.DiffResultsSummarizer;

import java.util.Collection;

public abstract class CompositeDiff<P, C> extends DiffBase<P, P> {

    protected Diff<C, C> childDiff;

    protected abstract Class getType();
    protected abstract Class getChildType();

    public CompositeDiff(final Diff<C, C> childDiff) {
        this.childDiff = childDiff;
    }

    protected Diff<C, C> getChildDiff() {
        return childDiff;
    }

    abstract class CompositeDiffRunner extends DiffRunner {
        protected final P o1;
        protected final P o2;
        protected final DiffResultsSummarizer summer;

        public CompositeDiffRunner(final P o1, final P o2) {
            this.o1 = o1;
            this.o2 = o2;
            this.summer = createSummerizer();
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

        @Override
        public MatchResult getResult() {
            return summer.getResult();
        }

        protected DiffResultsSummarizer createSummerizer() {
            return new DiffResultsSummarizer(
                    MatchResult.create(o1, o2, getType()), getChildType());
        }
    }
}
