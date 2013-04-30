package dbfit.util;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

public class MockitoTestBase {
    @Before public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
