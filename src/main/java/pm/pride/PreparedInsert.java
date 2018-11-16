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
package pm.pride;

import java.sql.SQLException;

/**
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class PreparedInsert extends PreparedOperation
{
	protected String table;
	protected String[] autoFields;
	
    public PreparedInsert(RecordDescriptor red)
        throws SQLException, ReflectiveOperationException {
        this(null, red);
    }

	public PreparedInsert(String[] autoFields, RecordDescriptor red)
		throws SQLException, ReflectiveOperationException {
		super("insert into " + red.getTableName() +
			  " (" + red.getFieldNames(autoFields) + ") values" +
			  " (" + red.getCreationValues(null, autoFields, DatabaseFactory.getDatabase(red.getContext())) + ")", red);
		this.table = red.dbtable;
		this.autoFields = autoFields;
        if (red.isRevisioned()) {
			revisioningPreparedInsert = new PreparedInsert(((RevisionedRecordDescriptor) red).getRevisioningRecordDescriptor());
        }
	}

    @Override
    public int execute(Object obj) throws SQLException {
        int numRows = -1;
        try {
            numRows = super.execute(obj);
            db.extractAutofieldValuesForObject(numRows, autoFields, obj, stmt, red);
        } catch (Exception x) {
            if (stmt != null) {
                closeAfterException(x);
            }
        }
        return numRows;
    }

    public void closeAfterException(Exception x) throws SQLException {
        if (x instanceof SQLException)
            db.sqlLogError((SQLException)x);
        close();
        db.processSevereButSQLException(x);
    }

    public void setParameters(Object obj) throws SQLException {
		try { red.getCreationValues(obj, autoFields, this, table, 1); }
		catch(Exception x) { db.processSevereButSQLException(x); }
    }

}
