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

import org.junit.Test;

import de.mathema.pride.*;


/**
 * @author bart57
 *
 * Test the usage of prepared operations in PriDE
 */
public class PridePreparedTest extends AbstractPrideTest {

	private static final int COUNT = 9;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		generateCustomer(COUNT);
	}
	
	@Test
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

	@Test
	public void testPreparedAutoInsert() throws Exception {
		AutoCustomer c = new AutoCustomer();
		c.setId(COUNT+1);
		PreparedInsert pi = new PreparedInsert(new String[] {"firstName"}, c.getDescriptor());
		pi.execute(c);
		c.find();
		assertNull(c.getFirstName());
	}

	@Test
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
		assertEquals(c.getLastName(), "Updated2");
		assertNotNull(c.getActive());
		assertEquals(c.getActive().booleanValue(), true);
		c.setId(1);
		c.find();
		assertEquals(c.getLastName(), "Updated1");
	}

	@Test
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
		assertEquals(ca.length, 2);
		assertTrue(ca[0].getId() != ca[1].getId());
		assertTrue(ca[0].getId() == 7 || ca[0].getId() == 8);
		assertTrue(ca[1].getId() == 7 || ca[1].getId() == 8);
	}

	@Test
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
		assertEquals(ca.length, 3);
	}
	
	@Test
	public void testIllegalPreparedUpdate() throws Exception {
		Customer c = new Customer();
		try {
			PreparedUpdate pu = new PreparedUpdate(new String[] { "unknown" }, c.getDescriptor());
			pu.execute(c);
		}
		catch(IllegalAccessException iax) { return; } /* Expected */
		fail();
	}


}
