package pm.pride.basic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.pride.DatabaseFactory;
import pm.pride.ResultIterator;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

	private static final int COUNT = 100;
	private static final int UNKNOWN_ID = COUNT + 1;


	@Override
	@BeforeEach
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

	@Test
	public void findXEThrowsExceptionOnMissingResult() throws Exception {
		Customer c = new Customer();
		c.setId(UNKNOWN_ID);
		assertThrows(SQLException.class, c::findXE);
	}

	@Test
	public void findRCReturnsCopyOnMatch() throws Exception {
		Customer c = new Customer();
		c.setId(1);
		Customer fc = c.findRC(Customer.class);
		assertNotNull(fc);
        assertNotSame(fc, c);
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
        assertNull(c.getFirstName());
	}

	@Test
	public void testSelectAll() throws Exception{
		Customer c = new Customer();
		try(ResultIterator it = c.queryAll()) {
			it.setFetchSize(it.getFetchSize()); // Just call the functions to ensure they don't fail
			int counter = 0;
			do {
				counter++;
			} while (it.next());
			assertEquals(counter, COUNT);
		}
	}

	@Test
	public void testSelectByWildcard() throws Exception {
		Customer c = new Customer();
		c.setFirstName("H%");
		try(ResultIterator it = c.wildcard(new String[] {"firstName"})) {
			do {
				assertTrue(c.getFirstName().startsWith("H"));
				assertTrue((c.getLastName().startsWith("K") || c.getLastName().startsWith("I")));
			} while (it.next());
		}
	}

	@Test
    public void testQueryByExample() throws Exception {
        Customer c = new Customer();
        c.setLastName("Customer");
        ResultIterator it = c.queryByExample("lastName");
        int numRecords = 0;
        do {
            assertEquals("Customer", c.getLastName());
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
        try(ResultIterator it = c.queryByExample()) {
			int numRecords = 0;
			do {
				numRecords++;
			} while (it.next());
			assertEquals(COUNT, numRecords);
		}
    }

	@Test
	public void testSelectByWildcardTwoColumns() throws Exception {
		Customer c = new Customer();
		c.setFirstName("H%");
		c.setLastName("Kl%");
		try(ResultIterator it = c.wildcard(new String[] {"firstName", "lastName"})) {
			do {
				assertTrue(c.getFirstName().startsWith("Hajo"));
				assertTrue(c.getLastName().startsWith("Klick"));
			} while (it.next());
		}
	}

	@Test
	public void testSelectToArray() throws Exception {
		Customer c = new Customer();
		Customer[] result = (Customer[])c.queryAll().toArray();
		assertEquals(COUNT, result.length);
		for (int i = 0; i < result.length; i++) {
			assertEquals(result[i].getId(), i+1);
		}
	}

	@Test
	public void testSelectToBoundedArray() throws Exception {
		Customer c = new Customer();
		ResultIterator iter = c.queryAll();
		Customer[] result = (Customer[])iter.toArray(COUNT-2);
		assertEquals(COUNT-2, result.length);
		assertTrue(iter.isClosed());
	}

	@Test
	public void testSelectEmptyArray() throws Exception {
		Customer c = new Customer();
		Customer[] result = c.query("id=-1").toArray(Customer.class);
		assertEquals(0, result.length);
	}

	@Test
	public void testIllegalSelect() throws Exception {
		assertThrows(RuntimeException.class, () -> selectIllegal(),
				"Illegal select should have thrown an exception");
	}

	protected void selectIllegal() throws Exception {
        Customer c = new Customer();
		c.findByExample(new String[] { "unknown" });
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

	@Test
	public void testAutoClose() throws Exception {
		Customer c = new Customer();
		ResultIterator riOut;

		try(ResultIterator ri = c.queryAll()) {
			ri.next();
			riOut = ri;
			assertFalse(riOut.isClosed());
		}

		assertTrue(riOut.isClosed());

	}
}
