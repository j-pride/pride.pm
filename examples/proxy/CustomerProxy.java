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

import de.mathema.pride.*;

public class CustomerProxy extends MappedObject {
    protected static RecordDescriptor red = new RecordDescriptor
        (CustomerProxy.class, "customer", null, new String[][] {
            { "id",        "getId",        "setId" },
            { "name",      "getName",      "setName" },
            { "surname",   "getSurname",   "setSurname" }}
         );
    
    public RecordDescriptor getDescriptor() { return red; }
    
    private int id;
    private String name;
    private String surname;
    
    /** Returns a string representation of the form
     * "Lessner, Jan (22735004)"
     */
    public String toString() {
        return name + ", " + surname + " (" + id + ")";
    }

    // to be continued...
}
    
