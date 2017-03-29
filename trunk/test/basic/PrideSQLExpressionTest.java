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

import de.mathema.pride.DatabaseFactory;
import de.mathema.pride.ResultIterator;
import de.mathema.pride.SQLExpression;

/**
 * @author bart57
 *
 * Class to Test the Select-Behaviour of the PriDE-Framework
 */
public class PrideSQLExpressionTest extends PrideBaseTest {

	private static int COUNT = 100;


	public PrideSQLExpressionTest(String name)  {
		super(name);
	}


	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		generateCustomer(COUNT);
	}
	
	public void testOrderByAsc() throws Exception {
		SQLExpression expression = new SQLExpression(DatabaseFactory.getDatabase(), null, "id");
		checkOrderByResult(expression, 1, COUNT);
	}
	
	public void testWhereAndOrderBy() throws Exception {
		SQLExpression expression = new SQLExpression(DatabaseFactory.getDatabase(), "id in (3,5)");
		expression = expression.orderBy("id", SQLExpression.Direction.ASC);
		checkOrderByResult(expression, 3, 5);
	}


	public void testOrderByDesc() throws Exception {
		SQLExpression expression = new SQLExpression(DatabaseFactory.getDatabase()).orderBy("id", SQLExpression.Direction.DESC);
		checkOrderByResult(expression, COUNT, 1);
	}
	
	public void testMultipleOrderByDesc() throws Exception {
		SQLExpression expression = new SQLExpression(DatabaseFactory.getDatabase(), "firstName >= 'First' AND firstName <= 'Last'").orderBy("firstName", SQLExpression.Direction.DESC).orderBy("id", SQLExpression.Direction.DESC);
		Customer c = new Customer();
		ResultIterator ri = c.query(expression.toString());
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
	
	private void checkOrderByResult(SQLExpression expression, int firstId, int lastId) throws SQLException {
		Customer c = new Customer();
		ResultIterator ri = c.query(expression.toString());
		Customer[] array = (Customer[]) ri.toArray(COUNT);
    	assertEquals(firstId, array[0].getId());
    	assertEquals(lastId, array[array.length - 1].getId());
	}
}
