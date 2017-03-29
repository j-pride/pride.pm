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

public class Customer extends MappedObject {
    protected static RecordDescriptor red = new RecordDescriptor
        (Customer.class, "customer", null, new String[][] {
            { "id",      "getId",      "setId" },
            { "name",    "getName",    "setName" },
        });
    protected RecordDescriptor getDescriptor() { return red; }
    
    private int id;
    private String name;
    
    public int getId() { return id; }
    public String getName() { return name; }
    
    public void setId(int val) { id = val; }
    public void setName(String val) { name = val; }
    
    // typical persistent construction
    public Customer(int id, String name) throws SQLException {
        setId(id);
        setName(name);
        create();
    }
    
    // typical reconstruction
    public Customer(int id) throws SQLException {
        setId(id);
        find();
    }
    
    public Customer() {}
}
