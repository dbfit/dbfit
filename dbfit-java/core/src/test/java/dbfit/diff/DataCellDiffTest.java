package dbfit.diff;

import dbfit.util.MatchResult;
import dbfit.util.DataCell;
import dbfit.util.DiffListenerAdapter;
import dbfit.util.DiffHandler;
import static dbfit.util.MatchStatus.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataCellDiffTest {

    @Mock private DiffHandler handler;
    @Mock private DataCell c1;
    @Mock private DataCell c2;

    private ArgumentCaptor<MatchResult> captor =
        ArgumentCaptor.forClass(MatchResult.class);

    private MatchResult getResult() {
        return captor.getValue();
    }

    @SuppressWarnings("unchecked")
    private void runDiff() {
        DataCellDiff diff = new DataCellDiff();
        diff.addListener(new DiffListenerAdapter(handler));

        diff.diff(c1, c2);

        verify(handler).endCell(captor.capture());
    }

    @Test
    public void diffOfDifferentCellsShouldEmitWrongEvent() {
        when(c1.equalTo(c2)).thenReturn(false);

        runDiff();

        assertThat(getResult().getStatus(), is(WRONG));
    }

    @Test
    public void diffOfEqualCellsShouldEmitSuccessEvent() {
        when(c1.equalTo(c2)).thenReturn(true);

        runDiff();

        assertThat(getResult().getStatus(), is(SUCCESS));
    }

    @Test
    public void diffWithNullSecondShouldEmitMissingEvent() {
        c2 = null;

        runDiff();

        assertThat(getResult().getStatus(), is(MISSING));
    }

    @Test
    public void diffWithNullFirstShouldEmitSurplusEvent() {
        c1 = null;

        runDiff();

        assertThat(getResult().getStatus(), is(SURPLUS));
    }

    @Test
    public void diffWithBothNullsShouldEmitExceptionEvent() {
        c1 = null;
        c2 = null;

        runDiff();

        assertThat(getResult().getStatus(), is(EXCEPTION));
        assertThat(getResult().getException(),
                instanceOf(IllegalArgumentException.class));
    }

    @Test
    public void exceptionOnComparisonShouldBeEmittedAsExceptionEvent() {
        Exception ex = new RuntimeException("Cruel World!");
        when(c1.equalTo(c2)).thenThrow(ex);

        runDiff();

        assertThat(getResult().getStatus(), is(EXCEPTION));
        assertThat(captor.getValue().getException(), is(ex));
    }

}
