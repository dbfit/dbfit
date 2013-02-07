package dbfit;

import dbfit.environment.*;
public class TeradataTest extends DatabaseTest {
	public TeradataTest(){
		super(new TeradataEnvironment());
		System.out.println("TeradataTest: TeradataEnvironment()");
	}
}
