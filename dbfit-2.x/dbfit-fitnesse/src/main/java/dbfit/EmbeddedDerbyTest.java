package dbfit;

import org.dbfit.derby.EmbeddedDerbyEnvironment;

/**
 * Provides support for testing Derby databases (also known as JavaDB) running
 * in embedded mode.
 * 
 * @author P&aring;l Brattberg, pal.brattberg@acando.com
 */
public class EmbeddedDerbyTest extends DerbyTest {
	public EmbeddedDerbyTest() {
		super(new EmbeddedDerbyEnvironment());
	}
}
