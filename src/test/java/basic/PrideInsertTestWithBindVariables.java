package basic;
import org.junit.Test;

import pm.pride.DatabaseFactory;
import pm.pride.ResourceAccessor;
import pm.pride.ResourceAccessorJ2SE;
import pm.pride.ResultIterator;
import pm.pride.WhereCondition;

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
