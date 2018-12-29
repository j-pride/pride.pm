package query;

import java.sql.SQLException;
import java.util.List;

import pm.pride.ResultIterator;
import pm.pride.SQL;
import pm.pride.WhereCondition;
import static pm.pride.WhereCondition.Operator.*;
import static quickstart.Customer.*;
import quickstart.Customer;
import util.ResourceAccessorExampleConfig;

public class QueryClient {
	public static void main(String[] args) throws Exception {
		ResourceAccessorExampleConfig.initPriDE();
		findCustomerById();
		printAllCustomers();
		printAllCustomersAsList();
		printPaddysByExample();
		printPaddysByWhereCondition();
		printPaddysAndMarys();
		printOldMickeyMouses();
		printOldMickeyMousesWithExpressionBuilder();
		printNameEqualsFirstName();
		printAllCustomersOrdered();
	}

	private static void printOldMickeyMousesWithExpressionBuilder() throws SQLException {
		String byMickeyMouse = SQL.build(
			"@ID < 1000 AND (" +
			"    @FIRST_NAME IN ('@fname', '@lname') OR " +
			"    @NAME IN ('@fname', '@lname')" +
			")",
			COL_ID,
			COL_FIRST_NAME, "Mickey", "Mouse",
			COL_NAME);
		printCustomers(new Customer().query(byMickeyMouse));
	}

	private static void printAllCustomersOrdered() throws SQLException {
		WhereCondition orderedByNameAndFirstName = new WhereCondition()
				.orderBy(COL_NAME).orderBy(COL_FIRST_NAME);
		printCustomers(new Customer().query(orderedByNameAndFirstName));
	}

	private static void printNameEqualsFirstName() throws SQLException {
		WhereCondition byNameEqualsFirstName = new WhereCondition()
				.and(COL_NAME, SQL.raw(COL_FIRST_NAME));
		printCustomers(new Customer().query(byNameEqualsFirstName));
	}

	private static void printOldMickeyMouses() throws SQLException {
		WhereCondition byMickeyMouse = new WhereCondition()
			.and(COL_ID, LESS, 1000)
			.and()
				.or(COL_FIRST_NAME, IN, "Mickey", "Mouse")
				.or(COL_NAME, IN, "Mickey", "Mouse")
			.bracketClose();
		printCustomers(new Customer().query(byMickeyMouse));
	}

	private static void printPaddysAndMarys() throws Exception {
		WhereCondition byPaddysAndMarys = new WhereCondition()
				.and(COL_FIRST_NAME, IN, "Paddy", "Mary");
		printCustomers(new Customer().query(byPaddysAndMarys));
	}

	private static void printPaddysByWhereCondition() throws SQLException {
		WhereCondition byFirstNameAndEmptyName = new WhereCondition()
				.and(Customer.COL_FIRST_NAME, "Paddy")
				.and(Customer.COL_NAME, null);
		ResultIterator ri = new Customer().query(byFirstNameAndEmptyName);
		printCustomers(ri);
	}

	private static void printCustomers(ResultIterator ri) throws SQLException {
		if (ri != null) {
		    do {
		        System.out.println(ri.getObject());
		    }
		    while(ri.next());
		}
		System.out.println();
	}

	private static void printPaddysByExample() throws SQLException {
		Customer customer = new Customer();
		customer.setFirstName("Paddy");
		List<Customer> paddys = customer.toList(customer.queryByExample(Customer.COL_FIRST_NAME, Customer.COL_NAME));
		System.out.println(paddys);
	}

	private static void printAllCustomersAsList() throws SQLException {
		Customer customer = new Customer();
		List<Customer> allCustomers = customer.toList(customer.queryAll());
		System.out.println(allCustomers);
	}

	private static void findCustomerById() throws SQLException {
		Customer customer = new Customer(1);
		System.out.println(customer);
		System.out.println();
	}

	private static void printAllCustomers() throws SQLException {
		Customer customer = new Customer();
		ResultIterator ri = customer.queryAll();
		if (ri != null) {
		    do {
		        System.out.println(customer);
		    }
		    while(ri.next());
		}
		else {
		    System.out.println("No customers found");
		}
		System.out.println();
	}
	
}
