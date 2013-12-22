package dbfit.util.actions;

import dbfit.util.*;

import java.util.Arrays;
import java.util.List;

public class MostAppropriateAction implements Action {
    public List<? extends Action> possibleActions = Arrays.asList(
            new SaveActualValueToSymbolAction(),
            new StoredValueEqualsSpecifiedValueAssertion(),
            new ActualValueDoesNotEqualSpecifiedValueAssertion(),
            new DisplayActualValueAction(),
            new ActualValueEqualsSpecifiedValueAssertion(),
            new AssignSpecifiedValueToAccessor(),
            new AssignStoredValueToAccessor());

    public void run(Cell cell) {
        try {
            Action mostAppropriateAction = getMostAppropriateAction(cell);
            if (mostAppropriateAction != null) {
                mostAppropriateAction.run(cell);
            } else {
                noSuitableActionFoundFor(cell);
            }
        }
        catch (Throwable t){
            cell.getTestResultHandler().exception(t);
        }
    }

    public boolean appliesTo(Cell cell) {
        return true;
    }

    private Action getMostAppropriateAction(Cell cell) {
        for (Action action : possibleActions) {
            if (action.appliesTo(cell)) return action;
        }
        return null;
    }

    private void noSuitableActionFoundFor(Cell cell) throws IllegalArgumentException {
        String cellDirection = (cell.isInput() ? "input" : "output or return value");
        throw new IllegalArgumentException("Unexpected text [" + cell.getSpecifiedText() +
                "] specified in " + cellDirection + " cell. No suitable action found.");
    }
}
