package dbfit.util;

import static dbfit.util.MatchStatus.*;

public class DiffResultsSummarizer implements DiffListener {

    private Class childType;
    private MatchResult result;

    public DiffResultsSummarizer(final MatchResult initialResult, final Class childType) {
        this.childType = childType;
        this.result = initialResult;
        initStatus();
    }

    protected void initStatus() {
        if (result.getObject2() == null) {
            result.setStatus(MISSING);
        } else if (result.getObject1() == null) {
            result.setStatus(SURPLUS);
        } else {
            result.setStatus(SUCCESS);
        }
    }

    protected void onChildEvent(final MatchResult childResult) {
        switch (getStatus()) {
        case EXCEPTION:
        case MISSING:
        case SURPLUS:
            return; // Cannot overwrite exceptions or missing
        }

        switch (childResult.getStatus()) {
        case EXCEPTION:
            result.setException(childResult.getException());
            break;
        case WRONG:
        case SURPLUS:
        case MISSING:
            result.setStatus(WRONG);
            break;
        }
    }

    @Override
    public void onEvent(final MatchResult childResult) {
        if (childType.equals(childResult.getType())) {
            onChildEvent(childResult);
        }
    }

    public MatchResult getResult() {
        return result;
    }

    public MatchStatus getStatus() {
        return result.getStatus();
    }

}
