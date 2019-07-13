/*******************************************************************************
 * Copyright (c) 2001-2019 The PriDE team
 *******************************************************************************/
package pm.pride;

import java.io.FileWriter;
import java.lang.reflect.Array;
import java.sql.*;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public abstract class AbstractResourceAccessor implements ResourceAccessor {

	protected String dbType = null;
	protected Format dateFormat = null;
	protected Format timeFormat = null;
	protected String dbUser = null;
	protected String dbPassword = null;
	protected Date dbSystime = null;
	protected boolean bindvarsDefault = false;
    protected Properties props = null;
    protected int autoKeyMode = AutoKeyMode.UNKNOWN;

	//-----------------  S Q L   l o g g i n g   s t u f f  -----------------

	/** Maximum number of SQL statements to be written to the log file before
	 * the file is re-written from the start.
	 */
	protected static final int LOGMAX_DEFAULT = 100000;
	protected static DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	protected static final int MAX_COLLECTION_ITEMS_IN_LOG = 10;
	private String sqlLogFileName;
	private FileWriter sqllog = null;
	private boolean log;
	private int sqlcount = 0;
	private long logMax = LOGMAX_DEFAULT;

	/** Switch SQL logging on and off.
	 * @return the current logging state, i.e. usually the desired state
	 * passed in parameter <code>val</code> except when toggling doesn't
	 * work. E.g. switching logging on doesn't work if there is no SQL
	 * log file specified.
	 */
	public boolean setLogging(Database db, boolean val) {
		log = val;
		sqlLog(db, "SQL logging " + (log ? "enabled" : "disabled"));
		return isLogging();
	}

	/** Returns <code>true</code> if SQL logging is switched on */
	public boolean isLogging() { return (log && (sqllog != null)); }

	public void sqlLog(Database db, String operation) {
		try {
			if (sqllog != null && log) {
				synchronized(this) {
					if (sqllog != null && log) { // Double checking
						if (sqlcount >= logMax) {
							sqllog.close();
							sqllog = new FileWriter(sqlLogFileName);
							sqlcount = 0;
						}
						sqlcount++;
						sqllog.write("["+df.format(new java.util.Date())+"] " + operation);
						sqllog.write("\n");
						sqllog.flush();
					}
				}
			}
		}
		catch(Exception x) { db.processSevereException(x); }
	}

    public void sqlLogError(Database db, SQLException sqlx) {
    	sqlLog(db, "sql error: " + sqlx.getMessage());
    }

	//-----------------  e n d   S Q L   l o g g i n g   s t u f f  -----------------

	
    /** Makes '' from single-quotes in the passed string to make it suitable for SQL syntax
     * @param raw the raw String to escape quotes in
     * @return the string with the escaped single-quotes
     */
    protected static String escapeQuotes(String raw) {
    	return escape(raw, '\'');
    }

    /** Makes double backslashes from single backslashes which is only required for MySQL
     * @param raw the raw String to escape backslashes in
     * @return the string with the escaped single backslashes
     */
    protected String escapeBackslahes(String raw) {
    	return (ResourceAccessor.DBType.MYSQL.equals(dbType) ||
    			ResourceAccessor.DBType.MARIADB.equals(dbType)) ?
    			escape(raw, '\\') : raw;
    }

    /** Duplicate any occurence of the passed character in the passed string to make it suitable for SQL syntax
     * @param raw the raw String to duplicate  in
     * @param escapeChar the character to look for and to duplicate
     * @return the string with the escaped single-quotes
     */
    protected static String escape(String raw, char escapeChar) {
        String escaped = "";
        int start = 0, end;
        while((end = raw.indexOf(escapeChar, start)) != -1) {
            escaped += raw.substring(start, end+1) + escapeChar;
            start = end + 1;
            if (start >= raw.length())
                break;
        }
        if (start == 0)
            return raw;
        else
            return (start < raw.length()) ? escaped += raw.substring(start) : escaped;
    }

	protected String formatBoolean(Boolean value) {
	    if (dbType != null && dbType.equalsIgnoreCase(DBType.POSTGRES))
	        return value.toString();
		return value.booleanValue() ? "1" : "0";
	}
	
	/**
	 * Returns a format for date values, based on the database type. For Oracle databases, the proprietary
	 * to_date syntax is used. The precision of date representation depends on the vendor and must match
	 * the "native" precision loss when storing java.sql.Date values via prepared statements. Most databases
	 * drop the complete time portion while e.g. Oracle keeps a seconds precision and SQLite keeps
	 * everything down to milliseconds.
	 * 
	 * @return The common date format to use or null if there is no format known
	 */
	protected Format dateFormat() {
		if (dbType != null) {
			if(dbType.equalsIgnoreCase(DBType.ORACLE))
				return new SimpleDateFormat("'to_date('''yyyy-MM-dd HH:mm:ss''',''YYYY-MM-DD HH24:MI:SS'')'");
			if(dbType.equalsIgnoreCase(DBType.HSQL))
			    return new SimpleDateFormat("'to_date('''yyyy-MM-dd''',''YYYY-MM-DD'')'");
			if(dbType.equalsIgnoreCase(DBType.SQLITE))
				return new UnixTimeDateFormat();
			else if(dbType.equalsIgnoreCase(DBType.CLOUDSCAPE))
				return new SimpleDateFormat("''yyyy-MM-dd''");
		}
		return null;
	}
	
	protected Format timeFormat() {
		if (dbType != null) {
			if(dbType.equalsIgnoreCase(DBType.ORACLE))
				return new SimpleDateFormat("'to_timestamp('''yyyy-MM-dd HH:mm:ss.SSS''',''YYYY-MM-DD HH24:MI:SS.FF3'')'");
			if(dbType.equalsIgnoreCase(DBType.HSQL))
			    return new SimpleDateFormat("'to_timestamp('''yyyy-MM-dd HH:mm:ss.SSS''',''YYYY-MM-DD HH24:MI:SS.FF'')'");
			if(dbType.equalsIgnoreCase(DBType.SQLITE))
				return new UnixTimeDateFormat();
			else if(dbType.equalsIgnoreCase(DBType.CLOUDSCAPE))
				return new SimpleDateFormat("''yyyy-MM-dd HH:mm:ss.SSS''");
		}
		return null;
	}
	
	/**
	 * Like {@link #formatTime} but for date values. This function is
     * synchronized due to the fact that DateFormat.format is not!!
	 * @param date The value to format
	 * @return The SQL-formatted value
	 */
    synchronized protected String formatDate(java.sql.Date date) {
		if(dateFormat == null)
			dateFormat = dateFormat();
		return (dateFormat != null) ?
			dateFormat.format(date) : "'" + date + "'";
    }

	/**
	 * Formats a time value into an SQL-suitable string. The function uses the
	 * time format specified in the constructor or tries a get a common time
	 * format from {@link #timeFormat}. If there is nothing to find, it returns the
	 * value itself, surrounded by single-quotes. This function is synchronized
	 * due to the fact that DateFormat.format is not!!
	 * @param time The value to format
	 * @return The SQL-formatted value
	 */
    synchronized protected String formatTime(java.sql.Timestamp time) {
		if(timeFormat == null)
			timeFormat = timeFormat();
		return (timeFormat != null) ?
			timeFormat.format(time) : "'" + time + "'";
	}

    protected String formatEnum(Enum value) {
        return value.name();
    }
    
	/**
	 * This function maps java.util.Date to java.sql.Date or java.sql.Timestamp
	 * This mapping should match the specifications incResultSetAccess and
	 * PreparedStatementAccess.
	 * 
	 * @param value The value to convert
	 * @param targetType The expected type to cast the value to if known.
	 * 
	 * @return A java.sql.Timestamp or a java.sql.Date, depending on the
	 *  passed targetType. If targetType is null, the method returns a
	 *  java.sql.Date with seconds precision if the if the value was of
	 *  type java.util.Date or a derivation other than java.sql.Date or
	 *  java.sql.Timestamp. Otherwise the passed value itself.
	 */
	protected Object castJavaUtilDate(Object value, Class<?> targetType) {
		if (dbSystime != null && value instanceof java.util.Date) {
			if (dbSystime.getTime() == ((java.util.Date) value).getTime()) {
			  Object systimeValue = getSystimeConstant();
			  if (!(systimeValue instanceof java.util.Date))
			  	return systimeValue;
			}
		}

		if (value instanceof java.util.Date && value.getClass() != targetType) {
			long time = ((java.util.Date) value).getTime();
			if (targetType == null || targetType == java.sql.Date.class) {
				return new java.sql.Date(time);
			}
			return new java.sql.Timestamp(time);
		}
		
		return value;
	}
	
   /**
    * Returns the database specific constant representing the
    * database server's current system time, e.g. CURRENT_TIMESTAMP
    * for MySQL and Postgres or SYSDATE for Oracle. If there is
    * no constant known, the functions returns the current
    * client-side time instead.
    */
	private Object getSystimeConstant() {
		if (ResourceAccessor.DBType.MYSQL.equals(dbType) ||
			ResourceAccessor.DBType.MARIADB.equals(dbType) ||
			ResourceAccessor.DBType.POSTGRES.equals(dbType))
			return "CURRENT_TIMESTAMP";
		else if (ResourceAccessor.DBType.ORACLE.equals(dbType))
			return "SYSDATE";
		else if (ResourceAccessor.DBType.HSQL.equals(dbType))
			return "CURRENT_DATE";
		else if (ResourceAccessor.DBType.SQLITE.equals(dbType))
			return "strftime('%Y-%m-%d %H:%M:%f', 'now')";
		else
			return new java.util.Date();
	}
		
    /** Formats the passed object for SQL syntax, e.g. by putting single-quotes
     * around strings etc. The function is not very flexible yet, it just supports
     * special formatting for String, java.util.Date, java.sql.Date, java.sql.Timestamp,
     * and null. In all other cases it performs value.toString()
     */
    public String formatValue(Object value, Class<?> targetType, boolean forLogging) {
        if (value == null)
            return "NULL";
        if (value.getClass().isEnum())
            value = formatEnum((Enum)value);
        if (value.getClass() == String.class)
            return "'" + escapeBackslahes(escapeQuotes((String)value)) + "'";
        if (value.getClass() == Boolean.class)
        	return formatBoolean((Boolean)value);
        value = castJavaUtilDate(value, targetType);
		if (value.getClass() == java.sql.Timestamp.class)
        	return formatTime((java.sql.Timestamp)value);
        else if (value.getClass() == java.sql.Date.class)
        	return formatDate((java.sql.Date)value);
        else if (value instanceof java.util.Map)
            return formatMap((Map<?, ?>)value, forLogging);
        else if (value.getClass().isArray())
            return formatArray(value, forLogging);
        return value.toString();
    }

	private String formatArray(Object value, boolean forLogging) {
	    Class<?> itemClass = value.getClass().getComponentType();
	    String cutForLogging = "";
        int length = Array.getLength(value);
        if (forLogging && length > MAX_COLLECTION_ITEMS_IN_LOG) {
        	length = MAX_COLLECTION_ITEMS_IN_LOG;
        	cutForLogging = "...";
        }
	    if (itemClass == byte.class) {
	    	byte[] arrayValue = (byte[])value;
	    	if (length < arrayValue.length) {
		    	arrayValue = Arrays.copyOf(arrayValue, length);
	    	}
	    	return "('" + printHexBinary(arrayValue) + cutForLogging + "')";
	    }
	    StringBuffer result = new StringBuffer("'{");
        for (int i = 0; i < length; i++) {
            Object item = Array.get(value, i);
	    	appendFormattedCollectionItem(result, item);
	    }
	    cutOfTrailingComma(result);
        result.append(cutForLogging);
        result.append("}'");
        return result.toString();
    }

	static final String HEXES = "0123456789ABCDEF";
	public static String printHexBinary( byte [] raw ) {
	    if ( raw == null ) {
	        return null;
	    }
	    final StringBuilder hex = new StringBuilder( 2 * raw.length );
	    for ( final byte b : raw ) {
	        hex.append(HEXES.charAt((b & 0xF0) >> 4))
	            .append(HEXES.charAt((b & 0x0F)));
	    }
	    return hex.toString();
	}
	
    private String formatMap(Map<?, ?> value, boolean forLogging) {
    	StringBuffer result = new StringBuffer("'");
	    for (Entry<?, ?> entry: value.entrySet()) {
	    	result.append(entry.getKey().toString());
	    	result.append(" => ");
	    	appendFormattedCollectionItem(result, entry.getValue());
	    }
	    cutOfTrailingComma(result);
	    result.append("'");
	    return result.toString();
    }
    
    private void appendFormattedCollectionItem(StringBuffer result, Object item) {
        if (item == null) {
            result.append("null");
        }
        else if (item.getClass() == String.class) {
        	result.append("\"");
        	result.append(item.toString());
        	result.append("\"");
        }
        else {
        	result.append(item.toString());
        }
        result.append(",");
	}

	private void cutOfTrailingComma(StringBuffer string) {
        if (string.length() > 0) {
        	string.deleteCharAt(string.length()-1);
        }
    }

    /** Formats an operator for usage in SQL statements.
	 * @param operator Any of the operators in {@link WhereCondition.Operator}
	 * @param value The value to apply the operator to before it is formatted
	 * @return A valid SQL operator. By default the function just retuns the
	 *   passed operator as is. If the value is NULL it returns "IS" for
	 *   operator {@link WhereCondition.Operator#EQUAL} and "IS NOT" for 
	 *   operator {@link WhereCondition.Operator#UNEQUAL}.
	 */
	public static String standardOperator(String operator, Object value) {
		if (value == null) {
			if (operator.equals(WhereCondition.Operator.EQUAL))
				return "IS";
			if (operator.equals(WhereCondition.Operator.UNEQUAL))
				return "IS NOT";
		}
		return operator;
	}

	/** Formats an operator for usage in SQL statements.
	 * See {@link #standardOperator} for details
	 */
    public String formatOperator(String operator, Object value) {
        return standardOperator(operator, value);
    }

	/** Converts the passed value for usage in a prepared statement write access function
	 * The function currently just runs {@link #formatEnum} or {@link #castJavaUtilDate}
	 */
	public Object formatPreparedValue(Object value, Class<?> targetType) {
        if (value != null && value.getClass().isEnum())
            return formatEnum((Enum)value);
		return castJavaUtilDate(value, targetType);
	}

	@Override
	public Object unformatValue(Object dbValue, Class<?> targetType) throws SQLException {
		if (targetType.isEnum() && (dbValue instanceof String)) {
			return Enum.valueOf((Class<Enum>) targetType, (String) dbValue);
		}
		else if (dbValue instanceof java.sql.Array) {
			return sqlArray2javaArray((java.sql.Array) dbValue, targetType);
		}
        else if (dbValue instanceof SQLXML && targetType == String.class) {
        	return ((SQLXML)dbValue).getString();
        }
		return dbValue;
	}

	/**
	 * This method is required to also support Enums and primitive types as
	 * array elements Additionally it turned out that Postgres (the only
	 * database with array support we know) uses the String "NULL" to represent
	 * a NULL value in a String arrays.
	 */
	protected Object sqlArray2javaArray(java.sql.Array dbValue, Class<?> targetArrayType) throws SQLException {
		Object rawArray = dbValue.getArray();
		Class<?> targetComponentType = targetArrayType.getComponentType();
		if (targetComponentType.isPrimitive() || targetComponentType.isEnum()) {
			int arrayLength = Array.getLength(rawArray);
			Object unboxedArray = Array.newInstance(targetComponentType, arrayLength);
			for (int i = 0; i < arrayLength; i++) {
				Object rawItemValue = Array.get(rawArray, i);
				if (targetComponentType.isPrimitive()) {
					Array.set(unboxedArray, i, rawItemValue);
				} else {
					Object enumarizedItemValue = Enum.valueOf((Class<Enum>) targetComponentType,
							rawItemValue.toString());
					Array.set(unboxedArray, i, enumarizedItemValue);
				}
			}
			return unboxedArray;
		}
		return rawArray;
	}

	/** Returns the passed logical table name as physical name */
	public String getTableName(String logicalTableName) throws Exception {
		return logicalTableName;
	}

	protected void setAutoCommit(Connection con, boolean state) throws SQLException {
		con.setAutoCommit(state);
	}

    /**
     * The function initializes the auto key fetching mode of not yet done by
     * accessing the passed statement's meta data and calling supportsGetGeneratedKeys().
     * @return the passed list of fields if auto key mode is set to STANDARD or
     *   null otherwise. For MySQL, the function always returns null because MySQL 3
     *   supports getGeneratedKeys() but doesn't support passing the field list in
     *   insert execution (strange but true).
     */
    public String[] getAutoFields(Statement stmt, String[] rawAutoFields) {
        if (autoKeyMode == AutoKeyMode.UNKNOWN) {
            try {
                DatabaseMetaData meta = stmt.getConnection().getMetaData();
                autoKeyMode = (meta.supportsGetGeneratedKeys()) ?
                    AutoKeyMode.STANDARD : AutoKeyMode.VENDOR;
            }
            // Catch anything, even things like AbstractMethodError
            catch(Throwable t) {
                autoKeyMode = AutoKeyMode.VENDOR;
            }
        }
        return (DBType.MYSQL.equalsIgnoreCase(dbType) || autoKeyMode == AutoKeyMode.VENDOR) ?
            null : rawAutoFields;
    }
    
    /** Return the list of auto field values using JDBC's getGeneratedKeys()
     * function on the passed statement.
     */
    public ResultSet getAutoFieldVals(Statement stmt, String[] rawAutoFields)
        throws SQLException {
        if (autoKeyMode == AutoKeyMode.VENDOR) {
            if (DBType.MYSQL.equalsIgnoreCase(dbType))
                return stmt.executeQuery("SELECT LAST_INSERT_ID()");
            if (DBType.MARIADB.equalsIgnoreCase(dbType))
                return stmt.executeQuery("SELECT LAST_INSERT_ID()");
            if (DBType.SQLSERVER.equalsIgnoreCase(dbType))
                return stmt.executeQuery("SELECT @@IDENTITY");
            if (DBType.HSQL.equalsIgnoreCase(dbType))
                return stmt.executeQuery("CALL IDENTITY()");
            if (DBType.SQLITE.equalsIgnoreCase(dbType))
                return stmt.executeQuery("SELECT LAST_INSERT_ROWID()");
        }
        return stmt.getGeneratedKeys();
    }
    
	/**
	 * Create a new AbstractResourceAccessor according to the passed properties.
	 * See interface {@link ResourceAccessor.Config} for available configuration
	 * properties.
	 */
	public AbstractResourceAccessor(Properties props) throws Exception {
        this.props = props;
		if (props != null) {
			dbType = props.getProperty(Config.DBTYPE, null);
			String datef = props.getProperty(Config.DATEFORMAT);
			if (datef != null)
				dateFormat = new SimpleDateFormat(datef);
			String timef = props.getProperty(Config.TIMEFORMAT);
			if (timef != null)
				timeFormat = new SimpleDateFormat(timef);

			dbUser     = props.getProperty(Config.USER);
			dbPassword = props.getProperty(Config.PASSWORD);
			
			// set reference for comparing if databse sysdate/current_timestamp should be used
			dbSystime     = SYSTIME_DEFAULT;
			String millis = props.getProperty(Config.SYSTIME);

			if (millis != null && millis.length() > 0 && !"default".equals(millis)) { 
			  dbSystime = new java.util.Date(Long.parseLong(millis));
			}

			sqlLogFileName = props.getProperty(Config.LOGFILE);
			if (sqlLogFileName != null && sqlLogFileName.length() != 0)
				sqllog = new FileWriter(sqlLogFileName);
				
			String logMaxString = props.getProperty(Config.LOGMAX, "");
			try {
				logMax = Long.parseLong(logMaxString);
				if (logMax < 1)
					logMax = LOGMAX_DEFAULT;
			}
			catch(NumberFormatException nfx) { /* Do nothing, i.e. use the default */ }
			
			String bindvarsDefaultString = props.getProperty(Config.BINDVARS, "off");
			bindvarsDefault = bindvarsDefaultString.equals("on");
		}
	}
	

	public AbstractResourceAccessor() throws Exception { this(null); }

	/**
	 * Retrieves the timestamp, that is currently used to identify, that a date 
	 * has to be replaced by the database server's system time.
	 * If a date for an sql statement matches this value, it will
	 * be replaced by a database specific string in insert and update statements. 
	 * 
	 * @return the current reference date
	 */
  	public Date getSystime() { return dbSystime; }
  
	/**
	 * Retrieves the URL for this DBMS represented by the
	 * ResourceAccessor. 
	 * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/sql/DatabaseMetaData.html#getURL()">DatabaseMetaData</a>
	 * 
	 * @param db The logical database name
	 * @return the database url
	 */
  	public String getURL(String db) throws Exception {
  		return this.getConnection(db).getMetaData().getURL();
  	}
  
	/**
	 * Retrieves the user name as known to this database represented by the
	 * ResourceAccessor.
	 * @see <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/sql/DatabaseMetaData.html#getUserName()">DatabaseMetaData</a>
	 * 
	 * @param db The logical database name
	 * @return the user name
	 */
  	public String getUserName(String db) throws Exception {
  		return this.getConnection(db).getMetaData().getUserName();
  	}
    
    /**
     * Returns the type of database as specified by the constructor properties or
     * null if no type is specified at all.
     */
    public String getDBType() { return this.dbType; }

    /**
     * Returns true if SQL statements should be assembled via bind-variables by default
     * Without specifying and default, PriDE runs plain SQL.
     */
    public boolean bindvarsByDefault() { return this.bindvarsDefault; }

}
