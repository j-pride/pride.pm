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

// Commonly usable class for attribute maximum calculation
public class MaxDescriptor extends RecordDescriptor {
    private String maxfield; // The databae field to get the maximum of
    private Integer max;     // The maximum found
    public Integer getMax() { return max; }
    public void setMax(Integer max) { this.max = max; }
    
    // MaxDescriptor defines itself as the sink for query results.
    public MaxDescriptor(RecordDescriptor baseDescriptor, String maxfield)
        throws IllegalDescriptorException {
        super(MaxDescriptor.class, baseDescriptor.getTableName(), null,
              new String[][]{ {null, "getMax", "setMax"} });
        this.maxfield = maxfield;
    }
    
    // As a difference to an ordinary record descriptor, MaxDescriptor
    // does not ask the database for '*' (i.e. all fields) but for the
    // maximum of a particular field
    protected String getResultFields() { return "max(" + maxfield + ")"; }
    
    public static int getMax(String field, String where, RecordDescriptor red)
        throws SQLException {
        MaxDescriptor maxdesc =  new MaxDescriptor(red, field);
        DatabaseFactory.getDatabase().query(where, maxdesc, maxdesc, false);
        return (maxdesc.getMax() == null) ? -1 : maxdesc.getMax().intValue();
    }
}
