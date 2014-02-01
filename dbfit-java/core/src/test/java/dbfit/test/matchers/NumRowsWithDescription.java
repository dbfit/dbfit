package dbfit.test.matchers;

import static dbfit.test.matchers.IsParseWithTag.*;
import static dbfit.test.matchers.IsParseWithDescription.*;

import fit.Parse;

import org.hamcrest.Factory;
import static org.hamcrest.Matchers.*;

public class NumRowsWithDescription extends TraversingParseMatcher {

    public NumRowsWithDescription(int n, String descr, String tagClass) {
        super(allOf(
                hasTagThat(containsString(tagClass)),
                hasDescriptionThat(containsString(descr))));
        withExpectedCount(n);
    }

    @Override
    protected void visitElements(Parse table) {
        for (Parse row = table.parts; row != null; row = row.more) {
            matchElement(row);
        }
    }

    @Override
    protected String getElementsKind() {
        return "row";
    }

    @Factory
    public static NumRowsWithDescription numRowsWithDescription(
            int n, String descr, String tagClass) {
        return new NumRowsWithDescription(n, descr, tagClass);
    }
}
