package dbfit.matchers;

import dbfit.util.DataTable;
import dbfit.util.DataRow;
import dbfit.util.DataColumn;
import dbfit.util.MatchableDataTable;
import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;
import dbfit.util.MatcherListener;
import dbfit.util.RowStructure;
import static dbfit.util.MatchStatus.*;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import static java.util.Arrays.asList;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompareStoredQueriesMatcherTest {

    @Mock private MatcherListener listener;

    private dbfit.util.RowStructure rowStructure = new RowStructure(
            new String[] { "n", "2n" }, /* names */
            new boolean[] { true, false } /* keys */
        );

    DataRow r1 = createRow(1, 2);
    DataRow r2 = createRow(2, 4);
    DataRow r3 = createRow(3, 6);
    DataRow r4 = createRow(4, 8);
    DataRow r5 = createRow(5, 10);

    DataRow b2 = createRow(2, 44);

    private DataRow createRow(int... items) {
        HashMap<String, Object> rowValues = new HashMap<String, Object>();
        int i = 0;
        for (Integer item: items) {
            rowValues.put(rowStructure.getColumnName(i++), item.toString());
        }
        return new DataRow(rowValues);
    }

    private List<DataColumn> createColumns() {
        List<DataColumn> columns = new LinkedList<DataColumn>();
        for (String s: rowStructure.getColumnNames()) {
            columns.add(new DataColumn(s, s.getClass().getName(), ""));
        }
        return columns;
    }

    private MatchableDataTable createMdt(DataRow... rows) {
        return new MatchableDataTable(
                new DataTable(asList(rows), createColumns()));
    }

    private CompareStoredQueriesMatcher createMatcher(MatchableDataTable t1) {
        return new CompareStoredQueriesMatcher(t1, rowStructure, listener);
    }

    private ArgumentCaptor<MatchResult> createRowCaptor() {
        return ArgumentCaptor.forClass(MatchResult.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMismatchWithRightWrongSurplusAndMissing() {
        MatchableDataTable mdt1 = createMdt(r1, r2, r3);
        MatchableDataTable mdt2 = createMdt(r1, b2, r4);
        CompareStoredQueriesMatcher matcher = createMatcher(mdt1);
        ArgumentCaptor<MatchResult> captor = createRowCaptor();

        MatchResult res = matcher.match(mdt2);

        assertFalse(res.isMatching());
        verify(listener, times(4)).endRow(captor.capture());
        List<MatchResult> rowMatches = captor.getAllValues();
        assertEquals(SUCCESS, rowMatches.get(0).getStatus());
        assertEquals(WRONG, rowMatches.get(1).getStatus());
        assertEquals(MISSING, rowMatches.get(2).getStatus());
        assertEquals(SURPLUS, rowMatches.get(3).getStatus());
        assertEquals(4, rowMatches.size());
    }

}

