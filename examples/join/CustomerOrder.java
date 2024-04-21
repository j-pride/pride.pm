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

import java.sql.SQLException;
import de.mathema.pride.*;

public class CustomerOrder extends MappedObject
{
    private static final String JOIN = " AND customer.id=orders.customer_id";
    
    protected static RecordDescriptor red = new RecordDescriptor
        (CustomerOrder.class, "customer,orders", null, new String[][] {
            { "customer.id",   "getId",      "setId"      },
            { "customer.name", "getName",    "setName"    },
            { "orders.id",     "getOrderId", "setOrderId" },
            { "orders.volume", "getVolume",  "setVolume"  },
        });
    protected RecordDescriptor getDescriptor() { return red; }
    
    private int id;
    private String name;
    private int orderId;
    private int volume;
    
    public int getId() { return id; }
    public String getName() { return name; }
    public int getOrderId() { return orderId; }
    public int getVolume() { return volume; }
    
    
    public void setId(int val) { id = val; }
    public void setName(String val) { name = val; }
    public void setVolume(int val) { volume = val; }
    public void setOrderId(int val) { orderId = val; }
    
    public ResultIterator query(int customerId) throws SQLException {
        id = customerId;
        return super.query(constraint() + JOIN);
    }

    public static void main(String[] args) throws Exception {
        DatabaseFactory.setDatabaseName("jdbc:odbc:avalondb");
        DatabaseFactory.setResourceAccessor
            (new ResourceAccessorJ2SE(System.getProperties()));

        // Get all orders of customer 570336
        CustomerOrder co = new CustomerOrder();
        ResultIterator iter = co.query(570336);
        if (iter != null) {
            do { System.out.println(co.toString()); }
            while(iter.next());
        }
    }
}
