package pm.pride.basic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.pride.Database;
import pm.pride.DatabaseFactory;
import pm.pride.ResultIterator;
import pm.pride.WhereCondition;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static pm.pride.SqlFunction.*;

class SqlFunctionTest extends AbstractPrideTest {

  public static final String TEST_CUSTOMER_LAST_NAME = "CuStOmER_NaMe";

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  void testFunction_upper() throws SQLException {
    Database db = DatabaseFactory.getDatabase();
    Customer customer = new Customer(1);
    customer.setLastName(TEST_CUSTOMER_LAST_NAME);
    customer.update();
    db.commit();
    WhereCondition wc = new WhereCondition(upper(Customer.COL_LASTNAME), upper(TEST_CUSTOMER_LAST_NAME.toLowerCase()));
    try (ResultIterator resultIterator = customer.query(wc)) {
      Customer customerInDb = resultIterator.getObject(Customer.class);
      assertNotNull(customerInDb);
      assertEquals(TEST_CUSTOMER_LAST_NAME, customerInDb.getLastName());
    }
  }

  @Test
  void testFunction_lower() throws SQLException {
    Database db = DatabaseFactory.getDatabase();
    Customer customer = new Customer(1);
    customer.setLastName(TEST_CUSTOMER_LAST_NAME);
    customer.update();
    db.commit();
    WhereCondition wc = new WhereCondition(lower(Customer.COL_LASTNAME), lower(TEST_CUSTOMER_LAST_NAME.toUpperCase()));
    try (ResultIterator resultIterator = customer.query(wc)) {
      Customer customerInDb = resultIterator.getObject(Customer.class);
      assertNotNull(customerInDb);
      assertEquals(TEST_CUSTOMER_LAST_NAME, customerInDb.getLastName());
    }
  }

  @Test
  void testFunction_trim() throws SQLException {
    Database db = DatabaseFactory.getDatabase();
    Customer customer = new Customer(1);
    customer.setLastName(TEST_CUSTOMER_LAST_NAME);
    customer.update();
    db.commit();
    WhereCondition wc = new WhereCondition(Customer.COL_LASTNAME, trim( "   " + TEST_CUSTOMER_LAST_NAME + "   "));
    try (ResultIterator resultIterator = customer.query(wc)) {
      Customer customerInDb = resultIterator.getObject(Customer.class);
      assertNotNull(customerInDb);
      assertEquals(TEST_CUSTOMER_LAST_NAME, customerInDb.getLastName());
    }
  }

  @Test
  void testFunction_ltrim() throws SQLException {
    Database db = DatabaseFactory.getDatabase();
    Customer customer = new Customer(1);
    customer.setLastName(TEST_CUSTOMER_LAST_NAME);
    customer.update();
    db.commit();
    WhereCondition wc = new WhereCondition(Customer.COL_LASTNAME, trim( "   " + TEST_CUSTOMER_LAST_NAME));
    try (ResultIterator resultIterator = customer.query(wc)) {
      Customer customerInDb = resultIterator.getObject(Customer.class);
      assertNotNull(customerInDb);
      assertEquals(TEST_CUSTOMER_LAST_NAME, customerInDb.getLastName());
    }
  }

  @Test
  void testFunction_rtrim() throws SQLException {
    Database db = DatabaseFactory.getDatabase();
    Customer customer = new Customer(1);
    customer.setLastName(TEST_CUSTOMER_LAST_NAME);
    customer.update();
    db.commit();
    WhereCondition wc = new WhereCondition(Customer.COL_LASTNAME, trim( TEST_CUSTOMER_LAST_NAME + "   "));
    try (ResultIterator resultIterator = customer.query(wc)) {
      Customer customerInDb = resultIterator.getObject(Customer.class);
      assertNotNull(customerInDb);
      assertEquals(TEST_CUSTOMER_LAST_NAME, customerInDb.getLastName());
    }
  }
}
