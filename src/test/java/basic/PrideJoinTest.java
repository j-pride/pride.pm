package basic;
/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 *
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - JUnit test suite
 *******************************************************************************/

import java.util.List;

import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;

import oracle.jdbc.logging.annotations.DisableTrace;
import pm.pride.*;
/**
 * Test of table join functionality
 */
@SkipForDBType(ResourceAccessor.DBType.HSQL)
public class PrideJoinTest extends AbstractPrideTest {

	static JoinRecordDescriptor adhocJoin = new JoinRecordDescriptor(Customer.red, "husband")
    		.join("customer_pride_test", "wife", "wife.lastName = husband.lastName and wife.firstName != husband.firstName");

    @Override
    public void setUp() throws Exception {
        super.setUp();
        generateCustomer(9);
    }

    @Test
    public void testOuterJoin() throws Exception {
        int nowife = 0;
        boolean firstFound = false;
        CustomerJoinedWithCustomer jc = new CustomerJoinedWithCustomer();
        ResultIterator iter = jc.queryAll();

        do {
            if (jc.getFirstName().equals("First")) {
                firstFound = true;
                assertNotNull(jc.getWife());
                assertEquals("Last", jc.getWife().getFirstName());
                assertEquals("Customer", jc.getWife().getLastName());
            } else {
                nowife++;
                assertTrue(!jc.getFirstName().equals("First"));
                assertNull(jc.getWife());
            }
        } while (iter.next());

        assertEquals(8, nowife);
        assertTrue(firstFound);
    }

    @Test
    public void testOuterJoinByExampleWithAliasPrefixAutomaticallyAdded() throws Exception {
        CustomerJoinedWithCustomer jc = new CustomerJoinedWithCustomer();
        jc.setLastName("Customer");
    	List<CustomerJoinedWithCustomer> results = jc.queryByExample("lastName").toList(CustomerJoinedWithCustomer.class);
    	assertEquals(2, results.size()); // First married with Last, Last married with noone
    }

    @Test
    public void testInnerJoin() throws Exception {
        RecordDescriptor orig = CustomerJoinedWithCustomer.red;
        try {
            CustomerJoinedWithCustomer.red = CustomerJoinedWithCustomer.innerJoinRed;
            int found = 0;
            CustomerJoinedWithCustomer jc = new CustomerJoinedWithCustomer();
            ResultIterator iter = jc.queryAll();

            do {
                found++;
                assertEquals("First", jc.getFirstName());
                assertNotNull(jc.getWife());
                assertEquals("Last", jc.getWife().getFirstName());
                assertEquals("Customer", jc.getWife().getLastName());
            } while (iter.next());

            assertEquals(1, found);
        } finally {
            CustomerJoinedWithCustomer.red = orig;
        }
    }

    @Test
    public void testJoinWithFragments() throws Exception {
    	CustomerJoinedWithCustomerFragments jcf = new CustomerJoinedWithCustomerFragments();
    	jcf.setLastName("Customer");
    	List<CustomerJoinedWithCustomerFragments> results = jcf
    			.queryByExample(Customer.COL_LASTNAME)
    			.toList(CustomerJoinedWithCustomerFragments.class);
    	System.out.println(results);
    	assertEquals(1, results.size());
    	jcf = results.get(0);
    	assertEquals("Customer", jcf.getLastName());
    	assertEquals("First", jcf.getFirstName());
    	assertEquals("Last", jcf.getWifeFirstName());
    	assertNotEquals(jcf.getId(), jcf.getWifeId());
    }

    @Test
    /** Column name "id" in the where condition is ambiguous but is automatically expanded to husband.id */
    public void testJoinWithFragmentsAndWhereConditionAutoTableAlias() throws Exception {
    	CustomerJoinedWithCustomerFragments jcf = new CustomerJoinedWithCustomerFragments();
    	List<?> results = jcf.query(
    		new WhereCondition()
    			.and("id", 1)
    			.and("wife.id", 9)).toList();
    	System.out.println(results);
    	assertEquals(1, results.size());
    }

    @Test
    public void testFragmentJoin() throws Exception {
    	CustomerFragments fragments = new CustomerFragments();
    	List<CustomerFragments> results = fragments.queryAll().toList(CustomerFragments.class);
    	System.out.println(results);
    	assertEquals(2, results.size());
    	CustomerFragments r1 = results.get(0);
    	CustomerFragments r2 = results.get(1);
    	assertEquals(r1.getHusbandFirstName(), r2.getWifeFirstName());
    	assertEquals(r2.getHusbandFirstName(), r1.getWifeFirstName());
    	assertNotEquals(r1.getHusbandFirstName(), r1.getWifeFirstName());
    }

    /** Run a query which returns customers but needs a join with another
     * table just to express certain query conditions. In this case: query
     * married customers.
     */
    @Test
    public void testAdHocJoin() throws Exception {
    	Customer customer = new Customer();
    	List<Customer> results = customer.xqueryAll(adhocJoin).toList(Customer.class);
    	assertEquals(2, results.size());
    }
    
    @Test
    public void testAdHocJoinWithWhereCondition() throws Exception {
    	Customer customer = new Customer();
    	WhereCondition id1 = new WhereCondition().and("id", 1);
    	List<Customer> results = customer.xquery(adhocJoin, id1).toList(Customer.class);
    	assertEquals(1, results.size());
    	assertEquals(1, results.get(0).getId());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testAdHocJoinMustBeCompatible() throws Exception {
    	new Customer().xqueryAll(IdentifiedEntity.red);
    }
    
}
