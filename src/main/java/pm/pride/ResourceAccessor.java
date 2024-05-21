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

import java.sql.*;
import java.util.GregorianCalendar;

/**
 * Interface for access to physical resources. Each {@link pm.pride.Database}
 * object has one resource accessor associated which is passed at
 * construction time.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public interface ResourceAccessor extends SQL.Formatter
{
	interface DBType {
		String ORACLE     = "oracle";
		String MYSQL      = "mysql";
		String DB2        = "db2";
		String SQLSERVER  = "sqlserver";
		String POSTGRES   = "postgres";
    String HSQL       = "hsql";
    String SQLITE     = "sqlite";
    String MARIADB    = "mariadb";
	}
	
	interface Config {
		String PREFIX     = "pride.";
		String DBTYPE     = PREFIX + "dbtype";
		String DATEFORMAT = PREFIX + "format.date";
		String TIMEFORMAT = PREFIX + "format.time";
		String LOGFILE    = PREFIX + "logfile";
		String LOGMAX     = PREFIX + "logmax";
		String USER       = PREFIX + "user";
		String PASSWORD   = PREFIX + "password";
		String DRIVER     = PREFIX + "driver";
		String SYSTIME    = PREFIX + "systime";
		String DB         = PREFIX + "db";
		String BINDVARS   = PREFIX + "bindvars";
		
		String EMPTY      = "";
	}
    
    interface AutoKeyMode {
        int UNKNOWN       = 0;
        int STANDARD      = 1;
        int VENDOR        = 2;
    }
	
	/**
	 * Default value for the database system time identifier,
	 * if the database specific CURRENT_TIMESTAMP has to be used
	 * Default: Thu Jan 01 00:00:01 CET 0001
	 */
	java.util.Date SYSTIME_DEFAULT =
	    new Date((new GregorianCalendar(0, 0, 1, 0, 0, 1)).getTimeInMillis());

	String DEFAULT_IDENTIFIER_QUOTATION = "\"";
	
	/**
	 * Returns the timestamp, that is currently used to indicate, that a date 
	 * has to be replaced by the database server's system time.
	 * If a date for an sql statement matches this value, it will
	 * be replaced by a database specific string in insert and update statements.
	 * @return The indicator timestamp
	 */
	java.util.Date getSystime();
	  
    /** Switch SQL logging on and off.
     * @param db The logical name of the database to toggle the logging state for
     * @param state The requested state, true for switching logging on, false for switching off
     * @return the new logging state. The value might defer from
     * <code>state</code> if the ResourceAccessor denies toggling.
     */
    boolean setLogging(Database db, boolean state);

    /** Returns the current SQL logging state
     * @return the current logging state - true = on, false = off */
    boolean isLogging();

    /** Writes the passed operation to the SQL log, if logging is enabled
     * @param db The logical name of the database to log an operation for
     * @param operation The operation to log
     */
    void sqlLog(Database db, String operation);

    /** Writes the passed SQL exception to the log, if logging is enabled
     * @param db The logical name of the database to log an exception for
     * @param sqlx The exception to log
     */
    void sqlLogError(Database db, SQLException sqlx);

    /** Returns a connection to the database represented by the
     * ResourceAccessor. This function is called before every
     * single database operation and is therefore supposed to
     * run very fast.
     * @param db The logical name of the database to provide a connection for
     * @return The connection
     */
    Connection getConnection(String db) throws Exception;
    
    /** Release any connections being allocated by calls of
     * {@link ResourceAccessor#getConnection}. This function may
     * be called, before a worker thread is about to close down
     * or fall asleep.
     */
    void releaseConnection() throws SQLException;

    /** Release a particular connection being allocated by a call
     * of {@link ResourceAccessor#getConnection}. This function is
     * called after every every single database operation
     */
    void releaseConnection(Connection con) throws SQLException;

    /** Return an array of auto fields to be used as parameter for
     * an SQL insertion operation. If the function returns null, the
     * insertion is executed without any parameters. This is in fact
     * the main reason for this function because not all JDBC drivers
     * support Statement.executeUpdate(String operation, String[] autoFields).
     * @param stmt The statement which is about to be executed
     * @param rawAutoFields The auto fields to consider as declared in a descriptor
     * @throws Exception
     */
    String[] getAutoFields(Statement stmt, String[] rawAutoFields) throws Exception;
    
    /** Fetch the values for auto-generated fields from the last
     * insertion execution performed on the passed statement. This
     * is by default done using JDBC's getGeneratedKeys() function.
     * However, some databases and JDBC drivers have their own specific
     * ways to fetch that data.
     * @param stmt SQL statement object which was last be used to execute an insertion.
     * @param rawAutoFields The auto fields to consider as declared in a descriptor
     */
    ResultSet getAutoFieldVals(Statement stmt, String[] rawAutoFields)
        throws SQLException;
    
    /** Returns the physical table name for the logical table
     * name referred to by parameter <code>logicalTableName</code>
     */
    String getTableName(String logicalTableName) throws Exception;

	/** Performs an SQL formating of the passed value */
    String formatValue(Object value, Class<?> targetType, boolean forLogging);

	/** Performs an SQL formating of the passed operator.
	 * @param operator Any of the operators defined in {@link WhereCondition.Operator}
	 * @param rawValue The raw value the operator is applied to before it
	 *   is formated by {@link #formatValue}. May be null.
	 */
    String formatOperator(String operator, Object rawValue);

	/** Performs a type conversion of a passed value for usage in
	 * a prepared statement write access function. This function is
	 * required if the raw value can not directly be passed to a
	 * PreparedStatement, e.g. java.util.Date.
	 */
	Object formatPreparedValue(Object value, Class<?> targetType);

	/**
	 * Performs a type conversion of a passed value for usage in a result set
	 * read access function. This function is required if the raw value can not
	 * directly be passed to an entity's attribute, e.g. arrays or enums
	 */
	Object unformatValue(Object value, Class<?> targetType) throws SQLException;

    /**
     * Retrieves the URL for this DBMS represented by the
     * ResourceAccessor. 
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/sql/DatabaseMetaData.html#getURL()">DatabaseMetaData</a>
     * 
     * @param db
     * @return the database url
     * @throws Exception
     */
    String getURL(String db) throws Exception;
    
    /**
     * Retrieves the user name as known to this database represented by the
     * ResourceAccessor.
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/sql/DatabaseMetaData.html#getUserName()">DatabaseMetaData</a>
     * 
     * @param db
     * @return the user name
     * @throws Exception
     */
    String getUserName(String db) throws Exception;

    /**
     * Returns the type of DB being represented by this resource accessor.
     * See constants in interface {@link DBType} for the
     * type keys which are supported by default.
     */
    String getDBType();
    
    /**
     * Returns true if SQL statements should be assembled via bind-variables by default
     * Without specifying any default, PriDE talks plain SQL.
     */
    boolean bindvarsByDefault();

	boolean autogeneratedKeysSupported();

	String getIdentifierQuotation();

}
