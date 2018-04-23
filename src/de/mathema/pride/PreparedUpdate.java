/*******************************************************************************
 * Copyright (c) 2001-2007 The PriDE team and MATHEMA Software GmbH
 * All rights reserved. This toolkit and the accompanying materials 
 * are made available under the terms of the GNU Lesser General Public
 * License (LGPL) which accompanies this distribution, and is available
 * at http://pride.sourceforge.net/LGPL.html
 * 
 * Contributors:
 *     Jan Lessner, MATHEMA Software GmbH - initial API and implementation
 *******************************************************************************/
package de.mathema.pride;

import java.sql.*;
import java.lang.reflect.InvocationTargetException;

/**
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class PreparedUpdate extends PreparedOperation
{
    protected String[] dbkeyfields;
    protected String[] updatefields;

	public PreparedUpdate(String[] dbkeyfields, String[] updatefields, RecordDescriptor red)
		throws SQLException, ReflectiveOperationException {
		super("update " + red.getTableName() + " set " +
			  red.getUpdateValues(null, dbkeyfields, updatefields, DatabaseFactory.getDatabase(red.getContext())) + " where " +
			  red.getConstraint(null, dbkeyfields, false, DatabaseFactory.getDatabase(red.getContext())), red);
		this.dbkeyfields = dbkeyfields;
		this.updatefields = updatefields;
		if (red.isRevisioned()) {
		    if (this.updatefields != null) {
		        throw new BatchUpdateRevisioningEnabledException("Usage of RevisionedRecordDescriptor with updateFields will not work." +
                        " Turn off revisioning in the RecordDescriptor and do revisioning manually.");
            }
			revisioningPreparedInsert = new PreparedInsert(((RevisionedRecordDescriptor) red).getRevisioningRecordDescriptor());
		}
	}

	public PreparedUpdate(String[] dbkeyfields, RecordDescriptor red)
		throws SQLException, ReflectiveOperationException {
		this(dbkeyfields, null, red);
	}

    public PreparedUpdate(RecordDescriptor red)
    	throws SQLException, ReflectiveOperationException {
    	this(new String[] { red.getPrimaryKeyField() }, red);
    }

	public void setParameters(Object obj) throws SQLException {
		try {
			int position = red.getUpdateValues(obj, dbkeyfields, updatefields, this, null, 1);
			red.getConstraint(obj, dbkeyfields, this, null, position);
		}
		catch(Exception x) { db.processSevereButSQLException(x); }
	}

    public final static String REVISION_ID = "$Header$";
}

/* $Log: $
 */
