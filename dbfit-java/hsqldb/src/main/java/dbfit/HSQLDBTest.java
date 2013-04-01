package dbfit;
import dbfit.environment.HSQLDBEnvironment;
public class HSQLDBTest extends DatabaseTest {
    public HSQLDBTest (){
        super(new HSQLDBEnvironment());
    }
}

