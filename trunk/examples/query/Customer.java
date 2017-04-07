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
package query;

import java.sql.SQLException;
import de.mathema.pride.*;
import static de.mathema.pride.WhereCondition.Operator.*;

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
   
    public void findInRange() throws SQLException 
    {
        ResultIterator iter = query("id >= 1000 AND id <= 2000");
        do {
            System.out.println(getName());
        }
        while(iter.next());
    }

    public void findInRange2() throws SQLException 
    {
        Customer c = new Customer();
        WhereCondition exp = new WhereCondition();
        exp = exp.and("id", GREATEREQUAL, 1000);
        exp = exp.and("id", LESSEQUAL, 2000);
        ResultIterator iter = c.query(exp.toString());
        do {
            System.out.println(c.getName());
        }
        while(iter.next());
    }
    
    public void findForUpdate() throws SQLException {
        setName("L%");
        String constraint = constraint(new String[] { "name" }, true);
        ResultIterator iter = query(constraint + " FOR UPDATE");
        do {
            System.out.println(getName());
        }
        while(iter.next());
    }

    public int getMaxID() throws SQLException {
        return MaxDescriptor.getMax("id", null, getDescriptor());
    }
}
