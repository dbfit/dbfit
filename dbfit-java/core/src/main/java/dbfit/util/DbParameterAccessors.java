package dbfit.util;

import dbfit.fixture.StatementExecution;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class DbParameterAccessors {
    private ParameterOrColumn[] accessors;

    public DbParameterAccessors(ParameterOrColumn[] accessors) {
        this.accessors = accessors;
    }

    public void bindParameters(StatementExecution statement) throws SQLException {
        List<String> accessorNames = getSortedAccessorNames();
        for (ParameterOrColumn ac : accessors) {
            int realindex = accessorNames.indexOf(ac.getName());
            ac.bindTo(statement, realindex + 1); // jdbc params are 1-based
            if (ac.hasDirection(Direction.RETURN_VALUE)) {
                ac.bindTo(statement, Math.abs(ac.getPosition()));
            }
        }
    }

    private class PositionComparator implements Comparator<ParameterOrColumn> {
        public int compare(ParameterOrColumn o1, ParameterOrColumn o2) {
            return (int) Math.signum(o1.getPosition() - o2.getPosition());
        }
    }

    public List<String> getSortedAccessorNames() {
        ParameterOrColumn[] newacc = new ParameterOrColumn[accessors.length];
        System.arraycopy(accessors, 0, newacc, 0, accessors.length);
        Arrays.sort(newacc, new PositionComparator());
        List<String> nameList = new ArrayList<String>();
        String lastName = null;
        for (ParameterOrColumn p : newacc) {
            if (lastName != p.getName()) {
                lastName = p.getName();
                nameList.add(p.getName());
            }
        }
        return nameList;
    }

    public boolean containsReturnValue() {
        for (ParameterOrColumn ac : accessors) {
            if (ac.isReturnValueAccessor()) {
                return true;
            }
        }
        return false;
    }

}

