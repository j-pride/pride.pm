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
import junit.framework.Assert;

import de.mathema.pride.*;


/**
 * @author bart57
 *
 * Test the usage of prepared operations in PriDE
 */
public class PridePreparedTest extends PrideBaseTest {

	private static final int COUNT = 9;

	public PridePreparedTest(String name) {
		super(name);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		generateCustomer(COUNT);
	}
	
	public void testPreparedInsert() throws Exception {
		Customer c = new Customer();
		c.setId(COUNT+1);
		PreparedInsert pi = new PreparedInsert(c.getDescriptor());
		pi.execute(c);
		c.setId(c.getId() + 1);
		pi.execute(c);
		c.setId(c.getId() + 1);
		pi.execute(c);
		c.find();
	}

	public void testPreparedAutoInsert() throws Exception {
		AutoCustomer c = new AutoCustomer();
		c.setId(COUNT+1);
		PreparedInsert pi = new PreparedInsert(new String[] {"firstName"}, c.getDescriptor());
		pi.execute(c);
		c.find();
		Assert.assertNull(c.getFirstName());
	}

	public void testPreparedUpdate() throws Exception {
		Customer c = new Customer(1);
		c.setLastName("Updated1");
		PreparedUpdate pu = new PreparedUpdate(c.getDescriptor());
		pu.execute(c);
		c = new Customer(2);
		c.setLastName("Updated2");
		c.setActive(Boolean.TRUE);
		pu.execute(c);
		c.setLastName("");
		c.setActive(null);
		c.find();
		Assert.assertEquals(c.getLastName(), "Updated2");
		Assert.assertNotNull(c.getActive());
		Assert.assertEquals(c.getActive().booleanValue(), true);
		c.setId(1);
		c.find();
		Assert.assertEquals(c.getLastName(), "Updated1");
	}

	public void testPreparedUpdateMultiple() throws Exception {
		Customer c = new Customer();
		c.setLastName("Updated");
		c.setFirstName("Inge");
		PreparedUpdate pu = new PreparedUpdate
			(new String[] { "firstName" }, new String[] { "lastName" }, c.getDescriptor());
		pu.execute(c);
		pu.close();
		DatabaseFactory.getDatabase().commit();
		Customer[] ca = (Customer[])c.query(new String [] { "lastName" } ).toArray();
		Assert.assertEquals(ca.length, 2);
		Assert.assertTrue(ca[0].getId() != ca[1].getId());
		Assert.assertTrue(ca[0].getId() == 7 || ca[0].getId() == 8);
		Assert.assertTrue(ca[1].getId() == 7 || ca[1].getId() == 8);
	}

	public void testPreparedBatch() throws Exception {
		Customer c = new Customer();
		c.setLastName("Updated");
		PreparedUpdate pu = new PreparedUpdate(new String[] { "id" }, new String[] { "lastName" }, c.getDescriptor());
		c.setId(1); pu.addBatch(c);
		c.setId(2); pu.addBatch(c);
		c.setId(3); pu.addBatch(c);
		pu.executeBatch();
		pu.close();
		DatabaseFactory.getDatabase().commit();
		Customer[] ca = (Customer[])c.query(new String [] { "lastName" } ).toArray();
		Assert.assertEquals(ca.length, 3);
	}
	
	public void testIllegalPreparedUpdate() throws Exception {
		Customer c = new Customer();
		try {
			PreparedUpdate pu = new PreparedUpdate(new String[] { "unknown" }, c.getDescriptor());
			pu.execute(c);
		}
		catch(IllegalAccessException iax) { return; } /* Expected */
		Assert.fail();
	}


}
