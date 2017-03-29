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

import de.mathema.pride.RecordDescriptor;
import de.mathema.pride.ResultIterator;

public class ActiveCustomer extends Customer {
    // An inner join to use for retrieval
    private static final String JOIN = "customer c inner join orders o on o.customer_id = c.id";

    // An extended record descriptor specifying the alias "c"
    // and the alternate table expression JOIN
    protected static RecordDescriptor red = new RecordDescriptor(Customer.red, "c", JOIN);

    protected RecordDescriptor getDescriptor() { return red; }
}
