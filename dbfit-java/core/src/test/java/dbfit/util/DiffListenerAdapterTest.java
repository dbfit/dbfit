package dbfit.util;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DiffListenerAdapterTest {

    private DiffListenerAdapter adapter;
    private DiffHandler handler;

    @Before
    public void prepare() {
        handler = Mockito.mock(DiffHandler.class);
        adapter = DiffListenerAdapter.from(handler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldDelegateToEndCell() {
        MatchResult res = createResult(DataCell.class);

        adapter.onEvent(res);

        verify(handler).endCell(res);
        verify(handler, never()).endRow(any(MatchResult.class));
        verify(handler, never()).endTable(any(MatchResult.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldDelegatetoEndRow() {
        MatchResult res = createResult(DataRow.class);

        adapter.onEvent(res);

        verify(handler).endRow(res);
        verify(handler, never()).endCell(any(MatchResult.class));
        verify(handler, never()).endTable(any(MatchResult.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldDelegateToEndTable() {
        MatchResult res = createResult(DataTable.class);

        adapter.onEvent(res);

        verify(handler).endTable(res);
        verify(handler, never()).endCell(any(MatchResult.class));
        verify(handler, never()).endRow(any(MatchResult.class));
    }

    private <T> MatchResult<T, T> createResult(final Class<T> cls) {
        return MatchResult.create(Mockito.mock(cls), Mockito.mock(cls), cls);
    }
}
