package dbfit.diff;

import dbfit.util.MatchResult;
import dbfit.util.MatchStatus;
import dbfit.util.MatcherListener;
import dbfit.util.RowStructure;
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

    @Mock private MatcherListener listener;
    @Mock private DataCell c1;
    @Mock private DataCell c2;

    private ArgumentCaptor<MatchResult> captor =
        ArgumentCaptor.forClass(MatchResult.class);

    private void runDiff() {
        new DataCellDiff(c1, listener).diff(c2);
        verify(listener).endCell(captor.capture());
    }

    @Test
    public void diffOfDifferentCellsShouldEmitWrongEvent() {
        when(c1.toString()).thenReturn("1");
        when(c2.toString()).thenReturn("2");

        runDiff();

        assertEquals(WRONG, captor.getValue().getStatus());
    }

    @Test
    public void diffOfEqualCellsShouldEmitSuccessEvent() {
        when(c1.toString()).thenReturn("3");
        when(c2.toString()).thenReturn("3");

        runDiff();

        assertEquals(SUCCESS, captor.getValue().getStatus());
    }

}

