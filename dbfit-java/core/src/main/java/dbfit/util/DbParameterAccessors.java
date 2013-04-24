package dbfit.util;

import java.util.*;

public class DbParameterAccessors {
    private DbParameterAccessor[] accessors;

    public DbParameterAccessors(DbParameterAccessor[] accessors) {
        this.accessors = accessors;
    }

    private class PositionComparator implements Comparator<DbParameterAccessor> {
        public int compare(DbParameterAccessor o1, DbParameterAccessor o2) {
            return (int) Math.signum(o1.getPosition() - o2.getPosition());
        }
    }

    public List<String> getSortedAccessorNames() {
        DbParameterAccessor[] newacc = new DbParameterAccessor[accessors.length];
        System.arraycopy(accessors, 0, newacc, 0, accessors.length);
        Arrays.sort(newacc, new PositionComparator());
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

