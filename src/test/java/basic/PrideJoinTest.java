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

import de.mathema.pride.RecordDescriptor;
import de.mathema.pride.ResultIterator;
import org.junit.Test;

/**
 * Test of table join functionality
 */
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

}
