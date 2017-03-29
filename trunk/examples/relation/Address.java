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

public class Address extends MappedObject {
    protected static RecordDescriptor red = new RecordDescriptor
        (Address.class, "address", null, new String[][] {
            { "id",     "getId",     "setId" },
            { "street", "getStreet", "setStreet" },
            { "city",   "getCity",   "setCity" }
        });
	protected RecordDescriptor getDescriptor() { return red; }

    private int id;
    private String street;
    private String city;

    public int getId() { return id; }
    public String getStreet() { return street; }
    public String getCity() { return city; }

    public void setId(int val) { id = val; }
    public void setStreet(String val) { street = val; }
    public void setCity(String val) { city = val; }
    
    public Address(int id) throws SQLException {
    	setId(id);
    	find();
    }
}
