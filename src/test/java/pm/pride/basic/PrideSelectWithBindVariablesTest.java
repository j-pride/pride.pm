package pm.pride.basic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author bart57
 *
 * Class to Test the Select-Behaviour of the PriDE-Framework
 */
public class PrideSelectWithBindVariablesTest extends AbstractPrideTest {

	PrideSelectTest prideSelectTest = new PrideSelectTest();
	
    @Override
	@BeforeEach
    public void setUp() throws Exception {
		prideSelectTest.setUp();
		setBindvarsDefault(true);
	}

	@Override
	@AfterEach
	public void tearDown() throws Exception {
		setBindvarsDefault(null);
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
    
	@Test
	public void testIllegalSelect() throws Exception {
		assertThrows(RuntimeException.class, () -> prideSelectTest.selectIllegal());
	}
	
}
