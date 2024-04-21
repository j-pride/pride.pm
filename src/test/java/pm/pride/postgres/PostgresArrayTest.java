package pm.pride.postgres;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pm.pride.basic.NeedsDBType;
import org.junit.jupiter.api.Test;
import pm.pride.PreparedInsert;
import pm.pride.ResourceAccessor;


import pm.pride.basic.CustomerType;
import pm.pride.basic.AbstractPrideTest;

import static org.junit.jupiter.api.Assertions.*;

@NeedsDBType(ResourceAccessor.DBType.POSTGRES)
public class PostgresArrayTest extends AbstractPrideTest {
    protected static final String ARRAY_TEST_TABLE = "customer_pride_array_test";
    protected static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    protected void createTestTable() throws SQLException {
        String columns =
                "id " + DEFAULT_ID_CLASSIFIER + "," +
                "permissions text[]," +
                "logintimes integer[]," +
                "logindates date[]," +
                "turnovers decimal(8,2)[]," +
                "types text[]";
        dropAndCreateTable(ARRAY_TEST_TABLE, columns);
    }

    @Override
    protected void dropTestTable() throws SQLException {
        dropTestTable(ARRAY_TEST_TABLE);
    }

    @Test
	public void testEmpty() throws Exception {
        CustomerArray customer = new CustomerArray(0);
        customer.create();
        customer = new CustomerArray(0);
        customer.find();
        assertNull(customer.getPermissions());
    }

    @Test
    public void testMultipleStringValues() throws Exception {
        CustomerArray customer = newCustomer(1);
        customer.create();

        customer = new CustomerArray(1);
        customer.find();
        assertNotNull(customer.getPermissions());
        assertEquals(2, customer.getPermissions().length);
        assertEquals("Admin", customer.getPermissions()[0]);
        assertEquals("Supervisor", customer.getPermissions()[1]);
    }

    @Test
    public void testNullItemInStringArray() throws Exception {
        CustomerArray customer = newCustomer(2);
        customer.getPermissions()[1] = null;
        customer.create();

        customer = new CustomerArray(2);
        customer.find();
        assertEquals(2, customer.getPermissions().length);
        assertNull(customer.getPermissions()[1]);
    }

    @Test
    public void testPreparedStringInsert() throws Exception {
        CustomerArray customer = newCustomer(3);
        PreparedInsert pi = new PreparedInsert(customer.getDescriptor());
        pi.execute(customer);
        
        customer = new CustomerArray(3);
        customer.find();
        assertEquals(2, customer.getPermissions().length);
        assertEquals("Admin", customer.getPermissions()[0]);
        assertEquals("Supervisor", customer.getPermissions()[1]);
    }
    
    @Test
    public void testMultiplePrimitiveIntValues() throws Exception {
        CustomerArray customer = newCustomer(4);
        customer.setLogintimes(new int[] { 1, 2, 3 });
        customer.create();

        customer = new CustomerArray(4);
        customer.find();
        assertNotNull(customer.getLogintimes());
        assertEquals(3, customer.getLogintimes().length);
        assertEquals(1, customer.getLogintimes()[0]);
        assertEquals(2, customer.getLogintimes()[1]);
        assertEquals(3, customer.getLogintimes()[2]);
    }

    @Test
    public void testMultipleDateValues() throws Exception {
        CustomerArray customer = newCustomer(5);
        customer.setLogindates(new Date[] {
                dateFormat.parse("01.02.2003"),
                dateFormat.parse("04.05.2006")
                });
        customer.create();

        customer = new CustomerArray(5);
        customer.find();
        assertNotNull(customer.getLogindates());
        assertEquals(2, customer.getLogindates().length);
        assertEquals(dateFormat.parse("01.02.2003"), customer.getLogindates()[0]);
        assertEquals(dateFormat.parse("04.05.2006"), customer.getLogindates()[1]);
    }

    @Test
    public void testMultipleBigDecimalValues() throws Exception {
        CustomerArray customer = newCustomer(6);
        customer.setTurnovers(new BigDecimal[] {
                new BigDecimal("1.23"),
                new BigDecimal("4567.89")
                });
        customer.create();

        customer = new CustomerArray(6);
        customer.find();
        assertNotNull(customer.getTurnovers());
        assertEquals(2, customer.getTurnovers().length);
        assertEquals(new BigDecimal("1.23"), customer.getTurnovers()[0]);
        assertEquals(new BigDecimal("4567.89"), customer.getTurnovers()[1]);
    }

    @Test
    public void testMultipleEnumValues() throws Exception {
        CustomerArray customer = newCustomer(7);
        customer.setTypes(new CustomerType[] {
                CustomerType.gold,
                CustomerType.standard
                });
        customer.create();

        customer = new CustomerArray(7);
        customer.find();
        assertNotNull(customer.getTypes());
        assertEquals(2, customer.getTypes().length);
        assertEquals(CustomerType.gold, customer.getTypes()[0]);
        assertEquals(CustomerType.standard, customer.getTypes()[1]);
    }

    private CustomerArray newCustomer(int id) {
        CustomerArray customer = new CustomerArray(id);
        String[] permissions = new String[2];
        permissions[0] = "Admin";
        permissions[1] = "Supervisor";
        customer.setPermissions(permissions);
        return customer;
    }

    
}
