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
public class PreparedInsert extends PreparedOperation
{
	protected String table;
	protected String[] autoFields;
	
    public PreparedInsert(RecordDescriptor red)
        throws SQLException, IllegalAccessException, InvocationTargetException {
        this(null, red);
    }

	public PreparedInsert(String[] autoFields, RecordDescriptor red)
		throws SQLException, IllegalAccessException, InvocationTargetException {
		super("insert into " + red.getTableName() +
			  " (" + red.getFieldNames(autoFields) + ") values" +
			  " (" + red.getCreationValues(null, autoFields, DatabaseFactory.getDatabase(red.getContext())) + ")", red);
		this.table = red.dbtable;
		this.autoFields = autoFields;
	}
	
    public void setParameters(Object obj) throws SQLException {
		try { red.getCreationValues(obj, autoFields, this, table, 1); }
		catch(Exception x) { db.processSevereButSQLException(x); }
    }

    public final static String REVISION_ID = "$Header$";
}

/* $Log: $
 */
