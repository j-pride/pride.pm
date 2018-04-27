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

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.mathema.pride.util.PreparedStatementLogger;

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
    protected PreparedStatementLogger logger;
    protected PreparedInsert revisioningPreparedInsert;

    public PreparedOperation(String operation, RecordDescriptor red) throws SQLException {
    	try {
			db = DatabaseFactory.getDatabase(red.getContext());
			this.operation = operation;
			this.red = red;
			stmt = db.getConnection().prepareStatement(operation);
			db.addListener(this);
			logger = new PreparedStatementLogger(db, operation);
		} catch (Exception e) {
    		db.processSevereButSQLException(e);
		}
    }

	public int execute(Object obj) throws SQLException {
		try {
			setParameters(obj);
			if (red.isRevisioned()) {
				revisioningPreparedInsert.execute(obj);
			}
			return stmt.executeUpdate();
		}
		catch(SQLException sqlx) {
			db.sqlLogError(sqlx);
			throw sqlx;
		}
		finally {
			stmt.clearParameters();
			logger.reset();
		}
	}

	public void addBatch(Object obj) throws SQLException {
		try {
			setParameters(obj);
			stmt.addBatch();
			if (red.isRevisioned()) {
				revisioningPreparedInsert.addBatch(obj);
			}
		}
		catch(SQLException sqlx) {
			db.sqlLogError(sqlx);
			throw sqlx;
		}
		finally {
			logger.reset();
		}
	}

	public int[] executeBatch() throws SQLException {
		try {
			if (red.isRevisioned()) {
				revisioningPreparedInsert.executeBatch();
			}
			return stmt.executeBatch();
		}
		finally { 
		    stmt.clearBatch(); 
	        logger.reset();
		}
	}

    public abstract void setParameters(Object obj) throws SQLException;
    
    @Override
    public void setBindParameter(Method setter, int parameterIndex, Object preparedValue)
    		throws ReflectiveOperationException {
    	if (preparedValue != null) {
    		logger.logBindingAndScroll(preparedValue, parameterIndex);
    		setter.invoke(getStatement(),
                    new Object[] { new Integer(parameterIndex), preparedValue });
    	} else {
    		throw new IllegalArgumentException("preparedValue must not be null");
    	}
    }
    
    @Override
    public void setBindParameterNull(int parameterIndex, int columnType) throws SQLException {
		logger.logBindingAndScroll("NULL", parameterIndex);
		getStatement().setNull(parameterIndex, columnType);
    }
    
    public void close() throws SQLException {
        if (stmt != null) {
            stmt.close();
            stmt = null;
        }
        if (revisioningPreparedInsert != null) {
        	revisioningPreparedInsert.close();
        	revisioningPreparedInsert = null;
		}
    }

	@Override
	public PreparedStatement getStatement() { return stmt; }

	@Override
	public Database getDatabase() { return db; }
	
    @Override
    public void commit(TransactionEvent e) throws SQLException { rollback(e); }
    
    @Override
    public void rollback(TransactionEvent e) throws SQLException { close(); }
    
    public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/database/PreparedOperation.java,v 1.1 2001/08/08 14:11:35 lessner Exp $";
}

/* $Log: PreparedOperation.java,v $
/* Revision 1.1  2001/08/08 14:11:35  lessner
/* Temporary state
/*
 */