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
package locking;

import java.sql.SQLException;
import de.mathema.pride.*;

class OptimisticLock extends MappedObject {
    protected static RecordDescriptor red = new RecordDescriptor
        (OptimisticLock.class, null, null, new String[][] {
            { "version", "getVersion", "setVersion" },
        });
    protected RecordDescriptor getDescriptor() { return red; }
    public String[] getKeyFields() { return super.getKeyFields(); }
    
    private long version;
    public long getVersion() { return version; }
    public void setVersion(long val) { version = val; }
    
    protected OptimisticLock() { version = 0; }

    /*
     * The simple but fast approach
     */
    /*
    public int update() throws SQLException {
        setVersion(getVersion() + 1);
        int numRows = update
            (constraint() + " AND version=" + (getVersion()-1));
        if (numRows == 0)
            throw new SQLException("optimistic lock error");
        return numRows;
    }
    */

    /*
     * The sophisticated but slower approach
     */
    public int update() throws SQLException {
        OptimisticLockClone clone = new OptimisticLockClone(this);
        clone.findAndLock();
        if (clone.getVersion() > getVersion())
            throw new SQLException("optimistic lock error");
        if (clone.getVersion() < getVersion())
            throw new SQLException("consistency error");
        setVersion(getVersion() + 1);
        return super.update();
    }
}
