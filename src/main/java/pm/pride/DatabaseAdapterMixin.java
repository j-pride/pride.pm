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

/** This is the base for two different flavors of {@link DatabaseAdapter}s. The
 * {@link ObjectAdapter} which provides an entity fpr alle mapping functions
 * and the {@link MappedObject} which maps itself.
 *
 * @param <E> The type of entity to be mapped
 */
public interface DatabaseAdapterMixin<E> {
	
	/** Returns the value object the adapter is operating on */
    E getEntity();

	/** Returns the entity's record descriptor. This function is used by the
	 * the default database access functions below to provide simplified
	 * standard functionality. This function is abstract and must therefore
	 * be provided by every derived type.
	 */
	RecordDescriptor getDescriptor();

	/** Returns the primary key fields for this entity. This function
	 * returns <code>null</code> by default causing the <code>update()</code>
	 * and <code>delete()</code> functions below to use the first field in
	 * the record descriptor as primary key.
	 */
	default String[] getKeyFields() { return null; }
	
	/** Returns a list of fields being managed automatically by the database
	 * and therefore must not be passed in a record creation. This function
	 * returns <code>null</code> by default causing all fields of the record
	 * descriptor to be considered.
	 */
	default String[] getAutoFields() { return null; }

    default Database getDatabase(RecordDescriptor red) {
        return DatabaseFactory.getDatabase(red.getContext());
    }
    
	/** Fetch an object by key. */
	default boolean fetch(Object key) throws SQLException {
		return DatabaseAdapter.fetch(getEntity(), key, getDescriptor());
	}

	/** Fetch an object by fields. The values are taken from the fields' accociated get-methods */
	default ResultIterator queryByExample(String... dbfields) throws SQLException {
		return DatabaseAdapter.query(getDescriptor(), getEntity(), dbfields);
	}

	/** Same like <code>query()</code> but performs a wildcard search */
	default ResultIterator wildcard(String... dbfields) throws SQLException {
		return DatabaseAdapter.wildcard(getDescriptor(), getEntity(), dbfields);
	}

	/** Same like <code>query()</code> but takes the first record only */
	default boolean find() throws SQLException {
		String[] pk = getKeyFields();
		if (pk != null)
			return find(pk);
		else
			return DatabaseAdapter.find(getEntity(), getDescriptor());
	}

    /** Like {@link #find()} but reports a missing match by a {@link FindException} rather than
     * a return value. This is of interest when ever finding no result is an unexpected situation.
     * In these cases findx() keeps from cluttering the happy-path of the application logic with
     * if-statements for error handling.
     * <p>
     * The FindException is derived from SQLException, so there is usually no additional catch
     * block required for this exception.
     */
	default void findx() throws SQLException {
		if (!find()) {
			throw new FindException();
		}
	}
	
	/** Same like <code>query()</code> but takes the first record only */
	default boolean find(String... dbkeyfields) throws SQLException {
		return DatabaseAdapter.find(getEntity(), dbkeyfields, getDescriptor());
	}

	/** Same like <code>find()</code> but reports a missing match by a {@link FindException} rather than
     * a return value. Further details concerning the intention, see {@link #findx()}. */
	default void findx(String... dbkeyfields) throws SQLException {
		DatabaseAdapter.findx(getEntity(), dbkeyfields, getDescriptor());
	}

	/** Same like <code>query()</code> but takes the first record only */
	default boolean find(String where) throws SQLException {
		return DatabaseAdapter.find(getEntity(), where, getDescriptor());
	}

	/** Same like <code>find()</code> but reports a missing match by a {@link FindException} rather than
     * a return value. Further details concerning the intention, see {@link #findx()}. */
	default void findx(String where) throws SQLException {
		DatabaseAdapter.findx(getEntity(), where, getDescriptor());
	}

	/** Fetch all objects */
	default ResultIterator queryAll() throws SQLException {
		return DatabaseAdapter.queryAll(getEntity(), getDescriptor());
	}

	/** Fetch an object by a self-made where clause */
	default ResultIterator query(String where) throws SQLException {
		return DatabaseAdapter.query(getEntity(), where, getDescriptor());
	}

	/** Fetch an object by a self-made where clause */
	default ResultIterator query(WhereCondition where) throws SQLException {
		return DatabaseAdapter.query(getEntity(), where, getDescriptor());
	}

	default int update() throws SQLException {
		String[] pk = getKeyFields();
		return (pk != null) ? update(pk) : DatabaseAdapter.update(getEntity(), getDescriptor());
	}

	default int update(String[] dbkeyfields) throws SQLException {
		return DatabaseAdapter.update(getEntity(), dbkeyfields, getDescriptor());
	}

	default int update(String[] dbkeyfields, String[] updatefields) throws SQLException {
		return DatabaseAdapter.update(getEntity(), dbkeyfields, updatefields, getDescriptor());
	}

	@Deprecated
	default int update(String where) throws SQLException {
		return DatabaseAdapter.update(getEntity(), where, getDescriptor());
	}

	@Deprecated
	default int update(String where, String[] updatefields) throws SQLException {
		return DatabaseAdapter.update(getEntity(), where, updatefields, getDescriptor());
	}

	default int update(WhereCondition where) throws SQLException {
		return DatabaseAdapter.update(getEntity(), where, getDescriptor());
	}

	default int update(WhereCondition where, String[] updatefields) throws SQLException {
		return DatabaseAdapter.update(getEntity(), where, updatefields, getDescriptor());
	}

	default int create() throws SQLException { return create(getAutoFields()); }

	default int create(String[] autoFields) throws SQLException {
		return DatabaseAdapter.create(getEntity(), autoFields, getDescriptor());
	}

	default int delete() throws SQLException {
		String[] pk = getKeyFields();
		return (pk != null) ? delete(pk) : DatabaseAdapter.delete(getEntity(), getDescriptor());
	}

	default int delete(String[] dbkeyfields) throws SQLException {
		return DatabaseAdapter.delete(getEntity(), dbkeyfields, getDescriptor());
	}

	default String constraint(String[] dbfields, boolean byLike) {
		return DatabaseAdapter.constraint(getEntity(), dbfields, byLike, getDescriptor());
	}

	default String constraint(String[] dbfields) {
		return constraint(dbfields, false);
	}

	/** Returns a constraint made up from the primary key attributes of this getEntity() */
	default String constraint() { return constraint(getKeyFields()); }

	default void commit() throws SQLException {
		DatabaseAdapter.commit(getDescriptor());
	}

	default void process(Exception x) throws SQLException {
		DatabaseAdapter.process(x, getDescriptor());
	}
}
