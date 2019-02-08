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

/**
 * @author ggdcc04
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PrideDateTest extends AbstractPrideTest {
	public static final long MAX_LOSS_OF_TIME_IN_DATE = 24 * 60 * 60 * 1000;
	public static final String DATETIME_TEST_TABLE = "datetime_pride_test";

    protected void createDateTimeTable() throws SQLException {
        String columns = ""
                + "timePlain timestamp, "
                + "timeAsDate timestamp, "
                + "datePlain date, "
                + "dateAsTime date ";
        dropAndCreateTable(DATETIME_TEST_TABLE, columns);
    }

	@Override
	public void setUp() throws Exception {
		super.setUp();
		generateCustomer(9);
    	createDateTimeTable();
	}
	
	@Test
	public void testDatePrecisionLoss() throws Exception {
		// java.sql.Date is derived from java.util.Date and therefore has an internal
		// precision of milliseconds. But most databases store dates with a lesser precision,
		// usually, cutting the time portion completely. This method determines the precision loss
		// based on low-level prepared statements, expecting that PriDE's plain SQL representation
		// of dates produces the same loss. It should not make a difference if the user is working
		// with plain SQL or prepared statements
		System.out.println("################### CHECKING LOSS OF DATE PRECISION ##################");
		Connection con = DatabaseFactory.getDatabase().getConnection(); 
		PreparedStatement insert = con.prepareStatement
				("insert into datetime_pride_test (datePlain) values (?)");

		Calendar dateAssembly = new GregorianCalendar(2019, 12, 13, 14, 15, 16);
		dateAssembly.set(Calendar.MILLISECOND, 170);
		java.sql.Date fullPrecisionDate = new java.sql.Date(dateAssembly.getTimeInMillis());
		System.out.println(fullPrecisionDate.getTime());
		insert.setDate(1, fullPrecisionDate);
		insert.executeUpdate();
		
		PreparedStatement query = con.prepareStatement
				("select datePlain from datetime_pride_test");
		ResultSet rs = query.executeQuery();
		assertTrue(rs.next());
		java.sql.Date dbPrecisionDate = rs.getDate(1);
		System.out.println(dbPrecisionDate.getTime());
		Calendar dateChecker = Calendar.getInstance();
		dateChecker.setTimeInMillis(dbPrecisionDate.getTime());
		if (dateChecker.get(Calendar.MILLISECOND) == 0) {
			System.out.println("Millseconds lost");
			if (dateChecker.get(Calendar.SECOND) == 0) {
				System.out.println("Seconds lost");
				if (dateChecker.get(Calendar.MINUTE) == 0) {
					System.out.println("Minutes lost");
					if (dateChecker.get(Calendar.HOUR) == 0) {
						System.out.println("Hours lost");
					}
				}
			}
		}

		PreparedStatement queryWithOffset = con.prepareStatement
				("select datePlain from datetime_pride_test where datePlain=?");
		java.sql.Date offsetDate = new java.sql.Date(dbPrecisionDate.getTime() + 1);
		System.out.println(offsetDate.getTime());
		queryWithOffset.setDate(1, offsetDate);
		rs = query.executeQuery();
		assertTrue(rs.next());
		
	}

	@Test
	public void testInsert() throws Exception{
		Date myDate = new Date((new GregorianCalendar(1974, 6, 23)).getTimeInMillis()); //23.7.1974

		Customer c3 = new Customer(200, "two", "two", Boolean.TRUE);
		c3.setHireDate(myDate);
		DatabaseFactory.getDatabase().commit();
		
		Customer c4 = new Customer(200);
		assertEquals("two", c3.getLastName());
		assertTrue(myDate.equals(c3.getHireDate()));
	}

	@Test
	public void testInsertWithServerTime() throws Exception{
		Date dbTime = new Date(DatabaseFactory.getDatabase().getSystime().getTime());

		Customer c = new Customer(100, "Easy", "Rider", Boolean.TRUE, dbTime);
		DatabaseFactory.getDatabase().commit();
		
		Customer c2 = new Customer(100);
		assertEquals("Easy", c2.getFirstName());
		assertEquals("Rider", c2.getLastName());
		assertNotNull(c2.getHireDate());
		assertTrue(c2.getHireDate().before(new java.util.Date()));
	}

	@Test
	public void testJavaUtilDateAsDate() throws Exception {
		java.util.Date myDate = new java.util.Date();
		System.out.println(myDate);

		long dateWithoutTime = myDate.getTime() - (myDate.getTime() % MAX_LOSS_OF_TIME_IN_DATE);
		myDate = new java.util.Date(dateWithoutTime);
		System.out.println(myDate);
		
		Customer write = new Customer(100, "Easy", "Rider", Boolean.TRUE, myDate);
		Customer read = new Customer(100);
		assertNotNull(read.getHireDate());
		// Date was written with less precision depending on database type
		// The whole time portion may have gone
		assertTrue(
				"Unexpectedly high difference of " + (myDate.getTime() - read.getHireDate().getTime()) +
						" exceeds maximum of " + MAX_LOSS_OF_TIME_IN_DATE,
				myDate.getTime() - read.getHireDate().getTime() < MAX_LOSS_OF_TIME_IN_DATE);
	}

	@Test
	public void testTimestampAcceptedForDate() throws Exception {
		java.util.Date myDate = new java.util.Date();
		Timestamp myTime = new Timestamp(myDate.getTime());
		// Timestamp is accepted although java.util.Date is by default mapped to java.sql.Date
		Customer write = new Customer(100, "Easy", "Rider", Boolean.TRUE, myTime);
		Customer read = new Customer(100);
		assertNotNull(read.getHireDate());
		// Time portion of time stamp may have gone if the database stores dates only with day precision.
		// Amount of loss depends on the database type
		assertTrue(
				"Unexpectedly high difference of " + (myTime.getTime() - read.getHireDate().getTime()),
				myTime.getTime() - read.getHireDate().getTime() < MAX_LOSS_OF_TIME_IN_DATE);
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
}
