package util;

import java.sql.SQLException;

import pm.pride.Database;
import pm.pride.DatabaseFactory;

public class CreateCustomerTable {
	public static final String TABLE_NAME = "CUSTOMER";

	public static void main(String[] args) throws Exception {
		ResourceAccessorExampleConfig.initPriDE();
		Database database = DatabaseFactory.getDatabase();
		try {
			database.sqlExecute("drop table " + TABLE_NAME);
		}
		catch(SQLException sqlx) {} // Go ahead, table may not yet exist
		database.sqlExecute(
			"create table " + TABLE_NAME + " (" +
			"  id integer not null primary key AUTOINCREMENT," +
			"  name varchar(20)," +
		    "  first_name varchar(30)" +
			")");
		database.commit();
		System.out.println(TABLE_NAME + " table created successfully");
	}
}
