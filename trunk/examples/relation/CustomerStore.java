/*******************************************************************************
 * Copyright (c) 2001-2005 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - Example code
 *******************************************************************************/
package relation;

import java.sql.SQLException;
import java.util.Collection;

class CustomerStore {

    // Implementation of an address retrieval with PriDE
    Address getCustomerAddress(Customer customer) throws SQLException {
            return new Address(customer.getAddressKey());
    }

    // Implementation of an order retrieval with PriDE
    Collection getCustomerOrders(Customer customer) throws SQLException {
            Order order = new Order(customer.getId());
            return order.query(new String[] { "customer_id" }).toArrayList();
    }

    // to be continued
}