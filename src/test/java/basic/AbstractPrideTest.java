/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - JUnit test suite
 *******************************************************************************/
package basic;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;

import pm.pride.*;
import pm.pride.ResourceAccessor.DBType;

/**
 * @author bart57
 *
 * Abstract Base-Class for PriDE Unit-Tests
 * This Class sets the stage for the Unit-Tests, 
 * it establishes the DatabaseConnection, 
 * creates a Table for Testdata 
 * and Provides a useful Method to generate Testdata
 */
public abstract class AbstractPrideTest extends Assert {
	private static final String DEFAULT_CONFIG = "config/hsql.test.config.properties";

	private Random randi = null;
	private String[][] names = new String[][] {
			{ "Hajo", "Klick" },
			{ "Britta", "Klick" },
			{ "Peter", "Pan" },
			{ "Heinz", "Ketchup" },
			{ "Chris", "K." },
			{ "Peer", "Sönlich" },
			{ "Hans", "Imglück" },
			{ "Inge", "Heim-Ermission" },
			{ "Inge", "Knito" }
	};
	protected Date firstCustomersHiredate;

	private static Boolean overriddenBindvarsDefault;
	protected static final String TEST_TABLE = "customer_pride_test";
	protected static final String REVISIONING_TEST_TABLE = "R_" + TEST_TABLE;
	protected static final String DEFAULT_ID_CLASSIFIER = "int not null primary key ";
	protected static final String REVISIONED_ID_CLASSIFIER = "int ";
    
    protected void createTestTable(String idFieldClassifier) throws SQLException {
        String columns = ""
                + "id " + idFieldClassifier + ","
                + "firstName varchar(50),"
                + "lastName varchar(50),"
                + "hireDate date,"
//                + "hireDate " + getHireDateColumnTypeBasedOnDBType() + ","
                + "active " + (isDBType(DBType.POSTGRES) ? "boolean" : "int") + ","
                + "type varchar(10)";
        dropAndCreateTable(TEST_TABLE, columns);
    }

	protected void createRevisioningTestTable() throws SQLException {
		String columns = getTestTableColumns(REVISIONED_ID_CLASSIFIER, true) + ","
				+ RevisionedRecordDescriptor.COLUMN_REVISION_TIMESTAMP + " timestamp";
		dropAndCreateTable(REVISIONING_TEST_TABLE, columns);
	}

	private String getTestTableColumns(String idFieldClassifier, boolean revisioningTable) {
		return ""
				+ "id " + idFieldClassifier + ","
				+ "firstName varchar(50),"
				+ "lastName varchar(50),"
				+ "hireDate date,"
				+ "active " + (isDBType(DBType.POSTGRES) ? "boolean" : "int")
				+ (revisioningTable ? "" : (", type varchar(10)"));
	}

    private String getHireDateColumnTypeBasedOnDBType() {
		if (isDBType(DBType.HSQL))
			return "timestamp";
		else if (isDBType(DBType.MYSQL))
			return "datetime";
		else if (isDBType(DBType.MARIADB))
			return "datetime";
		else
			return "date";
	}
    
    protected void dropAndCreateTable(String table, String columns) throws SQLException {
        dropTestTable(table);
        DatabaseFactory.getDatabase().sqlUpdate("CREATE TABLE " + table + "(" + columns + ")");
        DatabaseFactory.getDatabase().commit();
    }
    
    protected boolean isDBType(String type) {
        String dbType = DatabaseFactory.getDatabase().getDBType();
        return (dbType != null && dbType.equalsIgnoreCase(type));
    }

    protected void createTestTable() throws SQLException {
        createTestTable(DEFAULT_ID_CLASSIFIER);
    }

    /**
     * We use this exception listener to suppress the default listener's
     * stack trace printing.
     */
    private static ExceptionListener exlistener = new ExceptionListener() {
        public void process(Database db, Exception x) throws Exception { throw x; }
        public RuntimeException processSevere(Database db, Exception x) {
            throw new RuntimeException(x.getMessage(), x);
        }
    };
    
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
    @Before
	public void setUp() throws Exception {
		randi = new Random();
		initDB();
		checkIfTestShouldBeSkipped();
        createTestTable();
        createRevisioningTestTable();
	}

	protected static void setBindvarsDefault(Boolean value) {
		overriddenBindvarsDefault = value;
	}
	
	public static void initDB() throws Exception {
		Properties testConfig = determineDatabaseTestConfiguration();
		
		// Special resource accessor to allow changing the default
		// for bind-variable usage at any time in the unit tests, not only on initialization
        ResourceAccessor ra = new ResourceAccessorJSE(testConfig) {
			@Override
			public boolean bindvarsByDefault() {
				if (overriddenBindvarsDefault != null)
					return overriddenBindvarsDefault;
				return super.bindvarsByDefault();
			}
        };
        
        DatabaseFactory.setResourceAccessor(ra);
        DatabaseFactory.setExceptionListener(exlistener);
        DatabaseFactory.setDatabaseName(testConfig.getProperty(ResourceAccessor.Config.DB));
	}

	protected void setBindvarsByDefault(boolean b) {
	}

	protected void checkIfTestShouldBeSkipped() {
        final String currentDbType = DatabaseFactory.getDatabase().getDBType();
        if (this.getClass().isAnnotationPresent(NeedsDBType.class)) {
			NeedsDBType annotation = this.getClass().getAnnotation(NeedsDBType.class);
            String[] supportedDBTypes = annotation.value();
            Assume.assumeTrue(annotation.message(), Arrays.asList(supportedDBTypes).contains(currentDbType));
        }
        if (this.getClass().isAnnotationPresent(SkipForDBType.class)) {
			SkipForDBType annotation = this.getClass().getAnnotation(SkipForDBType.class);
            String[] skippedDBTypes = annotation.value();
            Assume.assumeFalse(annotation.message(), Arrays.asList(skippedDBTypes).contains(currentDbType));
        }
    }
	
	private static Properties determineDatabaseTestConfiguration() throws IOException {
		String configFileName = System.getProperty("pride.test.config.file");
		if (configFileName == null) {
			String currentUser = System.getProperty("user.name");
			configFileName = "config/" + currentUser + ".test.config.properties";
		}
		if (!new File(configFileName).exists()) {
			System.err.println("Can't determine database configuration - tried to read from file: " + configFileName);
			System.err.println("Loading default in memory configuration: " + DEFAULT_CONFIG);
			configFileName = DEFAULT_CONFIG;
		}
		Properties testConfig = new Properties();
		FileInputStream fis = new FileInputStream(configFileName);
		testConfig.load(fis);
		fis.close();
		return testConfig;
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	@After
	public void tearDown() throws Exception {
		//dropTestTable();
	}
	
	protected void dropTestTable() throws SQLException { dropTestTable(TEST_TABLE); }

	protected void dropTestTable(String table) throws SQLException {
        try {
        	// At least SQLite has problems dropping the table from the same
        	// connection which was just used to operate on the database. So
        	// we force the creation of a new connection for that job. It should
        	// not bother any other database which is less fragile :-)
        	DatabaseFactory.getDatabase().releaseConnection();
            DatabaseFactory.getDatabase().sqlUpdate("DROP TABLE " + table);
        }
        catch (SQLException sqlx) {
        	// Report problem but go ahead. Maybe we are successful anyway
        	System.err.println("DROP failed: " + sqlx);
        }
        DatabaseFactory.getDatabase().commit();
	}
	
	protected void generateCustomer(int count) throws Exception {
		firstCustomersHiredate = new Date(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse("01.01.2010 13:05:45").getTime());
		
		Customer c = new Customer(1, "First", "Customer", null, firstCustomersHiredate);
		for (int i = 2; i < count; i++) {
			String[] name = generateName(i);
			String firstName = name[0];
			String lastName = name[1];
			if (lastName.length() > 50)
				System.out.println("LAST NAME: " + lastName);
			c = new Customer(i, firstName, lastName, null, getRandomDate());
		}
		if (count != 1) {
			c = new Customer(count, "Last", "Customer");
		}
		DatabaseFactory.getDatabase().commit();
	}

	private Date getRandomDate() {
		Calendar startDate = Calendar.getInstance();
		startDate.set(2010, Calendar.JANUARY, 1);

		Calendar endDate = Calendar.getInstance();
		endDate.set(2017, Calendar.DECEMBER, 31);

		return new Date(ThreadLocalRandom.current().longs(startDate.getTimeInMillis(), endDate.getTimeInMillis()).findAny().getAsLong());
	}

	protected String[] generateName(int i) {
		int index = (i < names.length) ? i : randi.nextInt(names.length);
		String[] name = new String[2];
		name[0] = names[index][0];
		name[1] = names[index][1] + randi.nextInt(100);
		return name;

	}

	protected int countCustomers() throws SQLException {
		return new Customer().queryAll().toArray().length;
	}
	
	protected void assertNullResult(ResultIterator ri) {
		assertTrue(ri.isNull());
	}
	
	protected void checkOrderByResult(WhereCondition expression, int firstId, int lastId) throws SQLException {
		Customer c = new Customer();
		ResultIterator ri = c.query(expression);
		assertFalse(ri.isNull());
		Customer[] array = ri.toArray(Customer.class);
		assertTrue(array.length > 0);
    	assertEquals(firstId, array[0].getId());
    	assertEquals(lastId, array[array.length - 1].getId());
	}

}
