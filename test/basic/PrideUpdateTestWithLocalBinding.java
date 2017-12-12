package basic;
import java.sql.SQLException;

import de.mathema.pride.RecordDescriptor;

/**
 * @author Jeismann
 *
 * Class to Test the Update-Behaviour of the PriDE-Framework
 */
public class PrideUpdateTestWithLocalBinding extends PrideUpdateTest {
	
	protected CustomerWithLocalBinding createCustomer() {
		return new CustomerWithLocalBinding();
	}

	protected CustomerWithLocalBinding createCustomer(int id) throws SQLException {
		return new CustomerWithLocalBinding(id);
	}

	private class CustomerWithLocalBinding extends Customer {
		public CustomerWithLocalBinding() {
		}

		public CustomerWithLocalBinding(int id) throws SQLException {
			super(id);
		}

		@Override
		protected RecordDescriptor getDescriptor() {
			RecordDescriptor descriptor = super.getDescriptor();
			// activate the binding just for this class
			descriptor.setWithBind(true);
			return descriptor;
		}
	}

}
