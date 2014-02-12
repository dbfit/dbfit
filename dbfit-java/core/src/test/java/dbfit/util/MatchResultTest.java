package dbfit.util;

import static dbfit.util.MatchStatus.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MatchResultTest {

    @Mock private DataCell dataCell;

    @Test
    public void testMatchResultType() {
        MatchResult res = MatchResult.create(dataCell, dataCell, DataCell.class);

        assertEquals(DataCell.class, res.getType());
    }

    @Test
    public void setExceptionIsChangingStatusToException() {
        MatchResult res = MatchResult.create(dataCell, dataCell, SUCCESS, DataCell.class);
        res.setException(new Exception());

        assertEquals(EXCEPTION, res.getStatus());
    }

}
