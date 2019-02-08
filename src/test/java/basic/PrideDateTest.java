package basic;
/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - JUnit test suite
 *     Manfred Karda�, Beckmann & Partner
 *******************************************************************************/
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import pm.pride.Database;
import pm.pride.DatabaseFactory;
import pm.pride.ResultIterator;
import pm.pride.WhereCondition;

/**
 * @author ggdcc04
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PrideDateTest extends AbstractPrideTest {
	public static final String DATETIME_TEST_TABLE = "datetime_pride_test";
	public static final int[] DATE_PRECISIONS = new int[] {
			Calendar.MILLISECOND,
			Calendar.SECOND,
			Calendar.MINUTE,
			Calendar.HOUR_OF_DAY,
			Calendar.DAY_OF_MONTH
	};
	
	static int DB_DATE_PRECISION = -1;

    protected void createDateTimeTable() throws SQLException {
        String columns = ""
        		+ "record_name varchar(50), "
                + "time_plain timestamp, "
                + "time_as_date timestamp, "
                + "date_plain date, "
                + "date_as_time date, "
                + "date_as_date date";
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
	 * usually, cutting the time portion completely. This method determines the precision loss
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
				("insert into datetime_pride_test (date_plain) values (?)");
		insert.setDate(1, fullPrecisionDate);
		insert.executeUpdate();
		insert.close();
		
		PreparedStatement query = con.prepareStatement
				("select date_plain from datetime_pride_test");
		ResultSet rs = query.executeQuery();
		assertTrue(rs.next());
		java.sql.Date dbPrecisionDate = rs.getDate(1);
		System.out.println(dbPrecisionDate.getTime());
		Calendar dateChecker = Calendar.getInstance();
		dateChecker.setTimeInMillis(dbPrecisionDate.getTime());
		for (int precision: DATE_PRECISIONS) {
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

	private void assertEqualsRoundedDates(
			java.util.Date expectedWithFullPrecision,
			java.util.Date actualWithReducedPrecision,
			int precision) {
		Calendar c = Calendar.getInstance();
		c.setTime(expectedWithFullPrecision);
		for (int calendarField: DATE_PRECISIONS) {
			if (calendarField > precision) {
				c.set(calendarField, 0);
			}
		}
		java.util.Date expectedWithReducedPrecision = c.getTime();
		actualWithReducedPrecision = new java.util.Date(actualWithReducedPrecision.getTime());
		assertEquals(expectedWithReducedPrecision, actualWithReducedPrecision);
	}

	@Test
	public void testSelectProblem() throws Exception {
		ResultIterator ri = DatabaseFactory.getDatabase().sqlQuery("select * from datetime_pride_test");
		ri.close();
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
		assertEqualsRoundedDates(myDate, dtRead.getDateAsDate(), DB_DATE_PRECISION);
	}

	@Test
	public void testTimestampAcceptedForDate() throws Exception {
		java.util.Date myDate = new java.util.Date();
		Timestamp myTime = new Timestamp(myDate.getTime());
		
		DateTime dtWrite = new DateTime("testTimestampAcceptedForDate");
		dtWrite.setDateAsDate(myTime);
		dtWrite.create();

		DateTime dtRead = new DateTime(dtWrite);
		assertEqualsRoundedDates(myTime, dtRead.getDateAsDate(), DB_DATE_PRECISION);
	}
	
	/**
	 * Update a Customer with hireDate and test the result
	 */	
	@Test
	public void testUpdateNoDBDate() throws Exception {
		Date myDate = new Date((new GregorianCalendar(1974, 6, 23)).getTimeInMillis()); //23.7.1974
		Date dbTime = new Date(DatabaseFactory.getDatabase().getSystime().getTime());

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
	
}
