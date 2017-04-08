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
public class PrideWhereConditionTestWithBindVariables extends AbstractPrideTest {

	PrideWhereConditionTest prideWhereConditionTest = new PrideWhereConditionTest();
	
	@Override
	public void setUp() throws Exception {
		prideWhereConditionTest.setUp();
		WhereCondition.setBindDefault(true);
	}

	@Override
	public void tearDown() {
		WhereCondition.setBindDefault(false);
	}
	
	@Test
	public void testEqualsExpression() throws Exception {
		prideWhereConditionTest.testEqualsExpression();
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
	
}
