/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 * 
 * Contributors:
 *     Jan Lessner, S&N AG
 *******************************************************************************/
package pm.pride;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import pm.pride.util.generator.PreparedStatementLogger;

/**
 * Abstract base class for convenient usage of prepared statements. Currently there
 * are the derived classes {@link PreparedUpdate}, {@link PreparedInsert}, and
 * {@link PreparedSelect} available for the most important kinds of operations
 * which require performance optimization. PriDE also uses this classes internally
 * when the usage of bind variables is switched on.
 *
 * @author <a href="mailto:jan.lessner@s-und-n.de">Jan Lessner</a>
 */
abstract public class PreparedOperation implements PreparedOperationI, TransactionListener, AutoCloseable
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
			stmt = db.autogeneratedKeysSupported() ?
				db.getConnection().prepareStatement(operation, Statement.RETURN_GENERATED_KEYS) :
				db.getConnection().prepareStatement(operation);
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
    		Class<?> targetType = setter.getParameterTypes()[1];
    		logger.logBindingAndScroll(preparedValue, parameterIndex, targetType);
    		preparedValue = db.formatPreparedValue(preparedValue, targetType);
    		setter.invoke(getStatement(), parameterIndex, preparedValue);
    	}
    	else {
    		throw new IllegalArgumentException("preparedValue must not be null");
    	}
    }
    
    @Override
    public void setBindParameterNull(int parameterIndex, int columnType) throws SQLException {
		logger.logBindingAndScroll("NULL", parameterIndex, null);
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
    
}
