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

    @Override
    public void setUp() throws Exception {
        super.setUp();
        generateCustomer(9);
    }

    @Test
    public void testOuterJoin() throws Exception {
        int nowife = 0;
        boolean firstFound = false;
        JoinedCustomer jc = new JoinedCustomer();
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
        JoinedCustomer jc = new JoinedCustomer();
        jc.setLastName("Customer");
    	List<JoinedCustomer> results = jc.queryByExample("lastName").toList(JoinedCustomer.class);
    	assertEquals(2, results.size()); // First married with Last, Last married with noone
    }

    @Test
    public void testInnerJoin() throws Exception {
        RecordDescriptor orig = JoinedCustomer.red;
        try {
            JoinedCustomer.red = JoinedCustomer.innerJoinRed;
            int found = 0;
            JoinedCustomer jc = new JoinedCustomer();
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
            JoinedCustomer.red = orig;
        }
    }

    @Test
    public void testFragmentJoin() throws Exception {
    	JoinedCustomerFragments jcf = new JoinedCustomerFragments();
    	jcf.setLastName("Customer");
    	List<JoinedCustomerFragments> results = jcf.queryByExample(Customer.COL_LASTNAME).toList(JoinedCustomerFragments.class);
    	System.out.println(results);
    	assertEquals(1, results.size());
    	jcf = results.get(0);
    	assertEquals("Customer", jcf.getLastName());
    	assertEquals("First", jcf.getFirstName());
    	assertEquals("Last", jcf.getWifeFirstName());
    	assertNotEquals(jcf.getId(), jcf.getWifeId());
    }
    
}
