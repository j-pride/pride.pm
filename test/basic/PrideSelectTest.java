package basic;
import org.junit.Test;

import de.mathema.pride.DatabaseFactory;
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
import de.mathema.pride.ResultIterator;

/**
 * @author bart57
 *
 * Class to Test the Select-Behaviour of the PriDE-Framework
 */
public class PrideSelectTest extends AbstractPrideTest {

	private static int COUNT = 100;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		generateCustomer(COUNT);
	}

	@Test
	public void testSelectByKey() throws Exception{
		Customer c = new Customer(1);
		assertEquals("First", c.getFirstName());
		assertEquals("Customer", c.getLastName());
		c = new Customer(COUNT);
		assertEquals("Last", c.getFirstName());
		assertEquals("Customer", c.getLastName());
	}
	
	@Test
	public void testSelectAll() throws Exception{
		Customer c = new Customer();
		ResultIterator it = c.queryAll();
		it.setFetchSize(it.getFetchSize()); // Just call the functions to ensure they don't fail
		int counter = 0;
		do {
			counter++;
		} while (it.next());
		assertEquals(counter,COUNT);
	}
	
	@Test
	public void testSelectByWildcard() throws Exception {
		Customer c = new Customer();
		c.setFirstName("H%");
		ResultIterator it = c.wildcard(new String[] {"firstName"});
		do {
			assertTrue(c.getFirstName().startsWith("H"));
			assertTrue((c.getLastName().startsWith("K") || c.getLastName().startsWith("I")));
		} while (it.next());	
	}
    
	@Test
    public void testQueryByExample() throws Exception {
        Customer c = new Customer();
        c.setLastName("Customer");
        ResultIterator it = c.query(new String[] {"lastName"});
        int numRecords = 0;
        do {
            assertTrue(c.getLastName().equals("Customer"));
            assertTrue(
                    c.getFirstName().equals("Last") ||
                    c.getFirstName().equals("First"));
            numRecords++;
        } while (it.next());
        assertEquals(2, numRecords);
    }
	
	@Test
    public void testQueryByEmptyExample() throws Exception {
        Customer c = new Customer();
        ResultIterator it = c.query(new String[] {});
        int numRecords = 0;
        do { numRecords++; } while (it.next());
        assertEquals(COUNT, numRecords);
    }
    
	@Test
	public void testSelectByWildcardTwoColumns() throws Exception {
		Customer c = new Customer();
		c.setFirstName("H%");
		c.setLastName("Kl%");
		ResultIterator it = c.wildcard(new String[] {"firstName", "lastName"});
		do {
			assertTrue(c.getFirstName().startsWith("Hajo"));
			assertTrue(c.getLastName().startsWith("Klick"));
		} while (it.next());		
	} 
	
	@Test
	public void testSelectToArray() throws Exception {
		Customer c = new Customer();
		Customer[] result = (Customer[])c.queryAll().toArray();
		assertEquals(result.length, COUNT);
		for (int i = 0; i < result.length; i++)
			assertEquals(result[i].getId(), i+1);
	}
    
	@Test(expected = RuntimeException.class)
	public void testIllegalSelect() throws Exception {
		Customer c = new Customer();
		c.find(new String[] { "unknown" });
		fail("Illegal select should have thrown an exception");
	}
	
	@Test
	public void testSelectPlain() throws Exception {
		String select = "select firstName from " + TEST_TABLE + " where firstName='First'";
		ResultIterator iter = DatabaseFactory.getDatabase().sqlQuery(select);
		assertNotNull(iter);
		assertTrue(iter.next());
		assertEquals("First", iter.getString(1));
		assertFalse(iter.next());
	}

	@Test
	public void testSelectPlainPrepared() throws Exception {
		String select = "select firstName from " + TEST_TABLE + " where firstName=?";
		ResultIterator iter = DatabaseFactory.getDatabase().sqlQuery(select, "First");
		assertNotNull(iter);
		assertTrue(iter.next());
		assertEquals("First", iter.getString(1));
		assertFalse(iter.next());
	}
}
