package basic;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author bart57
 *
 * Class to Test the Update-Behaviour of the PriDE-Framework
 */
public class PrideUpdateWithBindVariablesTest extends AbstractPrideTest {

	PrideUpdateTest prideUpdateTest = new PrideUpdateTest();
	
	@Override
	@BeforeEach
	public void setUp() throws Exception {
		prideUpdateTest.setUp();
		setBindvarsDefault(true);
	}

	@Override
	@AfterEach
	public void tearDown() {
		setBindvarsDefault(null);
	}
	
	@Test
	public void testUpdatePK() throws Exception{
		prideUpdateTest.testUpdatePK();
	}

	@Test
	public void testUpdateByExample() throws Exception{
		prideUpdateTest.testUpdateByExample();
	}

	@Test
	public void testUpdateFields() throws Exception{
		prideUpdateTest.testUpdateFields();
	}

	@Test
	public void testUpdateMultiple() throws Exception{
		prideUpdateTest.testUpdateMultiple();
	}

	@Test
	public void testUpdateWhere() throws Exception{
		prideUpdateTest.testUpdateWhere();
	}

}
