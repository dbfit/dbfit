package dbfit.diff;

import dbfit.api.Diff;
import dbfit.util.MatchResult;
import dbfit.util.DiffResultsSummarizer;

public abstract class CompositeDiff<P, C> extends DiffBase<P, P> {

    protected abstract Class getType();
    protected abstract Class getChildType();

    abstract class CompositeDiffRunner extends DiffRunner {
        protected final P o1;
        protected final P o2;
        protected final DiffResultsSummarizer summer;

        public CompositeDiffRunner(final P o1, final P o2) {
            this.o1 = o1;
            this.o2 = o2;
            this.summer = createSummerizer();
        }

        protected abstract Diff<C, C> newChildDiff();

        protected Diff<C, C> initChildDiff(final Diff<C, C> childDiff) {
            childDiff.addListeners(listeners);
            childDiff.addListener(summer);
            return childDiff;
        }

        protected DiffResultsSummarizer createSummerizer() {
            return new DiffResultsSummarizer(
                    MatchResult.create(o1, o2, getType()), getChildType());
        }

        protected Diff<C, C> createChildDiff() {
            return initChildDiff(newChildDiff());
        }

        @Override
        public MatchResult getResult() {
            return summer.getResult();
        }

    }
}
