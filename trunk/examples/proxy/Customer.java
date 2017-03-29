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
package proxy;

import java.sql.Date;
import de.mathema.pride.*;

public class Customer extends CustomerProxy {
    protected static RecordDescriptor red = new RecordDescriptor
        (Customer.class, "customer", CustomerProxy.red, new String[][] {
            { "phone",     "getPhone",     "setPhone" },
            { "fax",       "setFax",       "getFax" },
            { "lastOrder", "getLastOrder", "setLastOrder" },
            { "solvency",  "getSolvency",  "setSolvency" },
            { "credit",    "getCredit",    "setCredit" }
        });

    private String phone;
    private String fax;
    private Date lastOrder;
    private int solvency;
    private int credit;
    
    // to be continued...
}
