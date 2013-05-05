package dbfit.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static dbfit.util.DbParameterAccessor.Direction;

public class DbParameterAccessors {
    private DbParameterAccessor[] accessors;

    public DbParameterAccessors(DbParameterAccessor[] accessors) {
        this.accessors = accessors;
    }

    public void bindParameters(PreparedStatement statement) throws SQLException {
        List<String> accessorNames = getSortedAccessorNames();
        for (DbParameterAccessor ac : accessors) {
            int realindex = accessorNames.indexOf(ac.getName());
            ac.bindTo(statement, realindex + 1); // jdbc params are 1-based
            if (ac.getDirection() == Direction.RETURN_VALUE) {
                ac.bindTo(statement, Math.abs(ac.getPosition()));
            }
        }
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

