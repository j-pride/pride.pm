package basic;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import pm.pride.Database;
import pm.pride.DatabaseFactory;
import pm.pride.WhereCondition;
import pm.pride.util.generator.TableColumn;

public class PrideDateTest extends AbstractPrideTest {
	public static final String DATETIME_TEST_TABLE = "DATETIME_PRIDE_TEST";
	public static final int[] DATE_PRECISION_LEVELS = new int[] {
			Calendar.MILLISECOND,
			Calendar.SECOND,
			Calendar.MINUTE,
			Calendar.HOUR_OF_DAY,
			Calendar.DAY_OF_MONTH
	};
	
	static int DB_DATE_PRECISION = -1;

    protected void createDateTimeTable() throws SQLException {
    	// Timestamps usually have milliseconds precision in the DB (or maybe even micro seconds
    	// which is not of interest because they are not addressable by Java standard date types.
    	// But at least MySQL works only with seconds precision by default. That's why the
    	// timestamp columns below are specified with (3) which is the seconds fraction precision.
        String columns = ""
        		+ "RECORD_NAME varchar(50), "
                + "TIME_PLAIN timestamp(3), "
                + "TIME_AS_DATE timestamp(3), "
                + "DATE_PLAIN date, "
                + "DATE_AS_TIME date, "
                + "DATE_AS_DATE date";
        dropAndCreateTable(DATETIME_TEST_TABLE, columns);
    }

	@Override
	public void setUp() throws Exception {
		super.setUp();
		generateCustomer(9);
    	createDateTimeTable();
    	determineDatePrecisionLoss();
	}

	/**
	 * java.sql.Date is derived from java.util.Date and therefore has an internal
	 * precision of milliseconds. But most databases store dates with a lesser precision,
	 * usually cutting the time portion completely. This method determines the precision loss
	 * based on low-level prepared statements, expecting that PriDE's plain SQL representation
	 * of dates produces the same loss. It should not make a difference if the user is working
	 * with plain SQL or prepared statements
	 */
    public void determineDatePrecisionLoss() throws Exception {
    	if (DB_DATE_PRECISION != -1) {
    		return;
    	}

    	Database db = DatabaseFactory.getDatabase();
		Calendar dateAssembly = new GregorianCalendar(2019, 12, 13, 14, 15, 16);
		dateAssembly.set(Calendar.MILLISECOND, 170);
		java.sql.Date fullPrecisionDate = new java.sql.Date(dateAssembly.getTimeInMillis());
		System.out.println(fullPrecisionDate.getTime());

		Connection con = db.getConnection(); 
		PreparedStatement insert = con.prepareStatement
				("insert into DATETIME_PRIDE_TEST (DATE_PLAIN) values (?)");
		insert.setDate(1, fullPrecisionDate);
		insert.executeUpdate();
		insert.close();
		
		PreparedStatement query = con.prepareStatement
				("select DATE_PLAIN from DATETIME_PRIDE_TEST");
		ResultSet rs = query.executeQuery();
		assertTrue(rs.next());
		java.sql.Date dbPrecisionDate = rs.getDate(1);
		System.out.println(dbPrecisionDate.getTime());
		Calendar dateChecker = Calendar.getInstance();
		dateChecker.setTimeInMillis(dbPrecisionDate.getTime());
		for (int precision: DATE_PRECISION_LEVELS) {
			if (dateChecker.get(precision) != 0) {
				DB_DATE_PRECISION = precision;
				break;
			}
		}
		System.out.println("Date precision for " + db.getDBType() + ": " + DB_DATE_PRECISION);
		rs.close();
		query.close();
		db.rollback(); // Rollback the insert above
    	assertNotEquals(-1, DB_DATE_PRECISION); // Come on - not even days precision??
    }

    /**
     * Checks if two dates are equal within the precision which the database stores date values with.
     * The method cuts off all time portions from {@link expectedWithFullPrecision} which are below
     * the database' date precision and ensures that both values are converted to a java.util.Date
     * before comparing them. So the function should also work with java.sql.Date and java.sql.Timestamp
     */
	private void assertEqualsWithReducedPrecision(
			java.util.Date expectedWithFullPrecision,
			java.util.Date actualWithReducedPrecision,
			int dbDatePrecision) {
		Calendar c = Calendar.getInstance();
		c.setTime(expectedWithFullPrecision);
		for (int calendarField: DATE_PRECISION_LEVELS) {
			if (calendarField > dbDatePrecision) {
				c.set(calendarField, 0);
			}
		}
		java.util.Date expectedWithReducedPrecision = c.getTime();
		actualWithReducedPrecision = new java.util.Date(actualWithReducedPrecision.getTime());
		assertEquals(expectedWithReducedPrecision, actualWithReducedPrecision);
	}

	@Test
	public void testInsert() throws Exception{
		Date myDate = new Date((new GregorianCalendar(1974, 6, 23)).getTimeInMillis()); //23.7.1974

		DateTime dtWrite = new DateTime();
		dtWrite.setRecordName("testInsert");
		dtWrite.setDatePlain(myDate);
		dtWrite.create();
		
		DateTime dtRead = new DateTime(dtWrite);
		assertTrue(myDate.equals(dtRead.getDatePlain()));
	}

	@Test
	public void testInsertWithServerTime() throws Exception{
		Timestamp dbTime = new Timestamp(DatabaseFactory.getDatabase().getSystime().getTime());

		DateTime dtWrite = new DateTime("testInsertWithServerTime");
		dtWrite.setTimePlain(dbTime);
		dtWrite.create();
		
		Thread.sleep(1);
		DateTime dtRead = new DateTime(dtWrite);
		assertTrue(dtRead.getTimePlain().before(new java.util.Date()));
	}

	@Test
	public void testJavaUtilDateAsDate() throws Exception {
		java.util.Date myDate = new java.util.Date();
		System.out.println(myDate);

		DateTime dtWrite = new DateTime("testJavaUtilDateAsDate");
		dtWrite.setDateAsDate(myDate);
		dtWrite.create();

		DateTime dtRead = new DateTime(dtWrite);
		assertEqualsWithReducedPrecision(myDate, dtRead.getDateAsDate(), DB_DATE_PRECISION);
	}

	@Test
	public void testTimestampAcceptedForDate() throws Exception {
		java.util.Date myDate = new java.util.Date();
		Timestamp myTime = new Timestamp(myDate.getTime());
		
		DateTime dtWrite = new DateTime("testTimestampAcceptedForDate");
		dtWrite.setDateAsDate(myTime);
		dtWrite.create();

		DateTime dtRead = new DateTime(dtWrite);
		assertEqualsWithReducedPrecision(myTime, dtRead.getDateAsDate(), DB_DATE_PRECISION);
	}
	
	/**
	 * Update a Customer with hireDate and test the result
	 */	
	@Test
	public void testUpdateNoDBDate() throws Exception {
		Date myDate = new Date((new GregorianCalendar(1974, 6, 23)).getTimeInMillis()); //23.7.1974

		Customer c1 = new Customer(1);
		c1.setHireDate(myDate);
		c1.update();
		DatabaseFactory.getDatabase().commit();
		
		Customer c2 = new Customer(1);
		assertTrue(myDate.equals(c2.getHireDate()));
	}

	@Test
	public void testUpdateWithDBDate() throws Exception {
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		
		Database db = DatabaseFactory.getDatabase();
		Customer cWrite = new Customer(2);
		
		cWrite.setHireDate(new Date(db.getSystime().getTime()));
		cWrite.update();
		db.commit();
		
		Customer cRead = new Customer(2);
		assertTrue(cal.getTime().before(cRead.getHireDate()));
	}
	
	@Test
	public void testEqualDatesMillisecondsCorrectlyIgnored() throws SQLException {
		if (DB_DATE_PRECISION == Calendar.MILLISECOND) {
			System.out.println("No date portions to ignore for a database of type " +
					DatabaseFactory.getDatabase().getDBType());
			return;
		}
			
		Customer c = new Customer(1);
		Date firstCustomersHiredatePlus1Millisecond = new Date(firstCustomersHiredate.getTime() + 1);
		WhereCondition whereCondition = new WhereCondition()
				.and("hiredate", firstCustomersHiredatePlus1Millisecond);
		checkOrderByResult(whereCondition, 1, 1);
	}
	
	@Test
	public void testTimestampWithMilliseconds() throws Exception {
		java.util.Date myDate = new java.util.Date();
		Timestamp myTime = new Timestamp(myDate.getTime());
		
		DateTime dtWrite = new DateTime("testTimestampWithMilliseconds");
		dtWrite.setTimePlain(myTime);
		dtWrite.create();

		DateTime dtRead = new DateTime(dtWrite);
		assertEquals(myTime, dtRead.getTimePlain());
	}
	
	@Test
	public void testTimestampAsJavaUtilDate() throws Exception {
		java.util.Date myDate = new java.util.Date();

		DateTime dtWrite = new DateTime("testTimestampAsJavaUtilDate");
		dtWrite.setTimeAsDate(myDate);
		dtWrite.create();

		DateTime dtRead = new DateTime(dtWrite);
		assertEquals(myDate, dtRead.getTimeAsDate());
	}

}
