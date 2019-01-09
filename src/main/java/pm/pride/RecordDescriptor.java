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
    protected String dbContext;
    protected String dbtable;
    protected AttributeDescriptor[] attrDescriptors;
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
     * @param attributeMap Description of field mappings used to instantiate
     *   {@link AttributeDescriptor}s. Each inner array is passed as parameter
     *   <code>attrInfo</code> to the {@link AttributeDescriptor#AttributeDescriptor
     *   constructor of AttributeDescriptor}.
     * @param extractionMode The ResultSet extraction mode according to the constants
     *   defined in interface {@link ExtractionMode}.
     */
    public RecordDescriptor(Class objectType, String dbContext, String dbtable,
			    RecordDescriptor baseDescriptor, String[][] attributeMap, int extractionMode)
		throws IllegalDescriptorException {
        this.objectType = objectType;
        this.dbContext = dbContext;
        this.dbtable = dbtable;
        this.baseDescriptor = baseDescriptor;
        if (attributeMap != null) {
            attrDescriptors = new AttributeDescriptor[attributeMap.length];
            for (int i = 0; i < attributeMap.length; i++)
                attrDescriptors[i] = new AttributeDescriptor(objectType, attributeMap[i], extractionMode);
        }
        else
            attrDescriptors = new AttributeDescriptor[0];
    }

    /** Creates a new mapping descriptor like constructor above
     * but always uses the current DB context of {@link DatabaseFactory}
     * and auto extraction mode.
     */
    public RecordDescriptor(Class<?> objectType, String dbtable,
		RecordDescriptor baseDescriptor, String[][] attributeMap)
		throws IllegalDescriptorException {
        this(objectType, null, dbtable, baseDescriptor, attributeMap);
    }

	/** Creates a new mapping descriptor like constructor above
	 * but always uses the current DB context of {@link DatabaseFactory}.
	 */
	public RecordDescriptor(Class<?> objectType, String dbtable,
		RecordDescriptor baseDescriptor, String[][] attributeMap, int extractionMode)
		throws IllegalDescriptorException {
		this(objectType, null, dbtable, baseDescriptor, attributeMap, extractionMode);
	}

	public RecordDescriptor(Class<?> objectType, String dbContext, String dbtable,
		RecordDescriptor baseDescriptor, String[][] attributeMap)
		throws IllegalDescriptorException {
		this(objectType, dbContext, dbtable, baseDescriptor, attributeMap, ExtractionMode.AUTO);
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
        
        if (alias != null) {
            if (altTable == null) // Don't apply alias on explicite alternate table name
                dbtable += " " + alias;
            if (red.baseDescriptor != null)
                // Don't pass the altTable to the base descriptor. It is of no use
                // there anyway and we want the alias to be definitely applied
                baseDescriptor = new RecordDescriptor(red.baseDescriptor, alias);
            else
                baseDescriptor = null;
            attrDescriptors = new AttributeDescriptor[red.attrDescriptors.length];
            for (int a = 0; a < attrDescriptors.length; a++)
                attrDescriptors[a] = new AttributeDescriptor(red.attrDescriptors[a], alias);
        }
        else {
            baseDescriptor = red.baseDescriptor;
            attrDescriptors = red.attrDescriptors;
        }
    }

    /**
     * Returns the total number of attributes being mapped by this
     * descriptor and all of its base descriptors. This function is
     * useful to skip non-existent parts of an outer join.
     * @return The number of mapped attributes
     */
    public int totalAttributes() {
        return attrDescriptors.length +
           ((baseDescriptor != null) ? baseDescriptor.totalAttributes() : 0);
    }
    
    /** Like copy constructor above but without alternate table name */
    public RecordDescriptor(RecordDescriptor red, String alias) { this(red, alias, null); }

    public String getTableName() { return dbtable; }
    public Class getObjectType() { return objectType; }
    public String getContext() { return dbContext; }

    /** Get the name of the database field making up the primary key.
     * Currently just returns the first attribute mapping's field name, so make
     * shure to always put the description of the primary key field at the
     * very beginning of the mappings passed in the constructor.
     */
    @Deprecated
    public String getPrimaryKeyField() {
        return (baseDescriptor != null) ? baseDescriptor.getPrimaryKeyField() :
            attrDescriptors[0].getFieldName();
    }

    public String[] getPrimaryKeyFields() {
        return (baseDescriptor != null) ? baseDescriptor.getPrimaryKeyFields() :
            new String[]{attrDescriptors[0].getFieldName()};
    }

	public int record2object(Object obj, ResultSet results,
		int position, AttributeDescriptor attrDesc)
		throws SQLException, ReflectiveOperationException {
		attrDesc.record2object(obj, results, position);
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
        if (baseDescriptor != null)
            position = baseDescriptor.record2object(obj, results, position);
        for (int i = 0; i < attrDescriptors.length; i++)
        	position = record2object(obj, results, position, attrDescriptors[i]);
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
        if (baseDescriptor != null)
            position = baseDescriptor.record2object(obj, results, position, includeAttrs);
        for (int i = 0; i < attrDescriptors.length; i++)
            if (contains(includeAttrs, attrDescriptors[i].getFieldName(), false))
                position = record2object(obj, results, position, attrDescriptors[i]);
        return position;
    }
    
    /** Get the passed object's primary key value, assuming that this is
     * the value of the very first attribute mapping.
     */
    public Object getPrimaryKey(Object obj) throws ReflectiveOperationException {
        return (baseDescriptor != null) ? baseDescriptor.getPrimaryKey(obj) :
            attrDescriptors[0].getValue(obj);
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
        for (int i = 0; i < attrDescriptors.length; i++) {
            if (attrDescriptors[i].getFieldName().equals(dbfield))
                return attrDescriptors[i].getWhereValue(obj, db, byLike);
        }
        throw new IllegalAccessException("Unknown field " + dbfield + " in where-clause");
    }

    public WhereFieldCondition assembleWhereValue(Object obj, String dbfield, boolean byLike, Boolean withBind)
		throws ReflectiveOperationException {
        try {
        	if (baseDescriptor != null)
        		return baseDescriptor.assembleWhereValue(obj, dbfield, byLike, withBind);
        } catch (IllegalAccessException e) {
            // Nothing to be done here. The field was not found in the base
            // descriptor but probably occurs in the current one
        }
        String tablePrefix = getTablePrefix();
        for (int i = 0; i < attrDescriptors.length; i++) {
            if (attrDescriptors[i].getFieldName().equals(dbfield))
                return attrDescriptors[i].assembleWhereValue(obj, tablePrefix, byLike, withBind);
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
    public void getWhereValue(Object obj, String dbfield, PreparedOperationI pop, String table, int position)
		throws ReflectiveOperationException, SQLException {
		if (table == null)
			table = dbtable;
        if (baseDescriptor != null) {
        	try {
        		baseDescriptor.getWhereValue(obj, dbfield, pop, table, position);
        		return;
            } catch (IllegalAccessException e) {
                // Nothing to be done here. The field was not found in the base
                // descriptor but probably occurs in the current one
            }
        }
        for (int i = 0; i < attrDescriptors.length; i++) {
            if (attrDescriptors[i].getFieldName().equals(dbfield)) {
                attrDescriptors[i].getParameter(obj, pop, table, position);
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
        	WhereFieldCondition fieldCondition = assembleWhereValue(obj, dbfields[i], byLike, condition.bind);
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
            dbfields = new String[] { getPrimaryKeyField() };
        for (int i = 0; i < dbfields.length; i++)
            getWhereValue(obj, dbfields[i], pop, table, position++);
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
        for (int i = 0; i < attrDescriptors.length; i++) {
            if (!contains(excludeAttrs, attrDescriptors[i].getFieldName(), false)
                && contains(includeAttrs, attrDescriptors[i].getFieldName(), true))
                values += "," + attrDescriptors[i].getUpdateValue(obj, db);
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
        for (int i = 0; i < attrDescriptors.length; i++) {
            if (!contains(excludeAttrs, attrDescriptors[i].getFieldName(), false)
            	&& contains(includeAttrs, attrDescriptors[i].getFieldName(), true))
                attrDescriptors[i].getParameter(obj, pop, table, position++);
        }
        return position;
    }

    /** Returns a comma-seperated list of all attribute values of the
     * passed object as required for an SQL insert operation
     */
    public String getCreationValues(Object obj, String[] excludeAttrs, Database db)
		throws ReflectiveOperationException, SQLException {
        String values = (baseDescriptor != null) ? baseDescriptor.getCreationValues(obj, excludeAttrs, db) : "";
        for (int i = 0; i < attrDescriptors.length; i++) {
			if (!contains(excludeAttrs, attrDescriptors[i].getFieldName(), false))
            	values += "," + attrDescriptors[i].getCreationValue(obj, db);
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
        for (int i = 0; i < attrDescriptors.length; i++) {
			if (!contains(excludeAttrs, attrDescriptors[i].getFieldName(), false))
            	attrDescriptors[i].getParameter(obj, pop, table, position++);
		}
        return position;
    }

    /** Returns a comma-seperated list of all attribute names
     * as required for an SQL insert operation
     */
    public String getFieldNames(String[] excludeAttrs) {
        String names = (baseDescriptor != null) ? baseDescriptor.getFieldNames(excludeAttrs) : "";
        for (int i = 0; i < attrDescriptors.length; i++) {
        	if (!contains(excludeAttrs, attrDescriptors[i].getFieldName(), false))
            	names += "," + attrDescriptors[i].getFieldName();
		}
        return trim(names);
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
