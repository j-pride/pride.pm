package util;

public class CreateCustomerTable extends AbstractCreateTable {
	public static void main(String[] args) throws Exception {
		createTable("CUSTOMER",
			"id integer not null primary key",
			"name varchar(20)",
			"first_name varchar(30)"
		);
	}
}
