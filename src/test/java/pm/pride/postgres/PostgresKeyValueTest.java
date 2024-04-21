package pm.pride.postgres;

import pm.pride.basic.AbstractPrideTest;
import pm.pride.basic.NeedsDBType;
import org.junit.jupiter.api.Test;
import pm.pride.PreparedInsert;
import pm.pride.ResourceAccessor;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@NeedsDBType(ResourceAccessor.DBType.POSTGRES)
public class PostgresKeyValueTest extends AbstractPrideTest {
    protected static final String KEY_VALUE_TEST_TABLE = "customer_pride_key_value_test";

    @Override
    protected void createTestTable() throws SQLException {
		// ATTENTION:
		// Since version 9 the column type hstore is not supported by default.
		// You probably have to run the following command from a PostgreSQL shell before:
		// CREATE EXTENSION hstore;
        String columns =
                "id " + DEFAULT_ID_CLASSIFIER + "," +
                "contacts hstore";
        dropAndCreateTable(KEY_VALUE_TEST_TABLE, columns);
    }

    @Override
    protected void dropTestTable() throws SQLException {
        dropTestTable(KEY_VALUE_TEST_TABLE);
    }

    @Test
    public void testEmpty() throws Exception {
        CustomerKeyValue customer = new CustomerKeyValue(0);
        customer.create();
        customer = new CustomerKeyValue(0);
        customer.find();
        assertNull(customer.getContacts());
    }

    @Test
    public void testMultipleValues() throws Exception {
        CustomerKeyValue customer = newCustomer(1);
        customer.create();

        customer = new CustomerKeyValue(1);
        customer.find();
        assertEquals(2, customer.getContacts().size());
        assertEquals("jlessner@gmx.de", customer.getContacts().get("email"));
        assertEquals("12345", customer.getContacts().get("phone"));
    }

    @Test
    public void testNullItemValue() throws Exception {
        CustomerKeyValue customer = newCustomer(2);
        customer.getContacts().put("null", null);
        customer.create();

        customer = new CustomerKeyValue(2);
        customer.find();
        assertEquals(3, customer.getContacts().size());
        assertNull(customer.getContacts().get("null"));
    }

    @Test
    public void testPreparedInsert() throws Exception {
    	CustomerKeyValue customer = newCustomer(3);
        PreparedInsert pi = new PreparedInsert(customer.getDescriptor());
        pi.execute(customer);
        
        customer = new CustomerKeyValue(3);
        customer.find();
        assertEquals(2, customer.getContacts().size());
        assertEquals("jlessner@gmx.de", customer.getContacts().get("email"));
        assertEquals("12345", customer.getContacts().get("phone"));
    }
    
    private CustomerKeyValue newCustomer(int id) {
        CustomerKeyValue customer = new CustomerKeyValue(id);
        Map<String, String> contacts = new HashMap<String, String>();
        contacts.put("email", "jlessner@gmx.de");
        contacts.put("phone", "12345");
        customer.setContacts(contacts);
        return customer;
    }
    
}
