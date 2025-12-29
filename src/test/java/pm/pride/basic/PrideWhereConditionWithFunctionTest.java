package pm.pride.basic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pm.pride.*;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static pm.pride.WhereFunction.*;
import static pm.pride.WhereCondition.Operator.IN;
import static pm.pride.basic.Customer.*;

class PrideWhereConditionWithFunctionTest extends AbstractPrideTest {

  public static final String TEST_PART_OF_CUSTOMER_LAST_NAME = "OmER_Na";
  public static final String TEST_CUSTOMER_LAST_NAME = "CuSt" + TEST_PART_OF_CUSTOMER_LAST_NAME + "Me";
  public static final String TEST_CUSTOMER_FIRST_NAME = "FiRsT_NaMe";
  public static final int TEST_CUSTOMER_ID = 1;

  private Customer customer;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    Database db = DatabaseFactory.getDatabase();
    customer = new Customer(TEST_CUSTOMER_ID);
    customer.setLastName(TEST_CUSTOMER_LAST_NAME);
    customer.setFirstName(TEST_CUSTOMER_FIRST_NAME);
    customer.update();
    db.commit();
  }

  private void assertSuccessfulQuery(WhereCondition wc) throws SQLException {
    try (ResultIterator resultIterator = customer.query(wc)) {
      Customer customerInDb = resultIterator.getObject(Customer.class);
      assertNotNull(customerInDb);
      assertEquals(TEST_CUSTOMER_LAST_NAME, customerInDb.getLastName());
    }
  }

  @Test
  void testFunction_upper() throws SQLException {
    WhereCondition wc = new WhereCondition(upper(COL_LASTNAME), upper(TEST_CUSTOMER_LAST_NAME.toLowerCase()));
    assertSuccessfulQuery(wc);
  }

  @Test
  void testFunction_upper_withWildCard() throws SQLException {
    WhereCondition wc = new WhereCondition(upper(COL_LASTNAME), upper( "%" + TEST_PART_OF_CUSTOMER_LAST_NAME.toLowerCase() + "%"));
    assertSuccessfulQuery(wc);
  }


  @Test
  void testFunction_upper_withOtherQueryParams() throws SQLException {
    Date myDate = new Date((new GregorianCalendar(1999, 12, 2)).getTimeInMillis());
    WhereCondition wc = new WhereCondition()
        .and(upper(COL_LASTNAME), upper(TEST_CUSTOMER_LAST_NAME.toLowerCase()))
        .and(upper(COL_FIRSTNAME), upper(TEST_CUSTOMER_FIRST_NAME.toLowerCase()))
        .and(COL_HIREDATE, myDate)
        .and(COL_ID, IN, Collections.singletonList(TEST_CUSTOMER_ID).toArray());
    assertSuccessfulQuery(wc);
  }

  @Test
  void testFunction_lower() throws SQLException {
    WhereCondition wc = new WhereCondition(lower(COL_LASTNAME), lower(TEST_CUSTOMER_LAST_NAME));
    assertSuccessfulQuery(wc);
  }

  @Test
  void testFunction_trim() throws SQLException {
    WhereCondition wc = new WhereCondition(COL_LASTNAME, trim( "   " + TEST_CUSTOMER_LAST_NAME + "   "));
    assertSuccessfulQuery(wc);
  }

  @Test
  void testFunction_ltrim() throws SQLException {
    WhereCondition wc = new WhereCondition(COL_LASTNAME, ltrim( "   " + TEST_CUSTOMER_LAST_NAME));
    assertSuccessfulQuery(wc);
  }

  @Test
  void testFunction_rtrim() throws SQLException {
    WhereCondition wc = new WhereCondition(COL_LASTNAME, rtrim( TEST_CUSTOMER_LAST_NAME + "   "));
    assertSuccessfulQuery(wc);
  }

  @Test
  void testFunction_length() throws SQLException {
    WhereCondition wc = new WhereCondition(length(COL_LASTNAME), TEST_CUSTOMER_LAST_NAME.length());
    assertSuccessfulQuery(wc);
  }

  @Test
  void testFunction_concat() throws SQLException {
    WhereCondition wc = new WhereCondition(concat(COL_LASTNAME, COL_FIRSTNAME), TEST_CUSTOMER_LAST_NAME + TEST_CUSTOMER_FIRST_NAME);
    assertSuccessfulQuery(wc);
  }

  @Test
  void testFunction_concat_fieldsAndValues() throws SQLException {
    WhereCondition wc = new WhereCondition(
      concat(COL_LASTNAME, COL_FIRSTNAME),
      concat(TEST_CUSTOMER_LAST_NAME, TEST_CUSTOMER_FIRST_NAME));
    assertSuccessfulQuery(wc);
  }

  @Test
  void testFunction_concat_fieldsAndValues_withBindvars() throws SQLException {
    WhereCondition wc = new WhereCondition().bindvars(true).and(
        concat(COL_LASTNAME, COL_FIRSTNAME),
        concat(TEST_CUSTOMER_LAST_NAME, TEST_CUSTOMER_FIRST_NAME));
    assertSuccessfulQuery(wc);
  }

  @Test
  void testFunction_concat_withBindvars() throws SQLException {
    WhereCondition wc = new WhereCondition().bindvars(true).and(
      concat(COL_LASTNAME, COL_FIRSTNAME),
      TEST_CUSTOMER_LAST_NAME + TEST_CUSTOMER_FIRST_NAME);
    assertSuccessfulQuery(wc);
  }

  @Test
  void testFunction_substring() throws SQLException {
    WhereCondition wc = new WhereCondition(
      substr(COL_LASTNAME, 2),
      TEST_CUSTOMER_LAST_NAME.substring(2));
    assertSuccessfulQuery(wc);
  }

  @Test
  void testFunction_substringWithLength() throws SQLException {
    WhereCondition wc = new WhereCondition(
      substr(COL_LASTNAME, 2, 5),
      TEST_CUSTOMER_LAST_NAME.substring(2, 5));
    assertSuccessfulQuery(wc);
  }

}
