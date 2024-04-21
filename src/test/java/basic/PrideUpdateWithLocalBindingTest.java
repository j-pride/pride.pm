package basic;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import pm.pride.RecordDescriptor;
import pm.pride.WhereCondition;

/**
 * @author Jeismann
 *
 * Class to Test the Update-Behaviour of the PriDE-Framework
 */
public class PrideUpdateWithLocalBindingTest extends PrideUpdateTest {
	@Override
	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
		new Customer().getDescriptor().setWithBind(true);
	}

	@Override
	@AfterEach
	public void tearDown() throws Exception {
		super.tearDown();
		new Customer().getDescriptor().setWithBind(false);
	}
	
	// test cases are the same as in PrideUpdateTest
}
