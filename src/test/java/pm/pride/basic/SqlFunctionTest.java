package pm.pride.basic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.pride.Database;
import pm.pride.DatabaseFactory;
import pm.pride.ResultIterator;
import pm.pride.WhereCondition;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static pm.pride.SqlFunction.*;
import static pm.pride.WhereCondition.Operator.IN;

class SqlFunctionTest extends AbstractPrideTest {

  public static final String TEST_PART_OF_CUSTOMER_LAST_NAME = "OmER_Na";
  public static final String TEST_CUSTOMER_LAST_NAME = "CuSt" + TEST_PART_OF_CUSTOMER_LAST_NAME + "Me";
  public static final String TEST_CUSTOMER_FIRST_NAME = "FiRsT_NaMe";
  public static final int TEST_CUSTOMER_ID = 1;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
  }

  @Test
  void testFunction_upper() throws SQLException {
    Database db = DatabaseFactory.getDatabase();
    Customer customer = new Customer(TEST_CUSTOMER_ID);
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
  void testFunction_upper_withWildCard() throws SQLException {
    Database db = DatabaseFactory.getDatabase();
    Customer customer = new Customer(TEST_CUSTOMER_ID);
    customer.setLastName(TEST_CUSTOMER_LAST_NAME);
    customer.update();
    db.commit();
    WhereCondition wc = new WhereCondition(upper(Customer.COL_LASTNAME), upper( "%" + TEST_PART_OF_CUSTOMER_LAST_NAME.toLowerCase() + "%"));
    try (ResultIterator resultIterator = customer.query(wc)) {
      Customer customerInDb = resultIterator.getObject(Customer.class);
      assertNotNull(customerInDb);
      assertEquals(TEST_CUSTOMER_LAST_NAME, customerInDb.getLastName());
    }
  }


  @Test
  void testFunction_upper_withOtherQueryParams() throws SQLException {
    Database db = DatabaseFactory.getDatabase();
    Customer customer = new Customer(TEST_CUSTOMER_ID);
    customer.setLastName(TEST_CUSTOMER_LAST_NAME);
    customer.setFirstName(TEST_CUSTOMER_FIRST_NAME);
    Date myDate = new Date((new GregorianCalendar(1999, 12, 2)).getTimeInMillis());
    customer.setHireDate(myDate);
    customer.update();
    db.commit();
    WhereCondition wc = new WhereCondition(upper(Customer.COL_LASTNAME), upper(TEST_CUSTOMER_LAST_NAME.toLowerCase()))
            .and(upper(Customer.COL_FIRSTNAME), upper(TEST_CUSTOMER_FIRST_NAME.toLowerCase()))
            .and(Customer.COL_HIREDATE, myDate)
            .and(Customer.COL_ID, IN, Collections.singletonList(TEST_CUSTOMER_ID).toArray());
    try (ResultIterator resultIterator = customer.query(wc)) {
      Customer customerInDb = resultIterator.getObject(Customer.class);
      assertNotNull(customerInDb);
      assertEquals(TEST_CUSTOMER_LAST_NAME, customerInDb.getLastName());
    }
  }

  @Test
  void testFunction_lower() throws SQLException {
    Database db = DatabaseFactory.getDatabase();
    Customer customer = new Customer(TEST_CUSTOMER_ID);
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
    Customer customer = new Customer(TEST_CUSTOMER_ID);
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
    Customer customer = new Customer(TEST_CUSTOMER_ID);
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
    Customer customer = new Customer(TEST_CUSTOMER_ID);
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
