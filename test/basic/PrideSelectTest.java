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
import de.mathema.pride.ResultIterator;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author bart57
 *
 * Class to Test the Select-Behaviour of the PriDE-Framework
 */
public class PrideSelectTest extends PrideBaseTest {

	private static int COUNT = 100;


	public PrideSelectTest(String name)  {
		super(name);
	}


	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		generateCustomer(COUNT);
	}
	
	public void testSelectByKey() throws Exception{
		Customer c = new Customer(1);
		Assert.assertEquals("First", c.getFirstName());
		Assert.assertEquals("Customer", c.getLastName());
		c = new Customer(COUNT);
		Assert.assertEquals("Last", c.getFirstName());
		Assert.assertEquals("Customer", c.getLastName());
	}
	
	public void testSelectAll() throws Exception{
		Customer c = new Customer();
		ResultIterator it = c.queryAll();
		it.setFetchSize(it.getFetchSize()); // Just call the functions to ensure they don't fail
		int counter = 0;
		do {
			counter++;
		} while (it.next());
		Assert.assertEquals(counter,COUNT);
	}
	
	public void testSelectByWildcard() throws Exception {
		Customer c = new Customer();
		c.setFirstName("H%");
		ResultIterator it = c.wildcard(new String[] {"firstName"});
		do {
			Assert.assertTrue(c.getFirstName().startsWith("H"));
			Assert.assertTrue((c.getLastName().startsWith("K") || c.getLastName().startsWith("I")));
		} while (it.next());	
	}
    
    public void testQueryByExample() throws Exception {
        Customer c = new Customer();
        c.setLastName("Customer");
        ResultIterator it = c.query(new String[] {"lastName"});
        int numRecords = 0;
        do {
            Assert.assertTrue(c.getLastName().equals("Customer"));
            Assert.assertTrue(
                    c.getFirstName().equals("Last") ||
                    c.getFirstName().equals("First"));
            numRecords++;
        } while (it.next());
        Assert.assertEquals(2, numRecords);
    }
	
    public void testQueryByEmptyExample() throws Exception {
        Customer c = new Customer();
        ResultIterator it = c.query(new String[] {});
        int numRecords = 0;
        do { numRecords++; } while (it.next());
        Assert.assertEquals(COUNT, numRecords);
    }
    
	public void testSelectByWildcardTwoColumns() throws Exception {
		Customer c = new Customer();
		c.setFirstName("H%");
		c.setLastName("Kl%");
		ResultIterator it = c.wildcard(new String[] {"firstName", "lastName"});
		do {
			Assert.assertTrue(c.getFirstName().startsWith("Hajo"));
			Assert.assertTrue(c.getLastName().startsWith("Klick"));
		} while (it.next());		
	} 
	
	public void testSelectToArray() throws Exception {
		Customer c = new Customer();
		Customer[] result = (Customer[])c.queryAll().toArray();
		Assert.assertEquals(result.length, COUNT);
		for (int i = 0; i < result.length; i++)
			Assert.assertEquals(result[i].getId(), i+1);
	}
    
	public void testIllegalSelect() throws Exception {
		Customer c = new Customer();
		try { c.find(new String[] { "unknown" }); }
		catch(RuntimeException rx) { /* expected */
			Assert.assertNotNull(rx.getCause());
			Assert.assertEquals(rx.getCause().getClass(), IllegalAccessException.class);
			return;
		}
		Assert.fail();
	}
	
}
