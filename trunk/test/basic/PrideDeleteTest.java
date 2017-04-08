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

import de.mathema.pride.DatabaseFactory;
import de.mathema.pride.NoResultsException;
import de.mathema.pride.ResultIterator;


/**
 * @author bart57
 *
 * Class to Test the Delete-Behaviour of the PriDE-Framework
 */
public class PrideDeleteTest extends AbstractPrideTest {

	private static final int COUNT = 10;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		generateCustomer(COUNT);
	}
	
	@Test
	public void testDelete() throws Exception {
		Customer c = new Customer();
		ResultIterator it =	c.queryAll();
		int counter = 0;
		do {
			c.delete();
			counter++;
		} while (it.next());
		DatabaseFactory.getDatabase().commit();
		assertEquals(COUNT, counter);
		it = null;
		try {
			it = c.queryAll();
		} catch (Exception e) {
			assertTrue(e instanceof NoResultsException);	
		}
		assertNull(it);
	}

}
