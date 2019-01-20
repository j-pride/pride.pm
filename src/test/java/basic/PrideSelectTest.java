package basic;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import pm.pride.DatabaseFactory;
import pm.pride.ResultIterator;

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

/**
 * @author bart57
 *
 * Class to Test the Select-Behaviour of the PriDE-Framework
 */
public class PrideSelectTest extends AbstractPrideTest {

	private static int COUNT = 100;
	private static int UNKNOWN_ID = COUNT+1;
	int count;
	int lastId;

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
		c.setId(COUNT);
		assertTrue(c.find());
		assertEquals("Last", c.getFirstName());
		assertEquals("Customer", c.getLastName());
	}
	
	@Test
	public void findReturnsFalseOnMissingResult() throws Exception {
		Customer c = new Customer();
		c.setId(UNKNOWN_ID);
		assertFalse(c.find());
	}
	
	@Test(expected=SQLException.class)
	public void findXEThrowsExceptionOnMissingResult() throws Exception {
		Customer c = new Customer();
		c.setId(UNKNOWN_ID);
		c.findXE();
	}

	@Test
	public void findRCReturnsCopyOnMatch() throws Exception {
		Customer c = new Customer();
		c.setId(1);
		Customer fc = c.findRC(Customer.class);
		assertNotNull(fc);
		assertFalse(fc == c);
		assertNull(c.getFirstName());
		assertNotNull(fc.getFirstName());
	}

	@Test
	public void findRCReturnsNullOnMissingResult() throws Exception {
		Customer c = new Customer();
		c.setId(UNKNOWN_ID);
		Customer fc = c.findRC(Customer.class);
		assertNull(fc);
	}

	@Test
	public void existsReturnsFalseOnMissingResult() throws Exception {
		Customer c = new Customer();
		c.setId(UNKNOWN_ID);
		assertFalse(c.exists());
	}

	@Test
	public void existsReturnsTrueOnMatch() throws Exception {
		Customer c = new Customer();
		c.setId(1);
		assertTrue(c.exists());
		assertEquals(null, c.getFirstName());
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
	public void testSelectByStream() throws Exception{
		Customer c = new Customer();
		count = 0;
		lastId = -1;
		c.queryAll().stream(Customer.class).forEach(customer -> {
			assertNotNull(customer);
			assertNotEquals(c, customer); // Original entity is cloned
			assertNotEquals(lastId, customer.getId()); // And the content differs
			lastId = customer.getId();
			count++;
		});
		assertEquals(count,COUNT);
	}
	
	@Test
	public void testSelectByUnclonedStream() throws Exception{
		Customer c = new Customer();
		count = 0;
		lastId = -1;
		Set<Integer> selectedIds = new HashSet<>();
		c.queryAll().streamOE(Customer.class).forEach(customer -> {
			assertNotNull(customer);
			assertEquals(c, customer); // Original entity is not cloned
			assertNotEquals(lastId, customer.getId()); // But the content differs
			// Produce back-pressure to ensure that slow processing doesn't cause skipping of results
			try { Thread.sleep(10); } catch (InterruptedException ir) {};
			lastId = customer.getId();
			selectedIds.add(lastId);
			count++;
		});
		assertEquals(COUNT, count);
		assertEquals(COUNT, selectedIds.size()); // Back-pressure didn't cause results to be skipped
	}
	
	@Test
	public void testSelectByEmptyStream() throws Exception{
		Customer c = new Customer();
		long count = c.query("id = 0").stream(Customer.class).count();
		assertEquals(count, 0);
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
        ResultIterator it = c.queryByExample("lastName");
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
        ResultIterator it = c.queryByExample();
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
		assertEquals(COUNT, result.length);
		for (int i = 0; i < result.length; i++)
			assertEquals(result[i].getId(), i+1);
	}
    
	@Test
	public void testSelectToBoundedArray() throws Exception {
		Customer c = new Customer();
		ResultIterator iter = c.queryAll();
		Customer[] result = (Customer[])iter.toArray(COUNT-2);
		assertEquals(COUNT-2, result.length);
		assertTrue(iter.isClosed());
	}
    
	@Test(expected = RuntimeException.class)
	public void testIllegalSelect() throws Exception {
		Customer c = new Customer();
		c.findByExample(new String[] { "unknown" });
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
	
	@Test
	public void testSelectPlainPreparedWithAdapter() throws Exception {
        Customer c = new Customer();
        List<Customer> lc = c.query("firstName=?", "First").toList(Customer.class);
        assertEquals(1, lc.size());
	}
	
}
