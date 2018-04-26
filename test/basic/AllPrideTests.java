package basic;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/*******************************************************************************
 * Copyright (c) 2001-2005 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - JUnit test suite
 *******************************************************************************/
import de.mathema.pride.DatabaseFactory;
import de.mathema.pride.ResourceAccessor.DBType;
import postgres.PostgresArrayTest;
import postgres.PostgresKeyValueTest;
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author bart57
 *
 * TestSuite for PriDE Unit-Testing
 */
public class AllPrideTests {

    public static Test suite() throws Exception {
		TestSuite suite = new TestSuite();
		add(suite, PrideInsertTest.class);
		add(suite, PrideInsertTestWithBindVariables.class);
		add(suite, PrideSelectTest.class);
		add(suite, PrideSelectTestWithBindVariables.class);
		add(suite, PrideSelectSpooledTest.class);
		add(suite, PrideUpdateTest.class);
		add(suite, PrideUpdateTestWithBindVariables.class);
		add(suite, PrideUpdateTestWithLocalBinding.class);
		add(suite, PrideUpdateTestWithWhereCondition.class);
		add(suite, PrideDeleteTest.class);
		add(suite, PrideDateTest.class);
		add(suite, PrideWhereConditionTest.class);
		add(suite, PrideWhereConditionTestWithBindVariables.class);
		add(suite, PrideWhereConditionWithFormatterTest.class);
		add(suite, PridePreparedTest.class);
		add(suite, PrideJoinTest.class);
		add(suite, PrideResourceTest.class);
		add(suite, DerivedRecordDescriptorTest.class);
		add(suite, PrideSQLExpressionFormatterTest.class);
		add(suite, PrideRevisioningTest.class);
		add(suite, PrideRevisioningPreparedUpdateTest.class);
	
		AbstractPrideTest.initDB(); // Required to make the following DB type checks work
		
		if (DBType.POSTGRES.equals(DatabaseFactory.getDatabase().getDBType())) {
			add(suite, PostgresArrayTest.class);
			add(suite, PostgresKeyValueTest.class);
		}
		else {
			System.err.println("Tests for Postgres NoSQL column types omitted as you are not running a Postgres DB");
		}
		
		if (!DBType.HSQL.equals(DatabaseFactory.getDatabase().getDBType())) {
			add(suite, PrideThreadTest.class);
		}
		else {
			System.err.println("Multithreading tests omitted for HSQL. They don't work with an embedded database");
		}
		return suite;
    }
    
    public static void add(TestSuite suite, Class<?> test) {
		suite.addTest(new JUnit4TestAdapter(test));
    	
    }
}
