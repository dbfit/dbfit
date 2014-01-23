package dbfit.diff;

import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;
import dbfit.util.DiffListener;
import dbfit.util.DataCell;
import static dbfit.util.MatchStatus.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)

public class DataCellDiffTest {

    @Mock private DiffListener listener;
    @Mock private DataCell c1;
    @Mock private DataCell c2;

    private ArgumentCaptor<MatchResult> captor =
        ArgumentCaptor.forClass(MatchResult.class);

    @SuppressWarnings("unchecked")
    private void runDiff() {
        DataCellDiff diff = new DataCellDiff();
        diff.addListener(listener);

        diff.diff(c1, c2);

        verify(listener).endCell(captor.capture());
    }

    @Test
    public void diffOfDifferentCellsShouldEmitWrongEvent() {
        when(c1.equalTo(c2)).thenReturn(false);

        runDiff();

        assertEquals(WRONG, captor.getValue().getStatus());
    }

    @Test
    public void diffOfEqualCellsShouldEmitSuccessEvent() {
        when(c1.equalTo(c2)).thenReturn(true);

        runDiff();

        assertEquals(SUCCESS, captor.getValue().getStatus());
    }

    @Test
    public void diffWithNullSecondShouldEmitMissingEvent() {
        c2 = null;

        runDiff();

        assertEquals(MISSING, captor.getValue().getStatus());
    }

    @Test
    public void diffWithNullFirstShouldEmitSurplusEvent() {
        c1 = null;

        runDiff();

        assertEquals(SURPLUS, captor.getValue().getStatus());
    }

}
