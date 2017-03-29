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

import de.mathema.pride.DatabaseFactory;
import de.mathema.pride.NoResultsException;
import de.mathema.pride.ResultIterator;


/**
 * @author bart57
 *
 * Class to Test the Delete-Behaviour of the PriDE-Framework
 */
public class PrideDeleteTest extends PrideBaseTest {

	private static final int COUNT = 10;

	public PrideDeleteTest(String name) {
		super(name);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		generateCustomer(COUNT);
	}
	
	public void testDelete() throws Exception {
		Customer c = new Customer();
		ResultIterator it =	c.queryAll();
		int counter = 0;
		do {
			c.delete();
			counter++;
		} while (it.next());
		DatabaseFactory.getDatabase().commit();
		Assert.assertEquals(COUNT, counter);
		it = null;
		try {
			it = c.queryAll();
		} catch (Exception e) {
			Assert.assertTrue(e instanceof NoResultsException);	
		}
		Assert.assertNull(it);
	}

}
