package basic;
import org.junit.Test;

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
import de.mathema.pride.DatabaseFactory;
import de.mathema.pride.ResourceAccessor;
import de.mathema.pride.ResourceAccessorJ2SE;
import de.mathema.pride.ResultIterator;
import de.mathema.pride.WhereCondition;

/**
 * @author bart57
 *
 * Class to Test the Insert-Behaviour of the PriDE-Framework
 */
public class PrideInsertTestWithBindVariables extends AbstractPrideTest {
	
	PrideInsertTest prideInsertTest = new PrideInsertTest();

	
    @Override
	public void setUp() throws Exception {
    	prideInsertTest.setUp();
    	WhereCondition.setBindDefault(true);
	}

	@Override
	public void tearDown() throws Exception {
    	WhereCondition.setBindDefault(false);
	}

	@Test
	public void testInsert() throws Exception{
		prideInsertTest.testInsert();
	}

    @Test
	public void testAutoInsert() throws Exception {
    	prideInsertTest.testAutoInsert();
	}

}
