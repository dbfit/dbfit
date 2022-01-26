package dbfit;

public class InformixTest extends DatabaseTest {

    public InformixTest() {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Informix"));
    }
}
