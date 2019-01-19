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

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    
	/** Fetch an object by fields. The values are taken from the fields' accociated get-methods */
	default ResultIterator queryByExample(String... dbfields) throws SQLException {
		return DatabaseAdapter.queryByExample(getDescriptor(), getEntity(), dbfields);
	}

	/** Same like <code>query()</code> but performs a wildcard search */
	default ResultIterator wildcard(String... dbfields) throws SQLException {
		return DatabaseAdapter.wildcard(getDescriptor(), getEntity(), dbfields);
	}

	/** Same like <code>query()</code> but takes the first record only */
	default boolean find() throws SQLException {
		String[] pk = getKeyFields();
		if (pk != null)
			return findByExample(pk);
		else
			return DatabaseAdapter.find(getEntity(), getDescriptor());
	}

	default <T> T findC(Class<T> t) throws SQLException {
		return (T)DatabaseAdapter.findC(getEntity(), getDescriptor());
	}
	
	/**
	 * Returns true, if there exists a record in the database mathing the entity's primary key.
	 */
	default boolean exists() throws SQLException {
		return DatabaseAdapter.exists(getEntity(), getDescriptor());
	}
	
    /** Like {@link #find()} but reports a missing match by a {@link FindException} rather than
     * a return value. This is of interest when ever finding no result is an unexpected situation.
     * In these cases {@link #findX()} keeps from cluttering the happy-path of the application logic with
     * if-statements for error handling.
     * <p>
     * The FindException is derived from SQLException, so there is usually no additional catch
     * block required for this exception.
     */
	default void findX() throws SQLException {
		if (!find()) {
			throw new FindException();
		}
	}
	
	/** Same like <code>query()</code> but takes the first record only */
	default boolean findByExample(String... dbkeyfields) throws SQLException {
		return DatabaseAdapter.findByExample(getEntity(), getDescriptor(), dbkeyfields);
	}

	/** Same like {@link #findByExample(String...)} but reports a missing match by a {@link FindException} rather than
     * a return value. Further details concerning the intention, see {@link #findX()}. */
	default void findByExampleX(String... dbkeyfields) throws SQLException {
		DatabaseAdapter.findByExampleX(getEntity(), getDescriptor(), dbkeyfields);
	}

	/** Same like <code>query()</code> but takes the first record only */
	default boolean find(String where, Object... params) throws SQLException {
		return DatabaseAdapter.find(getEntity(), getDescriptor(), where, params);
	}

	/** Same like {@link #find(String)} but reports a missing match by a {@link FindException} rather than
     * a return value. Further details concerning the intention, see {@link #findX()}. */
	default void findX(String where, Object... params) throws SQLException {
		DatabaseAdapter.findX(getEntity(), getDescriptor(), where, params);
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
		String[] primaryKeyFields = getKeyFields();
		return (primaryKeyFields != null) ?
			update(primaryKeyFields) :
			DatabaseAdapter.update(getEntity(), getDescriptor());
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

	default int create() throws SQLException { return create(getAutoFields()); }

	default int create(String... autoFields) throws SQLException {
		return DatabaseAdapter.create(getEntity(), getDescriptor(), autoFields);
	}

	default int delete() throws SQLException {
		String[] pk = getKeyFields();
		return (pk != null) ? deleteByExample(pk) : DatabaseAdapter.delete(getEntity(), getDescriptor());
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
	default String constraint() { return constraint(getKeyFields()); }

	default WhereCondition where(String[] dbfields, boolean byLike) {
		return DatabaseAdapter.assembleWhereCondition(getEntity(), dbfields, byLike, getDescriptor());
	}

	default WhereCondition where(String[] dbfields) {
		return where(dbfields, false);
	}

	/** Returns a {@link WhereCondition} made up from the primary key attributes
	 * of the entity return by {@link #getEntity()} */
	default WhereCondition where() { return where(getKeyFields()); }


	default void commit() throws SQLException {
		DatabaseAdapter.commit(getDescriptor());
	}

	default void process(Exception x) throws SQLException {
		DatabaseAdapter.process(x, getDescriptor());
	}
}
