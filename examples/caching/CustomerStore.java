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
package caching;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import de.mathema.pride.*;

public class CustomerStore {
    private Map customers = new HashMap();
    
    void createCustomer(Customer customer) throws SQLException {
        customer.create();
        customers.put(new Integer(customer.getId()), customer);
    }
    
    Customer getCustomer(int id) throws SQLException {
        Customer c = (Customer)customers.get(new Integer(id));
        if (c == null) {
            c = new Customer(id);
            customers.put(new Integer(id), c);
        }
        return c;
    }
    
    // and so forth
}
