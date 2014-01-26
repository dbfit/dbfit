package dbfit.util;

import dbfit.util.DataCell;
import dbfit.util.DataRow;
import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;
import dbfit.util.DiffListener;
import static dbfit.util.MatchStatus.*;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class DiffTestUtils {

    /*------ MatchResult Setup helpers ----- */

    public static MatchResult createCellResultSuccess(String val) {
        return createCellResult(val, val, SUCCESS, null);
    }

    public static MatchResult createCellResultWrong(String s1, String s2) {
        return createCellResult(s1, s2, WRONG, null);
    }

    public static MatchResult createCellException(String s1, String s2, Exception e) {
        return createCellResult(s1, s2, EXCEPTION, e);
    }

    public static MatchResult createCellResult(String s, MatchStatus status) {
        return createCellResult(s, s, status, null);
    }

    public static MatchResult createCellResult(final String s1, final String s2,
                                               final MatchStatus status) {
        return createCellResult(s1, s2, status, null);
    }

    @SuppressWarnings("unchecked")
    public static MatchResult createCellResult(final String s1, final String s2,
            final MatchStatus status, final Exception ex) {
        DataCell o1 = fakeDataCell(s1);
        DataCell o2 = fakeDataCell(s2);

        MatchResult res = new MatchResult(o1, o2, status, DataCell.class, ex);

        return res;
    }

    public static MatchResult createNullRowResult(final MatchStatus status) {
        return createRowResult(null, null, status, null);
    }

    public static MatchResult createRowResult(String s, final MatchStatus status) {
        return createRowResult(s, s, status, null);
    }

    public static MatchResult createRowResult(final String s1, final String s2,
                                               final MatchStatus status) {
        return createRowResult(s1, s2, status, null);
    }

    @SuppressWarnings("unchecked")
    public static MatchResult createRowResult(final String s1, final String s2,
            final MatchStatus status, final Exception ex) {
        DataRow o1 = fakeDataRow(s1);
        DataRow o2 = fakeDataRow(s2);

        MatchResult res = new MatchResult(o1, o2, status, DataRow.class, ex);

        return res;
    }

    public static DataCell fakeDataCell(final String stringValue) {
        return new DataCell(null, null) {
            @Override public String toString() { return stringValue; }
        };
    }

    public static DataRow fakeDataRow(final String stringValue) {
        return new DataRow(null) {
            @Override public String toString() { return stringValue; }
        };
    }
}
