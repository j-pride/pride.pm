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
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

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
 * @author Administrator
 */
public class JoinedCustomer extends Customer {
    
    private static final String JOIN =
    	PrideBaseTest.TEST_TABLE + " cst " +
        "  left outer join " + PrideBaseTest.TEST_TABLE + " wife on " +
        "    wife.lastName = cst.lastName and wife.id > cst.id";
     
    private Customer wife;
    public Customer getWife() { return wife; }
    public void setWife(Customer wife) { this.wife = wife; }

    protected static RecordDescriptor red = new JoinedDescriptor();
    public RecordDescriptor getDescriptor() { return red; }

    /**
     * Specialized record descriptor for selecting customers and their
     * wifes by a single SQL command using an outer join. 
     */
    protected static class JoinedDescriptor extends RecordDescriptor {
        private RecordDescriptor wifeDesc = new RecordDescriptor(Customer.red, "wife");
        public JoinedDescriptor() { super(Customer.red, "cst", JOIN); }
        
        protected String getResultFields() {
            return super.getResultFields() + ", " + wifeDesc.getFieldNames(null);
        }
        
        public int record2object(Object obj, ResultSet results, int position)
            throws SQLException, IllegalAccessException, InvocationTargetException {
            JoinedCustomer jc = (JoinedCustomer)obj;
            position = super.record2object(obj, results, position);
            results.getObject(position /* "wife.id" */); // HSQL doesn't like names here
            if (!results.wasNull()) {
                jc.wife = new Customer();
                position = wifeDesc.record2object(jc.wife, results, position);
            }
            else {
                jc.wife = null;
                position += wifeDesc.totalAttributes();
            }
            return position;
        }
    }
    
}
