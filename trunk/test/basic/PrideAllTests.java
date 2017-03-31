package basic;
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
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author bart57
 *
 * TestSuite for PriDE Unit-Testing
 */
public class PrideAllTests {

	public static void main(String[] args) {
		junit.swingui.TestRunner.run(PrideAllTests.class);
	}
	
	public static Test suite() throws Exception {
		TestSuite suite = new TestSuite("Test for default package");
		
		suite.addTest(new TestSuite(PrideInsertTest.class));
        suite.addTest(new TestSuite(PrideSelectTest.class));
        suite.addTest(new TestSuite(PrideUpdateTest.class));
        suite.addTest(new TestSuite(PrideDeleteTest.class));
		//suite.addTest(new TestSuite(PrideExtensionTest.class));
        suite.addTest(new TestSuite(PrideDateTest.class));
        suite.addTest(new TestSuite(PrideSQLExpressionTest.class));
        suite.addTest(new TestSuite(PridePreparedTest.class));
        suite.addTest(new TestSuite(PrideJoinTest.class));
        suite.addTest(new TestSuite(PrideResourceTest.class));
        suite.addTest(new TestSuite(PrideThreadTest.class));

        PrideBaseTest.initDB();
        if (DBType.POSTGRES.equals(DatabaseFactory.getDatabase().getDBType())) {
            suite.addTest(new TestSuite(PostgresArrayTest.class));
            suite.addTest(new TestSuite(PostgresKeyValueTest.class));
        }
        else {
            System.err.println("Tests for Postgres NoSQL column types omitted as you are not running a Postgres DB");
        }

        return suite;
	}
}
