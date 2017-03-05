package dbfit.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DbEnvironmentFacadeTest {

    @Mock private DBEnvironment environment;
    @Mock private TestHost testHost;
    private DbEnvironmentFacade environmentFacade;
    private String testStatement = "test";

    @Before
    public void prepare() {
        environmentFacade = new DbEnvironmentFacade(environment, testHost);
    }

    @Test
    public void testCreateStatementWithBoundSymbols() throws Exception {
        environmentFacade.createCommandWithBoundSymbols(testStatement);
        verify(environment).createStatementWithBoundSymbols(testHost, testStatement);
    }

    @Test
    public void testCreateQueryWithBoundSymbols() throws Exception {
        environmentFacade.createQueryWithBoundSymbols(testStatement);
        verify(environment).createStatementWithBoundSymbols(testHost, testStatement);
    }

    @Test
    public void testCreateDdlCommand() throws Exception {
        environmentFacade.createDdlCommand(testStatement);
        verify(environment).createDdlCommand(testStatement);
    }
}

