package util;

import java.sql.SQLException;
import java.util.Arrays;

import pm.pride.Database;
import pm.pride.DatabaseFactory;

public class AbstractCreateTable {

	public static void createTable(String tableName, String... columnSpecs) throws Exception {
		ResourceAccessorExampleConfig.initPriDE();
		Database database = DatabaseFactory.getDatabase();
		try {
			database.sqlExecute("drop table " + tableName);
		}
		catch(SQLException sqlx) {} // Go ahead, table may not yet exist
		
		String ddl = assembleCreateTableDDL(tableName, columnSpecs);
		
		database.sqlExecute(ddl);
		database.commit();
		System.out.println(tableName + " table created successfully");
	}
	
	protected static String assembleCreateTableDDL(String tableName, String[] columnSpecs) {
		String ddl = "";
		for (String columnSpec: columnSpecs) {
			ddl += "," + columnSpec;
		}
		ddl = ddl.substring(1);
		return "create table " + tableName + "(" + ddl + ")";
	}
}
