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
 *******************************************************************************/
import de.mathema.pride.DatabaseFactory;

import junit.framework.Assert;

/**
 * @author bart57
 *
 * Class to Test the Update-Behaviour of the PriDE-Framework
 */
public class PrideUpdateTest extends PrideBaseTest {

	public PrideUpdateTest(String name) {
		super(name);
	}
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		generateCustomer(9);
	}
	
	public void testUpdatePK() throws Exception{
		Customer c = new Customer(1);
		Assert.assertEquals("First", c.getFirstName());
		Assert.assertEquals("Customer", c.getLastName());
		c.setFirstName("Casper");
		c.setLastName("Kopp");
		c.update();
		DatabaseFactory.getDatabase().commit();
		Customer c2 = new Customer(1);
		Assert.assertEquals("Casper", c2.getFirstName());
		Assert.assertEquals("Kopp", c2.getLastName());
	}

	public void testUpdateByExample() throws Exception{
		Customer c = new Customer(1);
		Assert.assertEquals("First", c.getFirstName());
		Assert.assertEquals("Customer", c.getLastName());
		c.setFirstName("Inge");
		c.setLastName("Updated");
		c.update(new String[] { "id" });
		DatabaseFactory.getDatabase().commit();
		Customer[] ca = (Customer[])c.query(new String [] { "lastName" } ).toArray();
		Assert.assertEquals(ca.length, 1);
		Assert.assertEquals("Inge", ca[0].getFirstName());
		Assert.assertEquals("Updated", ca[0].getLastName());
	}

	public void testUpdateFields() throws Exception{
		Customer c = new Customer(1);
		Assert.assertEquals("First", c.getFirstName());
		Assert.assertEquals("Customer", c.getLastName());
		c.setFirstName("Casper");
		c.update((String[])null, new String[] { "firstName" });
		DatabaseFactory.getDatabase().commit();
		Customer c2 = new Customer(1);
		Assert.assertEquals("Casper", c2.getFirstName());
		Assert.assertEquals("Customer", c2.getLastName());
	}

	public void testUpdateMultiple() throws Exception{
		Customer c = new Customer();
		c.setFirstName("Inge");
		c.setLastName("Updated");
		c.update(new String[] { "firstName"}, new String[] { "lastName" });
		DatabaseFactory.getDatabase().commit();
		Customer[] ca = (Customer[])c.query(new String [] { "firstName" } ).toArray();
		Assert.assertEquals(ca.length, 2);
		Assert.assertTrue(ca[0].getId() != ca[1].getId());
		Assert.assertTrue(ca[0].getId() == 7 || ca[0].getId() == 8);
		Assert.assertTrue(ca[1].getId() == 7 || ca[1].getId() == 8);
	}

	public void testUpdateWhere() throws Exception{
		Customer c = new Customer();
		c.setLastName("Updated");
		c.update("firstName like 'Pe%'", new String[] { "lastName" });
		DatabaseFactory.getDatabase().commit();
		Customer[] ca = (Customer[])c.query(new String [] { "lastName" } ).toArray();
		Assert.assertEquals(ca.length, 2);
		Assert.assertTrue(ca[0].getId() != ca[1].getId());
		Assert.assertTrue(ca[0].getId() == 2 || ca[0].getId() == 5);
		Assert.assertTrue(ca[1].getId() == 2 || ca[1].getId() == 5);
	}

}
