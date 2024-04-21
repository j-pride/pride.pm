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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.pride.DatabaseFactory;
import pm.pride.ResultIterator;
import pm.pride.WhereCondition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


/**
 * @author bart57
 *
 * Class to Test the Delete-Behaviour of the PriDE-Framework
 */
public class PrideDeleteTest extends AbstractPrideTest {

	private static final int COUNT = 10;

	@Override
	@BeforeEach
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
		it = c.queryAll();
		assertNullResult(it);
	}

    @Test
    public void testDeletePlain() throws Exception {
    	int customerCount = countCustomers();
    	String delete = "delete from " + TEST_TABLE + " where firstName='First'";
    	boolean result = DatabaseFactory.getDatabase().sqlExecute(delete);
    	assertFalse(result);
    	assertEquals(customerCount-1, countCustomers());
    }

    @Test
    public void testDeletePlainWithBind() throws Exception {
    	int customerCount = countCustomers();
    	String delete = "delete from " + TEST_TABLE + " where firstName=? and lastName=?";
    	boolean result = DatabaseFactory.getDatabase().sqlExecute(delete, "First", "Customer");
    	assertFalse(result);
    	assertEquals(customerCount-1, countCustomers());
    }
    
    @Test
    public void testDeleteWithWhereCondition() throws Exception {
    	int customerCount = countCustomers();
    	WhereCondition byLastName = new WhereCondition("lastName", "Customer");
		int deleted = new Customer().delete(byLastName);
		assertEquals(2, deleted);
    	assertEquals(customerCount-deleted, countCustomers());
    }

    @Test
    public void testDeleteWithWhereConditionAndBinding() throws Exception {
    	int customerCount = countCustomers();
    	WhereCondition byFirstName = new WhereCondition().bindvarsOn().and("lastName", "Customer");
		int deleted = new Customer().delete(byFirstName);
		assertEquals(2, deleted);
    	assertEquals(customerCount-deleted, countCustomers());
    }

}
