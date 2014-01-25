package dbfit.util;

import static dbfit.util.MatchStatus.*;

public class DiffResultsSummarizer implements DiffListener {

    private Class childType;
    private MatchResult result;

    public DiffResultsSummarizer(final MatchResult initialResult, final Class childType) {
        this.childType = childType;
        this.result = initialResult;
        result.setStatus(SUCCESS); // innocent until proven guilty
    }

    private void onChildEvent(final MatchResult childResult) {
        if (getStatus() == EXCEPTION) {
            return; // keep first exception
        }

        switch (childResult.getStatus()) {
        case EXCEPTION:
            result.setException(childResult.getException());
        case WRONG:
        case SURPLUS:
        case MISSING:
            result.setStatus(childResult.getStatus());
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
