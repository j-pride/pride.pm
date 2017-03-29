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

public class Order extends MappedObject {
    protected static RecordDescriptor red = new RecordDescriptor
        (Order.class, "orders", null, new String[][] {
            { "id",     "getId", "setId" },
            { "volume", "getVolume",  "setVolume"  },
            { "customer_id", "getCustomerId",  "setCustomerId"  }
        });
    protected RecordDescriptor getDescriptor() { return red; }
    
    private int id;
    private String name;
    private int customerId;

    public int getCustomerId() { return customerId; }
    public int getId() { return id; }
    public String getName() { return name; }

    public void setCustomerId(int i) { customerId = i; }
    public void setId(int i) { id = i; }
    public void setName(String string) { name = string; }

}

