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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;

import pm.pride.ResourceAccessor.DBType;

/** Database access class, providing base functionality for the
 * persistence framework. The member functions in here are supposed
 * to be rarely used by the application except common things like
 * commit() and rollback().
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class Database implements SQL.Formatter
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

    RuntimeException processSevereException(Exception x) throws RuntimeException {
		throw exceptionListener.processSevere(this, x);
    }

    RuntimeException processSevereButSQLException(Exception x) throws SQLException {
		if (x instanceof SQLException)
		    throw (SQLException)x;
		throw processSevereException(x);
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
	 * @throws SQLException
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
	 * @return the user name
	 */
    public String getUserName() throws SQLException{
		try { return this.accessor.getUserName(this.dbname); }
		catch(Exception x) { processSevereButSQLException(x); return null; }
    }

    /**
     * Returns the type of DB being represented by the current resource accessor.
     * See constants in interface {@link DBType} for the type keys which are supported by default.
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

    @Override
	public boolean bindvarsByDefault() {
		return accessor.bindvarsByDefault();
	}

	/** Fetches the first record from a result set returned for <code>query</code>.
     * The result is stored in the passed <code>obj</code> the mapping scheme of
     * which is described by parameter <code>red</code>.
     * @return A {@link ResultIterator} which allows to walk through the following records by storing
     * them step by step into <code>obj</code>. If parameter keepRest is false, the ResultIterator is
     * immediately closed after fetching the first record into the passed object. The caller can only
     * use the returned Iterator to check if the query was successful. Further iterating is omitted.
     * A return value of <code>null</code> indicates that there was no matching record found. 
     */
    protected ResultIterator fetchFirst(String query, Object obj,
                                        RecordDescriptor red,
                                        boolean keepRest)
        throws SQLException {
        ResultIterator ri = sqlQuery(red, obj, query);
        return returnIteratorIfNotEmpty(ri, query, keepRest);
    }
    
    protected ResultIterator returnIteratorIfNotEmpty(ResultIterator ri, String query, boolean keepRest) throws SQLException {
        if (!ri.next()) {
            ri.close();
            return null;
        }
        if (!keepRest) {
            ri.close();
        }
        return ri;
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
     * @param operation The operation to execute
     * @param autoFields An array of field which to fetch auto-generated values from after
     *   a successfull insertion. Should be null if either the operation is not
     *   an insertion or if there are no auto-fields existing
     * @param obj The object to store auto-field values in
     * @param red Descriptor providing the field mappings
     * @param params Optional parameters in case this is a plain prepared SQL statement,
     *   i.e. function was called from {@link #sqlUpdate(String, Object...)}
     */
    public int sqlUpdate(String operation, String[] autoFields, Object obj, RecordDescriptor red, Object... params) throws SQLException {
    	ConnectionAndStatement cns = null;
        ResultSet autoResults = null;
        int numRows = -1;
        try {
        	cns = new ConnectionAndStatement(this, operation, params);
            String[] autoFieldsForExec = accessor.getAutoFields(cns.stmt, autoFields);
            if (autoFieldsForExec != null && autoFieldsForExec.length > 0) {
                numRows = cns.executeUpdate(autoFieldsForExec);
            }
            else {
                numRows = cns.executeUpdate();
            }
            extractAutofieldValuesForObject(numRows, autoFields, obj, cns.stmt, red);
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

    protected void revisionEntity(RecordDescriptor red, Object entity) throws SQLException {
        if (red.isRevisioned()) {
            RecordDescriptor recordDescriptorForRevisioning = ((RevisionedRecordDescriptor) red).getRevisioningRecordDescriptor();
            createRecord(recordDescriptorForRevisioning, entity, null);
        }
    }

    protected void extractAutofieldValuesForObject(int numRows, String[] autoFields, Object obj, Statement stmt, RecordDescriptor red) throws SQLException, ReflectiveOperationException {
        ResultSet autoResults = null;
        if (numRows == 1 && autoFields != null && autoFields.length > 0) {
            autoResults = accessor.getAutoFieldVals(stmt, autoFields);
            if (autoResults != null && autoResults.next())
                red.record2object(obj, autoResults, ResultIterator.COLUMN_STARTINDEX, autoFields);
        }
    }

    /** Like function above but without any auto-field expectations */
    public int sqlUpdate(String operation, Object... params) throws SQLException {
        return sqlUpdate(operation, null, null, null, params);
    }

    /** Runs an SQL query according to the passed operation
     * @param sqlStatement The statement to execute
     * @param params Optional parameters if the statement contains bind variables and therefore needs to be executed as a {@link PreparedStatement}
     */
    public ResultIterator sqlQuery(String sqlStatement, Object... params) throws SQLException {
        return sqlQuery(null, null, sqlStatement, params);
    }

    /** Runs an SQL query and returns a {@link ResultIterator}, initialized
     * with parameters <code>obj</code> and <code>red</code> and the
     * {@link java.sql.ResultSet} returned by the query.
     */
    public ResultIterator sqlQuery(RecordDescriptor red, Object obj, String operation, Object... params)
        throws SQLException {
    	ConnectionAndStatement cns = null;
        try {
        	cns = new ConnectionAndStatement(this, operation, params);
            ResultSet rs = cns.executeQuery();
            return new ResultIterator(cns.stmt, false, rs, obj, red, this, cns.con);
        }
        catch(Exception x) {
        	if (cns != null) {
        		cns.closeAfterException(x);
        	}
        	throw processSevereButSQLException(x);
        }
    }

    /**
     * Executes an SQL Statement that is neither a query nor an update and does not return anything.
     * 
     * @param sqlStatement The statement to execute
     * @param params Optional parameters if the statement contains bind variables and therefore needs to be executed as a {@link PreparedStatement}
     * @throws SQLException
     */
    public boolean sqlExecute(String sqlStatement, Object... params) throws SQLException {
        ConnectionAndStatement cns = null;
        try {
        	cns = new ConnectionAndStatement(this, sqlStatement, params);
        	boolean result = cns.execute();
            cns.close();
            return result;
        }
        catch (Exception x) {
        	if (cns != null) {
        		cns.closeAfterException(x);
        	}
        	throw processSevereButSQLException(x);
        }
    }
    
    protected String where2string(WhereCondition where) {
        if (where == null)
            return null;
        SQL.Formatter formatter = where.formatter != null ? where.formatter : this;
        return where.toSQL(formatter);
    }

    protected String where(String where) {
        return ((where != null) && where.trim().length() > 0) ?
            " where " + where : "";
    }
    
    public void setStatementTimeout(Integer timeout) {
        this.statementTimeout = timeout;
    }
    
    public Integer getStatementTimeout() {
    	return this.statementTimeout;
    }
    
    /** Fetch a record from the database and store the results in a JAVA object
     * according to the passed mapping descriptor.
     * @param red Descriptor providing the field mappings and the table name to access
     * @param obj Destination object to store the data in
     * @param primaryKey Primary key used for unique selection from the database
     * @deprecated use fetchRecord(WhereCondition, Object, RecordDescriptor) insted
     */
    @Deprecated
    public boolean fetchRecord(RecordDescriptor red, Object obj, Object primaryKey)
        throws SQLException {
    	WhereCondition primaryKeyCondition = new WhereCondition().and(red.getPrimaryKeyField(), primaryKey);
        return query(red, false, obj, primaryKeyCondition) != null;
    }

    public boolean fetchRecord(RecordDescriptor red, Object obj, WhereCondition primaryKeyCondition) throws SQLException {
    	return query(red, false, obj, primaryKeyCondition) != null;
    }

    /** Fetch a record from the database and store the results in a JAVA object
     * according to the passed mapping descriptor.
     * @param red Descriptor providing the field mappings and the table name to access
     * @param obj Destination object to store the data in and to take the primary key from
     */
    public boolean fetchRecord(RecordDescriptor red, Object obj)
        throws SQLException {
        try {
        	return fetchRecord(red, obj, red.assembleWhereCondition(obj, red.getPrimaryKeyFields(), false));
        }
        catch(Exception x) {
        	processSevereButSQLException(x);
        	return false;
        }
    }

    /** Run a database query.
     * @param red Descriptor providing the field mappings and the table name to access
     * @param all Flag saying wether to fetch all matching records or only the first one
     * @param obj Destination object to store the data in
     * @param dbfield table field which is to be used as selection criteria
     * @param value value which the field determined by <code>dbfield</code> must match
     * @return A {@link ResultIterator} if at least one matching record is present or null
     * otherwise. The first matching record's data is stored in <code>obj</code>.
     * Following records can successively be copied to <code>obj</code> using the
     * ResultIterator.
     */
    public ResultIterator query(RecordDescriptor red, boolean all, Object obj,
                                String dbfield, Object value)
        throws SQLException {
          if(value == null)
             return query(red, all, obj, dbfield + " is  null ");
        return query(red, all, obj, dbfield + " = " + formatValue(value));
    }

    /** Run a database query.
     * @param red descriptor providing the field mappings and the table name to access
     * @param all flag saying wether to fetch all matching records or only the first one
     * @param obj both, destination object for result data and source object for
     * the values selection field values
     * @param dbfields table fields which are to be used as selection criteria
     * @return A {@link ResultIterator} if at least one matching record is present or null
     * otherwise. The first matching record's data is stored in <code>obj</code>.
     * Following records can successivly be copied to <code>obj</code> using the
     * ResultIterator.
     */
    public ResultIterator query(RecordDescriptor red, boolean all, Object obj, String... dbfields)
        throws SQLException {
		try { return query(red, all, obj, red.assembleWhereCondition(obj, dbfields, false)); }
		catch(Exception x) { processSevereButSQLException(x); return null; }
    }

    /** Like function <code>query()</code> above but selects using the
     * <code>like</code> operator
     */
    public ResultIterator wildcardSearch(RecordDescriptor red, boolean all, Object obj, String... dbfields)
        throws SQLException {
        try { return query(red, all, obj, red.assembleWhereCondition(obj, dbfields, true)); }
		catch(Exception x) { processSevereButSQLException(x); return null; }
    }

    /** Run a database query.
     * @param red Descriptor providing the field mappings and the table name to access
     * @param all Flag saying wether to fetch all matching records or only the first one
     * @param obj Destination object to store the data in
     * @param where where-clause to apply (excluding the keyword 'where'!)
     * @return A {@link ResultIterator} if at least one matching record is present or null
     * otherwise. The first matching record's data is stored in <code>obj</code>.
     * Following records can successively be copied to <code>obj</code> using the
     * ResultIterator.
     */
    public ResultIterator query(RecordDescriptor red, boolean all, Object obj, String where)
        throws SQLException {
        String query = "select " + red.getResultFields() + " from " +
            getTableName(red) + where(where);
        return fetchFirst(query, obj, red, all);
    }

    public ResultIterator query(RecordDescriptor red, boolean all, Object obj, WhereCondition where) throws SQLException {
		String whereString = where2string(where);

    	if (where != null && where.requiresBinding(this)) {
            String query = "select " + red.getResultFields() + " from " +
                    getTableName(red) + " where " + whereString;
            ConnectionAndStatement cns = null;
            try {
            	cns = new ConnectionAndStatement(this, query, true);
                where.bind(this, cns);
                ResultSet rs = cns.getStatement().executeQuery();
                ResultIterator ri = new ResultIterator(cns.stmt, false, rs, obj, red, this, cns.con);
                return returnIteratorIfNotEmpty(ri, query, all);
            }
            catch (Exception x) {
            	if (cns != null)
            		cns.closeAfterException(x);
                return null;
            }
    	}
    	else {
    		return query(red, all, obj, whereString);
    	}
    }

	/** Run a database query, returning all records of the table
     * denoted by parameter <code>red</code>.
	 * @param red Descriptor providing the field mappings and the table name to access
	 * @param obj Destination object to store the data in
     * @return A {@link ResultIterator} if at least one matching record is present or null otherwise.
     *   The first matching record's data is stored in <code>obj</code>. Following records can
     *   successively be copied to <code>obj</code> using the ResultIterator.
     */
    public ResultIterator queryAll(RecordDescriptor red, Object obj)
      throws SQLException {
        return query(red, true, obj, (String)null);
    }

    /** Update a database record with the data of a JAVA object according to the
     * passed mapping descriptor. The first attribute listed in parameter
     * <code>red</code> is assumed to make up the primary key. It is used for unique
     * object identification in the update statement's where-clause and is not
     * modified
     * @param red Descriptor providing the field mappings and the table name to access
     * @param obj Source object to extract the data from
     */
    public int updateRecord(RecordDescriptor red, Object obj)
        throws SQLException {
    	return updateRecord(red, obj, red.getPrimaryKeyFields());
    }

    /** Update a database record.
     * @param red descriptor providing the field mappings and the table name to access
     * @param obj source object to extract the data from. There are only those fields
     *  taken into account which are not listed in <code>dbkeyfields</code>
     * @param dbkeyfields database fields which are supposed to determine the records of
     *  interest. Typically the primary key, identifying a single object.
     */
    public int updateRecord(RecordDescriptor red, Object obj, String... dbkeyfields)
        throws SQLException {
        return updateRecord(red, obj, dbkeyfields, null);
    }

	/** Update a database record.
	 * @param red descriptor providing the field mappings and the table name to access
	 * @param obj source object to extract the data from. There are only those fields
	 *  taken into account which are not listed in <code>dbkeyfields</code>
	 * @param dbkeyfields database fields which are supposed to determine the records of
	 *  interest. Typically the primary key, identifying a single object.
	 */
	public int updateRecord(RecordDescriptor red, Object obj, String[] dbkeyfields, String... updatefields)
		throws SQLException {
		PreparedUpdate preparedUpdate = null;
		try {
			if (accessor.bindvarsByDefault() || red.withBind) {
				preparedUpdate = new PreparedUpdate(dbkeyfields, updatefields, red);
				return preparedUpdate.execute(obj);
			}
            String where = red.getConstraint(obj, dbkeyfields, false, this);
			String update = "update " + getTableName(red) + " set " +
				red.getUpdateValues(obj, dbkeyfields, updatefields, this) +
                where(where);
			final int result = sqlUpdate(update);
			revisionEntity(red, obj);
			return result;
		}
		catch(Exception x) {
			throw processSevereButSQLException(x);
		}
		finally {
			if (preparedUpdate != null)
				preparedUpdate.close();
		}
	}

    /** Update a database record.
     * @param red descriptor providing the field mappings and the table name to access
     * @param obj source object to extract the data from
     * @param where where-clause, identifying the records of interest
     */
	@Deprecated
    public int updateRecord(RecordDescriptor red, Object obj, String where)
        throws SQLException {
        return updateRecord(red, obj, where, null);
    }

    public int updateRecord(RecordDescriptor red, Object obj, WhereCondition where)
    	throws SQLException {
    	return updateRecord(red, obj, where, null);
    }

	/** Update a database record.
	 * @param red descriptor providing the field mappings and the table name to access
	 * @param obj source object to extract the data from
	 * @param where where-clause, identifying the records of interest
	 */
    @Deprecated
	public int updateRecord(RecordDescriptor red, Object obj, String where, String... updatefields)
		throws SQLException {
		try {
			String update = "update " + getTableName(red) + " set " +
				red.getUpdateValues(obj, red.getPrimaryKeyFields(), updatefields, this) + where(where);
			final int result = sqlUpdate(update);
			revisionEntity(red, obj);
			return result;
		}
		catch(Exception x) { throw processSevereButSQLException(x); }
	}

	public int updateRecord(RecordDescriptor red, Object obj, WhereCondition where, String... updatefields)
		throws SQLException {
		try {
			String whereString = where2string(where);
	    	if (where != null && where.requiresBinding(this)) {
	    		String[] excludeFields = red.getPrimaryKeyFields();
				String update = "update " + getTableName(red) + " set " +
						red.getUpdateValues(null, excludeFields, updatefields, this) + " where " + whereString;
	            ConnectionAndStatement cns = null;
	            try {
	            	cns = new ConnectionAndStatement(this, update, true);
	            	int nextParam = red.getUpdateValues(obj, excludeFields, updatefields, cns, null, 1);
	                where.bind(this, cns, nextParam);
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
	    		return updateRecord(red, obj, whereString, updatefields);
	    	}
		}
		catch(Exception x) { throw processSevereButSQLException(x); }
	}

    /** Returns a header for a record inserting of the form
     * <code>"insert into &lt;table&gt; ( &lt;field 1&gt; ... &lt;field n&gt; ) values "</code>
     * This function is helpfull to assemble bulk update statements.
     * @param red descriptor providing the field names and the table name to access
     * @param autoFields list of fields being automatically managed and initialized
     *   by the database itself and must therefore not be provided on record creation.
     *   May be null if there are no fields to ignore.
     */
    public String getInsertionHeader(RecordDescriptor red, String[] autoFields) {
        return "insert into " + getTableName(red) + " (" + red.getFieldNames(autoFields) + ") values ";
    }

    /** Add a record to the database.
     * @param red descriptor providing the field mappings and the table name to access
     * @param obj source object to extract the data from
     */
    public int createRecord(RecordDescriptor red, Object obj, String... autoFields)
        throws SQLException {
    	PreparedInsert preparedInsert = null;
		try {
			if (accessor.bindvarsByDefault()) {
				preparedInsert = new PreparedInsert(autoFields, red);
				return preparedInsert.execute(obj);
			}
            String operation = getInsertionHeader(red, autoFields) + " (" +
                red.getCreationValues(obj, autoFields, this) + ")";
			final int result = sqlUpdate(operation, autoFields, obj, red);
			revisionEntity(red, obj);
	    	return result;
	    }
		catch(Exception x) {
			throw processSevereButSQLException(x);
		}
		finally {
			if (preparedInsert != null)
				preparedInsert.close();
		}
    }

    /** Delete a record from the database.
     * @param red descriptor providing the field mappings and the table name to access.
     *  The very first field in the descriptor is supposed to make up the primary key
     * @param obj source object to extract the object's key value from.
     */
    public int deleteRecord(RecordDescriptor red, Object obj) throws SQLException {
		try {
		    return deleteRecord(red, obj, red.getPrimaryKeyFields());
		}
		catch(Exception x) { throw processSevereButSQLException(x); }
    }

    /** Delete a record from the database.
     * @param red descriptor providing the table name to access.
     * @param obj not used
     * @param where where-clause to select the records of interest
     */
    public int deleteRecord(RecordDescriptor red, Object obj, String where) throws SQLException {
        String delete = "delete from " + getTableName(red) + where(where);
        return sqlUpdate(delete);
    }

    /** Delete record(s) from the database.
     * @param red descriptor providing the field mappings and the table name to access
     * @param obj source object to extract the key data from.
     * @param dbkeyfields database fields which are supposed to determine the records of
     *  interest. Typically the primary key, identifying a single object.
     */
    public int deleteRecord(RecordDescriptor red, Object obj, String... dbkeyfields)
        throws SQLException {
		try {
		    String delete = "delete from " + getTableName(red) +
			    where(red.getConstraint(obj, dbkeyfields, false, this));
		    return sqlUpdate(delete);
		}
		catch(Exception x) { throw processSevereButSQLException(x); }
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

    public boolean autogeneratedKeysSupported() {
        switch (getDBType().toLowerCase()) {
            case ResourceAccessor.DBType.HSQL:
            case ResourceAccessor.DBType.MYSQL:
                return true;
            default:
                return false;
        }
    }

    /** Add a transaction listener to this database object */
    public synchronized void addListener(TransactionListener l) { txlisteners.add(l); }

    /** Remove a transaction listener from this database object */
    public synchronized void removeListener(TransactionListener l) { txlisteners.remove(l); }

}

/* $Log:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/Database.java-arc  $
 * 
 *    Rev 1.1   06 Sep 2002 14:54:18   math19
 * Now use new SQLFormatter interface.
 */
