package dbfit.diff;

import dbfit.util.DataTable;
import dbfit.util.DataRow;
import dbfit.util.DataColumn;
import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;
import dbfit.util.DiffListener;
import dbfit.util.DiffListenerAdapter;
import dbfit.util.DiffHandler;
import dbfit.util.RowStructure;
import static dbfit.util.MatchStatus.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import static java.util.Arrays.asList;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataTableDiffTest {

    @Mock private DiffHandler handler;

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

    @SuppressWarnings("unchecked")
    private MatchResult runDiff(ArgumentCaptor<MatchResult> captor,
            DataTable dt1, DataTable dt2) {
        DataTableDiff diff = createDiff(dt1);
        return diff.match(dt2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMismatchWithRightWrongSurplusAndMissing() {
        ArgumentCaptor<MatchResult> captor = createRowCaptor();

        MatchResult res = runDiff(captor,
                createDt(r1, r2, r3), createDt(r1, b2, r4));

        assertFalse(res.isMatching());
        verify(handler, times(4)).endRow(captor.capture());
        List<MatchResult> rowMatches = captor.getAllValues();

        assertEquals(SUCCESS, rowMatches.get(0).getStatus());
        assertEquals(WRONG, rowMatches.get(1).getStatus());
        assertEquals(MISSING, rowMatches.get(2).getStatus());
        assertEquals(SURPLUS, rowMatches.get(3).getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSingleWrongRow() {
        ArgumentCaptor<MatchResult> captor = createRowCaptor();

        MatchResult res = runDiff(captor,
                createDt(r2), createDt(b2));

        verify(handler).endRow(captor.capture());
        assertEquals(WRONG, captor.getValue().getStatus());
        assertFalse(res.isMatching());
    }

    private Map<String, Object> createMatchingMaskR2() {
        DataTable dt1 = createDt(r1, r2);
        DataTableDiff diff = createDiff(dt1);
        return diff.buildMatchingMask(r2);
    }

    @Test
    public void testMatchingMaskR2() {
        Map<String, Object> mask = createMatchingMaskR2();

        assertEquals(1, mask.size());
        assertEquals("2", mask.get("n"));
    }

    @Test
    public void testDataRowStringValueR2() {
        assertEquals("2", r2.getStringValue("n"));
        assertEquals("4", r2.getStringValue("2n"));
    }

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

    private DataTable createDt(DataRow... rows) {
        return new DataTable(asList(rows), createColumns());
    }

    private DataTableDiff createDiff(DataTable t1) {
        return new DataTableDiff(t1, rowStructure,
                new DiffListenerAdapter(handler));
    }

    private ArgumentCaptor<MatchResult> createRowCaptor() {
        return ArgumentCaptor.forClass(MatchResult.class);
    }
}

