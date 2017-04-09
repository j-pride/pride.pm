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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

/** Database access class, providing base functionality for the
 * persistence framework. The member functions in here are supposed
 * to be rarely used by the application except common things like
 * commit() and rollback().
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class Database implements SQLFormatter
{
    private final ExceptionListener exceptionListener;
    private final ResourceAccessor accessor;

    private final String dbname;
    private Vector txlisteners = null;
    private Integer statementTimeout = null;


    // ------------- G e n e r a l   e x c e p t i o n   h a n d l i n g ------------

    void processException(Exception x) throws Exception {
		exceptionListener.process(this, x);
		throw x;
    }

    void processSevereException(Exception x) throws RuntimeException {
		exceptionListener.processSevere(this, x);
    }

    int processSevereButSQLException(Exception x) throws SQLException {
		if (x instanceof SQLException)
		    throw (SQLException)x;
		processSevereException(x);
        return -1;
    }

    // ------------ R e s o u r c e   a c c e s s o r   s t u f f ---------------

    /** Returns a JDBC database connection for standard JDBC programming.
     * There is one unique connection returned per thread.
     */
    public Connection getConnection() throws SQLException {
		try { return accessor.getConnection(dbname); }
		catch(Exception x) { processSevereButSQLException(x); return null; }
    }

    /** Close the current thread's connection for later reuse.
     * It is recommended to explicitely release connections if the associated
     * thread is about to terminate or sleep for a long time. JDBC's built-in
     * connection garbage collection is either not working at all or is not
     * this agile as it may be required.
     */
    public void releaseConnection() throws SQLException {
		accessor.releaseConnection();
    }

    public void releaseConnection(Connection con) throws SQLException {
		accessor.releaseConnection(con);
    }

    public String getTableName(RecordDescriptor red) {
    	return getPhysicalTableName(red.getTableName());
    }

	public String getPhysicalTableName(String logicalTableName) {
		try { return accessor.getTableName(logicalTableName); }
		catch(Exception x) {
			processSevereException(x);
			return null;
		}
	}

    public String getPhysicalTableName(ExtensionDescriptor xd) {
        try { return accessor.getTableName(xd.getTableName()); }
        catch(Exception x) {
            processSevereException(x);
            return null;
        }
    }
    
	/**
	 * Returns the resource accessors timestamp, that is currently used to
	 * identify, that a date has to be replaced by the database server's
	 * system time. If a date for an sql statement matches this value, it will
	 * be replaced by a database specific string in insert and update statements. 
	 */
    public java.util.Date getSystime() {
    	return accessor.getSystime();
    }
    
	/**
	 * Retrieves the URL for this DBMS represented by the ResourceAccessor.
	 * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/sql/DatabaseMetaData.html#getURL()">DatabaseMetaData</a>
	 * 
	 * @return the database url
	 * @throws Exception
	 */
    public String getURL() throws SQLException {
		try { return this.accessor.getURL(this.dbname); }
		catch(Exception x) { processSevereButSQLException(x); return null; }
    }

    /**
     * Return the name of the databaserepresented by this object. In a managed
     * environment, it is a logical lookup name (JNDI name in J2EE), otherwise
     * it is identical to the database URL (see function {@link #getURL()}).
     * 
     * @return The database name
     */
    public String getDBName() { return this.dbname; }
    
	/**
	 * Retrieves the user name as known to this database represented by the
     * ResourceAccessor.
	 * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/sql/DatabaseMetaData.html#getUserName()">DatabaseMetaData</a>
	 * 
	 * @param db
	 * @return the user name
	 */
    public String getUserName() throws SQLException{
		try { return this.accessor.getUserName(this.dbname); }
		catch(Exception x) { processSevereButSQLException(x); return null; }
    }

    /**
     * Returns the type of DB being represented by the current resource accessor.
     * See constants in interface {@link ResourceAccessor#DBType} for the
     * type keys which are supported by default.
     */
    public String getDBType() {
        return this.accessor.getDBType();
    }
    

    /** Create a database object
     * @param log writes SQL statements to log file if parameter <code>log</code> is <code>true</code>.
     * The name of the logfile is determined by system property <code>xbc.sqllog</code> which default
     * <code>./sql.log</code>. It is recommended to use {@link DatabaseFactory DatabaseFactory.getDatabase()}
     * instead.
     */
    public Database(String dbname, ResourceAccessor accessor, ExceptionListener el, boolean log) {
		this.accessor = accessor;
		this.exceptionListener = el;
		this.dbname = dbname;
		txlisteners = new Vector(10);
		if (accessor == null)
		    processSevereException(new NoAccessorException());
        else
            setLogging(log);
    }

    /** Formats the passed object for SQL syntax, e.g. by putting single-quotes
     * arround strings etc. The function is not very flexible yet, it just supports
     * special formatting for String, java.util.Date, java.sql.Date, java.sql.Timestamp,
     * and null. All other other cases it runs value.toString()
     */
    public String formatValue(Object value) {
		return accessor.formatValue(value);
    }

    public String formatOperator(String operator, Object rawValue) {
        return accessor.formatOperator(operator, rawValue);
    }
    
	public Object formatPreparedValue(Object value) {
		return accessor.formatPreparedValue(value);
	}


    /** Fetches the first record from a result set returned for <code>query</code>.
     * The result is stored in the passed <code>obj</code> the mapping scheme of
     * which is described by parameter <code>red</code>.
     * @return <code>null</code> if parameter <code>keepRest</code> is true, i.e.
     * only the very first matching record is of interest. A {@link ResultIterator}
     * object otherwise, which allows to walk through the following records by storing
     * them step by step into <code>obj</code>.
     */
    protected ResultIterator fetchFirst(String query, Object obj,
                                        RecordDescriptor red,
                                        boolean keepRest)
        throws SQLException {
        ResultIterator ri = sqlQuery(query, obj, red);
        return returnIteratorIfNotEmpty(ri, query, keepRest);
    }
    
    protected ResultIterator returnIteratorIfNotEmpty(ResultIterator ri, String query, boolean keepRest) throws SQLException {
        if (!ri.next()) {
            ri.close();
            throw new NoResultsException(query);
        }
        if (keepRest)
            return ri;
        else {
            ri.close();
            return null;
        }
    }

    /** Writes the passed SQL string to the SQL log file. The log file is wrapped
     * every SQLMAXCOUNT lines.
     */
    public void sqlLog(String operation) { accessor.sqlLog(this, operation); }

    public void sqlLogError(SQLException sqlx) { accessor.sqlLogError(this, sqlx); }
    
    /** Switch SQL logging on and off.
     * @return the current logging state, i.e. usually the desired state
     * passed in parameter <code>val</code> except when toggling doesn't
     * work. E.g. switching logging on doesn't work if there is no SQL
     * log file specified.
     */
    public boolean setLogging(boolean val) { return accessor.setLogging(this, val); }

    /** Returns <code>true</code> if SQL logging is switched on */
    public boolean isLogging() { return accessor.isLogging(); }

    /** Runs an SQL update statement according to the passed operation
     * @param The operation to execute
     * @param An array of field which to fetch auto-generated values from after
     *   a successfull insertion. Should be null if either the operation is not
     *   an insertion or if there are no auto-fields existing
     * @param obj The object to store auto-field values in
     * @param red Descriptor providing the field mappings
     */
    public int sqlUpdate(String operation, String[] autoFields, Object obj, RecordDescriptor red) throws SQLException {
    	ConnectionAndStatement cns = null;
        ResultSet autoResults = null;
        int numRows = -1;
        try {
        	cns = new ConnectionAndStatement(operation, false);
            String[] autoFieldsForExec = accessor.getAutoFields(cns.stmt, autoFields);
            if (autoFieldsForExec != null && autoFieldsForExec.length > 0)
                numRows = cns.stmt.executeUpdate(operation, autoFieldsForExec);
            else
                numRows = cns.stmt.executeUpdate(operation);
            if (autoFields != null && autoFields.length > 0) {
                autoResults = accessor.getAutoFieldVals(cns.stmt, autoFields);
                if (autoResults != null && autoResults.next())
                    red.record2object(obj, autoResults, ResultIterator.COLUMN_STARTINDEX, autoFields);
            }
            cns.close();
        }
        catch(Exception x) {
        	if (cns != null)
        		cns.closeAfterException(x);
        }
        finally {
            if (autoResults != null) autoResults.close();
        }
        return numRows;
    }

    /** Like function above but without any auto-field expectations */
    public int sqlUpdate(String operation) throws SQLException {
        return sqlUpdate(operation, null, null, null);
    }

    /** Runs an SQL query according to the passed operation */
    public ResultIterator sqlQuery(String operation) throws SQLException {
        return sqlQuery(operation, null, null);
    }

    /** Runs an SQL query and returns a {@link ResultIterator}, initialized
     * with parameters <code>obj</code> and <code>red</code> and the
     * {@link java.sql.ResultSet} returned by the query.
     */
    public ResultIterator sqlQuery(String operation, Object obj, RecordDescriptor red)
        throws SQLException {
    	ConnectionAndStatement cns = null;
        try {
        	cns = new ConnectionAndStatement(operation, false);
            ResultSet rs = cns.stmt.executeQuery(operation);
            return new ResultIterator(cns.stmt, false, rs, obj, red, this, cns.con);
        }
        catch(Exception x) {
        	if (cns != null)
        		cns.closeAfterException(x);
            return null;
        }
    }

    /**
     * Executes an SQL Statement that is neither a query nor an update and does not return anything.
     * 
     * @param sqlStatement
     * @throws SQLException
     */
    public void sqlExecute(String sqlStatement) throws SQLException {
        ConnectionAndStatement cns = null;
        try {
        	cns = new ConnectionAndStatement(sqlStatement, false);
            cns.stmt.execute(sqlStatement);
            cns.close();
        }
        catch (Exception x) {
        	if (cns != null)
        		cns.closeAfterException(x);
        }
    }
    
    protected String where(String where) {
        return ((where != null) && where.trim().length() > 0) ?
            " where " + where : "";
    }
    
    public void setStatementTimeout(Integer timeout) {
        this.statementTimeout = timeout;
    }
    
    /** Fetch a record from the database and store the results in a JAVA object
     * according to the passed mapping descriptor.
     * @param primaryKey Primary key used for unique selection from the database
     * @param obj Destination object to store the data in
     * @param red Descriptor providing the field mappings and the table name to access
     */
    public void fetchRecord(Object primaryKey, Object obj, RecordDescriptor red)
        throws SQLException {
    	WhereCondition primaryKeyCondition = new WhereCondition().and(red.getPrimaryKeyField(), primaryKey);
        query(primaryKeyCondition, obj, red, false);
    }

    /** Fetch a record from the database and store the results in a JAVA object
     * according to the passed mapping descriptor.
     * @param obj Destination object to store the data in and to take the primary key from
     * @param red Descriptor providing the field mappings and the table name to access
     */
    public void fetchRecord(Object obj, RecordDescriptor red)
        throws SQLException {
        try { fetchRecord(red.getPrimaryKey(obj), obj, red); }
        catch(Exception x) { processSevereButSQLException(x); }
    }

    /** Run a database query.
     * @param dbfield table field which is to be used as selection criteria
     * @param value value which the field determined by <code>dbfield</code> must match
     * @param obj Destination object to store the data in
     * @param red Descriptor providing the field mappings and the table name to access
     * @param all Flag saying wether to fetch all matching records or only the first one
     * @return A {@link ResultIterator} if parameter <code>all</code> is true, null
     * otherwise. The first matching record's data is stored in <code>obj</code>.
     * Following records can successivly be copied to <code>obj</code> using the
     * ResultIterator.
     * @throws NoResultsException if no matching record could be found
     */
    public ResultIterator query(String dbfield, Object value, Object obj,
                                RecordDescriptor red, boolean all)
        throws SQLException {
          if(value == null)
             return query(dbfield + " is  null ", obj, red, all);
        return query(dbfield + " = " + formatValue(value), obj, red, all);
    }

    /** Run a database query.
     * @param dbfields table fields which are to be used as selection criteria
     * @param obj both, destination object for result data and source object for
     * the values selection field values
     * @param red descriptor providing the field mappings and the table name to access
     * @param all flag saying wether to fetch all matching records or only the first one
     * @return A {@link ResultIterator} if parameter <code>all</code> is true, null
     * otherwise. The first matching record's data is stored in <code>obj</code>.
     * Following records can successivly be copied to <code>obj</code> using the
     * ResultIterator.
     * @throws NoResultsException if no matching record could be found
     */
    public ResultIterator query(String[] dbfields, Object obj, RecordDescriptor red, boolean all)
        throws SQLException {
		try { return query(red.assembleWhereCondition(obj, dbfields, false), obj, red, all); }
		catch(Exception x) { processSevereButSQLException(x); return null; }
    }

    /** Like function <code>query()</code> above but selects using the
     * <code>like</code> operator
     */
    public ResultIterator wildcardSearch(String[] dbfields, Object obj, RecordDescriptor red, boolean all)
        throws SQLException {
        try { return query(red.assembleWhereCondition(obj, dbfields, true), obj, red, all); }
		catch(Exception x) { processSevereButSQLException(x); return null; }
    }

    /** Run a database query.
     * @param where where-clause to apply (excluding the keyword 'where'!)
     * @param obj Destination object to store the data in
     * @param red Descriptor providing the field mappings and the table name to access
     * @param all Flag saying wether to fetch all matching records or only the first one
     * @return A {@link ResultIterator} if parameter <code>all</code> is true, null
     * otherwise. The first matching record's data is stored in <code>obj</code>.
     * Following records can successivly be copied to <code>obj</code> using the
     * ResultIterator.
     * @throws NoResultsException if no matching record could be found
     */
    public ResultIterator query(String where, Object obj, RecordDescriptor red, boolean all)
        throws SQLException {
        String query = "select " + red.getResultFields() + " from " +
            getTableName(red) + where(where);
        return fetchFirst(query, obj, red, all);
    }

    public ResultIterator query(WhereCondition where, Object obj, RecordDescriptor red, boolean all)
        throws SQLException {
		String whereString = where.toSQL(this);
    	if (where.requiresBinding()) {
            String query = "select " + red.getResultFields() + " from " +
                    getTableName(red) + " where " + whereString;
            ConnectionAndStatement cns = null;
            try {
            	cns = new ConnectionAndStatement(query, true);
                where.bind(this, cns.getStatement());
                ResultSet rs = cns.getStatement().executeQuery();
                ResultIterator ri = new ResultIterator(cns.stmt, false, rs, obj, red, this, cns.con);
                return returnIteratorIfNotEmpty(ri, query, true);
            }
            catch (Exception x) {
            	if (cns != null)
            		cns.closeAfterException(x);
                return null;
            }
    	}
    	else {
    		return query(whereString, obj, red, all);
    	}
    }

    /** Run a database query, returning all records of the table
     * denoted by parameter <code>red</code>.
     * @param obj Destination object to store the data in
     * @param red Descriptor providing the field mappings and the table name to access
     * @return A {@link ResultIterator}. The first matching record's data is stored in
     * <code>obj</code>. Following records can successivly be copied to <code>obj</code>
     * using the ResultIterator.
     * @throws NoResultsException if no matching record could be found
     */
    public ResultIterator queryAll(Object obj, RecordDescriptor red)
      throws SQLException {
        return query((String)null, obj, red, true);
    }

    /** Update a database record with the data of a JAVA object according to the
     * passed mapping descriptor. The first attribute listed in parameter
     * <code>red</code> is assumed to make up the primary key. It is used for unique
     * object identification in the update statement's where-clause and is not
     * modified
     * @param obj Source object to extract the data from
     * @param red Descriptor providing the field mappings and the table name to access
     */
    public int updateRecord(Object obj, RecordDescriptor red)
        throws SQLException {
    	return updateRecord(new String[] { red.getPrimaryKeyField() }, obj, red);
    }

    /** Update a database record.
     * @param dbkeyfields database fields which are supposed to determine the records of
     *  interest. Typically the primary key, identifying a single object.
     * @param obj source object to extract the data from. There are only those fields
     *  taken into account which are not listed in <code>dbkeyfields</code>
     * @param red descriptor providing the field mappings and the table name to access
     */
    public int updateRecord(String[] dbkeyfields, Object obj, RecordDescriptor red)
        throws SQLException {
        return updateRecord(dbkeyfields, null, obj, red);
    }

	/** Update a database record.
	 * @param dbkeyfields database fields which are supposed to determine the records of
	 *  interest. Typically the primary key, identifying a single object.
	 * @param obj source object to extract the data from. There are only those fields
	 *  taken into account which are not listed in <code>dbkeyfields</code>
	 * @param red descriptor providing the field mappings and the table name to access
	 */
	public int updateRecord(String[] dbkeyfields, String[] updatefields, Object obj, RecordDescriptor red)
		throws SQLException {
		PreparedUpdate preparedUpdate = null;
		try {
			if (WhereCondition.bindDefault) {
				preparedUpdate = new PreparedUpdate(dbkeyfields, updatefields, red);
				return preparedUpdate.execute(obj);
			}
            String where = red.getConstraint(obj, dbkeyfields, false, this);
			String update = "update " + getTableName(red) + " set " +
				red.getUpdateValues(obj, dbkeyfields, updatefields, this) +
                where(where);
			return sqlUpdate(update);
		}
		catch(Exception x) {
			return processSevereButSQLException(x);
		}
		finally {
			if (preparedUpdate != null)
				preparedUpdate.close();
		}
	}

    /** Update a database record.
     * @param where where-clause, identifying the records of interest
     * @param obj source object to extract the data from
     * @param red descriptor providing the field mappings and the table name to access
     */
	@Deprecated
    public int updateRecord(String where, Object obj, RecordDescriptor red)
        throws SQLException {
        return updateRecord(where, null, obj, red);
    }

    public int updateRecord(WhereCondition where, Object obj, RecordDescriptor red)
    	throws SQLException {
    	return updateRecord(where, null, obj, red);
    }

	/** Update a database record.
	 * @param where where-clause, identifying the records of interest
	 * @param obj source object to extract the data from
	 * @param red descriptor providing the field mappings and the table name to access
	 */
    @Deprecated
	public int updateRecord(String where, String[] updatefields, Object obj, RecordDescriptor red)
		throws SQLException {
		try {
			String update = "update " + getTableName(red) + " set " +
				red.getUpdateValues(obj, null, updatefields, this) + where(where);
			return sqlUpdate(update);
		}
		catch(Exception x) { return processSevereButSQLException(x); }
	}

	public int updateRecord(WhereCondition where, String[] updatefields, Object obj, RecordDescriptor red)
		throws SQLException {
		try {
			String whereString = where.toSQL(this);
	    	if (where.requiresBinding()) {
				String update = "update " + getTableName(red) + " set " +
						red.getUpdateValues(null, null, updatefields, this) + " where " + whereString;
	            ConnectionAndStatement cns = null;
	            try {
	            	cns = new ConnectionAndStatement(update, true);
	            	int nextParam = red.getConstraint(obj, updatefields, cns, null, 1);
	                where.bind(this, cns.getStatement(), nextParam);
	                int result = cns.getStatement().executeUpdate();
	                cns.close();
	                return result;
	            }
	            catch (Exception x) {
	            	if (cns != null)
	            		cns.closeAfterException(x);
	                return -1;
	            }
	    	}
	    	else {
	    		return updateRecord(whereString, updatefields, obj, red);
	    	}
		}
		catch(Exception x) { return processSevereButSQLException(x); }
	}

    /** Returns a header for a record inserting of the form
     * <code>"insert into <table> ( <field 1> ... <field n> ) values "</code>
     * This function is helpfull to assemble bulk update statements.
     * @param descriptor providing the field names and the table name to access
     * @param autoFields list of fields being automatically managed and initialized
     *   by the database itself and must therefore not be provided on record creation.
     *   May be null if there are no fields to ignore.
     */
    public String getInsertionHeader(RecordDescriptor red, String[] autoFields) {
        return "insert into " + getTableName(red) + " (" + red.getFieldNames(autoFields) + ") values ";
    }

    /** Add a record to the database.
     * @param obj source object to extract the data from
     * @param red descriptor providing the field mappings and the table name to access
     */
    public int createRecord(String[] autoFields, Object obj, RecordDescriptor red)
        throws SQLException {
    	PreparedInsert preparedInsert = null;
		try {
			if (WhereCondition.bindDefault) {
				preparedInsert = new PreparedInsert(autoFields, red);
				return preparedInsert.execute(obj);
			}
            String operation = getInsertionHeader(red, autoFields) + " (" +
                red.getCreationValues(obj, autoFields, this) + ")";
	    	return sqlUpdate(operation, autoFields, obj, red);
	    }
		catch(Exception x) {
			return processSevereButSQLException(x);
		}
		finally {
			if (preparedInsert != null)
				preparedInsert.close();
		}
    }

    /** Delete a record from the database.
     * @param obj source object to extract the object's key value from.
     * @param red descriptor providing the field mappings and the table name to access.
     *  The very first field in the descriptor is supposed to make up the primary key
     */
    public int deleteRecord(Object obj, RecordDescriptor red) throws SQLException {
		try {
		    return deleteRecord(red.getPrimaryKeyField() + " = " +
	                            formatValue(red.getPrimaryKey(obj)), obj, red);
		}
		catch(Exception x) { return processSevereButSQLException(x); }
    }

    /** Delete a record from the database.
     * @param where where-clause to select the records of interest
     * @param obj not used
     * @param red descriptor providing the table name to access.
     */
    public int deleteRecord(String where, Object obj, RecordDescriptor red) throws SQLException {
        String delete = "delete from " + getTableName(red) + where(where);
        return sqlUpdate(delete);
    }

    /** Delete record(s) from the database.
     * @param dbkeyfields database fields which are supposed to determine the records of
     *  interest. Typically the primary key, identifying a single object.
     * @param obj source object to extract the key data from.
     * @param red descriptor providing the field mappings and the table name to access
     */
    public int deleteRecord(String[] dbkeyfields, Object obj, RecordDescriptor red)
        throws SQLException {
		try {
		    String delete = "delete from " + getTableName(red) +
			    where(red.getConstraint(obj, dbkeyfields, false, this));
		    return sqlUpdate(delete);
		}
		catch(Exception x) { return processSevereButSQLException(x); }
    }


	/** Terminate the current transaction. Runs all registered
	 * transaction listeners' commit resp. rollback functions first
	 */
	public void endTransaction(boolean commit) throws SQLException {
		Connection con = null;
		try {
			synchronized(this) {
				TransactionEvent event = new TransactionEvent(this);
				Iterator iter = txlisteners.iterator();
				while(iter.hasNext()) {
					TransactionListener listener = (TransactionListener)iter.next();
					if (commit)
						listener.commit(event);
					else
						listener.rollback(event);
				}
			}
			con = getConnection();
			if (commit)
				con.commit();
			else
				con.rollback();
		}
		finally {
			if (con != null)
				releaseConnection(con);
		}
	}

    /** Commit the current transaction.
     * Runs all registered transaction listeners' commit function first
     */
    public void commit() throws SQLException {
    	endTransaction(true);
    }

    /** Rollback the current transaction.
     * Runs all registered transaction listeners' rollback function first
     */
    public void rollback() throws SQLException {
    	endTransaction(false);
    }

    /** Add a transaction listener to this database object */
    public synchronized void addListener(TransactionListener l) { txlisteners.add(l); }

    /** Remove a transaction listener from this database object */
    public synchronized void removeListener(TransactionListener l) { txlisteners.remove(l); }

    protected class ConnectionAndStatement implements PreparedOperationI {
    	final Connection con;
    	final Statement stmt;
    	
    	ConnectionAndStatement(String statementContent, boolean prepared) throws SQLException {
            sqlLog(statementContent);
            con = getConnection();
            stmt = prepared ? con.prepareStatement(statementContent) : con.createStatement();
            if (statementTimeout != null)
                stmt.setQueryTimeout(statementTimeout);
    		
    	}

		public void close() throws SQLException {
            if (stmt != null) stmt.close();
            if (con != null) releaseConnection(con);
		}

		public void closeAfterException(Exception x) throws SQLException {
        	if (x instanceof SQLException)
        		sqlLogError((SQLException)x);
        	close();
            processSevereButSQLException(x);
		}
		
		@Override
		public Database getDatabase() { return Database.this; }

		@Override
		public PreparedStatement getStatement() { return (PreparedStatement)stmt; }
    }

}

/* $Log:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/Database.java-arc  $
 * 
 *    Rev 1.1   06 Sep 2002 14:54:18   math19
 * Now use new SQLFormatter interface.
 */
