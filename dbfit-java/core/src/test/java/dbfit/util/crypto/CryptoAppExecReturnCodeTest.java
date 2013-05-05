package dbfit.util.crypto;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;

import static dbfit.util.crypto.CryptoApp.EXIT_INVALID_COMMAND;
import static dbfit.util.crypto.CryptoApp.EXIT_SUCCESS;
import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class CryptoAppExecReturnCodeTest extends CryptoAppTestBase {

    private static TemporaryFolder existingFakeKSFolder = initStaticTemp(true);
    private static TemporaryFolder emptyFakeKSFolder = initStaticTemp(false);

    // Args for parameterized tests
    private final int expectedExitCode;
    private final ArgList appArgs;

    public CryptoAppExecReturnCodeTest(final int expectedExitCode, final ArgList appArgs) {
        this.expectedExitCode = expectedExitCode;
        this.appArgs = appArgs;
    }

    @Test
    public void execReturnCodeShouldBeCorrect() throws Exception {
        assertEquals(expectedExitCode, execApp(appArgs));
    }

    @Parameters(name = "({index}): exec with args {1} -> expecting {0}")
    public static Collection<Object[]> data() throws Exception {
        return java.util.Arrays.asList(new Object[][] {
            {EXIT_SUCCESS, args("ABC")},
            {EXIT_SUCCESS, args("ABC", "-keyStoreLocation", emptyFakeKSFolder.getRoot().getPath())},
            {EXIT_INVALID_COMMAND, args("too", "many", "args")},
            {EXIT_INVALID_COMMAND, args("XYZ", "invalid", "args")},
            {EXIT_INVALID_COMMAND, args()},
        });
    }

    @Before
    public void prepareFakeKSRoots() throws Exception {
        System.setProperty("dbfit.keystore.path", tempKeyStoreFolder2.getRoot().getPath());
    }

    @AfterClass
    public static void cleanup() {
        System.clearProperty("dbfit.keystore.path");
        emptyFakeKSFolder.delete();
        existingFakeKSFolder.delete();
    }

}

