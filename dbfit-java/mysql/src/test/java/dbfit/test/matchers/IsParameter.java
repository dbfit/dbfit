package dbfit.test.matchers;

import dbfit.environment.ParamDescriptor;
import dbfit.util.Direction;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.core.IsEqual;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.ArrayList;
import static java.util.Arrays.asList;

public class IsParameter extends TypeSafeDiagnosingMatcher<ParamDescriptor> {
    private final Matcher matcher;

    public IsParameter(String name, Direction direction, String type) {
        matcher = new IsEqual<List>(asList(name, direction, type));
    }

    public IsParameter(ParamDescriptor p) {
        this(p.name, p.direction, p.type);
    }

    @Override
    public boolean matchesSafely(ParamDescriptor param,
                                 Description mismatchDescription) {
        if (!matcher.matches(asList(param.name, param.direction, param.type))) {
            matcher.describeMismatch(param, mismatchDescription);
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description describtion) {
        describtion.appendDescriptionOf(matcher);
    }

    @Factory
    public static IsParameter hasReturnType(String type) {
        return new IsParameter("", Direction.RETURN_VALUE, type);
    }

    @Factory
    public static Matcher<Iterable<? extends ParamDescriptor>> containsParameters(
            List<ParamDescriptor> expectedParameters) {
        List<Matcher<? super ParamDescriptor>> matchers = new ArrayList<>();

        for (ParamDescriptor param: expectedParameters) {
            matchers.add(new IsParameter(param));
        }

        return contains(matchers);
    }

    @Factory
    public static Matcher<Iterable<? extends ParamDescriptor>> containsParameters(
            ParamDescriptor... expectedParameters) {
        return containsParameters(asList(expectedParameters));
    }
}
