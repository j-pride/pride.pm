package basic;
import java.sql.SQLException;

import org.junit.Test;

public class PrideDateWithBindVariablesTest extends AbstractPrideTest {

	PrideDateTest prideDateTest = new PrideDateTest();
	
    @Override
	public void setUp() throws Exception {
    	prideDateTest.setUp();
    	setBindvarsDefault(true);
	}

	@Override
	public void tearDown() throws Exception {
		setBindvarsDefault(null);
	}

	@Test
	public void testInsert() throws Exception{
		prideDateTest.testInsert();
	}

	@Test
	public void testInsertWithServerTime() throws Exception {
		//TODO JL: This doesn't work yet
		//prideDateTest.testInsertWithServerTime();
	}

	@Test
	public void testJavaUtilDateAsDate() throws Exception {
		prideDateTest.testJavaUtilDateAsDate();
	}

	@Test
	public void testTimestampAcceptedForDate() throws Exception {
		prideDateTest.testTimestampAcceptedForDate();
	}
	
	@Test
	public void testUpdateNoDBDate() throws Exception {
		prideDateTest.testUpdateNoDBDate();
	}

	@Test
	public void testUpdateWithDBDate() throws Exception {
		//TODO JL: This doesn't work yet
		//prideDateTest.testUpdateWithDBDate();
	}
	
	@Test
	public void testEqualDatesMillisecondsCorrectlyIgnored() throws SQLException {
		prideDateTest.testEqualDatesMillisecondsCorrectlyIgnored();
	}
	
	@Test
	public void testTimestampWithMilliseconds() throws Exception {
		prideDateTest.testTimestampWithMilliseconds();
	}
	
}
