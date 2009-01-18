package org.dbfit.greenpepper;

import org.dbfit.core.DBEnvironment;
import org.dbfit.greenpepper.fixture.QueryFixture;
import org.dbfit.greenpepper.util.Statement;
import org.dbfit.greenpepper.util.Table;
import org.dbfit.mysql.MySqlEnvironment;

import com.greenpepper.GreenPepper;
import com.greenpepper.document.Document;
import com.greenpepper.reflect.Fixture;
import com.greenpepper.reflect.PlainOldFixture;
import com.greenpepper.systemunderdevelopment.DefaultSystemUnderDevelopment;

import dbfit.util.Log;

public class DatabaseSystemUnderDevelopment extends DefaultSystemUnderDevelopment {
	protected DBEnvironment environment;
	public DatabaseSystemUnderDevelopment() {
		super.addImport("org.dbfit.greenpepper");
        GreenPepper.addImport( "org.dbfit.greenpepper");
	}
	@Override
	public void onEndDocument(Document document) {
		Log.log("end document");
		try {
			Log.log("Rolling back");
			environment.closeConnection();

		} catch (Exception e) {
			throw new Error(e);
		}
		super.onEndDocument(document);
	}

	@Override
	public void onStartDocument(Document document) {
		Log.log("start document");
		super.onStartDocument(document);
	}
	@Override
	public Fixture getFixture(String name, String... params) throws Throwable {
		if (name.toUpperCase().trim().equals("MYSQL")){
			this.environment=new MySqlEnvironment();
			return new PlainOldFixture(new ConnectionProperties(environment, params));
		}			
		if (name.toUpperCase().trim().equals("TABLE")){
			return new PlainOldFixture(new Table(environment, params[0]));
		}			
		if (name.toUpperCase().trim().equals("STATEMENT")){
			return new PlainOldFixture(new Statement(environment, params[0]));
		}			
		if (name.toUpperCase().trim().equals("QUERY")){
			return new QueryFixture(environment,params[0]);
		}
		return super.getFixture(name, params);
	}	
}
