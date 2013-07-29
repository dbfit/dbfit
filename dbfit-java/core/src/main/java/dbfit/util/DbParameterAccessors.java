package dbfit.util;

import dbfit.fixture.StatementExecution;

import java.sql.SQLException;
import java.util.*;

import static dbfit.util.Direction.INPUT;
import static dbfit.util.Direction.OUTPUT;
import static dbfit.util.Direction.RETURN_VALUE;

public class DbParameterAccessors {
    private List<DbParameterAccessor> accessors;

    public DbParameterAccessors(DbParameterAccessor[] accessors) {
        this.accessors = new ArrayList<DbParameterAccessor>(Arrays.asList(accessors));
    }

    public DbParameterAccessors() {
        this(new DbParameterAccessor[]{});
    }

    public void bindParameters(StatementExecution statement) throws SQLException {
        List<String> accessorNames = getSortedAccessorNames();
        for (DbParameterAccessor ac : accessors) {
            int realindex = accessorNames.indexOf(ac.getName());
            ac.bindTo(statement, realindex + 1); // jdbc params are 1-based
            if (ac.hasDirection(Direction.RETURN_VALUE)) {
                ac.bindTo(statement, Math.abs(ac.getPosition()));
            }
        }
    }

    public DbParameterAccessor[] toArray() {
        return accessors.toArray(new DbParameterAccessor[]{});
    }

    public void add(DbParameterAccessor accessor) {
        accessors.add(accessor);
    }

    public <T> Map<DbParameterAccessor, T> zipWith(List<T> items) {
        Map<DbParameterAccessor, T> map = new HashMap<DbParameterAccessor, T>();
        for (int column = 0; column < accessors.size(); column++) {
            map.put(accessors.get(column), items.get(column));
        }
        return map;
    }

    public List<DbParameterAccessor> getOutputAccessors() {
        List<DbParameterAccessor> filteredAccessors = new ArrayList<DbParameterAccessor>();
        for (DbParameterAccessor accessor : accessors) {
            if (accessor.hasDirection(OUTPUT) || accessor.hasDirection(RETURN_VALUE)) {
                filteredAccessors.add(accessor);
            }
        }
        return filteredAccessors;
    }

    public List<DbParameterAccessor> getInputAccessors() {
        List<DbParameterAccessor> filteredAccessors = new ArrayList<DbParameterAccessor>();
        for (DbParameterAccessor accessor : accessors) {
            if (accessor.hasDirection(INPUT)) {
                filteredAccessors.add(accessor);
            }
        }
        return filteredAccessors;
    }

    private class PositionComparator implements Comparator<DbParameterAccessor> {
        public int compare(DbParameterAccessor o1, DbParameterAccessor o2) {
            return (int) Math.signum(o1.getPosition() - o2.getPosition());
        }
    }

    public List<String> getSortedAccessorNames() {
        List<DbParameterAccessor> newacc = new ArrayList<DbParameterAccessor>(accessors);
        Collections.sort(newacc, new PositionComparator());
        List<String> nameList = new ArrayList<String>();
        String lastName = null;
        for (DbParameterAccessor p : newacc) {
            if (lastName != p.getName()) {
                lastName = p.getName();
                nameList.add(p.getName());
            }
        }
        return nameList;
    }

    public boolean containsReturnValue() {
        for (DbParameterAccessor ac : accessors) {
            if (ac.isReturnValueAccessor()) {
                return true;
            }
        }
        return false;
    }

}

