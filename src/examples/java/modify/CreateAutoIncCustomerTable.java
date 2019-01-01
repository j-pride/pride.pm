package modify;

import pm.pride.Database;
import pm.pride.DatabaseFactory;
import pm.pride.ResourceAccessor.DBType;
import util.AbstractCreateTable;
import util.CreateCustomerTable;
import util.ResourceAccessorExampleConfig;

/**
 * Creates the same table as class {@link CreateCustomerTable} with the difference
 * that in makes the ID column an auto-increment column. The code here is completely
 * intentionally redundant to keep from coupling the quick start example to this very
 * special auto-increment example which may not of interest for every padawan.
 */
public class CreateAutoIncCustomerTable extends AbstractCreateTable {
	public static void main(String[] args) throws Exception {
		ResourceAccessorExampleConfig.initPriDE();
		
		Database database = DatabaseFactory.getDatabase();
		if (!database.getDBType().equals(DBType.SQLITE)) {
			System.err.println("The auto-increment DDL is only suitable for a database of type " + DBType.SQLITE +
					" but you are running type " + database.getDBType() + ". This will probably not work.");
		}
		
		createTable("CUSTOMER",
			"id integer not null primary key AUTOINCREMENT",
			"name varchar(20)",
			"first_name varchar(30)"
		);
	}

}
