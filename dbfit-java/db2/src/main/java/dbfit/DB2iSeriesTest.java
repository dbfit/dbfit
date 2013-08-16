package dbfit;

public class DB2iSeriesTest extends DatabaseTest{

    public DB2iSeriesTest(){
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("DB2iSeries")); 
    }	
}
