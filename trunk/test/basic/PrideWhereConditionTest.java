package basic;
/*******************************************************************************
 * Copyright (c) 2001-2005 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - JUnit test suite
 *******************************************************************************/
import java.sql.SQLException;

import org.junit.Test;

import de.mathema.pride.ResultIterator;
import de.mathema.pride.WhereCondition;

import static de.mathema.pride.WhereCondition.Direction.*;

/**
 * @author bart57
 *
 * Class to Test the Select-Behaviour of the PriDE-Framework
 */
public class PrideWhereConditionTest extends AbstractPrideTest {

	private static int COUNT = 100;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		generateCustomer(COUNT);
	}
	
	@Test
	public void testEqualsExpression() throws Exception {
		WhereCondition expression = new WhereCondition().
				and("firstName", "First").
				and("lastName", "Customer");
		checkOrderByResult(expression, 1, 1);
	}
	
	@Test
	public void testBind() throws Exception {
		WhereCondition expression = new WhereCondition().withBind().
				and("firstName", "First").
				and("lastName", "Customer");
		checkOrderByResult(expression, 1, 1);
	}
	
	@Test
	public void testOrderByAsc() throws Exception {
		WhereCondition expression = new WhereCondition().orderBy("id");
		checkOrderByResult(expression, 1, COUNT);
	}
	
	@Test
	public void testWhereAndOrderBy() throws Exception {
		WhereCondition expression = new WhereCondition("id in (3,5)").orderBy("id", ASC);
		checkOrderByResult(expression, 3, 5);
	}

	@Test
	public void testOrderByDesc() throws Exception {
		WhereCondition expression = new WhereCondition().orderBy("id", DESC);
		checkOrderByResult(expression, COUNT, 1);
	}
	
	@Test
	public void testMultipleOrderByDesc() throws Exception {
		WhereCondition expression = new WhereCondition("firstName >= 'First' AND firstName <= 'Last'").
				orderBy("firstName", DESC).orderBy("id", DESC);
		Customer c = new Customer();
		ResultIterator ri = c.query(expression);
		String lastFirstName = "ZZZ";
		int lastId = 99999;
		if (ri != null){
		    do {
		    	String firstName = c.getFirstName();
		    	int id = c.getId();
		    	if (firstName.equals(lastFirstName)) {
		    		assertTrue(id < lastId);
		    	} else {
		    		lastFirstName = firstName;
		    	}
		    	lastId = id;
		    	assertTrue(firstName.compareTo(lastFirstName) < 1);
		    } while(ri.next());
		}
	}
	
	private void checkOrderByResult(WhereCondition expression, int firstId, int lastId) throws SQLException {
		Customer c = new Customer();
		ResultIterator ri = c.query(expression);
		Customer[] array = (Customer[]) ri.toArray(COUNT);
    	assertEquals(firstId, array[0].getId());
    	assertEquals(lastId, array[array.length - 1].getId());
	}
}
