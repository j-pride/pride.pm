package modify;

import static modify.AutoIncCustomer.*;

import java.sql.SQLException;

import pm.pride.Database;
import pm.pride.DatabaseFactory;
import pm.pride.SQL;
import pm.pride.WhereCondition;
import util.ResourceAccessorExampleConfig;

public class AutoIncCustomerClient {

	public static void main(String[] args) throws Exception {
		ResourceAccessorExampleConfig.initPriDE();

		create10Customers();
		createAndUpdate();
		renamePaddyToPatrickByEntity();
		renamePaddyToPatrickByDatabase();
	}

	private static void renamePaddyToPatrickByDatabase() throws SQLException {
		Database database = DatabaseFactory.getDatabase();
		String operation = SQL.build(
				"update @CUSTOMER set @FIRST_NAME = 'Patrick' where ( @FIRST_NAME = 'Paddy' ) ",
				TABLE, COL_FIRST_NAME);
		int updates = database.sqlUpdate(operation);
		database.commit();
		System.out.println(updates + " row(s) updated");
	}

	private static void renamePaddyToPatrickByEntity() throws SQLException {
		AutoIncCustomer customer = new AutoIncCustomer();
		customer.setFirstName("Patrick");
		int updates = customer.update(new WhereCondition(COL_FIRST_NAME, "Paddy"), COL_FIRST_NAME);
		customer.commit();
		System.out.println(updates + " row(s) updated");
	}

	private static void createAndUpdate() throws SQLException {
		AutoIncCustomer paddy = new AutoIncCustomer(57);
		paddy.setFirstName("Paddy");
		paddy.update();
		paddy.commit();
	}

	private static void create10Customers() throws SQLException {
		AutoIncCustomer customer = new AutoIncCustomer();
		for (int i = 0; i < 10; i++) {
			customer.setName("Fingal-" + i);
			customer.setFirstName("Paddy-" + i);
			customer.create();
			System.out.println(customer.getId());
		}
		customer.commit();
	}
}
