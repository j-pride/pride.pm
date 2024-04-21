package basic;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClobBlobWithBindVariablesTest extends AbstractPrideTest {
	ClobBlobTest clobBlobTest = new ClobBlobTest();
	
    @Override
	@BeforeEach
	public void setUp() throws Exception {
    	clobBlobTest.setUp();
    	setBindvarsDefault(true);
	}

	@Override
	@AfterEach
	public void tearDown() throws Exception {
		setBindvarsDefault(null);
	}

	@Test
	public void testInsertReadNull() throws Exception {
		clobBlobTest.testInsertReadNull();
	}
	
	@Test
	public void testInsertReadClob() throws Exception {
		clobBlobTest.testInsertReadClob();
	}
	
	@Test
	public void testInsertReadBlob() throws Exception {
		clobBlobTest.testInsertReadBlob();
	}
	
}
