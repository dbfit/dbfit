package dbfit.util.crypto;

import java.util.Collection;

import org.junit.Test;
import org.junit.Before;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.rules.TemporaryFolder;
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
            {0, args("-createKeyStore")},
            {0, args("-createKeyStore", emptyFakeKSFolder.getRoot().getPath())},
            {0, args("-encryptPassword", "ABC")},
            {0, args("-encryptPassword", "ABC", "-keyStoreLocation", emptyFakeKSFolder.getRoot().getPath())},
            {0, args("-help")},
            {1, args("-non-existing-command")},
            {1, args("another invalid command")},
            {2, args("-encryptPassword", "too", "many", "args")},
            {2, args("-encryptPassword", "XYZ", "invalid", "args")},
            {2, args("-encryptPassword")},
            {3, args("-createKeyStore", existingFakeKSFolder.getRoot().getPath())}
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

