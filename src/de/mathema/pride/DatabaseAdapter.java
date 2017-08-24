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

import java.sql.SQLException;

/**
 * Convenience baseclass, providing a set of simple methods for
 * interaction between the database and data entity objects. Most
 * of the functions get an object 'entity' passed as the first
 * parameter which is the data entity to extract data from for
 * database manipulation resp. to pass data to returned from
 * database queries.<br>
 * This class is of minor importance. The derived types {@link MappedObject}
 * or {@link ValueObjectAdapter} are usually sufficient and easier to use.
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
    protected boolean fetch(Object entity, Object key, RecordDescriptor red)
        throws SQLException {
        if (key != null) // null key indicates fetching is performed in derived type
            return getDatabase(red).fetchRecord(key, entity, red);
        return false;
    }

    /** Fetch an object by fields. The values are taken from the fields' accociated get-methods */
    protected static ResultIterator query(Object entity, String[] dbfields, RecordDescriptor red)
        throws SQLException {
        return (dbfields != null) ? // null fields indicates fetching is performed in derived type
            getDatabase(red).query(dbfields, entity, red, true) : null;
    }

    /** Same like <code>query()</code> but performs a wildcard search */
    protected static ResultIterator wildcard(Object entity, String[] dbfields, RecordDescriptor red)
        throws SQLException {
        return (dbfields != null) ? // null fields indicates fetching is performed in derived type
            getDatabase(red).wildcardSearch(dbfields, entity, red, true) : null;
    }

    /** Same like <code>query()</code> but takes the first record only. Returns false if no matching record could be found */
    protected static boolean find(Object entity, String[] dbkeyfields, RecordDescriptor red)
        throws SQLException {
    	return (dbkeyfields != null) ? false :
            getDatabase(red).query(dbkeyfields, entity, red, false) != null;
    }

    /** Same like <code>query()</code> but takes the first record only */
    protected static boolean find(Object entity, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).fetchRecord(entity, red);
    }

    /** Fetch all objects */
    protected static ResultIterator queryAll(Object entity, RecordDescriptor red)
      throws SQLException
    {
      return getDatabase(red).queryAll(entity, red);
    }

    /** Fetch an object by a self-made where clause */
    protected static ResultIterator query(Object entity, String where, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).query(where, entity, red, true);
    }

    /** Fetch an object by a self-made where clause */
    protected static ResultIterator query(Object entity, WhereCondition where, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).query(where, entity, red, true);
    }

    /** Same like <code>query()</code> but takes the first record only */
    protected static boolean find(Object entity, String where, RecordDescriptor red)
        throws SQLException {
        return (getDatabase(red).query(where, entity, red, false) != null);
    }

    protected static int update(Object entity, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).updateRecord(entity, red);
    }

    protected static int update(Object entity, String[] dbkeyfields, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).updateRecord(dbkeyfields, entity, red);
    }

	protected static int update(Object entity, String[] dbkeyfields, String[] updatefields, RecordDescriptor red)
		throws SQLException {
		return getDatabase(red).updateRecord(dbkeyfields, updatefields, entity, red);
	}

    @Deprecated
    protected static int update(Object entity, String where, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).updateRecord(where, entity, red);
    }

    @Deprecated
	protected static int update(Object entity, String where, String[] updatefields, RecordDescriptor red)
		throws SQLException {
		return getDatabase(red).updateRecord(where, updatefields, entity, red);
	}

    protected static int update(Object entity, WhereCondition where, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).updateRecord(where, entity, red);
    }

	protected static int update(Object entity, WhereCondition where, String[] updatefields, RecordDescriptor red)
		throws SQLException {
		return getDatabase(red).updateRecord(where, updatefields, entity, red);
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
		return getDatabase(red).createRecord(autoFields, entity, red);
	}

    protected static int delete(Object entity, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).deleteRecord(entity, red);
    }

    protected static int delete(Object entity, String[] dbkeyfields, RecordDescriptor red)
        throws SQLException {
        return getDatabase(red).deleteRecord(dbkeyfields, entity, red);
    }

    /** Assembles an SQL constraint which would be used as a WHERE clause in a query.
     * @param entity The object to take the attribute values from
     * @param dbfields The database fields to consider
     * @param byLike Use the <code>like</code> operator if set
     * @param red The {@link RecordDescriptor} providing the attribute mappings
     * @return The SQL constraint (without leading 'where')
     */
    protected String constraint(Object entity, String[] dbfields, boolean byLike, RecordDescriptor red) {
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


    public final static String REVISION_ID = "$Header:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/DatabaseAdapter.java-arc   1.0   Jun 05 2002 16:18:40   math19  $";
}

/* $Log:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/DatabaseAdapter.java-arc  $
 * 
 *    Rev 1.0   Jun 05 2002 16:18:40   math19
 * Initial revision.
 */