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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abstract base class for convenient usage of prepared statements.
 * Support for that is pretty new in PriDE and not yet completed.
 * Currently there are the derived classes {@link PreparedUpdate}
 * and {@link PreparedInsert} available for the most important kinds
 * of operations which require performance optimization.
 *
 * @author <a href="mailto:jan.lessner@acoreus.de">Jan Lessner</a>
 */
abstract public class PreparedOperation implements PreparedOperationI, TransactionListener
{
    protected PreparedStatement stmt;
    protected RecordDescriptor red;
    protected String operation;
    protected Database db;

    public PreparedOperation(String operation, RecordDescriptor red) throws SQLException {
        db = DatabaseFactory.getDatabase(red.getContext());
        this.operation = operation;
        this.red = red;
        stmt = db.getConnection().prepareStatement(operation, Statement.RETURN_GENERATED_KEYS);
        db.addListener(this);
    }

	public int execute(Object obj) throws SQLException {
		try {
			setParameters(obj);
			if (db.isLogging()) // Optimization to avoid string assembly if not required
				db.sqlLog(operation + " using " + obj.toString());
			return stmt.executeUpdate();
		}
		catch(SQLException sqlx) {
			db.sqlLogError(sqlx);
			throw sqlx;
		}
		finally {
			stmt.clearParameters();
		}
	}

	public void addBatch(Object obj) throws SQLException {
		try {
			setParameters(obj);
			if (db.isLogging()) // Optimization to avoid string assembly if not required
				db.sqlLog(operation + " using " + obj.toString());
			stmt.addBatch();
		}
		catch(SQLException sqlx) {
			db.sqlLogError(sqlx);
			throw sqlx;
		}
	}

	public int[] executeBatch() throws SQLException {
		try { return stmt.executeBatch(); }
		finally { stmt.clearBatch(); }
	}

    public abstract void setParameters(Object obj) throws SQLException;

    public void close() throws SQLException {
        if (stmt != null) {
            stmt.close();
            stmt = null;
        }
    }

	@Override
	public PreparedStatement getStatement() { return stmt; }

	@Override
	public Database getDatabase() { return db; }
	
    public void commit(TransactionEvent e) throws SQLException { rollback(e); }
    
    public void rollback(TransactionEvent e) throws SQLException { close(); }
    
    public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/database/PreparedOperation.java,v 1.1 2001/08/08 14:11:35 lessner Exp $";
}

/* $Log: PreparedOperation.java,v $
/* Revision 1.1  2001/08/08 14:11:35  lessner
/* Temporary state
/*
 */
