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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A RecordDescriptor defines the mapping of relation database records to JAVA objects
 *
 * @author <a href="mailto:jan.lessner@acoreus.de">Jan Lessner</a>
 */
public class RecordDescriptor
{
    /** Defined constants for the ResultSet extraction mode of this descriptor
	 * and its depending {@link AttributeDescriptor}s.
	 */
	public static interface ExtractionMode {
		/** Automatic mode, making up the default for ResultSet access: The data
		 * extraction is performed by-name first and in case of an error repeated
		 * by-index
		 */
		public static final int AUTO = 0;

		/** ResultSet data is extracted by name */
		public static final int NAME = 1;

		/** ResultSet data is extracted by index */
		public static final int INDEX = 2;
	};

    protected Class<?> objectType;
    protected ObjectInstanciator objectInstanciator;
    protected String dbContext;
    protected String dbtable;
    protected String dbtableAlias;
    protected List<AttributeDescriptor> attrDescriptors;
    protected List<String> primaryKeyFields;
    protected String[] autoFields;
    protected int extractionMode;
    protected RecordDescriptor baseDescriptor;
    protected boolean withBind;

    /** Returns <code>true</code> if the passed array of strings contains <code>element</code>.
     * Parameter <tt>onNull</tt> is returned in case the passed string array is <tt>null</tt>.
     */
    protected static boolean contains(String[] list, String element, boolean onNull) {
        if (list == null)
            return onNull;
        for (int i = 0; i < list.length; i++) {
            if (list[i] == null) {
                if (element == null)
                    return true;
            }
            else
                if (list[i].equals(element))
                    return true;
        }
        return false;
    }

    /** Creates a new mapping descriptor
     * @param objectType JAVA object type being mapped by the descriptor
     * @param dbtable Database table to refer to
     * @param dbtableAlias an optional alias for the table name. This is especially of interest for descriptors of
     *   composite types from joins
     * @param attributeMap Description of field mappings used to instantiate {@link AttributeDescriptor}s. Each
     *   inner array is passed as parameter <code>attrInfo</code> to the {@link AttributeDescriptor#AttributeDescriptor
     *   constructor of AttributeDescriptor}.
     * @param extractionMode The ResultSet extraction mode according to the constants
     *   defined in interface {@link ExtractionMode}.
     */
    public RecordDescriptor(Class<?> objectType, String dbContext, String dbtable, String dbtableAlias,
			    RecordDescriptor baseDescriptor, String[][] attributeMap, int extractionMode)
		throws IllegalDescriptorException {
    	this(objectType, dbContext, dbtable, dbtableAlias, baseDescriptor, extractionMode);
        if (attributeMap != null) {
            for (String[] rawAttributeDesc: attributeMap) {
            	row(rawAttributeDesc);
            }
        }
    }

    public RecordDescriptor row(String[] rawAttributeDesc) {
    	AttributeDescriptor attributeDesc = new AttributeDescriptor(objectType, extractionMode, rawAttributeDesc);
    	attrDescriptors.add(attributeDesc);
    	return this;
	}

    public RecordDescriptor row(String dbfield, String getter, String setter) {
    	return row(new String[] { dbfield, getter, setter });
	}

    public RecordDescriptor rowPK(String dbfield, String getter, String setter) {
    	primaryKeyFields.add(dbfield);
    	return row(dbfield, getter, setter);
	}

    public RecordDescriptor keyFields(String... dbfields) {
    	primaryKeyFields.clear();
    	primaryKeyFields.addAll(Arrays.asList(dbfields));
    	return this;
    }
    
    public RecordDescriptor autoFields(String... dbfields) {
    	autoFields = dbfields;
    	return this;
    }
    
	public RecordDescriptor(Class<?> objectType, String dbContext, String dbtable, String dbtableAlias,
		    RecordDescriptor baseDescriptor, int extractionMode)
		throws IllegalDescriptorException {
	    this.objectType = objectType;
	    this.objectInstanciator = new ObjectInstanciator(objectType);
	    this.dbContext = dbContext;
	    this.dbtable = dbtable;
	    this.dbtableAlias = dbtableAlias;
	    this.baseDescriptor = baseDescriptor;
	    this.attrDescriptors = new ArrayList<>();
	    this.extractionMode = extractionMode;
	    this.primaryKeyFields = new ArrayList<>();
	    // Primary Key Fields is always the cumulated list from all descriptors along an inheritance hierarchy
	    if (baseDescriptor != null) {
	    	this.primaryKeyFields.addAll(Arrays.asList(baseDescriptor.getPrimaryKeyFields()));
	    	this.autoFields = baseDescriptor.getAutoFields();
	    }
	}

	/** Creates a new mapping descriptor like constructor above but always uses the current DB
	 * context of {@link DatabaseFactory} and auto extraction mode. Constructors with an
	 * attribute map as two-dimensional String array are deprecated. Use constructor
	 * {@link #RecordDescriptor(Class, String, RecordDescriptor)} instead, followed by
	 * calls of method {@link #row(String, String, String)} for all attributes.
     */
	@Deprecated
    public RecordDescriptor(Class<?> objectType, String dbtable,
		RecordDescriptor baseDescriptor, String[][] attributeMap)
		throws IllegalDescriptorException {
        this(objectType, null, dbtable, null, baseDescriptor, attributeMap, ExtractionMode.AUTO);
    }

    public RecordDescriptor(Class<?> objectType, String dbtable, RecordDescriptor baseDescriptor)
		throws IllegalDescriptorException {
        this(objectType, null, dbtable, null, baseDescriptor, null, ExtractionMode.AUTO);
    }

	/** Creates a new mapping descriptor like constructor above
	 * but always uses the current DB context of {@link DatabaseFactory}.
	 */
	public RecordDescriptor(Class<?> objectType, String dbtable, String dbtableAlias,
		RecordDescriptor baseDescriptor, int extractionMode)
		throws IllegalDescriptorException {
		this(objectType, null, dbtable, dbtableAlias, baseDescriptor, extractionMode);
	}

	public RecordDescriptor(Class<?> objectType, String dbContext, String dbtable, String dbtableAlias,
		RecordDescriptor baseDescriptor)
		throws IllegalDescriptorException {
		this(objectType, dbContext, dbtable, dbtableAlias, baseDescriptor, ExtractionMode.AUTO);
	}

    /**
     * Copy constructor
     * @param red The descriptor which to copy data from
     * @param alias An optional alias name to use for database access.
     *    This is of interest if this descriptor is supposed to be used
     *    in an aggregation of multiple descriptors for accessing full
     *    table joins. May be null;
     * @param altTable An optional alternative table name to use for database
     *    access. If both alias and table name are specified, the alias is
     *    not applied to the alternate table name.
     */
    public RecordDescriptor(RecordDescriptor red, String alias, String altTable) {
        objectType = red.objectType;
        dbContext = red.dbContext;
        dbtable = (altTable == null) ? red.dbtable : altTable;
        dbtableAlias = alias;
        
        if (alias != null) {
            if (altTable == null) // Don't apply alias on explicite alternate table name
                dbtable += " " + alias;
            if (red.baseDescriptor != null)
                // Don't pass the altTable to the base descriptor. It is of no use
                // there anyway and we want the alias to be definitely applied
                baseDescriptor = new RecordDescriptor(red.baseDescriptor, alias);
            else
                baseDescriptor = null;
        }
        else {
            baseDescriptor = red.baseDescriptor;
        }
        attrDescriptors = red.attrDescriptors;
    }

    /**
     * Returns the total number of attributes being mapped by this
     * descriptor and all of its base descriptors. This function is
     * useful to skip non-existent parts of an outer join.
     * @return The number of mapped attributes
     */
    public int totalAttributes() {
        return attrDescriptors.size() +
           ((baseDescriptor != null) ? baseDescriptor.totalAttributes() : 0);
    }
    
    /** Like copy constructor above but without alternate table name */
    public RecordDescriptor(RecordDescriptor red, String alias) { this(red, alias, null); }

    public String getTableName() { return dbtable; }
    public Class<?> getObjectType() { return objectType; }
    public String getContext() { return dbContext; }

    /** Get the names of the database fields making up the primary key. */
    public String[] getPrimaryKeyFields() {
    	return primaryKeyFields.toArray(new String[primaryKeyFields.size()]);
    }

	public String[] getAutoFields() { return autoFields; }

	public int record2object(String toplevelTableAlias, Object obj, ResultSet results,
		int position, AttributeDescriptor attrDesc)
		throws SQLException, ReflectiveOperationException {
		attrDesc.record2object(toplevelTableAlias, obj, results, position);
		return (position >= 0) ? position+1 : position;
	}
	
    /** Extract result values from a result set according to the field mappings
     * The extraction is by default performed in auto-mode. i.e. first by-name
     * if possible and by-index otherwise. This is the only reliable way in all
     * JDBC drivers to support not only access of single tables but also joins
     * and alias fields. Joins turned out to cause some JDBC drivers to provide
     * no field names at all in the ResultSet. Alias names for function-based
     * fields turned out not to be provided in a reliable order.<br>
     * The index-based extraction requires function {RecordDescriptor#getResultFields}
     * to provide the attributes in the same order they are traversed by this
     * function.
     * @param obj The objet where to transfer the data to
     * @param results The result set to extract the data from
     * @param position The start index for data extraction
     * @return The next index for subsequent extractions or -1 to force
     *   extraction by name (see class {@link AttributeDescriptor} for details).
     */
    public int record2object(Object obj, ResultSet results, int position)
            throws SQLException, ReflectiveOperationException {
    	return record2object(dbtableAlias, obj, results, position);
    }

    protected int record2object(String toplevelTableAlias, Object obj, ResultSet results, int position)
        throws SQLException, ReflectiveOperationException {
        if (baseDescriptor != null)
            position = baseDescriptor.record2object(toplevelTableAlias, obj, results, position);
        for (AttributeDescriptor attrDesc: attrDescriptors)
        	position = record2object(toplevelTableAlias, obj, results, position, attrDesc);
        return position;
    }

    /**
     * Function used to inject auto-generated field values after an insertion
     * operation. The function works similar as the one above except that extraction
     * from the result set is limited to a list of specified attributes. Extraction
     * is done by-position because some JDBC drivers don't support the standard JDBC
     * function Statement.getGeneratedKeys() but use strange retrieval selections
     * to fetch auto field values.
     * @param obj The objet where to transfer the data to
     * @param results The result set to extract the data from
     * @param includeAttrs An array of attribute names to consider
     */
    public int record2object(Object obj, ResultSet results, int position, String[] includeAttrs)
        throws SQLException, ReflectiveOperationException {
    	return record2object(dbtableAlias, obj, results, position, includeAttrs);
	}

    protected int record2object(String toplevelTableAlias, Object obj, ResultSet results, int position, String[] includeAttrs)
        throws SQLException, ReflectiveOperationException {
        if (baseDescriptor != null)
            position = baseDescriptor.record2object(toplevelTableAlias, obj, results, position, includeAttrs);
        for (AttributeDescriptor attrDesc: attrDescriptors) {
            if (contains(includeAttrs, attrDesc.getFieldName(), false))
                position = record2object(toplevelTableAlias, obj, results, position, attrDesc);
        }
        return position;
    }
    
    /** Get the passed object's primary key value, assuming that this is
     * the value of the very first attribute mapping.
     */
    public Object getPrimaryKey(Object obj) throws ReflectiveOperationException {
        return (baseDescriptor != null) ? baseDescriptor.getPrimaryKey(obj) :
        	attrDescriptors.get(0).getValue(obj);
    }

    /** Runs getWhereValue on the attribute descriptor, representing the
     * attribute specified by parameter <code>dbfield</code>
     */
    @Deprecated
    public Object getWhereValue(Object obj, String dbfield, boolean byLike, Database db)
		throws ReflectiveOperationException, SQLException {
        Object value = null;
        try {
            value = (baseDescriptor != null) ?
            baseDescriptor.getWhereValue(obj, dbfield, byLike, db) : null;
        } catch (IllegalAccessException e) {
            // Nothing to be done here. The field was not found in the base
            // descriptor but probably occurs in the current one
        }
        if (value != null)
            return value;
        for (AttributeDescriptor attrDesc: attrDescriptors) {
            if (attrDesc.matches(dbtableAlias, dbfield))
                return attrDesc.getWhereValue(obj, db, byLike);
        }
        throw new IllegalAccessException("Unknown field " + dbfield + " in where-clause");
    }

    public WhereFieldCondition assembleWhereValue(String toplevelTableAlias, Object obj, String dbfield, boolean byLike, Boolean withBind)
		throws ReflectiveOperationException {
        try {
        	if (baseDescriptor != null)
        		return baseDescriptor.assembleWhereValue(toplevelTableAlias, obj, dbfield, byLike, withBind);
        } catch (IllegalAccessException e) {
            // Nothing to be done here. The field was not found in the base
            // descriptor but probably occurs in the current one
        }
        for (AttributeDescriptor attrDesc: attrDescriptors) {
            if (attrDesc.matches(toplevelTableAlias, dbfield))
                return attrDesc.assembleWhereValue(obj, toplevelTableAlias, byLike, withBind);
        }
        throw new IllegalAccessException("Unknown field " + dbfield + " in where-clause");
    }

    protected String getTablePrefix() { return null; }

	/** Similar to function above but instead writes the value into a passed
     * {@link PreparedOperation}.
     * @param obj the value object from which to take the value from
     * @param dbfield the field to fetch the value for
     * @param pop the {@link PreparedOperation} to pass the value to
     * @param table the name of the database table, the operation refers to.
     *   See {@link AttributeDescriptor} for the nasty reason
     * @param position index of the prepared statements next parameter to set
     */
    public void getWhereValue(String toplevelTableAlias, Object obj, String dbfield, PreparedOperationI pop, String table, int position)
		throws ReflectiveOperationException, SQLException {
		if (table == null)
			table = dbtable;
        if (baseDescriptor != null) {
        	try {
        		baseDescriptor.getWhereValue(toplevelTableAlias, obj, dbfield, pop, table, position);
        		return;
            } catch (IllegalAccessException e) {
                // Nothing to be done here. The field was not found in the base
                // descriptor but probably occurs in the current one
            }
        }
        for (AttributeDescriptor attrDesc: attrDescriptors) {
            if (attrDesc.matches(toplevelTableAlias, dbfield)) {
            	attrDesc.getParameter(obj, pop, table, position);
                return;
            }
        }
        throw new IllegalAccessException("Unknown field " + dbfield + " in where-clause");
    }

    /** Constructs a constraint for a database query
     * @param dbfields the database fields for which to take a
     *  constraint value from the passed value object. If this is null,
     *  the first registered attribute is used.
     * @param obj the value object from which to take the constraint values
     * @param byLike if <code>false</code>, builds the constraint from equality
     *  checks, otherwise by using operator <code>like</code>
     */
    @Deprecated
    public String getConstraint(Object obj, String[] dbfields,
				boolean byLike, Database db)
		throws ReflectiveOperationException, SQLException {
        String constraint = "";
        if (dbfields == null)
            dbfields = getPrimaryKeyFields();
        for (int i = 0; i < dbfields.length; i++) {
            if (i > 0)
                constraint += " and ";
            constraint += getWhereValue(obj, dbfields[i], byLike, db);
        }
        return constraint;
    }

	public WhereCondition assembleWhereCondition(Object obj, String[] dbfields, boolean byLike)
		throws ReflectiveOperationException {
		WhereCondition condition = new WhereCondition();
        if (dbfields == null) {
            dbfields = getPrimaryKeyFields();
        }
        for (int i = 0; i < dbfields.length; i++) {
        	WhereFieldCondition fieldCondition = assembleWhereValue(dbtableAlias, obj, dbfields[i], byLike, condition.bind);
        	condition = condition.and(fieldCondition);
        }
		return condition;
	}
	
	/** Similar to function above but instead writes the values into a passed
     * {@link PreparedOperation}.
     * @param obj the value object from which to take the constraint values
     * @param dbfields the database fields for which to take a
     *  constraint value from the passed value object. If this is null,
     *  the first registered attribute is used.
     * @param pop the {@link PreparedOperation} to pass the value to
     * @param table the name of the database table, the operation refers to.
     *   See {@link AttributeDescriptor} for the nasty reason
     * @param position index of the prepared statements next parameter to set
     * @return the next pending position
     */
    public int getConstraint(Object obj, String[] dbfields,
                             PreparedOperationI pop, String table, int position)
		throws ReflectiveOperationException, SQLException {
		if (table == null)
			table = dbtable;
        if (dbfields == null)
            dbfields = getPrimaryKeyFields();
        for (int i = 0; i < dbfields.length; i++)
            getWhereValue(dbtableAlias, obj, dbfields[i], pop, table, position++);
        return position;
    }

    protected String trim(String value) {
        return (value.length() > 0 && value.charAt(0) == ',') ? value.substring(1) : value;
    }

    /** Returns a database update clause for the passed object's attribute. The very first
     * attribute is assumed to make up the primary key and is therefore left off
     */
    public String getUpdateValues(Object obj, Database db)
		throws ReflectiveOperationException, SQLException {
		return getUpdateValues(obj, null, null, db);
    }

    /** Like the function above except that all attributes listed in <code>excludeAttrs</code>
     * are left off instead of the first one.
     */
    public String getUpdateValues(Object obj, String[] excludeAttrs, String[] includeAttrs, Database db)
		throws ReflectiveOperationException, SQLException {
        String values = (baseDescriptor != null) ?
            baseDescriptor.getUpdateValues(obj, excludeAttrs, includeAttrs, db) : "";
        for (AttributeDescriptor attrDesc: attrDescriptors) {
            if (!contains(excludeAttrs, attrDesc.getFieldName(), false)
                && contains(includeAttrs, attrDesc.getFieldName(), true))
                values += "," + attrDesc.getUpdateValue(obj, db);
        }
        return trim(values);
    }

	
    /** Similar to function above but instead writes the values into a passed
     * {@link PreparedOperation}.
     * @param obj the value object from which to take the data from
     * @param excludeAttrs an array of field names which are to be ignored.
     * @param pop the {@link PreparedOperation} to pass the value to
     * @param table the name of the database table, the operation refers to.
     *   See {@link AttributeDescriptor} for the nasty reason
     * @param position index of the prepared statements next parameter to set
     * @return the next pending position
     */
    public int getUpdateValues(Object obj, String[] excludeAttrs, String[] includeAttrs,
                               PreparedOperationI pop, String table, int position)
		throws ReflectiveOperationException, SQLException {
		if (table == null)
			table = dbtable;
        if (baseDescriptor != null)
            position = baseDescriptor.getUpdateValues(obj, excludeAttrs, includeAttrs, pop, table, position);
        for (AttributeDescriptor attrDesc: attrDescriptors) {
            if (!contains(excludeAttrs, attrDesc.getFieldName(), false)
            	&& contains(includeAttrs, attrDesc.getFieldName(), true))
            	attrDesc.getParameter(obj, pop, table, position++);
        }
        return position;
    }

    /** Returns a comma-seperated list of all attribute values of the
     * passed object as required for an SQL insert operation
     */
    public String getCreationValues(Object obj, String[] excludeAttrs, Database db)
		throws ReflectiveOperationException, SQLException {
        String values = (baseDescriptor != null) ? baseDescriptor.getCreationValues(obj, excludeAttrs, db) : "";
        for (AttributeDescriptor attrDesc: attrDescriptors) {
			if (!contains(excludeAttrs, attrDesc.getFieldName(), false))
            	values += "," + attrDesc.getCreationValue(obj, db);
		}
        return trim(values);
    }

    /** Similar to function above but instead writes the values into a passed
     * {@link PreparedOperation}.
     * @param obj the value object from which to take the creation values
     * @param pop the {@link PreparedOperation} to pass the value to
     * @param table the name of the database table, the operation refers to.
     *   See {@link AttributeDescriptor} for the nasty reason
     * @param position index of the prepared statements next parameter to set
     * @return the next pending position
     */
    public int getCreationValues(Object obj, String[] excludeAttrs, PreparedOperation pop,
    							 String table, int position)
		throws ReflectiveOperationException, SQLException {
		if (table == null)
			table = dbtable;
        if (baseDescriptor != null)
            position = baseDescriptor.getCreationValues(obj, excludeAttrs, pop, table, position);
        for (AttributeDescriptor attrDesc: attrDescriptors) {
			if (!contains(excludeAttrs, attrDesc.getFieldName(), false))
				attrDesc.getParameter(obj, pop, table, position++);
		}
        return position;
    }

    /** Returns a comma-seperated list of all attribute names
     * as required for an SQL insert operation
     */
    public String getFieldNames(String[] excludeAttrs) {
        String names = (baseDescriptor != null) ? baseDescriptor.getFieldNames(excludeAttrs) : "";
        names += getFieldNames(dbtableAlias, attrDescriptors, excludeAttrs);
        return trim(names);
    }

    protected String getFieldNames(String alias, List<AttributeDescriptor> pAttrDescriptors, String[] excludeAttrs) {
        String names = "";
        for (AttributeDescriptor attrDesc: pAttrDescriptors) {
            if (!contains(excludeAttrs, attrDesc.getFieldName(), false)) {
                names += "," + getFieldName(alias, attrDesc);
            }
        }
        return names;
    }   

    protected String getFieldName(String alias, AttributeDescriptor attrdesc) {
    	return attrdesc.getFieldName(alias);
    }

    /** Returns the list of fields to be looked up in a query, which is by
     * default the list of all fields registered in this descriptor and
     * its base descriptors and thus the same list as returned by
     * {@link RecordDescriptor#getFieldNames}.
     * Overriding this function must be done with great care. The extraction
     * of data from a result set in {@link RecordDescriptor#record2object}
     * is performed <i>by index</i> and relies on the number and order of
     * fields defined by this function. If it returns '*' for example, make
     * shure to call {@link AttributeDescriptor#record2object} with index -1
     * to force data extraction by name.
     */
    public String getResultFields() { return getFieldNames(null); }

    /**
     * This method is of interest for embedding objects in others. In this case the attribute mapping
     * of a persistent type may also be used for a member of the same type in a composite, having
     * appropriate delegate setters and getters.
     */
    public String[][] getRawAttributeMap() {
        List<String[]> mappings = new ArrayList<String[]>();
        if (baseDescriptor != null)
            mappings.addAll(Arrays.asList(baseDescriptor.getRawAttributeMap()));
        for (AttributeDescriptor attrDesc: attrDescriptors) {
            mappings.add(attrDesc.getRawAttributeMap());
        }
        return mappings.toArray(new String[0][]);
    }

    public void calculateUpdateChecksum(Object obj) {
    }

    public boolean isWithBind() {
		return withBind;
	}

	public boolean isRevisioned() {
        return false;
    }

	public void setWithBind(boolean withBind) {
		this.withBind = withBind;
	}

	protected static String constantValue(String value) {
        return GetterSetterPair.CONSTANT_VALUE_PREFIX + value;
    }

	public final static String REVISION_ID = "$Header: /home/cvsroot/xbcsetup/source/packages/xbc/server/database/RecordDescriptor.java,v 1.9 2001/08/08 14:04:23 lessner Exp $";

}
