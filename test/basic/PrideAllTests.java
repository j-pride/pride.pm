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
public class PrideAllTests {

    public static Test suite() throws Exception {
		TestSuite suite = new TestSuite();
		add(suite, PrideInsertTest.class);
		add(suite, PrideSelectTest.class);
		add(suite, PrideSelectTestWithBindVariables.class);
		add(suite, PrideUpdateTest.class);
		add(suite, PrideUpdateTestWithBindVariables.class);
		add(suite, PrideDeleteTest.class);
		//add(suite, PrideExtensionTest.class);
		add(suite, PrideDateTest.class);
		add(suite, PrideWhereConditionTest.class);
		add(suite, PrideWhereConditionTestWithBindVariables.class);
		add(suite, PridePreparedTest.class);
		add(suite, PrideJoinTest.class);
		add(suite, PrideResourceTest.class);
		add(suite, PrideThreadTest.class);
	
		if (DBType.POSTGRES.equals(DatabaseFactory.getDatabase().getDBType())) {
			AbstractPrideTest.initDB();
			add(suite, PostgresArrayTest.class);
			add(suite, PostgresKeyValueTest.class);
		}
		else {
			System.err.println("Tests for Postgres NoSQL column types omitted as you are not running a Postgres DB");
		}
		return suite;
    }
    
    public static void add(TestSuite suite, Class<?> test) {
		suite.addTest(new JUnit4TestAdapter(test));
    	
    }
}
