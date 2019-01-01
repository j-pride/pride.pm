package inherit;

import java.sql.SQLException;

import pm.pride.FindException;
import pm.pride.ResultIterator;
import quickstart.CustomerClient;
import util.AbstractCommandLineClient;

/**
 * This client has the same functionality as {@link CustomerClient} from the quick start tutorial
 * operating on the same table but is based on the entity class {@link DerivedCustomer}.
 * The code is completely redundant on purpose to avoid the quick start tutorial
 * being dependent on other chapters' example code.
 */
public class DerivedCustomerClient extends AbstractCommandLineClient {

	public void create(int id, String name, String firstName) throws SQLException {
		DerivedCustomer customer = new DerivedCustomer();
		customer.setId(id);
		customer.setName(name);
		customer.setFirstName(firstName);
		customer.create();
		System.out.println("Customer " + customer.getId() + " created");
	}
	
	public void find(int id) throws SQLException {
		try {
			DerivedCustomer customer = new DerivedCustomer(id);
			printCustomer(customer);
		}
		catch(FindException x) {
			System.err.println("not found");
		}
	}

	public void query(String name) throws SQLException {
		DerivedCustomer customer = new DerivedCustomer();
		customer.setName(name);
		ResultIterator ri = customer.queryByExample(DerivedCustomer.COL_NAME);
		printCustomers(ri);
	}

	public void list() throws SQLException {
		DerivedCustomer customer = new DerivedCustomer();
		ResultIterator ri = customer.queryAll();
		printCustomers(ri);
	}

	private void printCustomers(ResultIterator ri) throws SQLException {
		if (!ri.isNull()) {
			DerivedCustomer customer = ri.getObject(DerivedCustomer.class);
			do {
				printCustomer(customer);
			} while(ri.next());
		}
		else {
			System.err.println("no results");
		}
	}
	
	private void printCustomer(DerivedCustomer customer) {
		System.out.println(customer.getId() + ": name=" + customer.getName() + ", firstName=" + customer.getFirstName());
	}

	public static void main(String[] args) throws Exception {
		new DerivedCustomerClient().
		registerCommand("create a new customer", "create", "id", "name", "firstName").
		registerCommand("find a customer by its ID", "find", "id").
		registerCommand("query customers by their name", "query", "name").
		registerCommand("list all customers", "list").
		work();
	}
}
