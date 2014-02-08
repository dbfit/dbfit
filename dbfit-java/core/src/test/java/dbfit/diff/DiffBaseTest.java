package dbfit.diff;

import dbfit.util.DiffListener;
import dbfit.util.MatchResult;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DiffBaseTest {

    @Mock private DiffListener listener1;
    @Mock private DiffListener listener2;
    @Mock private MatchResult matchResult;
    private DiffBase<Object, Object> diffBase;

    @Before
    public void prepare() {
        diffBase = new DiffBase<Object, Object>() {
            @Override
            protected Class getType() {
                return Object.class;
            }

            @Override
            protected DiffBase<Object, Object>.DiffRunner getDiffRunner(MatchResult<Object, Object> request) {
                return null;
            }
        };
        diffBase.addListener(listener1);
        diffBase.addListener(listener2);
    }

    @Test
    public void shouldNotifyAllRegisteredListener() {
        diffBase.notifyListeners(matchResult);
        verify(listener1).onEvent(matchResult);
        verify(listener2).onEvent(matchResult);
    }

    @Test
    public void shouldNotNotifyUnregisteredListeners() {
        diffBase.removeListener(listener1);

        diffBase.notifyListeners(matchResult);

        verifyZeroInteractions(listener1);
        verify(listener2).onEvent(matchResult);
    }
}
