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

import java.lang.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import de.mathema.pride.*;

class OptimisticLockClone extends OptimisticLock {
    // The object to update and thus the one to take
    // the primary key data for a version number query from
    private OptimisticLock source;

       // Specialized record descriptor, which takes data for a query from member
       // 'source' above, queries only the 'version' field and stores the
       // result in the clone.
    private class CloneDescriptor extends RecordDescriptor {
        public CloneDescriptor() {
            super(OptimisticLock.class, null, OptimisticLock.red, null);
        }

        // Always take the table name from the 'source' object above
        public String getTableName() { return source.getDescriptor().getTableName(); }

        // Always build constraints from the 'source' object above
        public String getConstraint(Object obj, String[] dbfields, boolean byLike)
            throws IllegalAccessException, InvocationTargetException {
            return source.getDescriptor().getConstraint
                (source, dbfields, byLike, DatabaseFactory.getDatabase());
        }

        // Always query for the 'version' field only
        public String getResultFields() { return "version"; }
    }

    private RecordDescriptor red = new CloneDescriptor();
    public RecordDescriptor getDesc() { return red; }
    public String[] getKeyFields() { return source.getKeyFields(); }

    public OptimisticLockClone(OptimisticLock source) { this.source = source; }

    public void findAndLock() throws SQLException {
        find(constraint() + " FOR UPDATE");
    }
}
