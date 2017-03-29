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
package relation;

import java.sql.SQLException;
import de.mathema.pride.*;

public class Customer extends MappedObject {
    protected static RecordDescriptor red = new RecordDescriptor
        (Customer.class, "customer", null, new String[][] {
            { "id",      "getId",      "setId" },
            { "name",    "getName",    "setName" },
            { "address_key",    "getAddressKey",    "setAddressKey" }
        });
	protected RecordDescriptor getDescriptor() { return red; }

    private int id;
    private String name;
    private int addressKey;

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAddressKey() { return addressKey; }

    public void setId(int val) { id = val; }
    public void setName(String val) { name = val; }
    public void setAddressKey(int val) { addressKey = val; }
}
