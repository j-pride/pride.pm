package basic;
import org.junit.Test;

import pm.pride.DatabaseFactory;
import pm.pride.ResourceAccessor;
import pm.pride.ResourceAccessorJSE;
import pm.pride.ResultIterator;
import pm.pride.WhereCondition;

/**
 * @author bart57
 *
 * Class to Test the Insert-Behaviour of the PriDE-Framework when using bind-variables
 */
public class PrideInsertWithBindVariablesTest extends AbstractPrideTest {
	
	PrideInsertTest prideInsertTest = new PrideInsertTest();

	
    @Override
	public void setUp() throws Exception {
    	prideInsertTest.setUp();
    	setBindvarsDefault(true);
	}

	@Override
	public void tearDown() throws Exception {
		setBindvarsDefault(null);
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
