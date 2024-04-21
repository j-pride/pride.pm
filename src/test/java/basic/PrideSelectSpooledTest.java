package basic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.pride.ResultIterator;
import pm.pride.ResultIterator.SpoolCondition;

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
public class PrideSelectSpooledTest extends AbstractPrideTest {

	private static int COUNT = 100;

	@Override
	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
		generateCustomer(COUNT);
	}

	@Test
	public void testGroupedByFirstName() throws Exception {
		ResultIterator iter = new Customer().query("1=1 order by firstName");
		List<?> allCustomersWithSameFirstName = null;
		int smallestGroupSize = Integer.MAX_VALUE;
		int numberOfGroups = 0;
		do {
			String currentFirstName = iter.getObject(Customer.class).getFirstName();
			allCustomersWithSameFirstName = iter.spoolToList(Customer.class,
					customer -> customer.getFirstName().equals(currentFirstName));
			if (allCustomersWithSameFirstName != null) {
				numberOfGroups++;
				smallestGroupSize = Math.min(smallestGroupSize, allCustomersWithSameFirstName.size());
			}
		} while(allCustomersWithSameFirstName != null);
		assertEquals(10, numberOfGroups);
		assertEquals(1, smallestGroupSize); // The group of customer 'First'
	}
	
	/** Tests a special case from the spooling point of view as the first result from a
	 * query is always directly loaded into the ResultIterator's entity. So having only
	 * one result immediately finishes the spooling process right from the start
	 */
	@Test
	public void testOneResult() throws Exception {
		ResultIterator iter = new Customer().query("firstName='First'");
		List<Customer> results = iter.spoolToList(Customer.class, customer -> true);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals("First", results.get(0).getFirstName());
		assertTrue(iter.isClosed());
		assertNull(iter.spoolToList(Customer.class, customer -> true));
	}
	
	@Test
	public void testAllAtOnce() throws Exception {
		ResultIterator iter = new Customer().queryAll();
		List<Customer> results = iter.spoolToList(Customer.class, customer -> true);
		assertNotNull(results);
		assertEquals(COUNT, results.size());
		assertTrue(iter.isClosed());
		assertNull(iter.spoolToList(Customer.class, customer -> true));
	}

	@Test
	public void testNoProgress() throws Exception {
		ResultIterator iter = new Customer().queryAll();
		
		List<Customer> results = iter.spoolToList(Customer.class, customer -> false);
		assertNotNull(results);
		assertEquals(0, results.size());
		assertFalse(iter.isClosed());
		
		results = iter.spoolToList(Customer.class, customer -> false);
		assertNotNull(results);
		assertEquals(0, results.size());
		assertFalse(iter.isClosed());
		
		// Indirect proof, that the spooling calls above didn't produce any progress at all
		results = iter.spoolToList(Customer.class, customer -> true);
		assertNotNull(results);
		assertEquals(COUNT, results.size());
		assertTrue(iter.isClosed());
	}

	@Test
	public void testSingleResults() throws Exception {
		ResultIterator iter = new Customer().queryAll();
		int groupCount = 0;
		List<Customer> results;
		do {
			SpoolCondition<Customer> spoolOneResult = new SpoolCondition<Customer>() {
				int count = 0;
				@Override
				public boolean spool(Customer entity) {
					count++;
					return count==1;
				}
			};
			
			results = iter.spoolToList(Customer.class, spoolOneResult);
			if (results != null) {
				assertEquals(1, results.size());
				groupCount++;
			}
		} while(results != null);
		assertEquals(COUNT, groupCount);
	}
	
}
