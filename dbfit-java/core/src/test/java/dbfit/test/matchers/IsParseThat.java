package dbfit.test.matchers;

import fit.Parse;

import org.hamcrest.Matcher;
import org.hamcrest.Factory;
import static org.hamcrest.Matchers.anything;

public class IsParseThat extends TraversingParseMatcher {
    protected boolean recursiveChildren = false;
    protected boolean recursiveSiblings = false;

    public IsParseThat() {
        super();
    }

    public IsParseThat(Matcher matcher) {
        super(matcher);
    }


    public IsParseThat withRecursiveChildren() {
        recursiveChildren = true;
        return this;
    }

    public IsParseThat withRecursiveSiblings() {
        recursiveSiblings = true;
        return this;
    }

    @Override
    protected void visitElements(Parse parse) {
        if (parse == null) {
            return;
        }

        matchElement(parse);

        if (recursiveChildren) {
            visitElements(parse.parts);
        }

        if (recursiveSiblings) {
            visitElements(parse.more);
        }
    }

    @Factory
    public static IsParseThat isParseThat() {
        return new IsParseThat();
    }

    @Factory
    public static IsParseThat isParseThat(Matcher matcher) {
        return new IsParseThat(matcher);
    }
}
