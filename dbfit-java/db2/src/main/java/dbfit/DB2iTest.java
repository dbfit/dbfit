package dbfit;
//this is DB2iTest renamed
public class DB2iTest extends DatabaseTest{

    public DB2iTest(){
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("DB2i")); 
    }	
}
