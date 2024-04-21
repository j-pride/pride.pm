package basic;
import static pm.pride.WhereCondition.Operator.UNEQUAL;

import java.sql.SQLException;

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.pride.WhereCondition;

/**
 * @author less02
 *
 * Class to test select behavior using {@link WhereCondition} with bind variables. The test
 * simply performs exactly the same queries as {@link PrideWhereConditionTest} but forces
 * PriDE to use bind variables by default.
 */
public class PrideWhereConditionWithBindVariablesTest extends AbstractPrideTest {

	PrideWhereConditionTest prideWhereConditionTest = new PrideWhereConditionTest();
	
	@Override
	@BeforeEach
	public void setUp() throws Exception {
		prideWhereConditionTest.setUp();
		setBindvarsDefault(true);
	}

	@Override
	@AfterEach
	public void tearDown() {
		setBindvarsDefault(null);
	}
	
	@Test
	public void testEqualsExpression() throws Exception {
		prideWhereConditionTest.testEqualsExpression();
	}
	
	@Test
	public void testEqualsWithNull() throws Exception {
		prideWhereConditionTest.testEqualsWithNull();
	}

	@Test
	public void testUnequalsWithNull() throws Exception {
		prideWhereConditionTest.testUnequalsWithNull();
	}
	
	@Test
	public void testEqualsWithNullSkipped() throws Exception {
		prideWhereConditionTest.testEqualsWithNullSkipped();
	}
	
	@Test
	public void testEqualDates() throws SQLException {
		prideWhereConditionTest.testEqualDates();
	}
	
	@Test
	public void testBind() throws Exception {
		prideWhereConditionTest.testBind();
	}
	
	@Test
	public void testOrderByAsc() throws Exception {
		prideWhereConditionTest.testOrderByAsc();
	}
	
	@Test
	public void testWhereAndOrderBy() throws Exception {
		prideWhereConditionTest.testWhereAndOrderBy();
	}

	@Test
	public void testOrderByDesc() throws Exception {
		prideWhereConditionTest.testOrderByDesc();
	}
	
	@Test
	public void testMultipleOrderByDesc() throws Exception {
		prideWhereConditionTest.testMultipleOrderByDesc();
	}
	
	@Test
	public void testSubcondition() throws Exception {
		prideWhereConditionTest.testSubcondition();
	}
	
}
