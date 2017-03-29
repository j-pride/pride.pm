/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - Example code
 *******************************************************************************/
package join;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.mathema.pride.RecordDescriptor;

public class JoinedCustomer extends Customer {

    private static final String JOIN =
        "customer c left outer join orders o on o.customer_id = c.id";
     
    private Order lastOrder;
    public Order getLastOrder() { return lastOrder; }
    public void setLastOrder(Order lastOrder) { this.lastOrder = lastOrder; }

    protected static RecordDescriptor red = new JoinedDescriptor();
    public RecordDescriptor getDescriptor() { return red; }

    /**
     * Specialized record descriptor for selecting customers and their
     * potential last order by a single SQL command using an outer join.
     */
    protected static class JoinedDescriptor extends RecordDescriptor {
        private RecordDescriptor orderDesc = new RecordDescriptor(Order.red, "o");
        public JoinedDescriptor() { super(Customer.red, "c", JOIN); }
        
        protected String getResultFields() {
            return super.getResultFields() + ", " + orderDesc.getFieldNames(null);
        }
        
        public int record2object(Object obj, ResultSet results, int position)
            throws SQLException, IllegalAccessException, InvocationTargetException {
            JoinedCustomer jc = (JoinedCustomer)obj;
            position = super.record2object(obj, results, position);
            results.getObject(position);
            if (!results.wasNull()) {
                jc.lastOrder = new Order();
                orderDesc.record2object(jc.lastOrder, results, position);
            }
            else
                jc.lastOrder = null;
            position += orderDesc.totalAttributes();
            return position;
        }
    }
    
}
