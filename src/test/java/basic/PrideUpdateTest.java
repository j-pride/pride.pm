package basic;
import org.junit.Test;

import pm.pride.DatabaseFactory;
import pm.pride.WhereCondition;

import java.util.Date;

/**
 * @author bart57
 *
 * Class to Test the Update-Behaviour of the PriDE-Framework
 */
public class PrideUpdateTest extends AbstractPrideTest {

	@Override
	public void setUp() throws Exception {
		super.setUp();
		generateCustomer(9);
		setBindvarsDefault(false);
	}

	@Test
	public void testUpdatePK() throws Exception{
		Customer c = new Customer(1);
		assertEquals("First", c.getFirstName());
		assertEquals("Customer", c.getLastName());
		c.setFirstName("Casper");
		c.setLastName("Kopp");
		c.update();
		DatabaseFactory.getDatabase().commit();
		Customer c2 = new Customer(1);
		assertEquals("Casper", c2.getFirstName());
		assertEquals("Kopp", c2.getLastName());
	}

	@Test
	public void testUpdateByExample() throws Exception{
		Customer c = new Customer(1);
		assertEquals("First", c.getFirstName());
		assertEquals("Customer", c.getLastName());
		c.setFirstName("Inge");
		c.setLastName("Updated");
		c.update(new String[] { "id" });
		DatabaseFactory.getDatabase().commit();
		Customer[] ca = (Customer[])c.queryByExample("lastName").toArray();
		assertEquals(ca.length, 1);
		assertEquals("Inge", ca[0].getFirstName());
		assertEquals("Updated", ca[0].getLastName());
	}

	@Test
	public void testUpdateFields() throws Exception{
		Customer c = new Customer(1);
		assertEquals("First", c.getFirstName());
		assertEquals("Customer", c.getLastName());
		c.setFirstName("Casper");
		c.update((String[])null, new String[] { "firstName" });
		DatabaseFactory.getDatabase().commit();
		Customer c2 = new Customer(1);
		assertEquals("Casper", c2.getFirstName());
		assertEquals("Customer", c2.getLastName());
	}

	@Test
	public void testUpdateMultiple() throws Exception{
		Customer c = new Customer();
		c.setFirstName("Inge");
		c.setLastName("Updated");
		c.update(new String[] { "firstName"}, new String[] { "lastName" });
		DatabaseFactory.getDatabase().commit();
		Customer[] ca = (Customer[])c.queryByExample("firstName").toArray();
		assertEquals(ca.length, 2);
		assertTrue(ca[0].getId() != ca[1].getId());
		assertTrue(ca[0].getId() == 7 || ca[0].getId() == 8);
		assertTrue(ca[1].getId() == 7 || ca[1].getId() == 8);
	}

	@Test
	public void testUpdateWhere() throws Exception{
		Customer c = new Customer();
		c.setLastName("Updated");
		c.update(new WhereCondition().and("firstName", WhereCondition.Operator.LIKE, "Pe%"), new String[] { "lastName" });
		DatabaseFactory.getDatabase().commit();
		Customer[] ca = (Customer[])c.queryByExample("lastName").toArray();
		assertEquals(ca.length, 2);
		assertTrue(ca[0].getId() != ca[1].getId());
		assertTrue(ca[0].getId() == 2 || ca[0].getId() == 5);
		assertTrue(ca[1].getId() == 2 || ca[1].getId() == 5);
	}

	@Test
	public void testUpdatePlain() throws Exception {
		String update = "update " + TEST_TABLE + " set firstName='Updated' where lastName='Customer'";
		int numUpdates = DatabaseFactory.getDatabase().sqlUpdate(update);
		assertEquals(2, numUpdates);
	}
	
	@Test
	public void testUpdatePlainPrepared() throws Exception {
		String update = "update " + TEST_TABLE + " set firstName=? where lastName=?";
		int numUpdates = DatabaseFactory.getDatabase().sqlUpdate(update, "Updated", "Customer");
		assertEquals(2, numUpdates);
	}


}
