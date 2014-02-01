package dbfit.test.matchers;

import static dbfit.test.matchers.IsParseWithTag.*;
import static dbfit.test.matchers.IsParseWithBody.*;

import fit.Parse;

import org.hamcrest.Matcher;
import org.hamcrest.Factory;
import static org.hamcrest.Matchers.*;

public class NumCellsThat extends NumParsePartsThat {

    public NumCellsThat(int n, Matcher cellMatcher) {
        super(n, cellMatcher);
    }

    @Override
    protected void visitElements(Parse table) {
        for (Parse row = table.parts; row != null; row = row.more ) {
            for (Parse cell = row.parts; cell != null; cell = cell.more) {
                matchElement(cell);
            }
        }
    }

    @Override
    protected String getElementsKind() {
        return "cell";
    }

    @Factory
    public static NumCellsThat numCellsWith(int n, String body, String tagClass) {
        return new NumCellsThat(n, allOf(
                    hasTagThat(containsString(tagClass)),
                    hasBodyThat(containsString(body))));
    }

    @Factory
    public static NumCellsThat withCellsThat(Matcher cellMatcher) {
        return new NumCellsThat(1, cellMatcher);
    }
}
