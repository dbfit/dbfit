package dbfit.matchers;

import dbfit.util.DataTable;
import dbfit.util.DataRow;
import dbfit.util.DataColumn;
import dbfit.util.MatchableDataTable;
import dbfit.util.MatchableDataRow;
import dbfit.util.MatchableDataCell;
import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;
import dbfit.util.DiffListener;
import dbfit.util.RowStructure;
import static dbfit.util.MatchStatus.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import static java.util.Arrays.asList;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompareStoredQueriesMatcherTest {

    @Mock private DiffListener listener;

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
    private MatchResult runMatcher(ArgumentCaptor<MatchResult> captor,
            MatchableDataTable mdt1, MatchableDataTable mdt2) {
        CompareStoredQueriesMatcher matcher = createMatcher(mdt1);
        return matcher.match(mdt2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMismatchWithRightWrongSurplusAndMissing() {
        ArgumentCaptor<MatchResult> captor = createRowCaptor();

        MatchResult res = runMatcher(captor,
                createMdt(r1, r2, r3), createMdt(r1, b2, r4));

        assertFalse(res.isMatching());
        verify(listener, times(4)).endRow(captor.capture());
        List<MatchResult> rowMatches = captor.getAllValues();

        assertEquals(SUCCESS, rowMatches.get(0).getStatus());
        assertEquals(WRONG, rowMatches.get(1).getStatus());
        assertEquals(MISSING, rowMatches.get(2).getStatus());
        assertEquals(SURPLUS, rowMatches.get(3).getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSingleWrongRow() {
        ArgumentCaptor<MatchResult> captor = createRowCaptor();

        MatchResult res = runMatcher(captor,
                createMdt(r2), createMdt(b2));

        verify(listener).endRow(captor.capture());
        assertEquals(WRONG, captor.getValue().getStatus());
        assertFalse(res.isMatching());
    }

    @SuppressWarnings("unchecked")
    private void compareR2B2Cells(String column, boolean expected) {
        CompareStoredQueriesMatcher matcher = createMatcher(createMdt(r2));
        MatchableDataRow mdr1 = new MatchableDataRow(r2, "t1");
        MatchableDataRow mdr2 = new MatchableDataRow(b2, "t2");

        MatchableDataCell c1 = new MatchableDataCell(mdr1, column, "t1");
        MatchableDataCell c2 = new MatchableDataCell(mdr2, column, "t2");

        assertTrue(c1.getStringValue() + " vs " + c2.getStringValue(),
                (expected == matcher.compare(c1, c2)));
    }

    @SuppressWarnings("unchecked")
    private void matchR2B2Cells(String column, MatchStatus expected) {
        ArgumentCaptor<MatchResult> captor = createRowCaptor();
        CompareStoredQueriesMatcher matcher = createMatcher(createMdt(r2));
        MatchableDataRow mdr1 = new MatchableDataRow(r2, "t1");
        MatchableDataRow mdr2 = new MatchableDataRow(b2, "t2");
        MatchResult rowResult = new MatchResult(mdr1, mdr2, UNVERIFIED);

        matcher.matchCell(mdr1, mdr2, column, rowResult);

        verify(listener).endCell(captor.capture());
        assertEquals(expected, captor.getValue().getStatus());
    }

    @Test
    public void testMatchOfDiferentCells() {
        matchR2B2Cells("2n", WRONG);
    }

    @Test
    public void testMatchOfEqualCells() {
        matchR2B2Cells("n", SUCCESS);
    }

    @Test
    public void testCompareOfDifferentCells() {
        compareR2B2Cells("2n", false);
    }

    @Test
    public void testCompareOfEqualCells() {
        compareR2B2Cells("n", true);
    }

    private Map<String, Object> createMatchingMaskR2() {
        MatchableDataTable mdt1 = createMdt(r1, r2);
        CompareStoredQueriesMatcher matcher = createMatcher(mdt1);
        return matcher.buildMatchingMask(r2);
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

}

