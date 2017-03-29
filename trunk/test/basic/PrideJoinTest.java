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
import junit.framework.Assert;
import de.mathema.pride.ResultIterator;

/**
 * Test of table join functionality
 * 
 * @author Administrator
 */
public class PrideJoinTest extends PrideBaseTest {

    public PrideJoinTest(String name) {
        super(name);
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        generateCustomer(9);
    }

    public void testAggregateJoin() throws Exception {
        int nowife = 0;
        boolean firstFound = false;
        JoinedCustomer jc = new JoinedCustomer();
        ResultIterator iter = jc.queryAll();

        do {
            if (jc.getFirstName().equals("First")) {
                firstFound = true;
                Assert.assertNotNull(jc.getWife());
                Assert.assertEquals(jc.getWife().getFirstName(), "Last");
                Assert.assertEquals(jc.getWife().getLastName(), "Customer");
            }
            else {
                nowife++;
                Assert.assertTrue(!jc.getFirstName().equals("First"));
                Assert.assertNull(jc.getWife());
            }
        } while(iter.next());
        
        Assert.assertEquals(nowife, 8);
        Assert.assertTrue(firstFound);
    }

    public static void main(String[] args) throws Exception {
        PrideJoinTest pjt = new PrideJoinTest("join");
        pjt.setUp();
        pjt.testAggregateJoin();
    }
}
