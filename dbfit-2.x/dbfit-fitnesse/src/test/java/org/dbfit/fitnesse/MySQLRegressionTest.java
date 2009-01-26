package org.dbfit.fitnesse;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.neuri.trinidad.JUnitHelper;
import com.neuri.trinidad.fitnesserunner.FitTestEngine;
import com.neuri.trinidad.fitnesserunner.FitNesseRepository;

public class MySQLRegressionTest {
	JUnitHelper helper;

	@Before
	public void initHelper() throws Exception{
		helper=new JUnitHelper(new FitNesseRepository("src/main/fitnesse"),
			new FitTestEngine(),
			new File(System.getProperty("java.io.tmpdir"),"dbfit-tests").getAbsolutePath());
	}	@Test
	public void runFlowModeSuite() throws Exception {
		helper
				.assertSuitePasses("AcceptanceTests.JavaTests.MySqlTests.FlowMode");
		// helper.assertTestPasses("AcceptanceTests.JavaTests.MySqlTests.FlowMode.DataTypes.DateTests");
	}

}
