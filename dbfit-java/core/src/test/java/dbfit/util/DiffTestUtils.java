package dbfit.util;

import dbfit.util.DataColumn;
import dbfit.util.DataCell;
import dbfit.util.DataRow;
import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;
import dbfit.util.DiffListener;
import static dbfit.util.MatchStatus.*;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import static java.util.Arrays.asList;

public class DiffTestUtils {

    public static List<DataColumn> createColumns(RowStructure rowStructure) {
        List<DataColumn> columns = new LinkedList<DataColumn>();
        for (String s: rowStructure.getColumnNames()) {
            columns.add(new DataColumn(s, s.getClass().getName(), ""));
        }
        return columns;
    }

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

    public static DataRowBuilder createDataRowBuilder() {
        return new DefaultDataRowBuilder();
    }

    public static DataRowBuilder createDataRowBuilder(String... columns) {
        return new NamedDataRowBuilder(columns);
    }

    public static DataRowBuilder createDataRowBuilder(RowStructure rowStr) {
        return createDataRowBuilder(rowStr.getColumnNames());
    }

    public static interface DataRowBuilder {
        public DataRow createRow(Integer... items);
        public DataRow createRow(List items);
    }

    public static DataTable createDataTable(RowStructure rs, DataRow... rows) {
        return new DataTable(asList(rows), createColumns(rs));
    }

    public static abstract class AbstractDataRowBuilder implements DataRowBuilder {
        protected abstract String getColumnName(int index);

        @Override
        public DataRow createRow(Integer... items) {
            return createRow(asList(items));
        }

        @Override
        public DataRow createRow(List items) {
            if (items == null) {
                return null;
            }

            HashMap<String, Object> rowValues = new HashMap<String, Object>();
            int i = 0;

            for (Object item : items) {
                rowValues.put(getColumnName(i++), String.valueOf(item));
            }

            return new DataRow(rowValues);
        }
    }

    public static class DefaultDataRowBuilder extends AbstractDataRowBuilder {
        private String prefix = "c";

        @Override protected String getColumnName(int index) {
            return prefix + index;
        }
    }

    public static class NamedDataRowBuilder extends AbstractDataRowBuilder {
        private String[] columns;

        public NamedDataRowBuilder(final String... columnNames) {
            this.columns = columnNames;
        }

        @Override protected String getColumnName(int index) {
            return columns[index];
        }
    }

    public static List<MatchStatus> statusesOf(List<? extends MatchResult> results) {
        List<MatchStatus> statuses = new LinkedList<>();

        for (MatchResult res : results) {
            statuses.add(res.getStatus());
        }

        return statuses;
    }

}
