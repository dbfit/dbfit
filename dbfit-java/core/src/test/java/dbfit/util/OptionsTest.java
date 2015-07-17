package dbfit.util;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

public class OptionsTest {

    @Before
    public void prepare() {
        Options.reset();
    }

    @Test
    public void debugLogDefaultIsFalse() {
        assertThat(Options.isDebugLog(), is(false));
    }

    @Test
    public void fixedLengthStringParsingDefaultIsFalse() {
        assertThat(Options.isFixedLengthStringParsing(), is(false));
    }

    @Test
    public void bindSymbolsDefaultIsTrue() {
        assertThat(Options.isBindSymbols(), is(true));
    }

    @Test
    public void autoCommitDefaultIsFalse() {
        assertThat(Options.get(Options.OPTION_AUTO_COMMIT), is("false"));
    }

    @Test
    public void canSetPredefinedOption() {
        Options.setOption(Options.OPTION_DEBUG_LOG, "true");
        assertThat(Options.isDebugLog(), is(true));
    }

    @Test
    public void canUnsetPredefinedOption() {
        Options.setOption(Options.OPTION_DEBUG_LOG, "true");
        Options.setOption(Options.OPTION_DEBUG_LOG, "false");
        assertThat(Options.isDebugLog(), is(false));
    }

    @Test
    public void canSetFreeOption() {
        Options.setOption("dummy-option", "true");
        assertThat(Options.is("dummy-option"), is(true));
    }

    @Test
    public void canUnsetFreeOption() {
        Options.setOption("dummy-option", "true");
        Options.setOption("dummy-option", "false");
        assertThat(Options.is("dummy-option"), is(false));
    }

    @Test
    public void freeOptionDefaultIsFalse() {
        assertThat(Options.is("dummy-option-2"), is(false));
    }
}
