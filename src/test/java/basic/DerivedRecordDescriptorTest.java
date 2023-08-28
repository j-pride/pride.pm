package basic;
import org.junit.Test;

import junit.framework.TestCase;
import pm.pride.*;
import junit.framework.Assert;

/**
 * @author Matthias Bartels
 *
 * Testclass that tests an inheritance hierarchy of RecordDescriptors.
 */
public class DerivedRecordDescriptorTest extends AbstractPrideTest {

	@Override
    public void setUp() throws Exception {
        super.setUp();
        String quote = DatabaseFactory.getDatabase().getIdentifierQuotation();
        DatabaseFactory.getDatabase().sqlUpdate("DROP TABLE " + TEST_TABLE);
		DatabaseFactory.getDatabase().sqlUpdate(
			"CREATE TABLE " + TEST_TABLE
				+ "(id int not null primary key,"
				+ "firstName varchar(50),"
				+ "lastName varchar(50),"

				+ SQL.quote("ty pe") + " varchar(10),"
				+ "hireDate date,"
				+ "street varchar(50),"
				+ "city varchar(50),"
				+ "active " + (isDBType(ResourceAccessor.DBType.POSTGRES) ? "boolean" : "int") + ")");
		DatabaseFactory.getDatabase().commit();        
		DerivedCustomer c = new DerivedCustomer(1,"Hajo","Klick", "Hinter den 7 Bergen", "Maerchenwald", Boolean.TRUE);
		DatabaseFactory.getDatabase().commit();
    }

	@Test
    public void testInsert() throws Exception {
		DerivedCustomer c = new DerivedCustomer(2,"Hajo","Klick", "Hinter den 7 Bergen", "Maerchenwald", Boolean.TRUE);
		DatabaseFactory.getDatabase().commit();
		DerivedCustomer c2 = new DerivedCustomer(2);
		assertEquals("Hajo", c2.getFirstName());
		assertEquals("Klick", c2.getLastName());
		assertEquals("Maerchenwald", c2.getCity());
    }
    
	@Test
    public void testUpdate() throws Exception {
    
		DerivedCustomer c = new DerivedCustomer(1);
		assertEquals("Hajo", c.getFirstName());
		assertEquals("Maerchenwald", c.getCity());
		c.setFirstName("Casper");
		c.setLastName("Kopp");
		c.setStreet("Bei Schneewittchen");
		c.update(new String[] {"id", "city"});
		DatabaseFactory.getDatabase().commit();
		DerivedCustomer[] ca = (DerivedCustomer[])c.queryByExample("id", "street").toArray();
		assertEquals(ca.length, 1);
		assertEquals("Casper", ca[0].getFirstName());
		assertEquals("Kopp", ca[0].getLastName());        
		assertEquals("Bei Schneewittchen", ca[0].getStreet());
    }
    
	@Test
    public void testDelete() throws Exception {
        DerivedCustomer c = new DerivedCustomer();
		ResultIterator ri =	c.queryAll();
		int counter = 0;
		do {
			c.delete();
			counter++;
		} while (ri.next());
		DatabaseFactory.getDatabase().commit();
		assertEquals(1, counter);
		ri = c.queryAll();
		assertNullResult(ri);
    }
    
}