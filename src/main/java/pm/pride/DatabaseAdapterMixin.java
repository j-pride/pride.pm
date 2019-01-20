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
 * {@link ObjectAdapter} which provides an entity for all mapping functions
 * and the {@link MappedObject} which maps itself.
 */
public interface DatabaseAdapterMixin {
	
	/** Returns the value object the adapter is operating on */
    Object getEntity();

	/** Returns the entity's record descriptor. This function is used by the
	 * the default database access functions below to provide simplified
	 * standard functionality. This function is abstract and must therefore
	 * be provided by every derived type.
	 */
	RecordDescriptor getDescriptor();

    default Database getDatabase(RecordDescriptor red) {
        return DatabaseFactory.getDatabase(red.getContext());
    }
    
	/** Fetch an object by fields. The values are taken from the fields' accociated get-methods */
	default ResultIterator queryByExample(String... dbfields) throws SQLException {
		return DatabaseAdapter.queryByExample(getDescriptor(), getEntity(), dbfields);
	}

	/** Same like <code>query()</code> but performs a wildcard search */
	default ResultIterator wildcard(String... dbfields) throws SQLException {
		return DatabaseAdapter.wildcard(getDescriptor(), getEntity(), dbfields);
	}

	/**
	 * Returns true, if there exists a record in the database mathing the entity's primary key.
	 */
	default boolean exists() throws SQLException {
		return DatabaseAdapter.exists(getEntity(), getDescriptor());
	}
	
	/** Same like <code>query()</code> but takes the first record only */
	default boolean find() throws SQLException {
		return DatabaseAdapter.find(getEntity(), getDescriptor());
	}

	default <T> T findRC(Class<T> t) throws SQLException {
		return (T)DatabaseAdapter.findRC(getEntity(), getDescriptor());
	}
	
    /** Like {@link #find()} but reports a missing match by a {@link FindException} rather than
     * a return value. This is of interest when ever finding no result is an unexpected situation.
     * In these cases {@link #findXE()} keeps from cluttering the happy-path of the application logic with
     * if-statements for error handling.
     * <p>
     * The FindException is derived from SQLException, so there is usually no additional catch
     * block required for this exception.
     */
	default void findXE() throws SQLException {
		if (!find()) {
			throw new FindException();
		}
	}
	
	/** Same like <code>query()</code> but takes the first record only */
	default boolean findByExample(String... dbkeyfields) throws SQLException {
		return DatabaseAdapter.findByExample(getEntity(), getDescriptor(), dbkeyfields);
	}

	/** Same like {@link #findByExample(String...)} but reports a missing match by a {@link FindException} rather than
     * a return value. Further details concerning the intention, see {@link #findXE()}. */
	default void findByExampleXE(String... dbkeyfields) throws SQLException {
		DatabaseAdapter.findByExampleXE(getEntity(), getDescriptor(), dbkeyfields);
	}

	/** Same like {@link #query(String, Object...)} but takes the first record only and returns
	 * false if there was no matching record found */
	default boolean find(String where, Object... params) throws SQLException {
		return DatabaseAdapter.find(getEntity(), getDescriptor(), where, params);
	}

	/** Same like {@link #query(WhereCondition)} but takes the first record only and returns
	 * false if there was no matching record found */
	default boolean find(WhereCondition where) throws SQLException {
		return DatabaseAdapter.find(getEntity(), getDescriptor(), where);
	}

	/** Same like {@link #find(String, Object...)} but reports a missing match by a {@link FindException} rather than
     * a return value. Further details concerning the intention, see {@link #findXE()}. */
	default void findXE(String where, Object... params) throws SQLException {
		DatabaseAdapter.findXE(getEntity(), getDescriptor(), where, params);
	}

	/** Fetch all objects */
	default ResultIterator queryAll() throws SQLException {
		return DatabaseAdapter.queryAll(getEntity(), getDescriptor());
	}

	/**
	 * Like {@link #queryAll()} but uses an alternative record descriptor. The descriptor must be
	 * applicable to this adapter's descriptor. The method is intended for queries which require
	 * an extended record descriptor, primarily a {@link JoinRecordDescriptor} for query conditions
	 * which span multiple related tables. The method throws an {@link IllegalArgumentException}
	 * if the passed descriptor is not compatible.
	 */
	default ResultIterator joinQueryAll(RecordDescriptor desc) throws SQLException {
		assertDescriptorCompatibility(desc);
		return DatabaseAdapter.queryAll(getEntity(), desc);
	}

	/** Fetch an object by a self-made where clause */
	default ResultIterator query(String where, Object... params) throws SQLException {
		return DatabaseAdapter.query(getEntity(), getDescriptor(), where, params);
	}

	/**
	 * Like {@link #query(String, Object...)} but uses an alternative record descriptor. The descriptor must be
	 * applicable to this adapter's descriptor. The method is intended for queries which require
	 * an extended record descriptor, primarily a {@link JoinRecordDescriptor} for query conditions
	 * which span multiple related tables. The method throws an {@link IllegalArgumentException}
	 * if the passed descriptor is not compatible.
	 */
	default ResultIterator joinQuery(RecordDescriptor desc, String where, Object... params) throws SQLException {
		assertDescriptorCompatibility(desc);
		return DatabaseAdapter.query(getEntity(), desc, where);
	}
	
	/** Fetch an object by a self-made where clause */
	default ResultIterator query(WhereCondition where) throws SQLException {
		return DatabaseAdapter.query(getEntity(), getDescriptor(), where);
	}

	/**
	 * Like {@link #query(WhereCondition)} but uses an alternative record descriptor. The descriptor must be
	 * applicable to this adapter's descriptor. The method is intended for queries which require
	 * an extended record descriptor, primarily a {@link JoinRecordDescriptor} for query conditions
	 * which span multiple related tables. The method throws an {@link IllegalArgumentException}
	 * if the passed descriptor is not compatible.
	 */
	default ResultIterator joinQuery(RecordDescriptor desc, WhereCondition where) throws SQLException {
		assertDescriptorCompatibility(desc);
		return DatabaseAdapter.query(getEntity(), desc, where);
	}

	default void assertDescriptorCompatibility(RecordDescriptor desc) {
		if (desc.getObjectType() != getDescriptor().getObjectType()) {
			throw new IllegalArgumentException("Descriptor for type " + desc.getObjectType() +
					" not applicable for a query for " + getDescriptor().getObjectType());
		}
	}
	
	default int update() throws SQLException {
		return DatabaseAdapter.update(getEntity(), getDescriptor());
	}

	default int update(String... dbkeyfields) throws SQLException {
		return DatabaseAdapter.update(getEntity(), getDescriptor(), dbkeyfields);
	}

	default int update(String[] dbkeyfields, String... updatefields) throws SQLException {
		return DatabaseAdapter.update(getEntity(), getDescriptor(), dbkeyfields, updatefields);
	}

	@Deprecated
	default int update(String where) throws SQLException {
		return DatabaseAdapter.update(getEntity(), getDescriptor(), where);
	}

	@Deprecated
	default int update(String where, String... updatefields) throws SQLException {
		return DatabaseAdapter.update(getEntity(), getDescriptor(), where, updatefields);
	}

	default int update(WhereCondition where) throws SQLException {
		return DatabaseAdapter.update(getEntity(), getDescriptor(), where);
	}

	default int update(WhereCondition where, String... updatefields) throws SQLException {
		return DatabaseAdapter.update(getEntity(), getDescriptor(), where, updatefields);
	}

	default int create() throws SQLException {
		return DatabaseAdapter.create(getEntity(), getDescriptor());
	}

	default int create(String... autoFields) throws SQLException {
		return DatabaseAdapter.create(getEntity(), getDescriptor(), autoFields);
	}

	default int delete() throws SQLException {
		return DatabaseAdapter.delete(getEntity(), getDescriptor());
	}

	default int deleteByExample(String... dbkeyfields) throws SQLException {
		return DatabaseAdapter.deleteByExample(getEntity(), getDescriptor(), dbkeyfields);
	}

	/** @deprecated Use {@link #where(String[], boolean)} instead */
	@Deprecated
	default String constraint(String[] dbfields, boolean byLike) {
		return DatabaseAdapter.constraint(getEntity(), dbfields, byLike, getDescriptor());
	}

	/** @deprecated Use {@link #where(String[])} instead */
	@Deprecated
	default String constraint(String[] dbfields) {
		return constraint(dbfields, false);
	}

	/** Returns a constraint made up from the primary key attributes
	 * of the entity return by {@link #getEntity()}
	 * @deprecated Use {@link #where()} instead */
	@Deprecated
	default String constraint() { return constraint(getDescriptor().getPrimaryKeyFields()); }

	default WhereCondition where(String[] dbfields, boolean byLike) {
		return DatabaseAdapter.assembleWhereCondition(getEntity(), dbfields, byLike, getDescriptor());
	}

	default WhereCondition where(String[] dbfields) {
		return where(dbfields, false);
	}

	/** Returns a {@link WhereCondition} made up from the primary key attributes
	 * of the entity return by {@link #getEntity()} */
	default WhereCondition where() { return where(getDescriptor().getPrimaryKeyFields()); }

	default void commit() throws SQLException {
		DatabaseAdapter.commit(getDescriptor());
	}

	default void process(Exception x) throws SQLException {
		DatabaseAdapter.process(x, getDescriptor());
	}
}
