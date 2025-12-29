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
package pm.pride.basic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pm.pride.*;
import pm.pride.ResourceAccessor.Config;
import pm.pride.ResourceAccessor.DBType;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * @author bart57
 *
 * Abstract Base-Class for PriDE Unit-Tests
 * This Class sets the stage for the Unit-Tests, 
 * it establishes the DatabaseConnection, 
 * creates a Table for Testdata 
 * and provides usefull methods to generate Testdata
 */
public abstract class AbstractPrideTest {

	private Random randi = null;
	private final String[][] names = new String[][] {
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
	private static Properties testConfig;

	protected static final String TEST_TABLE = "customer_pride_test";
	protected static final String REVISIONING_TEST_TABLE = "R_" + TEST_TABLE;
	protected static final String DEFAULT_ID_CLASSIFIER = "int not null primary key ";
	protected static final String REVISIONED_ID_CLASSIFIER = "int ";
    
  protected void createTestTable(String idFieldClassifier) throws SQLException {
		String columns = ""
			+ Customer.COL_ID + " " + idFieldClassifier + ","
			+ Customer.COL_FIRSTNAME + " varchar(50),"
			+ Customer.COL_LASTNAME + " varchar(50),"
			+ Customer.COL_HIREDATE + " date,"
			+ Customer.COL_ACTIVE + " " + (isDBType(DBType.POSTGRES) ? "boolean" : "int") + ","
			+ SQL.quote(Customer.COL_TYPE) + " varchar(10)";
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
				+ (revisioningTable ? "" : (", " + SQL.quote("ty pe") + " varchar(10)"));
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

	protected boolean isDBType(String... types) {
		String currentDBType = DatabaseFactory.getDatabase().getDBType();
		return Arrays.stream(types).anyMatch(type -> type.equalsIgnoreCase(currentDBType));
	}

    protected void createTestTable() throws SQLException {
        createTestTable(DEFAULT_ID_CLASSIFIER);
    }

    /**
     * We use this exception listener to suppress the default listener's
     * stack trace printing.
     */
    private static final ExceptionListener exlistener = new ExceptionListener() {
        public void process(Database db, Exception x) throws Exception { throw x; }
        public RuntimeException processSevere(Database db, Exception x) {
            throw new RuntimeException(x.getMessage(), x);
        }
    };

	@BeforeEach
	public void setUp() throws Exception {
		randi = new Random();
		initDB();
		checkIfTestShouldBeSkipped();
    createTestTable();
    createRevisioningTestTable();
	}

	public static void resetTestConfig() {
		testConfig = null;
		Customer.resetTestConfig();
		DBConfigurator.resetTestConfig();
	}

	protected static void setBindvarsDefault(Boolean value) {
		overriddenBindvarsDefault = value;
	}
	
	public static void initDB() throws Exception {
		if (testConfig == null) {
			testConfig = DBConfigurator.determineDatabaseTestConfiguration();
			System.err.println("URL: " + testConfig.getProperty(Config.DB));
			System.err.println("DB User: " + testConfig.getProperty(Config.USER));
		}

		// Special resource accessor to allow changing the default
		// for bind-variable usage at any time in the unit tests, not only on initialization
    ResourceAccessor ra = new ResourceAccessorJSE(testConfig) {
			@Override
			public boolean bindvarsByDefault() {
				if (overriddenBindvarsDefault != null) {
					return overriddenBindvarsDefault;
				}
				return super.bindvarsByDefault();
			}
    };

		DatabaseFactory.setContext(testConfig.getProperty(Config.DBTYPE));
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
      assumeTrue(Arrays.asList(supportedDBTypes).contains(currentDbType), annotation.message());
    }
    if (this.getClass().isAnnotationPresent(SkipForDBType.class)) {
			SkipForDBType annotation = this.getClass().getAnnotation(SkipForDBType.class);
      String[] skippedDBTypes = annotation.value();
      assumeFalse(Arrays.asList(skippedDBTypes).contains(currentDbType), annotation.message());
    }
	}

	@AfterEach
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
			// However, at least DB2 complains if there is an open transaction
			// on that connection, so we rollback whatever may have been left
			// behind from an earlier test.
			DatabaseFactory.getDatabase().rollback();
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
