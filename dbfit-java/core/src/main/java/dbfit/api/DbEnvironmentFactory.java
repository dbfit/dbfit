package dbfit.api;

import dbfit.api.DBEnvironment;

public class DbEnvironmentFactory {
	private static DBEnvironment environment;
	public static DBEnvironment getDefaultEnvironment(){
		return environment;
	}
	public static void setDefaultEnvironment( DBEnvironment newDefaultEnvironment){
		environment=newDefaultEnvironment;
	}
}
