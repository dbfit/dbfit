package dbfit.util;

import fit.Fixture;
import fit.FixtureLoader;
import fit.Parse;

/**
 * Make it possible to remove a previously imported fixture from Fit's fixture
 * path.
 */
public class ExportFixture extends Fixture {
	public void doRow(Parse row) {
		String packageName = row.parts.text();
		FixtureLoader.instance().fixturePathElements.remove(packageName);
	}
}
