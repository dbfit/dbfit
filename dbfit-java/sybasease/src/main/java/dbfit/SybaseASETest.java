package dbfit;

public class SybaseASETest extends DatabaseTest {
    public SybaseASETest(){
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Sybase"));
    }
}
