package dbfit;

import org.dbfit.core.DBEnvironment;
import org.dbfit.derby.DerbyEnvironment;

/**
 * Provides support for testing Derby databases (also known as JavaDB).
 * 
 * @author P&aring;l Brattberg, pal.brattberg@acando.com
 */
public class DerbyTest extends DatabaseTest {

	public DerbyTest() {
		super(new DerbyEnvironment());
	}
	protected DerbyTest(DBEnvironment env) {
		super(env);
	}
	public void dbfitDotDerbyTest() {
		// required by fitnesse release 20080812
	}
}
