package dbfit.test.acceptance;

import fitnesse.junit.TestHelper;
import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import java.io.*;

import org.hamcrest.core.SubstringMatcher;
import org.hamcrest.Matcher;

public class ExceptionMessageTest {
    private static final String TEST_NAME = "DbFit.AcceptanceTests.JavaTests.OracleTests.IndividualTests.NonExistentColumnExceptionTest";
    private static final String EXPECTED_PATTERN = "No such database column or parameter: 'non_existent_column'";

    private TestHelper helper;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void prepare() {
        helper = new TestHelper("../..",
            temporaryFolder.getRoot().getAbsolutePath());
    }
        
    @Test
    public void exceptionMessageOnNonExistentColumShouldIncludeColumnName() throws Exception {
        helper.run(TEST_NAME, TestHelper.PAGE_TYPE_TEST, null, 1234);
        String outputHtml = readOutputHtml("utf8");

        assertThat(outputHtml, containsStringIgnoringCase(EXPECTED_PATTERN));
    }

    private String readOutputHtml(String encoding) throws Exception {
        Reader reader = new InputStreamReader(getHtmlOutputStream(), encoding);
        StringBuilder result = new StringBuilder();
        char[] buffer = new char[1000];
        while (reader.read(buffer) > 0) {
            result.append(buffer);
        }
        return result.toString();
    }

    private InputStream getHtmlOutputStream() throws FileNotFoundException {
        File outputHtml = new File(temporaryFolder.getRoot(), TEST_NAME + ".html");
        return new FileInputStream(outputHtml);
    }

    private static Matcher<String> containsStringIgnoringCase(String substring) {
        return new SubstringMatcher(substring) {
            @Override protected boolean evalSubstringOf(String s) {
                return s.toLowerCase().indexOf(substring.toLowerCase()) >= 0;
            }

            @Override protected String relationship() {
                return "contains ignoring case";
            }
        };
    }
}

