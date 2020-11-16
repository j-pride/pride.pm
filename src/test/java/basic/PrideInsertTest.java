package basic;
import org.junit.Test;

import pm.pride.DatabaseFactory;
import pm.pride.ResourceAccessor;
import pm.pride.ResourceAccessorJSE;
import pm.pride.ResultIterator;

/**
 * @author bart57
 *
 * Class to Test the Insert-Behaviour of the PriDE-Framework
 */
public class PrideInsertTest extends AbstractPrideTest {

    public String getAutoIncClassifier() {
        String dbType = DatabaseFactory.getResourceAccessor().getDBType();
        if (ResourceAccessor.DBType.MYSQL.equals(dbType))
            return DEFAULT_ID_CLASSIFIER + "AUTO_INCREMENT";
        if (ResourceAccessor.DBType.MARIADB.equals(dbType))
            return DEFAULT_ID_CLASSIFIER + "AUTO_INCREMENT";
        if (ResourceAccessor.DBType.SQLSERVER.equals(dbType))
            return DEFAULT_ID_CLASSIFIER + "IDENTITY(1,1)";
        if (ResourceAccessor.DBType.HSQL.equals(dbType))
            return "int identity";
        if (ResourceAccessor.DBType.DB2.equals(dbType))
            return "int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)";
        if (ResourceAccessor.DBType.SQLITE.equals(dbType))
            return "INTEGER PRIMARY KEY AUTOINCREMENT";
        return null;
    }

	/**
	 * Insert a Customer and test the result
	 */
    @Test
	public void testInsert() throws Exception{
		new Customer(1,"Hajo\\'","Klick",Boolean.TRUE);
		DatabaseFactory.getDatabase().commit();
		Customer c2 = new Customer(1);
		assertEquals("Hajo\\'", c2.getFirstName());
		assertEquals("Klick", c2.getLastName());
	}

    /**
     * Insert customers with auto-generated primary keys
     * and fetch the key values from the database insertion operation
     */
  @Test
	public void testAutoInsert() throws Exception {
    String autoIncClassifier = getAutoIncClassifier();
    if (autoIncClassifier == null) {
        System.err.println("Don't know how to auto-increment for database of type " +
            DatabaseFactory.getResourceAccessor().getDBType() + ", skipping testAutoInsert");
        return;
    }
    createTestTable(getAutoIncClassifier());
		AutoCustomer c = new AutoCustomer("Firstname","Customer");
		DatabaseFactory.getDatabase().commit();
    assertTrue(c.getId() != -1);
		Customer c2 = new Customer(c.getId());
    AutoCustomer c3 = new AutoCustomer("Firstname","Customer");
    DatabaseFactory.getDatabase().commit();
    assertTrue(c3.getId() != -1);
    assertEquals(c.getId() + 1, c3.getId());
	}

}
