package quickstart;

import java.sql.SQLException;

import pm.pride.ResourceAccessorJSE;
import pm.pride.ResultIterator;
import util.AbstractCommandLineClient;

public class CustomerClient extends AbstractCommandLineClient {

	public void create(int id, String name, String firstName) throws SQLException {
		Customer customer = new Customer();
		customer.setId(id);
		customer.setName(name);
		customer.setFirstName(firstName);
		customer.create();
		System.out.println("Customer created");
		customer.commit();
	}
	
	public void find(int id) throws SQLException {
		Customer customer = new Customer(id);
		if (customer.find()) {
			printCustomer(customer);
		}
		else {
			System.err.println("not found");
		}
	}

	public void query(String name) throws SQLException {
		Customer customer = new Customer();
		customer.setName(name);
		ResultIterator ri = customer.queryByExample(Customer.COL_NAME);
		printCustomers(ri);
	}

	public void list() throws SQLException {
		Customer customer = new Customer();
		ResultIterator ri = customer.queryAll();
		printCustomers(ri);
	}

	private void printCustomers(ResultIterator ri) throws SQLException {
		if (ri != null) {
			Customer customer = (Customer)ri.getObject();
			do {
				printCustomer(customer);
			} while(ri.next());
		}
		else {
			System.err.println("no results");
		}
	}
	
	private void printCustomer(Customer customer) {
		System.out.println(customer.getId() + ": name=" + customer.getName() + ", firstName=" + customer.getFirstName());
	}

	public static void main(String[] args) throws Exception {
		ResourceAccessorJSE.fromSystemProps();
		new CustomerClient().
		registerCommand("create a new customer", "create", "id", "name", "firstName").
		registerCommand("find a customer by its ID", "find", "id").
		registerCommand("query customers by their name", "query", "name").
		registerCommand("list all customers", "list").
		work();
	}
}
