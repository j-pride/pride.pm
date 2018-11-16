package basic;
import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import pm.pride.ResultIterator;
import pm.pride.WhereCondition;

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
