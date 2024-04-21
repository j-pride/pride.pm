package basic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author bart57
 *
 * Class to Test the Insert-Behaviour of the PriDE-Framework when using bind-variables
 */
public class PrideInsertWithBindVariablesTest extends AbstractPrideTest {
	
	PrideInsertTest prideInsertTest = new PrideInsertTest();

	
    @Override
	@BeforeEach
	public void setUp() throws Exception {
    	prideInsertTest.setUp();
    	setBindvarsDefault(true);
	}

	@Override
	@AfterEach
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
