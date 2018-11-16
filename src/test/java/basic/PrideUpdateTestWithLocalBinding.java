package basic;
import java.sql.SQLException;

import pm.pride.RecordDescriptor;
import pm.pride.WhereCondition;

/**
 * @author Jeismann
 *
 * Class to Test the Update-Behaviour of the PriDE-Framework
 */
public class PrideUpdateTestWithLocalBinding extends PrideUpdateTest {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		new Customer().getDescriptor().setWithBind(true);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
		new Customer().getDescriptor().setWithBind(false);
	}
	
	// test cases are the same as in PrideUpdateTest
}
