package pm.pride.basic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class PrideSelectStreamTest extends AbstractPrideTest {

	private static int COUNT = 100;
	int count;
	int lastId;
	
	@Override
	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
		generateCustomer(COUNT);
	}

	@Test
	public void testSelectByStream() throws Exception{
		Customer c = new Customer();
		count = 0;
		lastId = -1;
		c.queryAll().stream(Customer.class).forEach(customer -> {
			assertNotNull(customer);
			if (lastId == -1) {
				assertEquals(c, customer); // First element in stream is the original object
			}
			else {
				assertNotEquals(c, customer); // Following streamed customers are clones
			}
			assertNotEquals(lastId, customer.getId()); // And the content differs
			assertNotNull(customer.getFirstName());
			lastId = customer.getId();
			count++;
		});
		assertEquals(count,COUNT);
	}
	
	@Test
	public void testSelectByUnclonedStream() throws Exception{
		Customer c = new Customer();
		count = 0;
		lastId = -1;
		Set<Integer> selectedIds = new HashSet<>();
		c.queryAll().streamOE(Customer.class).forEach(customer -> {
			assertNotNull(customer);
			assertEquals(c, customer); // Original entity is not cloned
			assertNotEquals(lastId, customer.getId()); // But the content differs
			assertNotNull(customer.getFirstName());
			// Produce back-pressure to ensure that slow processing doesn't cause skipping of results
			try { Thread.sleep(10); } catch (InterruptedException ir) {};
			lastId = customer.getId();
			selectedIds.add(lastId);
			count++;
		});
		assertEquals(COUNT, count);
		assertEquals(COUNT, selectedIds.size()); // Back-pressure didn't cause results to be skipped
	}
	
	@Test
	public void testSelectByEmptyStream() throws Exception{
		Customer c = new Customer();
		long count = c.query("id = 0").stream(Customer.class).count();
		assertEquals(count, 0);
	}

	@Test
	public void testFindRCWithStream() throws Exception {
		Customer c = new Customer();
		c.setLastName("Customer");
		count = 0;
		c.queryByExampleRC("lastName").stream(Customer.class).forEach(customer -> {
			assertNotNull(customer);
			assertNotEquals(c, customer); // All stream elements are clones
			assertNotNull(customer.getFirstName());
			count++;
		});
		assertEquals(2, count);
	}
	

}
