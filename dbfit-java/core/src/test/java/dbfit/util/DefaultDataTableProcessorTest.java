package dbfit.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

import static java.util.Arrays.asList;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDataTableProcessorTest {

    @Mock DataRow r1, r2;
    @Mock DataRowProcessor mockedChildProcessor;
    @Captor ArgumentCaptor<DataRow> captor;

    @Test
    public void shouldInvokeProcessOnEachChild() {
        DataTable dt = createDt(r1, r2);

        new DefaultDataTableProcessor(mockedChildProcessor).process(dt);

        verify(mockedChildProcessor, times(2)).process(captor.capture());
        assertThat(captor.getAllValues(), contains(r1, r2));
    }

    private DataTable createDt(DataRow... rows) {
        DataTable dt = mock(DataTable.class);
        when(dt.getRows()).thenReturn(asList(rows));
        return dt;
    }
}
