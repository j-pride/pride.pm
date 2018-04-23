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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import de.mathema.pride.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import de.mathema.pride.ResourceAccessor.DBType;

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

	protected static final String TEST_TABLE = "customer_pride_test";
	protected static final String REVISIONING_TEST_TABLE = "R_" + TEST_TABLE;
    protected static final String DEFAULT_ID_CLASSIFIER = "int not null primary key ";
	protected static final String REVISIONED_ID_CLASSIFIER = "int ";

	protected void createTestTable() throws SQLException {
		createTestTable(DEFAULT_ID_CLASSIFIER);
	}

    protected void createTestTable(String idFieldClassifier) throws SQLException {
		dropAndCreateTable(TEST_TABLE, getTestTableColumns(idFieldClassifier));
    }

	protected void createRevisioningTestTable() throws SQLException {
		String columns = getTestTableColumns(REVISIONED_ID_CLASSIFIER) + ","
				+ RevisionedRecordDescriptor.COLUMN_REVISION_TIMESTAMP + " timestamp";
		dropAndCreateTable(REVISIONING_TEST_TABLE, columns);
	}

	private String getTestTableColumns(String idFieldClassifier) {
		return ""
				+ "id " + idFieldClassifier + ","
				+ "firstName varchar(50),"
				+ "lastName varchar(50),"
				+ "hireDate date,"
				+ "active " + (isPostgresDB() ? "boolean" : "int") + ","
				+ "type varchar(10)";
	}
    
    protected void dropAndCreateTable(String table, String columns) throws SQLException {
        dropTestTable(table);
        DatabaseFactory.getDatabase().sqlUpdate("CREATE TABLE " + table + "(" + columns + ")");
        DatabaseFactory.getDatabase().commit();
    }
    
    protected boolean isPostgresDB() {
        String dbType = DatabaseFactory.getDatabase().getDBType();
        return (dbType != null && dbType.equalsIgnoreCase(DBType.POSTGRES));
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
        createTestTable();
        createRevisioningTestTable();
	}

	public static void initDB() throws Exception {
		Properties testConfig = determineDatabaseTestConfiguration();
        ResourceAccessor ra =
                new ResourceAccessorJ2SE(testConfig);
            DatabaseFactory.setResourceAccessor(ra);
            DatabaseFactory.setExceptionListener(exlistener);
            DatabaseFactory.setDatabaseName(testConfig.getProperty("pride.db"));
	}
	
	private static Properties determineDatabaseTestConfiguration() throws IOException {
		String configFileName = System.getProperty("pride.test.config.file");
		if (configFileName == null) {
			String currentUser = System.getProperty("user.name");
			configFileName = "config/" + currentUser + ".test.config.properties";
		}
		if (!new File(configFileName).exists()) {
			throw new IllegalArgumentException("Can't determine database configuration - tried to read from file: " + configFileName);
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
            DatabaseFactory.getDatabase().sqlUpdate("DROP TABLE " + table);
        }
        catch (SQLException sqlx) {} // ignore
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
}
