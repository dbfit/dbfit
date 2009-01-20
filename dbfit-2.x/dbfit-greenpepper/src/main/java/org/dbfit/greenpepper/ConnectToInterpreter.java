package org.dbfit.greenpepper;
import com.greenpepper.Interpreter;
import com.greenpepper.Specification;
import com.greenpepper.Statistics;
import com.greenpepper.reflect.Fixture;

public class ConnectToInterpreter implements Interpreter{
	ConnectionProperties props;
	public ConnectToInterpreter(Fixture f){
		if (!(f.getTarget() instanceof ConnectionProperties)) 
			throw new UnsupportedOperationException("Connect To can only work on connection properties");
		this.props=(ConnectionProperties) f.getTarget();
	}
	public void interpret(Specification specification) {
		Statistics s=new Statistics();
		specification.nextExample();
		try {
			props.connect();			
		}
		catch (Exception e){
			System.err.println(e);
			s.exception();		
		}
		specification.exampleDone(s);
	}
}
