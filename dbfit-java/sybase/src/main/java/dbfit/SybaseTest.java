package dbfit;

public class SybaseTest extends DatabaseTest {
    public SybaseTest(){
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Sybase"));
    }
}

