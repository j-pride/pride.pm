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
 * Convenience baseclass, providing a set of simple methods for
 * interaction between the database and data entity objects. Most
 * of the functions get an object 'entity' passed as the first
 * parameter which is the data entity to extract data from for
 * database manipulation resp. to pass data to returned from
 * database queries.<br>
 * This class is of minor importance. The derived types {@link MappedObject}
 * or {@link ObjectAdapter} are usually sufficient and easier to use.
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
abstract public class DatabaseAdapter
{
    protected static Database getDatabase(RecordDescriptor red) {
        return DatabaseFactory.getDatabase(red.getContext());
    }
    
    protected static Database getDatabase(ExtensionDescriptor xd) {
        return DatabaseFactory.getDatabase(xd.getContext());
    }
    
    /** Fetch an object by key. */
    protected static boolean fetch(Object entity, Object key, RecordDescriptor red)
        throws SQLException {
        if (key != null) // null key indicates fetching is performed in derived type
            return getDatabase(red).fetchRecord(red, entity, key);
        return false;
    }

    /** Fetch objects by fields. The values are taken from the fields' associated get-methods */
    protected static ResultIterator query(RecordDescriptor red, Object entity, String... dbfields)
        throws SQLException {
        return (dbfields != null) ? // null fields indicates fetching is performed in derived type
            getDatabase(red).query(red, true, entity, dbfields) : null;
    }

    /** Same like <code>query()</code> but performs a wildcard search */
    protected static ResultIterator wildcard(RecordDescriptor red, Object entity, String... dbfields)
        throws SQLException {
        return (dbfields != null) ? // null fields indicates fetching is performed in derived type
            getDatabase(red).wildcardSearch(red, true, entity, dbfields) : null;
    }

    /** Same like {@link #query(RecordDescriptor, Object, String...)} but takes the first record only.
     * Returns false if no matching record could be found. */
    protected static boolean find(Object entity, String[] dbkeyfields, RecordDescriptor red)
        throws SQLException {
    	return (dbkeyfields != null) ?
            getDatabase(red).query(red, false, entity, dbkeyfields) != null : false;
    }

    /** Like {@link #find(Object, String[], RecordDescriptor)} but reports a missing match by
     * a {@link FindException} rather than a return value. This is of interest when ever finding
     * no result is an unexpected situation. In these cases findx() keeps from cluttering the
     * happy-path of the application logic with if-statements for error handling.
     * <p>
     * The FindException is derived from SQLException, so there is usually no additionally catch
     * block required for this exception.
     */
    protected static void findx(Object entity, String[] dbkeyfields, RecordDescriptor red)
        throws SQLException {
    	if (!find(entity, dbkeyfields, red)) {
    		throw new FindException();
    	}
    }

    /** Find an object by it's primary key fields. Returns false if no matching record could be found. */
    protected static boolean find(Object entity, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).fetchRecord(red, entity);
    }

    /** Like {@link #find(Object, RecordDescriptor)} but reports a missing match by
     * a {@link FindException} rather than a return value. Further details, see
     * {@link #findx(Object, String[], RecordDescriptor)}.
     */
    protected static void findx(Object entity, RecordDescriptor red)
        throws SQLException {
    	if (!find(entity, red)) {
    		throw new FindException();
    	}
    }

    /** Same like {@link #query(Object, WhereCondition, RecordDescriptor)} but takes the first record only.
     * Returns false if no matching record could be found */
    protected static boolean find(Object entity, String where, RecordDescriptor red)
        throws SQLException {
        return (getDatabase(red).query(red, false, entity, where) != null);
    }

    /** Like {@link #find(Object, String, RecordDescriptor)} but reports a missing match by
     * a {@link FindException} rather than a return value. Further details, see
     * {@link #findx(Object, String[], RecordDescriptor)}.
     */
    protected static void findx(Object entity, String where, RecordDescriptor red)
        throws SQLException {
    	if (!find(entity, where, red)) {
    		throw new FindException();
    	}
    }

    /** Fetch all objects */
    protected static ResultIterator queryAll(Object entity, RecordDescriptor red)
      throws SQLException {
      return getDatabase(red).queryAll(red, entity);
    }

    /** Fetch an object by a self-made where clause */
    protected static ResultIterator query(Object entity, String where, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).query(red, true, entity, where);
    }

    /** Fetch an object by a self-made where clause */
    protected static ResultIterator query(Object entity, WhereCondition where, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).query(red, true, entity, where);
    }

    protected static int update(Object entity, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).updateRecord(red, entity);
    }

    protected static int update(Object entity, String[] dbkeyfields, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).updateRecord(red, entity, dbkeyfields);
    }

	protected static int update(Object entity, String[] dbkeyfields, String[] updatefields, RecordDescriptor red)
		throws SQLException {
		return getDatabase(red).updateRecord(red, entity, dbkeyfields, updatefields);
	}

    @Deprecated
    protected static int update(Object entity, String where, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).updateRecord(red, entity, where);
    }

    @Deprecated
	protected static int update(Object entity, String where, String[] updatefields, RecordDescriptor red)
		throws SQLException {
		return getDatabase(red).updateRecord(red, entity, where, updatefields);
	}

    protected static int update(Object entity, WhereCondition where, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).updateRecord(red, entity, where);
    }

	protected static int update(Object entity, WhereCondition where, String[] updatefields, RecordDescriptor red)
		throws SQLException {
		return getDatabase(red).updateRecord(red, entity, where, updatefields);
	}

	/** Create a new record. The values are taken from the passed object, according to the specifications
	 * of the provided {@link RecordDescriptor}
	 */
    protected static int create(Object entity, RecordDescriptor red)
        throws SQLException {
        return create(entity, null, red);
    }

	/** Like function above but additionally allows to pass a set of fields which should be excluded
	 *  from the SQL insert command. This functon is required if the table containes any database-managed
	 *  columns which must not be provided on creation (usually sequence columns). 
	 */
	protected static int create(Object entity, String[] autoFields, RecordDescriptor red)
		throws SQLException {
		return getDatabase(red).createRecord(red, entity, autoFields);
	}

    protected static int delete(Object entity, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).deleteRecord(red, entity);
    }

    protected static int delete(Object entity, String[] dbkeyfields, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).deleteRecord(red, entity, dbkeyfields);
    }

    /** Assembles an SQL constraint which would be used as a WHERE clause in a query.
     * @param entity The object to take the attribute values from
     * @param dbfields The database fields to consider
     * @param byLike Use the <code>like</code> operator if set
     * @param red The {@link RecordDescriptor} providing the attribute mappings
     * @return The SQL constraint (without leading 'where')
     */
    protected static String constraint(Object entity, String[] dbfields, boolean byLike, RecordDescriptor red) {
        try {
		    Database db = getDatabase(red);
		    return red.getConstraint(entity, dbfields, byLike, db);
		}
        catch(Exception x) {
            getDatabase(red).processSevereException(x);
            return null;
        }
    }

    protected static void commit(RecordDescriptor red) throws SQLException {
        getDatabase(red).commit();
    }

    protected static void process(Exception x, RecordDescriptor red) throws SQLException {
        getDatabase(red).processSevereButSQLException(x);
    }

}
