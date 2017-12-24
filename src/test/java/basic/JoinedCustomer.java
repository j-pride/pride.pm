package basic;
/*******************************************************************************
 * Copyright (c) 2001-20017 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - JUnit test suite
 *******************************************************************************/
import de.mathema.pride.JoinRecordDescriptor;
import de.mathema.pride.RecordDescriptor;

/**
 * Extension of {@link Customer} representing a customer and its
 * wife to test a database join, based on alias aggregation of
 * existing record descriptors. The join condition is of course
 * bullshit to keep things simple in this test: A wife is
 * identified by a customer with the same lastname of another
 * customer and a higher ID. This causes "Last Customer" to be
 * identified as the wife of "First Customer" in the JUnit test.
 * 
 * @author Jan Lessner
 */
public class JoinedCustomer extends Customer {
    
    private Customer wife;
    public Customer getWife() { return wife; }
    public void setWife(Customer wife) { this.wife = wife; }
    
    public static RecordDescriptor innerJoinRed = new JoinRecordDescriptor(Customer.red, "cst").
    		join(Customer.red, "wife", "wife.lastName = cst.lastName and wife.id > cst.id");
    public static RecordDescriptor outerJoinRed = new JoinRecordDescriptor(Customer.red, "cst").
    		leftJoin(Customer.red, "wife", "wife.lastName = cst.lastName and wife.id > cst.id");
    public static RecordDescriptor red = outerJoinRed;
    		
    public RecordDescriptor getDescriptor() { return red; }
}
