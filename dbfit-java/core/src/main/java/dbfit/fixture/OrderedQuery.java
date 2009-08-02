package dbfit.fixture;

/**
 * Wrapper for the query fixture in standalone mode that initialises
 * ordered row comparisons
 */
public class OrderedQuery extends Query {
	@Override
	protected boolean isOrdered() {
		return true;
	}
}
