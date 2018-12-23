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
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * Helper class to iterate through a list of results from a query.
 * ResultIterators are initialized by the {@link Database} class
 * and provide their results step-by-step in the same entity
 * object. The first query result is initially put into entity.
 * If a query does not find any results at all, it returns null.
 * A typical usage looks like this:
 * <pre>
 *       Person p = new Person();
 *       ResultIterator iter = p.queryAll();
 *       if (iter != null) {
 *         do {
 *           System.println(p.getName());
 *         } while(iter.next());
 *       }
 * </pre>
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
public class ResultIterator
{
    public final static int COLUMN_STARTINDEX = 1;

    protected Statement statement;
    protected boolean customStatement;
    protected Connection connection;
    protected ResultSet results;
    protected Object obj;
    protected RecordDescriptor red;
    protected Database db;
    protected Method cloneMethod;
    protected SpoolState spoolState;

    /** Creates a new ResultIterator from an query. */
    public ResultIterator(Statement statement, boolean customStatement, ResultSet rs,
		Object obj, RecordDescriptor red,
		Database db, Connection con) {
        results = rs;
        this.statement = statement;
        this.obj = obj;
        this.red = red;
		this.db = db;
		this.connection = con;
		this.spoolState = SpoolState.ReadyToSpool;
    }

    public ResultIterator(Statement statement, boolean customStatement, ResultSet rs, Database db, Connection con) {
        this(statement, customStatement, rs, null, null, db, con);
    }

    /** Returns the result set, the iterator is operating on. This may be
     * required to pass query results to standard reporting engines etc.
     */
    public ResultSet getResultSet() { return results; }

    /** Closes the iteration by closing the result set, the statement and the database connection
     * passed in the constructor. This function is only required when aborting an iteration.
     * If the complete result set gets iterated, the iterator is closed automatically.
     * <p>
     * If the ResultIterator is based on a prepared statement, the statement will not be closed
     * assuming that it may be reused.
     */
    public void close() throws SQLException {
		if (results != null) { // Keep from multiply closing operations on database resources
		    results.close();
		    if (customStatement) {
		        ((PreparedStatement)statement).clearParameters();
		    }
		    else {
	            statement.close();
		    }
		    db.releaseConnection(connection);
		    results = null;
		}
    }

    /** Returns true if this result iterator has been closed. */
    public boolean isClosed() { return results == null; }
    
    /** Fetches the next record from the result set and stores it
     * in the entity object passed in the constructor. If there is
     * no further data available, the iterator is closed.
     * @return false, if there is no more data available.
     * @throws java.sql.SQLException if accessing the query results fails
     * or if the iterator is already closed
     */
    public boolean next() throws SQLException {
        if (isClosed())
            throw new SQLException("Result iterator closed");
        boolean b = results.next();
        if (b) {
            if (red != null) {
				try {
					red.record2object(obj, results, COLUMN_STARTINDEX);
					red.calculateUpdateChecksum(obj);
				}
				catch(Exception x) { db.processSevereButSQLException(x); }
	    	}
        }
        else
            close();
        return b;
    }

    /** Returns the object, the ResultIterator writes its data to */
    public Object getObject() { return obj; }
    
    /** Returns the object, the ResultIterator writes its data to */
    public <T> T getObject(Class<T> t) { return (T)obj; }
    
    /**
     * Iterates through the complete result set and returns the data in an ArrayList.
     * The list contains clones of the iterator's operation object for each iteration
     * step.
     * @param maxResults The maximum number of results to collect. If maxResults is -1,
     *   the function returns all data from the underlying result set
     * @return The assembled ArrayList
     * @throws SQLException if there occurs a databaes error during iteration
     */
	public List<?> toList(long maxResults) throws SQLException {
		try {
			SpoolCondition<Object> counter = new SpoolCondition<Object>() {
				int count = 0;
				@Override
				public boolean spool(Object entity) throws SQLException {
					return maxResults == -1 || ++count <= maxResults;
				}
			};
			return spoolToList(Object.class, counter);
		}
		finally {
			close();
		}
	}

	/**
	 * Like the function above but returns all data without size limitation. This function
	 * must be used with great care since the number of elements in a result set can
	 * be very large.
	 */
	public List<?> toList() throws SQLException { return toList(-1); }
	
	public <T> List<T> toList(Class<T> t, long maxResults) throws SQLException {
		return (List<T>)toList(maxResults);
	}

	public <T> List<T> toList(Class<T> t) throws SQLException {
		return (List<T>)toList();
	}

	public <T> List<T> spoolToList(Class<T> t, SpoolCondition<T> spoolCondition) throws SQLException {
		if (obj == null)
			throw new UnsupportedOperationException("Target object missing");
		try {
			ArrayList<Object> list = new ArrayList<Object>();
			if (cloneMethod == null)
				cloneMethod = obj.getClass().getMethod("clone", null);
			switch(spoolState) {
			case Finished:
				return null;
			case PendingLastResultAfterClose:
				if (spoolCondition.spool((T)obj)) {
					list.add(obj);
					spoolState = SpoolState.Finished;
				}
				break;
			default:
				boolean lastResultSpooled = false;
				do {
					lastResultSpooled = spoolCondition.spool((T)obj);
					if (lastResultSpooled)
						list.add(cloneMethod.invoke(obj, null));
					else
						break;
				}
				while(next());
				if (isClosed())
					spoolState = lastResultSpooled ? SpoolState.Finished : SpoolState.PendingLastResultAfterClose;
				if (spoolState == SpoolState.Finished && list.size() == 0)
					return null;
			}
			return (List<T>)list;
		}
		catch(InvocationTargetException itx) {
			throw db.processSevereButSQLException((Exception)itx.getTargetException());
		}
		catch(Exception x) {
			throw db.processSevereButSQLException(x);
		}
	}

	/**
	 * Like {@link #spoolToList} but returns the data as an array.
	 */
	public <T> T[] spoolToArray(Class<T> t, SpoolCondition<T> spoolCondition) throws SQLException {
		List<T> list = spoolToList(t, spoolCondition);
		if(list == null) return null;
		T[] result = (T[])Array.newInstance(obj.getClass(), list.size());
		return list.toArray(result);
	}

	public static interface SpoolCondition<T> {
		boolean spool(T entity) throws SQLException;
	}
	
	private enum SpoolState { ReadyToSpool, PendingLastResultAfterClose, Finished }
	
	/**
	 * Like {@link #toList} but returns the data as an array. The runtime type
	 * of the array's elements is the one of the iterator's operation object.
	 */
	public Object[] toArray(long maxResults) throws SQLException {
		if (obj == null)
			throw new UnsupportedOperationException("Target object missing");
		List<?> list = toList(maxResults);
		Object[] result = (Object[])Array.newInstance(obj.getClass(), list.size());
		return list.toArray(result);
	}

	/**
	 * Like the function above but returns all data without limitation. This function
	 * must be used with great care since the number of elements in a result set can
	 * be very large.
	 */
	public Object[] toArray() throws SQLException { return toArray(-1); }
	
	public <T> T[] toArray(Class<T> t, long maxResults) throws SQLException { return (T[])toArray(maxResults); }
	
	public <T> T[] toArray(Class<T> t) throws SQLException { return (T[])toArray(); }

	/** Returns the current fetch size for the underlying ResultSet */
	public int getFetchSize() throws SQLException { return results.getFetchSize(); }

	/** Sets the fetch size for the underlying ResultSet */
	public void setFetchSize(int size) throws SQLException { results.setFetchSize(size); }


    public int getInt(int index) throws SQLException { return results.getInt(index); }
    public BigDecimal getBigDecimal(int index) throws SQLException {
        return results.getBigDecimal(index);
    }
    public BigDecimal getBigDecimal(String column) throws SQLException {
        return results.getBigDecimal(column);
    }
    public Date getDate(int index) throws SQLException {
        return results.getDate(index);
    }
	public String getString(int index) throws SQLException {
        return results.getString(index);
	}

}

/* $Log:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/ResultIterator.java-arc  $
 * 
 *    Rev 1.1   02 Oct 2002 11:16:56   math19
 * New method isClosed() added.
 * 
 *    Rev 1.0   Jun 05 2002 16:18:44   math19
 * Initial revision.
/* Revision 1.7  2001/08/08 14:04:23  lessner
/* *** empty log message ***
/*
/* Revision 1.6  2001/07/24 14:33:50  haag
/* getDate()
/*
/* Revision 1.5  2001/07/18 08:19:41  lessner
/* *** empty log message ***
/*
/* Revision 1.4  2001/07/18 08:18:13  lessner
/* BigDecimal support added.
/*
/* Revision 1.3  2001/07/13 08:09:13  lessner
/* Minor improvements
/*
/* Revision 1.2  2001/07/13 07:57:28  lessner
/* Flexibelized for usage without object-depending iteration. ResultSet access functions like getInt() must successively be added on request.
/*
/* Revision 1.1  2001/06/25 07:50:07  lessner
/* Database framework extended by support for generic attributes
/*
 */
