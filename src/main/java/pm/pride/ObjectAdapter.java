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
 * Convenience baseclass, providing a set of ready-to-use standard
 * methods for interaction between the database and data entity
 * objects. This is a derivation from DatabaseAdapter assuming that
 * derived types provide functions to access the type's
 * record descriptor, primary key definition and operation value object
 *
 * @author <a href="mailto:jan.lessner@mathema.de">Jan Lessner</a>
 */
abstract public class ObjectAdapter extends DatabaseAdapter
{
	/** Returns the entity's record desriptor. This function is used by the
	 * the public database access functions below to provide simplified
	 * standard functionality. This function is abstract and must therefore
	 * be provided by every derived type.
	 */
	protected abstract RecordDescriptor getDescriptor();

	/** Returns the primary key fields for this entity. This function
	 * returns <code>null</code> by default causing the <code>update()</code>
	 * and <code>delete()</code> functions below to use the first field in
	 * the record descriptor as primary key.
	 */
	public String[] getKeyFields() { return null; }
	
	/** Returns a list of fields being managed automatically by the database
	 * and therefore must not be passed in a record creation. This function
	 * returns <code>null</code> by default causing all fields of the record
	 * descriptor to be considered.
	 */
	public String[] getAutoFields() { return null; }

	/** Returns the value object the adapter is operating on */
	protected abstract Object getEntity();
    
    protected static Database getDatabase(RecordDescriptor red) {
        return DatabaseFactory.getDatabase(red.getContext());
    }
    
	/** Fetch an object by key. */
	public boolean fetch(Object key) throws SQLException {
		return super.fetch(getEntity(), key, getDescriptor());
	}

	/** Fetch an object by fields. The values are taken from the fields' accociated get-methods */
	public ResultIterator query(String[] dbfields) throws SQLException {
		return super.query(getEntity(), dbfields, getDescriptor());
	}

	/** Same like <code>query()</code> but performs a wildcard search */
	public ResultIterator wildcard(String[] dbfields) throws SQLException {
		return super.wildcard(getEntity(), dbfields, getDescriptor());
	}

	/** Same like <code>query()</code> but takes the first record only */
	public boolean find(String[] dbkeyfields) throws SQLException {
		return super.find(getEntity(), dbkeyfields, getDescriptor());
	}

	/** Same like <code>query()</code> but takes the first record only */
	public boolean find() throws SQLException {
		String[] pk = getKeyFields();
		if (pk != null)
			return find(pk);
		else
			return super.find(getEntity(), getDescriptor());
	}

	/** Fetch all objects */
	public ResultIterator queryAll() throws SQLException {
		return super.queryAll(getEntity(), getDescriptor());
	}

	/** Fetch an object by a self-made where clause */
	public ResultIterator query(String where) throws SQLException {
		return super.query(getEntity(), where, getDescriptor());
	}

	/** Fetch an object by a self-made where clause */
	public ResultIterator query(WhereCondition where) throws SQLException {
		return super.query(getEntity(), where, getDescriptor());
	}

	/** Same like <code>query()</code> but takes the first record only */
	public boolean find(String where) throws SQLException {
		return super.find(getEntity(), where, getDescriptor());
	}

	public int update() throws SQLException {
		String[] pk = getKeyFields();
		return (pk != null) ? update(pk) : super.update(getEntity(), getDescriptor());
	}

	public int update(String[] dbkeyfields) throws SQLException {
		return super.update(getEntity(), dbkeyfields, getDescriptor());
	}

	public int update(String[] dbkeyfields, String[] updatefields) throws SQLException {
		return super.update(getEntity(), dbkeyfields, updatefields, getDescriptor());
	}

	@Deprecated
	public int update(String where) throws SQLException {
		return super.update(getEntity(), where, getDescriptor());
	}

	@Deprecated
	public int update(String where, String[] updatefields) throws SQLException {
		return super.update(getEntity(), where, updatefields, getDescriptor());
	}

	public int update(WhereCondition where) throws SQLException {
		return super.update(getEntity(), where, getDescriptor());
	}

	public int update(WhereCondition where, String[] updatefields) throws SQLException {
		return super.update(getEntity(), where, updatefields, getDescriptor());
	}

	public int create() throws SQLException { return create(getAutoFields()); }

	public int create(String[] autoFields) throws SQLException {
		return super.create(getEntity(), autoFields, getDescriptor());
	}

	public int delete() throws SQLException {
		String[] pk = getKeyFields();
		return (pk != null) ? delete(pk) : super.delete(getEntity(), getDescriptor());
	}

	public int delete(String[] dbkeyfields) throws SQLException {
		return super.delete(getEntity(), dbkeyfields, getDescriptor());
	}

	public String constraint(String[] dbfields, boolean byLike) {
		return super.constraint(getEntity(), dbfields, byLike, getDescriptor());
	}

	public String constraint(String[] dbfields) {
		return constraint(dbfields, false);
	}

	/** Returns a constraint made up from the primary key attributes of this getEntity() */
	public String constraint() { return constraint(getKeyFields()); }

	public void commit() throws SQLException {
		commit(getDescriptor());
	}

	public void process(Exception x) throws SQLException {
		super.process(x, getDescriptor());
	}

	public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/database/DatabaseRecord.java,v 1.7 2001/07/24 11:47:05 lessner Exp $";
}

/* $Log:   //DEZIRWD6/PVCSArchives/dmd3000-components/framework/pride/src/de/mathema/pride/DatabaseAdapter.java-arc  $
 * 
 *    Rev 1.0   Jun 05 2002 16:18:40   math19
 * Initial revision.
 */
