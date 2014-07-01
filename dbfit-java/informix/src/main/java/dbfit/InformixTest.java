package dbfit;

import com.informix.jdbc.IfxSqliConnect;


/**
 * Created by muppana on 6/5/2014.
 */
public class InformixTest extends DatabaseTest {
    private IfxSqliConnect ifxSqliConnection;
    private static String Role;

    public InformixTest()  {
        super(dbfit.api.DbEnvironmentFactory.newEnvironmentInstance("Informix"));
    }



}



