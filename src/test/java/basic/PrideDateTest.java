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
 *     Manfred Kardaß, Beckmann & Partner
 *******************************************************************************/
import java.sql.Timestamp;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import junit.framework.Assert;
import de.mathema.pride.Database;
import de.mathema.pride.DatabaseFactory;

/**
 * @author ggdcc04
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PrideDateTest extends AbstractPrideTest 
{

	@Override
	public void setUp() throws Exception {
		super.setUp();
		generateCustomer(9);
	}
	
	/**
	 * Insert a Customer and test with and without generated dates
	 */	
	@Test
	public void testInsert() throws Exception{
		Date      myDate = new Date((new GregorianCalendar(1974, 6, 23)).getTimeInMillis()); //23.7.1974
		Date      dbTime = new Date(DatabaseFactory.getDatabase().getSystime().getTime());

		Customer c      = new Customer(100, "Easy", "Rider", Boolean.TRUE, dbTime);
		DatabaseFactory.getDatabase().commit();
		
		Customer c2 = new Customer(100);
		assertEquals("Easy", c2.getFirstName());
		assertEquals("Rider", c2.getLastName());
		assertTrue(myDate.before(c2.getHireDate()));
		

		Customer c3 = new Customer(200, "two", "two", Boolean.TRUE);
		c3.setHireDate(myDate);
		DatabaseFactory.getDatabase().commit();
		
		Customer c4 = new Customer(200);
		assertEquals("two", c3.getLastName());
		assertTrue(myDate.equals(c3.getHireDate()));
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
		Assert.assertTrue(myDate.equals(c2.getHireDate()));
		
	}

	/**
	 * Insert a Customer and test the result
	 */	
	@Test
	public void testUpdateWithDBDate() throws Exception {
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		
		Database db        = DatabaseFactory.getDatabase();
		Customer c1        = new Customer(2);
		
		c1.setHireDate(new Date(db.getSystime().getTime()));
		c1.update();
		db.commit();
		
		Customer c2 = new Customer(2);
		Assert.assertTrue(cal.getTime().before(c2.getHireDate()));
	}
}
