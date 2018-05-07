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
import de.mathema.pride.ResultIterator;
import de.mathema.pride.WhereCondition;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author bart57
 *
 * Class to Test the Select-Behaviour of the PriDE-Framework
 */
public class PrideSelectTestWithBindVariables extends AbstractPrideTest {

	PrideSelectTest prideSelectTest = new PrideSelectTest();
	
    @Override
    public void setUp() throws Exception {
		prideSelectTest.setUp();
    	WhereCondition.setBindDefault(true);
	}

	@Override
	public void tearDown() throws Exception {
    	WhereCondition.setBindDefault(false);
	}
	
	@Test
	public void testSelectByKey() throws Exception{
		prideSelectTest.testSelectByKey();
	}
	
	@Test
	public void testSelectAll() throws Exception{
		prideSelectTest.testSelectAll();
	}
	
	@Test
	public void testSelectByWildcard() throws Exception {
		prideSelectTest.testSelectByWildcard();
	}
    
	@Test
    public void testQueryByExample() throws Exception {
		prideSelectTest.testQueryByExample();
    }
	
	@Test
    public void testQueryByEmptyExample() throws Exception {
		prideSelectTest.testQueryByEmptyExample();
    }
    
	@Test
	public void testSelectByWildcardTwoColumns() throws Exception {
		prideSelectTest.testSelectByWildcardTwoColumns();
	} 
	
	@Test
	public void testSelectToArray() throws Exception {
		prideSelectTest.testSelectToArray();
	}
    
	@Test(expected = RuntimeException.class)
	public void testIllegalSelect() throws Exception {
		prideSelectTest.testIllegalSelect();
	}
	
}
