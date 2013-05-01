package dbfit.util.crypto;

import java.util.Collection;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.rules.TemporaryFolder;
import org.junit.Rule;


@RunWith(Parameterized.class)
public class CryptoAppExecReturnCodeTest extends CryptoAppTestBase {
    // Args for parameterized tests
    private final int expectedExitCode;
    private final ArgList appArgs;

    public CryptoAppExecReturnCodeTest(final int expectedExitCode, final ArgList appArgs) {
        this.expectedExitCode = expectedExitCode;
        this.appArgs = appArgs;
    }

    @Test
    public void execReturnCodeShouldBeCorrect() throws Exception {
        int res = execApp(appArgs.args);
        assertEquals(expectedExitCode, res);
    }

    @Parameters(name = "{index}: exec with args {1} -> expecting {0}")
    public static Collection<Object[]> data() throws Exception {
        TemporaryFolder tmpDir = new TemporaryFolder();

        Object[][] data = new Object[][] {
            {0, args("-createKeyStore")},
            {0, args("-createKeyStore", System.getProperty("java.io.tmpdir"))},
            {0, args("-encryptPassword", "ABC")},
            {0, args("-help")},
            {1, args("-non-existing-command")},
            {1, args("another invalid command")},
            {2, args("-encryptPassword", "too", "many", "args")},
            {2, args("-encryptPassword")},
            {2, args("-createKeyStore", "too", "many")}
        };

        return java.util.Arrays.asList(data);
    }
}

